package com.caue.bookstore.payment_gateway.controllers;

import com.caue.bookstore.enums.OrderStatus;
import com.caue.bookstore.payment_gateway.services.StripeService;
import com.caue.bookstore.repositories.OrderRepository;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class StripeController {

    private final StripeService stripeService;
    private final OrderRepository orderRepository;


    public StripeController(StripeService stripeService, OrderRepository orderRepository) {
        this.stripeService = stripeService;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader){

        Event event;

        try {
            event = stripeService.verifyWebHookSignature(payload,sigHeader);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook Error");
        }

        if("checkout.session.completed".equals(event.getType())){
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            if (session != null){
                String orderIdString = session.getMetadata().get("orderId");

                if (orderIdString != null){
                    UUID orderId = UUID.fromString(orderIdString);

                    orderRepository.findById(orderId).ifPresent(order -> {
                        order.setStatus(OrderStatus.PAID);

                        orderRepository.save(order);
                    });
                }
            }
        }

        return ResponseEntity.ok("Success");
    }
}
