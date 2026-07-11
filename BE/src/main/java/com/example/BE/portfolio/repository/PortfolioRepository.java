package com.example.BE.portfolio.repository;

import com.example.BE.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findByUser_IdOrderByIdDesc(Long userId);
}
