package com.lumen.fastivr.IVRAdmin;

import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRSignon.IVRSignOnInterface;
import com.lumen.fastivr.IVRSignon.IVRSignOnServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.lumen.fastivr.IVRUtils.IVRConstants.*;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.*;

@Service
public class IVRAdminServiceImpl implements IVRAdminInterface {

    @Autowired
    private IVRSignOnInterface ivrSignOn;

    @Autowired
    private IVRCacheService cacheService;

    @Autowired
    private IVRSignOnServiceHelper serviceHelper;

    final static Logger LOGGER = LoggerFactory.getLogger(IVRAdminServiceImpl.class);
    
    /**
	 * State : ADD010
	 * Return Code:
	 * Success (2)
		Technician has Default Area Code(0)
		Technician enters Area Code on each call(1)
	 */

    @Override
	public IVRWebHookResponseDto validateVariableAreaCode(String userInputDTMF, String sessionId) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		String hookReturnCode = new String();
		String hookReturnMessage = new String();
		IVRUserSession session = cacheService.getBySessionId(sessionId);

		if (null != session && session.isVariableNpaFlag()) {

			hookReturnCode = HOOK_RETURN_1;
			hookReturnMessage = VARIABLE_AREA_CODE_NOT_PRESENT;
			LOGGER.info("Areacodevariablrflag : Session: " + sessionId + ", User :" + session.getEmpID()
					+ " is authenticated");

		} else {
			hookReturnCode = HOOK_RETURN_0;
			hookReturnMessage = VARIABLE_AREA_CODE_SAME_AS_DEFAULT;
		}

		response.setHookReturnCode(hookReturnCode);
		response.setSessionId(sessionId);
		response.setHookReturnMessage(hookReturnMessage);
		response.setCurrentState(STATE_ADD010);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_ADD010 + " hookReturnCode: "
				+ hookReturnCode + " hookReturnMessage: " + hookReturnMessage);
		return response;
	}


    /**
     * State : ADD020
     * Return Code:
     * Success (3)
     * Same as OLD password(0)
     * Length issue(3)
     * Password same as Security code(2)
     */
    @Override
    public IVRWebHookResponseDto validatePassword(String userInputDTMF, String sessionId) {

    		// password should be min 6 and max 8 charecters
    		// new password should not be same as old password
    		// new password should not match with Employee id
    		int PASSWORD_MAX_LENGTH = 8;
    		int PASSWORD_MIN_LENGTH = 6;

    		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
    		String hookReturnCode = "";
    		String hookReturnMessage = "";

    		response.setSessionId(sessionId);
    		response.setCurrentState(STATE_ADD020);
    		//IVRUserSession user = userSessionMap.get(sessionId);;
    		IVRUserSession user = cacheService.getBySessionId(sessionId);
    		
    		if (user != null) {
    			String oldPassword = serviceHelper.findPasswordByEmpID(user.getEmpID());

    			if (userInputDTMF.length() < PASSWORD_MIN_LENGTH || userInputDTMF.length() > PASSWORD_MAX_LENGTH) {
    				hookReturnCode = HOOK_RETURN_2;
    				hookReturnMessage = PASSWORD_WRONG_LENGTH;

    			} // TODO: we can't fetch password from Session -> fetch current password from
    				// Database
    			else if (oldPassword.equalsIgnoreCase(userInputDTMF)) {
    				hookReturnCode = HOOK_RETURN_1;
    				hookReturnMessage = PASSWORD_SAME_AS_OLD ;

    			} else if (user.getEmpID().equalsIgnoreCase(userInputDTMF)) {
    				hookReturnCode = HOOK_RETURN_3;
    				hookReturnMessage = PASSWORD_SAME_AS_SECURITYCODE;

    			} else {
    				//Passed all business validations
    				hookReturnCode = HOOK_RETURN_4;
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
    	

    

    private void resetCounters(IVRUserSession user) {
		//reset all login counters in cache and DB
		user.setLoginJeopardyFlag(false);
		user.setPasswordAttemptCounter(0);
		serviceHelper.updateLoginJeopardyFlag(user);
		serviceHelper.updatePasswordCounter(user);
	}
    /**
     * State : ADD035
     * Return code: 0 (Success),
     * 0(Failure)
     */
    @Override
    public IVRWebHookResponseDto issueUserLoginWithNewPassword(String userInputDTMF, String sessionId) {
        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
    		String hookReturnCode = "1";
    		response.setSessionId(sessionId);
    		response.setCurrentState(STATE_ADD035);
    		String hookReturnMessage = "";
    		//IVRUserSession user = userSessionMap.get(sessionId);
    		IVRUserSession user = cacheService.getBySessionId(sessionId);
    		LOGGER.info("issueUserLoginWithNewPassword : Session: " + sessionId + ", State:" + STATE_SSD135 + ", User Input: [*******]");
    		
    		if (user != null) {
    			if (userInputDTMF.equalsIgnoreCase("1")) {
    				hookReturnCode = processNewPasswordforAdmin(user);
    			}
    			if (hookReturnCode.equalsIgnoreCase(HOOK_RETURN_0)) {
    				// TODO: reset all counters
    				resetCounters(user);
    				int updatedRecords = serviceHelper.updatePasswordExpireDate(user);
    				if(updatedRecords > 0) {
    					LOGGER.info("issueUserLoginWithNewPassword : Session: " + sessionId + ", State:" + STATE_ADD035 + ", Password expire date updated");
    				} else {
    					LOGGER.info("issueUserLoginWithNewPassword : Session: " + sessionId + ", State:" + STATE_ADD035 + ", Password expire date Cannot be updated");
    				}
    				hookReturnMessage = "Password Updated Successfully";
    			}
    			// Since, birthdate check is not done in Password reset flow,
    			// Keeping it here if need to activate it, uncomment it.
//    				else if(hookReturnCode.equalsIgnoreCase(HOOK_RETURN_3)) {
//    				hookReturnMessage = "Password updated successfully, birthdate is missing";
//    				} 
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
    		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_ADD035 + " hookReturnCode: "
    				+ hookReturnCode + " hookReturnMessage: " + hookReturnMessage);
    		return response;
    	}


    private String processNewPasswordforAdmin(IVRUserSession user) {
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

		return HOOK_RETURN_0;
	}
////////////////////////////////////////////////////////////////////////////////////
//	Name:				ValidateAreacode
//	Input Parameters:
//  Processing		:	Validate the new areacode entered by the tech.
//  Output			:	Hook return code
//	Current State	:	ADD110
//	Next State		:	ADE110(1), ADE111(2), AD0115(3)
////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IVRWebHookResponseDto validateAreaCode(String userInputDTMF, String sessionId) {


        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        String hookReturnCode = new String();
        String hookReturnMessage = new String();

        IVRUserSession session = cacheService.getBySessionId(sessionId);

        if (null != session) {
        	String tempNpa = session.getNpaPrefix();
        	// 2 - success, 0 -failure 
            String returnCode = ivrSignOn.validateNPA(userInputDTMF, sessionId).getHookReturnCode();
            
            if (userInputDTMF.equalsIgnoreCase(tempNpa)) {
                hookReturnCode = HOOK_RETURN_1;
                hookReturnMessage = AREA_CODE_SAME_AS_OLD;
                response.setParameters(serviceHelper.addParamterData(userInputDTMF));

            }else if(HOOK_RETURN_2.equalsIgnoreCase(returnCode)){
                hookReturnCode = HOOK_RETURN_3;
                hookReturnMessage = OK;
                response.setParameters(serviceHelper.addParamterData(userInputDTMF));
            } 
            else {
                hookReturnCode = HOOK_RETURN_2;
                hookReturnMessage = INVALID_AREA_CODE;
                response.setParameters(serviceHelper.addParamterData(userInputDTMF));
            } 
        }

        cacheService.updateSession(session);
        response.setSessionId(sessionId);
        response.setCurrentState(STATE_ADD110);
        response.setHookReturnCode(hookReturnCode);
        response.setHookReturnMessage(hookReturnMessage);
        LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_ADD110 + " hookReturnCode: " + hookReturnCode + " hookReturnMessage: " + hookReturnMessage);
        return response;

    }


    ////////////////////////////////////////////////////////////////////////////////////
//	Name:				GetCurrentAreaCode
//	Input Parameters:
//  Processing		:	Get the current areacode of the technician from user profile
//  Output			:	Returns Hook code
//	Current State	:	ADD100
//	Next State		:	AD0105(0 to 9)
////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IVRWebHookResponseDto getCurrentAreaCode(String sessionId) {

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        String hookReturnCode = "";
        String hookReturnMessage = "";
        response.setSessionId(sessionId);
        response.setCurrentState(STATE_ADD100);
        IVRUserSession session = cacheService.getBySessionId(sessionId);
        if (null != session && serviceHelper.isNpaExists(session.getNpaPrefix())) {
            hookReturnCode = HOOK_RETURN_0;
            hookReturnMessage = AREA_CODE_FOUND;
            LOGGER.info("getCurrentAreaCode : Session: " + sessionId + ", User :" + session.getEmpID());
            response.setParameters(serviceHelper.addParamterData(session.getNpaPrefix()));
        }
        cacheService.updateSession(session);
        response.setHookReturnCode(hookReturnCode);
        response.setHookReturnMessage(hookReturnMessage);
        LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_ADD100 + " hookReturnCode: " + hookReturnCode + " hookReturnMessage: " + hookReturnMessage);
        return response;

    }


    ////////////////////////////////////////////////////////////////////////////////////
//	Name:				IssueSetTechNpaRequest
//	Input Parameters:
//  Processing		:	Sends the request to fas to update the technician's areacode
//  Output			:	Returns Hook code
//	Current State	:	ADD500
//	Next State		:	AD0500(0),GPDOWN(1)
////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IVRWebHookResponseDto setTechNPARequest(String userInputDTMF, String sessionId) {

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        String hookReturnCode;
        String hookReturnMessage;
        int rowsUpdated = 0;

        IVRUserSession session = cacheService.getBySessionId(sessionId);
        String npaPrefix = session.getNpaPrefix();
        LOGGER.info("setTechNPARequest : Calls to update in Database & returns hook code");
        if (!session.isVariableNpaFlag()) {
            rowsUpdated = serviceHelper.updateAreaCodeInDB(npaPrefix, session.getEmpID());
        }
        if (rowsUpdated > 0 || session.isVariableNpaFlag()) {
            hookReturnCode = HOOK_RETURN_0;
            hookReturnMessage = UPDATE_AREA_CODE;
        } else {
            hookReturnMessage = GPDOWN_ERR_MSG;
            hookReturnCode = HOOK_RETURN_1;
        }


        cacheService.updateSession(session);
        response.setSessionId(sessionId);
        response.setCurrentState(STATE_ADD500);
        response.setHookReturnCode(hookReturnCode);
        response.setHookReturnMessage(hookReturnMessage);
        LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_ADD500 + " hookReturnCode: " + hookReturnCode + " hookReturnMessage: " + hookReturnMessage);
        return response;

    }


    /**
	 * State : ADD550
	 * Return code: 2 (Success), 
	 *  0(Failure)
	 */
	@Override
	public IVRWebHookResponseDto issueSetVarNpaRequest(String userInputDTMF, String sessionId) {
			
		   IVRWebHookResponseDto response = new IVRWebHookResponseDto();
	        String hookReturnCode;
	        String hookReturnMessage;
			IVRUserSession session = cacheService.getBySessionId(sessionId);
			
			boolean	variableAreacode	=	session.isVariableNpaFlag();
			int rowsUpdated = 0;
			
			//toggle happens here 
	       variableAreacode = variableAreacode ? false: true;
	       
	        if (variableAreacode) {
	            rowsUpdated = serviceHelper.updateAreaCodeInDB(null, session.getEmpID());
	        }else {
	        	rowsUpdated = serviceHelper.updateAreaCodeInDB(session.getNpaPrefix(), session.getEmpID());
	        }
	        
	        if (rowsUpdated > 0) {
	            hookReturnCode = HOOK_RETURN_0;
	            hookReturnMessage = UPDATE_AREA_CODE;
	        } else {
	            hookReturnMessage = GPDOWN_ERR_MSG;
	            hookReturnCode = HOOK_RETURN_1;
	        }


	        session.setVariableNpaFlag(variableAreacode);
	        cacheService.updateSession(session);
	        response.setSessionId(sessionId);
	        response.setCurrentState(STATE_ADD550);
	        response.setHookReturnCode(hookReturnCode);
	        response.setHookReturnMessage(hookReturnMessage);
	        LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_ADD550 + " hookReturnCode: " + hookReturnCode + " hookReturnMessage: " + hookReturnMessage);
	        return response;
	}
			


    ////////////////////////////////////////////////////////////////////////////////////
//	Name:				voiceResults
//	Input Parameters:
//  Output			:	Returns Hook code
//	Current State	:	ADD555
//	Next State		:	Main menu
////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IVRWebHookResponseDto voiceResults(String sessionId) {

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        String hookReturnCode;
        String hookReturnMessage;

        IVRUserSession session = cacheService.getBySessionId(sessionId);
        session.setCanBePagedEmail(Boolean.TRUE);
        session.setCanBePagedMobile(Boolean.FALSE);

            hookReturnMessage = RETURN_TO_MAIN_MENU;
            hookReturnCode = HOOK_RETURN_0;

        cacheService.updateSession(session);
        response.setSessionId(sessionId);
        response.setCurrentState(STATE_ADD555);
        response.setHookReturnCode(hookReturnCode);
        response.setHookReturnMessage(hookReturnMessage);
        LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_ADD555 + " hookReturnCode: " + hookReturnCode + " hookReturnMessage: " + hookReturnMessage);
        return response;

    }



}
