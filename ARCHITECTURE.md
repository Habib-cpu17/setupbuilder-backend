# Architecture

## High-Level View

    +---------------------+         +---------------------------+
    |  React 19 (Vite)    |  HTTPS  |  Spring Boot 3.5 (Java 21) |
    |  Frontend (SPA)     +-------->|  REST API                  |
    |                     |  JSON   |  - Auth filter             |
    +----------+----------+         |  - Controllers             |
               |                    |  - Services                |
               | Firebase SDK       |  - Repositories            |
               | (browser)          +---------+---------+-------+
               |                              |         |
               v                              v         v
        +-------------+              +-------------+  +--------------+
        | Firebase    |              | PostgreSQL  |  | Gemini API   |
        | Auth        |              | (Docker /   |  | (price est.  |
        |             |              |  Render)    |  |  + chat)     |
        +------+------+              +-------------+  +--------------+
               | ID token
               v
        +----------------------------+
        | Firebase Admin SDK         |
        | (verifies token in filter) |
        +----------------------------+

## Layers

### Controller layer (controller/)
Thin REST endpoints. Every controller:
- Parses the HTTP request
- Delegates to a service
- Returns a DTO wrapped in ResponseEntity

No business logic here.

### Service layer (service/)
All business logic:
- UserService - upserts Firebase users, auto-promotes admins from ADMIN_EMAILS
- ComponentService - catalog listing, filtering, pagination
- BuildService - CRUD, price calculation, compatibility check, ownership checks
- CommentService - comments on builds
- ProfileService - public profile view, update own, achievement computation
- CuratedService - admin-curated builds
- AdminComponentService / AdminUserService - admin operations
- PriceService - refresh prices from the active provider, write price_history
- AiPriceService - Gemini call for market price estimate
- GeminiChatService - multi-turn Gemini chat with fallback chain
- ChatService - chat session + message persistence, system prompt with catalog
- FileStorageService - saves/reads uploaded avatars and banners
- CompatibilityService - socket, RAM type, PSU wattage, missing parts

### Repository layer (repository/)
Spring Data JPA interfaces. One per entity, plus derived/@Query methods.

### Entity layer (entity/)
JPA entities extending BaseEntity (id, created_at, updated_at).

| Entity | Purpose |
| :--- | :--- |
| User | Registered user (linked to Firebase UID) |
| Component | Catalog item (CPU, GPU, ...) |
| Build | User's PC configuration |
| BuildComponent | Many-to-many between Build and Component with quantity + price snapshot |
| Comment | User comment on a build |
| PriceHistory | Audit trail for component price changes |
| ChatSession | A conversation thread |
| ChatMessage | Individual message inside a session |

### Security layer (security/ + config/SecurityConfig)
FirebaseAuthenticationFilter runs before the Spring Security chain:
1. Reads Authorization: Bearer <token> header
2. Verifies the ID token via Firebase Admin SDK
3. Upserts the user row via UserService
4. Sets the Authentication in the SecurityContext

SecurityConfig defines:
- Public routes: /api/components/**, /api/builds/public, /api/users/* (GET only),
  /api/curated, /uploads/**, /images/**, /actuator/health
- Admin routes: /api/admin/** requires ROLE_ADMIN
- Everything else requires authentication
- CORS origins come from CORS_ALLOWED_ORIGINS env var (comma-separated)

### Pricing layer (pricing/)
Abstraction over price sources:

    PriceProvider (interface)
        +-- MockPriceProvider    <- ACTIVE (pricing.provider=mock)
        +-- ApifyPriceProvider   <- stub (pricing.provider=apify)

PriceProviderConfig picks one bean based on the pricing.provider property.

### Scheduler (scheduler/PriceRefreshJob)
Runs at the cron defined by pricing.refresh.cron (default 3:00 AM daily).
Iterates active components, fetches prices from the active provider, and writes
a PriceHistory row when a price changes. Toggle with pricing.refresh.enabled.

## Key Design Decisions

All decisions live in docs/adr/. Summary:

| ADR | Decision |
| :--- | :--- |
| 0001 | Use PostgreSQL everywhere instead of MySQL |
| 0002 | Delegate auth to Firebase |
| 0003 | Use Spring profiles for env separation |
| 0004 | Multi-model Gemini fallback |
| 0005 | Local disk storage for uploads |
| 0006 | Ship with mock price provider |

## Deployment Topology

- Backend: Render Web Service (Docker-based, free tier)
- Database: Render Postgres (free tier, expires after 30 days)
- Frontend: Render Static Site
- Firebase: Managed by Google (free tier)
- Gemini: Google AI Studio (free quota)
