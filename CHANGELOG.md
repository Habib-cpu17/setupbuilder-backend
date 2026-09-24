# Changelog

All notable changes to the SetupBuilder backend, organized by week.

## [Week 4] - 2025-09-14 to 2025-09-17

### Added
- Initial project scaffold: Spring Boot 3.5 with Web, Data JPA, Security
- Docker Compose for local PostgreSQL (host port 5332)
- BaseEntity + JPA auditing (@CreatedDate, @LastModifiedDate)
- Core entities: User, Component, Build, BuildComponent, Comment
- Enums: UserRole, ComponentCategory
- Repositories for all entities
- Firebase Admin SDK integration (FirebaseConfig)
- FirebaseAuthenticationFilter - verifies Firebase ID tokens
- SecurityConfig - public GET routes, admin gating, CORS via env var

### Decisions
- Chose PostgreSQL everywhere (local + prod) over MySQL
- Chose Firebase Auth over hand-rolled authentication

## [Week 5] - 2025-09-20 to 2025-09-24

### Added
- Component seed: 43 initial catalog items with SAR prices and images
- ComponentController - public listing, filtering, pagination
- BuildController - CRUD for authenticated users
- CompatibilityService - socket, RAM type, PSU wattage checks
- DataSeeder - initial seed data
- GlobalExceptionHandler - friendly JSON error responses

### Fixed
- CORS preflight handling on /api/**
- @EnableJpaAuditing wired so entity timestamps populate

### Decisions
- Moved seed data into a CommandLineRunner that skips if the catalog is non-empty
- Chose Map<String, String> for component specs - flexible without schema churn

## [Week 6] - 2025-09-27 to 2025-10-01

### Added
- CommentController + CommentService
- UserController - public profile view, update own profile
- ProfileService - statistics + achievement computation
- CuratedController - public curated builds + admin add/remove/rank
- AdminComponentController - full CRUD for components
- AdminUserController - list users, update roles
- Admin bootstrap via ADMIN_EMAILS env var in UserService

### Changed
- Build detail response now includes compatibility warnings
- Component entity got imageUrl + active flag

### Decisions
- Admin role gated by ROLE_ADMIN in SecurityConfig, no separate annotation

## [Week 7] - 2025-10-04 to 2025-10-08

### Added
- 200+ component seed data across all 7 categories
- Realistic SAR pricing including current RAM price surge
- PriceHistory entity + PriceHistoryRepository
- PriceProvider interface with MockPriceProvider and ApifyPriceProvider (stub)
- PriceProviderConfig - conditional bean selection via pricing.provider
- PriceService - orchestrates refresh + writes history
- PriceRefreshJob - scheduled via pricing.refresh.cron
- AdminPriceController - manual refresh + provider status

### Decisions
- Shipped with MockPriceProvider active - real integration is a future phase
- Scheduled refresh runs via @Scheduled + @EnableScheduling

## [Week 8] - 2025-10-11 to 2025-10-15

### Added
- Gemini integration for price estimation (AiPriceService)
- Multi-model fallback chain (flash-latest -> flash-lite-latest) with retries
- ChatSession + ChatMessage entities
- GeminiChatService - multi-turn chat with system prompt
- ChatService - orchestrates session persistence + Gemini calls
- ChatController - sessions CRUD + send message
- System prompt injects the full component catalog for accurate recommendations

### Decisions
- Used Java's built-in HttpClient instead of a Gemini SDK dependency
- Chat history capped at last 20 messages to keep prompt cost predictable

## [Week 9] - 2025-10-18 to 2025-10-22

### Added
- FileStorageService - avatar/banner upload with size + MIME validation
- UploadController - /api/uploads/avatar, /api/uploads/banner
- WebConfig - serves /uploads/** via Spring resource handler
- ProfileService - deletes old file on replace

### Changed
- User entity gained avatarUrl, bannerUrl, bio, location, websiteUrl

## [Week 11] - 2026-09-21 to 2026-09-25

### Added
- Per-product component images for all 203 catalog items, bundled under
  src/main/resources/static/images/components/ (139 files, max 800px JPEG)
- app.image-base-url property controlling how component image URLs are built
  (local: http://localhost:8080, prod: ${APP_BASE_URL})

### Changed
- DataSeeder now maps each component to a real product photo by model (CPU by
  socket, RAM/storage/PSU by product line). Same-chassis/same-line models share
  one photo (e.g. cm-mwe-bronze.jpg, nvidia-rtx-4060ti.jpg). Falls back to the
  category Unsplash image when a photo is missing.
- SecurityConfig permits GET /images/** so component photos load in <img> tags

## [Week 10] - 2025-10-25 to 2025-10-29

### Changed
- Unified to PostgreSQL - removed MySQL dependency from pom.xml
- Replaced hibernate.dialect=MySQLDialect with PostgreSQLDialect
- Introduced Spring profiles: local (Docker) and prod (Render)
- Moved all env-specific values to application-{profile}.properties and env vars
- FirebaseConfig now reads base64-encoded JSON from
  FIREBASE_SERVICE_ACCOUNT_BASE64 if present, falls back to classpath file

### Removed
- application-local.properties from git tracking (added to .gitignore)

### Decisions
- Database URL format: jdbc:postgresql://host:5432/dbname - credentials in
  separate env vars, never embedded in the URL

## [Unreleased]

### Planned
- Apify integration for real retailer prices
- Persistent file storage (Cloudflare R2)
- Migration path to Supabase / Neon to escape the 30-day Render free tier
- Followers + feed endpoint
- Optional Arabic / RTL support
