package com.example.ticketapi.controller;

import com.example.ticketapi.model.QueueTicketMessage;
import com.example.ticketapi.model.TicketRequest;
import com.example.ticketapi.model.TicketStatusResponse;
import com.example.ticketapi.model.TicketSubmissionResponse;
import com.example.ticketapi.service.QueueStorageService;
import com.example.ticketapi.service.TableStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final QueueStorageService queueStorageService;
    private final TableStorageService tableStorageService;

    @PostMapping
    public ResponseEntity<TicketSubmissionResponse> submitTicket(@Valid @RequestBody TicketRequest request) {
        String ticketId = UUID.randomUUID().toString();
        log.info("Received ticket submission from customer: {}, generated ID: {}", request.getCustomer(), ticketId);

        QueueTicketMessage message = QueueTicketMessage.builder()
                .ticketId(ticketId)
                .customer(request.getCustomer())
                .issue(request.getIssue())
                .priority(request.getPriority())
                .status("submitted")
                .createdAt(Instant.now().toString())
                .build();

        try {
            queueStorageService.enqueueTicket(message);
        } catch (Exception e) {
            log.error("Failed to queue ticket", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to queue ticket at this time");
        }

        TicketSubmissionResponse response = new TicketSubmissionResponse(ticketId, "queued");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketStatusResponse> getTicketStatus(@PathVariable String id) {
        log.info("Looking up ticket status for id: {}", id);
        
        return tableStorageService.getTicketStatus(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found or not processed yet"));
    }
}
