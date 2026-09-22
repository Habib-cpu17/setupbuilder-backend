# Environment Variables

## Backend - Production (Render Web Service)

| Key | Required | Description |
| :--- | :--- | :--- |
| SPRING_PROFILES_ACTIVE | Yes | Set to prod |
| DATABASE_URL | Yes | JDBC format: jdbc:postgresql://HOST:5432/DBNAME - do not embed credentials |
| DB_USERNAME | Yes | Postgres username |
| DB_PASSWORD | Yes | Postgres password |
| GEMINI_API_KEY | Yes | Google AI Studio API key (AIzaSy...) |
| FIREBASE_SERVICE_ACCOUNT_BASE64 | Yes | Base64-encoded Firebase service account JSON |
| ADMIN_EMAILS | Optional | Comma-separated emails auto-promoted to ADMIN on login |
| CORS_ALLOWED_ORIGINS | Yes | Comma-separated origins, e.g. https://frontend-ponc.onrender.com |
| JAVA_TOOL_OPTIONS | Optional | -Xmx256m to fit within the free-tier RAM |

## Backend - Local (application-local.properties)

Not committed. Create manually. See README.md for the exact contents.

| Key | Example |
| :--- | :--- |
| gemini.api.key | AIzaSy... |
| spring.datasource.url | jdbc:postgresql://localhost:5332/setupbuilder_db |
| spring.datasource.username | setupbuilder |
| spring.datasource.password | setupbuilder_pass |
| app.admin.emails | your-email@example.com |
| app.cors.allowed-origins | http://localhost:5173 |

## Backend - Base (application.properties, committed)

Committed with placeholder syntax only - never contains secrets.

    server.port=${PORT:8080}

    spring.datasource.url=${DATABASE_URL}
    spring.datasource.username=${DB_USERNAME}
    spring.datasource.password=${DB_PASSWORD}

    gemini.api.key=${GEMINI_API_KEY:}
    gemini.api.base-url=https://generativelanguage.googleapis.com/v1beta/models
    gemini.api.models=gemini-flash-latest,gemini-flash-lite-latest
    gemini.api.retries=2

    app.admin.emails=${ADMIN_EMAILS:}
    app.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:5173}

    pricing.provider=mock
    pricing.refresh.enabled=true
    pricing.refresh.cron=0 0 3 * * *

## Rotating Secrets

If any secret leaks into a public place (chat, screenshot, public commit):

| Secret | Where to rotate |
| :--- | :--- |
| Gemini API key | https://aistudio.google.com/apikey |
| Firebase Admin SDK key | Firebase Console -> Project Settings -> Service Accounts -> Generate new key |
| Postgres password | Render Postgres -> Settings -> Rotate Password -> update DB_PASSWORD env var |
| Firebase Web config | Firebase Console -> Project Settings -> Web app -> regenerate |

After rotation, redeploy the affected service.
