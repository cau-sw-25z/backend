package com.example.BE.portfolio.entity;

import com.example.BE.stock.entity.Stock;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Entity
@Table(name = "portfolio_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(name = "avg_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal avgPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public PortfolioItem(Portfolio portfolio, Stock stock, BigDecimal avgPrice, Integer quantity) {
        this.portfolio = portfolio;
        this.stock = stock;
        this.avgPrice = avgPrice;
        this.quantity = quantity;
    }

    public void applyBuy(BigDecimal buyPrice, Integer buyQuantity) {
        BigDecimal currentAmount = avgPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal buyAmount = buyPrice.multiply(BigDecimal.valueOf(buyQuantity));
        int updatedQuantity = quantity + buyQuantity;

        this.avgPrice = currentAmount
                .add(buyAmount)
                .divide(BigDecimal.valueOf(updatedQuantity), 2, RoundingMode.HALF_UP);
        this.quantity = updatedQuantity;
    }

    public void applySell(Integer sellQuantity) {
        this.quantity -= sellQuantity;
    }
}