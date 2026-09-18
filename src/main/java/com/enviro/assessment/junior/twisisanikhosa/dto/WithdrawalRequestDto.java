package com.enviro.assessment.junior.twisisanikhosa.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record WithdrawalRequestDto(
    @NotNull(message = "Product ID is required")
    Long productId,

    @NotNull(message = "Withdrawal amount is required")
    @DecimalMin(value = "0.01", message = "Withdrawal amount must be greater than zero")
    BigDecimal amount,

    @NotBlank(message = "Banking details are required")
    String bankingDetails
) {}