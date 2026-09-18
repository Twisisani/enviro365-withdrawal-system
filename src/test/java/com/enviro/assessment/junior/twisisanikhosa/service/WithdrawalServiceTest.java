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

@ExtendWith(MockitoExtension.class)
class WithdrawalServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WithdrawalNoticeRepository withdrawalNoticeRepository;

    @InjectMocks
    private WithdrawalService withdrawalService;

    private Investor under65Investor;
    private Investor over65Investor;
    private Product retirementProductUnder65;
    private Product retirementProductOver65;
    private Product savingsProduct;

    @BeforeEach
    void setUp() {
        under65Investor = new Investor("Test", "Junior", "junior@test.com", "0123456789", LocalDate.now().minusYears(30));
        over65Investor = new Investor("Test", "Senior", "senior@test.com", "0123456789", LocalDate.now().minusYears(68));

        retirementProductUnder65 = new Product(ProductType.RETIREMENT, "Retirement Fund A", new BigDecimal("100000.00"), under65Investor);
        retirementProductUnder65.setId(1L);

        retirementProductOver65 = new Product(ProductType.RETIREMENT, "Retirement Fund B", new BigDecimal("100000.00"), over65Investor);
        retirementProductOver65.setId(2L);

        savingsProduct = new Product(ProductType.SAVINGS, "Savings Fund", new BigDecimal("50000.00"), under65Investor);
        savingsProduct.setId(3L);
    }

    @Test
    @DisplayName("Should reject retirement withdrawal when investor age <= 65")
    void shouldRejectRetirementWithdrawal_WhenInvestorUnder65() {
        when(productRepository.findByIdWithInvestor(1L)).thenReturn(Optional.of(retirementProductUnder65));
        WithdrawalRequestDto request = new WithdrawalRequestDto(1L, new BigDecimal("10000.00"), "FNB Acc: 123456");

        InvalidWithdrawalException exception = assertThrows(InvalidWithdrawalException.class,
                () -> withdrawalService.createWithdrawalNotice(request));

        assertTrue(exception.getMessage().contains("age is greater than 65"));
        verify(withdrawalNoticeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject withdrawal when amount exceeds current balance")
    void shouldRejectWithdrawal_WhenAmountExceedsBalance() {
        when(productRepository.findByIdWithInvestor(3L)).thenReturn(Optional.of(savingsProduct));
        WithdrawalRequestDto request = new WithdrawalRequestDto(3L, new BigDecimal("60000.00"), "Standard Bank Acc: 987654");

        InvalidWithdrawalException exception = assertThrows(InvalidWithdrawalException.class,
                () -> withdrawalService.createWithdrawalNotice(request));

        assertTrue(exception.getMessage().contains("exceeds current product balance"));
        verify(withdrawalNoticeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject withdrawal when amount exceeds 90% limit")
    void shouldRejectWithdrawal_WhenAmountExceeds90Percent() {
        when(productRepository.findByIdWithInvestor(3L)).thenReturn(Optional.of(savingsProduct));
        // Balance is 50,000. 90% is 45,000. Requesting 46,000.
        WithdrawalRequestDto request = new WithdrawalRequestDto(3L, new BigDecimal("46000.00"), "Nedbank Acc: 555666");

        InvalidWithdrawalException exception = assertThrows(InvalidWithdrawalException.class,
                () -> withdrawalService.createWithdrawalNotice(request));

        assertTrue(exception.getMessage().contains("exceeds 90% of balance"));
        verify(withdrawalNoticeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully process valid withdrawal and update balance")
    void shouldProcessWithdrawalSuccessfully_WhenValid() {
        when(productRepository.findByIdWithInvestor(2L)).thenReturn(Optional.of(retirementProductOver65));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(withdrawalNoticeRepository.save(any(WithdrawalNotice.class))).thenAnswer(invocation -> {
            WithdrawalNotice notice = invocation.getArgument(0);
            notice.setId(101L);
            return notice;
        });

        // Current balance: 100,000. 90% is 90,000. Requesting 50,000.
        WithdrawalRequestDto request = new WithdrawalRequestDto(2L, new BigDecimal("50000.00"), "Capitec Acc: 778899");

        WithdrawalResponseDto response = withdrawalService.createWithdrawalNotice(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("50000.00"), response.withdrawalAmount());
        assertEquals(new BigDecimal("100000.00"), response.balanceBefore());
        assertEquals(new BigDecimal("50000.00"), response.balanceAfter());
        assertEquals(new BigDecimal("50000.00"), retirementProductOver65.getCurrentBalance());
        verify(withdrawalNoticeRepository, times(1)).save(any(WithdrawalNotice.class));
    }
}