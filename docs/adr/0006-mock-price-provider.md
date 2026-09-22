# ADR 0006 - Ship with Mock Price Provider Active

- Status: Accepted
- Date: 2025-11-01
- Deciders: Backend + product team

## Context

SetupBuilder's long-term value includes real prices from Saudi retailers
(Amazon.sa, Noon, Jarir, Extra, Falcon Games). Integrating real prices requires
either:

1. Building and maintaining scrapers for each retailer (brittle, ongoing
   maintenance)
2. Negotiating API access with each retailer (business + legal work)
3. Paying for a third-party aggregator (Apify, Parse.bot, etc.)

None of these can be completed within the course timeline. The project still
needs a working price column so builds display totals, filters work, and the AI
estimator has a baseline to compare against.

## Decision

Ship with MockPriceProvider active. The provider returns a slight random
variation (+/-5%) around each component's fallbackPrice (which is the
admin-managed seed price stored in the DB). It runs via the scheduled
PriceRefreshJob or on demand from the admin dashboard.

Real retailer integration is stubbed in ApifyPriceProvider but not
configured. Switching providers is a one-line config change:

    pricing.provider=apify
    pricing.apify.token=...
    pricing.apify.actor-id=...

## Consequences

Positive:
- Prices display in SAR without any external dependency
- Admins can hand-tune any price via the dashboard
- The AI estimator still works - it compares the mock total against a Gemini
  market estimate and reports the difference
- Price history + alerts + scheduled refresh all work end-to-end against the
  mock provider
- Zero cost, zero external setup

Negative:
- Users see simulated prices, not real retailer prices
- The "price updated X hours ago" indicator reflects mock refreshes
- Real integrations must be added before public launch

## Alternatives Considered

- Block the feature until Apify is integrated - blocks the entire catalog
  from showing prices, unacceptable
- Static seed prices only - no scheduled updates, no price history, no
  alerts possible
