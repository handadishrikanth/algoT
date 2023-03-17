package com.exp.algot;

import static com.exp.algot.common.Constant.DATE_FORMATTER;
import static java.time.LocalDate.parse;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import com.exp.algot.service.ExchangeDataDownloader;
import com.exp.algot.service.ExchangeDataLoader;

@SpringBootApplication(exclude = { ValidationAutoConfiguration.class })
public class AlgotApplication implements CommandLineRunner {

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private ExchangeDataDownloader downloader;

	@Autowired
	private ExchangeDataLoader loader;

	public static void main(String[] args) {
		SpringApplication.run(AlgotApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		LocalDate startDate = parse("01012020", DATE_FORMATTER);
		LocalDate endDate = parse("15012020", DATE_FORMATTER);

		downloader.initDates(startDate, endDate);
		downloader.downloadPendingFiles(startDate, endDate);

		loader.loadAllData(startDate, endDate);

		System.exit(SpringApplication.exit(context));
	}

}
