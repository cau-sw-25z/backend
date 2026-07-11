package com.example.BE.portfolio.repository;

import com.example.BE.portfolio.entity.PortfolioStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioStockRepository extends JpaRepository<PortfolioStock, Long> {

    List<PortfolioStock> findByPortfolio_IdOrderByIdAsc(Long portfolioId);

    void deleteByPortfolio_Id(Long portfolioId);
}
