package com.exp.algot.csv;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.exp.algot.entity.StockData;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class BigDecimalConverter extends AbstractBeanField<StockData, BigDecimal> {

	@Override
	protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
		value = StringUtils.isBlank(value) ? "-99999" : value.trim();
		return new BigDecimal(value.trim());
	}

}
