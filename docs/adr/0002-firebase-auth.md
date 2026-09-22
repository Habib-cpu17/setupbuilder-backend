# ADR 0002 - Delegate Authentication to Firebase

- Status: Accepted
- Date: 2025-09-15
- Deciders: Backend + frontend teams

## Context

The platform needs email/password authentication with secure token handling.
Building it from scratch means implementing:

- Password hashing and salting (e.g., BCrypt)
- Email verification flows
- Password reset flows
- Session or JWT issuance and rotation
- Secure token storage on the client

Each of these has real security pitfalls. The team has no dedicated security
engineer, and the timeline is tight.

## Decision

Use Firebase Authentication for sign-up, login, and password reset.
The frontend uses the Firebase JS SDK; the backend verifies Firebase ID tokens
via the Firebase Admin SDK.

## Consequences

Positive:
- No password storage on our side - Firebase handles hashing and storage
- Email verification and password reset flows come for free
- Managed JWT lifecycle (issuance, refresh, revocation)
- Standard, well-documented SDKs on both ends
- Free tier covers expected traffic

Negative:
- Vendor lock-in to Firebase for auth
- Requires service account credentials on the backend
- Frontend bundle grows by the Firebase SDK size
- Requires whitelisting each frontend origin in the Firebase Console

## Implementation Notes

- Backend filter: FirebaseAuthenticationFilter runs before Spring Security
- On each request, the filter reads the Authorization: Bearer <token> header,
  verifies it, and upserts the local users row on first sight
- The local DB stores firebase_uid (indexed, unique) to link Firebase
  identities to application-level users

## Alternatives Considered

- Spring Security + JWT from scratch - too much surface area for the
  timeline, high risk of security bugs
- Auth0 - more features than needed; free tier is smaller
- Supabase Auth - viable, but the project already committed to Firebase
  for the frontend; changing both ends mid-project would cost time
