package com.example.ticketapi.service;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.example.ticketapi.model.QueueTicketMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@Slf4j
public class QueueStorageService {

    private final QueueClient queueClient;
    private final ObjectMapper objectMapper;

    public QueueStorageService(
            @Value("${azure.storage.connection-string}") String connectionString,
            @Value("${azure.storage.queue.name}") String queueName,
            ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        
        if (connectionString != null && !connectionString.isEmpty()) {
            this.queueClient = new QueueClientBuilder()
                    .connectionString(connectionString)
                    .queueName(queueName)
                    .buildClient();
            try {
                this.queueClient.createIfNotExists();
            } catch (Exception e) {
                log.warn("Could not create queue. This might be normal in local dev without Azurite: {}", e.getMessage());
            }
        } else {
            log.warn("Azure Storage connection string is not configured. Queue storage is disabled.");
            this.queueClient = null;
        }
    }

    public void enqueueTicket(QueueTicketMessage message) {
        if (queueClient == null) {
            log.warn("Simulating queue send (no connection string): {}", message);
            return;
        }

        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            // Azure Queue usually expects Base64 encoded messages
            String base64Message = Base64.getEncoder().encodeToString(jsonMessage.getBytes());
            queueClient.sendMessage(base64Message);
            log.info("Message sent to queue for ticket: {}", message.getTicketId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize queue message", e);
            throw new RuntimeException("Error processing ticket data", e);
        } catch (Exception e) {
            log.error("Failed to send message to Azure Queue Storage", e);
            throw new RuntimeException("Error communicating with queue", e);
        }
    }
}
