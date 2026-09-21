package com.enviro.assessment.junior.twisisanikhosa.service;

import com.enviro.assessment.junior.twisisanikhosa.dto.PortfolioResponseDto;
import com.enviro.assessment.junior.twisisanikhosa.dto.ProductDto;
import com.enviro.assessment.junior.twisisanikhosa.entity.Investor;
import com.enviro.assessment.junior.twisisanikhosa.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.twisisanikhosa.repository.InvestorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class InvestorService {

    private final InvestorRepository investorRepository;

    public InvestorService(InvestorRepository investorRepository) {
        this.investorRepository = investorRepository;
    }

    @Transactional(readOnly = true)
    public PortfolioResponseDto getInvestorPortfolio(Long investorId) {
        Investor investor = investorRepository.findByIdWithProducts(investorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found with ID: " + investorId));

        List<ProductDto> productDtos = investor.getProducts().stream()
                .map(product -> {
                    BigDecimal maxAllowed = product.getCurrentBalance()
                            .multiply(BigDecimal.valueOf(0.90))
                            .setScale(2, RoundingMode.HALF_UP);
                    return new ProductDto(
                            product.getId(),
                            product.getName(),
                            product.getProductType(),
                            product.getCurrentBalance(),
                            maxAllowed
                    );
                })
                .toList();

        return new PortfolioResponseDto(
                investor.getId(),
                investor.getFirstName() + " " + investor.getLastName(),
                investor.getEmail(),
                investor.getContactNumber(),
                investor.getDateOfBirth(),
                investor.getAge(),
                productDtos
        );
    }

    @Transactional(readOnly = true)
    public List<PortfolioResponseDto> getAllInvestors() {
        return investorRepository.findAll().stream()
                .map(investor -> getInvestorPortfolio(investor.getId()))
                .toList();
    }
}