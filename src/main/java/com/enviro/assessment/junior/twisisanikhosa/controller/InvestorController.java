package com.enviro.assessment.junior.twisisanikhosa.controller;

import com.enviro.assessment.junior.twisisanikhosa.dto.PortfolioResponseDto;
import com.enviro.assessment.junior.twisisanikhosa.service.InvestorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/investors")
public class InvestorController {

    private final InvestorService investorService;

    public InvestorController(InvestorService investorService) {
        this.investorService = investorService;
    }

    @GetMapping("/{id}/portfolio")
    public ResponseEntity<PortfolioResponseDto> getInvestorPortfolio(@PathVariable Long id) {
        return ResponseEntity.ok(investorService.getInvestorPortfolio(id));
    }
}