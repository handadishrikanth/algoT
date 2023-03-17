package com.exp.algot.service;

import static com.exp.algot.common.Util.writeErrorToFile;
import static com.exp.algot.entity.StockDataLoadStat.LoadStatus.DB_LOAD_ERROR;
import static com.exp.algot.entity.StockDataLoadStat.LoadStatus.DOWNLOAD_FINSIHED;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.exp.algot.dao.StockDataDao;
import com.exp.algot.dao.StockDataLoadStatDao;
import com.exp.algot.entity.StockData;
import com.exp.algot.entity.StockDataLoadStat;
import com.exp.algot.entity.StockDataLoadStat.LoadStatus;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import jakarta.inject.Inject;

@Service
public class ExchangeDataLoader {

	private static final Logger log = LoggerFactory.getLogger(ExchangeDataLoader.class);

	private static final String NSE_CSV_FOLDER = "nsedata";

	@Inject
	private StockDataLoadStatDao stockDataLoadStatDao;

	@Inject
	private StockDataDao stockDataDao;

	public int loadAllData(LocalDate startDate, LocalDate endDate) {
		int totalRows = stockDataLoadStatDao.updateStatus(DB_LOAD_ERROR, DOWNLOAD_FINSIHED, startDate, endDate);
		log.info("Reset the load status with total rows {}", totalRows);

		AtomicInteger count = new AtomicInteger(0);
		List<StockDataLoadStat> nextPendingLoad = new ArrayList<>();
		while ((nextPendingLoad = getNextPendingCsvLoad(startDate, endDate)).size() > 0) {
			nextPendingLoad.parallelStream().forEach(e -> {
				loadCsvBhavData(e);
				count.incrementAndGet();
			});
		}
		return count.intValue();
	}

	private List<StockDataLoadStat> getNextPendingCsvLoad(LocalDate startDate, LocalDate endDate) {
		return stockDataLoadStatDao.findAllTopFiftyByLoadStatusAndTradeDateGreaterThanAndTradeDateLessThanEqual(
				DOWNLOAD_FINSIHED, startDate, endDate);
	}

	private void loadCsvBhavData(StockDataLoadStat loadStat) {
		try {
			log.info("Started loading NSE bhav data for trade date {}", loadStat.getTradeDate());
			int totalRows = loadCsvBhavData(loadStat.getFileName());
			save(loadStat, null, LoadStatus.DB_LOAD_FINISHED);
			log.info("Finished loading NSE bhav data for trade date {} with total rows {}", loadStat.getTradeDate(),
					totalRows);
		} catch (Exception e) {
			log.error(String.format("Exception while loading NSE bhav data for trade date %s", loadStat.getTradeDate()),
					e);
			writeErrorToFile(loadStat.getTradeDate(), e, "load.err");
			save(loadStat, e.getMessage(), LoadStatus.DB_LOAD_ERROR);
		}
	}

	private void save(StockDataLoadStat loadStat, String statusMessage, LoadStatus loadStatus) {
		loadStat.setLoadStatus(loadStatus);
		loadStat.setStatusMessage(statusMessage);
		stockDataLoadStatDao.save(loadStat);
	}

	private int loadCsvBhavData(String fileName) throws IllegalStateException, FileNotFoundException {
		CsvToBean<StockData> csvToBean = null;
		try {
			csvToBean = new CsvToBeanBuilder<StockData>(new FileReader(NSE_CSV_FOLDER + "/" + fileName))
					.withType(StockData.class).withIgnoreLeadingWhiteSpace(true).build();
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<StockData> beans = csvToBean.parse();
		stockDataDao.saveAll(beans);
		return beans.size();
	}

}
