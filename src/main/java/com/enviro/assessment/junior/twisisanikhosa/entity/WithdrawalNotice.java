package com.enviro.assessment.junior.twisisanikhosa.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "withdrawal_notices")
public class WithdrawalNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal withdrawalAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceBefore;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @Column(nullable = false)
    private String bankingDetails;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public WithdrawalNotice() {}

    public WithdrawalNotice(BigDecimal withdrawalAmount, BigDecimal balanceBefore, BigDecimal balanceAfter,
                            String bankingDetails, LocalDateTime createdAt, Product product) {
        this.withdrawalAmount = withdrawalAmount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.bankingDetails = bankingDetails;
        this.createdAt = createdAt;
        this.product = product;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getWithdrawalAmount() { return withdrawalAmount; }
    public void setWithdrawalAmount(BigDecimal withdrawalAmount) { this.withdrawalAmount = withdrawalAmount; }
    public BigDecimal getBalanceBefore() { return balanceBefore; }
    public void setBalanceBefore(BigDecimal balanceBefore) { this.balanceBefore = balanceBefore; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    public String getBankingDetails() { return bankingDetails; }
    public void setBankingDetails(String bankingDetails) { this.bankingDetails = bankingDetails; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}