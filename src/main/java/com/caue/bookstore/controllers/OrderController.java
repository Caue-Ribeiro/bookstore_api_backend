package com.caue.bookstore.controllers;

import com.caue.bookstore.dto.OrderDTO;
import com.caue.bookstore.exceptions.DatabaseException;
import com.caue.bookstore.services.OrderService;
import jakarta.validation.constraints.Min;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    
    @PostMapping("/users/{userId}/cart/items/{bookId}")
    @PreAuthorize("hasRole('ADMIN') or @securityChecker.isUserOwner(authentication,#userId)")
    public ResponseEntity<@NonNull OrderDTO> addToCart(@PathVariable UUID userId,
                                                       @PathVariable UUID bookId,
                                              @RequestParam @Min(1) Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new DatabaseException("Quantity must be greater than zero.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addToCart(userId, bookId, quantity));
    }

    
    @DeleteMapping("/users/{userId}/cart/items/{bookId}")
    @PreAuthorize("hasRole('ADMIN') or @securityChecker.isUserOwner(authentication,#userId)")
    public ResponseEntity<@NonNull OrderDTO> removeFromCart(@PathVariable UUID userId,
                                                            @PathVariable UUID bookId,
                                                   @RequestParam @Min(1) Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new DatabaseException("Quantity must be greater than zero.");
        }
        return ResponseEntity.ok(orderService.removeFromCart(userId, bookId, quantity));
    }

    @DeleteMapping("/users/{userId}/cart/trash-item/{bookId}")
    @PreAuthorize("hasRole('ADMIN') or @securityChecker.isUserOwner(authentication, #userId)")
    public ResponseEntity<OrderDTO> deleteItemFromCartById(@PathVariable UUID userId, @PathVariable UUID bookId){
               OrderDTO order= orderService.deleteItemFromCartById(userId,bookId);

               return ResponseEntity.ok(order);
    }

    @DeleteMapping("/users/clear-cart/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @securityChecker.isUserOwner(authentication, #userId)")
    public ResponseEntity<Void> clearCart(@PathVariable UUID userId){

        orderService.clearCart(userId);

        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/users/{userId}/cart")
    @PreAuthorize("hasRole('ADMIN') or @securityChecker.isUserOwner(authentication,#userId)")
    public ResponseEntity<@NonNull OrderDTO> getCart(@PathVariable UUID userId) {
        return ResponseEntity.ok(orderService.getCart(userId));
    }

    
    @PostMapping("/users/{userId}/checkout")
    @PreAuthorize("hasRole('ADMIN') or @securityChecker.isUserOwner(authentication,#userId)")
    public ResponseEntity<@NonNull OrderDTO> checkout(@PathVariable UUID userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.checkout(userId));
    }

    
    @PostMapping("/users/{userId}/{orderId}/pay")
    @PreAuthorize("hasRole('ADMIN') or @securityChecker.isUserOwner(authentication,#userId)")
    public ResponseEntity<@NonNull OrderDTO> pay(@PathVariable UUID userId, @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.pay(userId, orderId));
    }

    
    @PostMapping("/users/{userId}/{orderId}/cancel")
    @PreAuthorize("hasRole('ADMIN') or @securityChecker.isUserOwner(authentication,#userId)")
    public ResponseEntity<@NonNull OrderDTO> cancel(@PathVariable UUID userId, @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.cancel(userId, orderId));
    }

    
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @securityChecker.isUserOwner(authentication,#userId)")
    public ResponseEntity<@NonNull List<@NonNull OrderDTO>> getOrdersByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getAllOrders(){

        List<OrderDTO> orders = orderService.getAllOrders();

        return ResponseEntity.ok(orders);
    }
}




