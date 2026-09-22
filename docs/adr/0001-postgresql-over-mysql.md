# ADR 0001 - Use PostgreSQL Everywhere Instead of MySQL

- Status: Accepted
- Date: 2025-10-28
- Deciders: Backend team

## Context

The project initially used MySQL for local development because the team was
already familiar with it. When deployment to Render began, the platform offered
free managed PostgreSQL but not free managed MySQL. This created two
environments with different database engines, which caused:

- Different SQL dialect issues (e.g., AUTO_INCREMENT vs SERIAL)
- Different JDBC driver dependencies and connection URL formats
- Behavioral differences in Hibernate's schema generation
- A growing maintenance burden to test against both engines

## Decision

Standardize on PostgreSQL everywhere - local development via Docker
Compose and production via Render Postgres.

## Consequences

Positive:
- Identical schema, queries, and behavior across environments
- One JDBC driver, one Hibernate dialect, one set of SQL to maintain
- Easy to migrate to other Postgres providers (Supabase, Neon) later
- Local Postgres is provided free via Docker

Negative:
- Required a one-time migration: removing the MySQL dependency, replacing the
  dialect, and reseeding the local database
- Contributors must have Docker Desktop installed

## Alternatives Considered

- MySQL everywhere - Render doesn't offer free managed MySQL; would
  require paying for a third-party MySQL host.
- H2 in-memory for local, Postgres in prod - behavioral drift between
  environments is unacceptable for a project using JSON, sequences, and
  dialect-specific features.
