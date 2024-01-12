package com.lumen.fastivr.IVRSignon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;

import java.util.List;

public interface IVRSignOnInterface {
	
	
	/**
	 * This step is called, 
	 * When User enters both Security ID and password/reset-password request
	 * @param userInputs,sessionId
	 * @return
	 */
	IVRWebHookResponseDto issueUserLogin(List<String> userInputs, String sessionId);
	
	/**
	 * This contract should validate the password with the business rules 
	 * @param userInputDTMF
	 * @param sessionId
	 * @return responseDto
	 */
	IVRWebHookResponseDto validatePasswordBusinessRules(String userInputDTMF, String sessionId);
	
	
	IVRWebHookResponseDto issueValidateTechnicianProperties(String sessionId);
	
	/**
	 * This contract is to enable tech to login with a new password
	 * @param userInputDTMF
	 * @param sessionId
	 * @return
	 */
	IVRWebHookResponseDto issueUserLoginWithNewPassword(String userInputDTMF, String sessionId);
	
	/**
	 * This is the alternate login or Reset password flow 
	 * Reset password flow activates only if birth-date is set 
	 * @return
	 */
	IVRWebHookResponseDto issueUserAltLogin(String userInputDTMF, String sessionId);
	
	/**
	 * Birth-date is the secondary code in IVR system
	 * it's accepted in MMDDYYYY (8-digit) format 
	 * This birth-date is set (if-empty) so that the tech can perform
	 * automatic password reset, without calling the help-line 
	 * @param birthDate
	 * @return
	 */
	IVRWebHookResponseDto issueSecCodeUpdate(String userInputDTMF, String sessionId);
	
	/**
	 * This contract is to enable the tech to end the call with IVR system
	 * or any pre-conditions causes the call to end
	 * @return
	 */
	IVRWebHookResponseDto issueLogout(String userInputDTMF, String sessionId);
	
	
	
	/**
	 * Validates the entered date with the format MMDDYYYY
	 * @param date
	 * @return
	 */
	IVRWebHookResponseDto validateBirthDate(String userInputDTMF, String sessionId);
	
	/**
	 * Persists the validated birthdate in the Technician table 
	 * @param date, sessionId
	 * @return
	 */
	IVRWebHookResponseDto persistNewBirthdate(String sessionId);
	
	
	/**
	 * This is the last step of authentication,
	 * where it's checked whether technician's area code(NPA) 
	 * is set in it's profile
	 * @param cuid
	 * @return
	 */
	IVRWebHookResponseDto checkTechAreaCode(String sessionId);
	
	
	/**
	 * SSD150
	 * This method will validate whether Technician has a Pager configured properly
	 * @param sessionId
	 * @return
	 */
	IVRWebHookResponseDto validateTechPagerConfiguredPasswordReset(String sessionId);
	
	/**
	 * SSD170
	 * This method will validate whether Technician has a Pager configured properly
	 * TODO: Need to find way to re-use SSD150 here 
	 * @param sessionId
	 * @return
	 */
	IVRWebHookResponseDto validateTechPagerConfiguredBirthdate(String sessionId);
	
	/**
	 * SSD210
	 * This method will validate the security code entered in the 
	 * beginning of the call 
	 * @param sessionId
	 * @return
	 */
	IVRWebHookResponseDto validateTechSecurityCodeFromSession(String userInputDTMF, String sessionId);
	
	/**
	 * SSD220
	 * This method will call the NET API webhook input, which will trigger the 
	 * Pager OTP
	 * @param sessionId
	 * @return
	 * @throws JsonProcessingException 
	 */
	IVRWebHookResponseDto generatePagerOTP(String sessionId);
	
	/**
	 * SSD300
	 * This method will validate the OTP generated with the OTP entered from User
	 * @param sessionId, userInput
	 * @return
	 */
	IVRWebHookResponseDto validatePagerOtp(String userInputDTMF, String sessionId);
	
	/**
	 * This contract is to Validate the NPA with the npa table 
	 * and return back HOOK RETURN CODE (1) or (0) or (2)
	 * @param userInputDTMF
	 * @param sessionId
	 * @return
	 */
	IVRWebHookResponseDto validateNPA(String userInputDTMF, String sessionId);
	
	//TBD
	IVRWebHookResponseDto playVoiceGram(String userInputDTMF, String sessionId);
	
	//TBD
	IVRWebHookResponseDto updateVoiceGram(String userInputDTMF, String sessionId);

}
