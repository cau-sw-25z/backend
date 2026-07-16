package com.example.BE.signal.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Immutable
@Table(name = "trading_signals")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradingSignal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "stock_id", nullable = false)
    private Long stockId;

    @Column(name = "ticker", nullable = false, length = 50)
    private String ticker;

    @Column(name = "strategy_type", nullable = false, length = 50)
    private String strategyType;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "signal_value", nullable = false)
    private Double signalValue;

    @Column(name = "close_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal closePrice;

    @Column(name = "signal_date", nullable = false)
    private LocalDate signalDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}