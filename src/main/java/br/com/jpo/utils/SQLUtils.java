package br.com.jpo.utils;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class SQLUtils {

	public static void setParameters(PreparedStatement pstm, Object value, int paramIndex) throws Exception {
		if (value instanceof BigDecimal) {
			pstm.setBigDecimal(paramIndex, BigDecimalUtils.adjustBigDecimalValue((BigDecimal) value));
		} else if (value instanceof Timestamp) {
			pstm.setTimestamp(paramIndex, (Timestamp) value);
		} else if (value instanceof Date) {
			pstm.setDate(paramIndex, (Date) value);
		} else if (value instanceof String) {
			pstm.setString(paramIndex, (String) value);
		} else {
			pstm.setObject(paramIndex, value);
		}
	}
}
