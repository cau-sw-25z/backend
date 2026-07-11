package com.example.BE.signal.dto;

import com.example.BE.signal.entity.TradingSignal;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SignalItemResponse(
        Long id,

        @JsonProperty("stock_id")
        Long stockId,

        String ticker,

        @JsonProperty("strategy_type")
        String strategyType,

        String action,

        @JsonProperty("signal_value")
        Double signalValue,

        @JsonProperty("close_price")
        BigDecimal closePrice,

        @JsonProperty("signal_date")
        LocalDate signalDate,

        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
    public static SignalItemResponse from(TradingSignal signal) {
        return new SignalItemResponse(
                signal.getId(),
                signal.getStockId(),
                signal.getTicker(),
                signal.getStrategyType(),
                signal.getAction(),
                signal.getSignalValue(),
                signal.getClosePrice(),
                signal.getSignalDate(),
                signal.getCreatedAt()
        );
    }
}