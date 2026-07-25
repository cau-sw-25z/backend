package com.example.BE.portfolio.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "portfolio_weights")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_item_id", nullable = false)
    private PortfolioItem portfolioItem;

    @Column(name = "weight", nullable = false, precision = 10, scale = 6)
    private BigDecimal weight;

    /*
     * 계산 당시의 위험도 스냅샷
     * 1 = 공격형
     * 5 = 안정형
     */
    @Column(name = "risk_level", nullable = false)
    private Integer riskLevel;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    public PortfolioWeight(
            PortfolioItem portfolioItem,
            BigDecimal weight,
            Integer riskLevel,
            LocalDateTime calculatedAt
    ) {
        this.portfolioItem = portfolioItem;
        this.weight = weight;
        this.riskLevel = riskLevel;
        this.calculatedAt = calculatedAt;
    }
}