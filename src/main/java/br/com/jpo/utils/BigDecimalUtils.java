package br.com.jpo.utils;

import java.math.BigDecimal;

public class BigDecimalUtils {

	public static BigDecimal getBigDecimalOrZero(BigDecimal value) {
		if (value == null) {
			return BigDecimal.ZERO;
		}

		return value;
	}

	public static BigDecimal getBigDecimalOrNull(Object value) {
		if (value == null) {
			return null;
		}

		try {
			long longValue = Long.valueOf(value.toString());
			return BigDecimal.valueOf(longValue);
		} catch(Exception ex) {
			return null;
		}
	}

	public static BigDecimal adjustBigDecimalValue(BigDecimal o) {
		if(o != null){
			// o driver da oracle tem um bug com números em notação científica (7.65E+7 por exemplo)
			// quando o BigDecimal está em notação cientifica o scale fica negativo, então testamos essa situação 
			// e recriamos o BigDecimal usando o String plano, que converte de notação cientifica para notação decimal 
			if( o.scale() < 0  ){
				o = new BigDecimal(((BigDecimal)o).toPlainString());
			}
		}

		return o;
	}
}
