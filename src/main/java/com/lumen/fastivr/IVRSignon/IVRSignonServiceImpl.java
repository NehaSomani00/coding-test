package com.lumen.fastivr.IVRSignon;

import static com.lumen.fastivr.IVRUtils.IVRConstants.FASTIVR_BACKEND_ERR;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INVALID_DATE_FORMAT;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INVALID_OTP;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INVALID_PAGER_MESSAGE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INVALID_SESSION_ID;
import static com.lumen.fastivr.IVRUtils.IVRConstants.NET_MAIL_DEVICE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.NET_PHONE_DEVICE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.PASSWORD_SAME_AS_OLD;
import static com.lumen.fastivr.IVRUtils.IVRConstants.PASSWORD_SAME_AS_SECURITYCODE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.PASSWORD_WRONG_LENGTH;
import static com.lumen.fastivr.IVRUtils.IVRConstants.STATE_SSD110;
import static com.lumen.fastivr.IVRUtils.IVRConstants.STATE_SSD111;
import static com.lumen.fastivr.IVRUtils.IVRConstants.STATE_SSD120;
import static com.lumen.fastivr.IVRUtils.IVRConstants.STATE_SSD135;
import static com.lumen.fastivr.IVRUtils.IVRConstants.STATE_SSD150;
import static com.lumen.fastivr.IVRUtils.IVRConstants.STATE_SSD160;
import static com.lumen.fastivr.IVRUtils.IVRConstants.STATE_SSD165;
import static com.lumen.fastivr.IVRUtils.IVRConstants.STATE_SSD170;
import static com.lumen.fastivr.IVRUtils.IVRConstants.STATE_SSD180;
import static com.lumen.fastivr.IVRUtils.IVRConstants.STATE_SSD190;
import static com.lumen.fastivr.IVRUtils.IVRConstants.STATE_SSD210;
import static com.lumen.fastivr.IVRUtils.IVRConstants.STATE_SSD220;
import static com.lumen.fastivr.IVRUtils.IVRConstants.STATE_SSD300;
import static com.lumen.fastivr.IVRUtils.IVRConstants.VALID_DATE_FORMAT;
import static com.lumen.fastivr.IVRUtils.IVRConstants.VALID_OTP;
import static com.lumen.fastivr.IVRUtils.IVRConstants.*;

import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_0;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_1;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_2;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_3;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_4;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_5;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;

/**
 * 
 * @author AD00912
 *
 */
@Service
public class IVRSignonServiceImpl implements IVRSignOnInterface {

	//TODO move all field injection to constructor injection
	
//	@Autowired
//	private Map<String, IVRUserSession> userSessionMap;
	
	@Autowired
	private IVRCacheService cacheService;
	
	@Autowired
	private IVRSignOnServiceHelper serviceHelper;
	
	final static Logger LOGGER = LoggerFactory.getLogger(IVRSignonServiceImpl.class);

	//////////////////////////////////////////////////////////////////////////////////////
	//Name:					IssueUserLogin												//
	//Input Parameters:		userInputs(security-code, password)							//
	//Global Data		:	User session map											//
	//Processing		:	Check the user status response and then issue User Login	//
	//						Request, if appropriate. Then check the user login response //
	//						and return	appropriate hook return code					//
	//Output			:	Returns the hook return code.								//
	//Instructions	:		Call this function to verify user status and user login		//
	//response.																			//
	//Current State	:		SSD110														//
	//Next State		:	SSD111(1), SSE111(2), SSE114(3), SSE112(4), SSD150(5) 		//
	//////////////////////////////////////////////////////////////////////////////////////

	@Override
	public IVRWebHookResponseDto issueUserLogin(List<String> userInputs, String sessionId) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		String hookReturnCode = HOOK_RETURN_1; // considering positive scenario
		String hookReturnMessage = "";
		IVRUserSession userSession = null;
		String securityCodeInput = userInputs.get(0);
		String passwordInput = userInputs.get(1);

		response.setSessionId(sessionId);
		response.setCurrentState(STATE_SSD110);
		int passwordAttemptCounter = 0;

		// fetch the user from cache
		// user is already having a conversation with fastivr
		// New_session_flag will tell whether this is a fresh session
		if (cacheService.getBySessionId(sessionId) != null) {
			userSession = cacheService.getBySessionId(sessionId);
//			userSession.setNewSession(false);
		} else {
			// new user , need to log into the session
			userSession = new IVRUserSession();
			userSession.setSessionId(sessionId);
			userSession.setNewSession(true);
			cacheService.addSession(userSession);
		}

		LOGGER.info("issueUserLogin: Session: " + sessionId + "," + " current state:" + STATE_SSD110);

		String returnCode = serviceHelper.validateSecurityCode(securityCodeInput, userSession);

		if (returnCode.equalsIgnoreCase(HOOK_RETURN_0)) {
			// security code not in database
			hookReturnCode = HOOK_RETURN_3;
			hookReturnMessage = "Invalid Security Code";

		} else if (returnCode.equalsIgnoreCase(HOOK_RETURN_1)) {
			// security code present, tech is enabled

			if (passwordInput.equalsIgnoreCase("1")) {
				// tech opting for password reset
				hookReturnCode = HOOK_RETURN_5;
				hookReturnMessage = "Password reset";
			} else {
				// user has entered the password
				passwordAttemptCounter = userSession.getPasswordAttemptCounter();
				passwordAttemptCounter++;
				LOGGER.info("issueUserLogin: Session: " + sessionId + "," + " current state:" + STATE_SSD110
						+ ", Password Attempt: " + passwordAttemptCounter);
				userSession.setPasswordAttemptCounter(passwordAttemptCounter);
				hookReturnCode = serviceHelper.validateLoginPassword(passwordInput, userSession);
				if (hookReturnCode.equalsIgnoreCase(HOOK_RETURN_1)) {
					hookReturnMessage = "Valid";
				} else if (hookReturnCode.equalsIgnoreCase(HOOK_RETURN_2)) {
					hookReturnMessage = "Wrong Password";
				} else {
					hookReturnMessage = "Security Code in Jeopardy";
				}
			}

		} else {
			// security code present, tech is not enabled
			hookReturnCode = HOOK_RETURN_4;
			hookReturnMessage = "Technician Not Enabled";
		}

		cacheService.updateSession(userSession);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_SSD110 +" hookReturnCode: "+ hookReturnCode + " hookReturnMessage: "+ hookReturnMessage);
		return response;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//Name:					ValidateTechnicianProperties								//
	//Input Parameters:		session id													//
	//Global Data		:	User session map											//
	//Processing		:	After security-code and Password is validated,				//
	//						Checks for other technician properties						//
	//							-Password Expiry										//
	//							-Birthdate present in DB								//
	//							-Paging centre configuration							//
	//Output			:	Returns the hook return code.								//
	//Instructions	:		Call this function to verify user status and user login		//
	//response.																			//
	//Current State	:		SSD111														//
	//Next State		:	SSD180(1), SS0400(2), SS0155(3), SS0180(4)					//
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public IVRWebHookResponseDto issueValidateTechnicianProperties(String sessionId) {

		//IVRUserSession session = userSessionMap.get(sessionId);
		IVRUserSession session = cacheService.getBySessionId(sessionId);
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setCurrentState(STATE_SSD111);
		response.setSessionId(sessionId);
		String hookReturnCode = "";
		String hookReturnMessage = "";
		
		if (session != null) {
			// if user's expiry date is past today's date then return 92
			if (serviceHelper.hasUserExpired(session.getPasswordExpireDate())) {
				hookReturnCode = HOOK_RETURN_2;
				hookReturnMessage = "Password Expired";
			}
			// if user's birthdate is not set then return 3
//			else if (!serviceHelper.isBirthdateSet(session.getBirthdate())) {
//				hookReturnCode = HOOK_RETURN_3;
//				hookReturnMessage = "BirthDate is Missing";
//			}
			// check if user has paging centre problem , then return 91
			else if (!serviceHelper.isUserPagerEnabled(session)) {
				hookReturnCode = HOOK_RETURN_4;
				hookReturnMessage = "Paging Centre Problem";
			} else {
				hookReturnCode = HOOK_RETURN_1;
				hookReturnMessage = "Valid Technician";
			}
			// If User is able to come into SSD111, it means the person has entered the
			// correct credentials.
			// Other properties may be offset.
			resetCounters(session);
		} else {
			hookReturnCode = GPDOWN_ERR_MSG_CODE;
			hookReturnMessage = GPDOWN_ERR_MSG + ":" + INVALID_SESSION_ID;
		}
		
		cacheService.updateSession(session);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_SSD111 +" hookReturnCode: "+ hookReturnCode + " hookReturnMessage: "+ hookReturnMessage);
		return response;
	}
	
	/**
	 * Incase of successful login, all counters are reset
	 * @param user
	 */
	private void resetCounters(IVRUserSession user) {
		//reset all login counters in cache and DB
		user.setLoginJeopardyFlag(false);
		user.setPasswordAttemptCounter(0);
		serviceHelper.updateLoginJeopardyFlag(user);
		serviceHelper.updatePasswordCounter(user);
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//Name:				    ValidatePassword with Business Rules						//
	//Input Parameters:		User DTMF, session											//
	//Global Data		:	DTMF output fields (OF6 contains tech's password)			//
	//Processing		:	Validate the password										//
	//Output			:	Returns the hook return code.	
	//						Success (3)
	//						Same as OLD password(0)
	//						Length issue(1)
	//						Password same as Security code(2)
	//Instructions		:	This function is called when password needs to be validated //
	//Current State		:	SSD120											//
	//Next State		:																//
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public IVRWebHookResponseDto validatePasswordBusinessRules(String userInputDTMF, String sessionId) {
		// password should be min 6 and max 8 charecters
		// new password should not be same as old password
		// new password should not match with Employee id
		int PASSWORD_MAX_LENGTH = 8;
		int PASSWORD_MIN_LENGTH = 6;

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		String hookReturnCode = "";
		String hookReturnMessage = "";

		response.setSessionId(sessionId);
		response.setCurrentState(STATE_SSD120);
		//IVRUserSession user = userSessionMap.get(sessionId);;
		IVRUserSession user = cacheService.getBySessionId(sessionId);
		
		if (user != null) {
			String oldPassword = serviceHelper.findPasswordByEmpID(user.getEmpID());

			if (userInputDTMF.length() < PASSWORD_MIN_LENGTH || userInputDTMF.length() > PASSWORD_MAX_LENGTH) {
				hookReturnCode = HOOK_RETURN_1;
				hookReturnMessage = PASSWORD_WRONG_LENGTH;

			} // TODO: we can't fetch password from Session -> fetch current password from
				// Database
			else if (oldPassword.equalsIgnoreCase(userInputDTMF)) {
				hookReturnCode = HOOK_RETURN_0;
				hookReturnMessage = PASSWORD_SAME_AS_OLD ;

			} else if (user.getEmpID().equalsIgnoreCase(userInputDTMF)) {
				hookReturnCode = HOOK_RETURN_2;
				hookReturnMessage = PASSWORD_SAME_AS_SECURITYCODE;

			} else {
				//Passed all business validations
				hookReturnCode = HOOK_RETURN_3;
				hookReturnMessage = "OK";
				user.setNewPassword(userInputDTMF);
				response.setParameters(serviceHelper.addParamterData(userInputDTMF));
			}
		} else {
			hookReturnCode = GPDOWN_ERR_MSG_CODE;
			hookReturnMessage = GPDOWN_ERR_MSG + ":" + INVALID_SESSION_ID;
		}
		
		//update session cache 
		cacheService.updateSession(user);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_SSD120 +" hookReturnCode: "+ hookReturnCode + " hookReturnMessage: "+ hookReturnMessage);
		return response;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//Name	:				IssueUserLoginWithNewPassword								//
	//Input Parameters	:	None									,					//
	//Global Data		:	DTMF Output fields (OF6 contains the new password)			//
	//Processing		:	Issue Login Status with New Password
	//						Reset all Login Attempt Counters							//
	//Instructions		:	Call this function after collecting the new password from 	//
	//						the technician.												//
	//Current State		:	SSD135 (New Architecture diagram)
	//Next State		:	NEW:
	//						Request Failed(1) : GPDOWN
	//						Request Processed (2) : SSD180
	//                                                                                  //
	//                                                                                  //
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public IVRWebHookResponseDto issueUserLoginWithNewPassword(String userInputDTMF, String sessionId) {
		String hookReturnCode = "1";
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(STATE_SSD135);
		String hookReturnMessage = "";
		//IVRUserSession user = userSessionMap.get(sessionId);
		IVRUserSession user = cacheService.getBySessionId(sessionId);
		LOGGER.info("issueUserLoginWithNewPassword : Session: " + sessionId + ", State:" + STATE_SSD135 + ", User Input: [*******]");
		
		if (user != null) {
			if (userInputDTMF.equalsIgnoreCase("1")) {
				hookReturnCode = processNewPassword(user);
			}
			if (hookReturnCode.equalsIgnoreCase(HOOK_RETURN_2)) {
				// TODO: reset all counters
				resetCounters(user);
				int updatedRecords = serviceHelper.updatePasswordExpireDate(user);
				if(updatedRecords > 0) {
					LOGGER.info("issueUserLoginWithNewPassword : Session: " + sessionId + ", State:" + STATE_SSD135 + ", Password expire date updated");
				} else {
					LOGGER.info("issueUserLoginWithNewPassword : Session: " + sessionId + ", State:" + STATE_SSD135 + ", Password expire date Cannot be updated");
				}
				hookReturnMessage = "Password Updated Successfully";
			}
			// Since, birthdate check is not done in Password reset flow,
			// Keeping it here if need to activate it, uncomment it.
//				else if(hookReturnCode.equalsIgnoreCase(HOOK_RETURN_3)) {
//				hookReturnMessage = "Password updated successfully, birthdate is missing";
//				} 
			else {
				hookReturnMessage = GPDOWN_ERR_MSG;
				hookReturnCode = GPDOWN_ERR_MSG_CODE;
			}
		} else {
			hookReturnMessage = GPDOWN_ERR_MSG + ":" + INVALID_SESSION_ID;
			hookReturnCode = GPDOWN_ERR_MSG_CODE;
		}

		cacheService.updateSession(user);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_SSD135 + " hookReturnCode: "
				+ hookReturnCode + " hookReturnMessage: " + hookReturnMessage);
		return response;
	}

	private String processNewPassword(IVRUserSession user) {
		LOGGER.info("validateNewPassword : Calls to update in Database & returns hook code");
		int rowsUpdated = serviceHelper.updatePasswordInDB(user.getNewPassword(), user.getEmpID());
		if (rowsUpdated < 1) {
			return HOOK_RETURN_1;
		}
		//new password is successfully updated
		//now we can empty the newPassword from cache 
		user.setNewPassword("");
		
		// TODO: birthdate scenario
//		if (!serviceHelper.isBirthdateSet(user.getBirthdate())) {
//			return HOOK_RETURN_3;
//		}

		return HOOK_RETURN_2;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//Name:					CheckUSWDBProfile											//
	//Input Parameters:		None														//
	//Global Data		:	User Session Map											//
	//Processing		:	Check if the tech. has an associated default areacode.(NPA)	//
	//Output			:	Returns the hook return code.								//
	//Instructions	:		Call this function after the tech. has logged in			//
	//						successfully after the security clearance.					//
	//Current State	:		SSD180														//
	//Next State		:	GPAN01(2), SS0190(1)										//
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public IVRWebHookResponseDto checkTechAreaCode(String sessionId) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		String hookReturnCode = GPDOWN_ERR_MSG_CODE;
		String hookReturnMessage = INVALID_SESSION_ID;
		response.setSessionId(sessionId);
		response.setCurrentState(STATE_SSD180);
		IVRUserSession session = cacheService.getBySessionId(sessionId);
		List<IVRParameter> params = new ArrayList<>();
		
		if (session != null) {
			if (serviceHelper.isNpaExists(session.getNpaPrefix())) {
				hookReturnCode=  HOOK_RETURN_2;
				hookReturnMessage = "Technician has valid Area Code";
				session.setAuthenticated(true);
				LOGGER.info("checkUSWDBProfile : Session: " + sessionId + ", User :" + session.getEmpID()
						+ " is authenticated");
				response.setParameters(serviceHelper.addParamterData(session.getNpaPrefix()));

			} else {
				hookReturnCode = HOOK_RETURN_1;
				hookReturnMessage = "Technician does not have valid Area Code";
			}
		} 

		cacheService.updateSession(session);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_SSD180 +" hookReturnCode: "+ hookReturnCode
		+ " hookReturnMessage: "+ hookReturnMessage);
		return response;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//Name	:				Validate User entered NPA  									//
	//Input Parameters	:	None									,					//
	//Global Data		:	Us															//
	//Processing		:	Validates the NPA entered and returns the appropriate		//
	//						hook return code and populates Response Output Fields with	//
	//						voice messages with dynamic data.							//
	//Output			:	Returns the hook return code and populates Response Output	//
	//Instructions		:	Call this function to verify NPA							//
	//Current State		:	SSD190														//
	//Next State		:	SSE190(0), GPDOWN(1),  GPAN01(2), Other Issue(500)			//
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public IVRWebHookResponseDto validateNPA(String userInputDTMF, String sessionId) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		String hookReturnMessage = "";
		String hookReturnCode = "";
		response.setCurrentState(STATE_SSD190);
		//IVRUserSession user = userSessionMap.get(sessionId);
		IVRUserSession user = cacheService.getBySessionId(sessionId);
		if (user != null) {
			if (userInputDTMF.length() == 3) {
				user.setNpaPrefix(userInputDTMF);
				hookReturnCode = validateNpaFromDB(user);
				response.setHookReturnCode(hookReturnCode);

				if (hookReturnCode.equalsIgnoreCase(HOOK_RETURN_0)) {
					hookReturnMessage = INVALID_AREA_CODE;
					response.setParameters(serviceHelper.addParamterData(userInputDTMF));

				} else if (hookReturnCode.equalsIgnoreCase(HOOK_RETURN_1)) {
					hookReturnMessage = GPDOWN_ERR_MSG;
					hookReturnCode = GPDOWN_ERR_MSG_CODE;

				} else {
					hookReturnMessage = VALID_AREA_CODE;
				}
			} else {
				hookReturnMessage = INVALID_AREA_CODE;
				hookReturnCode = HOOK_RETURN_0;
				response.setParameters(serviceHelper.addParamterData(userInputDTMF));
			}
		} else {
			hookReturnCode = GPDOWN_ERR_MSG_CODE;
			hookReturnMessage = GPDOWN_ERR_MSG + ":" + INVALID_SESSION_ID;
		}

		cacheService.updateSession(user);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_SSD190 + " hookReturnCode: "
				+ hookReturnCode + " hookReturnMessage: " + hookReturnMessage);
		return response;
	}

	private String validateNpaFromDB(IVRUserSession user) {
		try {
			boolean isPresent = serviceHelper.isNpaPresentInDB(user.getNpaPrefix());
			if (isPresent) {
				return HOOK_RETURN_2;
			} else {
				return HOOK_RETURN_0;
			}
		} catch (Exception ex) {
			// some issue happened while fetching data
			LOGGER.error("Exception stack trace: ", ex);
			return HOOK_RETURN_1;
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//Name	:				ValidateBirthDate											//
	//Input Parameters	:	birthdate, session-id	(date is in MMDDYYYY format)		//
	//Global Data		:	DTMF output fields											//
	//Processing		:	Validate the birthdate.										//
	//Output			:	Returns the hook return code.	
	//							0 (Failed)
	//							1 (Success)
	//Instructions		:	This function is called when birthdate needs to be validated//
	//Current State		:	SSD160										//
	//Next State		:	SSD160: SSE160(0), SS0165(1)								//
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public IVRWebHookResponseDto validateBirthDate(String userInputDTMF, String sessionId) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(STATE_SSD160);
		String hookReturnCode = "";
		String hookReturnMessage = "";
		//IVRUserSession session = userSessionMap.get(sessionId);
		IVRUserSession session = cacheService.getBySessionId(sessionId);

		if (session != null) {
			hookReturnCode = serviceHelper.birthDateValidation(userInputDTMF, session);
			if (hookReturnCode.equalsIgnoreCase(HOOK_RETURN_1)) {
				hookReturnMessage = VALID_DATE_FORMAT;
				response.setParameters(serviceHelper.addParamterData(userInputDTMF));
				session.setNewBirthDate(userInputDTMF);
			} else {
				hookReturnMessage = INVALID_DATE_FORMAT;
			}
		} else {
			hookReturnCode = GPDOWN_ERR_MSG_CODE;
			hookReturnMessage = GPDOWN_ERR_MSG + ":" + INVALID_SESSION_ID;
		}

		cacheService.updateSession(session);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_SSD160 + " hookReturnCode: "
				+ hookReturnCode + " hookReturnMessage: " + hookReturnMessage);
		return response;
	}

	

	//////////////////////////////////////////////////////////////////////////////////////
	//Name	:				Save the New birthdate										//
	//Input Parameters	:	session-id			//
	//Global Data		:	DTMF output fields											//
	//Processing		:	Validate the birthdate.										//
	//Output			:	Returns the hook return code.								//
	//Instructions		:	This function is called when birthdate needs to be persisted in DB//
	//Current State		:	SSD165										//
	//Next State		:	SSD170(1) - Update successfull
	//						SSXX(0) -Update failed
	//						SXX (-1) Invalid session id
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public IVRWebHookResponseDto persistNewBirthdate(String sessionId) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(STATE_SSD165);
		String hookReturnCode = "";
		String hookReturnMessage = "";
		//IVRUserSession session = userSessionMap.get(sessionId);
		IVRUserSession session = cacheService.getBySessionId(sessionId);
		
		if (session != null && session.getNewBirthDate() != null && session.getEmpID() != null) {
			int rows = serviceHelper.updateTechnicianBirthdate(session.getNewBirthDate(), session.getEmpID());
			hookReturnCode = rows == 1 ? HOOK_RETURN_1 : HOOK_RETURN_0;
			hookReturnMessage = hookReturnCode.equalsIgnoreCase(HOOK_RETURN_1) ? "Update Successfull" : "Update Failed";
			
		} else {
			hookReturnCode = GPDOWN_ERR_MSG_CODE;
			hookReturnMessage = GPDOWN_ERR_MSG + ":" + INVALID_SESSION_ID;
		}
		
		cacheService.updateSession(session);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_SSD165 + " hookReturnCode: "
				+ hookReturnCode + " hookReturnMessage: " + hookReturnMessage);
		return response;
	}

	@Override
	public IVRWebHookResponseDto issueUserAltLogin(String userInputDTMF, String sessionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVRWebHookResponseDto issueSecCodeUpdate(String userInputDTMF, String sessionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVRWebHookResponseDto issueLogout(String userInputDTMF, String sessionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVRWebHookResponseDto playVoiceGram(String userInputDTMF, String sessionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVRWebHookResponseDto updateVoiceGram(String userInputDTMF, String sessionId) {
		// TODO Auto-generated method stub
		return null;
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//Name	:				Validate whether Tech's PAGER if configured					//
	//Input Parameters	:	Security code												//
	//Global Data		:	Session object												//
	//Processing		:	Checks if the Tech can be Paged
	//						Returns: 													//
	//							1 (valid)
	//							0 (Not valid)
	//Output			:	Returns the hook return code.								//
	//Instructions		:	This function is called when Tech's PAGER configuration
	//						needs to be validated										//
	//Current State		:	SSD150,SSD170												//
	//Next State		:	SSE150(0), SS0200(1),SS0180(0),SSD180(1)					//
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public IVRWebHookResponseDto validateTechPagerConfiguredPasswordReset(String sessionId) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setCurrentState(STATE_SSD150);
		response.setSessionId(sessionId);
		String HOOK_RETURN_CODE = "";
		String HOOK_RETURN_MESSAGE = "";
		//IVRUserSession user = userSessionMap.get(sessionId);
		IVRUserSession user = cacheService.getBySessionId(sessionId);
		if(user != null) {
			HOOK_RETURN_CODE = serviceHelper.isUserPagerEnabled(user) ? "1" : "0";
			HOOK_RETURN_MESSAGE = HOOK_RETURN_CODE.equals(HOOK_RETURN_1) ? VALID_PAGER_MESSAGE : INVALID_PAGER_MESSAGE;
		} else {
			HOOK_RETURN_CODE = GPDOWN_ERR_MSG_CODE;
			HOOK_RETURN_MESSAGE = GPDOWN_ERR_MSG + ":" + INVALID_SESSION_ID;
		}
		
		cacheService.updateSession(user);
		response.setHookReturnCode(HOOK_RETURN_CODE);
		response.setHookReturnMessage(HOOK_RETURN_MESSAGE);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_SSD150 + " hookReturnCode: "
				+ HOOK_RETURN_CODE + " hookReturnMessage: " + HOOK_RETURN_MESSAGE);
		return response;
	}
	
	//Current State: SSD170	
	@Override
	public IVRWebHookResponseDto validateTechPagerConfiguredBirthdate(String sessionId) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setCurrentState(STATE_SSD170);
		response.setSessionId(sessionId);
		String HOOK_RETURN_CODE = "";
		String HOOK_RETURN_MESSAGE = "";
//		IVRUserSession user = userSessionMap.get(sessionId);
		IVRUserSession user = cacheService.getBySessionId(sessionId);
		if(user != null) {
			HOOK_RETURN_CODE = serviceHelper.isUserPagerEnabled(user) ? "1" : "0";
			if(HOOK_RETURN_CODE.equals(HOOK_RETURN_1)) {
			HOOK_RETURN_MESSAGE = VALID_PAGER_MESSAGE;
			} else {
				HOOK_RETURN_MESSAGE = INVALID_PAGER_MESSAGE;
			}
		} else {
			HOOK_RETURN_CODE = GPDOWN_ERR_MSG_CODE;
			HOOK_RETURN_MESSAGE = GPDOWN_ERR_MSG + ":" + INVALID_SESSION_ID;
		}
		
		cacheService.updateSession(user);
		response.setHookReturnCode(HOOK_RETURN_CODE);
		response.setHookReturnMessage(HOOK_RETURN_MESSAGE);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_SSD170 + " hookReturnCode: "
				+ HOOK_RETURN_CODE + " hookReturnMessage: " + HOOK_RETURN_MESSAGE);
		return response;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//Name	:				Validate Security Code from Session									//
	//Input Parameters	:	Security code												//
	//Global Data		:	Session object											//
	//Processing		:	Checks if the security code in session matches with the
	//						entered security code.
	//						Returns: 
	//							1 (valid)
	//							0 (Not valid)
	//Output			:	Returns the hook return code.								//
	//Instructions		:	This function is called when security code needs to be validated//
	//Current State		:	SSD210												//
	//Next State		:	SSD220(1), SSE211(0)								//
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public IVRWebHookResponseDto validateTechSecurityCodeFromSession(String userInputDTMF, String sessionId) {
		// fetch the security code from Session object and match with the user input
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setCurrentState(STATE_SSD210);
		response.setSessionId(sessionId);
		String HOOK_RETURN_CODE = "0";
		String HOOK_RETURN_MESSAGE = "";
//		IVRUserSession userSession = userSessionMap.get(sessionId);
		IVRUserSession userSession = cacheService.getBySessionId(sessionId);
		if (userSession != null) {
			String existingSecurityCode = userSession.getEmpID();
			if (!userInputDTMF.isBlank() && userInputDTMF.equalsIgnoreCase(existingSecurityCode)) {
				HOOK_RETURN_CODE = "1";
				HOOK_RETURN_MESSAGE = "Valid Security Code";
			} else {
				HOOK_RETURN_MESSAGE = "Invalid Security Code";
			}
		} else {
			HOOK_RETURN_CODE = GPDOWN_ERR_MSG_CODE;
			HOOK_RETURN_MESSAGE = GPDOWN_ERR_MSG + ":" + INVALID_SESSION_ID;
		}

		cacheService.updateSession(userSession);
		response.setHookReturnCode(HOOK_RETURN_CODE);
		response.setHookReturnMessage(HOOK_RETURN_MESSAGE);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_SSD210 + " hookReturnCode: "
				+ HOOK_RETURN_CODE + " hookReturnMessage: " + HOOK_RETURN_MESSAGE);
		return response;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//Name	:				Generate PAGER OTP & Send it to CUID						//
	//Input Parameters	:	Session id													//
	//Global Data		:	Session object												//
	//Processing		:	Checks if the security code in session matches with the		//
	//						entered security code.										//
	//						Returns: 
	//							1 (OTP generated)
	//							0 (OTP generation Failed)
	//Output			:	Returns the hook return code.								//
	//Instructions		:	This function is called when security code needs to be validated//
	//Current State		:	SSD220												//
	//Next State		:	SS0300(1), SSE210(0)								//
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public IVRWebHookResponseDto generatePagerOTP(String sessionId)  {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setCurrentState(STATE_SSD220);
		response.setSessionId(sessionId);

		// Setting values for Failed OTP Generation
		String HOOK_RETURN_CODE = HOOK_RETURN_0;
		String HOOK_RETURN_MESSAGE = OTP_GEN_FAILED;
		IVRUserSession userSession = null;
		userSession = cacheService.getBySessionId(sessionId);
		if (userSession != null) {
			String otp = serviceHelper.generateFourDigitOtp();

			// TODO: persist the OTP in session using hashing alogorithm like Bcrypt
			userSession.setOtpGenerated(otp);
			try {
				String responseString = serviceHelper.pushOtpToTechCuid(userSession);
				LOGGER.info("Session id: " + sessionId + ": Response from NET OTP API: " + responseString);
				if (serviceHelper.processJsonStringNETMessaging(responseString)) {
					HOOK_RETURN_CODE = "1";
					HOOK_RETURN_MESSAGE = "Otp generated";
					String data = userSession.isCanBePagedMobile() ? NET_PHONE_DEVICE : NET_MAIL_DEVICE;
					response.setParameters(serviceHelper.addParamterData(data));
				}
			}catch (JsonProcessingException e) {
				HOOK_RETURN_CODE = HOOK_RETURN_0;
				HOOK_RETURN_MESSAGE = OTP_GEN_FAILED;
			 }
		} else {
			HOOK_RETURN_CODE = GPDOWN_ERR_MSG_CODE;
			HOOK_RETURN_MESSAGE = GPDOWN_ERR_MSG + ":" + INVALID_SESSION_ID;
		}

		cacheService.updateSession(userSession);
		response.setHookReturnCode(HOOK_RETURN_CODE);
		response.setHookReturnMessage(HOOK_RETURN_MESSAGE);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_SSD220 + " hookReturnCode: "
				+ HOOK_RETURN_CODE + " hookReturnMessage: " + HOOK_RETURN_MESSAGE);
		return response;
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//Name	:				Generate PAGER OTP & Send it to CUID						//
	//Input Parameters	:	Session id													//
	//Global Data		:	Session object												//
	//Processing		:	Checks if the security code in session matches with the		//
	//						entered security code.										//
	//						Returns: 
	//							1 (Valid)
	//							0 (Invalid)
	//Output			:	Returns the hook return code.								//
	//Instructions		:	This function is called when security code needs to be validated//
	//Current State		:	SSD300												//
	//Next State		:	SS0120(1), SSE320(0)								//
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public IVRWebHookResponseDto validatePagerOtp(String userInputDTMF, String sessionId) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setCurrentState(STATE_SSD300);
		response.setSessionId(sessionId);
		String HOOK_RETURN_CODE = "0";
		String HOOK_RETURN_MESSAGE = "";
		//IVRUserSession session = userSessionMap.get(sessionId);
		IVRUserSession session = cacheService.getBySessionId(sessionId);
		if (session != null) {
			String generatedOtp = session.getOtpGenerated();
			
			//TODO: Implement secure matching of OTP using PasswordEncoder Bcrypt algo
			if(userInputDTMF.equalsIgnoreCase(generatedOtp)) {
				HOOK_RETURN_CODE = HOOK_RETURN_1;
				HOOK_RETURN_MESSAGE = VALID_OTP;
			} else {
				HOOK_RETURN_CODE = HOOK_RETURN_0;
				HOOK_RETURN_MESSAGE = INVALID_OTP;
			}
		} else {
			HOOK_RETURN_CODE = GPDOWN_ERR_MSG_CODE;
			HOOK_RETURN_MESSAGE = GPDOWN_ERR_MSG + ":" + INVALID_SESSION_ID;
		}
		
		cacheService.updateSession(session);
		response.setHookReturnCode(HOOK_RETURN_CODE);
		response.setHookReturnMessage(HOOK_RETURN_MESSAGE);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_SSD300 + " hookReturnCode: "
				+ HOOK_RETURN_CODE + " hookReturnMessage: " + HOOK_RETURN_MESSAGE);
		return response;
	}
	
	

}
