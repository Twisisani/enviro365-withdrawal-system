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

    // Constructor with dependency injection
    public InvestorController(InvestorService investorService) {
        this.investorService = investorService;
    }
        
    // GET portfolio for investor by ID
    @GetMapping("/{id}/portfolio")
    public ResponseEntity<PortfolioResponseDto> getInvestorPortfolio(@PathVariable Long id) {
        return ResponseEntity.ok(investorService.getInvestorPortfolio(id));
    }

    // GET all investors
    @GetMapping
    public ResponseEntity<List<PortfolioResponseDto>> getAllInvestors() {
        return ResponseEntity.ok(investorService.getAllInvestors());
    }
}