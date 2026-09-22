package com.alankurian.commerce.order.application;

import com.alankurian.commerce.order.api.CreateOrderRequest;
import com.alankurian.commerce.order.api.OrderResponse;
import com.alankurian.commerce.order.domain.Order;
import com.alankurian.commerce.order.domain.OrderItem;
import com.alankurian.commerce.order.domain.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository,
                        OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        var items = request.items()
                .stream()
                .map(item -> OrderItem.create(
                        item.productId(),
                        item.quantity(),
                        item.unitPrice()
                ))
                .toList();

        var order = Order.create(
                request.customerId(),
                request.currency(),
                items
        );

        var savedOrder = orderRepository.save(order);
        order.domainEvents().forEach(eventPublisher::publish);
        order.clearDomainEvents();


        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId) {

        var order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(orderId)
                );

        return OrderResponse.from(order);
    }

}
