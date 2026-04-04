package com.example.ticketapi.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TicketRequest {
    @NotBlank(message = "Customer is required")
    private String customer;

    @NotBlank(message = "Issue description is required")
    private String issue;

    @NotBlank(message = "Priority is required")
    private String priority;
}
