package com.example.BE.portfolio.repository;

import com.example.BE.portfolio.entity.PortfolioWeight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PortfolioWeightRepository
        extends JpaRepository<PortfolioWeight, Long> {

    List<PortfolioWeight>
    findByPortfolioItem_Portfolio_IdOrderByPortfolioItem_IdAsc(
            Long portfolioId
    );

    @Modifying(
            flushAutomatically = true
    )
    @Query("""
            delete from PortfolioWeight pw
            where pw.portfolioItem.portfolio.id = :portfolioId
            """)
    void deleteAllByPortfolioId(
            @Param("portfolioId") Long portfolioId
    );
}