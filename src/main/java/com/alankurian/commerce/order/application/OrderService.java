package com.alankurian.commerce.order.application;

import com.alankurian.commerce.order.api.CreateOrderRequest;
import com.alankurian.commerce.order.api.OrderResponse;
import com.alankurian.commerce.order.domain.Order;
import com.alankurian.commerce.order.domain.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        var order = Order.create(
                request.customerId(),
                request.totalAmount(),
                request.currency()
        );

        var savedOrder = orderRepository.save(order);

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
