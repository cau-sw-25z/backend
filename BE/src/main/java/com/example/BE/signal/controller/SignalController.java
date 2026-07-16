package com.example.BE.signal.controller;

import com.example.BE.common.response.ApiResponse;
import com.example.BE.signal.dto.LatestSignalsResponse;
import com.example.BE.signal.dto.StockSignalHistoryResponse;
import com.example.BE.signal.service.SignalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Signal", description = "매매 시그널 조회 API")
@RestController
@RequiredArgsConstructor
public class SignalController {

    private final SignalService signalService;

    @Operation(
            summary = "최신 매매 시그널 전체 조회",
            description = "trading_signals 테이블에서 가장 최신 signal_date 기준 전체 시그널을 조회합니다."
    )
    @GetMapping("/api/signals")
    public ResponseEntity<ApiResponse<LatestSignalsResponse>> getLatestSignals() {
        LatestSignalsResponse response = signalService.getLatestSignals();
        return ResponseEntity.ok(ApiResponse.success(response, "최신 매매 시그널 조회 성공"));
    }

    @Operation(
            summary = "종목별 매매 시그널 히스토리 조회",
            description = "특정 종목의 매매 시그널 히스토리를 최신순으로 조회합니다."
    )
    @GetMapping("/api/stocks/{ticker}/signals")
    public ResponseEntity<ApiResponse<StockSignalHistoryResponse>> getStockSignalHistory(
            @Parameter(description = "종목 티커", example = "005930")
            @PathVariable String ticker,

            @Parameter(description = "조회 개수, 기본 50, 최대 200")
            @RequestParam(required = false) Integer size
    ) {
        StockSignalHistoryResponse response = signalService.getStockSignalHistory(ticker, size);
        return ResponseEntity.ok(ApiResponse.success(response, "종목별 매매 시그널 조회 성공"));
    }
}