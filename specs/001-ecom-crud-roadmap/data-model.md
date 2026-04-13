# Data Model - E-commerce Backend CRUD & Scale Roadmap

## 1) User

- **Description**: Tài khoản sử dụng hệ thống.
- **Fields**:
  - `id` (string/uuid, required, unique)
  - `name` (string, required, 2-120 chars)
  - `email` (string, required, unique, email format)
  - `password` (string, required, hashed)
  - `role` (enum: `ADMIN`, `OPERATOR`, required)
  - `created_at` (datetime UTC, required)
  - `updated_at` (datetime UTC, required)
- **Validation Rules**:
  - Email bắt buộc đúng định dạng và không trùng.
  - Password không lưu plain text.
- **Relationships**:
  - 1 User -> N Orders.

## 2) Product

- **Description**: Hàng hóa trong hệ thống.
- **Fields**:
  - `id` (string/uuid, required, unique)
  - `name` (string, required, 2-255 chars)
  - `price` (decimal(12,2), required, >= 0)
  - `stock` (integer, required, >= 0)
  - `created_at` (datetime UTC, required)
  - `updated_at` (datetime UTC, required)
- **Validation Rules**:
  - `price >= 0`, `stock >= 0`.
- **Relationships**:
  - 1 Product -> N OrderItems.

## 3) Order

- **Description**: Đơn hàng thuộc về người dùng.
- **Fields**:
  - `id` (string/uuid, required, unique)
  - `user_id` (string/uuid, required)
  - `total_amount` (decimal(12,2), required, >= 0)
  - `status` (enum: `PENDING`, `PAID`, `SHIPPED`, `CANCELLED`, required)
  - `created_at` (datetime UTC, required)
  - `updated_at` (datetime UTC, required)
- **Validation Rules**:
  - `user_id` phải tồn tại.
  - `total_amount` tính từ tổng `order_items`.
- **Relationships**:
  - N Orders -> 1 User.
  - 1 Order -> N OrderItems.
- **State Transitions**:
  - `PENDING -> PAID -> SHIPPED`
  - `PENDING -> CANCELLED`
  - Không cho phép chuyển trạng thái ngược trừ khi có chính sách override đặc biệt.

## 4) OrderItem

- **Description**: Dòng sản phẩm trong đơn hàng.
- **Fields**:
  - `id` (string/uuid, required, unique)
  - `order_id` (string/uuid, required)
  - `product_id` (string/uuid, required)
  - `quantity` (integer, required, > 0)
  - `price` (decimal(12,2), required, >= 0) # snapshot giá tại thời điểm đặt hàng
  - `created_at` (datetime UTC, required)
  - `updated_at` (datetime UTC, required)
- **Validation Rules**:
  - `order_id` và `product_id` phải tồn tại.
  - `quantity > 0` và không vượt tồn kho tại thời điểm tạo/cập nhật.
- **Relationships**:
  - N OrderItems -> 1 Order.
  - N OrderItems -> 1 Product.

## 5) AuthSession (phase 2)

- **Description**: Phiên đăng nhập của người dùng.
- **Fields**:
  - `session_id` (string/uuid, required)
  - `user_id` (string/uuid, required)
  - `role` (enum, required)
  - `token_hash` (string, required)
  - `expires_at` (datetime UTC, required)
  - `created_at` (datetime UTC, required)

## 6) SearchIndexRecord (phase 5)

- **Description**: Bản ghi chỉ mục phục vụ truy vấn chuyên dụng.
- **Fields**:
  - `index_id` (string, required)
  - `entity_type` (enum: `PRODUCT`, `ORDER`, required)
  - `entity_id` (string/uuid, required)
  - `document` (object/json, required)
  - `indexed_at` (datetime UTC, required)

## Cross-Entity Consistency Rules

- Khi tạo/cập nhật/xóa `OrderItem`, `Order.total_amount` phải được tính lại.
- Tránh âm kho khi cập nhật `Product.stock`.
- Xóa `User`/`Product` đang tham chiếu bởi `Order`/`OrderItem` dùng soft-delete hoặc chặn xóa tùy policy.
