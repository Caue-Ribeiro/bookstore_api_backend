package com.caue.bookstore.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "bs_order_item")
public class OrderItem {

    @EmbeddedId
    private OrderItemPK id = new OrderItemPK();

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal total;

    public OrderItem() {
    }

    public OrderItem(Book book, Order order, Integer quantity, BigDecimal price) {
        id.setBook(book);
        id.setOrder(order);
        this.quantity = quantity;
        this.price = price;
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

    public Book getBook(){
        return id.getBook();
    }

    public Order getOrder(){
        return id.getOrder();
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal() {
        this.total = price.multiply(BigDecimal.valueOf(quantity));
    }
}
