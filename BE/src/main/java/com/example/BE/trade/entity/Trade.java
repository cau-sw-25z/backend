package com.example.BE.trade.entity;

import com.example.BE.portfolio.entity.PortfolioItem;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "trades")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_item_id", nullable = false)
    private PortfolioItem portfolioItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_type", nullable = false, length = 20)
    private TradeType tradeType;

    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "trade_date", nullable = false)
    private LocalDateTime tradeDate;

    public Trade(
            PortfolioItem portfolioItem,
            TradeType tradeType,
            BigDecimal price,
            Integer quantity,
            LocalDateTime tradeDate
    ) {
        this.portfolioItem = portfolioItem;
        this.tradeType = tradeType;
        this.price = price;
        this.quantity = quantity;
        this.tradeDate = tradeDate;
    }
}
