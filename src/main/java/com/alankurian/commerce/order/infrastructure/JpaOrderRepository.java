package com.alankurian.commerce.order.infrastructure;

import com.alankurian.commerce.order.domain.Order;
import com.alankurian.commerce.order.domain.OrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaOrderRepository extends JpaRepository<Order, UUID> ,OrderRepository {

}
