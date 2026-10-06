package com.enviro.assessment.junior.twisisanikhosa.dto;

import java.time.LocalDate;
import java.util.List;

// Response DTO for investor portfolio with products
public record PortfolioResponseDto(
    Long investorId,
    String fullName,
    String email,
    String contactNumber,
    LocalDate dateOfBirth,
    int age,
    List<ProductDto> products
) {}