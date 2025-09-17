package com.portfolio.repository;

import com.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByUserId(Long userId);

    @Query("SELECT p FROM Portfolio p WHERE p.user.id = :userId AND p.id = :portfolioId")
    Optional<Portfolio> findByIdAndUserId(@Param("portfolioId") Long portfolioId, @Param("userId") Long userId);

    @Query("SELECT p FROM Portfolio p LEFT JOIN FETCH p.assets WHERE p.id = :id")
    Optional<Portfolio> findByIdWithAssets(@Param("id") Long id);
}
