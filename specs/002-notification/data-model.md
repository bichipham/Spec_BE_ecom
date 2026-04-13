# Data Model - Notification System

## 1) Notification

- **Description**: Bản ghi thông báo gửi qua một kênh cụ thể đến một người nhận.
- **Fields**:
  - `id` (string/uuid, required, unique)
  - `recipientId` (string/uuid, required) — ID người nhận; không bắt buộc tồn tại trong hệ thống User
  - `channel` (enum: `ChannelType`, required)
  - `subject` (string, optional, max 255 chars) — bỏ qua khi channel là SMS hoặc ZALO
  - `body` (string, required, max 160 chars khi channel = SMS, max 4000 chars otherwise)
  - `status` (enum: `NotificationStatus`, required)
  - `createdAt` (datetime UTC, required) — thời điểm tạo bản ghi
  - `sentAt` (datetime UTC, nullable) — thời điểm gửi thành công; null khi PENDING hoặc FAILED
- **Validation Rules**:
  - `recipientId` bắt buộc, không được blank.
  - `body` bắt buộc, không được blank.
  - Khi `channel = SMS`: `body` ≤ 160 ký tự.
  - `subject` được bỏ qua (không lưu/không trả về lỗi) khi channel là SMS hoặc ZALO.
- **Relationships**:
  - Không có foreign key cứng đến `User` ở tầng persistence JSON; `recipientId` là string tham chiếu.
- **State Transitions**:
  - `(new) → PENDING` — khi bản ghi vừa được tạo, trước khi gọi sender.
  - `PENDING → SENT` — khi sender hoàn thành không ném exception; `sentAt` được gán.
  - `PENDING → FAILED` — khi sender ném exception; `sentAt` vẫn null.
  - Không cho phép chuyển ngược trạng thái.

## 2) ChannelType (enum)

```
EMAIL
SMS
ZALO
```

## 3) NotificationStatus (enum)

```
PENDING
SENT
FAILED
```

## Storage Mapping

| ChannelType | File JSON |
|-------------|-----------|
| EMAIL | `backend/src/main/resources/data/notifications-email.json` |
| SMS | `backend/src/main/resources/data/notifications-sms.json` |
| ZALO | `backend/src/main/resources/data/notifications-zalo.json` |

Mỗi file là một JSON array `[Notification, ...]`. File được khởi tạo trống `[]` nếu chưa tồn tại.

## Java Class Mapping

```
domain/model/ChannelType.java         → enum ChannelType { EMAIL, SMS, ZALO }
domain/model/NotificationStatus.java  → enum NotificationStatus { PENDING, SENT, FAILED }
domain/model/Notification.java        → @Data @Builder — tất cả fields trên + camelCase
domain/repository/NotificationRepository.java
    → interface: save(Notification), findById(String): Optional<Notification>
infrastructure/persistence/json/JsonNotificationRepository.java
    → implements NotificationRepository; routing theo channel
```

## Cross-Entity Consistency Rules

- `JsonNotificationRepository.save()` ghi vào đúng file theo `notification.getChannel()`.
  - Nếu `id` đã tồn tại → replace (update).
  - Nếu `id` chưa tồn tại → append.
- `findById()` tìm tuần tự: email → sms → zalo; trả `Optional.empty()` nếu không tìm thấy.
- Migration readiness: thay `JsonNotificationRepository` bằng JPA `@Entity` là đủ, không thay đổi `NotificationService`.
