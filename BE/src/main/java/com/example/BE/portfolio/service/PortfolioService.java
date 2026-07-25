package com.example.BE.portfolio.service;

import com.example.BE.common.exception.CustomException;
import com.example.BE.common.exception.ErrorCode;
import com.example.BE.entity.User;
import com.example.BE.portfolio.dto.AddPortfolioStockRequest;
import com.example.BE.portfolio.dto.CreatePortfolioRequest;
import com.example.BE.portfolio.dto.PortfolioDetailResponse;
import com.example.BE.portfolio.dto.PortfolioItemRequest;
import com.example.BE.portfolio.dto.PortfolioListResponse;
import com.example.BE.portfolio.dto.PortfolioSummaryResponse;
import com.example.BE.portfolio.dto.UpdatePortfolioRequest;
import com.example.BE.portfolio.entity.Portfolio;
import com.example.BE.portfolio.entity.PortfolioItem;
import com.example.BE.portfolio.repository.PortfolioItemRepository;
import com.example.BE.portfolio.repository.PortfolioRepository;
import com.example.BE.repository.UserRepository;
import com.example.BE.stock.entity.PriceHistory;
import com.example.BE.stock.entity.Stock;
import com.example.BE.stock.repository.PriceHistoryRepository;
import com.example.BE.stock.repository.StockRepository;
import com.example.BE.trade.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final TradeRepository tradeRepository;

    @Transactional
    public PortfolioDetailResponse createPortfolio(CreatePortfolioRequest request) {
        Long userId = getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Portfolio portfolio = portfolioRepository.save(new Portfolio(user, request.name().trim()));

        List<PortfolioItem> portfolioItems = buildPortfolioItems(portfolio, request.items());
        portfolioItemRepository.saveAll(portfolioItems);

        return toDetailResponse(portfolio, portfolioItems);
    }

    public PortfolioListResponse getMyPortfolios() {
        Long userId = getCurrentUserId();

        List<Portfolio> portfolios = portfolioRepository.findByUser_IdOrderByIdDesc(userId);

        List<PortfolioSummaryResponse> items = portfolios.stream()
                .map(portfolio -> {
                    List<PortfolioItem> portfolioItems =
                            portfolioItemRepository.findByPortfolio_IdOrderByIdAsc(portfolio.getId());

                    BigDecimal totalValuation = calculateTotalValuation(portfolioItems);

                    return new PortfolioSummaryResponse(
                            portfolio.getId(),
                            portfolio.getName(),
                            portfolioItems.size(),
                            totalValuation
                    );
                })
                .toList();

        return new PortfolioListResponse(items.size(), items);
    }

    public PortfolioDetailResponse getPortfolioDetail(Long portfolioId) {
        Long userId = getCurrentUserId();

        Portfolio portfolio = getOwnedPortfolio(portfolioId, userId);

        List<PortfolioItem> portfolioItems =
                portfolioItemRepository.findByPortfolio_IdOrderByIdAsc(portfolioId);

        return toDetailResponse(portfolio, portfolioItems);
    }

    @Transactional
    public PortfolioDetailResponse updatePortfolio(Long portfolioId, UpdatePortfolioRequest request) {
        Long userId = getCurrentUserId();

        Portfolio portfolio = getOwnedPortfolio(portfolioId, userId);
        portfolio.rename(request.name().trim());

        List<PortfolioItem> portfolioItems =
                portfolioItemRepository.findByPortfolio_IdOrderByIdAsc(portfolioId);

        return toDetailResponse(portfolio, portfolioItems);
    }

    @Transactional
    public void deletePortfolio(Long portfolioId) {
        Long userId = getCurrentUserId();

        Portfolio portfolio = getOwnedPortfolio(portfolioId, userId);

        tradeRepository.deleteAllByPortfolioId(portfolioId);
        portfolioItemRepository.deleteByPortfolio_Id(portfolioId);
        portfolioItemRepository.flush();

        portfolioRepository.delete(portfolio);
    }

    @Transactional
    public PortfolioDetailResponse addStockToPortfolio(
            Long portfolioId,
            AddPortfolioStockRequest request
    ) {
        Long userId = getCurrentUserId();

        Portfolio portfolio = getOwnedPortfolio(portfolioId, userId);

        String ticker = normalizeTicker(request.ticker());

        Stock stock = stockRepository.findByTicker(ticker)
                .orElseThrow(() -> new CustomException(ErrorCode.STOCK_NOT_FOUND));

        if (portfolioItemRepository.existsByPortfolio_IdAndStock_Id(portfolioId, stock.getId())) {
            throw new CustomException(ErrorCode.PORTFOLIO_ITEM_ALREADY_EXISTS);
        }

        portfolioItemRepository.save(
                new PortfolioItem(
                        portfolio,
                        stock,
                        request.avgPrice(),
                        request.quantity()
                )
        );

        List<PortfolioItem> portfolioItems =
                portfolioItemRepository.findByPortfolio_IdOrderByIdAsc(portfolioId);

        return toDetailResponse(portfolio, portfolioItems);
    }

    @Transactional
    public PortfolioDetailResponse deleteStockFromPortfolio(Long portfolioId, String ticker) {
        Long userId = getCurrentUserId();

        Portfolio portfolio = getOwnedPortfolio(portfolioId, userId);

        String normalizedTicker = normalizeTicker(ticker);

        PortfolioItem portfolioItem = portfolioItemRepository
                .findByPortfolio_IdAndStock_Ticker(portfolioId, normalizedTicker)
                .orElseThrow(() -> new CustomException(ErrorCode.PORTFOLIO_ITEM_NOT_FOUND));

        tradeRepository.deleteAllByPortfolioItemId(portfolioItem.getId());
        portfolioItemRepository.delete(portfolioItem);
        portfolioItemRepository.flush();

        List<PortfolioItem> portfolioItems =
                portfolioItemRepository.findByPortfolio_IdOrderByIdAsc(portfolioId);

        return toDetailResponse(portfolio, portfolioItems);
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

    private List<PortfolioItem> buildPortfolioItems(Portfolio portfolio, List<PortfolioItemRequest> items) {
        return items.stream()
                .map(item -> {
                    Stock stock = stockRepository.findByTicker(normalizeTicker(item.ticker()))
                            .orElseThrow(() -> new CustomException(ErrorCode.STOCK_NOT_FOUND));

                    return new PortfolioItem(
                            portfolio,
                            stock,
                            item.avgPrice(),
                            item.quantity()
                    );
                })
                .toList();
    }

    private PortfolioDetailResponse toDetailResponse(Portfolio portfolio, List<PortfolioItem> portfolioItems) {
        BigDecimal totalValuation = calculateTotalValuation(portfolioItems);

        List<PortfolioDetailResponse.PortfolioItemDetail> items = portfolioItems.stream()
                .map(portfolioItem -> {
                    Stock stock = portfolioItem.getStock();

                    BigDecimal currentPrice = getCurrentPrice(stock.getId());
                    BigDecimal evaluationAmount = currentPrice.multiply(
                            BigDecimal.valueOf(portfolioItem.getQuantity())
                    );
                    BigDecimal weightPercent = calculateWeightPercent(evaluationAmount, totalValuation);

                    return new PortfolioDetailResponse.PortfolioItemDetail(
                            stock.getId(),
                            stock.getTicker(),
                            stock.getName(),
                            stock.getMarket(),
                            portfolioItem.getAvgPrice(),
                            portfolioItem.getQuantity(),
                            currentPrice,
                            weightPercent
                    );
                })
                .toList();

        return new PortfolioDetailResponse(
                portfolio.getId(),
                portfolio.getName(),
                portfolioItems.size(),
                totalValuation,
                items
        );
    }

    private BigDecimal calculateTotalValuation(List<PortfolioItem> portfolioItems) {
        return portfolioItems.stream()
                .map(item -> getCurrentPrice(item.getStock().getId())
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateWeightPercent(BigDecimal evaluationAmount, BigDecimal totalValuation) {
        if (totalValuation.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return evaluationAmount
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

    private String normalizeTicker(String ticker) {
        if (ticker == null || ticker.isBlank()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        return ticker.trim();
    }
}