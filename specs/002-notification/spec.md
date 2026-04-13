# Feature Specification: Notification System

**Feature Branch**: `[002-notification]`  
**Created**: 2026-04-13  
**Status**: Draft  
**Input**: User description: "xây dựng hệ thống thông báo gửi qua Email, SMS và Zalo sử dụng Factory Pattern; interface NotificationSender, NotificationFactory phân giải theo ChannelType; lưu trữ tạm JSON theo từng kênh; stub sender; API gửi và truy vấn thông báo"

## User Scenarios & Testing *(mandatory)*

<!--
  IMPORTANT: User stories should be PRIORITIZED as user journeys ordered by importance.
  Each user story/journey must be INDEPENDENTLY TESTABLE - meaning if you implement just ONE of them,
  you should still have a viable MVP (Minimum Viable Product) that delivers value.
  
  Assign priorities (P1, P2, P3, etc.) to each story, where P1 is the most critical.
  Think of each story as a standalone slice of functionality that can be:
  - Developed independently
  - Tested independently
  - Deployed independently
  - Demonstrated to users independently
-->

### User Story 1 - Gửi thông báo qua kênh được chọn (Priority: P1)

Là hệ thống nghiệp vụ, tôi muốn gửi thông báo đến người nhận qua kênh Email, SMS hoặc Zalo
để truyền đạt thông tin kịp thời theo từng ngữ cảnh nghiệp vụ.

**Why this priority**: Đây là chức năng cốt lõi. Factory phân giải đúng sender theo kênh là nền tảng
của toàn bộ hệ thống thông báo. Không có phần này thì P2 và P3 không thể triển khai.

**Independent Test**: Có thể kiểm thử độc lập bằng cách gửi yêu cầu tới `POST /api/v1/notifications/send`
với từng giá trị `channel` (EMAIL, SMS, ZALO), xác nhận đúng sender được gọi và thông báo được
lưu với trạng thái hợp lệ.

**Acceptance Scenarios**:

1. **Given** hệ thống nhận yêu cầu gửi thông báo với `channel = EMAIL`,
   **When** yêu cầu được xử lý, **Then** `EmailSender` được gọi, thông báo được lưu vào
   `notifications-email.json` với trạng thái `SENT`.
2. **Given** hệ thống nhận yêu cầu với `channel = SMS`,
   **When** yêu cầu được xử lý, **Then** `SmsSender` được gọi, thông báo được lưu vào
   `notifications-sms.json` với trạng thái `SENT`.
3. **Given** hệ thống nhận yêu cầu với `channel = ZALO`,
   **When** yêu cầu được xử lý, **Then** `ZaloSender` được gọi, thông báo được lưu vào
   `notifications-zalo.json` với trạng thái `SENT`.

---

### User Story 2 - Truy vấn trạng thái và chi tiết thông báo (Priority: P2)

Là nhân viên vận hành, tôi muốn tra cứu thông tin và trạng thái của một thông báo đã gửi
theo mã định danh để theo dõi và xử lý sự cố nhanh chóng.

**Why this priority**: Sau khi có khả năng gửi thông báo, cần tra cứu kết quả để kiểm soát chất lượng
và hỗ trợ vận hành. Không phụ thuộc vào P3.

**Independent Test**: Có thể kiểm thử độc lập bằng cách tạo thông báo trước, sau đó gọi
`GET /api/v1/notifications/{id}` và xác nhận đầy đủ trường trả về khớp với dữ liệu đã tạo.

**Acceptance Scenarios**:

1. **Given** đã tồn tại thông báo với `id` hợp lệ, **When** gọi
   `GET /api/v1/notifications/{id}`, **Then** hệ thống trả đầy đủ thông tin thông báo bao gồm
   `channel`, `status`, `subject`, `body`, `createdAt`, `sentAt`.
2. **Given** `id` không tồn tại trong hệ thống, **When** gọi
   `GET /api/v1/notifications/{id}`, **Then** hệ thống trả lỗi rõ ràng với mã `404`.

---

### User Story 3 - Xử lý kênh không hợp lệ và lỗi đầu vào (Priority: P3)

Là nhà tích hợp API, tôi muốn nhận thông báo lỗi rõ ràng khi gửi yêu cầu với kênh không hợp lệ
hoặc dữ liệu đầu vào thiếu/sai định dạng để xử lý ngoại lệ phía client đúng cách.

**Why this priority**: Đảm bảo hợp đồng API rõ ràng và giảm thiểu lỗi tích hợp. Phụ thuộc vào P1
đã hoạt động.

**Independent Test**: Có thể kiểm thử độc lập bằng các yêu cầu cố ý sai: `channel = UNKNOWN`,
thiếu `recipientId`, thiếu `body`, và xác nhận mã lỗi và thông điệp trả về.

**Acceptance Scenarios**:

1. **Given** yêu cầu gửi thông báo với `channel = UNKNOWN`,
   **When** hệ thống xử lý, **Then** trả lỗi `400 Bad Request` với thông điệp rõ ràng
   chỉ ra kênh không được hỗ trợ.
2. **Given** yêu cầu thiếu trường bắt buộc (`recipientId` hoặc `body`),
   **When** hệ thống xử lý, **Then** trả lỗi `400 Bad Request` với danh sách trường
   bị thiếu và thông điệp mô tả.
3. **Given** yêu cầu hợp lệ nhưng sender gặp lỗi nội bộ (giả lập),
   **When** hệ thống xử lý, **Then** thông báo được lưu với trạng thái `FAILED` và
   không rò rỉ thông tin nội bộ ra ngoài.

---

### Edge Cases

- Gửi thông báo với `recipientId` tham chiếu đến người nhận không tồn tại trong hệ thống.
- `body` hoặc `subject` rỗng hoặc vượt quá độ dài tối đa cho phép.
- Gửi nhiều thông báo đồng thời đến cùng `recipientId` qua cùng một kênh.
- Truy vấn `GET /api/v1/notifications/{id}` với `id` đúng định dạng nhưng không tồn tại ở
  bất kỳ file JSON nào.
- File JSON lưu trữ bị lỗi cú pháp hoặc không đọc được khi khởi động.
- Thêm giá trị mới vào `ChannelType` mà chưa có implementation sender tương ứng.
- Gửi yêu cầu với `channel` đúng giá trị enum nhưng chứa ký tự thừa hoặc chữ thường.

## Requirements *(mandatory)*

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right functional requirements.
-->

### Functional Requirements

- **FR-001**: Hệ thống MUST định nghĩa interface `NotificationSender` với phương thức
  `void send(Notification notification)` làm hợp đồng cho mọi kênh gửi.
- **FR-002**: Hệ thống MUST có `NotificationFactory` phân giải đúng implementation sender
  dựa trên giá trị `ChannelType` (EMAIL, SMS, ZALO) và ném lỗi rõ ràng với kênh không hỗ trợ.
- **FR-003**: Hệ thống MUST cung cấp ba implementation độc lập, kiểm thử riêng biệt:
  `EmailSender`, `SmsSender`, `ZaloSender`; giai đoạn đầu sử dụng stub ghi log đầu ra.
- **FR-004**: Hệ thống MUST cho phép thêm kênh mới chỉ bằng cách bổ sung giá trị enum mới và
  class implementation tương ứng mà không sửa đổi logic hiện có.
- **FR-005**: Hệ thống MUST có mô hình dữ liệu `Notification` với các thuộc tính: `id`,
  `recipientId`, `channel`, `subject`, `body`, `status` (PENDING/SENT/FAILED),
  `createdAt`, `sentAt`.
- **FR-006**: Hệ thống MUST lưu thông báo vào file JSON riêng theo kênh:
  `notifications-email.json`, `notifications-sms.json`, `notifications-zalo.json`.
- **FR-007**: Hệ thống MUST cập nhật `status` thông báo sang `SENT` khi gửi thành công và
  `FAILED` khi gặp lỗi, đồng thời ghi nhận `sentAt` theo đó.
- **FR-008**: Hệ thống MUST expose `POST /api/v1/notifications/send` nhận yêu cầu gửi
  thông báo, trả định danh và trạng thái sau xử lý.
- **FR-009**: Hệ thống MUST expose `GET /api/v1/notifications/{id}` trả đầy đủ thông tin
  thông báo theo `id`; trả `404` nếu không tìm thấy.
- **FR-010**: Hệ thống MUST kiểm tra tính hợp lệ đầu vào cho mọi yêu cầu gửi thông báo,
  bao gồm sự hiện diện của `recipientId`, `channel`, `body` và định dạng hợp lệ của từng trường.
- **FR-011**: Hệ thống MUST trả phản hồi lỗi nhất quán theo định dạng chuẩn dự án với mã HTTP,
  thông điệp mô tả và không rò rỉ thông tin nội bộ.
- **FR-012**: Hệ thống MUST tách biệt logic gửi thông báo khỏi tầng lưu trữ để dễ thay thế
  file JSON bằng cơ sở dữ liệu thực mà không phá vỡ nghiệp vụ.

### Key Entities *(include if feature involves data)*

- **Notification**: Đại diện một thông báo trong hệ thống; thuộc tính gồm `id` (UUID),
  `recipientId`, `channel` (EMAIL/SMS/ZALO), `subject`, `body`,
  `status` (PENDING/SENT/FAILED), `createdAt`, `sentAt`.
- **ChannelType**: Enum xác định kênh gửi thông báo; giá trị hợp lệ: `EMAIL`, `SMS`, `ZALO`.
  Đây là khóa phân giải trong `NotificationFactory`.
- **NotificationSender**: Interface hành vi gửi thông báo; mọi implementation kênh đều tuân theo
  hợp đồng `void send(Notification notification)`.
- **NotificationFactory**: Thành phần phân giải sender đúng theo `ChannelType` tại runtime,
  áp dụng nguyên tắc Open/Closed.

## Success Criteria *(mandatory)*

<!--
  ACTION REQUIRED: Define measurable success criteria.
  These must be technology-agnostic and measurable.
-->

### Measurable Outcomes

- **SC-001**: 100% yêu cầu gửi hợp lệ với ba kênh (EMAIL, SMS, ZALO) được xử lý đúng sender
  tương ứng theo kịch bản chấp nhận.
- **SC-002**: 100% thông báo đã gửi có thể tra cứu đầy đủ thông tin qua API trong vòng
  dưới 1 giây với dữ liệu vận hành chuẩn.
- **SC-003**: 100% yêu cầu với kênh không hợp lệ hoặc dữ liệu thiếu/sai trả lỗi `400`
  kèm thông điệp mô tả rõ ràng.
- **SC-004**: Tỷ lệ thêm kênh mới thành công mà không cần sửa đổi code hiện có đạt 100%
  (kiểm tra bằng cách bổ sung kênh thử nghiệm trong môi trường phát triển).
- **SC-005**: Tỷ lệ hoàn thành luồng gửi thông báo end-to-end (gửi → lưu → tra cứu) đạt
  ít nhất 95% trong các kịch bản kiểm thử tích hợp.

## Assumptions

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right assumptions based on reasonable defaults
  chosen when the feature description did not specify certain details.
-->

- Giai đoạn đầu sử dụng stub sender ghi log đầu ra; không tích hợp SMTP, API SMS hay Zalo thực.
- Lưu trữ thông báo bằng file JSON tạm thời theo từng kênh; lộ trình chuyển sang cơ sở dữ liệu
  bền vững sẽ giữ nguyên hợp đồng API và hành vi nghiệp vụ chính.
- `recipientId` là định danh chuỗi tham chiếu đến người nhận; hệ thống không xác thực sự tồn tại
  của người nhận trong giai đoạn này.
- Hệ thống không xử lý hàng đợi hay cơ chế thử lại (retry) trong phạm vi đặc tả này.
- Mỗi yêu cầu gửi tạo một bản ghi `Notification` mới với `id` duy nhất bất kể nội dung trùng lặp.
- Định dạng lỗi tuân theo cấu trúc `ErrorResponse` chuẩn đã được thiết lập trong dự án.
- Hệ thống hoạt động trong môi trường đơn luồng hoặc đồng thời thấp ở giai đoạn đầu; các vấn đề
  đồng thời ghi file nằm ngoài phạm vi.
