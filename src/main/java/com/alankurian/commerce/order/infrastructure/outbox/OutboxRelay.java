package com.alankurian.commerce.order.infrastructure.outbox;

import com.alankurian.commerce.order.domain.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class OutboxRelay {

    private static final Logger log =
            LoggerFactory.getLogger(OutboxRelay.class);

    private static final String TOPIC = "orders.created";

    private final OutboxClaimService claimService;
    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutboxRelay(OutboxClaimService claimService,
            OutboxEventRepository repository,
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.claimService = claimService;
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        var events = claimService.claimEvents();

        events.forEach(this::publish);
    }

    private void publish(OutboxEvent event) {
        var orderCreatedEvent =
                objectMapper.readValue(
                        event.getPayload(),
                        OrderCreatedEvent.class
                );

        kafkaTemplate.send(
                TOPIC,
                event.getAggregateId().toString(),
                orderCreatedEvent
        ).whenComplete((result, exception) -> {

            if (exception != null) {
                log.error(
                        "Failed to publish outbox event: eventId={}",
                        event.getId(),
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


    }

}
