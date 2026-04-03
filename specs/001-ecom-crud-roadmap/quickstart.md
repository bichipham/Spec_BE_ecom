# Quickstart - E-commerce Backend CRUD & Scale Roadmap

## 1. Scope kiểm thử theo phase

- **Phase 1 (CRUD)** ✅: User, Product, Order, OrderItem CRUD + validation + relation consistency.
- **Phase 2 (Auth)**: login, role-based authorization, protected endpoints.
- **Phase 3 (Basic Search)**: keyword search cho Product/Order.
- **Phase 4 (Advanced Search)**: filter/sort/pagination + multi-condition query.
- **Phase 5 (Enterprise Search)**: tích hợp Elasticsearch + indexing/sync.
- **Phase 6 (Security)**: hardening (rate limit, audit logs, secure headers, abuse controls).

## 2. Chạy ứng dụng

```bash
# từ root của repo
JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home \
  /usr/local/bin/mvn -f backend/pom.xml spring-boot:run
```

- **Base URL**: `http://localhost:8081/api/v1`
- **Swagger UI**: `http://localhost:8081/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8081/v3/api-docs`
- **Health**: `http://localhost:8081/api/v1/health`
- **Ready**: `http://localhost:8081/api/v1/ready`

## 3. Phase 1 — Luồng CRUD nhanh (curl)

> Chạy từng bước theo thứ tự. Thay các `<id>` bằng UUID nhận được ở bước trước.

### 3.1 Tạo User

```bash
curl -s -X POST http://localhost:8081/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Smith",
    "email": "alice@example.com",
    "password": "s3cr3t!Pass",
    "role": "ADMIN"
  }' | jq .
# Lưu lại "id" → USER_ID
```

### 3.2 Tạo Product

```bash
curl -s -X POST http://localhost:8081/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Mouse",
    "price": 29.99,
    "stock": 100
  }' | jq .
# Lưu lại "id" → PRODUCT_ID
```

### 3.3 Tạo Order (cho User)

```bash
curl -s -X POST http://localhost:8081/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{"user_id": "<USER_ID>"}' | jq .
# Lưu lại "id" → ORDER_ID
# total_amount = 0, status = PENDING
```

### 3.4 Thêm OrderItem (snapshot giá, trừ stock)

```bash
curl -s -X POST http://localhost:8081/api/v1/order-items \
  -H "Content-Type: application/json" \
  -d '{
    "order_id": "<ORDER_ID>",
    "product_id": "<PRODUCT_ID>",
    "quantity": 3
  }' | jq .
# Lưu lại "id" → ITEM_ID
# Kiểm tra: order total_amount = 3 × 29.99 = 89.97, product stock = 97
```

### 3.5 Kiểm tra Order (total_amount đã cập nhật)

```bash
curl -s http://localhost:8081/api/v1/orders/<ORDER_ID> | jq .
# total_amount phải = 89.97
```

### 3.6 Chuyển trạng thái Order PENDING → PAID

```bash
curl -s -X PUT http://localhost:8081/api/v1/orders/<ORDER_ID> \
  -H "Content-Type: application/json" \
  -d '{"status": "PAID"}' | jq .
```

### 3.7 Chuyển trạng thái Order PAID → SHIPPED

```bash
curl -s -X PUT http://localhost:8081/api/v1/orders/<ORDER_ID> \
  -H "Content-Type: application/json" \
  -d '{"status": "SHIPPED"}' | jq .
```

### 3.8 Xóa OrderItem (khôi phục stock, recalculate total)

```bash
curl -s -X DELETE http://localhost:8081/api/v1/order-items/<ITEM_ID>
# stock product trở về 100, order total_amount = 0
```

### 3.9 Xóa Order (phải không còn order item)

```bash
curl -s -X DELETE http://localhost:8081/api/v1/orders/<ORDER_ID>
```

### 3.10 Thử xóa Product đang được tham chiếu (expect 400)

```bash
# Tạo lại order + item trước, rồi:
curl -s -X DELETE http://localhost:8081/api/v1/products/<PRODUCT_ID>
# → 400 Bad Request: "Cannot delete product ... still referenced by order items"
```

## 4. Kiểm tra lỗi validation (expect 400)

```bash
# Email trùng
curl -s -X POST http://localhost:8081/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Bob","email":"alice@example.com","password":"12345678","role":"OPERATOR"}' | jq .

# Quantity vượt stock
curl -s -X POST http://localhost:8081/api/v1/order-items \
  -H "Content-Type: application/json" \
  -d '{"order_id":"<ORDER_ID>","product_id":"<PRODUCT_ID>","quantity":9999}' | jq .

# Chuyển trạng thái không hợp lệ (SHIPPED → PENDING)
curl -s -X PUT http://localhost:8081/api/v1/orders/<ORDER_ID> \
  -H "Content-Type: application/json" \
  -d '{"status":"PENDING"}' | jq .
```

## 5. Expected checks

- Response dùng format lỗi chuẩn hóa (`code`, `message`, `errors`, `timestamp`, `correlation_id`).
- IDs là UUID duy nhất cho mọi entity.
- `total_amount` của order khớp chính xác với tổng `quantity × price` của các order items.
- Không cho tạo order item vượt stock hiện có của product.
- Xóa order item khôi phục stock về đúng số lượng.
- Logs có `correlationId` cho mỗi request (header `X-Correlation-Id`).
- Dữ liệu được lưu vào `backend/src/main/resources/data/*.json` dưới dạng JSON array.

## 6. Migration readiness checks

- Tất cả business logic gọi qua repository interface (`UserRepository`, v.v.).
- Không có controller truy cập file JSON trực tiếp.
- Mapping data model độc lập storage implementation (thay `Json*Repository` → JPA là đủ).
