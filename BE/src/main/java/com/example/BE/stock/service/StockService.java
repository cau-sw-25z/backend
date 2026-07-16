package com.example.BE.stock.service;

import com.example.BE.common.exception.CustomException;
import com.example.BE.common.exception.ErrorCode;
import com.example.BE.stock.dto.PriceHistoryResponse;
import com.example.BE.stock.dto.StockDetailResponse;
import com.example.BE.stock.dto.StockListResponse;
import com.example.BE.stock.entity.PriceHistory;
import com.example.BE.stock.entity.Stock;
import com.example.BE.stock.repository.PriceHistoryRepository;
import com.example.BE.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    private static final int DETAIL_PRICE_DAYS = 30;

    /**
     * 종목 목록 조회 (커서 기반 페이지네이션 + 키워드 검색)
     * @param cursor 마지막으로 받은 id (null이면 처음부터)
     * @param size   페이지 크기 (기본 20)
     * @param keyword 검색어 (null이면 전체)
     */
    public StockListResponse getStockList(Long cursor, Integer size, String keyword) {
        Long cursorId = (cursor == null) ? 0L : cursor;
        int pageSize  = (size == null || size <= 0) ? 20 : size;
        String kw     = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        // size + 1개 조회 → hasNext 판정용
        List<Stock> stocks = stockRepository.findStocksWithCursor(
                cursorId, kw, PageRequest.of(0, pageSize + 1));

        boolean hasNext = stocks.size() > pageSize;
        if (hasNext) {
            stocks = stocks.subList(0, pageSize);
        }

        // 각 종목의 최근 2일치 가격으로 종가, 등락률 계산
        List<StockListResponse.StockItem> items = stocks.stream()
                .map(this::toListItem)
                .toList();

        Long nextCursor = items.isEmpty() ? null
                : (hasNext ? stocks.get(stocks.size() - 1).getId() : null);

        return new StockListResponse(items, nextCursor, hasNext);
    }

    /**
     * 종목 상세 조회 — 기본 정보 + 최근 30일 가격 이력
     */
    public StockDetailResponse getStockDetail(String ticker) {
        Stock stock = stockRepository.findByTicker(ticker)
                .orElseThrow(() -> new CustomException(ErrorCode.STOCK_NOT_FOUND));

        // 최근 30개 조회 (날짜 내림차순)
        List<PriceHistory> recent = priceHistoryRepository.findByStockIdOrderByDateDesc(
                stock.getId(), PageRequest.of(0, DETAIL_PRICE_DAYS));

        // 차트용은 오래된 → 최신 순서가 자연스러우니 뒤집기
        List<StockDetailResponse.PriceItem> priceItems = recent.stream()
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .map(p -> new StockDetailResponse.PriceItem(
                        p.getDate(), p.getOpenPrice(), p.getClosePrice(),
                        p.getHighPrice(), p.getLowPrice(), p.getVolume()))
                .toList();

        return new StockDetailResponse(
                stock.getId(), stock.getTicker(), stock.getName(),
                stock.getMarket(), priceItems);
    }

    /**
     * 가격 이력 조회 (날짜 범위)
     */
    public PriceHistoryResponse getPriceHistory(String ticker, LocalDate from, LocalDate to) {
        Stock stock = stockRepository.findByTicker(ticker)
                .orElseThrow(() -> new CustomException(ErrorCode.STOCK_NOT_FOUND));

        if (from == null || to == null || from.isAfter(to)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        List<PriceHistory> prices = priceHistoryRepository
                .findByStockIdAndDateBetweenOrderByDateAsc(stock.getId(), from, to);

        List<PriceHistoryResponse.PriceItem> items = prices.stream()
                .map(p -> new PriceHistoryResponse.PriceItem(
                        p.getDate(), p.getOpenPrice(), p.getClosePrice(),
                        p.getHighPrice(), p.getLowPrice(), p.getVolume()))
                .toList();

        return new PriceHistoryResponse(ticker, from, to, items);
    }

    // ─────────────────────────────────────────────────────
    // 헬퍼: 종목 1개의 최근 2일 가격으로 종가, 등락률 계산
    // ─────────────────────────────────────────────────────
    private StockListResponse.StockItem toListItem(Stock stock) {
        List<PriceHistory> recent2 = priceHistoryRepository
                .findByStockIdOrderByDateDesc(stock.getId(), PageRequest.of(0, 2));

        BigDecimal closePrice = null;
        BigDecimal changeRate = null;

        if (!recent2.isEmpty()) {
            closePrice = recent2.get(0).getClosePrice();
            if (recent2.size() == 2) {
                BigDecimal prev = recent2.get(1).getClosePrice();
                if (prev.compareTo(BigDecimal.ZERO) != 0) {
                    // ((오늘 - 전일) / 전일) * 100, 소수 둘째 자리까지
                    changeRate = closePrice.subtract(prev)
                            .divide(prev, 6, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(2, RoundingMode.HALF_UP);
                }
            }
        }

        return new StockListResponse.StockItem(
                stock.getId(), stock.getTicker(), stock.getName(),
                stock.getMarket(), closePrice, changeRate);
    }
}