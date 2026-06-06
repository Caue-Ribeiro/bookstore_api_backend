package com.caue.bookstore.repositories;

import com.caue.bookstore.entities.OrderItem;
import com.caue.bookstore.entities.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {
}
