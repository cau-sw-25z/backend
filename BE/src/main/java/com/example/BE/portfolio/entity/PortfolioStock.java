package com.example.BE.portfolio.entity;

import com.example.BE.stock.entity.Stock;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "portfolio_stocks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_portfolio_stock",
                        columnNames = {"portfolio_id", "stock_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 소속 포트폴리오
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    // 포트폴리오에 담긴 종목
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    public PortfolioStock(Portfolio portfolio, Stock stock) {
        this.portfolio = portfolio;
        this.stock = stock;
    }
}