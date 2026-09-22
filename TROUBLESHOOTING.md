# Troubleshooting

Every error hit during development, with the fix.

## Startup Errors

### Could not resolve placeholder 'DATABASE_URL'

Cause: env var missing.
Fix: Add it on Render -> Environment tab. Locally: ensure
application-local.properties has spring.datasource.url.

### Could not resolve placeholder 'GEMINI_API_KEY'

Cause: env var missing on Render or in the local properties file.
Fix: Add it on Render, or add gemini.api.key=AIza... to
application-local.properties.

### Could not resolve placeholder 'CORS_ALLOWED_ORIGINS'

Cause: env var missing on Render, and no default provided.
Fix: Add the env var, or use a default in application-prod.properties:
app.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:https://frontend-ponc.onrender.com}

### password authentication failed for user "setupbuilder"

Cause (local): the Docker Postgres volume has a stale password (the
compose file's password was changed after the volume was created).
Fix:

    cd SetupBuilder
    docker compose down -v
    docker compose up -d

Cause (prod): wrong password in Render env var.
Fix: Rotate the Postgres password, update DB_PASSWORD, redeploy.

### Driver org.postgresql.Driver claims to not accept jdbcUrl, postgresql://...

Cause: the DATABASE_URL is missing the jdbc: prefix or embeds
credentials in the URL.
Fix: Use the format jdbc:postgresql://HOST:5432/DBNAME and provide
credentials via DB_USERNAME / DB_PASSWORD.

### firebase-service-account.json cannot be opened

Cause: the file isn't on the classpath.
Fix:
- Local: place it at src/main/resources/firebase-service-account.json
- Render: set the FIREBASE_SERVICE_ACCOUNT_BASE64 env var

### FIREBASE_SERVICE_ACCOUNT_BASE64 is not valid base64

Cause: extra whitespace or line breaks in the env var value.
Fix: Re-run the base64 command and paste the full string without editing.

### Connection to localhost:5332 refused

Cause: Docker Postgres isn't running, or the port is wrong.
Fix:

    docker compose ps
    docker compose up -d

Confirm application-local.properties says localhost:5332.

## Runtime Errors

### User is not admin after signing in

1. Verify the email is in ADMIN_EMAILS on Render
2. Log out, log back in - a fresh request re-triggers the promotion logic
3. Or update directly in DBeaver:

    UPDATE users SET role = 'ADMIN' WHERE email = 'your@email.com';

### CORS error in browser console

Cause: the frontend origin isn't in CORS_ALLOWED_ORIGINS.
Fix: Add the frontend URL (no trailing slash, with https://) to the
env var, then redeploy the backend.

### Avatars / banners show as broken images

Cause (production): file uploads go to uploads/ on the container's
local disk, which is ephemeral. Redeploys wipe them.
Fix (long-term): migrate FileStorageService to Cloudflare R2 or S3.

### Gemini returns 503 / 429 / timeout

Cause: transient Google-side load or quota.
Mitigation already in place: AiPriceService and GeminiChatService retry
on 429/500/502/503/504 and fall back to the next model in the chain. A 60s
budget caps the total time. If all models fail, the user sees a friendly
"AI is unavailable - try again" message.

### Scheduled price refresh is not running

1. Confirm pricing.refresh.enabled=true in properties (or env var)
2. Check the cron expression in pricing.refresh.cron
3. Look for "Running scheduled price refresh..." in the logs at the configured time
4. Trigger it manually via POST /api/admin/prices/refresh

## Build & IDE Issues

### Maven can't resolve dependencies

    ./mvnw.cmd -U clean install -DskipTests

If that fails, delete ~/.m2/repository/com/google/firebase/firebase-admin
and re-run.

### IntelliJ shows red underlines everywhere

File -> Invalidate Caches... -> Invalidate and Restart, then reload Maven in
the Maven tool window.

### IntelliJ doesn't pick up an env var I just set

Fully close IntelliJ (File -> Exit) and reopen. IntelliJ reads env vars at
startup only.
