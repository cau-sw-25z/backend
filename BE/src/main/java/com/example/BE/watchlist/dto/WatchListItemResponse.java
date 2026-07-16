package com.example.BE.watchlist.dto;

import com.example.BE.watchlist.entity.WatchList;

public record WatchListItemResponse(
        Long watchListId,
        Long stockId,
        String ticker,
        String name,
        String market
) {
    public static WatchListItemResponse from(WatchList watchList) {
        return new WatchListItemResponse(
                watchList.getId(),
                watchList.getStock().getId(),
                watchList.getStock().getTicker(),
                watchList.getStock().getName(),
                watchList.getStock().getMarket()
        );
    }
}