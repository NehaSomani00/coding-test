/**
 * 
 */
package com.lumen.fastivr.IVRChangeStatusCablePair;

import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.CHANGE_PAIR_STATUS_REQUEST;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INQUIRY_BY_TN;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INVALID_SESSION_ID;
import static com.lumen.fastivr.IVRUtils.IVRConstants.SUCCESS;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_0;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_1;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCNF.Dto.changePairStatus.ChangePairStatusResponse;
import com.lumen.fastivr.IVRCNF.Dto.changePairStatus.InputDataRequestBody;
import com.lumen.fastivr.IVRCNF.entity.IVRCnfEntity;
import com.lumen.fastivr.IVRCNF.helper.IVRCnfHelper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRLFACS.LfacsValidation;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.httpclient.IVRHttpClient;

@Service
public class IVRChangeStatusCablePairServiceImpl implements IVRChangeStatusCablePairService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IVRChangeStatusCablePairServiceImpl.class);

	@Autowired
	private IVRCacheService cacheService;

	@Autowired
	private LfacsValidation tnValidation;

	@Autowired
	private IVRLfacsServiceHelper ivrLfacsServiceHelper;

	@Autowired
	private IVRCnfHelper ivrCnfHelper;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private IVRHttpClient ivrHttpClient;

	@Value("#{${firstSetoFDefectiveCodes}}")
	Map<String, String> firsetSetOFDefectiveCodes = new HashMap<String, String>();

	@Value("#{${secondSetoFDefectiveCodes}}")
	Map<String, String> secondSetOFDefectiveCodes = new HashMap<String, String>();

	@Value("${cnf.change.pair.status.url}")
	private String changePairStatusUrl;

	@Override
	public IVRWebHookResponseDto processFPD011StateCode(String sessionId, String currentState, String userDTMFInput) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		IVRUserSession session = cacheService.getBySessionId(sessionId);

		if (session != null) {
			response = tnValidation.validateFacsTN(userDTMFInput, session);
			session.setFacsInqType(INQUIRY_BY_TN);
		} else {
			response.setHookReturnMessage(INVALID_SESSION_ID);
		}

		cacheService.updateSession(session);
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFPD060DefectiveCode(String sessionId, String currentState, String nextState,
			String userDTMFInput) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		response.setHookReturnCode(HOOK_RETURN_1);
		String defectiveCode = null;

		switch (currentState) {
		case IVRConstants.STATE_FP0040:
			defectiveCode = firsetSetOFDefectiveCodes.get(userDTMFInput);
			LOGGER.info("Inside processFPD060DefectiveCode(), defectiveCode is -- {} ",defectiveCode);
			break;

		case IVRConstants.STATE_FP0050:
			defectiveCode = secondSetOFDefectiveCodes.get(userDTMFInput);
			LOGGER.info("Inside processFPD060DefectiveCode(), defectiveCode is -- {} ",defectiveCode);
			break;

		default:
			response.setHookReturnCode(HOOK_RETURN);
			response.setHookReturnMessage("Invalid DefetiveCode");
			LOGGER.info("Inside processFPD060DefectiveCode(), Invalid DefectiveCode");
			return response;
		}

		response.setParameters(ivrLfacsServiceHelper.addParamterData(defectiveCode));
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFPD060StateCode(String sessionId, String currentState,
			IVRWebHookResponseDto response, List<String> userRequest) {
		try {
			IVRUserSession userSession = cacheService.getBySessionId(sessionId);
			if (userSession == null) {
				LOGGER.error("userSession is null inprocessFPD060StateCode() for sessionId -- {} ",sessionId);
				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(INVALID_SESSION_ID);
				return response;
			} else {
				String defectiveCode = response.getParameters().get(0).getData();
				ChangePairStatusResponse changePairStatusResponse = processChangePairStatus(userSession, userRequest, defectiveCode, response);
				LOGGER.info("changePairStatusResponse in inprocessFPD060StateCode() -- {} ",changePairStatusResponse.toString());
				if(("S").equalsIgnoreCase(changePairStatusResponse.getMessageStatus().getErrorStatus()) && !userSession.isCanBePagedMobile()) {
					response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_3);
					response.setHookReturnMessage("No Alpha Pager");
					response.setCurrentState(IVRConstants.STATE_FPD100);
				} else if(("S").equalsIgnoreCase(changePairStatusResponse.getMessageStatus().getErrorStatus()) && userSession.isCanBePagedMobile()) {
					response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_2);
					response.setHookReturnMessage("Aplha Pager");
					response.setCurrentState(IVRConstants.STATE_FPD060);
				} else {
					response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN);
					response.setHookReturnMessage("FAST is down due to "+ changePairStatusResponse.getMessageStatus().getHostErrorList().get(0).getErrorList().toString());
					LOGGER.error("Error in processing --{}", changePairStatusResponse.getMessageStatus().getHostErrorList().toString());
				} 
			}
		} catch (Exception e) {
			LOGGER.error("Error in inprocessFPD060StateCode() -- {} ",e.getMessage());
			response.setHookReturnCode(HOOK_RETURN);
			response.setHookReturnMessage(e.getMessage());
		}
		return response;
	}

	private ChangePairStatusResponse processChangePairStatus(IVRUserSession userSession, List<String> userRequest, String defectiveCode, IVRWebHookResponseDto response) {
		ChangePairStatusResponse responseObject = new ChangePairStatusResponse();
		if (userSession != null) {
			try {
				String losDbResponseJsonString = userSession.getLosDbResponse();
				TNInfoResponse losDbResponse = ivrLfacsServiceHelper
						.extractTNInfoFromLosDBResponse(losDbResponseJsonString);
				String primaryNpa = "";
				String primaryNxx = "";
				if (losDbResponse != null) {
					primaryNpa = losDbResponse.getPrimaryNPA();
					primaryNxx = losDbResponse.getPrimaryNXX();
				} else {
					LOGGER.error("Exception occurred in processChangePairStatus(), losDbResponse is null");
					throw new RuntimeException("");
				}
				IVRCnfEntity entity = new IVRCnfEntity();
				String cable = userRequest.get(0);
				String pair = userRequest.get(1);
				entity.setCable(cable);
				entity.setPair(pair);				
				com.lumen.fastivr.IVRCNF.Dto.changePairStatus.InputData request = ivrCnfHelper
						.buildChangePairStatusRequest(userSession, entity, primaryNpa, primaryNxx, defectiveCode);
				InputDataRequestBody dataRequestBody = new InputDataRequestBody();
				dataRequestBody.setInputData(request);

				String jsonRequest = objectMapper.writeValueAsString(dataRequestBody);

				String changePairStatusResultJson = ivrHttpClient.httpPostCall(jsonRequest, changePairStatusUrl,
						userSession.getSessionId(), CHANGE_PAIR_STATUS_REQUEST);
				String cleanJsonStr = ivrLfacsServiceHelper.cleanResponseString(changePairStatusResultJson);
				responseObject = objectMapper.readValue(cleanJsonStr,
						ChangePairStatusResponse.class);
				LOGGER.info("End processChangePairStatus() -- {}", responseObject.toString());
				return responseObject;
			} catch (JsonMappingException  e) {
				LOGGER.error("MismatchedInputException -- {}", e.getMessage());
			} catch (Exception e2) {
				LOGGER.error("HttpTimeoutException ->{} ",e2.getMessage());
			}
		} else {
			LOGGER.info("UserSession is null in processChangePairStatus()");
		}
		return responseObject;
	}

	@Override
	public IVRWebHookResponseDto processFPD100StateCode(IVRWebHookResponseDto response) {
		response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_8);
		response.setHookReturnMessage("No Alpha Pager");
		
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFPD005StateCode(String sessionId, String previousState, String currentState) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		IVRUserSession session = cacheService.getBySessionId(sessionId);
			if (session != null && IVRConstants.STATE_MM0001.equalsIgnoreCase(previousState)) {
				response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_0);
				response.setHookReturnMessage("Normal Flow");
				response.setCurrentState(currentState);
			} else if(session != null && IVRConstants.STATE_FND741.equalsIgnoreCase(previousState)) {
				response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_1);
				response.setHookReturnMessage("From another Menu");
				response.setCurrentState(currentState);		
			} else {
				response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN);
				response.setHookReturnMessage(INVALID_SESSION_ID);
			}
			cacheService.updateSession(session);
			response.setSessionId(sessionId);
			
			return response;	
					
	}

	@Override
	public IVRWebHookResponseDto processFPD020StateCode(String sessionId, String nextState, String userDTMFInput) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(nextState);

		response.setHookReturnCode(HOOK_RETURN_3);
		response.setHookReturnMessage(SUCCESS);
		if (userDTMFInput.contains("*")) {
			String convertedInput = ivrCnfHelper.convertInputCodesToAlphabets(userDTMFInput);

			if (convertedInput == null) {
				response.setHookReturnCode(HOOK_RETURN_0);
				response.setHookReturnMessage("Input Codes are entered incorrectly");
				return response;
			}
			String firstValues = userDTMFInput.substring(0, userDTMFInput.indexOf("*"));
			
			String lastValues = userDTMFInput.substring(userDTMFInput.lastIndexOf("*") + 1);
			
			convertedInput = firstValues + convertedInput + lastValues;

			List<IVRParameter> ivrParameterList = ivrLfacsServiceHelper.addParamterData(convertedInput);
			response.setParameters(ivrParameterList);
		} else {
			List<IVRParameter> parameterList = ivrLfacsServiceHelper.addParamterData(userDTMFInput);
			
			response.setParameters(parameterList);
		}
		return response;
	}
}