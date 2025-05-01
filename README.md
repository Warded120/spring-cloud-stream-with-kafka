# Integration testing
There are tasks to learn multi-layered architecture.

## Acceptance Criteria

 - Read about multi-layered architecture
 - Create diagrams with potential layers for pipeline and for web application. 
 - Add validation from jakarta.validation.* package
 - Add conversion/mapping with MapStruct library.

### Structure models

#### Input

Transaction

- discount (String) -> A number is up to 100.00. Precision after a point is always 2. Can be null.
- sequenceNumber (Long) -> Cannot be blank
- endDateTime (String) -> Cannot be blank
- items (List<Item>) -> Cannot be empty
- total (Total) - Cannot be null

Item

- id (Long) -> Cannot be null
- loyaltyAccountId (CharSequence)
- beginDateTime (String) -> Cannot be blank
- endDateTime (String) -> Cannot be blank

Total

- amount (BigDecimal) -> Cannot be null
- currency (String) -> Cannot be blank. Should be only (USD, GBP, EUR, CNY, UAH).


### Output

Transaction

- transactionId (UUID) -> randomly generated UUID
- source (String) -> 'Softserve' constant always
- discount (String) -> to be ignored
- sequenceNumber (Long)
- operationDateTime (Instant) -> mapped from endDateTime field
- items (List<Item>)
- total (Total)

Item

- id (Long)
- account (String) -> if account Id 1 -> Main, 2 -> Coupon, 3 -> Base, 4 -> Total. For other cases -> 'Unknown'
- beginDateTime (String)
- endDateTime (String)

Total

- amount (BigDecimal)
- currency (Enum)

