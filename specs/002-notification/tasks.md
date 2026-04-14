# Tasks: Notification System

**Input**: Design documents from `/specs/002-notification/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/openapi.yaml, quickstart.md

**Tests**: Không tạo task kiểm thử riêng trong danh sách này vì đặc tả chưa yêu cầu TDD bắt buộc. Kiểm thử sẽ được gắn trong task hoàn thiện từng luồng.

**Organization**: Tasks được nhóm theo user story để mỗi story có thể triển khai và kiểm tra độc lập.

## Phase 1: Setup (Seed JSON Files)

**Purpose**: Khởi tạo file dữ liệu JSON trống cho từng kênh thông báo

- [x] T001 Tạo file seed JSON rỗng cho kênh Email trong backend/src/main/resources/data/notifications-email.json
- [x] T002 [P] Tạo file seed JSON rỗng cho kênh SMS trong backend/src/main/resources/data/notifications-sms.json
- [x] T003 [P] Tạo file seed JSON rỗng cho kênh Zalo trong backend/src/main/resources/data/notifications-zalo.json

---

## Phase 2: Foundational (Domain Models & Repository)

**Purpose**: Nền tảng domain và persistence bắt buộc trước khi triển khai user story

**⚠️ CRITICAL**: Hoàn thành phase này trước mọi user story

- [x] T004 [P] Tạo enum ChannelType (EMAIL, SMS, ZALO) trong backend/src/main/java/com/ecom/domain/model/ChannelType.java
- [x] T005 [P] Tạo enum NotificationStatus (PENDING, SENT, FAILED) trong backend/src/main/java/com/ecom/domain/model/NotificationStatus.java
- [x] T006 Tạo entity Notification với @Data @Builder và đầy đủ fields (id, recipientId, channel, subject, body, status, createdAt, sentAt) trong backend/src/main/java/com/ecom/domain/model/Notification.java
- [x] T007 Tạo NotificationRepository interface với phương thức save() và findById() trong backend/src/main/java/com/ecom/domain/repository/NotificationRepository.java
- [x] T008 Cài đặt JsonNotificationRepository routing ghi theo channel và tìm kiếm tuần tự email→sms→zalo trong backend/src/main/java/com/ecom/infrastructure/persistence/json/JsonNotificationRepository.java

**Checkpoint**: Domain layer và persistence sẵn sàng cho triển khai user story

---

## Phase 3: User Story 1 - Gửi thông báo qua kênh được chọn (Priority: P1) 🎯 MVP

**Goal**: Triển khai **Strategy Pattern + Factory Pattern**:
- `NotificationSender` là **Strategy interface** (hợp đồng duy nhất)
- `EmailSender`, `SmsSender`, `ZaloSender` là **Concrete Strategy** độc lập, không import nhau
- `NotificationFactory` là **Factory** chọn đúng strategy theo `ChannelType` tại runtime
- `NotificationService` điều phối vòng đời PENDING→SENT/FAILED, endpoint `POST /api/v1/notifications/send`

**Independent Test**: Gọi `POST /api/v1/notifications/send` với `channel` = EMAIL, SMS, ZALO;
xác nhận đúng concrete strategy được gọi (qua log) và thông báo lưu đúng file JSON kênh với trạng thái SENT

### Implementation for User Story 1

- [x] T009 [P] [US1] Tạo **Strategy interface** `NotificationSender` với phương thức `send(Notification)` trong backend/src/main/java/com/ecom/application/notification/NotificationSender.java
- [x] T010 [P] [US1] Tạo **Concrete Strategy** `EmailSender` (stub ghi log) — chỉ implement `NotificationSender`, không import sender khác trong backend/src/main/java/com/ecom/application/notification/EmailSender.java
- [x] T011 [P] [US1] Tạo **Concrete Strategy** `SmsSender` (stub ghi log) — chỉ implement `NotificationSender`, không import sender khác trong backend/src/main/java/com/ecom/application/notification/SmsSender.java
- [x] T012 [P] [US1] Tạo **Concrete Strategy** `ZaloSender` (stub ghi log) — chỉ implement `NotificationSender`, không import sender khác trong backend/src/main/java/com/ecom/application/notification/ZaloSender.java
- [x] T013 [US1] Tạo **Factory** `NotificationFactory` — nhận `ChannelType`, trả đúng concrete strategy; ném exception rõ ràng cho kênh không hỗ trợ trong backend/src/main/java/com/ecom/application/notification/NotificationFactory.java
- [x] T014 [P] [US1] Tạo NotificationDtos (SendNotificationRequest, NotificationResponse) trong backend/src/main/java/com/ecom/api/notification/dto/NotificationDtos.java
- [x] T015 [US1] Tạo NotificationService xử lý luồng gửi PENDING → SENT/FAILED và lưu bản ghi trong backend/src/main/java/com/ecom/application/notification/NotificationService.java
- [x] T016 [US1] Triển khai NotificationController POST /api/v1/notifications/send trong backend/src/main/java/com/ecom/api/notification/NotificationController.java
- [x] T017 [US1] Đồng bộ schema endpoint POST /send vào specs/002-notification/contracts/openapi.yaml
- [x] T018 [US1] Bổ sung hướng dẫn kiểm thử gửi thông báo qua 3 kênh trong specs/002-notification/quickstart.md

**Checkpoint**: User Story 1 hoạt động độc lập — POST /send với 3 kênh, có thể demo MVP

---

## Phase 4: User Story 2 - Truy vấn trạng thái và chi tiết thông báo (Priority: P2)

**Goal**: `GET /api/v1/notifications/{id}` tìm kiếm tuần tự qua các file kênh, trả 404 khi không tìm thấy.
**Note**: `findById()` và `GET /{id}` đã được triển khai trong T015/T016 như một phần của US1 MVP.

**Independent Test**: Tạo thông báo qua POST /send, sau đó gọi GET /api/v1/notifications/{id}; xác nhận đầy đủ các trường trả về khớp dữ liệu đã tạo; kiểm tra 404 với id không tồn tại

### Implementation for User Story 2

- [x] T019 [US2] `findById` trong `NotificationService` đã có từ T015 — ném `ResourceNotFoundException.of("Notification", id)` khi không tìm thấy trong backend/src/main/java/com/ecom/application/notification/NotificationService.java
- [x] T020 [US2] `GET /api/v1/notifications/{id}` đã có từ T016 — điều hướng tớisérvice.findById(), trả 404 tự động qua `GlobalExceptionHandler` trong backend/src/main/java/com/ecom/api/notification/NotificationController.java
- [x] T021 [US2] Schema `GET /{id}` và response 404 đã có từ speckit.plan trong specs/002-notification/contracts/openapi.yaml

**Checkpoint**: User Story 2 đã được triển khai hoàn chỉnh cùng US1 — có thể kiểm thử ngay

---

## Phase 5: User Story 3 - Xử lý kênh không hợp lệ và lỗi đầu vào (Priority: P3)

**Goal**: Ràng buộc validation SMS body ≤ 160 ký tự, trả 400 rõ ràng cho channel không hợp lệ và thiếu trường bắt buộc; lưu trạng thái FAILED khi sender thất bại

**Independent Test**: Gửi yêu cầu với channel = UNKNOWN, thiếu recipientId, SMS body > 160 ký tự; xác nhận mã lỗi 400 với thông điệp mô tả rõ ràng; kiểm tra sender lỗi → thông báo lưu trạng thái FAILED

### Implementation for User Story 3

- [x] T022 [P] [US3] Validation đầu vào đã có: `@NotBlank recipientId`, `@NotBlank body`, `@NotNull channel` trong DTO; SMS 160-char guard trong `NotificationService.validateRequest()` bằng `IllegalArgumentException` (cross-field validation không thể dùng `@Size` tiêu chuẩn — service-layer là approach đúng) trong backend/src/main/java/com/ecom/api/notification/dto/NotificationDtos.java và backend/src/main/java/com/ecom/application/notification/NotificationService.java
- [x] T023 [P] [US3] Tạo `UnsupportedChannelException` và cập nhật `NotificationFactory` ném exception khi `ChannelType` không hỗ trợ thay vì siết logic sang concrete strategy trong backend/src/main/java/com/ecom/application/notification/NotificationFactory.java
- [x] T024 [US3] `NotificationService` đã xử lý: `catch(Exception ex)` → `status=FAILED` → `save()` (FR-013 — lỗi một strategy không lan sang strategy khác) trong backend/src/main/java/com/ecom/application/notification/NotificationService.java
- [x] T025 [US3] Đăng ký handler `UnsupportedChannelException` trả 400 Bad Request rõ ràng (phụ thuộc T023) trong backend/src/main/java/com/ecom/api/common/GlobalExceptionHandler.java
- [x] T026 [US3] Đồng bộ error response contract (400 validation, 400 channel không hợp lệ) vào specs/002-notification/contracts/openapi.yaml
- [x] T031 [US3] 🚫 **[GAP — SC-003]** Thêm handler `HttpMessageNotReadableException` → 400 trong `GlobalExceptionHandler`: khi `channel="UNKNOWN"` Jackson ném `InvalidFormatException`, hiện tại rơi xuống handler 500 chung; cần trả 400 kèm thông điệp rõ ràng (FR-010, FR-011, US3 scenario 1) trong backend/src/main/java/com/ecom/api/common/GlobalExceptionHandler.java

**Checkpoint**: User Story 3 có thể kiểm thử độc lập

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Hoàn thiện tài liệu, chuẩn hóa API doc và chuẩn bị bàn giao

- [x] T027 [P] Chuẩn hóa OpenAPI tag Notifications và mô tả đầy đủ cho controller trong backend/src/main/java/com/ecom/api/notification/NotificationController.java
- [x] T028 Rà soát và bổ sung migration note JSON → JPA/PostgreSQL trong specs/002-notification/research.md
- [x] T029 [P] Bổ sung hướng dẫn kiểm thử error path và edge case vào specs/002-notification/quickstart.md
- [x] T030 [P] Xác minh không có cross-import giữa concrete strategy: kiểm tra `EmailSender`, `SmsSender`, `ZaloSender` không import nhau (SC-006); ghi kết quả vào specs/002-notification/research.md

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: bắt đầu ngay.
- **Phase 2 (Foundational)**: phụ thuộc Phase 1, chặn toàn bộ user story.
- **Phase 3-5 (User Stories)**: phụ thuộc Phase 2.
- **Phase 6 (Polish)**: phụ thuộc hoàn thành các story mục tiêu.

### User Story Dependencies

- **US1 (P1)**: bắt đầu sau Foundational, không phụ thuộc story khác.
- **US2 (P2)**: phụ thuộc US1 để có controller và service nền tảng.
- **US3 (P3)**: phụ thuộc US1 để có luồng gửi và validation cơ bản.

### Within Each User Story

- Enums trước entity.
- Repository interface trước implementation.
- **Strategy interface** `NotificationSender` trước mọi concrete strategy.
- Concrete strategy (`EmailSender`, `SmsSender`, `ZaloSender`) độc lập nhau — có thể viết song song, không có thứ tự phụ thuộc giữa chúng.
- **Factory** `NotificationFactory` sau khi có đủ các concrete strategy.
- `NotificationFactory` trước `NotificationService`.
- Service trước controller.
- Controller trước cập nhật contract & quickstart.

### Parallel Opportunities

- Setup: T001, T002, T003 song song.
- Concrete strategies T010, T011, T012 song song (không phụ thuộc nhau, chỉ cần T009 interface sẵn).
- T019/T020 có thể song song sau khi T015/T016 đã có skeleton.
- Foundational: T004, T005 song song; T006 → T007 → T008 tuần tự.
- US1: T009-T012 song song với nhau; T014 song song với T009-T012; T013 sau T009-T012; T015 sau T013+T014.
- US3: T022, T023 song song.
- Polish: T027, T029 song song.

---

## Parallel Example: User Story 1

- Chạy song song nhóm senders:
  - Task: "Tạo EmailSender stub ghi log đánh dấu gửi qua kênh Email trong backend/src/main/java/com/ecom/application/notification/EmailSender.java"
  - Task: "Tạo SmsSender stub ghi log đánh dấu gửi qua kênh SMS trong backend/src/main/java/com/ecom/application/notification/SmsSender.java"
  - Task: "Tạo ZaloSender stub ghi log đánh dấu gửi qua kênh Zalo trong backend/src/main/java/com/ecom/application/notification/ZaloSender.java"

- Chạy song song DTO với nhóm senders:
  - Task: "Tạo NotificationDtos (SendNotificationRequest, NotificationResponse) trong backend/src/main/java/com/ecom/api/notification/dto/NotificationDtos.java"

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Hoàn thành Phase 1 và Phase 2.
2. Hoàn thành toàn bộ task US1 (T009-T018).
3. Dừng và validate POST /send end-to-end trên cả 3 kênh trước khi mở rộng.

### Incremental Delivery

1. US1 (Factory + Send) → bàn giao MVP.
2. US2 (Query by ID) → kiểm soát vận hành.
3. US3 (Validation + Error Handling) → sẵn sàng tích hợp phía client.

### Parallel Team Strategy

- Team A: domain/repository (Phase 2 — T004-T008).
- Team B: application/notification (Phase 3 — T009-T015).
- Team C: api/notification (Phase 3 — T014, T016; Phase 4 — T020).
- Đồng bộ bằng contract file và quickstart sau mỗi phase.

---

## Notes

- Mọi task đều dùng format checklist chuẩn.
- Mỗi phase kết thúc bằng checkpoint kiểm tra độc lập.
- Persistence JSON thuần túy — không cần DB; migration sang JPA chỉ cần thay `JsonNotificationRepository` bằng `@Entity`.
- Stub sender ghi log là đủ cho MVP; thay thế bằng real integration sau.
- `subject` bị bỏ qua (không lưu, không báo lỗi) khi channel là SMS hoặc ZALO.
