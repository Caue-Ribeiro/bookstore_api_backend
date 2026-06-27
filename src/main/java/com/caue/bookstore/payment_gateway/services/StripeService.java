package com.caue.bookstore.payment_gateway.services;

import com.caue.bookstore.entities.Order;
import com.caue.bookstore.entities.OrderItem;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class StripeService {
    Dotenv dotenv = Dotenv.load();

    private String stripeApiKey = dotenv.get("STRIPE_SECRET_KEY");

    private String endpointSecret = dotenv.get("STRIPE_WEBHOOK_SECRET");

    private String frontendUrl= dotenv.get("FRONTEND_URL");


    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
        System.out.println("STRIPE KEY LOADED: " + (stripeApiKey != null ? "YES" : "NO"));
    }

    public String createCheckoutSession(Order order) throws StripeException{
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();


        for (OrderItem item: order.getItems()){

            long priceInCents = item.getPrice().multiply(new BigDecimal(100)).longValue();

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(item.getQuantity().longValue())
                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("usd")
                            .setUnitAmount(priceInCents)
                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(item.getBook().getTitle()).build()
                            )
                            .build()
                    )
                    .build();
            lineItems.add(lineItem);
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/orders?success=true")
                .setCancelUrl(frontendUrl + "/cart?canceled=true")
                .putMetadata("orderId", order.getId().toString())
                .addAllLineItem(lineItems)
                .build();

        Session session = Session.create(params);

        return session.getUrl();
    }

    public Event verifyWebHookSignature( String payload, String sigHeader){
        try {
            return Webhook.constructEvent(payload,sigHeader,endpointSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }
    }

}
