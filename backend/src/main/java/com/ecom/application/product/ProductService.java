package com.ecom.application.product;

import com.ecom.api.common.ResourceNotFoundException;
import com.ecom.api.product.dto.ProductDtos.ProductCreateRequest;
import com.ecom.api.product.dto.ProductDtos.ProductResponse;
import com.ecom.api.product.dto.ProductDtos.ProductUpdateRequest;
import com.ecom.domain.model.Product;
import com.ecom.domain.repository.OrderItemRepository;
import com.ecom.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * T033 – Application service for Product CRUD.
 *
 * <p>Business rules:
 * <ul>
 *   <li>A product that is still referenced by any {@code OrderItem} cannot be deleted.</li>
 *   <li>Stock management (decrement / restore) is handled by {@code OrderItemService}.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public ProductResponse create(ProductCreateRequest req) {
        Instant now = Instant.now();
        Product product = Product.builder()
                .id(UUID.randomUUID().toString())
                .name(req.getName())
                .price(req.getPrice())
                .stock(req.getStock())
                .createdAt(now)
                .updatedAt(now)
                .build();

        log.info("Creating product name='{}'", req.getName());
        return ProductResponse.from(productRepository.save(product));
    }

    public ProductResponse findById(String id) {
        return ProductResponse.from(
                productRepository.findById(id)
                        .orElseThrow(() -> ResourceNotFoundException.of("Product", id))
        );
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    public ProductResponse update(String id, ProductUpdateRequest req) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", id));

        existing.setName(req.getName());
        existing.setPrice(req.getPrice());
        existing.setStock(req.getStock());
        existing.setUpdatedAt(Instant.now());

        log.info("Updating product id={}", id);
        return ProductResponse.from(productRepository.save(existing));
    }

    public void deleteById(String id) {
        if (!productRepository.existsById(id)) {
            throw ResourceNotFoundException.of("Product", id);
        }
        if (!orderItemRepository.findByProductId(id).isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot delete product id='" + id + "': still referenced by order items");
        }
        log.info("Deleting product id={}", id);
        productRepository.deleteById(id);
    }
}
