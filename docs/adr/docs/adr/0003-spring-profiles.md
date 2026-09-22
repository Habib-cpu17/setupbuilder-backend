# ADR 0003 - Use Spring Profiles for Environment Separation

- Status: Accepted
- Date: 2025-10-25
- Deciders: Backend team

## Context

The backend runs in two environments:

1. Local developer laptops (Docker Postgres on port 5332)
2. Render (managed Postgres, container environment)

Credentials, database URLs, and CORS origins differ between them. Hardcoding
values in application.properties caused three problems:

- Secrets committed to git
- Local config breaking production and vice versa
- Manual edits required every time the team deployed

## Decision

Adopt Spring profiles with three configuration files:

- application.properties - committed, no secrets, uses ${VAR:default} for
  every environment-specific value
- application-local.properties - gitignored, holds developer secrets and
  local DB connection details
- application-prod.properties - committed, contains no secrets, reads
  everything from environment variables

The local profile is activated in IntelliJ's run configuration (or via
-Dspring-boot.run.profiles=local). On Render, SPRING_PROFILES_ACTIVE=prod
is set as an environment variable.

## Consequences

Positive:
- No secrets in git
- Adding a new environment (e.g., a staging stack) is a matter of adding one
  profile file
- application.properties documents every configurable value at a glance
- Env-var driven config works identically on Render, Railway, Fly.io, etc.

Negative:
- New developers must create application-local.properties before running
  locally - documented in README.md
- Spring profile precedence can be confusing - mitigated by keeping only two
  profiles

## Alternatives Considered

- Single application.properties with all values - impossible without
  committing secrets
- Environment variables only (no profile files) - verbose and error-prone;
  devs would need to export 8+ variables per terminal session
- .env files with Spring Boot DevTools - not native to Spring, fragile
