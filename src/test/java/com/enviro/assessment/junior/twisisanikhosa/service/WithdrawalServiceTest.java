package com.enviro.assessment.junior.twisisanikhosa.service;

import com.enviro.assessment.junior.twisisanikhosa.dto.WithdrawalRequestDto;
import com.enviro.assessment.junior.twisisanikhosa.dto.WithdrawalResponseDto;
import com.enviro.assessment.junior.twisisanikhosa.entity.Investor;
import com.enviro.assessment.junior.twisisanikhosa.entity.Product;
import com.enviro.assessment.junior.twisisanikhosa.entity.ProductType;
import com.enviro.assessment.junior.twisisanikhosa.entity.WithdrawalNotice;
import com.enviro.assessment.junior.twisisanikhosa.exception.InvalidWithdrawalException;
import com.enviro.assessment.junior.twisisanikhosa.repository.ProductRepository;
import com.enviro.assessment.junior.twisisanikhosa.repository.WithdrawalNoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * WithdrawalServiceTest - Unit tests for WithdrawalService
 *
 * Comprehensive test suite covering:
 * - Age restriction validation for retirement products
 * - Balance constraint validation
 * - 90% withdrawal limit validation
 * - Successful withdrawal processing with balance updates
 *
 * Uses Mockito for dependency injection and mocking repositories.
 * Tests business rule enforcement at the service layer.
 */
@ExtendWith(MockitoExtension.class)
class WithdrawalServiceTest {

    /**
     * Mocked ProductRepository - used to simulate database interactions
     */
    @Mock
    private ProductRepository productRepository;

    /**
     * Mocked WithdrawalNoticeRepository - used to verify withdrawal records
     */
    @Mock
    private WithdrawalNoticeRepository withdrawalNoticeRepository;

    /**
     * Service under test - injected with mocked dependencies
     */
    @InjectMocks
    private WithdrawalService withdrawalService;

    // Test data fields
    private Investor under65Investor;
    private Investor over65Investor;
    private Product retirementProductUnder65;
    private Product retirementProductOver65;
    private Product savingsProduct;

    /**
     * Setup test data before each test method
     * Creates investors with different ages and products for testing
     */
    @BeforeEach
    void setUp() {
        // Create investors with specific ages for testing
        under65Investor = new Investor("Test", "Junior", "junior@test.com", "0123456789", LocalDate.now().minusYears(30));
        over65Investor = new Investor("Test", "Senior", "senior@test.com", "0123456789", LocalDate.now().minusYears(68));

        // Create retirement product for underage investor
        retirementProductUnder65 = new Product(ProductType.RETIREMENT, "Retirement Fund A", new BigDecimal("100000.00"), under65Investor);
        retirementProductUnder65.setId(1L);

        // Create retirement product for eligible investor
        retirementProductOver65 = new Product(ProductType.RETIREMENT, "Retirement Fund B", new BigDecimal("100000.00"), over65Investor);
        retirementProductOver65.setId(2L);

        // Create savings product (no age restrictions)
        savingsProduct = new Product(ProductType.SAVINGS, "Savings Fund", new BigDecimal("50000.00"), under65Investor);
        savingsProduct.setId(3L);
    }

    /**
     * Test: Retirement withdrawal rejection for underage investor
     *
     * Verifies that withdrawals from retirement products are rejected
     * when investor age is 65 or less (requirement: age > 65)
     */
    @Test
    @DisplayName("Should reject retirement withdrawal when investor age <= 65")
    void shouldRejectRetirementWithdrawal_WhenInvestorUnder65() {
        // Arrange - Setup mock to return retirement product with underage investor
        when(productRepository.findByIdWithInvestor(1L)).thenReturn(Optional.of(retirementProductUnder65));
        WithdrawalRequestDto request = new WithdrawalRequestDto(1L, new BigDecimal("10000.00"), "FNB Acc: 123456");

        // Act & Assert - Verify exception is thrown and no withdrawal notice is created
        InvalidWithdrawalException exception = assertThrows(InvalidWithdrawalException.class,
                () -> withdrawalService.createWithdrawalNotice(request));

        assertTrue(exception.getMessage().contains("age is greater than 65"));
        verify(withdrawalNoticeRepository, never()).save(any());
    }

    /**
     * Test: Withdrawal rejection when amount exceeds balance
     *
     * Verifies that withdrawals exceeding the product's current balance
     * are rejected (cannot withdraw more than available)
     */
    @Test
    @DisplayName("Should reject withdrawal when amount exceeds current balance")
    void shouldRejectWithdrawal_WhenAmountExceedsBalance() {
        // Arrange - Setup mock to return savings product
        when(productRepository.findByIdWithInvestor(3L)).thenReturn(Optional.of(savingsProduct));
        // Savings product has R50,000, attempt to withdraw R60,000
        WithdrawalRequestDto request = new WithdrawalRequestDto(3L, new BigDecimal("60000.00"), "Standard Bank Acc: 987654");

        // Act & Assert - Verify exception is thrown
        InvalidWithdrawalException exception = assertThrows(InvalidWithdrawalException.class,
                () -> withdrawalService.createWithdrawalNotice(request));

        assertTrue(exception.getMessage().contains("exceeds current product balance"));
        verify(withdrawalNoticeRepository, never()).save(any());
    }

    /**
     * Test: Withdrawal rejection when amount exceeds 90% limit
     *
     * Verifies that withdrawals cannot exceed 90% of current balance
     * (balance = R50,000, 90% = R45,000, requesting R46,000 should fail)
     */
        @Test
    @DisplayName("Should reject withdrawal when amount exceeds 90% limit")
    void shouldRejectWithdrawal_WhenAmountExceeds90Percent() {
        // Arrange - Setup mock to return savings product
        when(productRepository.findByIdWithInvestor(3L)).thenReturn(Optional.of(savingsProduct));

        // Balance is 50,000. 90% is 45,000. Requesting 46,000.
        WithdrawalRequestDto request = new WithdrawalRequestDto(3L, new BigDecimal("46000.00"), "Nedbank Acc: 555666");

        // Act & Assert - Verify exception is thrown
        InvalidWithdrawalException exception = assertThrows(InvalidWithdrawalException.class,
                () -> withdrawalService.createWithdrawalNotice(request));

        assertTrue(exception.getMessage().contains("exceeds 90% of balance"));
        verify(withdrawalNoticeRepository, never()).save(any());
    }

    /**
     * Test: Successful withdrawal processing
     *
     * Verifies that valid withdrawals are processed successfully:
     * - Balance is updated atomically
     * - Withdrawal notice is created and saved
     * - Response contains correct transaction details
     *
     * Scenario: Eligible investor (age > 65) withdrawing from retirement fund
     * Balance: R100,000 -> Withdrawal: R50,000 -> New Balance: R50,000
     */
    @Test
    @DisplayName("Should successfully process valid withdrawal and update balance")
    void shouldProcessWithdrawalSuccessfully_WhenValid() {
        // Arrange - Setup mocks for eligible investor's retirement product
        when(productRepository.findByIdWithInvestor(2L)).thenReturn(Optional.of(retirementProductOver65));
        // Mock save to return the same product (simulating persistence)
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Mock withdrawal notice save to set ID on returned notice
        when(withdrawalNoticeRepository.save(any(WithdrawalNotice.class))).thenAnswer(invocation -> {
            WithdrawalNotice notice = invocation.getArgument(0);
            notice.setId(101L);
            return notice;
        });

        // Retirement product balance: R100,000
        // 90% limit: R90,000
        // Request: R50,000 (valid)
        WithdrawalRequestDto request = new WithdrawalRequestDto(2L, new BigDecimal("50000.00"), "Capitec Acc: 778899");

        // Act - Process withdrawal
        WithdrawalResponseDto response = withdrawalService.createWithdrawalNotice(request);

        // Assert - Verify successful processing
        assertNotNull(response);
        assertEquals(new BigDecimal("50000.00"), response.withdrawalAmount());
        assertEquals(new BigDecimal("100000.00"), response.balanceBefore());
        assertEquals(new BigDecimal("50000.00"), response.balanceAfter());
        // Verify product balance was updated
        assertEquals(new BigDecimal("50000.00"), retirementProductOver65.getCurrentBalance());
        // Verify withdrawal notice was persisted
        verify(withdrawalNoticeRepository, times(1)).save(any(WithdrawalNotice.class));
    }
}