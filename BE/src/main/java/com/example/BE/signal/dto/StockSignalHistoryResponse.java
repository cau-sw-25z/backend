package com.example.BE.signal.dto;

import java.util.List;

public record StockSignalHistoryResponse(
        String ticker,
        int count,
        List<SignalItemResponse> signals
) {
}