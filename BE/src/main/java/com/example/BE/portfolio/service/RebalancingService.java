package com.example.BE.portfolio.service;

import com.example.BE.common.exception.CustomException;
import com.example.BE.common.exception.ErrorCode;
import com.example.BE.portfolio.dto.RebalancingResponse;
import com.example.BE.portfolio.dto.RebalancingSuggestion;
import com.example.BE.portfolio.entity.Portfolio;
import com.example.BE.portfolio.entity.PortfolioItem;
import com.example.BE.portfolio.entity.PortfolioWeight;
import com.example.BE.portfolio.repository.PortfolioItemRepository;
import com.example.BE.portfolio.repository.PortfolioRepository;
import com.example.BE.portfolio.repository.PortfolioWeightRepository;
import com.example.BE.signal.entity.TradingSignal;
import com.example.BE.signal.repository.TradingSignalRepository;
import com.example.BE.stock.entity.PriceHistory;
import com.example.BE.stock.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RebalancingService {

    private static final int WEIGHT_SCALE = 6;

    /*
     * 추천 비중과 현재 비중 차이가 1%p 이하라면 HOLD 처리
     */
    private static final BigDecimal HOLD_THRESHOLD =
            new BigDecimal("0.010000");

    private final PortfolioRepository portfolioRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final PortfolioWeightRepository portfolioWeightRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final TradingSignalRepository tradingSignalRepository;

    public RebalancingResponse getRebalancing(Long portfolioId) {
        Long userId = getCurrentUserId();

        Portfolio portfolio = getOwnedPortfolio(portfolioId, userId);

        List<PortfolioItem> portfolioItems =
                portfolioItemRepository.findByPortfolio_IdOrderByIdAsc(
                        portfolioId
                );

        if (portfolioItems.isEmpty()) {
            return new RebalancingResponse(
                    portfolio.getId(),
                    portfolio.getName(),
                    portfolio.getRiskLevel(),
                    null,
                    BigDecimal.ZERO,
                    List.of()
            );
        }

        List<PortfolioWeight> portfolioWeights =
                portfolioWeightRepository
                        .findByPortfolioItem_Portfolio_IdOrderByPortfolioItem_IdAsc(
                                portfolioId
                        );

        if (portfolioWeights.size() != portfolioItems.size()) {
            throw new CustomException(
                    ErrorCode.PORTFOLIO_WEIGHT_NOT_FOUND
            );
        }

        Map<Long, PortfolioWeight> weightByPortfolioItemId =
                portfolioWeights.stream()
                        .collect(
                                Collectors.toMap(
                                        weight -> weight
                                                .getPortfolioItem()
                                                .getId(),
                                        Function.identity()
                                )
                        );

        Map<Long, BigDecimal> currentPriceByPortfolioItemId =
                portfolioItems.stream()
                        .collect(
                                Collectors.toMap(
                                        PortfolioItem::getId,
                                        item -> getCurrentPrice(
                                                item.getStock().getId()
                                        )
                                )
                        );

        BigDecimal totalValuation = portfolioItems.stream()
                .map(item ->
                        currentPriceByPortfolioItemId
                                .get(item.getId())
                                .multiply(
                                        BigDecimal.valueOf(
                                                item.getQuantity()
                                        )
                                )
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<RebalancingResponse.RebalancingItem> items =
                portfolioItems.stream()
                        .map(portfolioItem ->
                                toRebalancingItem(
                                        portfolioItem,
                                        weightByPortfolioItemId
                                                .get(portfolioItem.getId()),
                                        currentPriceByPortfolioItemId
                                                .get(portfolioItem.getId()),
                                        totalValuation
                                )
                        )
                        .toList();

        LocalDateTime calculatedAt = portfolioWeights.stream()
                .map(PortfolioWeight::getCalculatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return new RebalancingResponse(
                portfolio.getId(),
                portfolio.getName(),
                portfolio.getRiskLevel(),
                calculatedAt,
                totalValuation,
                items
        );
    }

    private RebalancingResponse.RebalancingItem toRebalancingItem(
            PortfolioItem portfolioItem,
            PortfolioWeight portfolioWeight,
            BigDecimal currentPrice,
            BigDecimal totalValuation
    ) {
        if (portfolioWeight == null) {
            throw new CustomException(
                    ErrorCode.PORTFOLIO_WEIGHT_NOT_FOUND
            );
        }

        BigDecimal evaluationAmount = currentPrice.multiply(
                BigDecimal.valueOf(portfolioItem.getQuantity())
        );

        BigDecimal currentWeight = calculateCurrentWeight(
                evaluationAmount,
                totalValuation
        );

        BigDecimal recommendedWeight = portfolioWeight
                .getWeight()
                .setScale(
                        WEIGHT_SCALE,
                        RoundingMode.HALF_UP
                );

        BigDecimal weightDifference = recommendedWeight
                .subtract(currentWeight)
                .setScale(
                        WEIGHT_SCALE,
                        RoundingMode.HALF_UP
                );

        RebalancingSuggestion suggestion =
                determineSuggestion(weightDifference);

        RebalancingResponse.SignalInfo signal =
                getLatestSignal(portfolioItem.getStock().getTicker());

        return new RebalancingResponse.RebalancingItem(
                portfolioItem.getId(),
                portfolioItem.getStock().getId(),
                portfolioItem.getStock().getTicker(),
                portfolioItem.getStock().getName(),
                portfolioItem.getStock().getMarket(),
                portfolioItem.getAvgPrice(),
                portfolioItem.getQuantity(),
                currentPrice,
                currentWeight,
                recommendedWeight,
                weightDifference,
                suggestion,
                signal
        );
    }

    private BigDecimal calculateCurrentWeight(
            BigDecimal evaluationAmount,
            BigDecimal totalValuation
    ) {
        if (totalValuation.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(
                    WEIGHT_SCALE,
                    RoundingMode.HALF_UP
            );
        }

        return evaluationAmount.divide(
                totalValuation,
                WEIGHT_SCALE,
                RoundingMode.HALF_UP
        );
    }

    private RebalancingSuggestion determineSuggestion(
            BigDecimal weightDifference
    ) {
        if (weightDifference.compareTo(HOLD_THRESHOLD) > 0) {
            return RebalancingSuggestion.INCREASE;
        }

        if (weightDifference.compareTo(HOLD_THRESHOLD.negate()) < 0) {
            return RebalancingSuggestion.DECREASE;
        }

        return RebalancingSuggestion.HOLD;
    }

    private RebalancingResponse.SignalInfo getLatestSignal(
            String ticker
    ) {
        List<TradingSignal> signals =
                tradingSignalRepository
                        .findByTickerOrderBySignalDateDescCreatedAtDesc(
                                ticker,
                                PageRequest.of(0, 1)
                        );

        if (signals.isEmpty()) {
            return null;
        }

        TradingSignal signal = signals.get(0);

        return new RebalancingResponse.SignalInfo(
                signal.getId(),
                signal.getStrategyType(),
                signal.getAction(),
                signal.getSignalValue(),
                signal.getClosePrice(),
                signal.getSignalDate(),
                signal.getCreatedAt()
        );
    }

    private BigDecimal getCurrentPrice(Long stockId) {
        List<PriceHistory> recent =
                priceHistoryRepository
                        .findByStockIdOrderByDateDesc(
                                stockId,
                                PageRequest.of(0, 1)
                        );

        return recent.isEmpty()
                ? BigDecimal.ZERO
                : recent.get(0).getClosePrice();
    }

    private Portfolio getOwnedPortfolio(
            Long portfolioId,
            Long userId
    ) {
        Portfolio portfolio = portfolioRepository
                .findById(portfolioId)
                .orElseThrow(() ->
                        new CustomException(
                                ErrorCode.PORTFOLIO_NOT_FOUND
                        )
                );

        if (!portfolio.getUser().getId().equals(userId)) {
            throw new CustomException(
                    ErrorCode.PORTFOLIO_FORBIDDEN
            );
        }

        return portfolio;
    }

    private Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || authentication.getDetails() == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Object details = authentication.getDetails();

        if (details instanceof Long userId) {
            return userId;
        }

        throw new CustomException(ErrorCode.UNAUTHORIZED);
    }
}