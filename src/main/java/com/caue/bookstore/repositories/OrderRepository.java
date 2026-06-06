package com.caue.bookstore.repositories;

import com.caue.bookstore.entities.Order;
import com.caue.bookstore.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByUser_IdAndStatus(UUID userId, OrderStatus status);

    List<Order> findAllByUser_IdOrderByMomentDesc(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    Optional<Order> findByUserIdAndStatusForUpdate(@Param("userId") UUID userId, @Param("status") OrderStatus status);
}


