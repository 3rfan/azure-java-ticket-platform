# Architecture

The system is designed as a cloud-native asynchronous processing platform on Azure using Java.

## Request Flow
1. **API Ingestion**: A standard Spring Boot API receives requests (`POST /tickets`).
2. **Queue Push**: Instead of processing synchronously, the API acts as a producer and pushes a `QueueTicketMessage` JSON payload to an Azure Queue Storage (`tickets` queue).
3. **Immediate Acknowledgement**: The API responds with an `HTTP 202 Accepted` along with the assigned ticket UUID and `queued` status.

## Async Processing Flow
4. **Queue Trigger**: An Azure Function written in Java triggers automatically when a new message enters the Queue.
5. **Logic**: The Function evaluates the payload to create a deterministic outcome (such as `Processed ticket for <customer>`).
6. **Save Result**: The Function outputs the computed result to an Azure Table Storage (`ticketresults` table), indexed by partition and row key.

## Lookup Flow
7. **Retrieve Result**: The user can check tracking status via `GET /tickets/{id}`.
8. **Table Fetch**: The API looks into the Azure Table Storage using the requested UUID as the row key. 
9. **Return**: The API returns the computed `summary` and `processed` status, or a `404 Not Found` if it is still being processed or invalid.

## Design Decisions
- **Why Queues?** Decouples ingestion from processing to seamlessly handle traffic spikes without dropping requests.
- **Why Table Storage?** Offers extremely fast Key/Value NoSQL lookups perfect for simple tracking items without the overhead of relational DBs or CosmosDB initially.
- **Why Container Apps?** Fully managed Serverless microservices environment removing K8s management overhead (AKS).
