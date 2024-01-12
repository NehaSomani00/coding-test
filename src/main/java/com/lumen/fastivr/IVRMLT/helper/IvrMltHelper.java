package com.lumen.fastivr.IVRMLT.helper;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRMLT.utils.IVRMltUtilities;
import com.lumen.fastivr.IVRUtils.IVRConstants;

@Service
public class IvrMltHelper {
	
	final static Logger LOGGER = LoggerFactory.getLogger(IvrMltHelper.class);
	
	public String findTestCategory(String test) {
		
		String response="";
		
		switch(test) {
		
		case IVRConstants.DTMF_INPUT_1:
			response=IVRMltUtilities.QUICKX_TEST;
			break;
			
		case IVRConstants.DTMF_INPUT_2:
			response=IVRMltUtilities.LOOPX_TEST;
			break;
			
		case IVRConstants.DTMF_INPUT_3:
			response=IVRMltUtilities.FULLX_TEST;
			break;
			
		case IVRConstants.DTMF_INPUT_4:
			response=IVRMltUtilities.TONE_PLUS_TEST;
			break;
			
		case IVRConstants.DTMF_INPUT_5:
			response=IVRMltUtilities.TONE_REMOVAL_TEST;
			break;

		}
		
		return response;
	}


	public String getOverrideType(String overrideType) {
		String response="";
		
		switch(overrideType) {
		
		case IVRConstants.DTMF_INPUT_1:
			response=IVRMltUtilities.N;
			break;
			
		case IVRConstants.DTMF_INPUT_2:
			response=IVRMltUtilities.C;
			break;
			
		case IVRConstants.DTMF_INPUT_3:
			response=IVRMltUtilities.O;
			break;
			
		case IVRConstants.DTMF_INPUT_4:
			response=IVRMltUtilities.P;
			break;
			
		case IVRConstants.DTMF_INPUT_5:
			response=IVRMltUtilities.T;
			break;
			
		case IVRConstants.DTMF_INPUT_6:
			response=IVRMltUtilities.Y;
			break;

		}
		
		return response;
	}


/////////////////////////////////////////////////////////////////////////////////////////////
//	Name				:	VoltageInformationRequired
//	Input Parameters	:	rcsVerCode
//	Processing			:	Checks, if the voltage information needs to be played back to the tech.
//	Output				:	TRUE	-	if the voltage/resistance information needs to be played.
//							FALSE	-	if the voltage/resistance information need not be played
//	Instructions		:													
//////////////////////////////////////////////////////////////////////////////////////////////

	public boolean voltageInformationRequired(String rcsVerCode) {

		LOGGER.info("Inside :: voltageInformationRequired, to check voltage information needs to be played back to the tech.");
		boolean isVoltageInfoRequired = true;
		if (rcsVerCode.length() > 0) {
			// Do not speak the voltages and resistance for the following vercodes.
			// 6,61,71-74,B0-B6, E0-E7, F0-F2, ND, NS, NT, NV, SP.  For rest of the
			// vercodes speak back the details.
			switch (rcsVerCode.charAt(0)) {
			case '6':
				if (rcsVerCode.length() == 1 || rcsVerCode.charAt(1) == '1') { // Vercode is 6, 61
					isVoltageInfoRequired = false;
				}
				break;
			case '7':
				// Vercode is 71-74
				if (rcsVerCode.length() == 2 && rcsVerCode.charAt(1) >= '1' && rcsVerCode.charAt(1) <= '4') {
					isVoltageInfoRequired = false;
				}
				break;
			case 'B':
				// vercode is B0-B6
				if (rcsVerCode.length() == 2 && rcsVerCode.charAt(1) >= '0' && rcsVerCode.charAt(1) <= '6') {
					isVoltageInfoRequired = false;
				}
				break;
			case 'E':
				// Vercode is E0-E7
				if (rcsVerCode.length() == 2 && rcsVerCode.charAt(1) >= '0' && rcsVerCode.charAt(1) <= '7') {
					isVoltageInfoRequired = false;
				}
				break;
			case 'F':
				// Vercode is F0-F2
				if (rcsVerCode.length() == 2 && rcsVerCode.charAt(1) >= '0' && rcsVerCode.charAt(1) <= '2') {
					isVoltageInfoRequired = false;
				}
				break;
			case 'N':
				// Vercode is ND, NS, NT, NV
				if (rcsVerCode.length() == 2 && (rcsVerCode.charAt(1) == 'D' || rcsVerCode.charAt(1) == 'S'
						|| rcsVerCode.charAt(1) == 'T' || rcsVerCode.charAt(1) == 'V')) {
					isVoltageInfoRequired = false;
				}
				break;
			default:
				// Vercode is SP
				if (rcsVerCode.equals("SP")) {
					isVoltageInfoRequired = false;
				}
				break;
			}
		}
		return isVoltageInfoRequired;
	}
	
	public List<IVRParameter> addParamterData(String... str) {
		List<IVRParameter> params = new ArrayList<>();
		for(String s : str) {
			IVRParameter param = new IVRParameter();
			param.setData(s);
			params.add(param);
		}
		
		return params;
		
	}
	
	/**
	 * For a new MLT test requested by user , some of the fields needs to be reset
	 * @param mltSession
	 */
	public void resetTestFields(IvrMltSession mltSession) {
		mltSession.setTestRequestProxyUrl("");
		mltSession.setDatachannelId("");
		mltSession.setDataChannelProxyUrl("");
		mltSession.setMltTestResult(null);
		
	}

}
