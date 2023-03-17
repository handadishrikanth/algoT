package com.exp.algot.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.exp.algot.entity.StockDataLoadStat;
import com.exp.algot.entity.StockDataLoadStat.LoadStatus;

@Repository
public interface StockDataLoadStatDao extends JpaRepository<StockDataLoadStat, Long> {

	Optional<StockDataLoadStat> findByTradeDate(LocalDate tradeDate);

	List<StockDataLoadStat> findAllTopFiftyByLoadStatusIn(List<LoadStatus> loadStatus);

	List<StockDataLoadStat> findAllTopFiftyByLoadStatusAndTradeDateGreaterThanAndTradeDateLessThanEqual(
			LoadStatus loadStatus, LocalDate start, LocalDate end);

	@Transactional
	@Modifying
	@Query("update StockDataLoadStat s set s.loadStatus = :toStatus where s.loadStatus = :fromStatus and tradeDate >= :startDate and tradeDate <= :endDate")
	int updateStatus(@Param("fromStatus") LoadStatus fromStatus, @Param("toStatus") LoadStatus toStatus,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
