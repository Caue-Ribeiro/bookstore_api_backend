package com.caue.bookstore.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bs_payment")
public class Payment {

    @Id
    private UUID id;

    private Instant moment;

    @OneToOne
    @MapsId
    private Order order;

    public Payment() {
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
        if (order != null) {
            this.id = order.getId();
        }
    }
}
