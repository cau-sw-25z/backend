package com.example.BE.signal.service;

import com.example.BE.common.exception.CustomException;
import com.example.BE.common.exception.ErrorCode;
import com.example.BE.signal.dto.LatestSignalsResponse;
import com.example.BE.signal.dto.SignalItemResponse;
import com.example.BE.signal.dto.StockSignalHistoryResponse;
import com.example.BE.signal.entity.TradingSignal;
import com.example.BE.signal.repository.TradingSignalRepository;
import com.example.BE.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignalService {

    private final TradingSignalRepository tradingSignalRepository;
    private final StockRepository stockRepository;

    private static final int DEFAULT_HISTORY_SIZE = 50;
    private static final int MAX_HISTORY_SIZE = 200;

    public LatestSignalsResponse getLatestSignals() {
        LocalDate latestSignalDate = tradingSignalRepository.findLatestSignalDate();

        if (latestSignalDate == null) {
            return new LatestSignalsResponse(null, 0, List.of());
        }

        List<TradingSignal> signals =
                tradingSignalRepository.findBySignalDateOrderByTickerAscIdAsc(latestSignalDate);

        List<SignalItemResponse> items = signals.stream()
                .map(SignalItemResponse::from)
                .toList();

        return new LatestSignalsResponse(
                latestSignalDate,
                items.size(),
                items
        );
    }

    public StockSignalHistoryResponse getStockSignalHistory(String ticker, Integer size) {
        String normalizedTicker = normalizeTicker(ticker);

        boolean stockExists = stockRepository.findByTicker(normalizedTicker).isPresent();
        if (!stockExists) {
            throw new CustomException(ErrorCode.STOCK_NOT_FOUND);
        }

        int pageSize = normalizeSize(size);

        List<TradingSignal> signals =
                tradingSignalRepository.findByTickerOrderBySignalDateDescCreatedAtDesc(
                        normalizedTicker,
                        PageRequest.of(0, pageSize)
                );

        List<SignalItemResponse> items = signals.stream()
                .map(SignalItemResponse::from)
                .toList();

        return new StockSignalHistoryResponse(
                normalizedTicker,
                items.size(),
                items
        );
    }

    private String normalizeTicker(String ticker) {
        if (ticker == null || ticker.isBlank()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        return ticker.trim();
    }

    private int normalizeSize(Integer size) {
        if (size == null) {
            return DEFAULT_HISTORY_SIZE;
        }

        if (size <= 0) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        return Math.min(size, MAX_HISTORY_SIZE);
    }
}