package com.lumen.fastivr.IVRUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lumen.fastivr.IVRLFACS.CSLPage;

public class FormatUtilities {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FormatUtilities.class);
	
	public static String FormatTelephoneNNNXXXX(String inPhone) {
		String outPhone = "";
		inPhone = inPhone.trim();
		int lengthInPhone = inPhone.length();

		if(lengthInPhone > 12) 
			return outPhone;

		// separate numbers from rest of formatting
		String digitPhone = "";

		for(int curIndex=0; curIndex < lengthInPhone; curIndex++) 
		{
			char ch = inPhone.charAt(curIndex);
			if(Character.isDigit(ch))
				digitPhone += inPhone.charAt(curIndex);
		}

		int lengthDigitPhone = 0;
		if (digitPhone != null)
			lengthDigitPhone = digitPhone.length();


		// begin format verification to guarantee input phone number is in
		// standard Pots format
		if((lengthDigitPhone == 7) && 
			((lengthInPhone == 7) || ((lengthInPhone == 8) && (inPhone.charAt(3) == '-')) ) )
		{
			// this is a good seven digit phone number
			outPhone = digitPhone.substring(0, 3) + "-" + digitPhone.substring(3, 4);
			//good 7 digit phone number
		}
		else if((lengthDigitPhone == 10) && 
				((lengthInPhone == 10) ||
				((lengthInPhone == 12) && ((inPhone.charAt(3) == '-') || 
				(inPhone.charAt(3) == ' ')) && (inPhone.charAt(7) == '-')) ) ) 
		{
			// this is a good 10 digit phone number
			outPhone = digitPhone.substring(0,3) + " " + digitPhone.substring(3, 6) + "-" + digitPhone.substring(6);
			//good 10 digit phone number
		}

		return outPhone;
	}
	
	public static boolean isBP(char val)
	{
		boolean retVal = false;
		if ((val >= '1' && val <= '9'))
			retVal = true;
		else
			retVal = false;
		LOGGER.info("isBP says " + val + " is a BP");
		return retVal;
	}
	
	public static boolean isCC(char val)
	{
		boolean retVal = false;
		if ((val >= 'A' && val <= 'Z') || (val == ',') || (val == '+') || (val == '-'))
			retVal = true;
		else
			retVal = false;
		LOGGER.info("isCC says " + val + " is a CC");
		return retVal;
	}

}
