package com.exp.algot.service;

import static com.exp.algot.common.Constant.DATE_FORMATTER;
import static com.exp.algot.common.Constant.FILE_NAME_PREFIX;
import static com.exp.algot.common.Constant.NSE_CSV_FOLDER;
import static com.exp.algot.common.Util.writeErrorToFile;
import static com.exp.algot.entity.StockDataLoadStat.LoadStatus.NEW;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.exp.algot.common.Util;
import com.exp.algot.dao.StockDataLoadStatDao;
import com.exp.algot.entity.StockDataLoadStat;
import com.exp.algot.entity.StockDataLoadStat.LoadStatus;

import jakarta.inject.Inject;

@Service
public class ExchangeDataDownloader {

	private static final Logger log = LoggerFactory.getLogger(ExchangeDataDownloader.class);

	private static final String NSE_URL_PATH = "https://archives.nseindia.com/products/content/";

	private static final Set<LocalDate> NSE_HOLIDAYS = new HashSet<>();

	private static final Set<LocalDate> NSE_DOWNLOAD_ERROR_DATES = new HashSet<>();// missing NSE data

	@Inject
	private StockDataLoadStatDao stockDataLoadStatDao;

	static {
		try {
			Files.createDirectories(Paths.get(FILE_NAME_PREFIX));
		} catch (IOException e) {
			throw new RuntimeException("Unable to create NSE data folder ", e);
		}
		initNseHolidays();
		initNseDownloadErrorDates();
	}

	public int initDates(LocalDate startDate, LocalDate endDate) {
		log.info("Started initializing dates from start {} to end {}", startDate, endDate);
		AtomicInteger count = new AtomicInteger(0);
		long range = endDate.toEpochDay() - startDate.toEpochDay();
		range++;// to cover exclusive Java stream range
		List<LocalDate> tradeDates = LongStream.range(0, range).filter(i -> {
			LocalDate tradeDate = startDate.plusDays(i);
			log.info("#### trade date {}", tradeDate);
			return (!isTradeDateAlreadyAdded(tradeDate) && !isWeekend(tradeDate) && !isNseHolidy(tradeDate)
					&& !isNseDownloadErrorDate(tradeDate));
		}).collect(ArrayList<LocalDate>::new, (list, i) -> {
			list.add(startDate.plusDays(i));
		}, ArrayList::addAll);
		log.info("Finished creating local date objects for total dates {} & started creating StockDataLoadStat..",
				tradeDates.size());
		tradeDates.parallelStream().forEach(tradeDate -> {
			log.debug("Saving date info for {} with status {}", tradeDate, NEW);
			StockDataLoadStat stat = new StockDataLoadStat();
			stat.setLoadStatus(LoadStatus.NEW);
			stat.setTradeDate(tradeDate);
			stockDataLoadStatDao.save(stat);
			count.incrementAndGet();
		});
		log.info("Finished saving StockDataLoadStat objects for total entries {}", count);
		return count.get();
	}

	public int downloadPendingFiles(LocalDate startDate, LocalDate endDate) throws IOException {
		int totalRows = stockDataLoadStatDao.updateStatus(LoadStatus.DOWNLOAD_ERROR, LoadStatus.NEW, startDate,
				endDate);
		log.info("Reset the download status with total rows {}", totalRows);

		AtomicInteger count = new AtomicInteger(0);
		List<StockDataLoadStat> nextFiftyPendingLoad = new ArrayList<>();
		while ((nextFiftyPendingLoad = getNextFiftyPendingDownload(startDate, endDate)).size() > 0) {
			nextFiftyPendingLoad.forEach(e -> {
				downloadNseBhavData(e);
				count.incrementAndGet();
			});
		}
		return count.intValue();
	}

	private void downloadNseBhavData(StockDataLoadStat loadStat) {
		try {
			log.info("Started downloading NSE bhav data for trade date {}", loadStat.getTradeDate());
			String fileName = Util.toNseBhavDataFileName(loadStat.getTradeDate());
			if (new File(NSE_CSV_FOLDER + "/" + fileName).exists()) {
				log.info("File already present locally. File name: {}", fileName);
				save(loadStat, "File present locally", LoadStatus.DOWNLOAD_FINSIHED);
				return;
			}

			downloadNseBhavData(fileName);
			loadStat.setFileName(fileName);
			save(loadStat, null, LoadStatus.DOWNLOAD_FINSIHED);
			log.info("Finished downloading NSE bhav data for trade date {}", loadStat.getTradeDate());
		} catch (Exception e) {
			log.error(String.format("Exception while downloading NSE bhav data for trade date %s",
					loadStat.getTradeDate()), e);
			writeErrorToFile(loadStat.getTradeDate(), e, "download.err");
			save(loadStat, e.getMessage(), LoadStatus.DOWNLOAD_ERROR);
		}
	}

	private void downloadNseBhavData(String fileName) throws MalformedURLException, IOException {
		String url = (new StringBuilder(20).append(NSE_URL_PATH).append(fileName)).toString();
		HttpURLConnection httpCon = (HttpURLConnection) new URL(url).openConnection();
		httpCon.setConnectTimeout(3000);
		httpCon.setReadTimeout(3000);

		try (BufferedInputStream bis = new BufferedInputStream(httpCon.getInputStream());
				FileOutputStream fos = new FileOutputStream(NSE_CSV_FOLDER + "/" + fileName)) {

			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = bis.read(dataBuffer, 0, 1024)) != -1) {
				fos.write(dataBuffer, 0, bytesRead);
			}
		}
		sleep(500);
	}

	private List<StockDataLoadStat> getNextFiftyPendingDownload(LocalDate start, LocalDate end) {
		return stockDataLoadStatDao.findAllTopFiftyByLoadStatusAndTradeDateGreaterThanAndTradeDateLessThanEqual(NEW,
				start, end);
	}

	private boolean isTradeDateAlreadyAdded(LocalDate tradeDate) {
		return stockDataLoadStatDao.findByTradeDate(tradeDate).isPresent();
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ie) {
			log.error("Exception while executing thread sleep {}", ie.getMessage(), ie);
		}
	}

	private static void initNseHolidays() {
		try {
			Set<LocalDate> lines = IOUtils.readLines(new FileReader(new File("nse_holidays.txt"))).stream()
					.filter(e -> StringUtils.isNotEmpty(e)).map(e -> LocalDate.parse(e, DATE_FORMATTER))
					.collect(Collectors.toSet());
			NSE_HOLIDAYS.addAll(lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void initNseDownloadErrorDates() {
		try {
			Set<LocalDate> lines = Util.toLocalDates("nse_download_error_dates.txt");
			NSE_DOWNLOAD_ERROR_DATES.addAll(lines);
		} catch (Exception e) {
			log.error("Exception while initializing download error dates{}", e.getMessage(), e);
			throw new RuntimeException("Exception while initializing download error dates " + e.getMessage(), e);
		}
	}

	private boolean isWeekend(LocalDate localDate) {
		return (localDate.getDayOfWeek() == DayOfWeek.SATURDAY || localDate.getDayOfWeek() == DayOfWeek.SUNDAY);
	}

	private boolean isNseHolidy(LocalDate localDate) {
		return NSE_HOLIDAYS.contains(localDate);
	}

	private boolean isNseDownloadErrorDate(LocalDate localDate) {
		return NSE_DOWNLOAD_ERROR_DATES.contains(localDate);
	}

	private void save(StockDataLoadStat loadStat, String statusMessage, LoadStatus loadStatus) {
		loadStat.setLoadStatus(loadStatus);
		loadStat.setStatusMessage(statusMessage);
		stockDataLoadStatDao.save(loadStat);
	}

}
