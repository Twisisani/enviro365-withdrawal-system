package com.enviro.assessment.junior.twisisanikhosa.controller;

import com.enviro.assessment.junior.twisisanikhosa.dto.PortfolioResponseDto;
import com.enviro.assessment.junior.twisisanikhosa.service.InvestorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investors")
public class InvestorController {

    private final InvestorService investorService;

    /**
     * Constructor with dependency injection
     *
     * @param investorService Service layer for investor business logic
     */
    public InvestorController(InvestorService investorService) {
        this.investorService = investorService;
    }
        
    /**
     * GET /api/investors/{id}/portfolio - Get investor portfolio by ID
     *
     * Retrieves complete portfolio information for a specific investor including:
     * - Investor personal details (name, email, contact, age)
     * - All associated products with balances and withdrawal limits
     *
     * @param id The investor ID
     * @return HTTP 200 (OK) with PortfolioResponseDto containing investor details and products
     */
    @GetMapping("/{id}/portfolio")
    public ResponseEntity<PortfolioResponseDto> getInvestorPortfolio(@PathVariable Long id) {
        return ResponseEntity.ok(investorService.getInvestorPortfolio(id));
    }

    @GetMapping
    public ResponseEntity<List<PortfolioResponseDto>> getAllInvestors() {
        return ResponseEntity.ok(investorService.getAllInvestors());
    }
}