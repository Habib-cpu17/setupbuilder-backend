# ADR 0004 - Multi-Model Gemini Fallback with Retries

- Status: Accepted
- Date: 2025-11-01
- Deciders: Backend team

## Context

The AI features (price estimator + chat assistant) depend on the Google Gemini
API. In practice, the free tier frequently returns:

- 503 Service Unavailable during peak load
- 429 Too Many Requests when quota is exhausted
- 504 Gateway Timeout on slow paths

A single-model, single-attempt implementation would surface these to the user
as an error. Worse, if the model name becomes invalid (as happened when older
model versions stopped being provisioned), the whole feature breaks silently.

## Decision

Implement a multi-model fallback chain with retries and a global time budget:

1. Models come from gemini.api.models (comma-separated):
   gemini-flash-latest,gemini-flash-lite-latest
2. For each model, retry up to gemini.api.retries times (default 2)
3. Retry on: 429, 500, 502, 503, 504, HttpTimeoutException, ConnectException
4. Exponential backoff between retries (400ms x attempt)
5. After all retries fail for a model, move to the next model
6. A global 60-second budget caps the entire attempt - the user never waits
   longer than a minute
7. If all models fail, return a graceful "unavailable" response

Both AiPriceService and GeminiChatService implement this pattern.

## Consequences

Positive:
- Feature keeps working when a single model is temporarily overloaded
- Adding or removing models is a config change - no redeploy needed to swap
- Users never see a raw 503; they see a friendly fallback message
- Model deprecation is handled by keeping -latest aliases in the chain

Negative:
- Adds latency in the failure case (up to ~60s before giving up)
- Slightly more complex code than a single-call implementation

## Alternatives Considered

- Single model with retries - brittle; a single model outage kills the
  feature
- Retry indefinitely - would leave users stuck on a spinner
- Client-side fallback - pushes complexity to the frontend; wrong layer
