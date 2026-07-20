package com.example.BE.portfolio.repository;

import com.example.BE.portfolio.entity.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {

    List<PortfolioItem> findByPortfolio_IdOrderByIdAsc(Long portfolioId);

    void deleteByPortfolio_Id(Long portfolioId);
}