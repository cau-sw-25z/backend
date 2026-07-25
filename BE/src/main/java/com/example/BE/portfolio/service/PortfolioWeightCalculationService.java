package com.example.BE.portfolio.service;

import com.example.BE.common.exception.CustomException;
import com.example.BE.common.exception.ErrorCode;
import com.example.BE.portfolio.entity.Portfolio;
import com.example.BE.portfolio.entity.PortfolioItem;
import com.example.BE.portfolio.entity.PortfolioWeight;
import com.example.BE.portfolio.repository.PortfolioWeightRepository;
import com.example.BE.stock.entity.StockMetric;
import com.example.BE.stock.repository.StockMetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioWeightCalculationService {

    private static final int MIN_RISK_LEVEL = 1;
    private static final int MAX_RISK_LEVEL = 5;
    private static final int WEIGHT_SCALE = 6;

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final BigDecimal FOUR = BigDecimal.valueOf(4);

    private final PortfolioWeightRepository portfolioWeightRepository;
    private final StockMetricRepository stockMetricRepository;

    /**
     * 확정 컨벤션
     *
     * riskLevel = 1: 공격형
     * riskLevel = 5: 안정형
     *
     * alpha = (5 - riskLevel) / 4
     *
     * alpha = 1: 균등 비중
     * alpha = 0: 역변동성 비중
     */
    @Transactional
    public List<CalculatedWeight> calculateAndSave(
            Portfolio portfolio,
            List<PortfolioItem> portfolioItems
    ) {
        validateRiskLevel(portfolio.getRiskLevel());

        portfolioWeightRepository.deleteAllByPortfolioId(portfolio.getId());

        if (portfolioItems.isEmpty()) {
            return List.of();
        }

        LocalDateTime calculatedAt = LocalDateTime.now();

        if (portfolioItems.size() == 1) {
            PortfolioItem portfolioItem = portfolioItems.get(0);

            BigDecimal weight = ONE.setScale(WEIGHT_SCALE, RoundingMode.HALF_UP);

            portfolioWeightRepository.save(
                    new PortfolioWeight(
                            portfolioItem,
                            weight,
                            portfolio.getRiskLevel(),
                            calculatedAt
                    )
            );

            return List.of(
                    toCalculatedWeight(
                            portfolioItem,
                            weight,
                            portfolio.getRiskLevel(),
                            calculatedAt
                    )
            );
        }

        List<BigDecimal> inverseVolatilities = new ArrayList<>();

        for (PortfolioItem portfolioItem : portfolioItems) {
            StockMetric stockMetric = stockMetricRepository
                    .findFirstByStock_IdAndAnnualVolatilityIsNotNullAndAnnualVolatilityGreaterThanOrderByDateDesc(
                            portfolioItem.getStock().getId(),
                            0.0
                    )
                    .orElseThrow(() ->
                            new CustomException(ErrorCode.STOCK_VOLATILITY_NOT_FOUND)
                    );

            BigDecimal volatility = BigDecimal.valueOf(
                    stockMetric.getAnnualVolatility()
            );

            BigDecimal inverseVolatility = ONE.divide(
                    volatility,
                    16,
                    RoundingMode.HALF_UP
            );

            inverseVolatilities.add(inverseVolatility);
        }

        BigDecimal inverseVolatilitySum = inverseVolatilities.stream()
                .reduce(ZERO, BigDecimal::add);

        BigDecimal equalWeight = ONE.divide(
                BigDecimal.valueOf(portfolioItems.size()),
                16,
                RoundingMode.HALF_UP
        );

        BigDecimal alpha = BigDecimal.valueOf(
                        MAX_RISK_LEVEL - portfolio.getRiskLevel()
                )
                .divide(FOUR, 16, RoundingMode.HALF_UP);

        BigDecimal inverseVolatilityRatio = ONE.subtract(alpha);

        List<BigDecimal> calculatedWeights = new ArrayList<>();

        for (int i = 0; i < portfolioItems.size(); i++) {
            BigDecimal inverseVolatilityWeight = inverseVolatilities.get(i)
                    .divide(
                            inverseVolatilitySum,
                            16,
                            RoundingMode.HALF_UP
                    );

            BigDecimal blendedWeight = inverseVolatilityWeight
                    .multiply(inverseVolatilityRatio)
                    .add(equalWeight.multiply(alpha));

            calculatedWeights.add(
                    blendedWeight.setScale(
                            WEIGHT_SCALE,
                            RoundingMode.HALF_UP
                    )
            );
        }

        correctWeightSum(calculatedWeights);

        List<PortfolioWeight> entities = new ArrayList<>();
        List<CalculatedWeight> result = new ArrayList<>();

        for (int i = 0; i < portfolioItems.size(); i++) {
            PortfolioItem portfolioItem = portfolioItems.get(i);
            BigDecimal weight = calculatedWeights.get(i);

            entities.add(
                    new PortfolioWeight(
                            portfolioItem,
                            weight,
                            portfolio.getRiskLevel(),
                            calculatedAt
                    )
            );

            result.add(
                    toCalculatedWeight(
                            portfolioItem,
                            weight,
                            portfolio.getRiskLevel(),
                            calculatedAt
                    )
            );
        }

        portfolioWeightRepository.saveAll(entities);

        validateWeightSum(calculatedWeights);

        return result;
    }

    private void correctWeightSum(List<BigDecimal> weights) {
        int lastIndex = weights.size() - 1;

        BigDecimal sumExceptLast = ZERO;

        for (int i = 0; i < lastIndex; i++) {
            sumExceptLast = sumExceptLast.add(weights.get(i));
        }

        BigDecimal correctedLastWeight = ONE
                .subtract(sumExceptLast)
                .setScale(WEIGHT_SCALE, RoundingMode.HALF_UP);

        if (correctedLastWeight.compareTo(ZERO) < 0) {
            throw new CustomException(ErrorCode.PORTFOLIO_WEIGHT_CALCULATION_FAILED);
        }

        weights.set(lastIndex, correctedLastWeight);
    }

    private void validateWeightSum(List<BigDecimal> weights) {
        BigDecimal sum = weights.stream()
                .reduce(ZERO, BigDecimal::add)
                .setScale(WEIGHT_SCALE, RoundingMode.HALF_UP);

        BigDecimal expected = ONE.setScale(
                WEIGHT_SCALE,
                RoundingMode.HALF_UP
        );

        if (sum.compareTo(expected) != 0) {
            throw new CustomException(
                    ErrorCode.PORTFOLIO_WEIGHT_CALCULATION_FAILED
            );
        }
    }

    private void validateRiskLevel(Integer riskLevel) {
        if (riskLevel == null
                || riskLevel < MIN_RISK_LEVEL
                || riskLevel > MAX_RISK_LEVEL) {
            throw new CustomException(ErrorCode.INVALID_RISK_LEVEL);
        }
    }

    private CalculatedWeight toCalculatedWeight(
            PortfolioItem portfolioItem,
            BigDecimal weight,
            Integer riskLevel,
            LocalDateTime calculatedAt
    ) {
        return new CalculatedWeight(
                portfolioItem.getId(),
                portfolioItem.getStock().getTicker(),
                weight,
                riskLevel,
                calculatedAt
        );
    }

    public record CalculatedWeight(
            Long portfolioItemId,
            String ticker,
            BigDecimal weight,
            Integer riskLevel,
            LocalDateTime calculatedAt
    ) {
    }
}