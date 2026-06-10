package com.caue.bookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemDTO {

    @NotNull(message = "Book ID must not be null.")
    private UUID bookId;
    private String title;
    @Min(value = 1, message = "Quantity must be at least 1.")
    private Integer quantity;
    @PositiveOrZero(message = "Price must be zero or positive.")
    private BigDecimal price;
    @PositiveOrZero(message = "Total must be zero or positive.")
    private BigDecimal total;
    @PositiveOrZero(message = "Available stock must be zero or positive.")
    private Integer availableStock;

    public OrderItemDTO() {
    }

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }
}
