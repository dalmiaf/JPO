package br.com.jpo.utils;

import java.util.UUID;
import java.util.regex.Pattern;

public class UIDGenerator {

	private static Object lock = new Object();
    private static Pattern onlyNumbersAndChars = Pattern.compile("\\W");

    public static String getNextID() {
        synchronized(lock) {
            return onlyNumbersAndChars.matcher(UUID.randomUUID().toString()).replaceAll("");
        }
    }

    public static String getMod11Digit(String strNumber) {
		int lastPosition = strNumber.length() - 1;
    	int sum = 0;

    	for(int i = 0; i <= lastPosition; i++){
    		sum += Character.getNumericValue(strNumber.charAt(i)) * ((lastPosition + 2) - i);
    	}

    	int dig = sum * 10 % 11;

    	if(dig == 0 || dig == 10){
    		dig = 1;
    	}

    	return String.valueOf(dig);
	}
}