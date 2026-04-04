## 8. AI Agent Build Order

AI agents should follow this exact order unless there is a strong reason not to.

### Phase 1: scaffold repo and API
Tasks:
1. Create `api/pom.xml`
2. Create Spring Boot application entry point
3. Create `HealthController`
4. Create ticket request model
5. Create `POST /tickets`
6. Return generated UUID and status
7. Add validation and simple error handling
8. Add minimal tests

Acceptance criteria:
- `mvn spring-boot:run` works
- `/health` returns success
- `POST /tickets` returns a JSON response with `ticketId`

### Phase 2: Azure Queue integration
Tasks:
1. Add Azure Queue Storage SDK usage
2. Add configuration binding for storage connection string and queue name
3. Implement queue message sender service
4. Serialize message JSON cleanly
5. Update controller to send real message

Acceptance criteria:
- `POST /tickets` creates a real message in Azure Queue Storage
- failure paths are handled reasonably

### Phase 3: Table Storage lookup support
Tasks:
1. Add Azure Data Tables SDK usage
2. Implement table lookup service
3. Implement `GET /tickets/{id}` endpoint
4. Return 404 when record not found

Acceptance criteria:
- endpoint can read processed result from `ticketresults`

### Phase 4: containerization
Tasks:
1. Create API Dockerfile
2. Create `.dockerignore`
3. Ensure app binds to correct port
4. Add run instructions to README

Acceptance criteria:
- Docker image builds successfully
- local container works

### Phase 5: Java Azure Function
Tasks:
1. Create Azure Functions Java Maven project in `function/`
2. Define queue-triggered function on queue `tickets`
3. Parse incoming JSON
4. Simulate simple processing
5. Write entity to Azure Table Storage
6. Add local settings example

Acceptance criteria:
- function compiles
- function logic is clear and documented
- result schema matches API expectations

### Phase 6: documentation and screenshots
Tasks:
1. Create architecture diagram source or markdown explanation
2. Draft `docs/architecture.md`
3. Draft troubleshooting guide
4. Draft README sections

Acceptance criteria:
- repo explains what is happening at each stage

### Phase 7: Bicep
Tasks:
1. Create modular Bicep files
2. Start with storage, monitoring, ACR
3. Add Container App resources
4. Add Function App resources
5. Add parameters file
6. Add tags for resources

Acceptance criteria:
- Bicep structure is understandable
- even if not applied yet, templates reflect the portal setup

### Phase 8: GitHub Actions
Tasks:
1. Create `deploy-api.yml`
2. Create `deploy-infra.yml`
3. Use branch trigger on `main`
4. Build app, build image, push to ACR, deploy to Container App
5. Document required GitHub secrets

Acceptance criteria:
- workflow files are ready for manual secret insertion

---
## 9. Data Model

### Ticket request payload
```json
{
  "customer": "Acme BV",
  "issue": "VPN unstable",
  "priority": "high"
}
```

### Queue message payload
```json
{
  "ticketId": "uuid-here",
  "customer": "Acme BV",
  "issue": "VPN unstable",
  "priority": "high",
  "status": "submitted",
  "createdAt": "2026-04-01T12:00:00Z"
}
```

### Table Storage entity shape
- PartitionKey: `tickets`
- RowKey: `ticketId`
- status: `processed`
- summary: `Processed ticket for Acme BV: VPN unstable`
- priority: original priority
- processedAt: timestamp

### API response from `POST /tickets`
```json
{
  "ticketId": "uuid-here",
  "status": "queued"
}
```

### API response from `GET /tickets/{id}`
```json
{
  "ticketId": "uuid-here",
  "status": "processed",
  "summary": "Processed ticket for Acme BV: VPN unstable"
}
```

---
## 10. API Implementation Guidance

### Suggested package structure for API
```text
com.example.ticketapi
├─ config
├─ controller
├─ exception
├─ model
├─ service
└─ TicketApiApplication
```

### Recommended classes
- `TicketApiApplication`
- `TicketController`
- `HealthController`
- `TicketRequest`
- `TicketSubmissionResponse`
- `TicketStatusResponse`
- `QueueTicketMessage`
- `QueueStorageService`
- `TableStorageService`
- `AzureStorageProperties`
- `GlobalExceptionHandler`

### Controller behavior
#### `GET /health`
Return simple healthy response.

#### `POST /tickets`
Responsibilities:
- validate request body
- generate UUID
- map request into queue message
- send queue message to Azure Queue Storage
- return JSON with `ticketId` and `queued`

#### `GET /tickets/{id}`
Responsibilities:
- look up ticket result in Table Storage
- return `processed` result if found
- return 404 if not found

### Validation rules
- `customer` required
- `issue` required
- `priority` required
- `priority` may optionally be limited to `low`, `medium`, `high`

### Error handling
Include clear error responses for:
- invalid payload
- Azure queue submission failure
- missing ticket status

---
## 11. Function Implementation Guidance

### Suggested package structure for Function
```text
com.example.ticketprocessor
├─ model
├─ service
└─ TicketQueueFunction
```

### Function behavior
When triggered by a queue message:
1. parse JSON
2. extract ticket details
3. create a simple summary string
4. store result in Azure Table Storage
5. log success

### Simplified processing logic
Processing does not need to be smart.
Keep it simple and deterministic.

Example summary rule:
- If priority is high: `High-priority ticket received for <customer>: <issue>`
- Else: `Processed ticket for <customer>: <issue>`

### Result write behavior
Write one entity into `ticketresults` with:
- PartitionKey = `tickets`
- RowKey = `ticketId`
- status = `processed`
- summary = generated summary
- processedAt = current timestamp

---
## 12. Bicep Guidance

### Goal of Bicep in this project
The human will likely create resources manually in the portal first.
AI agents should then produce Bicep that mirrors the portal setup so the project demonstrates Infrastructure as Code.

### Minimum resources to define in Bicep
- Resource Group scope deployment
- Storage Account
- Queue and Table child resources where supported
- Log Analytics Workspace
- Application Insights
- Azure Container Registry
- Container Apps Environment
- Container App
- Function App
- Key Vault

### Requirements for Bicep files
- modular design
- parameters for environment name, region, and tags
- clear naming convention
- comments explaining what each module does

### Tags to include
- `env`
- `owner`
- `project`
- `costCenter` optional

### Important rule
Do not block the project if Bicep is imperfect.
The working portal deployment comes first.
Bicep is the reproducibility layer added after the system works.

---
## 13. GitHub Actions Guidance

### Workflow 1: deploy API
Purpose:
- build Spring Boot app
- build Docker image
- push image to Azure Container Registry
- deploy to Azure Container App

### Workflow 2: deploy infra
Purpose:
- run Bicep deployment to Azure

### Required GitHub secrets to document
- `AZURE_CREDENTIALS`
- `AZURE_SUBSCRIPTION_ID`
- `AZURE_RESOURCE_GROUP`
- `AZURE_CONTAINER_REGISTRY`
- any app-specific config values if needed

### Workflow expectations
- trigger on push to `main`
- use unique image tags
- include comments in YAML for readability

---
## 14. README Requirements

The `README.md` must include the following sections:

1. Project overview
2. Problem statement
3. Why this project is relevant for cloud engineering
4. Architecture diagram
5. Azure services used
6. Local development setup
7. Environment variables
8. Docker build/run
9. Azure deployment steps
10. Function processing flow
11. Bicep overview
12. GitHub Actions overview
13. Monitoring and logging
14. Security decisions
15. Cost and cleanup
16. Screenshots
17. Future improvements

### Tone of README
- concise
- professional
- cloud-engineering focused
- no fluff

---
## 15. Documentation Requirements

### `docs/architecture.md`
Must explain:
- components
- request flow
- async processing flow
- why queue-based design was chosen

### `docs/troubleshooting.md`
Must include:
- Container App not starting
- queue message not appearing
- function not triggering
- table entity not written
- wrong environment variables
- ACR image pull issues
- GitHub Actions deployment failures

### Screenshots to collect
The human should collect screenshots for:
- Resource Group overview
- Storage Queue
- Table Storage
- Container App overview
- Function App overview
- Application Insights or logs
- successful GitHub Actions run

---
## 16. Security and Cost Guidance

### Security for version 1
Good enough for version 1:
- use HTTPS
- do not hardcode secrets in source code
- use environment variables or Key Vault
- document that managed identity is a future improvement

### Cost awareness requirements
The repo must include:
- cleanup script
- note that student subscriptions should avoid leaving resources running unnecessarily
- recommendation to delete the resource group when done testing

### Cleanup scripts
Provide:
- `scripts/cleanup.ps1`
- `scripts/cleanup.sh`

These should delete the demo resource group using Azure CLI.

---
## 17. Testing Guidance

### Minimum tests for API
- health endpoint test
- POST ticket request validation test
- service test for queue submission abstraction
- GET ticket status not found test

### Manual end-to-end test flow
The human should be able to do this:
1. open API `/health`
2. POST a test ticket
3. confirm message appears in Queue Storage
4. wait for Function processing
5. confirm entity appears in Table Storage
6. GET `/tickets/{id}` and see processed response

### Test payloads
Use at least:
- one `high` priority ticket
- one `low` priority ticket
- one invalid payload

---
## 18. Quality Bar for “Done”

The project is “done enough” for internship applications when all of the following are true:

1. GitHub repo is public and clean
2. README explains the project clearly
3. Spring Boot API works in Azure Container Apps
4. Java Function processes queue messages
5. ticket result is written to Table Storage
6. `GET /tickets/{id}` returns result
7. Dockerfile exists and works
8. Bicep files exist and are reasonable
9. GitHub Actions workflow files exist
10. monitoring screenshots are present
11. cleanup script exists
12. repo looks intentional and polished

---
## 19. Suggested Commit Sequence

Use a clean commit history similar to:
1. `chore: initialize repo structure`
2. `feat: add spring boot health endpoint and ticket api skeleton`
3. `feat: integrate azure queue storage for ticket submission`
4. `feat: add table storage lookup endpoint`
5. `feat: containerize spring boot api`
6. `feat: add java azure function for queue processing`
7. `docs: add architecture and troubleshooting guides`
8. `infra: add bicep templates for azure resources`
9. `ci: add github actions deployment workflows`
10. `docs: finalize readme screenshots and cleanup instructions`

---
