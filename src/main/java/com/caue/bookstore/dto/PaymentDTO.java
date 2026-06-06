package com.caue.bookstore.dto;

import java.time.Instant;
import java.util.UUID;

public class PaymentDTO {

    private UUID id;
    private Instant moment;
    private UUID orderId;

    public PaymentDTO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }
}

