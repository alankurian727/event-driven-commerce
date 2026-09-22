package com.alankurian.commerce.order.infrastructure.kafka;

import com.alankurian.commerce.order.domain.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderCreatedConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderCreatedConsumer.class);

    private final ProcessedEventRepository processedEventRepository;

    public OrderCreatedConsumer(
            ProcessedEventRepository processedEventRepository
    ) {
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    @KafkaListener(
            topics = "orders.created",
            groupId = "commerce-order-processor"
    )
    public void consume(OrderCreatedEvent event) {

        int inserted =
                processedEventRepository.tryMarkProcessed(event.eventId());

        if (inserted == 0) {
            log.info(
                    "Ignoring duplicate event: eventId={}",
                    event.eventId()
            );
            return;
        }


        log.info(
                "Received OrderCreatedEvent: eventId={}, orderId={}",
                event.eventId(),
                event.orderId()
        );

        // Business processing will go here.

    }
}
