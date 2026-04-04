package com.example.ticketapi.service;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import com.azure.data.tables.models.TableEntity;
import com.example.ticketapi.model.TicketStatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class TableStorageService {

    private final TableClient tableClient;
    private static final String PARTITION_KEY = "tickets";

    public TableStorageService(
            @Value("${azure.storage.connection-string}") String connectionString,
            @Value("${azure.storage.table.name}") String tableName) {
        
        if (connectionString != null && !connectionString.isEmpty()) {
            this.tableClient = new TableClientBuilder()
                    .connectionString(connectionString)
                    .tableName(tableName)
                    .buildClient();
            try {
                this.tableClient.createTableIfNotExists();
            } catch (Exception e) {
                log.warn("Could not create table. This might be normal in local dev without Azurite: {}", e.getMessage());
            }
        } else {
            log.warn("Azure Storage connection string is not configured. Table storage is disabled.");
            this.tableClient = null;
        }
    }

    public Optional<TicketStatusResponse> getTicketStatus(String ticketId) {
        if (tableClient == null) {
            log.warn("Simulating table lookup (no connection string): {}", ticketId);
            return Optional.empty();
        }

        try {
            TableEntity entity = tableClient.getEntity(PARTITION_KEY, ticketId);
            
            TicketStatusResponse response = new TicketStatusResponse();
            response.setTicketId(ticketId);
            response.setStatus((String) entity.getProperty("status"));
            response.setSummary((String) entity.getProperty("summary"));
            
            return Optional.of(response);
        } catch (Exception e) {
            // Usually TableServiceException with 404 response
            log.info("Ticket not found in table storage: {}", ticketId);
            return Optional.empty();
        }
    }
}
