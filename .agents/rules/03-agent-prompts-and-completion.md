## 20. Suggested Agent Prompts

These prompts can be given to coding agents.

### Prompt: scaffold API
Create a minimal but production-structured Spring Boot 3 Java 24 API in the `api/` directory with `GET /health`, `POST /tickets`, request validation, clean package structure, Maven build, and starter tests. Do not add database support. Keep the design ready for Azure Queue Storage integration.

### Prompt: add queue integration
Update the Spring Boot API to integrate with Azure Queue Storage using environment variables for storage connection string and queue name. Implement a service that sends a JSON message containing ticketId, customer, issue, priority, status, and createdAt.

### Prompt: add table lookup
Add `GET /tickets/{id}` to the Spring Boot API. Use Azure Table Storage to read an entity from table `ticketresults` with PartitionKey `tickets` and RowKey equal to the ticket ID. Return 404 if not found.

### Prompt: add Docker support
Create a production-appropriate Dockerfile and `.dockerignore` for the Spring Boot API. Make sure the container runs on port 8080 and document the local Docker commands.

### Prompt: generate Java Azure Function
Create a Java Azure Function Maven project in `function/` with a queue trigger on queue `tickets`. Parse the JSON message, generate a simple summary, and write an entity to Azure Table Storage.

### Prompt: generate Bicep
Create modular Bicep files under `infra/` for storage, monitoring, container registry, container apps environment, container app, function app, and key vault. Keep it readable and parameterized for a beginner.

### Prompt: generate GitHub Actions
Create GitHub Actions workflow files for deploying infrastructure and deploying the API to Azure Container Apps via Azure Container Registry. Include comments showing which GitHub secrets the human must set.

### Prompt: generate README
Write a recruiter-quality README for this project that explains the architecture, problem statement, Azure services used, local setup, deployment flow, observability, security decisions, and cleanup instructions.

---
## 21. What Not to Change Without Approval

AI agents must not change these decisions without explicit approval:
- Java remains the main language
- Spring Boot remains the API framework
- Azure Container Apps remains the hosting target for the API
- Azure Queue Storage remains the async messaging layer
- Azure Table Storage remains the result store for version 1
- Bicep remains the IaC language
- GitHub Actions remains the CI/CD tool

---
## 22. Stretch Goals Only After Core Completion

Only attempt these after the project is complete:
- Key Vault integration for secrets at runtime
- managed identity
- more advanced retry/dead-letter explanation
- Swagger/OpenAPI polish
- custom domain
- auth layer
- multiple environments: dev/test/prod
- richer logging correlation

These are optional. Do not delay the internship application for them.

---
## 23. Final Project Pitch for Resume / Interview Use

This project can be described as:

> Built a cloud-native ticket ingestion platform on Microsoft Azure using Java, Spring Boot, Azure Container Apps, Azure Functions, Azure Queue Storage, Azure Table Storage, Bicep, and GitHub Actions. Implemented asynchronous processing, Infrastructure as Code, monitoring, and deployment automation to simulate a production-style cloud integration workflow.

---
## 24. Completion Instruction for AI Agents

When helping with this repo, always optimize for:
1. fast completion
2. clarity for a beginner Azure Portal user
3. recruiter relevance
4. strong documentation
5. production-minded but not overengineered choices

The best result is not the most complex architecture.
The best result is a clean, working, well-documented Azure project that strongly matches the Nebius internship.
