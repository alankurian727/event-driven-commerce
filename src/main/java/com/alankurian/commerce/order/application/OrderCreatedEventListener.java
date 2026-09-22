package com.alankurian.commerce.order.application;

import com.alankurian.commerce.order.domain.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderCreatedEventListener {

    private static final Logger log =
            LoggerFactory.getLogger(OrderCreatedEventListener.class);

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void handle(OrderCreatedEvent event) {

        log.info(
                "Order created: orderId={}, customerId={}, total={}, currency={} thread={}",
                event.orderId(),
                event.customerId(),
                event.totalAmount(),
                event.currency(),
                Thread.currentThread().getName()
        );
    }

}
