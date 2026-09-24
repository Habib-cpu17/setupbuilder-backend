# SetupBuilder — Backend

REST API for SetupBuilder, a PC build planner for the Saudi market. Handles
component catalog, build management, compatibility checks, community features,
profiles, admin operations, and AI-powered price estimation + chat.

## Stack

| Layer | Technology |
| :--- | :--- |
| Language | Java 21 |
| Framework | Spring Boot 3.5.6 |
| Modules | Web, Data JPA, Security, Validation |
| Database | PostgreSQL 16 |
| Auth | Firebase Authentication (Admin SDK) |
| AI | Google Gemini API |
| Build tool | Maven (wrapper included) |
| Deployment | Render (Docker) |

## Local Development

### Prerequisites

- JDK 21
- Docker Desktop (for local PostgreSQL)
- IntelliJ IDEA or any Java IDE
- A firebase-service-account.json file (not in this repo)

### 1. Start PostgreSQL

The docker-compose.yml lives one level up in the workspace root
(SetupBuilder/, where the frontend also sits). From that folder:

    docker compose up -d

Postgres binds to host port 5332 -> container 5432.
(5332 avoids conflicts with any existing Postgres install on the machine.)

Verify:

    docker compose ps
    # setupbuilder-postgres   Up (healthy)

### 2. Create src/main/resources/application-local.properties

This file is gitignored - it holds secrets that must not be committed.

    gemini.api.key=AIzaSy...your-real-key
    spring.datasource.url=jdbc:postgresql://localhost:5332/setupbuilder_db
    spring.datasource.username=setupbuilder
    spring.datasource.password=setupbuilder_pass
    app.admin.emails=your-email@example.com
    app.cors.allowed-origins=http://localhost:5173
    app.image-base-url=http://localhost:8080

### 3. Place firebase-service-account.json

Download it from Firebase Console -> Project Settings -> Service Accounts ->
Generate new private key. Put the file at
src/main/resources/firebase-service-account.json. Also gitignored.

### 4. Run with the local profile

In IntelliJ: Run -> Edit Configurations -> set Active profiles to "local".

Or from terminal:

    ./mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local

Backend starts on http://localhost:8080.

### Expected startup log

    The following 1 profile is active: "local"
    HikariPool-1 - Start completed.
    Firebase Admin SDK initialized successfully.
    DataSeeder : Seed complete. 200 components available.
    Started SetupbuilderBackendApplication in X.XXX seconds

### Verify

    curl.exe "http://localhost:8080/api/components?category=CPU"

Should return a JSON list of CPUs.

## Environment Variables

See ENVIRONMENT.md for every variable, per environment.

## Deployment

See DEPLOYMENT.md for the Render deployment guide.

## Architecture

See ARCHITECTURE.md for the layer breakdown and diagrams.

## Troubleshooting

See TROUBLESHOOTING.md for every error hit during development and its fix.

## Notes

- Price provider: currently MockPriceProvider (simulates +/-5% price swings
  around the stored fallback price). Real retailer integration is stubbed in
  ApifyPriceProvider but is not configured. Set pricing.provider=apify
  and fill in the actor credentials to enable it.
- Schema management: Hibernate ddl-auto=update - no Flyway or Liquibase.
- File uploads: stored on local disk under uploads/, served via Spring's
  static resource handler at /uploads/**. Ephemeral on Render's free tier.
- Component images: bundled per-product photos live under
  src/main/resources/static/images/components/ (served at /images/components/**).
  DataSeeder builds each component's imageUrl from app.image-base-url.
  On Render set APP_BASE_URL to the backend's public URL (see ENVIRONMENT.md).

## License

Private project. Not for redistribution.
