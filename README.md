# Payout Optimizer

Spring Boot service that selects the combination of payout requests with the highest total agent commission without exceeding the available payout float.

## Tech Stack

- Java 21+
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Flyway
- Maven
- Docker Compose

## Run

Start PostgreSQL:

```bash
docker compose up
```

In another terminal:

```bash
./mvnw spring-boot:run
```

Run tests:

```bash
./mvnw test
```

Build the JAR:

```bash
./mvnw clean package
```

Run the built JAR:

```bash
java -jar target/*.jar
```

## Database

The application connects to PostgreSQL at:

`localhost:5432/payout_optimizer`

The database is created through Docker Compose and the schema is managed by Flyway.

`payout_requests.batch_id` is a foreign key to `payout_batches.id`. Every candidate request is persisted with a `selected` flag so the full optimization decision can be audited later.

Main tables:

- `payout_batches` — stores each optimization run and its totals.
- `payout_requests` — stores all candidate requests for a batch and whether each one was selected.

Indexes:

- `payout_batches(created_at)` — supports newest-first audit queries.
- `payout_requests(batch_id)` — supports loading requests belonging to a batch.

## API

### Optimize payout batch

```bash
curl -X POST http://localhost:8080/api/v1/payout-batches/optimize \
  -H "Content-Type: application/json" \
  -d '{
    "availablePayoutFloat": 12000,
    "payoutRequests": [
      {"requestReference":"PO-3001","payoutAmount":4000,"agentCommission":90},
      {"requestReference":"PO-3002","payoutAmount":6000,"agentCommission":150},
      {"requestReference":"PO-3003","payoutAmount":2500,"agentCommission":55},
      {"requestReference":"PO-3004","payoutAmount":5000,"agentCommission":115}
    ]
  }'
```

Example response:

```json
{
  "batchId": "435f3a9a-ebe3-49f4-aa86-00482a8d1213",
  "selectedPayouts": [
    {
      "requestReference": "PO-3002",
      "payoutAmount": 6000,
      "agentCommission": 150
    },
    {
      "requestReference": "PO-3004",
      "payoutAmount": 5000,
      "agentCommission": 115
    }
  ],
  "totalFloatConsumed": 11000,
  "totalAgentCommission": 265,
  "createdAt": "2026-09-12T11:20:48Z"
}
```

If no payout request fits within the available float, the endpoint returns HTTP 200 with an empty `selectedPayouts` list and zero commission.

### Get batch by ID

```bash
curl http://localhost:8080/api/v1/payout-batches/{batchId}
```

Example response:

```json
{
  "batchId": "435f3a9a-ebe3-49f4-aa86-00482a8d1213",
  "selectedPayouts": [
    {
      "requestReference": "PO-3002",
      "payoutAmount": 6000.00,
      "agentCommission": 150.00
    }
  ],
  "totalFloatConsumed": 6000.00,
  "totalAgentCommission": 150.00,
  "createdAt": "2026-09-12T11:20:48Z"
}
```

### List batches

```bash
curl "http://localhost:8080/api/v1/payout-batches?page=0&size=10"
```

Example response:

```json
{
  "content": [
    {
      "batchId": "435f3a9a-ebe3-49f4-aa86-00482a8d1213",
      "selectedPayouts": [],
      "totalFloatConsumed": 0,
      "totalAgentCommission": 0,
      "createdAt": "2026-09-12T11:20:48Z"
    }
  ],
  "number": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

Results are paginated and ordered newest first.
