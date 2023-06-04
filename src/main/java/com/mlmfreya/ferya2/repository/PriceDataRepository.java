package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.PriceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PriceDataRepository extends JpaRepository<PriceData, Long> {

    @Query("delete from PriceData pd where pd.timestamp < :timestamp")
    void deleteDataOlderThan(@Param("timestamp") LocalDateTime timestamp);

    PriceData findTopBySymbolOrderByTimestampDesc(String symbol);

}
