package com.example.BE.watchlist.controller;

import com.example.BE.common.response.ApiResponse;
import com.example.BE.watchlist.dto.AddWatchListRequest;
import com.example.BE.watchlist.dto.WatchListItemResponse;
import com.example.BE.watchlist.dto.WatchListResponse;
import com.example.BE.watchlist.service.WatchListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "WatchList", description = "관심종목 API")
@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchListController {

    private final WatchListService watchListService;

    @Operation(
            summary = "내 관심종목 목록 조회",
            description = "로그인한 사용자의 관심종목 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<WatchListResponse>> getMyWatchList() {
        WatchListResponse response = watchListService.getMyWatchList();
        return ResponseEntity.ok(ApiResponse.success(response, "관심종목 목록 조회 성공"));
    }

    @Operation(
            summary = "관심종목 추가",
            description = "ticker를 기준으로 관심종목을 추가합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<WatchListItemResponse>> addWatchList(
            @Valid @RequestBody AddWatchListRequest request
    ) {
        WatchListItemResponse response = watchListService.addWatchList(request);
        return ResponseEntity.ok(ApiResponse.success(response, "관심종목 추가 성공"));
    }

    @Operation(
            summary = "관심종목 삭제",
            description = "ticker를 기준으로 관심종목을 삭제합니다."
    )
    @DeleteMapping("/{ticker}")
    public ResponseEntity<ApiResponse<Void>> deleteWatchList(
            @PathVariable String ticker
    ) {
        watchListService.deleteWatchList(ticker);
        return ResponseEntity.ok(ApiResponse.success(null, "관심종목 삭제 성공"));
    }
}