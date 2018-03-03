package br.com.jpo.sql;

import java.util.regex.Pattern;

public class MacroTranslator {

	private static Pattern	pOnlyDate;
	private static Pattern	pIgnoreCase;
	private static Pattern	pNullValue;
	private static Pattern	pConvertToNumber;
	private static Pattern	pMaxLines;
	private static Pattern	pYearMonth;
	private static Pattern	pTruncMonth;
	private static Pattern	pDbDate;
	private static Pattern	pStringIndex;
	private static Pattern	pTrim;
	private static Pattern	pLength;
	private static Pattern	pDiffDays;
	private static Pattern	pConcatStr;
	private static Pattern	pConvertToFloat;
	private static Pattern	pConvertToVarchar;
	private static Pattern	pUserName;
	private static Pattern	pLeftPad;
	private static Pattern	pSubString;
	private static Pattern	pUpperText;
	private static Pattern  pGetMonth;
	private static Pattern  pGetYear;
	private static Pattern  pGetDay;
	private static Pattern  pMonYear;
	private static Pattern  pAddMonths;

	static {
		pOnlyDate = Pattern.compile("onlydate(\\s+)?\\(", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pIgnoreCase = Pattern.compile("ignorecase(\\s+)?\\(", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pNullValue = Pattern.compile("\\bnullValue\\b(\\s+)?\\(", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pConvertToNumber = Pattern.compile("\\bconvertToNumber\\b(\\s+)?\\(", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pMaxLines = Pattern.compile("\\bmaxLines\\b(\\s+)?\\(", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pYearMonth = Pattern.compile("\\byearMonth\\b(\\s+)?\\(", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pDbDate = Pattern.compile("\\bdbDate\\b\\(\\)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pStringIndex = Pattern.compile("\\bstringIndex\\b(\\s+)?\\(", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pTrim = Pattern.compile("\\btrim\\b(\\s+)?\\(", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pLength = Pattern.compile("\\blength\\b(\\s+)?\\(", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pDiffDays = Pattern.compile("\\bdiffdays\\b(\\s+)?\\(", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pConcatStr = Pattern.compile("\\bconcatstr\\b(\\s+)?\\(", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pTruncMonth = Pattern.compile("\\btruncMonth\\b(\\s+)?\\(", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pConvertToFloat = Pattern.compile("\\bconvertToFloat\\b(\\s+)?\\(", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pConvertToVarchar = Pattern.compile("\\bconvertToVarchar\\b(\\s+)?\\(", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pUserName = Pattern.compile("\\$\\{user\\.name\\}\\.?", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pLeftPad = Pattern.compile("leftPad\\s*\\(\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*(\\s*[^)]+)\\s*\\)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pSubString = Pattern.compile("\\bsubString\\(([^\\)]+)\\)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pUpperText = Pattern.compile("upperText(\\s+)?\\(", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pGetMonth = Pattern.compile("\\bgetMonth\\b(\\s+)?\\(",Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pGetYear = Pattern.compile("\\bgetYear\\b(\\s+)?\\(",Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pGetDay = Pattern.compile("\\bgetDay\\b(\\s+)?\\(",Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pMonYear = Pattern.compile("\\bmonYear\\b(\\s+)?\\(",Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		pAddMonths = Pattern.compile("\\baddMonths\\b(\\s+)?\\(", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	}

	public static void translate(int databaseDialect, StringBuffer buf) {
		
	}
}
