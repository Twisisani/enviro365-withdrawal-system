package com.enviro.assessment.junior.twisisanikhosa.dto;

import com.enviro.assessment.junior.twisisanikhosa.entity.ProductType;
import java.math.BigDecimal;

// Response DTO for product with balance and max withdrawable amount
public record ProductDto(
    Long id,
    String name,
    ProductType productType,
    BigDecimal currentBalance,
    BigDecimal maxWithdrawableAmount
) {}