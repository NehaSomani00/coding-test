package com.lumen.fastivr.IVRService;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lumen.fastivr.IVRAdmin.IVRAdminInterface;
import com.lumen.fastivr.IVRBusinessException.BadUserInputException;
import com.lumen.fastivr.IVRCANST.service.IVRCanstService;
import com.lumen.fastivr.IVRCANST.utils.IVRCANSTConstants;
import com.lumen.fastivr.IVRCNF.service.IVRCnfService;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRCallerId.IVRCallerIdService;
import com.lumen.fastivr.IVRChangeStatusCablePair.IVRChangeStatusCablePairService;
import com.lumen.fastivr.IVRConstruction.service.IVRConstructionService;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRLFACS.IVRLfacsService;
import com.lumen.fastivr.IVRMLT.caching.IvrMltCacheService;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRMLT.service.IvrMltService;
import com.lumen.fastivr.IVRMLT.utils.IVRMltConstants;
import com.lumen.fastivr.IVRSignon.IVRSignOnInterface;
import com.lumen.fastivr.IVRStateManagement.IVRState;
import com.lumen.fastivr.IVRStateManagement.IVRStateSystem;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.IVRUtils.IVRUtility;
import org.apache.logging.log4j.util.Strings;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.lumen.fastivr.IVRCANST.utils.IVRCANSTConstants.*;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.STATE_FND741;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.*;
import static com.lumen.fastivr.IVRUtils.IVRConstants.*;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.*;

@Service
public class IVRServiceImpl implements IVRService {

    @Autowired
    private IvrMltService ivrMltService;

    @Autowired
    private IVRSignOnInterface ivrSignOn;

    @Autowired
    private IVRCacheService cacheService;

    @Autowired
    private IvrMltCacheService mltCacheService;

    @Autowired
    private IVRStateSystem ivrStateSystem;

    @Autowired
    private IVRLfacsService ivrLfacsService;

    @Autowired
    private IVRCnfService ivrCnfService;

    @Autowired
    private IVRAdminInterface ivrAdminInterface;

    @Autowired
    private IVRChangeStatusCablePairService ivrlChangeStatusCablePairService;

    @Autowired
    IVRConstructionService ivrConstructionService;

    @Autowired
    private IVRCanstService ivrCanstService;

    @Autowired
    private IVRCallerIdService ivrCallerIdService;


    final static Logger LOGGER = LoggerFactory.getLogger(IVRServiceImpl.class);


    /**
     * This service method is to redirect to the appropriate MLT states depending on the user input(s)
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    @Override
    public IVRWebHookResponseDto processMLT(String sessionId, String genesysState, List<String> userInputDTMFList) throws JsonMappingException, JsonProcessingException {
        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        response.setSessionId(sessionId);
        response.setHookReturnCode(IVRConstants.INVALID_SESSION_ID);
        response.setHookReturnMessage(GPDOWN_ERR_MSG_CODE);
        String backendState = findNextState(sessionId, genesysState, userInputDTMFList);
        response.setCurrentState(backendState);
        LOGGER.info("Session: " + sessionId + "," + " Backend state:" + backendState + ", User input:"+ userInputDTMFList);

        if(cacheService.getBySessionId(sessionId) != null &&
                (mltCacheService.getBySessionId(sessionId) != null || isMltTestableState(backendState))) {

            switch (backendState) {

                case STATE_MLD026:
                    // check whether tech is calling from the same line
                    ivrMltService.checkTechWorkingFromSameLineOrNot(sessionId, backendState, userInputDTMFList.get(0));
                    // issue MLT test request
                    response = ivrMltService.issueMLTTest(sessionId);
                    LOGGER.info("Session :" + sessionId + " issueMLTTest async task ongoing in background");
                    logOutputResponse(response);
                    break;
                    
                case STATE_MLD027:
                	// issue MLT test request
                	//this will be only called from ML0110, so resetting the MLT Response
                	resetMltResponse(sessionId);
                    response = ivrMltService.issueMLTTest(sessionId);
                    LOGGER.info("Session :" + sessionId + " issueMLTTest async task ongoing in background");
                    logOutputResponse(response);
                    break;

                //To be called from ML0040
                case STATE_MLD040:
                    // no alpha pager, and not calling from the same line, need to communicate by
                    // voice and email
                    LOGGER.info("Session :" + sessionId + " retriveMLTResult started");
                    response = ivrMltService.retriveMLTResult(sessionId);
                    logOutputResponse(response);
                    break;

                case STATE_MLD021:
                    response = ivrMltService.processMLD021(sessionId, backendState, userInputDTMFList);
                    logOutputResponse(response);
                    break;

                case STATE_MLD075:
                    response = ivrMltService.checkPlayVoltageInformation(sessionId, backendState);
                    if (response.getHookReturnCode().equals(HOOK_RETURN_0)) {
                        // STATE_MLD080
                        response = ivrMltService.playTipRingDcOhm(sessionId, IVRMltConstants.STATE_MLD080);
                    }
                    logOutputResponse(response);
                    break;

                case STATE_MLD084:
                    response = ivrMltService.playRingGroundDcOhm(sessionId, backendState);
                    logOutputResponse(response);
                    break;

                case STATE_MLD086:
                    response = ivrMltService.playTipGroundVolts(sessionId, backendState);
                    logOutputResponse(response);
                    break;

                case STATE_MLD082:
                    response = ivrMltService.playTipGroundDcOhm(sessionId, backendState);
                    logOutputResponse(response);
                    break;

                case STATE_MLD088:
                    response = ivrMltService.playRingGroundVolts(sessionId, backendState);
                    logOutputResponse(response);
                    break;

                case STATE_MLD090:
                    response = ivrMltService.playTipRingAcOhm(sessionId, backendState);
                    logOutputResponse(response);
                    break;

                case STATE_MLD092:
                    response = ivrMltService.playTipGroundAcOhm(sessionId, backendState);
                    logOutputResponse(response);
                    break;

                case STATE_MLD094:
                    response = ivrMltService.playRingGroundAcOhm(sessionId, backendState);
                    logOutputResponse(response);
                    break;

                case STATE_MLD300:
                    response = ivrMltService.validateFacsTnTone(sessionId, backendState, userInputDTMFList );
                    logOutputResponse(response);
                    break;

                case STATE_MLD500:
                    response = ivrMltService.validateFacsTnTone(sessionId, backendState, userInputDTMFList );
                    logOutputResponse(response);
                    break;

                case STATE_MLD307:
                    response = ivrMltService.addToneDuration_MLD307(sessionId, userInputDTMFList.get(0));
                    logOutputResponse(response);
                    break;

                case STATE_MLD310:
                    response = ivrMltService.issueTonePlusRequest(sessionId, userInputDTMFList.get(0));
//                    if (response.getHookReturnCode().equals(HOOK_RETURN_1)) {
//                        // STATE MLD340
//                        response = ivrMltService.retriveMLTResult(sessionId);
//                        response.setCurrentState(STATE_MLD340);
//                    }
                    logOutputResponse(response);
                    break;

                case STATE_MLD340:
                    response = ivrMltService.retriveMLTResult(sessionId);
                    response.setCurrentState(STATE_MLD340);
                    logOutputResponse(response);
                    break;

                case STATE_MLD510:
                    response = ivrMltService.issueXRequest(sessionId);
                    logOutputResponse(response);
                    break;
                default:
                    response.setHookReturnMessage(INACTIVE_STATE);
            }
        }

        return response;
    }

	private void resetMltResponse(String sessionId) {
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		mltSession.setMltTestResult(null);
		mltCacheService.updateSession(mltSession);
	}

    private void logOutputResponse(IVRWebHookResponseDto response) {
        try {
            LOGGER.info("Session:  " + response.getSessionId() + "," + " current state:" + response.getCurrentState() + " hookReturnCode: "
                    + response.getHookReturnCode() + " hookReturnMessage: " + response.getHookReturnMessage());
        } catch(NullPointerException e) {
            LOGGER.error("Null in IVR Output Response");
        }
    }

    private boolean isMltTestableState(String backendState) {
        List<String> mltTestableStates = Arrays.asList(STATE_MLD021, STATE_MLD300, STATE_MLD500);
        if(mltTestableStates.contains(backendState)) {
            return true;
        }
        return false;
    }

    @Override
    public IVRWebHookResponseDto processSignOn(String sessionId, String currentState, List<String> userInputDTMFList) {

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();

        String nextState = findNextState(sessionId, currentState, userInputDTMFList);

        // validate user session
        // if user session is not present, then it's a fresh session
        // redirect to case STATE_SSD100 to issue User Status

        // need to implement a logic where if User-session exists then
        // it should check if user is authenticated
        // if user is not authenticated then User should be redirected to initial STATE

        if (!nextState.equalsIgnoreCase(STATE_SSD120)) {

            LOGGER.info("Session: " + sessionId + "," + " Backend state:" + nextState + ", User input:"
                    + userInputDTMFList);
        } else {

            LOGGER.info("Session: " + sessionId + "," + " Backend state:" + nextState + ", User input:" + "[*******]");
        }

        try {
            switch (nextState) {

                case STATE_SSD110:
                    response = ivrSignOn.issueUserLogin(userInputDTMFList, sessionId);
                    if (response.getHookReturnCode().equals(HOOK_RETURN_1)) {
                        // STATE SSD111
                        response = ivrSignOn.issueValidateTechnicianProperties(sessionId);

                        if (response.getHookReturnCode().equals(HOOK_RETURN_1)) {
                            // STATE SSD180
                            response = ivrSignOn.checkTechAreaCode(sessionId);
                        }
                    } else if (response.getHookReturnCode().equals(HOOK_RETURN_5)) {
                        // SSD150
                        response = ivrSignOn.validateTechPagerConfiguredPasswordReset(sessionId);
                    }
                    break;

                case STATE_SSD120:
                    response = ivrSignOn.validatePasswordBusinessRules(userInputDTMFList.get(0), sessionId);
                    break;

                case STATE_SSD135:
                    response = ivrSignOn.issueUserLoginWithNewPassword(userInputDTMFList.get(0), sessionId);
                    if (response.getHookReturnCode().equals(HOOK_RETURN_2)) {
                        response = ivrSignOn.checkTechAreaCode(sessionId);
                    }
                    break;

                case STATE_SSD150:
                    response = ivrSignOn.validateTechPagerConfiguredPasswordReset(sessionId);
                    break;

                case STATE_SSD210:
                    response = ivrSignOn.validateTechSecurityCodeFromSession(userInputDTMFList.get(0), sessionId);
                    if (response.getHookReturnCode().equals(HOOK_RETURN_1)) {
                        // SSD220
                        response = ivrSignOn.generatePagerOTP(sessionId);
                    }
                    break;

                case STATE_SSD220:
                    response = ivrSignOn.generatePagerOTP(sessionId);
                    break;

                case STATE_SSD300:
                    response = ivrSignOn.validatePagerOtp(userInputDTMFList.get(0), sessionId);
                    break;

                case STATE_SSD160:
                    response = ivrSignOn.validateBirthDate(userInputDTMFList.get(0), sessionId);
                    break;

                case STATE_SSD165:
                    response = ivrSignOn.persistNewBirthdate(sessionId);
                    if (response.getHookReturnCode().equals(HOOK_RETURN_0)
                            || response.getHookReturnCode().equals(HOOK_RETURN_1)) {
                        // SSD170
                        response = ivrSignOn.validateTechPagerConfiguredBirthdate(sessionId);
                        if (response.getHookReturnCode().equals(HOOK_RETURN_1)) {
                            // SSD180
                            response = ivrSignOn.checkTechAreaCode(sessionId);
                        }
                    }
                    break;

                case STATE_SSD170:
                    response = ivrSignOn.validateTechPagerConfiguredBirthdate(sessionId);
                    if (response.getHookReturnCode().equals(HOOK_RETURN_1)) {
                        // SSD180
                        response = ivrSignOn.checkTechAreaCode(sessionId);
                    }
                    break;

                case STATE_SSD180:
                    response = ivrSignOn.checkTechAreaCode(sessionId);
                    break;
                case STATE_SSD190:
                    response = ivrSignOn.validateNPA(userInputDTMFList.get(0), sessionId);
                    break;

                default:
                    throw new BadUserInputException(sessionId, INACTIVE_STATE, currentState);
            }
        } catch (BadUserInputException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Error in Sign On Flow: ", e);
            response.setSessionId(sessionId);
            response.setCurrentState(nextState);
            response.setHookReturnCode(GPDOWN_ERR_MSG_CODE);
            response.setHookReturnMessage(GPDOWN_ERR_MSG);
        }
        return response;
    }

    @Override
    public IVRWebHookResponseDto processLFACS(String sessionId, String currentState, List<String> userInputDTMFList) throws JsonMappingException, JsonProcessingException {

        IVRWebHookResponseDto response = null;

        String nextState = findNextState(sessionId, currentState, userInputDTMFList);

        switch (nextState) {

            case STATE_FID011:
                response = ivrLfacsService.processFID011(sessionId, nextState, userInputDTMFList.get(0));
                break;

            case STATE_FID020:
                response = ivrLfacsService.processFID020Code(sessionId, nextState);
                break;

            case STATE_FID025:
                response = ivrLfacsService.processFID025Code(sessionId, nextState, userInputDTMFList.get(0));
                break;

            case STATE_FID500:
                response = ivrLfacsService.processFID500Code(sessionId, nextState, userInputDTMFList);
                if (IVRHookReturnCodes.HOOK_RETURN_6.equalsIgnoreCase(response.getHookReturnCode())) {

                    response = ivrLfacsService.processFID055Code(sessionId, STATE_FID055, userInputDTMFList.get(0));
                }
                break;

            case STATE_FID035:

                response = ivrLfacsService.processFID035Code(sessionId, nextState);

                if (HOOK_RETURN_2.equalsIgnoreCase(response.getHookReturnCode())) {

                    response = ivrLfacsService.processFID400Code(sessionId, STATE_FID400, HOOK_RETURN_2);
                }

                break;

            case STATE_FID045:
                response = ivrLfacsService.processFID045Code(sessionId, nextState);
                break;

            case STATE_FID400:

                response = ivrLfacsService.processFID400Code(sessionId, nextState, userInputDTMFList.get(0));
                break;

            case STATE_FID420:

                response = ivrLfacsService.processFID420Code(sessionId, nextState);
                break;

            case STATE_FID429:

                response = ivrLfacsService.processFID429Code(sessionId, nextState);
                break;

            case STATE_FID445:

                String userDMInput = userInputDTMFList.get(0);
                response = ivrLfacsService.processFID445Code(sessionId, currentState, nextState, StringUtils.isNotBlank(userDMInput) ? Integer.parseInt(userDMInput) : 0);
                break;

            case STATE_FID455:
                response = ivrLfacsService.processFID455Code(sessionId, nextState);
                break;

            case IVRConstants.STATE_FID068:
                response = ivrLfacsService.processFID068Code(sessionId, nextState, userInputDTMFList);

                if(HOOK_RETURN_6.equalsIgnoreCase(response.getHookReturnCode())) {

                    response = ivrLfacsService.processFID070Code(sessionId, STATE_FID070, Integer.parseInt(userInputDTMFList.get(0)));
                }

                break;

            case STATE_FID060:
                response = ivrLfacsService.processFID060Code(sessionId, nextState);
                break;

            case STATE_FID515:
                response = ivrLfacsService.processFID515Code(sessionId, nextState);
                break;


            case IVRConstants.STATE_FID525:
                response = ivrLfacsService.processFID525Code(sessionId, nextState,userInputDTMFList);
                break;

            case IVRConstants.STATE_FID532:
                response = ivrLfacsService.processFID532Code(sessionId, nextState);
                break;


            case STATE_FID211:
                response = ivrLfacsService.processFID211Code(sessionId, nextState, userInputDTMFList.get(0));
                break;

            //In TN flow, FID600 is called from FI0030
            case STATE_FID600:
                response = ivrLfacsService.processFID600Code(sessionId, nextState);
                if(response.getHookReturnCode().equalsIgnoreCase(HOOK_RETURN_1)) {
                    //inquiry by TN flow call state FID080
                    response = ivrLfacsService.processFID080Code(sessionId, IVRConstants.STATE_FID080);

                }
                break;

            case STATE_FID237:
                response = ivrLfacsService.processFID237Code(sessionId, nextState, userInputDTMFList);
                break;

            case STATE_FID250:

                response = ivrLfacsService.processFID250Code(sessionId, nextState, Integer.parseInt(userInputDTMFList.get(0)));

                if (HOOK_RETURN_1.equalsIgnoreCase(response.getHookReturnCode())) {

                    response = ivrLfacsService.processFID271Code(sessionId, STATE_FID271);

                    if (HOOK_RETURN_2.equalsIgnoreCase(response.getHookReturnCode())) {

                        response = ivrLfacsService.processFID400Code(sessionId, STATE_FID400, HOOK_RETURN_2);
                    }
                } else if(HOOK_RETURN_2.equalsIgnoreCase(response.getHookReturnCode())) {

                    response = ivrLfacsService.processFID500Code(sessionId, STATE_FID500, userInputDTMFList);
                    if (IVRHookReturnCodes.HOOK_RETURN_7.equalsIgnoreCase(response.getHookReturnCode())) {

                        response = ivrLfacsService.processFID272Code(sessionId, STATE_FID272,userInputDTMFList);
                    }
                } else if(HOOK_RETURN_4.equalsIgnoreCase(response.getHookReturnCode())) {
                    //CP flow
                    response = ivrLfacsService.processFID600Code(sessionId, STATE_FID600);

                    if (response.getHookReturnCode().equalsIgnoreCase(HOOK_RETURN_2)) {
                        //inq by cp flow , call state FID285
                        nextState = "FID285";
                        response = ivrLfacsService.processFID285Code(sessionId, nextState);
                    }
                } else if(HOOK_RETURN_5.equalsIgnoreCase(response.getHookReturnCode())) {

                    response = ivrLfacsService.processFID300Code(sessionId, STATE_FID300);
                }

                break;

            case IVRConstants.STATE_FID535:
                response = ivrLfacsService.processFID535Code(sessionId, nextState);
                break;

            case IVRConstants.STATE_FID560:
                response = ivrLfacsService.processFID560Code(sessionId, nextState);
                break;

            case IVRConstants.STATE_FID274:
                response = ivrLfacsService.processFID274Code(sessionId, nextState);
                break;

            case IVRConstants.STATE_FID090:
                response = ivrLfacsService.processFID090Code(sessionId, nextState);
                break;

            case IVRConstants.STATE_FID291:
                response = ivrLfacsService.processFID291Code(sessionId, nextState);
                break;

            case STATE_FID630:

                int dmInput = 0;

                if (userInputDTMFList != null && !userInputDTMFList.isEmpty()
                        && !StringUtils.isBlank(userInputDTMFList.get(0))) {

                    dmInput = Integer.parseInt(userInputDTMFList.get(0));
                }

                response = ivrLfacsService.processFID630Code(sessionId, nextState, dmInput);
                break;

            case STATE_FID615:
                response = ivrLfacsService.processAdditonalLinesVoiceFID615(sessionId, nextState);
                if(response.getHookReturnCode().equalsIgnoreCase(HOOK_RETURN_8)) {
                    //internally calling to Get Number of Addl lines excl the inquired TN
                    response = ivrLfacsService.processAdditonalLinesVoiceFID625(sessionId, STATE_FID625);
                }
                break;

            case STATE_FID635:
                response = ivrLfacsService.processAdditonalLinesVoiceFID635(sessionId);
                break;

            case STATE_FID273:
                response = ivrLfacsService.processFID273Code(sessionId, nextState, userInputDTMFList);
                break;

            case STATE_FID282:
                response = ivrLfacsService.processFID282Code(sessionId, nextState);
                break;

            case STATE_FID700:
                response = ivrLfacsService.processFID700Code(sessionId, nextState);
                break;

            case STATE_FID224:
                response = ivrLfacsService.processFID224Code(sessionId, nextState, userInputDTMFList);
                break;

            default:
                throw new BadUserInputException(sessionId, INACTIVE_STATE, currentState);
        }

        LOGGER.info("Session: "+ sessionId+ " Output: State:"+ response.getCurrentState() +  ", Hook Return Code: "+ response.getHookReturnCode()+ ", Message: "+ response.getHookReturnMessage());
        return response;
    }

    @Override
    public IVRWebHookResponseDto processCNF(String sessionId, String currentState, List<String> userInputDTMFList) throws JsonMappingException, JsonProcessingException {

        IVRWebHookResponseDto response = null;

        String nextState = findNextState(sessionId, currentState, userInputDTMFList);

        switch(nextState) {

            case STATE_FND035:

                response = ivrCnfService.processFND035(sessionId, nextState, userInputDTMFList);
                break;

            case STATE_FND055:

                response = ivrCnfService.processFND055(sessionId, nextState, userInputDTMFList.get(0));
                break;

            case STATE_FND059:

                response = ivrCnfService.processFND059(sessionId, nextState);

                if (response.getHookReturnCode().equalsIgnoreCase(HOOK_RETURN_0)) {

                    response = ivrCnfService.processFND060(sessionId, STATE_FND060);
                }
                break;

            case STATE_FND075:

                response = ivrCnfService.processFND075(sessionId, nextState, userInputDTMFList.get(0));
                break;

            case STATE_FND085:

                response = ivrCnfService.processFND085(sessionId, nextState);

                if(response.getHookReturnCode().equalsIgnoreCase(HOOK_RETURN_0)) {

                    response = ivrCnfService.processFND090(sessionId, STATE_FND090);

                    if(response.getHookReturnCode().equalsIgnoreCase(HOOK_RETURN_8)) {

                        response = ivrCnfService.processFND135(sessionId, STATE_FND135);
                    }
                }
                break;

            case STATE_FND135:

                response = ivrCnfService.processFND135(sessionId, nextState);
                break;

            case STATE_FND141:

                response = ivrCnfService.processFND141(sessionId, nextState);

                if(response.getHookReturnCode().equalsIgnoreCase(HOOK_RETURN_0)) {

                    response = ivrCnfService.processFND143(sessionId, STATE_FND143);

                    if(response.getHookReturnCode().equalsIgnoreCase(HOOK_RETURN_8)) {

                        response = ivrCnfService.processFND145(sessionId, STATE_FND145, DTMF_INPUT_8);
                    }
                }
                break;

            case STATE_FND145:

                response = ivrCnfService.processFND145(sessionId, nextState, userInputDTMFList.get(0));
                break;

            case STATE_FND155:

                response = ivrCnfService.processFND155(sessionId, nextState, userInputDTMFList.get(0));
                break;

            case STATE_FND170:

                response = ivrCnfService.processFND170(sessionId, nextState, userInputDTMFList.get(0));
                break;

            case STATE_FND215:

                response = ivrCnfService.processFND215(sessionId, currentState, nextState, userInputDTMFList.get(0));

                if(HOOK_RETURN_1.equalsIgnoreCase(response.getHookReturnCode())) {

                    response = ivrCnfService.processFND216(sessionId, STATE_FND216);
                }
                break;

            case STATE_FND700:

                response = ivrCnfService.processFND700(sessionId, nextState, userInputDTMFList.get(0));
                break;

            case STATE_FND740:

                response = ivrCnfService.processFND740(sessionId, nextState, userInputDTMFList);

                if(HOOK_RETURN_1.equalsIgnoreCase(response.getHookReturnCode())) {

                    response = ivrCnfService.processFND741(sessionId, STATE_FND741, userInputDTMFList);
                }else if(HOOK_RETURN_2.equalsIgnoreCase(response.getHookReturnCode())) {

                    response = ivrCnfService.processFND742(sessionId, STATE_FND742, userInputDTMFList);
                }
                break;

            case STATE_FND746:

                response = ivrCnfService.processFND746(sessionId, nextState, userInputDTMFList);
                break;

            default:
                throw new BadUserInputException(sessionId, INACTIVE_STATE, nextState);

        }

        return response;
    }

    @Override
    public IVRWebHookResponseDto getSanityResponse() {
        // TODO Auto-generated method stub
        LOGGER.info("Inside Sanity endpoint");
        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        IVRParameter param = new IVRParameter();
        param.setData("Dummy-Param");
        List<IVRParameter> params = new ArrayList<>();
        params.add(param);

        response.setSessionId(UUID.randomUUID().toString());
        response.setCurrentState("FASTIVR is UP and Running");
        response.setHookReturnCode("Sucess");
        response.setParameters(params);
        return response;
    }


    /*
     * Next state have to be fetched from Genesys_state and user_dtmf_input
     * combination Next state to be identified from the IVR State system. If the
     * input lists have 1 element only & if the element length is 1 then next state
     * will be triggered by the user-input. Else, if will be a direct state transfer
     */
    private String findNextState(String sessionId, String currentState, List<String> userInputDTMFList) {

        String nextState = "";

        try {
            if (sessionId == null || sessionId.isBlank()) {

                throw new BadUserInputException(sessionId, MISSING_SESSION_ID);
            }

            IVRState genesysState = ivrStateSystem.getStateMap().get(currentState);

            //Changed to this approach to Support MLD307 ( it supports DIRECT_STATE_TRANSFER input from 1->50 )
            if(genesysState.getTransitionForInput(DIRECT_STATE_TRANSFER) != null) {
				 nextState = genesysState.getTransitionForInput(DIRECT_STATE_TRANSFER).getNextState().getStateName();
            	
            } else {
                // for user dtmf input from keypad number(0-10)
				 nextState = genesysState.getTransitionForInput(userInputDTMFList.get(0)).getNextState().getStateName();
            }

//			 removed this approach
//			if (userInputDTMFList.size() == 1 && userInputDTMFList.get(0).length() == 1) {
//
//				nextState = genesysState.getTransitionForInput(userInputDTMFList.get(0)).getNextState().getStateName();
//
//			} else {
//				nextState = genesysState.getTransitionForInput(DIRECT_STATE_TRANSFER).getNextState().getStateName();
//			}
        } catch (Exception e) {

            throw new BadUserInputException(sessionId, UNREACHABLE_NEXT_STATE, currentState);
        }

        LOGGER.info(
                "Session: " + sessionId + "," + " Backend state:" + nextState + ", User input:" + userInputDTMFList);

        return nextState;
    }


    @Override
    public IVRWebHookResponseDto processAdministration(String sessionId, String currentState, List<String> userInputDTMFList) {

        IVRWebHookResponseDto response = null;

        if(null != sessionId && !sessionId.isBlank()) {

            String nextState = findNextState(sessionId, currentState, userInputDTMFList);
            LOGGER.info("Session: " + sessionId + "," + " Backend state:" + nextState + ", User input:" + "[*******]");


            switch (nextState) {

                case STATE_ADD010:

                    response = ivrAdminInterface.validateVariableAreaCode(userInputDTMFList.get(0), sessionId);
                    break;

                case STATE_ADD020:

                    response = ivrAdminInterface.validatePassword(userInputDTMFList.get(0), sessionId);
                    break;

                case STATE_ADD035:
                    response = ivrAdminInterface.issueUserLoginWithNewPassword(userInputDTMFList.get(0), sessionId);

                    break;

                case STATE_ADD100:

                    response = ivrAdminInterface.getCurrentAreaCode(sessionId);
                    break;

                case STATE_ADD110:

                    response = ivrAdminInterface.validateAreaCode(userInputDTMFList.get(0), sessionId);
                    break;

                case STATE_ADD550:
                    response = ivrAdminInterface.issueSetVarNpaRequest(userInputDTMFList.get(0), sessionId);
                    break;

                case STATE_ADD500:
                    response = ivrAdminInterface.setTechNPARequest(userInputDTMFList.get(0), sessionId);
                    break;

                case STATE_ADD555:
                    response = ivrAdminInterface.voiceResults(sessionId);
                    break;

                default:
                    throw new BadUserInputException(sessionId, INACTIVE_STATE, currentState);
            }

        }
        return response;
    }


    @Override
    public IVRWebHookResponseDto processChangeStatusOfCablePair(String sessionId, String currentState,
                                                                List<String> userDtmfsList) throws JsonMappingException, JsonProcessingException {
        IVRWebHookResponseDto response = null;
        String previousState = currentState;
        String nextState = findNextState(sessionId, currentState, userDtmfsList);

        switch (nextState) {

            case IVRConstants.STATE_FPD005:
                response = ivrlChangeStatusCablePairService.processFPD005StateCode(sessionId, previousState, nextState);
                break;
            case IVRConstants.STATE_FPD011:
                response = ivrlChangeStatusCablePairService.processFPD011StateCode(sessionId, nextState,
                        userDtmfsList.get(0));
                break;
            case IVRConstants.STATE_FPD020:
                response = ivrlChangeStatusCablePairService.processFPD020StateCode(sessionId, nextState,
                        userDtmfsList.get(0));
                break;
            case STATE_FPD060:
                String defectiveCode = userDtmfsList.size()== 3 ? userDtmfsList.get(2) : "";
                response = ivrlChangeStatusCablePairService.processFPD060DefectiveCode(sessionId, currentState, nextState, defectiveCode);
                if (response.getHookReturnCode().equalsIgnoreCase(HOOK_RETURN_1)) {
                    response = ivrlChangeStatusCablePairService.processFPD060StateCode(sessionId, STATE_FND060, response, userDtmfsList);
                    if (response.getHookReturnCode().equalsIgnoreCase(IVRHookReturnCodes.HOOK_RETURN_3)) {
                        return ivrlChangeStatusCablePairService.processFPD100StateCode(response);
                    } else {
                        return response;
                    }
                }

            default:
                throw new BadUserInputException(sessionId, INACTIVE_STATE, nextState);

        }

        return response;

    }

    @Override
    public IVRWebHookResponseDto processConstructionActivity(String sessionId, String currentState,List<String> cleanDtmfInputList) throws JsonProcessingException
    {
        IVRWebHookResponseDto response = null;

        String nextState = findNextState(sessionId, currentState, cleanDtmfInputList);

        switch (nextState) {

            case IVRConstants.STATE_CTD500:
                response =ivrConstructionService.processCTD500(sessionId, currentState, cleanDtmfInputList);
                break;

            case STATE_CTD400:
                response = ivrConstructionService.checkOpeningNumberExists(sessionId);
                break;

            case STATE_CTD403:
                response = ivrConstructionService.parseTelephone(cleanDtmfInputList.get(0), sessionId);
                break;

            case STATE_CTD410:
                response = ivrConstructionService.issueReferTroubleReport(cleanDtmfInputList.get(0), sessionId);
                break;

            default:
                throw new BadUserInputException(sessionId, INACTIVE_STATE, nextState);

        }

        return response;
    }

    @Override
    public IVRWebHookResponseDto processChangeorNewAssignServingTerminal(String sessionId, String currentState,
                                                                         List<String> userInputDTMFList) throws JsonProcessingException, HttpTimeoutException, ExecutionException, InterruptedException {

        IVRWebHookResponseDto response = null;

        String nextState = findNextState(sessionId, currentState, userInputDTMFList);

        try {

            if (null != sessionId || Strings.isNotBlank(sessionId)) {


                switch (nextState) {

            case STATE_FTD011:

                response = ivrCanstService.processFTD011(sessionId, nextState, userInputDTMFList.get(0));
                break;

            case STATE_FTD030:

                response = ivrCanstService.processFTD030(sessionId, nextState);

                if(HOOK_RETURN_0.equalsIgnoreCase(response.getHookReturnCode())) {

                    response = ivrCanstService.processFTD035(sessionId, STATE_FTD035);
                }
                break;

            case STATE_FTD060:

                response = ivrCanstService.processFTD060(sessionId, nextState, userInputDTMFList);
                break;
            case STATE_FTD370:

                response = ivrCanstService.processFTD370(sessionId, nextState, userInputDTMFList);
                break;

            case STATE_FTD371:

                response = ivrCanstService.processFTD371(sessionId, nextState, userInputDTMFList);
                break;

            case STATE_FTD120:

                response = ivrCanstService.processFTD120(sessionId, nextState);
                break;

            case STATE_FTD135:

                response = ivrCanstService.processFTD135(sessionId, nextState, userInputDTMFList.get(0));
                break;

            case STATE_FTD160:

                response = ivrCanstService.processFTD160(sessionId, nextState);

                if(HOOK_RETURN_0.equalsIgnoreCase(response.getHookReturnCode())) {

                    response = ivrCanstService.processFTD170(sessionId, STATE_FTD170);
                }
                break;

            case STATE_FTD190:

                response = ivrCanstService.processFTD190(sessionId, nextState);
                break;

            case STATE_FTD197:

                response = ivrCanstService.processFTD197(sessionId, nextState);
                break;

            case STATE_FTD210:

                response = ivrCanstService.processFTD210(sessionId, nextState);

                if(HOOK_RETURN_0.equalsIgnoreCase(response.getHookReturnCode())) {

                    response = ivrCanstService.processFTD220(sessionId, STATE_FTD220);
                }
                break;

            case STATE_FTD300:

                response = ivrCanstService.processFTD300(sessionId, nextState);
                break;

            case STATE_FTD315:

                response = ivrCanstService.processFTD315(sessionId, nextState, userInputDTMFList);

                if(HOOK_RETURN_1.equalsIgnoreCase(response.getHookReturnCode())) {

                    response = ivrCanstService.processFTD317(sessionId, STATE_FTD317);

                    if(HOOK_RETURN_0.equalsIgnoreCase(response.getHookReturnCode())) {
                        response = ivrCanstService.processFTD330(sessionId, IVRCANSTConstants.STATE_FTD330);
                    }

                } else if(HOOK_RETURN_3.equalsIgnoreCase(response.getHookReturnCode())) {

                    response = ivrCanstService.processFTD320(sessionId, STATE_FTD320);
                }
                break;

            case STATE_FTD231:

                response = ivrCanstService.processFTD231(sessionId);
                break;

            case STATE_FTD400:

                response = ivrCanstService.processFTD400(sessionId);
                break;

            case STATE_FTD240:

                response = ivrCanstService.processFTD240(sessionId);
                break;

            case STATE_FTD351:

                response = ivrCanstService.processFTD351(sessionId, nextState, userInputDTMFList.get(0));
                break;

            default:
                throw new BadUserInputException(sessionId, INACTIVE_STATE, nextState);

        }}} catch (Exception e) {
        LOGGER.error("Error in CANST ", e);
        response.setSessionId(sessionId);
        response.setCurrentState(nextState);
        response.setHookReturnCode(GPDOWN_ERR_MSG_CODE);
        response.setHookReturnMessage(GPDOWN_ERR_MSG);
    }
        return response;
}

    @Override
    public IVRWebHookResponseDto processCallerId(String sessionId, String currentState, List<String> userDtmfsList)
            throws JsonMappingException, JsonProcessingException {
        IVRWebHookResponseDto response = null;
        String previousState = currentState;
        String nextState = findNextState(sessionId, currentState, userDtmfsList);

        switch (nextState) {

            case IVRConstants.STATE_IDD011:
                response = ivrCallerIdService.processIDD011StateCode(sessionId, previousState, nextState);
                break;
            case IVRConstants.STATE_IDD020:
                response = ivrCallerIdService.processIDD020StateCode(sessionId, nextState, userDtmfsList.get(0));
                break;
            case IVRConstants.STATE_IDD035:
                response = ivrCallerIdService.processIDD035StateCode(sessionId, nextState, userDtmfsList.get(0));
                break;
            default:
                throw new BadUserInputException(sessionId, INACTIVE_STATE, nextState);

        }
        return response;
    }
}
