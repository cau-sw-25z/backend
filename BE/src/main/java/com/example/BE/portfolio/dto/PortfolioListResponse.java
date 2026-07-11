package com.example.BE.portfolio.dto;

import java.util.List;

public record PortfolioListResponse(
        int count,
        List<PortfolioSummaryResponse> portfolios
) {
}
