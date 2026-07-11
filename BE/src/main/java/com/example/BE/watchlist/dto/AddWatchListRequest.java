package com.example.BE.watchlist.dto;

import jakarta.validation.constraints.NotBlank;

public record AddWatchListRequest(

        @NotBlank(message = "ticker는 필수입니다.")
        String ticker
) {
}