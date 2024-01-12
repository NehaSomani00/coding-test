package com.lumen.fastivr.IVRLFACS;

import static com.lumen.fastivr.IVRUtils.IVRConstants.INVALID_TN_EMPTY;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INVALID_TN_ERR_CODE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INVALID_TN_LENGTH_ERR;
import static com.lumen.fastivr.IVRUtils.IVRConstants.TN_NOT_IN_TABLE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.VALID_TN_MSG;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_0;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_1;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;

@Service
public class LfacsValidation {

	private final IVRLfacsServiceHelper serviceHelper;

	private final ObjectMapper objectMapper;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LfacsValidation.class);

	public LfacsValidation(@Lazy IVRLfacsServiceHelper serviceHelper, @Lazy ObjectMapper objectMapper) {
		this.serviceHelper = serviceHelper;
		this.objectMapper = objectMapper;
	}

	/**
	 * This helper method checks if the TN is valid in LOSDB Output can be Hook
	 * return code 1 -> Valid TN 
	 * 0 -> TN not in table
	 * -1 -> If TN syntax is not 7 digits
	 * 
	 * @param userDTMFInput
	 * @param session
	 * @return
	 */
	public IVRWebHookResponseDto validateFacsTN(String userDTMFInput, IVRUserSession session) {
		String hookReturnCode = "";
		String hookReturnMessage = "";
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		List<IVRParameter> params = null;
		// do sanity check for TN
		if (userDTMFInput.isEmpty()) {
			hookReturnCode = INVALID_TN_ERR_CODE;
			hookReturnMessage = INVALID_TN_EMPTY;

		} else if (!(userDTMFInput.length() == 7 || userDTMFInput.length() == 10)) {
			hookReturnCode = INVALID_TN_ERR_CODE;
			hookReturnMessage = INVALID_TN_LENGTH_ERR;
		} else {
			// make a call to get losdb response
			// capture the NPA from session and append it to 7 digit TN
			String _10DigitTN = serviceHelper.get10DigitTN(session, userDTMFInput);
			hookReturnCode = serviceHelper.getLOSDBInterfaceStatus(session, _10DigitTN);

			if (hookReturnCode.equals(HOOK_RETURN_1)) {
				hookReturnMessage = VALID_TN_MSG;
//				hookReturnCode = HOOK_RETURN_1;
				try {
					TNInfoResponse tnInfoResponse = objectMapper.readValue(session.getLosDbResponse(),
							TNInfoResponse.class);
					String wcCilliCode = tnInfoResponse.getWcName();
					if (wcCilliCode != null && !wcCilliCode.isBlank()) {
						params = serviceHelper.addParamterData(wcCilliCode, userDTMFInput);
					}
				} catch (JsonProcessingException e) {
					LOGGER.error("Error while fetching TnInfoResponse ",e);
				}
			} else {
				hookReturnMessage = TN_NOT_IN_TABLE;
				hookReturnCode = HOOK_RETURN_0;
				params = serviceHelper.addParamterData(userDTMFInput);
			}
		}

		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		response.setParameters(params);
		return response;
	}

	/**
	 * Checks whether service address exists true if present false if absent
	 * 
	 * @param response
	 * @return
	 */
	public boolean validateServiceAddress(CurrentAssignmentResponseDto response) {

		if ((response.getReturnDataSet() != null) && (response.getReturnDataSet().getLoop() != null)
				&& (response.getReturnDataSet().getLoop().size() > 0)
				&& (response.getReturnDataSet().getLoop().get(0).getADDR() != null)
				&& (response.getReturnDataSet().getLoop().get(0).getADDR().size() > 0)
				&& (StringUtils.isNotBlank(response.getReturnDataSet().getLoop().get(0).getADDR().get(0).getADDRNO())))
			return true;

		return false;
	}
}
