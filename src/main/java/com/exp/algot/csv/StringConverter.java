package com.exp.algot.csv;

import java.math.BigDecimal;

import com.exp.algot.entity.StockData;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class StringConverter extends AbstractBeanField<StockData, BigDecimal> {

	@Override
	protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
		return value.trim();
	}

}
