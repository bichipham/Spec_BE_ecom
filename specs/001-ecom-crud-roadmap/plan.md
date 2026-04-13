# Implementation Plan: E-commerce Backend CRUD & Scale Roadmap

**Branch**: `[001-ecom-crud-roadmap]` | **Date**: 2026-04-02 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-ecom-crud-roadmap/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Thiết kế backend e-commerce theo hướng domain-first, hoàn thành trước mô hình dữ liệu và CRUD
cho `users`, `products`, `orders`, `order_items`, lưu trữ tạm thời bằng JSON thông qua lớp
repository abstraction. Sau MVP CRUD sẽ triển khai theo lộ trình 6 phase: Auth,
Basic Search, Advanced Search, Enterprise Search, Security Hardening.

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: Java 21  
**Primary Dependencies**: Spring Boot 3.3.x (Web, Validation, Security), Jackson, Lombok, springdoc-openapi  
**Storage**: JSON files (temporary) via repository adapters; designed to migrate to PostgreSQL later  
**Testing**: JUnit 5, Spring Boot Test, MockMvc, AssertJ  
**Target Platform**: Linux server (container-ready)
**Project Type**: Backend web-service (REST API)  
**Performance Goals**: Phase 1 CRUD p95 < 300ms read, < 500ms write với dữ liệu vận hành cơ bản  
**Constraints**: API versioned `/api/v1`, validation bắt buộc, response lỗi chuẩn hóa, không truy cập file trực tiếp từ controller  
**Scale/Scope**: 4 domain entities cốt lõi + roadmap 6 phase (CRUD -> Auth -> Search -> Advanced Search -> Enterprise Search -> Security)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

**Pre-Design Gate (PASS)**

- [x] Layering preserved: API -> service -> repository; no direct storage access in API layer.
- [x] Contract-first API defined: versioned endpoints, schema definitions, and error format.
- [x] CRUD test strategy defined: unit + integration coverage for success and failure paths.
- [x] Storage abstraction present: repository interfaces isolate JSON file persistence.
- [x] Migration readiness documented: path from JSON files to database-backed storage.
- [x] Observability included: structured logging, correlation IDs, health/readiness endpoints.

**Post-Design Gate (PASS)**

- [x] `data-model.md` định nghĩa quan hệ thực thể, ràng buộc dữ liệu và trạng thái nghiệp vụ.
- [x] `contracts/openapi.yaml` chốt API contracts versioned cho CRUD + auth + search.
- [x] `quickstart.md` mô tả kiểm thử độc lập theo phase.
- [x] `research.md` chốt quyết định kỹ thuật không còn mục NEEDS CLARIFICATION.

## Project Structure

### Documentation (this feature)

```text
specs/001-ecom-crud-roadmap/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)
<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

```text
backend/
├── src/
│   ├── main/java/com/ecom/
│   │   ├── api/
│   │   ├── application/
│   │   ├── domain/
│   │   ├── infrastructure/
│   │   │   ├── persistence/json/
│   │   │   └── observability/
│   │   └── config/
│   └── main/resources/
│       ├── application.yml
│       └── data/
└── tests/
  ├── contract/
  ├── integration/
  └── unit/

specs/
└── 001-ecom-crud-roadmap/
  ├── contracts/
  ├── data-model.md
  ├── plan.md
  ├── quickstart.md
  └── research.md
```

**Structure Decision**: Chọn cấu trúc web-service backend đơn (không frontend trong scope).
Tách rõ domain/application/infrastructure để đáp ứng nguyên tắc scale và thay thế storage.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | N/A | N/A |
