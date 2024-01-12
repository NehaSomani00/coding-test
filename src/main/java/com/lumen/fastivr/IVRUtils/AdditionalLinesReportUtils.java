package com.lumen.fastivr.IVRUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.SUPL;

@Service
public class AdditionalLinesReportUtils {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AdditionalLinesReportUtils.class);
	
	boolean commonValidation(CurrentAssignmentResponseDto currAssgResp) {
		if ((currAssgResp.getReturnDataSet().getLoop().size() > 0) &&
				(currAssgResp.getReturnDataSet().getLoop().get(0).getADDR() != null) &&
				(currAssgResp.getReturnDataSet().getLoop().get(0).getADDR().size() > 0) &&
				(currAssgResp.getReturnDataSet().getLoop().get(0).getADDR().get(0).getBADR() != null) &&
				(currAssgResp.getReturnDataSet().getLoop().get(0).getADDR().get(0).getBADR().size() > 0) &&
				(currAssgResp.getReturnDataSet().getLoop().get(0).getADDR().get(0).getBADR().get(0) != null)) {
			return true;
		}
		return false;
	}
	
	boolean commonValidationOtherInfo(CurrentAssignmentResponseDto currAssgResp) {
		if ((currAssgResp.getReturnDataSet().getLoop().size() > 0) &&
				(currAssgResp.getReturnDataSet().getLoop().get(0).getADDR() != null) &&
				(currAssgResp.getReturnDataSet().getLoop().get(0).getADDR().size() > 0) &&
				(currAssgResp.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL() != null) &&
				(currAssgResp.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().size() > 0) &&
				(currAssgResp.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0) != null)) {
			return true;
		}
		return false;
	}
	
	String getStreetNumber(CurrentAssignmentResponseDto currAssgResp) {
		String streetNumber = "";
		if(commonValidation(currAssgResp) ) {
			streetNumber = currAssgResp.getReturnDataSet().getLoop().get(0).getADDR().get(0).getBADR().get(0).getBAD();
		}
		return streetNumber != null ? streetNumber : "";
		
	}
	
	String getStreetInfo(CurrentAssignmentResponseDto currAssgResp) {
		String streetName = "";
		if(commonValidation(currAssgResp) ) {
			streetName = currAssgResp.getReturnDataSet().getLoop().get(0).getADDR().get(0).getBADR().get(0).getSTR();
		}
		return streetName != null ? streetName : "";
	}
	
	String getOtherInfo(CurrentAssignmentResponseDto currAssgResp) {
		String otherInfo = "";
		if(commonValidationOtherInfo(currAssgResp) &&
				currAssgResp.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getSTYP() != null) {
			
			 SUPL supl = currAssgResp.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0);
			otherInfo += "," + currAssgResp.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getSTYP();
			
			// Check if supplement addr info for structure type
			if(supl.getSID() != null) {
				otherInfo += "," + supl.getSTYP();
			}
			
			// Check if supplement addr info for elevation type
			if(supl.getETYP() != null) {
				otherInfo += "," + supl.getETYP();
				
				if(supl.getETYP() != null) {
					otherInfo += "=" + supl.getETYP();
				}
			}
			
			// Check if supplement addr info for unit type
			if(supl.getUTYP()!=null) {
				
				//JEN - this will fail if UTYP is UNIT and UID is not UDC so changing to only check 
				// for UDC
				if(!supl.getUID().equalsIgnoreCase("UDC")) {
					
					if(supl.getUTYP().equalsIgnoreCase("RM")) {
						otherInfo += "," + "ROOM";
						
					} else if (supl.getUTYP().equalsIgnoreCase("STE")) {
						otherInfo += "," + "SUIT";
					} else {
						otherInfo += "," + supl.getUTYP();
					}
					
					if(supl.getUID() != null) {
						otherInfo += "=" + supl.getUID();
					}
					
				}
			}
		}
		
		return otherInfo;
	}
	
	
	public String getFormattedServiceAddress(String sessionId, String currentAssignmentResponseJsonStr)
			throws JsonMappingException, JsonProcessingException {

		CurrentAssignmentResponseDto currentAssignmentResponse = objectMapper
				.readValue(currentAssignmentResponseJsonStr, CurrentAssignmentResponseDto.class);
		String streetNumber = getStreetNumber(currentAssignmentResponse);
		String streetInfo = getStreetInfo(currentAssignmentResponse);
		String otherInfo = getOtherInfo(currentAssignmentResponse);

		LOGGER.info("Sessionid:" + sessionId + " streetNumber=" + streetNumber);
		LOGGER.info("Sessionid:" + sessionId + " streetInfo=" + streetInfo);
		LOGGER.info("Sessionid:" + sessionId + " otherInfo=" + otherInfo);

		String retString = "NO=" + streetNumber;
		if (streetInfo != null) {
			retString += ",ST=" + streetInfo;
		}
		if (streetInfo != null) {
			retString += otherInfo;
		}
		LOGGER.info("Sessionid:" + sessionId + " service address=" + retString);
		return retString;
	}

}
