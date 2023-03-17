package com.exp.algot.csv;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.exp.algot.entity.StockData;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class LocalDateConverter extends AbstractBeanField<StockData, LocalDate> {

	@Override
	protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
		return LocalDate.parse(value.trim(), formatter);
	}


}
