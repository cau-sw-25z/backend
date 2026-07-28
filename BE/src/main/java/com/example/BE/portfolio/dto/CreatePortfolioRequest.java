package com.example.BE.portfolio.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreatePortfolioRequest(

        @NotBlank(message = "name은 필수입니다.")
        String name,

        @NotNull(message = "riskLevel은 필수입니다.")
        @Min(value = 1, message = "riskLevel은 1 이상이어야 합니다.")
        @Max(value = 5, message = "riskLevel은 5 이하여야 합니다.")
        Integer riskLevel,

        @NotEmpty(message = "items는 최소 1개 이상이어야 합니다.")
        @Valid
        List<PortfolioItemRequest> items
) {
}