package br.com.jpo.utils;

import java.math.BigDecimal;
import java.util.Properties;

public class PropertiesUtils {

	public static BigDecimal getPropertyAsBigDecimal(Properties properties, String propertyName, BigDecimal defaultValue) {
		BigDecimal bigDecimalValue = defaultValue;

		String value = properties.getProperty(propertyName);

		if (value != null) {
			bigDecimalValue = new BigDecimal(value);
		}

		return bigDecimalValue;
	}

	public static Integer getPropertyAsInteger(Properties properties, String propertyName, Integer defaultValue) {
		Integer intValue = defaultValue;

		String value = properties.getProperty(propertyName);

		if (value != null) {
			intValue = Integer.parseInt(value);
		}

		return intValue;
	}

	public static int getPropertyAsInt(Properties properties, String propertyName, int defaultValue) {
		int intValue = defaultValue;

		String value = properties.getProperty(propertyName);

		if (value != null) {
			intValue = Integer.parseInt(value);
		}

		return intValue;
	}

	public static boolean getPropertyAsBoolean(Properties properties, String propertyName, boolean defaultValue) {
		boolean booleanValue = defaultValue;

		String value = properties.getProperty(propertyName);

		if (value != null) {
			booleanValue = Boolean.parseBoolean(value);
		}

		return booleanValue;
	}
}
