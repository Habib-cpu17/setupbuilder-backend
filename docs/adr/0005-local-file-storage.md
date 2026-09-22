# ADR 0005 - Local Disk Storage for User Uploads

- Status: Accepted (with documented limitation)
- Date: 2025-11-08
- Deciders: Backend team

## Context

Users need to upload two types of images:

- Profile avatars (small, ~1 image per user)
- Profile banners (larger, ~1 image per user)

Options considered:

1. Local disk storage with Spring serving files via /uploads/**
2. Cloud object storage (AWS S3, Cloudflare R2, Firebase Storage)
3. Database storage (BLOB columns)

## Decision

Use local disk storage in uploads/avatars/ and uploads/banners/.
Files are named with UUIDs and served via WebConfig's resource handler at
/uploads/**.

Limits enforced: avatars <= 2 MB, banners <= 5 MB. Allowed MIME types: JPEG,
PNG, WEBP, GIF.

## Consequences

Positive:
- Zero cost, zero setup for local development
- No external dependency to configure or pay for
- Straightforward implementation (~150 lines of code)
- Old files automatically deleted when replaced (via FileStorageService.deleteByUrl)

Negative:
- Files are ephemeral on Render - the container's disk is wiped on every
  redeploy, so user avatars and banners disappear
- Not horizontally scalable - multiple backend instances would each see only
  their own files
- No CDN - every image request hits the backend

## Mitigation

The limitation is accepted for the current phase. The proposed migration path:
swap FileStorageService.save() to upload to Cloudflare R2 (S3-compatible,
10 GB free) and change WebConfig to serve the R2 public URL. No caller-side
changes are required.

## Alternatives Considered

- Cloud storage from day one - correct long-term answer but adds setup
  cost and credentials to manage during the initial build
- Database BLOBs - bloats the DB, worse performance, harder backups
