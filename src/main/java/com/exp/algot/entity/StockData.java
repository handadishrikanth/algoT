package com.exp.algot.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.exp.algot.csv.BigDecimalConverter;
import com.exp.algot.csv.FloatConverter;
import com.exp.algot.csv.LocalDateConverter;
import com.exp.algot.csv.LongConverter;
import com.exp.algot.csv.StringConverter;
import com.opencsv.bean.CsvCustomBindByName;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "StockData")
public class StockData {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long stockDataId;

	@CsvCustomBindByName(column = "SYMBOL", converter = StringConverter.class)
	private String symbol;

	@CsvCustomBindByName(column = "SERIES", converter = StringConverter.class)
	private String series;

	@CsvCustomBindByName(column = "DATE1", converter = LocalDateConverter.class)
	private LocalDate tradeDate;

	@CsvCustomBindByName(column = "PREV_CLOSE", converter = BigDecimalConverter.class)
	private BigDecimal prevClose;

	@CsvCustomBindByName(column = "OPEN_PRICE", converter = BigDecimalConverter.class)
	private BigDecimal openPrice;

	@CsvCustomBindByName(column = "HIGH_PRICE", converter = BigDecimalConverter.class)
	private BigDecimal highPrice;

	@CsvCustomBindByName(column = "LOW_PRICE", converter = BigDecimalConverter.class)
	private BigDecimal lowPrice;

	@CsvCustomBindByName(column = "LAST_PRICE", converter = BigDecimalConverter.class)
	private BigDecimal lastPrice;

	@CsvCustomBindByName(column = "CLOSE_PRICE", converter = BigDecimalConverter.class)
	private BigDecimal closePrice;

	@CsvCustomBindByName(column = "AVG_PRICE", converter = BigDecimalConverter.class)
	private BigDecimal averagePrice;

	@CsvCustomBindByName(column = "TTL_TRD_QNTY", converter = LongConverter.class)
	private long totalTradedQuantity;

	@CsvCustomBindByName(column = "TURNOVER_LACS", converter = BigDecimalConverter.class)
	private BigDecimal turnoverInLacs;

	@CsvCustomBindByName(column = "NO_OF_TRADES", converter = LongConverter.class)
	private long noOfTrades;

	@CsvCustomBindByName(column = "DELIV_QTY", converter = LongConverter.class)
	private long deliveryQuantity;

	@CsvCustomBindByName(column = "DELIV_PER", converter = FloatConverter.class)
	private float deliveryPercentage;

	public Long getStockDataId() {
		return stockDataId;
	}

	public void setStockDataId(Long stockDataId) {
		this.stockDataId = stockDataId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public LocalDate getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(LocalDate tradeDate) {
		this.tradeDate = tradeDate;
	}

	public BigDecimal getPrevClose() {
		return prevClose;
	}

	public void setPrevClose(BigDecimal prevClose) {
		this.prevClose = prevClose;
	}

	public BigDecimal getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(BigDecimal openPrice) {
		this.openPrice = openPrice;
	}

	public BigDecimal getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(BigDecimal highPrice) {
		this.highPrice = highPrice;
	}

	public BigDecimal getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(BigDecimal lowPrice) {
		this.lowPrice = lowPrice;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public BigDecimal getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(BigDecimal closePrice) {
		this.closePrice = closePrice;
	}

	public BigDecimal getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(BigDecimal averagePrice) {
		this.averagePrice = averagePrice;
	}

	public long getTotalTradedQuantity() {
		return totalTradedQuantity;
	}

	public void setTotalTradedQuantity(long totalTradedQuantity) {
		this.totalTradedQuantity = totalTradedQuantity;
	}

	public BigDecimal getTurnoverInLacs() {
		return turnoverInLacs;
	}

	public void setTurnoverInLacs(BigDecimal turnoverInLacs) {
		this.turnoverInLacs = turnoverInLacs;
	}

	public long getNoOfTrades() {
		return noOfTrades;
	}

	public void setNoOfTrades(long noOfTrades) {
		this.noOfTrades = noOfTrades;
	}

	public long getDeliveryQuantity() {
		return deliveryQuantity;
	}

	public void setDeliveryQuantity(long deliveryQuantity) {
		this.deliveryQuantity = deliveryQuantity;
	}

	public float getDeliveryPercentage() {
		return deliveryPercentage;
	}

	public void setDeliveryPercentage(float deliveryPercentage) {
		this.deliveryPercentage = deliveryPercentage;
	}

}
