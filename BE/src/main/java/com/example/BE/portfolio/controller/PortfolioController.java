package com.example.BE.portfolio.controller;

import com.example.BE.common.response.ApiResponse;
import com.example.BE.portfolio.dto.AddPortfolioStockRequest;
import com.example.BE.portfolio.dto.CreatePortfolioRequest;
import com.example.BE.portfolio.dto.PortfolioDetailResponse;
import com.example.BE.portfolio.dto.PortfolioListResponse;
import com.example.BE.portfolio.dto.UpdatePortfolioRequest;
import com.example.BE.portfolio.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Portfolio", description = "포트폴리오 관리 API")
@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Operation(
            summary = "포트폴리오 생성",
            description = "포트폴리오 이름과 종목 목록으로 새 포트폴리오를 생성합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<PortfolioDetailResponse>> createPortfolio(
            @Valid @RequestBody CreatePortfolioRequest request
    ) {
        PortfolioDetailResponse response = portfolioService.createPortfolio(request);
        return ResponseEntity.ok(ApiResponse.success(response, "포트폴리오 생성 성공"));
    }

    @Operation(
            summary = "내 포트폴리오 목록 조회",
            description = "로그인한 사용자의 포트폴리오 목록을 조회합니다. 포트폴리오별 종목 수와 총 평가금액을 포함합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<PortfolioListResponse>> getMyPortfolios() {
        PortfolioListResponse response = portfolioService.getMyPortfolios();
        return ResponseEntity.ok(ApiResponse.success(response, "포트폴리오 목록 조회 성공"));
    }

    @Operation(
            summary = "포트폴리오 상세 조회",
            description = "포트폴리오에 포함된 종목 목록, 현재가, 비중 정보를 조회합니다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PortfolioDetailResponse>> getPortfolioDetail(
            @PathVariable Long id
    ) {
        PortfolioDetailResponse response = portfolioService.getPortfolioDetail(id);
        return ResponseEntity.ok(ApiResponse.success(response, "포트폴리오 상세 조회 성공"));
    }

    @Operation(
            summary = "포트폴리오 이름 수정",
            description = "포트폴리오 이름만 수정합니다. 종목 추가/삭제는 별도 API를 사용합니다."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PortfolioDetailResponse>> updatePortfolio(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePortfolioRequest request
    ) {
        PortfolioDetailResponse response = portfolioService.updatePortfolio(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "포트폴리오 수정 성공"));
    }

    @Operation(
            summary = "포트폴리오 삭제",
            description = "로그인한 사용자의 포트폴리오와 포함된 종목 정보를 삭제합니다."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePortfolio(
            @PathVariable Long id
    ) {
        portfolioService.deletePortfolio(id);
        return ResponseEntity.ok(ApiResponse.success(null, "포트폴리오 삭제 성공"));
    }

    @Operation(
            summary = "포트폴리오 종목 추가",
            description = "로그인한 사용자의 특정 포트폴리오에 ticker 기준으로 종목 1개를 추가합니다."
    )
    @PostMapping("/{portfolioId}/stocks")
    public ResponseEntity<ApiResponse<PortfolioDetailResponse>> addStockToPortfolio(
            @PathVariable Long portfolioId,
            @Valid @RequestBody AddPortfolioStockRequest request
    ) {
        PortfolioDetailResponse response = portfolioService.addStockToPortfolio(portfolioId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "포트폴리오 종목 추가 성공"));
    }

    @Operation(
            summary = "포트폴리오 종목 삭제",
            description = "로그인한 사용자의 특정 포트폴리오에서 ticker 기준으로 종목 1개를 삭제합니다."
    )
    @DeleteMapping("/{portfolioId}/stocks/{ticker}")
    public ResponseEntity<ApiResponse<PortfolioDetailResponse>> deleteStockFromPortfolio(
            @PathVariable Long portfolioId,
            @PathVariable String ticker
    ) {
        PortfolioDetailResponse response = portfolioService.deleteStockFromPortfolio(portfolioId, ticker);
        return ResponseEntity.ok(ApiResponse.success(response, "포트폴리오 종목 삭제 성공"));
    }
}