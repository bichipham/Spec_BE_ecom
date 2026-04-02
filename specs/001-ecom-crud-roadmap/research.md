# Phase 0 Research - E-commerce Backend CRUD & Scale Roadmap

## Decision 1: Java + Spring Boot 3.3 làm nền tảng backend
- **Decision**: Chọn Java 21 với Spring Boot 3.3.x cho toàn bộ backend.
- **Rationale**: Phù hợp ngữ cảnh repo `BEJava`, hệ sinh thái mạnh cho REST API, validation, security,
  dễ mở rộng theo kiến trúc module và phù hợp scale dài hạn.
- **Alternatives considered**:
  - Node.js/NestJS: nhanh để prototyping nhưng lệch stack hiện tại.
  - Go/Fiber: hiệu năng tốt nhưng tăng chi phí đồng bộ năng lực team.

## Decision 2: Thiết kế data model trước, triển khai persistence tạm bằng JSON
- **Decision**: Chốt schema logic cho `users`, `products`, `orders`, `order_items` trước; lưu tạm JSON.
- **Rationale**: Đáp ứng yêu cầu người dùng và cho phép khởi động nhanh phase 1 CRUD.
  Đồng thời giữ khả năng chuyển đổi DB thật ở phase sau.
- **Alternatives considered**:
  - Dùng PostgreSQL ngay: chuẩn production hơn nhưng tăng effort ban đầu.
  - Lưu in-memory: đơn giản nhưng không bền và khó kiểm thử tích hợp.

## Decision 3: Áp dụng Repository Abstraction bắt buộc
- **Decision**: Tất cả truy cập dữ liệu đi qua interface repository; JSON chỉ là adapter.
- **Rationale**: Tuân thủ constitution, bảo đảm thay storage không ảnh hưởng business logic.
- **Alternatives considered**:
  - Controller đọc/ghi JSON trực tiếp: nhanh nhưng vi phạm layering và khó scale.

## Decision 4: API Contract-first với OpenAPI và versioning `/api/v1`
- **Decision**: Định nghĩa hợp đồng API trước triển khai bằng OpenAPI.
- **Rationale**: Giảm sai lệch giữa client/server, rõ schema, thuận lợi cho contract test.
- **Alternatives considered**:
  - Code-first không hợp đồng: tốc độ đầu nhanh nhưng khó kiểm soát breaking changes.

## Decision 5: Chiến lược kiểm thử theo phase
- **Decision**: Mỗi phase có test độc lập: unit + integration + contract (ưu tiên cho endpoints).
- **Rationale**: Đảm bảo incremental delivery, giảm regression khi mở rộng từ CRUD đến security.
- **Alternatives considered**:
  - Chỉ integration test: thiếu độ chi tiết khi debug logic.
  - Chỉ unit test: không đảm bảo API hoạt động end-to-end.

## Decision 6: Lộ trình search nhiều lớp
- **Decision**: Phase 3 dùng search cơ bản trên JSON data; phase 4 thêm filter/sort/pagination;
  phase 5 tích hợp Elasticsearch.
- **Rationale**: Cân bằng tốc độ delivery và khả năng scale.
- **Alternatives considered**:
  - Tích hợp Elasticsearch ngay: phức tạp sớm, vượt nhu cầu MVP.

## Decision 7: Security triển khai theo hardening phase
- **Decision**: Phase 2 thiết lập auth cơ bản (JWT + role), phase 6 hardening mở rộng
  (rate limiting, audit logs, secure headers, abuse protection).
- **Rationale**: Đưa bảo mật cơ bản sớm để bảo vệ CRUD; hardening chuyên sâu sau khi luồng nghiệp vụ ổn định.
- **Alternatives considered**:
  - Dồn toàn bộ security vào cuối: rủi ro cao cho phase trung gian.

## Resolved Clarifications

Không còn mục NEEDS CLARIFICATION trong technical context của plan.
