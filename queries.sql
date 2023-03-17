
SELECT stockDataId, symbol, series, tradeDate, prevClose, openPrice, highPrice, lowPrice, lastPrice, closePrice, averagePrice, 
 totalTradedQuantity, turnoverInLacs, noOfTrades, deliveryQuantity, deliveryPercentage FROM STOCKDATA order by STOCKDATAID ;

 
 SELECT * FROM STOCKDATA where lastPrice = '-99999.0'

 
 SELECT * FROM STOCKDATA where lastPrice = '-99999.0' ;

SELECT * FROM STOCKDATA where deliveryQuantity = '-99999' ;

SELECT * FROM STOCKDATA where deliveryPercentage = '-99999.0' ;

