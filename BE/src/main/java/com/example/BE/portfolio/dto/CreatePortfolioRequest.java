package com.example.BE.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreatePortfolioRequest(

        @NotBlank(message = "name은 필수입니다.")
        String name,

        @NotEmpty(message = "tickers는 최소 1개 이상이어야 합니다.")
        List<String> tickers
) {
}
