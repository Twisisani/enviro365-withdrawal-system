package com.enviro.assessment.junior.twisisanikhosa.config;

import com.enviro.assessment.junior.twisisanikhosa.entity.Investor;
import com.enviro.assessment.junior.twisisanikhosa.entity.Product;
import com.enviro.assessment.junior.twisisanikhosa.entity.ProductType;
import com.enviro.assessment.junior.twisisanikhosa.repository.InvestorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(InvestorRepository investorRepository) {
        return args -> {
            // Investor 1: Senior citizen (Eligible for retirement withdrawals, age > 65)[cite: 1]
            Investor senior = new Investor(
                    "Sarah", "Mokoena", "sarah.Mokoena@enviro365.co.za", "+27 66 793 7018", LocalDate.of(1955, 4, 12)
            );
            Product seniorSavings = new Product(ProductType.SAVINGS, "Growth Tax-Free Savings", new BigDecimal("120000.00"), senior);
            Product seniorRetirement = new Product(ProductType.RETIREMENT, "Prime Annuity Retirement Fund", new BigDecimal("750000.00"), senior);
            senior.setProducts(List.of(seniorSavings, seniorRetirement));
            investorRepository.save(senior);

            // Investor 2: Young professional (Ineligible for retirement withdrawals, age <= 65)[cite: 1]
            Investor junior = new Investor(
                    "Thabo", "Dlamini", "thabo.dlamini@enviro365.co.za", "+27 71 987 6543", LocalDate.of(1994, 9, 23)
            );
            Product juniorSavings = new Product(ProductType.SAVINGS, "Flexible Wealth Saver", new BigDecimal("45000.00"), junior);
            Product juniorRetirement = new Product(ProductType.RETIREMENT, "Future Guard Retirement Plan", new BigDecimal("280000.00"), junior);
            junior.setProducts(List.of(juniorSavings, juniorRetirement));
            investorRepository.save(junior);

            Investor twisisani = new Investor(
                    "Twisisani Howel", "Khosa", "twisisani.khosa@enviro365.co.za", "+27 71 519 1463", LocalDate.of(2001, 10, 13));
            Product twisisaniSavings = new Product(ProductType.SAVINGS, "Flexible Wealth Saver", new BigDecimal("62000.00"), twisisani);
            Product twisisaniRetirement = new Product(ProductType.RETIREMENT, "Future Guard Retirement Plan", new BigDecimal("395000.00"), twisisani);
            twisisani.setProducts(List.of(twisisaniSavings, twisisaniRetirement));
            investorRepository.save(twisisani);
        };
    }
}