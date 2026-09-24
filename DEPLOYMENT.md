# Deployment - Backend on Render

## Prerequisites

- A Render account (free tier works)
- The repo is public or Render has access
- Firebase service account JSON ready locally
- A Gemini API key

## 1. Create the PostgreSQL Database

1. Render Dashboard -> New + -> PostgreSQL
2. Name: setupbuilder-db, Region: closest to your users, Tier: Free
3. After creation, copy the External Database URL (for DBeaver) and the
   Internal Database URL (for the Web Service)

## 2. Create the Web Service

1. Render -> New + -> Web Service
2. Connect the setupbuilder-backend GitHub repo
3. Configure:
   - Runtime: Docker
   - Root Directory: (leave blank - Dockerfile is at repo root)
   - Dockerfile Path: ./Dockerfile
4. Under Environment, add every variable from ENVIRONMENT.md (including
   APP_BASE_URL so component images resolve to the backend's public URL).
5. Click Create Web Service.

## 3. Base64-encode the Firebase Credentials

On your local machine, from the backend folder:

    [Convert]::ToBase64String([IO.File]::ReadAllBytes("src\main\resources\firebase-service-account.json")) | Set-Clipboard

The string is now on your clipboard. Paste it into the Render env var
FIREBASE_SERVICE_ACCOUNT_BASE64.

## 4. Verify the Deploy

After the build finishes (3-5 min), check the logs for:

    Started SetupbuilderBackendApplication in X.XXX seconds
    Tomcat started on port 10000 (http)

Test the public endpoint:

    curl.exe "https://setupbuilder-backend.onrender.com/api/components?category=CPU"

Expected: 200 with a JSON array.

## 5. Wipe and Re-seed the Production DB (Optional)

Connect with DBeaver using the External URL, then run:

    TRUNCATE TABLE
        chat_messages,
        chat_sessions,
        price_history,
        build_components,
        comments,
        builds,
        component_specs,
        components,
        users
    RESTART IDENTITY CASCADE;

Then trigger Manual Deploy in Render - the DataSeeder repopulates 200+
components on startup.

## Notes

- Cold starts. On the free tier, the service sleeps after 15 minutes of
  inactivity. First request after a nap takes 30-60 seconds to wake up. Use
  a free monitor (UptimeRobot) to ping /actuator/health every 5 minutes.
- Database expiry. Render Postgres free tier is deleted 30 days after
  creation. Migrate to Supabase or Neon for permanent persistence.
  Both are PostgreSQL-compatible - only the DATABASE_URL, DB_USERNAME,
  and DB_PASSWORD env vars need to change.
- File uploads. Stored on the container's local disk. Lost on redeploy.
  For persistence, migrate FileStorageService to Cloudflare R2 or AWS S3.

## Rolling Back

Render Dashboard -> Events tab -> click any previous deploy -> Redeploy.
