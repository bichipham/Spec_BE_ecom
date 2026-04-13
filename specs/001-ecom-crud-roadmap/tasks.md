# Tasks: E-commerce Backend CRUD & Scale Roadmap

**Input**: Design documents from `/specs/001-ecom-crud-roadmap/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/openapi.yaml, quickstart.md

**Tests**: Không tạo task kiểm thử riêng trong danh sách này vì đặc tả chưa yêu cầu TDD bắt buộc. Kiểm thử sẽ được gắn trong task hoàn thiện từng luồng.

**Organization**: Tasks được nhóm theo user story để mỗi story có thể triển khai và kiểm tra độc lập.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Khởi tạo cấu trúc dự án backend theo plan

- [X] T001 Khởi tạo Maven Spring Boot project trong backend/pom.xml
- [X] T002 Tạo cấu trúc package chính trong backend/src/main/java/com/ecom/Application.java
- [X] T003 [P] Khai báo cấu hình ứng dụng cơ bản trong backend/src/main/resources/application.yml
- [X] T004 [P] Tạo thư mục dữ liệu tạm JSON và file seed rỗng trong backend/src/main/resources/data/.gitkeep
- [X] T005 [P] Tạo tài liệu API entrypoint trong backend/src/main/resources/openapi-info.md

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Hạ tầng bắt buộc trước khi làm user stories

**⚠️ CRITICAL**: Hoàn thành phase này trước mọi user story

- [X] T006 Tạo mô hình response lỗi chuẩn trong backend/src/main/java/com/ecom/api/common/ErrorResponse.java
- [X] T007 [P] Tạo global exception handler trong backend/src/main/java/com/ecom/api/common/GlobalExceptionHandler.java
- [X] T008 [P] Tạo request correlation filter trong backend/src/main/java/com/ecom/infrastructure/observability/CorrelationIdFilter.java
- [X] T009 [P] Tạo health endpoint trong backend/src/main/java/com/ecom/api/health/HealthController.java
- [X] T010 [P] Tạo readiness endpoint trong backend/src/main/java/com/ecom/api/health/ReadinessController.java
- [X] T011 Tạo interface repository nền tảng trong backend/src/main/java/com/ecom/domain/repository/BaseRepository.java
- [X] T012 [P] Tạo utility đọc/ghi JSON atomic trong backend/src/main/java/com/ecom/infrastructure/persistence/json/JsonFileStore.java
- [X] T013 [P] Tạo cấu hình Jackson UTC và định dạng thời gian trong backend/src/main/java/com/ecom/config/JacksonConfig.java
- [X] T014 Tạo OpenAPI config versioned `/api/v1` trong backend/src/main/java/com/ecom/config/OpenApiConfig.java
- [X] T015 Tạo package security skeleton trong backend/src/main/java/com/ecom/config/security/SecurityConfig.java

**Checkpoint**: Foundation sẵn sàng cho triển khai user story

---

## Phase 3: User Story 1 - CRUD lõi cho dữ liệu ecom (Priority: P1) 🎯 MVP

**Goal**: Triển khai CRUD đầy đủ cho User, Product, Order, OrderItem với lưu trữ JSON qua repository abstraction

**Independent Test**: Gọi API CRUD cho 4 thực thể, xác minh quan hệ và tính nhất quán dữ liệu JSON

### Implementation for User Story 1

- [X] T016 [P] [US1] Tạo entity User trong backend/src/main/java/com/ecom/domain/model/User.java
- [X] T017 [P] [US1] Tạo entity Product trong backend/src/main/java/com/ecom/domain/model/Product.java
- [X] T018 [P] [US1] Tạo entity Order trong backend/src/main/java/com/ecom/domain/model/Order.java
- [X] T019 [P] [US1] Tạo entity OrderItem trong backend/src/main/java/com/ecom/domain/model/OrderItem.java
- [X] T020 [P] [US1] Tạo DTO CRUD User trong backend/src/main/java/com/ecom/api/user/dto/UserDtos.java
- [X] T021 [P] [US1] Tạo DTO CRUD Product trong backend/src/main/java/com/ecom/api/product/dto/ProductDtos.java
- [X] T022 [P] [US1] Tạo DTO CRUD Order trong backend/src/main/java/com/ecom/api/order/dto/OrderDtos.java
- [X] T023 [P] [US1] Tạo DTO CRUD OrderItem trong backend/src/main/java/com/ecom/api/orderitem/dto/OrderItemDtos.java
- [X] T024 [P] [US1] Tạo UserRepository interface trong backend/src/main/java/com/ecom/domain/repository/UserRepository.java
- [X] T025 [P] [US1] Tạo ProductRepository interface trong backend/src/main/java/com/ecom/domain/repository/ProductRepository.java
- [X] T026 [P] [US1] Tạo OrderRepository interface trong backend/src/main/java/com/ecom/domain/repository/OrderRepository.java
- [X] T027 [P] [US1] Tạo OrderItemRepository interface trong backend/src/main/java/com/ecom/domain/repository/OrderItemRepository.java
- [X] T028 [P] [US1] Cài đặt JSON UserRepository adapter trong backend/src/main/java/com/ecom/infrastructure/persistence/json/JsonUserRepository.java
- [X] T029 [P] [US1] Cài đặt JSON ProductRepository adapter trong backend/src/main/java/com/ecom/infrastructure/persistence/json/JsonProductRepository.java
- [X] T030 [P] [US1] Cài đặt JSON OrderRepository adapter trong backend/src/main/java/com/ecom/infrastructure/persistence/json/JsonOrderRepository.java
- [X] T031 [P] [US1] Cài đặt JSON OrderItemRepository adapter trong backend/src/main/java/com/ecom/infrastructure/persistence/json/JsonOrderItemRepository.java
- [X] T032 [US1] Tạo UserService CRUD trong backend/src/main/java/com/ecom/application/user/UserService.java
- [X] T033 [US1] Tạo ProductService CRUD trong backend/src/main/java/com/ecom/application/product/ProductService.java
- [X] T034 [US1] Tạo OrderService CRUD + trạng thái trong backend/src/main/java/com/ecom/application/order/OrderService.java
- [X] T035 [US1] Tạo OrderItemService CRUD + tính total_amount + stock check trong backend/src/main/java/com/ecom/application/orderitem/OrderItemService.java
- [X] T036 [US1] Triển khai UserController `/api/v1/users` trong backend/src/main/java/com/ecom/api/user/UserController.java
- [X] T037 [US1] Triển khai ProductController `/api/v1/products` trong backend/src/main/java/com/ecom/api/product/ProductController.java
- [X] T038 [US1] Triển khai OrderController `/api/v1/orders` trong backend/src/main/java/com/ecom/api/order/OrderController.java
- [X] T039 [US1] Triển khai OrderItemController `/api/v1/order-items` trong backend/src/main/java/com/ecom/api/orderitem/OrderItemController.java
- [X] T040 [US1] Đồng bộ schema endpoint CRUD vào specs/001-ecom-crud-roadmap/contracts/openapi.yaml
- [X] T041 [US1] Bổ sung hướng dẫn chạy CRUD nhanh trong specs/001-ecom-crud-roadmap/quickstart.md

**Checkpoint**: User Story 1 hoạt động độc lập và có thể demo MVP

---

## Phase 4: User Story 2 - Xác thực và phân quyền cơ bản (Priority: P2)

**Goal**: Có đăng nhập và kiểm soát quyền theo vai trò

**Independent Test**: Đăng nhập thành công/thất bại, kiểm tra endpoint bị chặn đúng role

### Implementation for User Story 2

- [X] T042 [P] [US2] Tạo AuthSession model trong backend/src/main/java/com/ecom/domain/model/AuthSession.java
- [X] T043 [P] [US2] Tạo JWT utility trong backend/src/main/java/com/ecom/config/security/JwtTokenProvider.java
- [X] T044 [P] [US2] Tạo password encoder config trong backend/src/main/java/com/ecom/config/security/PasswordConfig.java
- [X] T045 [US2] Cài đặt AuthService login/logout trong backend/src/main/java/com/ecom/application/auth/AuthService.java
- [X] T046 [US2] Tạo AuthController `/api/v1/auth/login` trong backend/src/main/java/com/ecom/api/auth/AuthController.java
- [X] T047 [US2] Áp dụng RBAC cho endpoint CRUD trong backend/src/main/java/com/ecom/config/security/SecurityConfig.java
- [X] T048 [US2] Đồng bộ contract auth vào specs/001-ecom-crud-roadmap/contracts/openapi.yaml

**Checkpoint**: User Story 2 chạy độc lập trên nền CRUD

---

## Phase 5: User Story 3 - Tìm kiếm cơ bản (Priority: P3)

**Goal**: Tìm kiếm keyword cơ bản cho Product và Order

**Independent Test**: Gọi endpoint search với keyword hợp lệ, trả danh sách đúng

### Implementation for User Story 3

- [ ] T049 [P] [US3] Tạo SearchQuery DTO cơ bản trong backend/src/main/java/com/ecom/api/search/dto/SearchQuery.java
- [ ] T050 [P] [US3] Tạo SearchResult DTO cơ bản trong backend/src/main/java/com/ecom/api/search/dto/SearchResult.java
- [ ] T051 [US3] Cài đặt BasicSearchService cho Product/Order trong backend/src/main/java/com/ecom/application/search/BasicSearchService.java
- [ ] T052 [US3] Tạo SearchController `/api/v1/search` trong backend/src/main/java/com/ecom/api/search/SearchController.java
- [ ] T053 [US3] Đồng bộ contract basic search vào specs/001-ecom-crud-roadmap/contracts/openapi.yaml

**Checkpoint**: User Story 3 có thể kiểm thử độc lập

---

## Phase 6: User Story 4 - Tìm kiếm nâng cao cho dữ liệu lớn (Priority: P4)

**Goal**: Bổ sung filter/sort/pagination/multi-condition query

**Independent Test**: Truy vấn kết hợp nhiều điều kiện và kiểm tra phân trang ổn định

### Implementation for User Story 4

- [ ] T054 [P] [US4] Tạo AdvancedSearchQuery DTO trong backend/src/main/java/com/ecom/api/search/dto/AdvancedSearchQuery.java
- [ ] T055 [P] [US4] Tạo PageResponse DTO trong backend/src/main/java/com/ecom/api/common/PageResponse.java
- [ ] T056 [US4] Cài đặt AdvancedSearchService trong backend/src/main/java/com/ecom/application/search/AdvancedSearchService.java
- [ ] T057 [US4] Mở rộng SearchController `/api/v1/search/advanced` trong backend/src/main/java/com/ecom/api/search/SearchController.java
- [ ] T058 [US4] Đồng bộ contract advanced search vào specs/001-ecom-crud-roadmap/contracts/openapi.yaml

**Checkpoint**: User Story 4 hoàn chỉnh và có thể đo hiệu năng độc lập

---

## Phase 7: User Story 5 - Tìm kiếm chuyên dụng quy mô doanh nghiệp (Priority: P5)

**Goal**: Tích hợp Elasticsearch và đồng bộ chỉ mục

**Independent Test**: Index dữ liệu Product/Order và truy vấn qua backend trả đúng, nhanh

### Implementation for User Story 5

- [ ] T059 [P] [US5] Tạo Elasticsearch config trong backend/src/main/java/com/ecom/config/search/ElasticsearchConfig.java
- [ ] T060 [P] [US5] Tạo SearchIndexRecord model trong backend/src/main/java/com/ecom/domain/model/SearchIndexRecord.java
- [ ] T061 [US5] Cài đặt IndexSyncService đồng bộ dữ liệu trong backend/src/main/java/com/ecom/application/search/IndexSyncService.java
- [ ] T062 [US5] Cài đặt EnterpriseSearchService trong backend/src/main/java/com/ecom/application/search/EnterpriseSearchService.java
- [ ] T063 [US5] Mở rộng SearchController chọn backend search strategy trong backend/src/main/java/com/ecom/api/search/SearchController.java
- [ ] T064 [US5] Thêm cấu hình kết nối search engine trong backend/src/main/resources/application.yml

**Checkpoint**: User Story 5 chạy độc lập với dữ liệu index đồng bộ

---

## Phase 8: User Story 6 - Tăng cường bảo mật toàn hệ thống (Priority: P6)

**Goal**: Hardening bảo mật mức hệ thống

**Independent Test**: Mô phỏng truy cập sai/abuse và xác nhận hệ thống chặn + ghi audit

### Implementation for User Story 6

- [ ] T065 [P] [US6] Tạo rate limiting filter trong backend/src/main/java/com/ecom/config/security/RateLimitFilter.java
- [ ] T066 [P] [US6] Tạo secure headers filter trong backend/src/main/java/com/ecom/config/security/SecureHeadersFilter.java
- [ ] T067 [P] [US6] Tạo audit event model trong backend/src/main/java/com/ecom/domain/model/SecurityAuditEvent.java
- [ ] T068 [US6] Cài đặt AuditLogService trong backend/src/main/java/com/ecom/application/security/AuditLogService.java
- [ ] T069 [US6] Bật hardening chain trong backend/src/main/java/com/ecom/config/security/SecurityConfig.java
- [ ] T070 [US6] Bổ sung mô tả controls bảo mật trong specs/001-ecom-crud-roadmap/quickstart.md

**Checkpoint**: User Story 6 hoàn tất hardening mức nền tảng

---

## Phase 9: Polish & Cross-Cutting Concerns

**Purpose**: Hoàn thiện tài liệu, tối ưu và chuẩn bị bàn giao

- [ ] T071 [P] Chuẩn hóa mapping lỗi và mã lỗi tài liệu trong specs/001-ecom-crud-roadmap/contracts/error-codes.md
- [ ] T072 Rà soát migration notes JSON -> PostgreSQL trong specs/001-ecom-crud-roadmap/research.md
- [ ] T073 [P] Bổ sung hướng dẫn vận hành cho từng phase trong specs/001-ecom-crud-roadmap/quickstart.md
- [ ] T074 Tối ưu logging cấu trúc theo môi trường trong backend/src/main/resources/application.yml
- [ ] T075 Chốt release notes triển khai theo phase trong specs/001-ecom-crud-roadmap/release-notes.md

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: bắt đầu ngay.
- **Phase 2 (Foundational)**: phụ thuộc Phase 1, chặn toàn bộ user story.
- **Phase 3-8 (User Stories)**: phụ thuộc Phase 2.
- **Phase 9 (Polish)**: phụ thuộc hoàn thành các story mục tiêu.

### User Story Dependencies

- **US1 (P1)**: bắt đầu sau Foundational, không phụ thuộc story khác.
- **US2 (P2)**: phụ thuộc US1 để bảo vệ CRUD.
- **US3 (P3)**: phụ thuộc US1 và tận dụng quyền từ US2.
- **US4 (P4)**: mở rộng từ US3.
- **US5 (P5)**: phụ thuộc US4 để tái sử dụng query model.
- **US6 (P6)**: hardening sau khi auth và search luồng chính ổn định.

### Within Each User Story

- Models/DTOs trước.
- Repository/infra trước service.
- Service trước controller.
- Controller trước cập nhật contract & quickstart.

### Parallel Opportunities

- Setup: T003, T004, T005 song song.
- Foundational: T007, T008, T009, T010, T012, T013 song song.
- US1: T016-T031 có nhiều task song song theo từng thực thể.
- US2: T042-T044 song song.
- US3: T049-T050 song song.
- US4: T054-T055 song song.
- US5: T059-T060 song song.
- US6: T065-T067 song song.
- Polish: T071, T073 song song.

---

## Parallel Example: User Story 1

- Chạy song song nhóm model:
  - Task: "Tạo entity User trong backend/src/main/java/com/ecom/domain/model/User.java"
  - Task: "Tạo entity Product trong backend/src/main/java/com/ecom/domain/model/Product.java"
  - Task: "Tạo entity Order trong backend/src/main/java/com/ecom/domain/model/Order.java"
  - Task: "Tạo entity OrderItem trong backend/src/main/java/com/ecom/domain/model/OrderItem.java"

- Chạy song song nhóm JSON adapters:
  - Task: "Cài đặt JSON UserRepository adapter trong backend/src/main/java/com/ecom/infrastructure/persistence/json/JsonUserRepository.java"
  - Task: "Cài đặt JSON ProductRepository adapter trong backend/src/main/java/com/ecom/infrastructure/persistence/json/JsonProductRepository.java"
  - Task: "Cài đặt JSON OrderRepository adapter trong backend/src/main/java/com/ecom/infrastructure/persistence/json/JsonOrderRepository.java"
  - Task: "Cài đặt JSON OrderItemRepository adapter trong backend/src/main/java/com/ecom/infrastructure/persistence/json/JsonOrderItemRepository.java"

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Hoàn thành Phase 1 và Phase 2.
2. Hoàn thành toàn bộ task US1 (T016-T041).
3. Dừng và validate CRUD end-to-end trước khi mở rộng.

### Incremental Delivery

1. US1 (CRUD) -> bàn giao MVP.
2. US2 (Auth) -> bảo vệ dữ liệu.
3. US3 + US4 (Search) -> tăng hiệu quả vận hành.
4. US5 (Enterprise Search) -> scale lớn.
5. US6 (Security Hardening) -> sẵn sàng production.

### Parallel Team Strategy

- Team A: domain/repository.
- Team B: service/controller.
- Team C: security/search/ops docs.
- Đồng bộ bằng contract file và quickstart sau mỗi phase.

---

## Notes

- Mọi task đều dùng format checklist chuẩn.
- Task user story đều có nhãn `[USx]`.
- Task có `[P]` là song song an toàn theo file khác nhau.
- Đảm bảo không truy cập JSON trực tiếp từ controller.
