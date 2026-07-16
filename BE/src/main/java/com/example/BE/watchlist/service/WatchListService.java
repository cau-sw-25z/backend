package com.example.BE.watchlist.service;

import com.example.BE.common.exception.CustomException;
import com.example.BE.common.exception.ErrorCode;
import com.example.BE.entity.User;
import com.example.BE.repository.UserRepository;
import com.example.BE.stock.entity.Stock;
import com.example.BE.stock.repository.StockRepository;
import com.example.BE.watchlist.dto.AddWatchListRequest;
import com.example.BE.watchlist.dto.WatchListItemResponse;
import com.example.BE.watchlist.dto.WatchListResponse;
import com.example.BE.watchlist.entity.WatchList;
import com.example.BE.watchlist.repository.WatchListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WatchListService {

    private final WatchListRepository watchListRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    public WatchListResponse getMyWatchList() {
        Long userId = getCurrentUserId();

        List<WatchListItemResponse> items = watchListRepository
                .findByUser_IdOrderByIdDesc(userId)
                .stream()
                .map(WatchListItemResponse::from)
                .toList();

        return new WatchListResponse(items.size(), items);
    }

    @Transactional
    public WatchListItemResponse addWatchList(AddWatchListRequest request) {
        Long userId = getCurrentUserId();
        String ticker = normalizeTicker(request.ticker());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Stock stock = stockRepository.findByTicker(ticker)
                .orElseThrow(() -> new CustomException(ErrorCode.STOCK_NOT_FOUND));

        if (watchListRepository.existsByUser_IdAndStock_Id(userId, stock.getId())) {
            throw new CustomException(ErrorCode.WATCHLIST_ALREADY_EXISTS);
        }

        WatchList watchList = watchListRepository.save(new WatchList(user, stock));

        return WatchListItemResponse.from(watchList);
    }

    @Transactional
    public void deleteWatchList(String ticker) {
        Long userId = getCurrentUserId();
        String normalizedTicker = normalizeTicker(ticker);

        WatchList watchList = watchListRepository
                .findByUser_IdAndStock_Ticker(userId, normalizedTicker)
                .orElseThrow(() -> new CustomException(ErrorCode.WATCHLIST_NOT_FOUND));

        watchListRepository.delete(watchList);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getDetails() == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Object details = authentication.getDetails();

        if (details instanceof Long userId) {
            return userId;
        }

        throw new CustomException(ErrorCode.UNAUTHORIZED);
    }

    private String normalizeTicker(String ticker) {
        if (ticker == null || ticker.isBlank()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        return ticker.trim();
    }
}