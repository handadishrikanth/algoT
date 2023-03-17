package com.exp.algot.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

	private static final Logger log = LoggerFactory.getLogger(Util.class);

	public static void writeErrorToFile(LocalDate tradeDate, Exception e, String extension) {
		try {
			String errFileName = toNseBhavDataFileName(tradeDate) + "." + extension;
			FileUtils.writeStringToFile(new File(Constant.NSE_CSV_FOLDER + "/" + errFileName),
					e.getStackTrace() + "\n\\n\n", Charset.defaultCharset(), true);
		} catch (IOException e1) {
			log.error(String.format("Unable to print error file %s", e.getMessage()), e);
		}
	}

	public static String toNseBhavDataFileName(LocalDate tradeDate) {
		return (new StringBuilder(20)).append(Constant.FILE_NAME_PREFIX)
				.append(tradeDate.format(Constant.DATE_FORMATTER)).append(".csv").toString();
	}

	public static Set<LocalDate> toLocalDates(String file) throws FileNotFoundException, IOException {
		Set<LocalDate> lines = IOUtils.readLines(new FileReader(new File(file))).stream()
				.filter(e -> StringUtils.isNotEmpty(e)).map(e -> LocalDate.parse(e, Constant.DATE_FORMATTER))
				.collect(Collectors.toSet());
		return lines;
	}
}
