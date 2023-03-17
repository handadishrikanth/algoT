package com.exp.algot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exp.algot.entity.StockData;

@Repository
public interface StockDataDao extends JpaRepository<StockData, Long> {
	
}
