package com.exp.algot.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "StockDataLoadStat")
public class StockDataLoadStat {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long stockDataLoadStatId;

	@Column(unique = true)
	private LocalDate tradeDate;

	private String fileName;

	@Enumerated(EnumType.STRING)
	private LoadStatus loadStatus;

	private String statusMessage;

	public static enum LoadStatus {
		NEW, DOWNLOAD_FINSIHED, DOWNLOAD_ERROR, DB_LOAD_FINISHED, DB_LOAD_ERROR;
	}

	public Long getStockDataLoadStatId() {
		return stockDataLoadStatId;
	}

	public void setStockDataLoadStatId(Long stockDataLoadStatId) {
		this.stockDataLoadStatId = stockDataLoadStatId;
	}

	public LocalDate getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(LocalDate tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public LoadStatus getLoadStatus() {
		return loadStatus;
	}

	public void setLoadStatus(LoadStatus loadStatus) {
		this.loadStatus = loadStatus;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

}
