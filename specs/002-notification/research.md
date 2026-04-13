# Phase 0 Research - Notification System

## Decision 1: Factory Pattern cho phân giải NotificationSender
- **Decision**: Dùng `NotificationFactory` (Spring `@Component`) nhận `List<NotificationSender>` inject
  và build `Map<ChannelType, NotificationSender>` trong `@PostConstruct`.
- **Rationale**: Factory Pattern phù hợp yêu cầu người dùng; Spring DI injection tự động danh sách
  sender mà không cần registry thủ công; thêm kênh mới chỉ cần tạo bean mới.
- **Alternatives considered**:
  - `switch` tĩnh trong service: đơn giản nhưng vi phạm OCP và khó test stub.
  - Abstract Factory: quá phức tạp cho 3 kênh cố định ở scope hiện tại.

## Decision 2: Stub implementation ghi log thay vì kết nối provider thật
- **Decision**: `EmailSender`, `SmsSender`, `ZaloSender` là stub — chỉ ghi log `log.info("Sending [CHANNEL] to ...") `.
- **Rationale**: Phạm vi feature là hạ tầng định tuyến và lưu trạng thái, không phải tích hợp
  provider. Tránh phụ thuộc ngoài không cần thiết ở phase này.
- **Alternatives considered**:
  - JavaMailSender cho Email: cần SMTP config; ngoài scope stub.
  - Twilio SDK cho SMS: thêm dependency không cần thiết.

## Decision 3: Lưu trạng thái Notification — PENDING trước gửi, SENT/FAILED sau
- **Decision**: `NotificationService` lưu entity với trạng thái `PENDING` trước khi gọi sender;
  sau khi sender trả về (hoặc ném exception) cập nhật sang `SENT` hoặc `FAILED`.
- **Rationale**: Đảm bảo audit trail đầy đủ; nếu service crash sau khi lưu nhưng trước khi gửi,
  bản ghi tồn tại với `PENDING` để retry sau.
- **Alternatives considered**:
  - Chỉ lưu sau khi gửi thành công: mất audit trail cho FAILED case.
  - Lưu một lần sau khi gửi: không phân biệt được crash vs failure.

## Decision 4: JSON per-channel — ba file riêng biệt
- **Decision**: Mỗi kênh lưu vào file JSON riêng:
  `notifications-email.json`, `notifications-sms.json`, `notifications-zalo.json`.
- **Rationale**: Yêu cầu người dùng chỉ rõ. Giúp dễ inspect từng kênh riêng; tránh file JSON lớn
  khi nhiều kênh. `JsonNotificationRepository` routing sang đúng file theo `ChannelType`.
- **Alternatives considered**:
  - Một file `notifications.json` duy nhất: đơn giản hơn nhưng không đáp ứng yêu cầu.
  - Thư mục con theo kênh: cần thêm logic tạo thư mục động.

## Decision 5: Validation SMS body ≤ 160 ký tự; subject bỏ qua SMS/ZALO
- **Decision**: `@Size(max = 160)` áp dụng riêng cho SMS qua custom validator hoặc validation nhóm;
  field `subject` optional trong request, service tự bỏ qua khi channel là SMS/ZALO.
- **Rationale**: Ràng buộc nghiệp vụ từ yêu cầu thiết kế; giữ API request đồng nhất (một schema)
  nhưng business rule phân biệt theo channel.
- **Alternatives considered**:
  - Schema riêng cho từng channel: tăng độ phức tạp OpenAPI đáng kể, không cần thiết cho stub.

## Decision 6: ID là UUID, tra cứu cross-channel
- **Decision**: `GET /api/v1/notifications/{id}` tìm kiếm tuần tự qua ba repository (email → sms → zalo)
  cho đến khi tìm thấy; trả 404 nếu không tìm thấy trong tất cả.
- **Rationale**: Phù hợp với storage per-channel; không cần index tổng hợp ở phase JSON.
- **Alternatives considered**:
  - Lưu extra index `id → channel` trong file thứ tư: thêm complexity không cần thiết.
  - Embed channel vào ID prefix: làm ID không trong suốt với client.

## Resolved Clarifications

Không còn mục NEEDS CLARIFICATION trong technical context của plan.

---

## Decision 7: Migration path JSON → JPA/PostgreSQL (T028)

- **Decision**: Thay `JsonNotificationRepository` bằng JPA `@Repository` implement `NotificationRepository` là đủ để migrate kho lưu trữ mà không sửa bất kỳ tầng nào khác.
- **Migration checklist**:
  1. Thêm dependency `spring-boot-starter-data-jpa` + JDBC driver vào `pom.xml`.
  2. Tạo `JpaNotificationRepository extends JpaRepository<Notification, String>`.
  3. Tạo `JpaNotificationRepositoryAdapter implements NotificationRepository` — uỷ quyền sang Spring Data repo.
  4. Xoá hoặc disable `JsonNotificationRepository` bean (`@ConditionalOnProperty` hoặc Spring profile).
  5. Thêm `@Entity`, `@Table`, `@Column` vào `Notification.java` (hiện dùng Lombok `@Data`).
  6. Xoá ba file `notifications-*.json`, xoá seed data.
- **Zero-change boundary**: `NotificationService`, `NotificationFactory`, tất cả concrete strategy, `NotificationController`, DTOs — **không cần sửa đổi**.
- **Rationale**: Repository pattern tách biệt hoàn toàn nghiệp vụ khỏi cơ chế lưu trữ (FR-012); toàn bộ tầng application thao tác qua `NotificationRepository` interface.

---

## Decision 8: Cross-dependency verification — Concrete Strategy isolation (T030 / SC-006)

- **Verification date**: 2026-04-13
- **Result**: PASS ✅ — không có cross-import giữa các concrete strategy.

| Class | Imports concrete strategy | Result |
|---|---|---|
| `EmailSender.java` | `SmsSender`, `ZaloSender`: **không** | ✅ |
| `SmsSender.java` | `EmailSender`, `ZaloSender`: **không** | ✅ |
| `ZaloSender.java` | `EmailSender`, `SmsSender`: **không** | ✅ |

- **Mỗi class chỉ phụ thuộc**: `NotificationSender` (interface), `Notification` (domain entity), `@Slf4j`, `@Component`.
- **Rationale**: Tuân thủ FR-003 (Independence) và SC-006; thêm kênh mới không ảnh hưởng runtime bất kỳ concrete strategy hiện có (FR-013 — strategy isolation).
