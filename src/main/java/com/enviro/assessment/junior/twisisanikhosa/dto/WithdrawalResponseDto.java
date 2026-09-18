package com.enviro.assessment.junior.twisisanikhosa.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WithdrawalResponseDto(
    Long id,
    Long productId,
    String productName,
    BigDecimal withdrawalAmount,
    BigDecimal balanceBefore,
    BigDecimal balanceAfter,
    String bankingDetails,
    LocalDateTime createdAt
) {}