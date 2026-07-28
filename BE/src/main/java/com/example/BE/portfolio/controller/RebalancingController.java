package com.example.BE.portfolio.controller;

import com.example.BE.common.response.ApiResponse;
import com.example.BE.portfolio.dto.RebalancingResponse;
import com.example.BE.portfolio.service.RebalancingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Rebalancing",
        description = "포트폴리오 추천 비중 및 리밸런싱 조회 API"
)
@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class RebalancingController {

    private final RebalancingService rebalancingService;

    @Operation(
            summary = "포트폴리오 리밸런싱 조회",
            description = """
                    저장된 추천 비중과 현재 평가금액 기준 비중을 비교합니다.
                    조회 시 추천 비중을 재계산하지 않습니다.
                    riskLevel은 1이 공격형, 5가 안정형입니다.
                    비중 단위는 0부터 1까지입니다.
                    """
    )
    @GetMapping("/{portfolioId}/rebalancing")
    public ResponseEntity<ApiResponse<RebalancingResponse>> getRebalancing(
            @PathVariable Long portfolioId
    ) {
        RebalancingResponse response =
                rebalancingService.getRebalancing(portfolioId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "포트폴리오 리밸런싱 조회 성공"
                )
        );
    }
}