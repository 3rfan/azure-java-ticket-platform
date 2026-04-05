# Azure Java Ticket Platform

## Project Overview
This represents an asynchronous cloud-native ticket ingestion platform built in Java 21 on Microsoft Azure.

Built around **Spring Boot**, **Azure Functions**, **Azure Queue Storage** and **Azure Container Apps**, this decouples frontend ingestion layers from background backend processing.

## Problem Statement
Standard CRUD applications process events synchronously. During extreme traffic spikes, direct database operations will crash the web server. This platform resolves this by using Queues.

## Architecture
See [docs/architecture.md](docs/architecture.md) for deeper information.
1. Client POSTs to `/tickets`.
2. Spring Boot app enqueues message to `Queue Storage` and returns immediate ACK.
3. Azure Function trigger processes logic in the background and commits a row to `Table Storage`.

## Setup & Deployment
### 1. Azure Services
Ensure the following are provisioned in the Portal:
- Azure Storage (Standard LRS) + Queue `tickets` and Table `ticketresults`
- Azure Container Registry
- Azure Container App Environment
- Application Insights / Log Analytics
- Azure Function App (Linux, Java 21)

### 2. Local Docker Build
Run Docker build inside `/api/`:
```bash
docker build -t ticketapi .
docker run -p 8080:8080 -e AZURE_STORAGE_CONNECTION_STRING="your_connection" ticketapi
```

### 3. Deployments (GitHub Actions Workflow)
Set the following secrets in GitHub Repository Settings:
- `AZURE_CREDENTIALS`: Output of Service Principal.
- `AZURE_SUBSCRIPTION_ID`, `AZURE_RESOURCE_GROUP`, `AZURE_CONTAINER_REGISTRY`

## Infrastructure as Code (Bicep)
Look into `infra/main.bicep` for reproducible resource allocations mapping this scenario programmatically using Bicep templates.

## Cleanup
Execute `scripts/cleanup.ps1` or `scripts/cleanup.sh` to purge the demo project resources from Azure