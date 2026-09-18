package com.enviro.assessment.junior.twisisanikhosa.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal currentBalance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor_id", nullable = false)
    private Investor investor;

    public Product() {}

    public Product(ProductType productType, String name, BigDecimal currentBalance, Investor investor) {
        this.productType = productType;
        this.name = name;
        this.currentBalance = currentBalance;
        this.investor = investor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProductType getProductType() { return productType; }
    public void setProductType(ProductType productType) { this.productType = productType; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }
    public Investor getInvestor() { return investor; }
    public void setInvestor(Investor investor) { this.investor = investor; }
}