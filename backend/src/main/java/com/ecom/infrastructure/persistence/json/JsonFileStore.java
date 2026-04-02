package com.ecom.infrastructure.persistence.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * T012 – Thread-safe, atomic JSON file storage utility.
 *
 * <p>Each logical collection is stored as a JSON array in
 * {@code <data-dir>/<collection>.json}. Reads use a shared lock; writes are
 * atomic: data is written to a {@code .tmp} file, then renamed over the target
 * (POSIX atomic replace), so a crash mid-write never leaves a corrupted file.
 *
 * <p>All repository adapters ({@code JsonUserRepository}, etc.) delegate to
 * this class and never touch the filesystem directly.
 */
@Slf4j
@Component
public class JsonFileStore {

    private final ObjectMapper objectMapper;
    private final Path dataDir;
    private final ConcurrentHashMap<String, ReadWriteLock> locks = new ConcurrentHashMap<>();

    public JsonFileStore(ObjectMapper objectMapper,
                         @Value("${storage.json.data-dir}") String dataDirPath) {
        this.objectMapper = objectMapper;
        this.dataDir = Paths.get(dataDirPath);
        try {
            Files.createDirectories(this.dataDir);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create JSON data directory: " + dataDirPath, e);
        }
    }

    /**
     * Read all records from a collection.
     *
     * @param collection name of the collection (becomes the filename)
     * @param typeRef     Jackson type reference for deserialization
     * @param <T>         element type
     * @return mutable list of entities; empty list if the file does not exist yet
     */
    public <T> List<T> readAll(String collection, TypeReference<List<T>> typeRef) {
        ReadWriteLock lock = getLock(collection);
        lock.readLock().lock();
        try {
            Path file = resolve(collection);
            if (!Files.exists(file)) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file.toFile(), typeRef);
        } catch (IOException e) {
            log.error("Failed to read collection '{}': {}", collection, e.getMessage(), e);
            return new ArrayList<>();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Atomically overwrite a collection with the provided data.
     *
     * @param collection name of the collection
     * @param data        full list of entities to persist
     * @param <T>         element type
     */
    public <T> void writeAll(String collection, List<T> data) {
        ReadWriteLock lock = getLock(collection);
        lock.writeLock().lock();
        try {
            Path file = resolve(collection);
            Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(tmp.toFile(), data);
            Files.move(tmp, file,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            log.error("Failed to write collection '{}': {}", collection, e.getMessage(), e);
            throw new RuntimeException("Persistence failure for collection: " + collection, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private Path resolve(String collection) {
        return dataDir.resolve(collection + ".json");
    }

    private ReadWriteLock getLock(String collection) {
        return locks.computeIfAbsent(collection, k -> new ReentrantReadWriteLock());
    }
}
