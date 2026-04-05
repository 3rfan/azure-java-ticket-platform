package com.example.ticketprocessor;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import com.azure.data.tables.models.TableEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;

import java.time.Instant;

/**
 * Azure Functions with Azure Storage Queue trigger.
 */
public class TicketQueueFunction {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @FunctionName("TicketQueueFunction")
    public void run(
            @QueueTrigger(name = "message", queueName = "tickets", connection = "AzureStorageConnectionString") String message,
            final ExecutionContext context) {
        
        context.getLogger().info("Java Queue trigger function executed. Message received.");

        try {
            // Process the message payload
            JsonNode ticket = objectMapper.readTree(message);
            String ticketId = ticket.path("ticketId").asText();
            String customer = ticket.path("customer").asText();
            String issue = ticket.path("issue").asText();
            String priority = ticket.path("priority").asText("low");

            context.getLogger().info("Processing ticket " + ticketId + " for customer " + customer);

            // Simple processing summary
            String summary;
            if ("high".equalsIgnoreCase(priority)) {
                summary = "High-priority ticket received for " + customer + ": " + issue;
            } else {
                summary = "Processed ticket for " + customer + ": " + issue;
            }

            // Write to Table Storage
            String connectionString = System.getenv("AzureStorageConnectionString");
            String tableName = System.getenv("TableName");
            
            if (tableName == null || tableName.isEmpty()) {
                tableName = "ticketresults";
            }

            if (connectionString != null && !connectionString.isEmpty()) {
                TableClient tableClient = new TableClientBuilder()
                        .connectionString(connectionString)
                        .tableName(tableName)
                        .buildClient();
                        
                try {
                    tableClient.createTable();
                } catch (Exception e) {
                    // Ignore if already exists
                }

                TableEntity entity = new TableEntity("tickets", ticketId)
                        .addProperty("status", "processed")
                        .addProperty("summary", summary)
                        .addProperty("priority", priority)
                        .addProperty("processedAt", Instant.now().toString());

                tableClient.upsertEntity(entity);
                context.getLogger().info("Result written to table storage for ticket: " + ticketId);
            } else {
                context.getLogger().warning("AzureStorageConnectionString not set. Simulated write only: " + summary);
            }

        } catch (Exception e) {
            context.getLogger().severe("Error processing queue message: " + e.getMessage());
        }
    }
}
