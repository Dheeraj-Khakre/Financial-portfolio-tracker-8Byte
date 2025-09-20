package com.portfolio.repository;

import com.portfolio.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByPortfolioId(Long portfolioId);

    @Query("SELECT a FROM Asset a WHERE a.portfolio.id = :portfolioId AND a.tickerSymbol = :tickerSymbol")
    Optional<Asset> findByPortfolioIdAndTickerSymbol(@Param("portfolioId") Long portfolioId,
                                                     @Param("tickerSymbol") String tickerSymbol);

    @Query("SELECT a FROM Asset a WHERE a.portfolio.user.id = :userId")
    List<Asset> findByUserId(@Param("userId") Long userId);


    @Query("select coalesce(sum(a.totalValue), 0) from Asset a where a.portfolio.id = :portfolioId")
    BigDecimal sumTotalValue(@Param("portfolioId") Long portfolioId);
}
