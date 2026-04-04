## Project: Azure Java Ticket Platform for Nebius Cloud Engineer Internship

This document is the single source of truth for AI agents helping build this project.

The goal is to produce a recruiter-ready GitHub portfolio project that strongly matches the Nebius Cloud Engineer Early Talent internship in Amsterdam.

The project must demonstrate:
- Microsoft Azure usage
- cloud-native design
- API-based integration
- Infrastructure as Code
- CI/CD
- containers
- Azure storage services
- monitoring/logging
- cost awareness
- documentation quality

The human owner of this repo will perform Azure Portal operations manually.
AI agents should handle code generation, repo structure, docs, IaC, CI/CD, tests, and implementation guidance.

---
## 1. Project Summary

### What the project is
A cloud-native ticket ingestion platform built on Azure using Java.

A Spring Boot API accepts incoming ticket requests. Instead of processing the request synchronously, it pushes a message to Azure Queue Storage and immediately returns a ticket ID. A separate Azure Function reads the queue, processes the message asynchronously, and stores the result in Azure Table Storage. The API can later fetch the status/result by ticket ID.

### What problem it solves
This project demonstrates a real cloud integration pattern:
- accept incoming requests quickly
- decouple processing using a queue
- process work asynchronously
- expose status later through an API
- deploy repeatably using IaC
- observe system behavior through monitoring

This is intentionally designed to look like a junior cloud engineering project rather than a generic CRUD web app.

### Why this project fits the internship
This project maps well to the role because it demonstrates:
- Azure compute
- storage services
- API-based integration
- Infrastructure as Code with Bicep
- GitHub Actions CI/CD
- containerization
- basic security and secrets handling
- Azure monitoring/logging
- cleanup and cost awareness
- practical documentation

---
## 2. Ground Rules for AI Agents

### Core execution rules
1. Prioritize completion over perfection.
2. Keep the architecture simple enough for a beginner in Azure Portal to follow.
3. Use Java as the primary programming language.
4. Use Spring Boot for the API.
5. Use Azure Functions for asynchronous processing.
6. Use Azure Queue Storage and Azure Table Storage.
7. Use Azure Container Apps for hosting the API.
8. Use Bicep for Infrastructure as Code.
9. Use GitHub Actions for deployment automation.
10. Avoid adding unnecessary complexity such as AKS, private endpoints, Cosmos DB, Terraform, or frontend dashboards unless explicitly requested later.

### Human vs AI responsibilities
The human owner will:
- create Azure resources in the Azure Portal
- copy values from Azure Portal when needed
- approve GitHub pushes and Azure changes
- verify results in the Portal

AI agents should:
- generate code
- generate Dockerfile
- generate Maven config
- generate Azure Function code
- generate Bicep templates
- generate GitHub Actions workflows
- generate README and documentation
- generate testing instructions
- generate troubleshooting steps
- suggest exact Azure Portal operations for the human

### Non-goals
Do not:
- propose AKS as the main solution
- redesign the project into a frontend-heavy app
- introduce many microservices
- overcomplicate security for version 1
- require deep Azure CLI knowledge for the human

---
## 3. Final Architecture

### Components
1. **Spring Boot API**
   - endpoints:
     - `GET /health`
     - `POST /tickets`
     - `GET /tickets/{id}`
   - hosted in Azure Container Apps
   - writes messages to Azure Queue Storage
   - reads processed results from Azure Table Storage

2. **Azure Queue Storage**
   - queue name: `tickets`
   - stores incoming ticket messages

3. **Java Azure Function**
   - triggered by queue messages from `tickets`
   - processes the message asynchronously
   - writes results into Azure Table Storage

4. **Azure Table Storage**
   - table name: `ticketresults`
   - stores ticket status and summary

5. **Azure Container Registry**
   - stores the container image for the API

6. **Azure Container Apps Environment**
   - runtime environment for the Container App

7. **Azure Monitor / Log Analytics / Application Insights**
   - provides logs, telemetry, and monitoring

8. **Azure Key Vault**
   - stores secrets in later phases
   - version 1 may temporarily use app settings / env vars for speed

9. **Bicep Templates**
   - reproducibly define Azure resources

10. **GitHub Actions**
   - automate build and deployment

### Logical Flow
1. Client sends `POST /tickets`
2. Spring Boot API validates payload
3. API generates `ticketId`
4. API sends message to Azure Queue Storage
5. API returns `ticketId` and `queued` status
6. Azure Function is triggered by queue message
7. Function processes the message
8. Function writes result to Azure Table Storage
9. Client calls `GET /tickets/{id}`
10. API reads result from Table Storage and returns status

---
## 4. Tech Stack

### Required stack
- Java 24
- Spring Boot 3.x
- Maven
- Docker
- Azure Queue Storage SDK for Java
- Azure Data Tables SDK for Java
- Azure Functions Java
- Bicep
- GitHub Actions

### Recommended dependencies for API
- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `azure-storage-queue`
- `azure-data-tables`
- `jackson-databind`
- `lombok` optional
- `spring-boot-starter-test`

### Recommended dependencies for Function
- Azure Functions Java Maven plugin
- `azure-storage-queue`
- `azure-data-tables`
- Jackson or Gson

---
## 5. Repository Structure

The repo should end up in this form:

```text
azure-java-ticket-platform/
├─ api/
│  ├─ src/main/java/com/example/ticketapi/
│  │  ├─ controller/
│  │  ├─ service/
│  │  ├─ model/
│  │  ├─ config/
│  │  └─ TicketApiApplication.java
│  ├─ src/main/resources/
│  │  └─ application.yml
│  ├─ src/test/java/
│  ├─ Dockerfile
│  ├─ pom.xml
│  └─ .dockerignore
├─ function/
│  ├─ src/main/java/com/example/ticketprocessor/
│  │  ├─ TicketQueueFunction.java
│  │  ├─ model/
│  │  └─ service/
│  ├─ host.json
│  ├─ local.settings.json.example
│  └─ pom.xml
├─ infra/
│  ├─ main.bicep
│  ├─ main.parameters.json
│  └─ modules/
│     ├─ storage.bicep
│     ├─ monitoring.bicep
│     ├─ acr.bicep
│     ├─ containerapp.bicep
│     ├─ functionapp.bicep
│     └─ keyvault.bicep
├─ scripts/
│  ├─ cleanup.ps1
│  ├─ cleanup.sh
│  ├─ test-ticket.ps1
│  └─ test-ticket.sh
├─ docs/
│  ├─ architecture.md
│  ├─ architecture.png
│  ├─ screenshots/
│  └─ troubleshooting.md
├─ .github/
│  └─ workflows/
│     ├─ deploy-api.yml
│     └─ deploy-infra.yml
├─ .gitignore
└─ README.md
```

---
## 6. Project Milestones

### Milestone 1: local API skeleton
Deliverables:
- Spring Boot app starts locally
- `GET /health` works
- `POST /tickets` accepts payload and returns UUID
- Azure integration may still be mocked at this stage

### Milestone 2: real Azure queue integration
Deliverables:
- `POST /tickets` sends real message to Azure Queue Storage
- API reads config from env vars or application.yml
- manual portal-created storage account is used

### Milestone 3: containerized API in Azure Container Apps
Deliverables:
- Dockerfile builds successfully
- image pushed to Azure Container Registry
- Container App runs in Azure
- `/health` works publicly

### Milestone 4: queue-triggered Java Function
Deliverables:
- Function reads queue message
- Function writes result to Azure Table Storage
- end-to-end async flow works

### Milestone 5: full API lookup flow
Deliverables:
- `GET /tickets/{id}` reads processed result from Table Storage
- demo flow is complete

### Milestone 6: monitoring and documentation
Deliverables:
- logs visible in Azure
- screenshots captured
- architecture diagram added
- README drafted

### Milestone 7: Bicep and CI/CD
Deliverables:
- infra reproducible with Bicep
- GitHub Actions workflow builds and deploys API
- repo looks polished and internship-ready

---
## 7. Human-Owned Azure Portal Tasks

These are tasks the human must do in Azure Portal.
AI agents must not assume these have already been completed unless explicitly confirmed.

### 7.1 Create Resource Group
Create a resource group:
- suggested name: `rg-nebius-java-demo-dev`
- choose a nearby EU region

### 7.2 Create Storage Account
Create a storage account:
- Standard
- LRS
- put it in the resource group

Then inside the storage account create:
- Queue: `tickets`
- Table: `ticketresults`

### 7.3 Create monitoring resources
Create:
- Log Analytics Workspace
- Application Insights

### 7.4 Create Container Registry
Create Azure Container Registry:
- Basic tier is enough
- save the registry name for later

### 7.5 Create Container Apps Environment
Create a Container Apps Environment:
- attach Log Analytics Workspace

### 7.6 Create Container App
Create Azure Container App for the Spring Boot API:
- external ingress enabled
- target port should match app port
- connect image from Azure Container Registry

### 7.7 Create Function App
Create Java Function App:
- same resource group
- same storage account where appropriate
- enable monitoring if prompted

### 7.8 Create Key Vault
Create Key Vault for later phases.
Version 1 may not fully use it yet.

### 7.9 Save required values for AI coding help
The human should collect and provide when needed:
- storage account connection string
- queue name
- table name
- ACR login server
- Container App URL
- resource group name
- region
- subscription ID when needed

---
