package com.caue.bookstore.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderStatusUpdateDTO(
        @NotBlank(message = "Status cannot be blank")
        String status
) {}
