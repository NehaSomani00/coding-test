package com.lumen.fastivr.IVRUtils;

import org.springframework.stereotype.Component;

import static com.lumen.fastivr.IVRUtils.IVRConstants.ADMIN;
import static com.lumen.fastivr.IVRUtils.IVRConstants.CALLER_ID;
import static com.lumen.fastivr.IVRUtils.IVRConstants.CHANGE_SERVICE_TERMINAL;
import static com.lumen.fastivr.IVRUtils.IVRConstants.CHANGE_STATUS;
import static com.lumen.fastivr.IVRUtils.IVRConstants.CUT_TO_NEW_FACILITIES;
import static com.lumen.fastivr.IVRUtils.IVRConstants.LFACS_EVENT;
import static com.lumen.fastivr.IVRUtils.IVRConstants.MLT_EVENT;
import static com.lumen.fastivr.IVRUtils.IVRConstants.SIGNON_EVENT;
import static com.lumen.fastivr.IVRUtils.IVRConstants.REGEX_NON_NUMERIC;
import static com.lumen.fastivr.IVRUtils.IVRConstants.REGEX_NUMERIC;
import static com.lumen.fastivr.IVRUtils.IVRConstants.CONSTRUCTION_ACTIVITY;

@Component
public class IVRUtility {
	
	public String convertStateToEvent(String state, String userInput) {
		
		String stateIdentifier = state.substring(0, 2);
		if("4".equals(userInput) && "MM".equalsIgnoreCase(stateIdentifier)) {
			stateIdentifier = "CCPS";
		}
		
		switch(stateIdentifier) {
		case "SS":
			return SIGNON_EVENT;
		case "FI":
			return LFACS_EVENT;
		case "ML":
			return MLT_EVENT;
		case "FN":
			return CUT_TO_NEW_FACILITIES;
		case "FP":
		case "CCPS":
			return CHANGE_STATUS;
		case "ID":
			return CALLER_ID;
		case "FT":
			return CHANGE_SERVICE_TERMINAL;
		case "AD":
			return ADMIN;
		case "MM":
			return ADMIN;	
		case "CT":
			return CONSTRUCTION_ACTIVITY;
		default: 
			return null;
		
		}
		
	}
	
	public static boolean nonNumberCheck(String value) {
		
		return value.matches(REGEX_NON_NUMERIC);
	}
	
	public static boolean numericCheck(String value) {
		
		return value.matches(REGEX_NUMERIC);
	}
}
