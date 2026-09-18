package com.enviro.assessment.junior.twisisanikhosa.service;

import com.enviro.assessment.junior.twisisanikhosa.dto.WithdrawalRequestDto;
import com.enviro.assessment.junior.twisisanikhosa.dto.WithdrawalResponseDto;
import com.enviro.assessment.junior.twisisanikhosa.entity.Product;
import com.enviro.assessment.junior.twisisanikhosa.entity.ProductType;
import com.enviro.assessment.junior.twisisanikhosa.entity.WithdrawalNotice;
import com.enviro.assessment.junior.twisisanikhosa.exception.InvalidWithdrawalException;
import com.enviro.assessment.junior.twisisanikhosa.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.twisisanikhosa.repository.ProductRepository;
import com.enviro.assessment.junior.twisisanikhosa.repository.WithdrawalNoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WithdrawalService {

    private static final BigDecimal NINETY_PERCENT = BigDecimal.valueOf(0.90);
    private final ProductRepository productRepository;
    private final WithdrawalNoticeRepository withdrawalNoticeRepository;

    public WithdrawalService(ProductRepository productRepository,
                             WithdrawalNoticeRepository withdrawalNoticeRepository) {
        this.productRepository = productRepository;
        this.withdrawalNoticeRepository = withdrawalNoticeRepository;
    }

    @Transactional
    public WithdrawalResponseDto createWithdrawalNotice(WithdrawalRequestDto request) {
        Product product = productRepository.findByIdWithInvestor(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.productId()));

        // Business Rule 1: Retirement product age check (> 65)
        if (product.getProductType() == ProductType.RETIREMENT) {
            int age = product.getInvestor().getAge();
            if (age <= 65) {
                throw new InvalidWithdrawalException(
                        "Retirement withdrawals are only allowed if the investor's age is greater than 65. Current age: " + age
                );
            }
        }

        BigDecimal requestedAmount = request.amount();
        BigDecimal currentBalance = product.getCurrentBalance();

        // Business Rule 2: Cannot exceed current balance
        if (requestedAmount.compareTo(currentBalance) > 0) {
            throw new InvalidWithdrawalException(
                    "Withdrawal amount (R" + requestedAmount + ") exceeds current product balance (R" + currentBalance + ")"
            );
        }

        // Business Rule 3: Cannot exceed 90% of current balance
        BigDecimal maxAllowed = currentBalance.multiply(NINETY_PERCENT).setScale(2, RoundingMode.HALF_UP);
        if (requestedAmount.compareTo(maxAllowed) > 0) {
            throw new InvalidWithdrawalException(
                    "Withdrawal amount (R" + requestedAmount + ") exceeds 90% of balance. Maximum allowed: R" + maxAllowed
            );
        }

        // Atomic balance calculation & persistence
        BigDecimal newBalance = currentBalance.subtract(requestedAmount).setScale(2, RoundingMode.HALF_UP);
        product.setCurrentBalance(newBalance);
        productRepository.save(product);

        WithdrawalNotice notice = new WithdrawalNotice(
                requestedAmount,
                currentBalance,
                newBalance,
                request.bankingDetails().trim(),
                LocalDateTime.now(),
                product
        );
        WithdrawalNotice savedNotice = withdrawalNoticeRepository.save(notice);

        return mapToDto(savedNotice);
    }

    @Transactional(readOnly = true)
    public List<WithdrawalResponseDto> getWithdrawalNotices(Long productId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime end = (endDate != null) ? endDate.atTime(23, 59, 59) : null;

        return withdrawalNoticeRepository.findFilteredNotices(productId, start, end)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public void exportWithdrawalsCsv(Long productId, LocalDate startDate, LocalDate endDate, PrintWriter writer) {
        List<WithdrawalResponseDto> notices = getWithdrawalNotices(productId, startDate, endDate);

        writer.println("Notice ID,Product Name,Withdrawal Amount,Balance Before,Balance After,Banking Details,Date Created");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (WithdrawalResponseDto dto : notices) {
            writer.printf("\"%d\",\"%s\",\"R%.2f\",\"R%.2f\",\"R%.2f\",\"%s\",\"%s\"%n",
                    dto.id(),
                    dto.productName().replace("\"", "\"\""),
                    dto.withdrawalAmount(),
                    dto.balanceBefore(),
                    dto.balanceAfter(),
                    dto.bankingDetails().replace("\"", "\"\""),
                    dto.createdAt().format(formatter)
            );
        }
        writer.flush();
    }

    private WithdrawalResponseDto mapToDto(WithdrawalNotice notice) {
        return new WithdrawalResponseDto(
                notice.getId(),
                notice.getProduct().getId(),
                notice.getProduct().getName(),
                notice.getWithdrawalAmount(),
                notice.getBalanceBefore(),
                notice.getBalanceAfter(),
                notice.getBankingDetails(),
                notice.getCreatedAt()
        );
    }
}