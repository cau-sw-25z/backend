package com.example.BE.portfolio.service;

import com.example.BE.common.exception.CustomException;
import com.example.BE.common.exception.ErrorCode;
import com.example.BE.entity.User;
import com.example.BE.portfolio.dto.CreatePortfolioRequest;
import com.example.BE.portfolio.dto.PortfolioDetailResponse;
import com.example.BE.portfolio.dto.PortfolioListResponse;
import com.example.BE.portfolio.dto.PortfolioSummaryResponse;
import com.example.BE.portfolio.dto.UpdatePortfolioRequest;
import com.example.BE.portfolio.entity.Portfolio;
import com.example.BE.portfolio.entity.PortfolioStock;
import com.example.BE.portfolio.repository.PortfolioRepository;
import com.example.BE.portfolio.repository.PortfolioStockRepository;
import com.example.BE.repository.UserRepository;
import com.example.BE.stock.entity.PriceHistory;
import com.example.BE.stock.entity.Stock;
import com.example.BE.stock.repository.PriceHistoryRepository;
import com.example.BE.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioStockRepository portfolioStockRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    @Transactional
    public PortfolioDetailResponse createPortfolio(CreatePortfolioRequest request) {
        Long userId = getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Stock> stocks = resolveStocks(request.tickers());

        Portfolio portfolio = portfolioRepository.save(new Portfolio(user, request.name().trim()));

        List<PortfolioStock> portfolioStocks = stocks.stream()
                .map(stock -> new PortfolioStock(portfolio, stock))
                .toList();
        portfolioStockRepository.saveAll(portfolioStocks);

        return toDetailResponse(portfolio, portfolioStocks);
    }

    public PortfolioListResponse getMyPortfolios() {
        Long userId = getCurrentUserId();

        List<Portfolio> portfolios = portfolioRepository.findByUser_IdOrderByIdDesc(userId);

        List<PortfolioSummaryResponse> items = portfolios.stream()
                .map(portfolio -> {
                    List<PortfolioStock> portfolioStocks =
                            portfolioStockRepository.findByPortfolio_IdOrderByIdAsc(portfolio.getId());
                    BigDecimal totalValuation = calculateTotalValuation(portfolioStocks);

                    return new PortfolioSummaryResponse(
                            portfolio.getId(),
                            portfolio.getName(),
                            portfolioStocks.size(),
                            totalValuation,
                            portfolio.getCreatedAt()
                    );
                })
                .toList();

        return new PortfolioListResponse(items.size(), items);
    }

    public PortfolioDetailResponse getPortfolioDetail(Long portfolioId) {
        Long userId = getCurrentUserId();
        Portfolio portfolio = getOwnedPortfolio(portfolioId, userId);

        List<PortfolioStock> portfolioStocks =
                portfolioStockRepository.findByPortfolio_IdOrderByIdAsc(portfolioId);

        return toDetailResponse(portfolio, portfolioStocks);
    }

    @Transactional
    public PortfolioDetailResponse updatePortfolio(Long portfolioId, UpdatePortfolioRequest request) {
        Long userId = getCurrentUserId();
        Portfolio portfolio = getOwnedPortfolio(portfolioId, userId);

        portfolio.rename(request.name().trim());

        List<Stock> stocks = resolveStocks(request.tickers());

        portfolioStockRepository.deleteByPortfolio_Id(portfolioId);

        List<PortfolioStock> portfolioStocks = stocks.stream()
                .map(stock -> new PortfolioStock(portfolio, stock))
                .toList();
        portfolioStockRepository.saveAll(portfolioStocks);

        return toDetailResponse(portfolio, portfolioStocks);
    }

    @Transactional
    public void deletePortfolio(Long portfolioId) {
        Long userId = getCurrentUserId();
        Portfolio portfolio = getOwnedPortfolio(portfolioId, userId);

        portfolioStockRepository.deleteByPortfolio_Id(portfolioId);
        portfolioRepository.delete(portfolio);
    }

    // ─────────────────────────────────────────────────────
    // 헬퍼
    // ─────────────────────────────────────────────────────

    private Portfolio getOwnedPortfolio(Long portfolioId, Long userId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new CustomException(ErrorCode.PORTFOLIO_NOT_FOUND));

        if (!portfolio.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.PORTFOLIO_FORBIDDEN);
        }

        return portfolio;
    }

    private List<Stock> resolveStocks(List<String> tickers) {
        Set<String> normalizedTickers = new LinkedHashSet<>();
        for (String ticker : tickers) {
            if (ticker == null || ticker.isBlank()) {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }
            normalizedTickers.add(ticker.trim());
        }

        return normalizedTickers.stream()
                .map(ticker -> stockRepository.findByTicker(ticker)
                        .orElseThrow(() -> new CustomException(ErrorCode.STOCK_NOT_FOUND)))
                .toList();
    }

    private PortfolioDetailResponse toDetailResponse(Portfolio portfolio, List<PortfolioStock> portfolioStocks) {
        BigDecimal totalValuation = calculateTotalValuation(portfolioStocks);

        List<PortfolioDetailResponse.PortfolioStockItem> items = portfolioStocks.stream()
                .map(portfolioStock -> {
                    Stock stock = portfolioStock.getStock();
                    BigDecimal currentPrice = getCurrentPrice(stock.getId());
                    BigDecimal weightPercent = calculateWeightPercent(currentPrice, totalValuation);

                    return new PortfolioDetailResponse.PortfolioStockItem(
                            stock.getId(),
                            stock.getTicker(),
                            stock.getName(),
                            stock.getMarket(),
                            currentPrice,
                            weightPercent
                    );
                })
                .toList();

        return new PortfolioDetailResponse(
                portfolio.getId(),
                portfolio.getName(),
                portfolioStocks.size(),
                totalValuation,
                portfolio.getCreatedAt(),
                items
        );
    }

    private BigDecimal calculateTotalValuation(List<PortfolioStock> portfolioStocks) {
        return portfolioStocks.stream()
                .map(portfolioStock -> getCurrentPrice(portfolioStock.getStock().getId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateWeightPercent(BigDecimal currentPrice, BigDecimal totalValuation) {
        if (totalValuation.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return currentPrice
                .divide(totalValuation, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getCurrentPrice(Long stockId) {
        List<PriceHistory> recent = priceHistoryRepository
                .findByStockIdOrderByDateDesc(stockId, PageRequest.of(0, 1));

        return recent.isEmpty() ? BigDecimal.ZERO : recent.get(0).getClosePrice();
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
}
