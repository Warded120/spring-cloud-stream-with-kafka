# Spring Cloud Stream with Kafka
This document outlines the required tasks for understanding and implementing caching, api calling and Proxy pattern.

🧪 To Do:

- Item enrichment
- Item API Client
- Item Cache

✅ Acceptance Criteria:

- Enrich item details with calling external API with resource URL /users/{id} where id is Item id from payload
- API call is implemented in declarative approach with Spring Feign library.
- External service is not set up. Everything is configured in integration tests with Spring WireMock library.
- Item is cached for one day with Caffeine library
- Caching is implemented via Proxy (Dynamic Proxy) pattern

#### Enriched input item

- price (BigDecimal) -> Cannot be null and should be positive.
- producer (String) -> Cannot be blank
- description (String) -> Cannot be blank
- VATRate (BigDecimal) -> Cannot be null and should be be between 0.0 anf 100.00.
- UOM (String)
- BarCode (String) -> Cannot be null, can contain only 14 digits

#### output item

- id
- account
- beginDateTime
- endDateTime
- price (BigDecimal) -> Cannot be null and should be positive.
- producer (String) -> Cannot be blank
- description (String) -> Cannot be blank
- VATRate (BigDecimal) -> Cannot be null and should be be between 0.0 anf 100.00.
- UOM (String)
- BarCode (String) -> Cannot be null, can contain only 14 digits