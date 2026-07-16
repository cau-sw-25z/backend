package com.example.BE.stock.controller;

import com.example.BE.common.response.ApiResponse;
import com.example.BE.stock.dto.PriceHistoryResponse;
import com.example.BE.stock.dto.StockDetailResponse;
import com.example.BE.stock.dto.StockListResponse;
import com.example.BE.stock.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Stock", description = "종목 목록 / 상세 / 가격 이력 조회 API")
@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @Operation(summary = "종목 목록 조회", description = "키워드 검색 + 커서 기반 페이지네이션")
    @GetMapping
    public ResponseEntity<ApiResponse<StockListResponse>> getStocks(
            @Parameter(description = "마지막으로 받은 id (없으면 처음부터)")
            @RequestParam(required = false) Long cursor,

            @Parameter(description = "페이지 크기 (기본 20)")
            @RequestParam(required = false, defaultValue = "20") Integer size,

            @Parameter(description = "종목명 또는 티커 키워드")
            @RequestParam(required = false) String keyword
    ) {
        StockListResponse response = stockService.getStockList(cursor, size, keyword);
        return ResponseEntity.ok(ApiResponse.success(response, "종목 목록 조회 성공"));
    }

    @Operation(summary = "종목 상세 조회", description = "종목 기본 정보 + 최근 30일 가격 이력")
    @GetMapping("/{ticker}")
    public ResponseEntity<ApiResponse<StockDetailResponse>> getStockDetail(
            @PathVariable String ticker) {
        StockDetailResponse response = stockService.getStockDetail(ticker);
        return ResponseEntity.ok(ApiResponse.success(response, "종목 상세 조회 성공"));
    }

    @Operation(summary = "가격 이력 조회", description = "날짜 범위로 가격 이력을 조회합니다.")
    @GetMapping("/{ticker}/prices")
    public ResponseEntity<ApiResponse<PriceHistoryResponse>> getPriceHistory(
            @PathVariable String ticker,

            @Parameter(description = "시작 날짜 (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,

            @Parameter(description = "종료 날짜 (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        PriceHistoryResponse response = stockService.getPriceHistory(ticker, from, to);
        return ResponseEntity.ok(ApiResponse.success(response, "가격 이력 조회 성공"));
    }
}