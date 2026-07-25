package com.example.BE.trade.controller;

import com.example.BE.common.response.ApiResponse;
import com.example.BE.trade.dto.CreateTradeRequest;
import com.example.BE.trade.dto.TradeHistoryResponse;
import com.example.BE.trade.dto.TradeResponse;
import com.example.BE.trade.service.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Trade", description = "매수/매도 기록 API")
@RestController
@RequestMapping("/api/portfolio/{portfolioId}/stocks/{ticker}/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @Operation(
            summary = "매수/매도 기록 등록",
            description = "포트폴리오 종목의 거래를 BUY 또는 SELL로 기록하고 보유 수량과 평균 매수가를 갱신합니다. tradeDate를 생략하면 서버 현재 시각을 사용합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<TradeResponse>> createTrade(
            @PathVariable Long portfolioId,
            @PathVariable String ticker,
            @Valid @RequestBody CreateTradeRequest request
    ) {
        TradeResponse response = tradeService.createTrade(portfolioId, ticker, request);
        return ResponseEntity.ok(ApiResponse.success(response, "거래 기록 등록 성공"));
    }

    @Operation(
            summary = "종목별 거래 이력 조회",
            description = "특정 포트폴리오 종목의 거래 이력을 거래일시 최신순으로 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<TradeHistoryResponse>> getTradeHistory(
            @PathVariable Long portfolioId,
            @PathVariable String ticker
    ) {
        TradeHistoryResponse response = tradeService.getTradeHistory(portfolioId, ticker);
        return ResponseEntity.ok(ApiResponse.success(response, "거래 이력 조회 성공"));
    }
}
