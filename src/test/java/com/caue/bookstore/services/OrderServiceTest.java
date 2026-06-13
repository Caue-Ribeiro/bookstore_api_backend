package com.caue.bookstore.services;

import com.caue.bookstore.dto.OrderDTO;
import com.caue.bookstore.entities.Book;
import com.caue.bookstore.entities.Order;
import com.caue.bookstore.entities.OrderItem;
import com.caue.bookstore.entities.User;
import com.caue.bookstore.enums.OrderStatus;
import com.caue.bookstore.exceptions.DatabaseException;
import com.caue.bookstore.exceptions.InvalidOrderStateException;
import com.caue.bookstore.repositories.BookRepository;
import com.caue.bookstore.repositories.OrderRepository;
import com.caue.bookstore.repositories.PaymentRepository;
import com.caue.bookstore.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCheckoutReserveStockAndSetAwaitingPayment() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Clean Code");
        book.setPrice(new BigDecimal("50.00"));
        book.setStock(10);

        Order cart = new Order();
        cart.setId(UUID.randomUUID());
        cart.setUser(user);
        cart.setStatus(OrderStatus.CART);
        cart.setMoment(Instant.now());
        cart.addItem(new OrderItem(book, cart, 2, book.getPrice(), book.getCoverImageUrl()));

        when(orderRepository.findByUserIdAndStatusForUpdate(userId, OrderStatus.CART)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDTO result = orderService.checkout(userId);

        assertEquals(OrderStatus.AWAITING_PAYMENT, result.getStatus());
        assertTrue(result.getPayment() != null);
        assertEquals(8, book.getStock());
        verify(bookRepository).save(book);
        verify(paymentRepository).save(any());
    }

    @Test
    void shouldRejectPaymentFromAnotherUser() {
        UUID ownerId = UUID.randomUUID();
        UUID anotherUserId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        User owner = new User();
        owner.setId(ownerId);

        Order order = new Order();
        order.setId(orderId);
        order.setUser(owner);
        order.setStatus(OrderStatus.AWAITING_PAYMENT);

        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.of(order));

        assertThrows(DatabaseException.class, () -> orderService.pay(anotherUserId, orderId));
    }

    @Test
    void shouldCancelAwaitingPaymentAndRestoreStock() {
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Book book = new Book();
        book.setId(bookId);
        book.setTitle("DDD");
        book.setStock(5);
        book.setPrice(new BigDecimal("30.00"));

        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);
        order.setStatus(OrderStatus.AWAITING_PAYMENT);
        order.addItem(new OrderItem(book, order, 3, book.getPrice(), book.getCoverImageUrl()));

        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.of(order));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDTO result = orderService.cancel(userId, orderId);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        assertEquals(8, book.getStock());
        verify(bookRepository).save(book);
    }

    @Test
    void shouldRejectCancelForPaidOrder() {
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);
        order.setStatus(OrderStatus.PAID);

        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStateException.class, () -> orderService.cancel(userId, orderId));
    }
}
