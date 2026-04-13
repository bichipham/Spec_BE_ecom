# Implementation Plan: Notification System

**Branch**: `[002-notification]` | **Date**: 2026-04-13 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/002-notification/spec.md`

## Summary

Xây dựng hệ thống thông báo đa kênh (Email, SMS, Zalo) sử dụng Factory Pattern để phân giải
`NotificationSender` theo `ChannelType`. Interface `NotificationSender` một phương thức `send()`;
ba stub implementation ghi log. Domain entity `Notification` lưu trạng thái
`PENDING / SENT / FAILED`; persistence tạm thời bằng file JSON riêng theo kênh. Hai endpoint:
`POST /api/v1/notifications/send` và `GET /api/v1/notifications/{id}`.

## Technical Context

**Language/Version**: Java 21  
**Primary Dependencies**: Spring Boot 3.3.x (Web, Validation), Jackson, Lombok, springdoc-openapi  
**Storage**: JSON files theo kênh (`notifications-email.json`, `notifications-sms.json`, `notifications-zalo.json`) qua repository abstraction  
**Testing**: JUnit 5, Spring Boot Test, MockMvc, AssertJ  
**Target Platform**: Linux server (container-ready)  
**Project Type**: Backend web-service (REST API)  
**Performance Goals**: p95 < 300ms cho read, < 500ms cho send với dữ liệu vận hành cơ bản  
**Constraints**: API versioned `/api/v1`, validation bắt buộc, response lỗi chuẩn hóa, không truy cập file trực tiếp từ controller; SMS body ≤ 160 ký tự; subject bỏ qua cho SMS/ZALO  
**Scale/Scope**: 1 domain entity + Factory Pattern + 3 channel senders (stub)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

**Pre-Design Gate (PASS)**

- [x] Layering preserved: API -> service -> repository; không truy cập storage trực tiếp từ controller.
- [x] Contract-first API defined: versioned endpoints, schema definitions, và error format.
- [x] Test strategy defined: unit + integration coverage cho success và failure paths.
- [x] Storage abstraction present: `NotificationRepository` interface isolate JSON file persistence.
- [x] Migration readiness documented: thay `JsonNotificationRepository` bằng JPA là đủ.
- [x] Observability included: structured logging, correlation IDs, health/readiness endpoints (kế thừa từ 001).

**Post-Design Gate (PASS)**

- [x] `data-model.md` định nghĩa entity `Notification`, enum `ChannelType`, `NotificationStatus`, ràng buộc dữ liệu.
- [x] `contracts/openapi.yaml` chốt API contracts cho `POST /send` và `GET /{id}`.
- [x] `quickstart.md` mô tả kiểm thử độc lập từng user story.
- [x] `research.md` chốt quyết định kỹ thuật không còn mục NEEDS CLARIFICATION.

## Project Structure

### Documentation (this feature)

```text
specs/002-notification/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
backend/
├── src/
│   ├── main/java/com/ecom/
│   │   ├── api/
│   │   │   └── notification/
│   │   │       ├── NotificationController.java
│   │   │       └── dto/
│   │   │           └── NotificationDtos.java
│   │   ├── application/
│   │   │   └── notification/
│   │   │       ├── NotificationSender.java        # interface
│   │   │       ├── EmailSender.java
│   │   │       ├── SmsSender.java
│   │   │       ├── ZaloSender.java
│   │   │       ├── NotificationFactory.java
│   │   │       └── NotificationService.java
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── Notification.java
│   │   │   │   ├── ChannelType.java
│   │   │   │   └── NotificationStatus.java
│   │   │   └── repository/
│   │   │       └── NotificationRepository.java
│   │   └── infrastructure/
│   │       └── persistence/
│   │           └── json/
│   │               └── JsonNotificationRepository.java
│   └── main/resources/
│       └── data/
│           ├── notifications-email.json
│           ├── notifications-sms.json
│           └── notifications-zalo.json
└── tests/
    ├── contract/
    ├── integration/
    └── unit/

specs/
└── 002-notification/
    ├── contracts/
    ├── data-model.md
    ├── plan.md
    ├── quickstart.md
    └── research.md
```

**Structure Decision**: Tích hợp vào cấu trúc web-service backend hiện có từ feature 001.
Tuân thủ cùng phân tầng domain/application/infrastructure; chỉ thêm package `notification` mới.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | N/A | N/A |
