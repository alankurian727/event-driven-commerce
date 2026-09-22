package com.alankurian.commerce.order.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderItemRequest(@NotNull UUID productId,
                                     @Min(1) int quantity,
                                     @NotNull
                                     @DecimalMin(value = "0.01")
                                     BigDecimal unitPrice) {
}
