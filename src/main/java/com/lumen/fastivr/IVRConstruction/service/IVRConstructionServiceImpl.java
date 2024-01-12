package com.lumen.fastivr.IVRConstruction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRConstruction.Dto.ConstructionActivityResponse;
import com.lumen.fastivr.IVRConstruction.entity.IvrConstructionSession;
import com.lumen.fastivr.IVRConstruction.helper.IVRConstructionHelper;
import com.lumen.fastivr.IVRConstruction.repository.IVRConstructionCacheService;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentRequest;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRLFACS.LfacsValidation;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.httpclient.IVRHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.lumen.fastivr.IVRUtils.IVRConstants.*;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_8;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_9;

@Service
public class IVRConstructionServiceImpl implements IVRConstructionService {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private IVRHttpClient ivrHttpClient;

	@Autowired
	private IVRConstructionCacheService cacheService;

	@Autowired
	private IVRCacheService cacheServices;

	@Autowired
	private LfacsValidation tnValidation;

	@Autowired
	private IVRConstructionHelper ivrConstructionHelper;

	@Autowired
	private IVRLfacsServiceHelper ivrLfacsServiceHelper;

	private static final Logger LOGGER = LoggerFactory.getLogger(IVRConstructionServiceImpl.class);
	
	
	@Override
	public IVRWebHookResponseDto processCTD500(String sessionId, String currentState, List<String> cleanDtmfInputList) throws JsonMappingException, JsonProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVRWebHookResponseDto checkOpeningNumberExists(String sessionId) throws JsonProcessingException {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		String hookReturnCode = HOOK_RETURN_9;
		String hookReturnMessage = NO_OPENING_NUMBER;
		boolean flagTransferOpen = false;
		boolean flagTransferPending = false;

		IvrConstructionSession session = cacheService.getBySessionId(sessionId);
		LOGGER.info("checkOpeningNumberExists calls");
		if(null != session) {
			String constructionResponseJsonString = session.getConstructionResponse();

			ConstructionActivityResponse constructionActivityResponse = ivrConstructionHelper
					.extractDetailsFromConstructionResponse(constructionResponseJsonString);




		if (null != constructionActivityResponse  && constructionActivityResponse.getOpenNbr() != String.valueOf(0) && constructionActivityResponse.getOpenNbr() != IVRConstants.EMPTY) {
			if(constructionActivityResponse.getOpenStat().equalsIgnoreCase(OPEN)) {
				flagTransferOpen= true;
			}

			if(constructionActivityResponse.getOpenStat().equalsIgnoreCase(PEND)) {
				flagTransferPending= true;
			}
		}}

		if(flagTransferOpen || flagTransferPending) {
			hookReturnCode = HOOK_RETURN_8;
			hookReturnMessage = OPENING_NUMBER;
		}


		cacheService.updateSession(session);
		response.setSessionId(sessionId);
		response.setCurrentState(STATE_CTD400);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_CTD400 + " hookReturnCode: " + hookReturnCode + " hookReturnMessage: " + hookReturnMessage);
		return response;
	}

	@Override
	public IVRWebHookResponseDto parseTelephone(String userInput, String sessionId) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		IvrConstructionSession session = cacheService.getBySessionId(sessionId);
		IVRUserSession userSession = cacheServices.getBySessionId(sessionId);
		LOGGER.info("parseTelephone calls");

		if(null != userSession) {
			response = tnValidation.validateFacsTN(userInput, userSession);
		}

		cacheService.updateSession(session);
		response.setSessionId(sessionId);
		response.setCurrentState(STATE_CTD403);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_CTD403);
		return response;

	}

	@Override
	public IVRWebHookResponseDto issueReferTroubleReport(String userInput, String sessionId) throws JsonProcessingException {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		String tn = "";
		String primaryNpa = "";
		String primaryNxx = "";
		String npaState = "";
		String openNbr = "";

		IvrConstructionSession session = cacheService.getBySessionId(sessionId);
		IVRUserSession userSession = cacheServices.getBySessionId(sessionId);

		LOGGER.info("issueReferTroubleReport calls");

		if (null != userSession) {

			String losDbResponseJsonString = userSession.getLosDbResponse();
			TNInfoResponse losDbResponse = ivrLfacsServiceHelper
					.extractTNInfoFromLosDBResponse(losDbResponseJsonString);
			if (null != losDbResponse) {

				tn = losDbResponse.getTn();
				primaryNpa = losDbResponse.getPrimaryNPA();
				primaryNxx = losDbResponse.getPrimaryNXX();
				npaState = losDbResponse.getNpaState();
			}}

		String constructionResponse = session.getConstructionResponse();
		ConstructionActivityResponse responseRecvd = ivrConstructionHelper.extractDetailsFromConstructionResponse(constructionResponse);
		if(null != responseRecvd) {
			  openNbr = responseRecvd.getOpenNbr();
		}
			// change RetrieveLoopAssignmentRequest with correct request dto here and in IVRConstructionHelper also

			RetrieveLoopAssignmentRequest request = ivrConstructionHelper
					.buildReferTroubleReportRequest(tn, primaryNpa, primaryNxx, npaState, openNbr);

			String jsonRequest = objectMapper.writeValueAsString(request);
			//add request name and change url in below call
			String referTrRequestResultJson = ivrHttpClient.httpPostCall(jsonRequest, "", sessionId,
					"");
			String cleanJsonStr = ivrHttpClient.cleanResponseString(referTrRequestResultJson);

			//set in Db column for response of api
			userSession.setAdditionalLinesResponse(cleanJsonStr);

		cacheService.updateSession(session);
		response.setSessionId(sessionId);
		response.setCurrentState(STATE_CTD410);
		response.setHookReturnCode(HOOK_RETURN_9);
		response.setHookReturnMessage(TRANSFER_CALL_HOOK_MESSAGE);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_CTD410 + " hookReturnCode: " + HOOK_RETURN_9 + " hookReturnMessage: " + TRANSFER_CALL_HOOK_MESSAGE);
		return response;
	}

}
