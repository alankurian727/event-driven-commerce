package com.alankurian.commerce.order.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull
        UUID customerId,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal totalAmount,

        @NotBlank
        @Pattern(regexp = "^[A-Z]{3}$")
        String currency
) {}
