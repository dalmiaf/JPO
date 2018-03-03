package br.com.jpo.utils;


public class StringUtils {

	public static final String CHARSET_UTF8	 		= "UTF-8";
	public static final String CHARSET_ISO88591 	= "ISO-8859-1";
	public static final String REGEX_DOT			= "\\.";

	public static String getEmptyAsNull(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}

		return value;
	}

	public static String getEmptyAsNull(String[] value, int index) {
		if (value == null || index < 0 || index >= value.length) {
			return null;
		}

		return getEmptyAsNull(value[index]);
	}

	public static String getNullAsEmpty(String value) {
		if (value == null) {
			return "";
		}

		return value;
	}

	public static String getNullAsEmpty(Object value) {
		if (value == null) {
			return "";
		}

		return String.valueOf(value);
	}

	public static String getNullAsEmpty(String[] value, int index) {
		if (value == null || index < 0 || index >= value.length) {
			return null;
		}

		return getNullAsEmpty(value[index]);
	}

	public static boolean isNotEmpty(String value) {
		if (value != null && value.trim().length() > 0) {
			return true;
		}

		return false;
	}

	public static String[] split(String value, String regex) {
		return StringUtils.getNullAsEmpty(value).split(regex);
	}

	public static String[] splitDot(String value) {
		return split(value, REGEX_DOT);
	}

	public static String valueOf(Object value) {
		if (value == null) {
			return "";
		}

		return String.valueOf(value);
	}

	public static String toStringOrEmpty(Object value) {
		if (value == null) {
			return "";
		}

		return value.toString();
	}

	public static String decodeUtf8ToIso88591(String value) {
		return decode(value, CHARSET_UTF8, CHARSET_ISO88591);
	}

	public static String decodeIso88591ToUtf8(String value) {
		return decode(value, CHARSET_ISO88591, CHARSET_UTF8);
	}

	public static String decode(String value, String charsetIn, String charsetOut) {
		if (value == null || charsetIn == null || charsetOut == null) {
			return value;
		}

		try {
			return new String(value.getBytes(charsetIn), charsetOut);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		return value;
	}

	public static String replaceString(String search, String replace, String text, boolean replaceAll) {
		if (replaceAll) {
			text = text.replace(search, replace);
		} else {
			text = text.replaceAll(search, replace);
		}

		return text;
	}

	public static void replaceString(String search, String replace, StringBuffer buf, boolean replaceAll) {
		int startIndex = 0;

		if (replace == null) {
			replace = "";
		}

		boolean replaceConstainsSearch = replace.indexOf(search) > -1;

		while ((startIndex = buf.indexOf(search, startIndex)) > -1) {
			buf.replace(startIndex, startIndex + search.length(), replace);

			if (replaceConstainsSearch) { //evita loop infinito
				startIndex += replace.length();
			}

			if (!replaceAll) {
				break;
			}
		}
	}
}
