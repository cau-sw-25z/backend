package com.example.BE.trade.service;

import com.example.BE.common.exception.CustomException;
import com.example.BE.common.exception.ErrorCode;
import com.example.BE.portfolio.entity.Portfolio;
import com.example.BE.portfolio.entity.PortfolioItem;
import com.example.BE.portfolio.repository.PortfolioItemRepository;
import com.example.BE.portfolio.repository.PortfolioRepository;
import com.example.BE.trade.dto.CreateTradeRequest;
import com.example.BE.trade.dto.TradeHistoryResponse;
import com.example.BE.trade.dto.TradeResponse;
import com.example.BE.trade.entity.Trade;
import com.example.BE.trade.entity.TradeType;
import com.example.BE.trade.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeService {

    private final TradeRepository tradeRepository;
    private final PortfolioRepository portfolioRepository;
    private final PortfolioItemRepository portfolioItemRepository;

    @Transactional
    public TradeResponse createTrade(
            Long portfolioId,
            String ticker,
            CreateTradeRequest request
    ) {
        Long userId = getCurrentUserId();
        Portfolio portfolio = getOwnedPortfolio(portfolioId, userId);
        PortfolioItem portfolioItem = getPortfolioItem(portfolio.getId(), ticker);

        if (request.tradeType() == TradeType.BUY) {
            portfolioItem.applyBuy(request.price(), request.quantity());
        } else {
            if (request.quantity() > portfolioItem.getQuantity()) {
                throw new CustomException(ErrorCode.TRADE_INSUFFICIENT_QUANTITY);
            }
            portfolioItem.applySell(request.quantity());
        }

        LocalDateTime tradeDate = request.tradeDate() != null
                ? request.tradeDate()
                : LocalDateTime.now();

        Trade trade = tradeRepository.save(
                new Trade(
                        portfolioItem,
                        request.tradeType(),
                        request.price(),
                        request.quantity(),
                        tradeDate
                )
        );

        return TradeResponse.from(trade);
    }

    public TradeHistoryResponse getTradeHistory(Long portfolioId, String ticker) {
        Long userId = getCurrentUserId();
        Portfolio portfolio = getOwnedPortfolio(portfolioId, userId);
        PortfolioItem portfolioItem = getPortfolioItem(portfolio.getId(), ticker);

        List<Trade> trades = tradeRepository
                .findByPortfolioItem_IdOrderByTradeDateDescIdDesc(portfolioItem.getId());

        return TradeHistoryResponse.of(portfolioItem, trades);
    }

    private Portfolio getOwnedPortfolio(Long portfolioId, Long userId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new CustomException(ErrorCode.PORTFOLIO_NOT_FOUND));

        if (!portfolio.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.PORTFOLIO_FORBIDDEN);
        }

        return portfolio;
    }

    private PortfolioItem getPortfolioItem(Long portfolioId, String ticker) {
        String normalizedTicker = normalizeTicker(ticker);

        return portfolioItemRepository
                .findByPortfolio_IdAndStock_Ticker(portfolioId, normalizedTicker)
                .orElseThrow(() -> new CustomException(ErrorCode.PORTFOLIO_ITEM_NOT_FOUND));
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
