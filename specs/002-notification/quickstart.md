# Quickstart - Notification System

## 1. Scope kiểm thử theo user story

- **P1 — Gửi thông báo qua kênh được chọn** ✅: Factory phân giải đúng sender, notification lưu đúng file JSON với trạng thái `SENT`.
- **P2 — Truy vấn trạng thái và chi tiết thông báo** ✅: `GET /notifications/{id}` trả đầy đủ trường; 404 nếu không tồn tại.
- **P3 — Xử lý kênh không hợp lệ và lỗi đầu vào** ✅: Validation trả 400 chi tiết; sender failure lưu `FAILED`.

## 2. Chạy ứng dụng

```bash
# từ root của repo
JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home \
  /usr/local/bin/mvn -f backend/pom.xml spring-boot:run
```

- **Base URL**: `http://localhost:8081/api/v1`
- **Swagger UI**: `http://localhost:8081/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8081/v3/api-docs`
- **Health**: `http://localhost:8081/api/v1/health`

## 3. P1 — Gửi thông báo (curl)

### 3.1 Gửi qua EMAIL

```bash
curl -s -X POST http://localhost:8081/api/v1/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "recipientId": "user-uuid-001",
    "channel": "EMAIL",
    "subject": "Order Confirmation",
    "body": "Your order has been confirmed."
  }' | jq .
# → 201 Created; status = SENT; kiểm tra notifications-email.json
```

### 3.2 Gửi qua SMS (body ≤ 160 ký tự)

```bash
curl -s -X POST http://localhost:8081/api/v1/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "recipientId": "user-uuid-001",
    "channel": "SMS",
    "body": "Your order #1234 has been shipped."
  }' | jq .
# → 201 Created; status = SENT; subject bị bỏ qua; kiểm tra notifications-sms.json
# Lưu lại "id" → NOTIF_ID
```

### 3.3 Gửi qua ZALO

```bash
curl -s -X POST http://localhost:8081/api/v1/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "recipientId": "user-uuid-002",
    "channel": "ZALO",
    "body": "Đơn hàng của bạn đang được giao."
  }' | jq .
# → 201 Created; status = SENT; kiểm tra notifications-zalo.json
```

## 4. P2 — Truy vấn thông báo

### 4.1 Tra cứu theo ID (tồn tại)

```bash
curl -s http://localhost:8081/api/v1/notifications/<NOTIF_ID> | jq .
# → 200 OK; đầy đủ: id, recipientId, channel, subject (null cho SMS), body, status, createdAt, sentAt
```

### 4.2 Tra cứu theo ID (không tồn tại)

```bash
curl -s http://localhost:8081/api/v1/notifications/non-existent-id | jq .
# → 404 Not Found với ErrorResponse
```

## 5. P3 — Validation và lỗi đầu vào

### 5.1 Kênh không hợp lệ — unknown enum string (T031 gap fix)

```bash
curl -s -X POST http://localhost:8081/api/v1/notifications/send \
  -H "Content-Type: application/json" \
  -d '{"recipientId":"uid","channel":"UNKNOWN","body":"test"}' | jq .
# → 400 Bad Request
# {
#   "code": "INVALID_REQUEST",
#   "message": "Invalid value 'UNKNOWN' — allowed values: EMAIL, SMS, ZALO",
#   "errors": [],
#   "timestamp": "...",
#   "correlation_id": "..."
# }
```

### 5.2 Thiếu recipientId

```bash
curl -s -X POST http://localhost:8081/api/v1/notifications/send \
  -H "Content-Type: application/json" \
  -d '{"channel":"EMAIL","body":"test"}' | jq .
# → 400 Bad Request
# {
#   "code": "VALIDATION_ERROR",
#   "message": "Request validation failed",
#   "errors": [{"field": "recipientId", "message": "recipient_id is required"}],
#   "timestamp": "..."
# }
```

### 5.3 Thiếu body

```bash
curl -s -X POST http://localhost:8081/api/v1/notifications/send \
  -H "Content-Type: application/json" \
  -d '{"recipientId":"uid","channel":"SMS"}' | jq .
# → 400 Bad Request; errors chứa field "body"
```

### 5.4 SMS body vượt 160 ký tự

```bash
curl -s -X POST http://localhost:8081/api/v1/notifications/send \
  -H "Content-Type: application/json" \
  -d "{\"recipientId\":\"uid\",\"channel\":\"SMS\",\"body\":\"$(python3 -c 'print("x"*161)')\"}" | jq .
# → 400 Bad Request
# {
#   "code": "BAD_REQUEST",
#   "message": "SMS body must not exceed 160 characters",
#   "errors": [],
#   "timestamp": "..."
# }
```

### 5.5 Sender failure → trạng thái FAILED (strategy isolation)

```bash
# Giả lập bằng cách mock SmsSender trong test; hoặc tạm thời thay đổi SmsSender throw RuntimeException
# Kết quả mong đợi: 201 Created nhưng notification.status = FAILED
# → Không rò rỉ stack trace; lỗi nội bộ được che giấu (FR-011)
# → Notification vẫn được lưu với sentAt = null (FR-007)
# → Các kênh EMAIL và ZALO hoàn toàn không bị ảnh hưởng (FR-013 — strategy isolation)
```

## 6. Expected checks

- Response dùng format lỗi chuẩn hóa (`code`, `message`, `errors`, `timestamp`, `correlation_id`).
- IDs là UUID duy nhất cho mọi notification.
- `sentAt` được gán khi `status = SENT`; null khi `PENDING` hoặc `FAILED`.
- `subject` có giá trị `null` trong response khi `channel = SMS` hoặc `channel = ZALO`.
- Logs có `correlationId` cho mỗi request (header `X-Correlation-Id`).
- Dữ liệu được lưu vào đúng file JSON theo channel trong `backend/src/main/resources/data/`.
- Không có thông tin nội bộ (stack trace) rò rỉ trong response lỗi.

## 7. Migration readiness checks

- Tất cả business logic gọi qua `NotificationRepository` interface.
- `NotificationController` không truy cập file JSON trực tiếp.
- Thay `JsonNotificationRepository` bằng JPA `@Repository` là đủ để migrate sang database.
- `NotificationFactory` không phụ thuộc vào cơ chế lưu trữ.
