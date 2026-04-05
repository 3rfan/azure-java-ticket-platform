# Troubleshooting Guide

## Container App Not Starting
- Verify that your Dockerfile successfully builds locally.
- Validate that the Azure Container App `ingress.targetPort` matches the exposed port inside `Dockerfile` (8080).
- Check `Log stream` within the Azure Portal's Container App blade to catch startup crashes.

## Queue Message Not Appearing
- Ensure `AZURE_STORAGE_CONNECTION_STRING` is properly configured in the App configuration blade.
- Ensure the Azure App Service name of the queue you configured in `AZURE_STORAGE_QUEUE_NAME` perfectly matches the queue created in Azure Storage (case-sensitive).

## Azure Function Not Triggering
- Note down if the Function's Application Insights traces errors during bootstrapping.
- Make sure that `AzureWebJobsStorage` inside the Function Configuration has the perfect valid Storage Account connection string.
- If using `local.settings.json`, make sure `AzureWebJobsStorage` points to standard Dev storage while overriding `AzureStorageConnectionString` context.

## Table Entity Not Written
- Confirm `ticketresults` exists in the Storage Account's Azure Table blade.
- Follow the Function's App Insights telemetry logging to observe thrown Java exceptions when connecting or mapping the JSON.

## ACR Image Pull Issues
- Double-check whether `adminUserEnabled` is turned on in your Container Registry, otherwise your Container App might not be able to pull using the generated secret references.

## GitHub Actions Deployment Failures
- The most common reason for failure is omitted GitHub Actions Environments Secrets. Ensure `AZURE_CREDENTIALS` matches the exact Service Principal JSON output.
