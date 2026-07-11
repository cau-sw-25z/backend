package com.example.BE.watchlist.dto;

import java.util.List;

public record WatchListResponse(
        int count,
        List<WatchListItemResponse> items
) {
}