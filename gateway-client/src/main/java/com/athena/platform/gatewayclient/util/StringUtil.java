package com.athena.platform.gatewayclient.util;

public class StringUtil {
	
	public static boolean isNullOrEmpty(String input) {
		
		if(input == null || input.equals("")) {
			
			return true;
		}
		
		return false;
	}

}
