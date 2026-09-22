# SetupBuilder — Backend

REST API for **SetupBuilder**, a PC build planner for the Saudi market. Handles
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
- A `firebase-service-account.json` file (not in this repo)

### 1. Start PostgreSQL

The `docker-compose.yml` lives one level up (in the workspace root
`SetupBuilder/`, where the frontend also sits). From that folder:

```bash
docker compose up -d
