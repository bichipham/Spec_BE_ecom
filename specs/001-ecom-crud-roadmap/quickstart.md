# Quickstart - E-commerce Backend CRUD & Scale Roadmap

## 1. Scope kiểm thử theo phase

- **Phase 1 (CRUD)**: User, Product, Order, OrderItem CRUD + validation + relation consistency.
- **Phase 2 (Auth)**: login, role-based authorization, protected endpoints.
- **Phase 3 (Basic Search)**: keyword search cho Product/Order.
- **Phase 4 (Advanced Search)**: filter/sort/pagination + multi-condition query.
- **Phase 5 (Enterprise Search)**: tích hợp Elasticsearch + indexing/sync.
- **Phase 6 (Security)**: hardening (rate limit, audit logs, secure headers, abuse controls).

## 2. API base

- Base URL: `/api/v1`
- Health endpoints: `/api/v1/health`, `/api/v1/ready`

## 3. Phase 1 - Kiểm thử nhanh CRUD

1. Tạo user mới.
2. Tạo product mới.
3. Tạo order cho user.
4. Tạo order item cho order và product.
5. Cập nhật order status.
6. Xóa product không còn tham chiếu.
7. Xác minh dữ liệu JSON đã được cập nhật nhất quán.

## 4. Expected checks

- Response dùng format lỗi chuẩn hóa.
- IDs là duy nhất.
- `total_amount` của order khớp với tổng order items.
- Không cho tạo order item vượt stock.
- Logs có correlation id cho mỗi request.

## 5. Migration readiness checks

- Tất cả business logic gọi qua repository interface.
- Không có controller truy cập file JSON trực tiếp.
- Mapping data model độc lập storage implementation.
