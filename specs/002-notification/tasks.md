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

**Goal**: Triển khai Factory Pattern với 3 stub sender, NotificationService điều phối vòng đời PENDING→SENT/FAILED, endpoint POST /api/v1/notifications/send

**Independent Test**: Gọi POST /api/v1/notifications/send với channel = EMAIL, SMS, ZALO; xác nhận đúng sender ghi log và thông báo được lưu với trạng thái SENT trong đúng file JSON theo kênh

### Implementation for User Story 1

- [ ] T009 [P] [US1] Tạo interface NotificationSender với phương thức send(Notification) trong backend/src/main/java/com/ecom/application/notification/NotificationSender.java
- [ ] T010 [P] [US1] Tạo EmailSender stub ghi log đánh dấu gửi qua kênh Email trong backend/src/main/java/com/ecom/application/notification/EmailSender.java
- [ ] T011 [P] [US1] Tạo SmsSender stub ghi log đánh dấu gửi qua kênh SMS trong backend/src/main/java/com/ecom/application/notification/SmsSender.java
- [ ] T012 [P] [US1] Tạo ZaloSender stub ghi log đánh dấu gửi qua kênh Zalo trong backend/src/main/java/com/ecom/application/notification/ZaloSender.java
- [ ] T013 [US1] Tạo NotificationFactory phân giải đúng sender theo ChannelType trong backend/src/main/java/com/ecom/application/notification/NotificationFactory.java
- [ ] T014 [P] [US1] Tạo NotificationDtos (SendNotificationRequest, NotificationResponse) trong backend/src/main/java/com/ecom/api/notification/dto/NotificationDtos.java
- [ ] T015 [US1] Tạo NotificationService xử lý luồng gửi PENDING → SENT/FAILED và lưu bản ghi trong backend/src/main/java/com/ecom/application/notification/NotificationService.java
- [ ] T016 [US1] Triển khai NotificationController POST /api/v1/notifications/send trong backend/src/main/java/com/ecom/api/notification/NotificationController.java
- [ ] T017 [US1] Đồng bộ schema endpoint POST /send vào specs/002-notification/contracts/openapi.yaml
- [ ] T018 [US1] Bổ sung hướng dẫn kiểm thử gửi thông báo qua 3 kênh trong specs/002-notification/quickstart.md

**Checkpoint**: User Story 1 hoạt động độc lập — POST /send với 3 kênh, có thể demo MVP

---

## Phase 4: User Story 2 - Truy vấn trạng thái và chi tiết thông báo (Priority: P2)

**Goal**: Bổ sung GET /api/v1/notifications/{id} tìm kiếm tuần tự qua các file kênh, trả 404 khi không tìm thấy

**Independent Test**: Tạo thông báo qua POST /send, sau đó gọi GET /api/v1/notifications/{id}; xác nhận đầy đủ các trường trả về khớp dữ liệu đã tạo; kiểm tra 404 với id không tồn tại

### Implementation for User Story 2

- [ ] T019 [US2] Mở rộng NotificationService với phương thức findById và ném NotFoundException khi không tìm thấy trong backend/src/main/java/com/ecom/application/notification/NotificationService.java
- [ ] T020 [US2] Bổ sung endpoint GET /api/v1/notifications/{id} vào NotificationController trong backend/src/main/java/com/ecom/api/notification/NotificationController.java
- [ ] T021 [US2] Đồng bộ schema endpoint GET /{id} và response 404 vào specs/002-notification/contracts/openapi.yaml

**Checkpoint**: User Story 2 có thể kiểm thử độc lập trên nền US1

---

## Phase 5: User Story 3 - Xử lý kênh không hợp lệ và lỗi đầu vào (Priority: P3)

**Goal**: Ràng buộc validation SMS body ≤ 160 ký tự, trả 400 rõ ràng cho channel không hợp lệ và thiếu trường bắt buộc; lưu trạng thái FAILED khi sender thất bại

**Independent Test**: Gửi yêu cầu với channel = UNKNOWN, thiếu recipientId, SMS body > 160 ký tự; xác nhận mã lỗi 400 với thông điệp mô tả rõ ràng; kiểm tra sender lỗi → thông báo lưu trạng thái FAILED

### Implementation for User Story 3

- [ ] T022 [P] [US3] Bổ sung ràng buộc @NotBlank recipientId, @NotBlank body và @Size(max=160) khi channel=SMS vào SendNotificationRequest trong backend/src/main/java/com/ecom/api/notification/dto/NotificationDtos.java
- [ ] T023 [P] [US3] Tạo UnsupportedChannelException và cập nhật NotificationFactory ném exception khi channel không hợp lệ trong backend/src/main/java/com/ecom/application/notification/NotificationFactory.java
- [ ] T024 [US3] Xử lý sender ném exception → cập nhật trạng thái FAILED và lưu bản ghi trong NotificationService trong backend/src/main/java/com/ecom/application/notification/NotificationService.java
- [ ] T025 [US3] Đăng ký handler UnsupportedChannelException trả 400 Bad Request rõ ràng trong backend/src/main/java/com/ecom/api/common/GlobalExceptionHandler.java
- [ ] T026 [US3] Đồng bộ error response contract (400 validation, 400 channel không hợp lệ) vào specs/002-notification/contracts/openapi.yaml

**Checkpoint**: User Story 3 có thể kiểm thử độc lập

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Hoàn thiện tài liệu, chuẩn hóa API doc và chuẩn bị bàn giao

- [ ] T027 [P] Chuẩn hóa OpenAPI tag Notifications và mô tả đầy đủ cho controller trong backend/src/main/java/com/ecom/api/notification/NotificationController.java
- [ ] T028 Rà soát và bổ sung migration note JSON → JPA/PostgreSQL trong specs/002-notification/research.md
- [ ] T029 [P] Bổ sung hướng dẫn kiểm thử error path và edge case vào specs/002-notification/quickstart.md

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
- Interface NotificationSender trước từng sender implementation.
- Senders trước NotificationFactory.
- Factory trước NotificationService.
- Service trước controller.
- Controller trước cập nhật contract & quickstart.

### Parallel Opportunities

- Setup: T001, T002, T003 song song.
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
