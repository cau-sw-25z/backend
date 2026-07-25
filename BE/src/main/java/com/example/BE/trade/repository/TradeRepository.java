package com.example.BE.trade.repository;

import com.example.BE.trade.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByPortfolioItem_IdOrderByTradeDateDescIdDesc(Long portfolioItemId);

    @Modifying
    @Query("delete from Trade t where t.portfolioItem.id = :portfolioItemId")
    void deleteAllByPortfolioItemId(@Param("portfolioItemId") Long portfolioItemId);

    @Modifying
    @Query("delete from Trade t where t.portfolioItem.portfolio.id = :portfolioId")
    void deleteAllByPortfolioId(@Param("portfolioId") Long portfolioId);
}
