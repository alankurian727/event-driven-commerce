package com.alankurian.commerce.order.domain;

public enum OrderStatus {

    CREATED,
    INVENTORY_RESERVED,
    PAYMENT_PENDING,
    PAID,
    CONFIRMED,
    CANCELLED

}
