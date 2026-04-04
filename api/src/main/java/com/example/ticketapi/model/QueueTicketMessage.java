package com.example.ticketapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueTicketMessage {
    private String ticketId;
    private String customer;
    private String issue;
    private String priority;
    private String status;
    private String createdAt;
}
