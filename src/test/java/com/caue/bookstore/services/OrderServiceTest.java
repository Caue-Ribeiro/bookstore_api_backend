package com.caue.bookstore.services;

import com.caue.bookstore.AIService.AssistantService;
import com.caue.bookstore.dto.OrderDTO;
import com.caue.bookstore.entities.*;
import com.caue.bookstore.enums.CategoryType;
import com.caue.bookstore.enums.OrderStatus;
import com.caue.bookstore.exceptions.DatabaseException;
import com.caue.bookstore.exceptions.InvalidOrderStateException;
import com.caue.bookstore.exceptions.ResourceNotFoundException;
import com.caue.bookstore.payment_gateway.services.StripeService;
import com.caue.bookstore.repositories.BookRepository;
import com.caue.bookstore.repositories.OrderRepository;
import com.caue.bookstore.repositories.PaymentRepository;
import com.caue.bookstore.repositories.UserRepository;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Mock
    private StripeService stripeService;

    @Mock
    private AssistantService assistantService;


    private static Book book = null;

    private static User user = null;

    private static Order cart;

    @BeforeEach
    void defaultValues() {
        book = new Book();

        user = new User();
        user.setId(UUID.randomUUID());

        book.setAuthors(Set.of(new Author(1L, "Fyodor", "Dostoievsky")));
        book.setCategories(Set.of(new Category(1L, CategoryType.CLASSICS)));
        book.setPrice(new BigDecimal("100.00"));
        book.setStock(100);
        book.setDescription("Test description");
        book.setReleaseDate(LocalDate.now());
        book.setTitle("White Nights");
        book.setCoverImageUrl("https://image.com");
        book.setIsbn(16168541565L);
        book.setId(UUID.randomUUID());


        cart = new Order();
        cart.setId(UUID.randomUUID());
        cart.setUser(user);
        cart.setStatus(OrderStatus.CART);
        cart.setMoment(Instant.now());
        cart.addItem(new OrderItem(book, cart, 2, book.getPrice(), book.getCoverImageUrl()));
    }


    @Test
    void shouldCheckoutReserveStockAndSetAwaitingPayment() throws StripeException {
        when(orderRepository.findByUserIdAndStatusForUpdate(user.getId(), OrderStatus.CART)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment payment = new Payment();
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(stripeService.createCheckoutSession(cart)).thenReturn("https://mock-stripe-url.com/checkout");

        OrderDTO result = orderService.checkout(user.getId());

        assertEquals(OrderStatus.AWAITING_PAYMENT, result.getStatus());
        assertNotNull(result.getPayment());
        assertEquals(98, book.getStock());

        verify(bookRepository, times(1)).save(book);
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(stripeService, times(1)).createCheckoutSession(any(Order.class));

    }

    @Test
    void shouldRejectPaymentFromAnotherUser() {
        UUID anotherUserId = UUID.randomUUID();

        cart.setStatus(OrderStatus.AWAITING_PAYMENT);

        when(orderRepository.findByIdForUpdate(cart.getId())).thenReturn(Optional.of(cart));

        assertThrows(DatabaseException.class, () -> orderService.pay(anotherUserId, cart.getId()));
    }

    @Test
    void shouldCancelAwaitingPaymentAndRestoreStock() {
        cart.setStatus(OrderStatus.AWAITING_PAYMENT);

        when(orderRepository.findByIdForUpdate(cart.getId())).thenReturn(Optional.of(cart));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDTO result = orderService.cancel(user.getId(), cart.getId());

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        assertEquals(102, book.getStock());
        verify(bookRepository).save(book);
    }

    @Test
    void shouldRejectCancelForPaidOrder() {

        cart.setStatus(OrderStatus.PAID);

        when(orderRepository.findByIdForUpdate(cart.getId())).thenReturn(Optional.of(cart));

        assertThrows(InvalidOrderStateException.class, () -> orderService.cancel(user.getId(), cart.getId()));
    }

    @Test
    void shouldAddToCartSuccessfully() {

        int quantity = 1;

        cart.setStatus(OrderStatus.CART);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(orderRepository.findByUserIdAndStatusForUpdate(user.getId(), OrderStatus.CART)).thenReturn(Optional.empty());

        when(orderRepository.save(any(Order.class))).thenReturn(cart);


        OrderDTO result = orderService.addToCart(user.getId(), book.getId(), quantity);

        assertEquals(99, result.getItems().getFirst().getAvailableStock());
        assertNotNull(result);

        verify(userRepository, times(1)).findById(user.getId());
        verify(orderRepository, times(1)).findByUserIdAndStatusForUpdate(user.getId(), OrderStatus.CART);
        verify(bookRepository, times(1)).findById(book.getId());

    }


    @Test
    void shouldAddToCartThrowDatabaseException() {
        int quantity = 0;

        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            orderService.addToCart(user.getId(), book.getId(), quantity);
        });

        assertEquals("Quantity must be greater than zero.", exception.getMessage());
    }

    @Test
    void shouldRemoveFromCartSuccessfully() {

        OrderItem item = new OrderItem(book, cart, 2, book.getPrice(), book.getCoverImageUrl());
        cart.addItem(item);

        when(orderRepository.findByUserIdAndStatusForUpdate(user.getId(), OrderStatus.CART)).thenReturn(Optional.of(cart));

        when(orderRepository.save(any(Order.class))).thenReturn(cart);

        OrderDTO orderDTO = orderService.removeFromCart(user.getId(), book.getId(), 1);

        assertNotNull(orderDTO);

        assertEquals(1, orderDTO.getItems().getFirst().getQuantity());
        assertEquals(101, orderDTO.getItems().getFirst().getAvailableStock());
    }

    @Test
    void shouldEmptyTheCartSuccessfully() {

        OrderItem item = new OrderItem(book, cart, 2, book.getPrice(), book.getCoverImageUrl());
        cart.addItem(item);

        when(orderRepository.findByUserIdAndStatusForUpdate(user.getId(), OrderStatus.CART)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        OrderDTO orderDTO = orderService.deleteItemFromCartById(user.getId(), book.getId());


        assertEquals(0, cart.getItems().size());
        assertEquals(102, book.getStock());
        assertNotNull(orderDTO);

        verify(orderRepository, times(1)).delete(cart);
    }

    @Test
    void shouldDeleteAnItemFromCartSuccessfully() {
        Book book2 = new Book();

        book2.setAuthors(Set.of(new Author(1L, "Teresa", "Cardenas")));
        book2.setCategories(Set.of(new Category(1L, CategoryType.CLASSICS)));
        book2.setPrice(BigDecimal.valueOf(50.00));
        book2.setStock(100);
        book2.setDescription("Test description");
        book2.setReleaseDate(LocalDate.now());
        book2.setTitle("Letters to my Mother");
        book2.setCoverImageUrl("https://image.com");
        book2.setIsbn(16168541658L);
        book2.setId(UUID.randomUUID());

        OrderItem item1 = new OrderItem(book, cart, 2, book.getPrice(), book.getCoverImageUrl());
        OrderItem item2 = new OrderItem(book2, cart, 3, book2.getPrice(), book2.getCoverImageUrl());
        cart.addItem(item1);
        cart.addItem(item2);

        when(orderRepository.findByUserIdAndStatusForUpdate(user.getId(), OrderStatus.CART)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(orderRepository.save(cart)).thenReturn(cart);

        OrderDTO orderDTO = orderService.deleteItemFromCartById(user.getId(), book.getId());


        assertEquals(1, cart.getItems().size());
        assertEquals(102, book.getStock());
        assertNotNull(orderDTO);


        verify(orderRepository, times(1)).save(cart);
    }

    @Test
    void shouldClearTheCartSuccessfully() {
        Book book2 = new Book();

        book2.setAuthors(Set.of(new Author(1L, "Teresa", "Cardenas")));
        book2.setCategories(Set.of(new Category(1L, CategoryType.CLASSICS)));
        book2.setPrice(BigDecimal.valueOf(50.00));
        book2.setStock(100);
        book2.setDescription("Test description");
        book2.setReleaseDate(LocalDate.now());
        book2.setTitle("Letters to my Mother");
        book2.setCoverImageUrl("https://image.com");
        book2.setIsbn(16168541658L);
        book2.setId(UUID.randomUUID());

        OrderItem item2 = new OrderItem(book2, cart, 3, book2.getPrice(), book2.getCoverImageUrl());
        cart.addItem(item2);

        when(orderRepository.findByUserIdAndStatusForUpdate(user.getId(), OrderStatus.CART)).thenReturn(Optional.of(cart));
        doNothing().when(orderRepository).delete(cart);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookRepository.findById(book2.getId())).thenReturn(Optional.of(book2));

        when(bookRepository.save(any(Book.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));


        orderService.clearCart(user.getId());

        assertEquals(102, book.getStock());
        assertEquals(103, book2.getStock());

        verify(orderRepository, times(1)).delete(cart);
    }

    @Test
    void shouldCheckoutFailTryingToFindCart() {
        when(orderRepository.findByUserIdAndStatusForUpdate(user.getId(), OrderStatus.CART)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.checkout(user.getId());

        });

        assertEquals("Cart not found.", exception.getMessage());
    }

    @Test
    void shouldCheckoutFailTryingToFindBook() {

        when(orderRepository.findByUserIdAndStatusForUpdate(cart.getId(), OrderStatus.CART)).thenReturn(Optional.of(cart));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.checkout(cart.getId());

        });

        assertEquals("Book not found.", exception.getMessage());
    }

    @Test
    void shouldCheckoutBecauseCartIsEmpty() {
        when(orderRepository.findByUserIdAndStatusForUpdate(user.getId(), OrderStatus.CART)).thenReturn(Optional.of(cart));

        cart.getItems().clear();

        InvalidOrderStateException exception = assertThrows(InvalidOrderStateException.class, () -> {
            orderService.checkout(user.getId());

        });

        assertEquals("Cart is empty.", exception.getMessage());
    }

    @Test
    void shouldCancelOrderSuccessfully() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        cart.setStatus(OrderStatus.AWAITING_PAYMENT);


        when(orderRepository.findByIdForUpdate(cart.getId())).thenReturn(Optional.of(cart));

        when(orderRepository.save(cart)).thenAnswer(invocation -> invocation.getArgument(0));

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        orderService.cancel(user.getId(), cart.getId());

        assertEquals(OrderStatus.CANCELLED, cart.getStatus());
        assertEquals(102, book.getStock());
    }

    @Test
    void shouldFailTryingToCancelAnotherUserOrder() {
        when(orderRepository.findByIdForUpdate(cart.getId())).thenReturn(Optional.of(cart));

        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            orderService.cancel(UUID.randomUUID(), cart.getId());
        });

        assertEquals("You cannot cancel another user's order.", exception.getMessage());

    }

    @Test
    void shouldFailTryingToCancelPaidOrder() {
        cart.setStatus(OrderStatus.PAID);

        when(orderRepository.findByIdForUpdate(cart.getId())).thenReturn(Optional.of(cart));

        InvalidOrderStateException exception = assertThrows(InvalidOrderStateException.class, () -> {
            orderService.cancel(user.getId(), cart.getId());
        });

        assertEquals("Paid or delivered orders cannot be cancelled.", exception.getMessage());

    }

    @Test
    void shouldGetOrderByUser() {
        cart.setStatus(OrderStatus.PAID);

        when(orderRepository.findAllByUser_IdOrderByMomentDesc(user.getId())).thenReturn(List.of(cart));

        List<OrderDTO> result = orderService.getOrdersByUser(user.getId());

        assertNotNull(result);

        assertEquals(1, result.size(), "The list should contain exactly 1 order");
        assertEquals(cart.getId(), result.getFirst().getId());
        assertEquals(cart.getStatus(), result.getFirst().getStatus());

        verify(orderRepository, times(1)).findAllByUser_IdOrderByMomentDesc(user.getId());

    }

    @Test
    void shouldGetOrderByUserReturnEmptyList() {
        when(orderRepository.findAllByUser_IdOrderByMomentDesc(user.getId())).thenReturn(List.of());

        List<OrderDTO> result = orderService.getOrdersByUser(user.getId());

        assertNotNull(result);

        assertEquals(0, result.size(), "The list should contain no items");

        verify(orderRepository, times(1)).findAllByUser_IdOrderByMomentDesc(user.getId());

    }

    @Test
    void shouldGetAllOrders() {
        cart.setStatus(OrderStatus.PAID);

        when(orderRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(cart)));

        Page<OrderDTO> result = orderService.getAllOrders(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(orderRepository, times(1)).findAll(PageRequest.of(0, 10));

    }

    @Test
    void shouldUpdateOrderStatus() {
        cart.setStatus(OrderStatus.AWAITING_PAYMENT);

        when(orderRepository.findByIdForUpdate(cart.getId())).thenReturn(Optional.of(cart));

        when(orderRepository.save(cart)).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.updateOrderStatus(cart.getId(), String.valueOf(OrderStatus.PAID));

        assertEquals(OrderStatus.PAID, cart.getStatus());

        verify(orderRepository, times(1)).findByIdForUpdate(cart.getId());
        verify(orderRepository, times(1)).save(cart);

    }

    @Test
    void shouldFailToUpdateOrderStatusWithWrongStatus() {
        cart.setStatus(OrderStatus.AWAITING_PAYMENT);
        String newStatus = "SOMETHING";

        when(orderRepository.findByIdForUpdate(cart.getId())).thenReturn(Optional.of(cart));

        DatabaseException exception = assertThrows(DatabaseException.class,
                () -> orderService.updateOrderStatus(cart.getId(), newStatus));


        assertEquals("Invalid status provided: " + newStatus, exception.getMessage());
    }

    @Test
    void shouldExecuteAIBookJudgerServiceSuccessfully() {
        cart.setStatus(OrderStatus.PAID);

        when(orderRepository.findByUserIdAndStatusForUpdate(user.getId(), OrderStatus.CART)).thenReturn(Optional.of(cart));
        when(assistantService.orderChoiceJudger(anyMap())).thenReturn(new BookJudger_Judgment("Stupid Book", List.of("Better" + " book")));

        BookJudger_Judgment result = orderService.userOrderChoiceAIJudger(user.getId());

        assertNotNull(result.better_suggestions());
        assertNotNull(result.judgment());

        verify(assistantService, times(1)).orderChoiceJudger(anyMap());
        verify(orderRepository, times(1)).findByUserIdAndStatusForUpdate(user.getId(), OrderStatus.CART);


    }

    @Test
    void shouldThrowExceptionAIBookJudgerServiceIsOverloaded() {

        cart.setStatus(OrderStatus.PAID);

        when(orderRepository.findByUserIdAndStatusForUpdate(user.getId(), OrderStatus.CART)).thenReturn(Optional.of(cart));

        when(assistantService.orderChoiceJudger(anyMap())).thenThrow(new RuntimeException("AI Model is currently " + "overloaded"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.userOrderChoiceAIJudger(user.getId()));

        assertEquals("AI Model is currently overloaded", exception.getMessage());

        verify(assistantService, times(1)).orderChoiceJudger(anyMap());
    }
}