package com.alankurian.commerce.order.infrastructure.outbox;

import com.alankurian.commerce.order.domain.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Service
public class OutboxPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(OutboxPublisher.class);

    private static final String TOPIC = "orders.created";

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Duration retryInitialDelay;
    private final Duration retryMaxDelay;

    public OutboxPublisher(
            OutboxEventRepository repository,
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${outbox.relay.retry.initial-delay}") Duration retryInitialDelay,
            @Value("${outbox.relay.retry.max-delay}") Duration retryMaxDelay) {

        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.retryInitialDelay = retryInitialDelay;
        this.retryMaxDelay = retryMaxDelay;
    }

    public void publish(OutboxEvent event) {

        try {
            var orderCreatedEvent =
                    objectMapper.readValue(
                            event.getPayload(),
                            OrderCreatedEvent.class
                    );

            kafkaTemplate
                    .send(
                            TOPIC,
                            event.getAggregateId().toString(),
                            orderCreatedEvent
                    )
                    .whenComplete((result, exception) -> {

                        if (exception != null) {

                            event.scheduleNextAttempt(
                                    retryInitialDelay,
                                    retryMaxDelay
                            );

                            event.releaseClaim();
                            repository.save(event);

                            log.error(
                                    "Failed to publish outbox event: eventId={}, attempt={}, nextAttemptAt={}",
                                    event.getId(),
                                    event.getAttemptCount(),
                                    event.getNextAttemptAt(),
                                    exception
                            );
                            return;
                        }

                        event.markPublished();
                        repository.save(event);

                        log.info(
                                "Published outbox event: eventId={}, aggregateId={}, partition={}, offset={}",
                                event.getId(),
                                event.getAggregateId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    });

        } catch (Exception e) {
            log.error(
                    "Failed to process outbox event: eventId={}",
                    event.getId(),
                    e
            );
        }
    }
}
