package com.example.ticketapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatusResponse {
    private String ticketId;
    private String status;
    private String summary;
}
