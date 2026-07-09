package com.caue.bookstore.services;

import com.caue.bookstore.AIService.AssistantService;
import com.caue.bookstore.dto.OrderDTO;
import com.caue.bookstore.dto.OrderItemDTO;
import com.caue.bookstore.dto.PaymentDTO;
import com.caue.bookstore.entities.*;
import com.caue.bookstore.enums.OrderStatus;
import com.caue.bookstore.exceptions.DatabaseException;
import com.caue.bookstore.exceptions.InsufficientStockException;
import com.caue.bookstore.exceptions.InvalidOrderStateException;
import com.caue.bookstore.exceptions.ResourceNotFoundException;
import com.caue.bookstore.payment_gateway.services.StripeService;
import com.caue.bookstore.repositories.BookRepository;
import com.caue.bookstore.repositories.OrderRepository;
import com.caue.bookstore.repositories.PaymentRepository;
import com.caue.bookstore.repositories.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final AssistantService assistantService;
    private final StripeService stripeService;

    public OrderService(OrderRepository orderRepository, PaymentRepository paymentRepository, BookRepository bookRepository, UserRepository userRepository, AssistantService assistantService, StripeService stripeService) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.assistantService = assistantService;
        this.stripeService = stripeService;
    }

    @Transactional
    public OrderDTO addToCart(UUID userId, UUID bookId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new DatabaseException("Quantity must be greater than zero.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found."));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book not found."));

        Order cart = orderRepository.findByUserIdAndStatusForUpdate(userId, OrderStatus.CART)
                .orElseGet(() -> {
                    Order order = new Order();
                    order.setUser(user);
                    order.setMoment(Instant.now());
                    order.setStatus(OrderStatus.CART);
                    return orderRepository.save(order);
                });

        ensureStockAvailable(book, quantity);

        OrderItem item = cart.getItems().stream()
                .filter(orderItem -> orderItem.getBook().getId().equals(bookId))
                .findFirst()
                .orElse(null);

        if (item == null) {
            item = new OrderItem(book, cart, quantity, book.getPrice(), book.getCoverImageUrl());
            book.setStock(book.getStock() - quantity);
            cart.addItem(item);
        } else {
            int updatedQuantity = item.getQuantity() + quantity;
            book.setStock(book.getStock() - quantity);
            ensureStockAvailable(book, updatedQuantity);
            item.setQuantity(updatedQuantity);
            item.setPrice(book.getPrice());
            item.setTotal();
        }

        recalculateTotals(cart);
        cart = orderRepository.save(cart);
        return toDto(cart);
    }

    @Transactional
    public OrderDTO removeFromCart(UUID userId, UUID bookId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new DatabaseException("Quantity must be greater than zero.");
        }

        Order cart = orderRepository.findByUserIdAndStatusForUpdate(userId, OrderStatus.CART)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found."));

        OrderItem item = cart.getItems().stream()
                .filter(orderItem -> orderItem.getBook().getId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Book not found in cart."));

        int updatedQuantity = item.getQuantity() - quantity;
        if (updatedQuantity < 0) {
            throw new DatabaseException("Cannot remove more items than are in the cart.");
        }

        if (updatedQuantity == 0) {
            cart.removeItem(item);
        } else {
            item.setQuantity(updatedQuantity);
            item.setTotal();
        }

        item.getBook().setStock(item.getBook().getStock() + quantity);
        recalculateTotals(cart);
        cart = orderRepository.save(cart);
        return toDto(cart);

    }

    @Transactional
    public OrderDTO deleteItemFromCartById(UUID userId, UUID bookId) {

        Order cart = orderRepository.findByUserIdAndStatusForUpdate(userId, OrderStatus.CART).orElseThrow(() -> new ResourceNotFoundException("Cart not found."));

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book not found."));

        List<OrderItem> orderItems = cart.getItems().stream().filter(orderItem -> orderItem.getBook().getId().equals(bookId)).toList();

        book.setStock(book.getStock() + orderItems.getFirst().getQuantity());

        cart.getItems().removeIf(orderItem -> orderItem.getBook().getId().equals(bookId));

        recalculateTotals(cart);

        if (cart.getItems().isEmpty()) {
            orderRepository.delete(cart);
        } else {
            cart = orderRepository.save(cart);
        }
        bookRepository.save(book);

        return toDto(cart);
    }

    @Transactional
    public void clearCart(UUID userId) {

        Order cart = orderRepository.findByUserIdAndStatusForUpdate(userId, OrderStatus.CART).orElseThrow(() -> new ResourceNotFoundException("Cart not found."));

        restoreStock(cart);

        cart.getItems().clear();


        orderRepository.delete(cart);

    }

    @Transactional(readOnly = true)
    public OrderDTO getCart(UUID userId) {
        Order cart = orderRepository.findByUser_IdAndStatus(userId, OrderStatus.CART)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found."));

        return toDto(cart);
    }

    @Transactional
    public OrderDTO checkout(UUID userId) {
        Order cart = orderRepository.findByUserIdAndStatusForUpdate(userId, OrderStatus.CART)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found."));

        if (cart.getItems().isEmpty()) {
            throw new InvalidOrderStateException("Cart is empty.");
        }

        for (OrderItem item : cart.getItems()) {
            Book book = bookRepository.findById(item.getBook().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found."));
            ensureStockAvailable(book, item.getQuantity());
            book.setStock(book.getStock() - item.getQuantity());
            bookRepository.save(book);
        }

        cart.setStatus(OrderStatus.AWAITING_PAYMENT);
        cart.setMoment(Instant.now());

        Payment payment = new Payment();
        payment.setMoment(Instant.now());
        payment.setOrder(cart);
        cart.setPayment(payment);

        cart = orderRepository.save(cart);
        paymentRepository.save(payment);

        OrderDTO dto = toDto(cart);

        try {
            String stripeUrl = stripeService.createCheckoutSession(cart);
            dto.setCheckoutUrl(stripeUrl);
        } catch (com.stripe.exception.StripeException e) {
            System.err.println("STRIPE ERROR DETAILS: " + e.getMessage());
            throw new RuntimeException("Stripe API Error: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("GENERAL ERROR: " + e.getMessage());
            throw new RuntimeException("Failed to generate payment session", e);
        }

        return dto;
    }

    @Transactional
    public OrderDTO pay(UUID userId, UUID orderId) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found."));

        if (!order.getUser().getId().equals(userId)) {
            throw new DatabaseException("You cannot pay for another user's order.");
        }

        if (order.getStatus() != OrderStatus.AWAITING_PAYMENT) {
            throw new InvalidOrderStateException("Only awaiting-payment orders can be paid.");
        }

        if (order.getPayment() == null) {
            Payment payment = new Payment();
            payment.setMoment(Instant.now());
            payment.setOrder(order);
            order.setPayment(payment);
            paymentRepository.save(payment);
        }


        order = orderRepository.save(order);
       OrderDTO dto= toDto(order);

        try {
            String stripeUrl = stripeService.createCheckoutSession(order);
            dto.setCheckoutUrl(stripeUrl);
        } catch (com.stripe.exception.StripeException e) {
            System.err.println("STRIPE ERROR DETAILS: " + e.getMessage());
            throw new RuntimeException("Stripe API Error: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("GENERAL ERROR: " + e.getMessage());
            throw new RuntimeException("Failed to generate payment session", e);
        }

        return dto;
    }

    @Transactional
    public OrderDTO cancel(UUID userId, UUID orderId) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found."));

        if (!order.getUser().getId().equals(userId)) {
            throw new DatabaseException("You cannot cancel another user's order.");
        }

        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException("Paid or delivered orders cannot be cancelled.");
        }

        if (order.getStatus() == OrderStatus.AWAITING_PAYMENT) {
            restoreStock(order);
        }

        order.cancel();
        order = orderRepository.save(order);
        return toDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByUser(UUID userId) {
        return orderRepository.findAllByUser_IdOrderByMomentDesc(userId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional
    public OrderDTO updateOrderStatus(UUID orderId, String newStatus) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found."));

        try {
            order.setStatus(OrderStatus.valueOf(newStatus.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new DatabaseException("Invalid status provided: " + newStatus);
        }

        order = orderRepository.save(order);
        return toDto(order);
    }

    @Transactional
    public BookJudger_Judgment userOrderChoiceAIJudger(UUID userId) {
        Map<String, List<String>> userChoices = new HashMap<>();

        List<String> titles =
                orderRepository.findByUserIdAndStatusForUpdate(userId, OrderStatus.CART)
                        .orElseThrow(() -> new ResourceNotFoundException("Cart not found."))
                        .getItems().stream()
                        .map(OrderItem::getBook)
                        .map(Book::getTitle).toList();;

        userChoices.put("user_choice", titles);


        return assistantService.orderChoiceJudger(userChoices);
    }

    //HELPER METHODS

    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Book book = bookRepository.findById(item.getBook().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found."));
            book.setStock(book.getStock() + item.getQuantity());
            bookRepository.save(book);
        }
    }

    private void ensureStockAvailable(Book book, Integer quantity) {
        int available = book.getStock() == null ? 0 : book.getStock();
        if (available < quantity) {
            throw new InsufficientStockException("Book '" + book.getTitle() + "' is sold out or does not have enough stock.");
        }
    }

    private void recalculateTotals(Order order) {
        order.getItems().forEach(OrderItem::setTotal);
    }

    private OrderDTO toDto(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setMoment(order.getMoment());
        dto.setStatus(order.getStatus());
        dto.setUserId(order.getUser() != null ? order.getUser().getId() : null);

        List<OrderItemDTO> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setBookId(item.getBook().getId());
            itemDTO.setTitle(item.getBook().getTitle());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPrice(item.getPrice());
            itemDTO.setTotal(item.getTotal());
            itemDTO.setAvailableStock(item.getBook().getStock());
            itemDTO.setCoverImageUrl(item.getCoverImageUrl());
            items.add(itemDTO);
            total = total.add(item.getTotal() == null ? BigDecimal.ZERO : item.getTotal());
        }
        dto.setItems(items);
        dto.setTotal(total);

        if (order.getPayment() != null) {
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setId(order.getPayment().getId());
            paymentDTO.setMoment(order.getPayment().getMoment());
            paymentDTO.setOrderId(order.getId());
            dto.setPayment(paymentDTO);
        }

        return dto;
    }
}



