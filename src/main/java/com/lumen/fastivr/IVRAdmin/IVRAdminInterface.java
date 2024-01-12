package com.lumen.fastivr.IVRAdmin;

import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;

public interface IVRAdminInterface {
	/**
	 * This method should validate the variable area code. 
	 * @param userInputDTMF
	 * @param sessionId
	 * @return responseDto
	 */
	IVRWebHookResponseDto validateVariableAreaCode(String userInputDTMF, String sessionId);
	
	/**
	 * This method Toggle the variable area code.
	 * @param userInputDTMF
	 * @param sessionId
	 * @return
	 */
	IVRWebHookResponseDto issueSetVarNpaRequest(String userInputDTMF, String sessionId);
	
	/**
	 * This contract should validate the password with the business rules 
	 * @param userInputDTMF
	 * @param sessionId
	 * @return responseDto
	 */
	IVRWebHookResponseDto validatePassword(String userInputDTMF, String sessionId);
	
	IVRWebHookResponseDto issueUserLoginWithNewPassword(String userInputDTMF, String sessionId);

	IVRWebHookResponseDto validateAreaCode(String userInputDTMF, String sessionId);

	IVRWebHookResponseDto getCurrentAreaCode(String sessionId);

	IVRWebHookResponseDto setTechNPARequest(String userInputDTMF, String sessionId);

	IVRWebHookResponseDto voiceResults(String sessionId);
}
