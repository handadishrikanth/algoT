package com.exp.algot.csv;

import java.math.BigDecimal;

import com.exp.algot.entity.StockData;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class LongConverter extends AbstractBeanField<StockData, BigDecimal> {

	@Override
	protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
		value = value.equals(" -") ? "-99999" : value.trim();
		return Long.valueOf(value.trim());
	}

}
