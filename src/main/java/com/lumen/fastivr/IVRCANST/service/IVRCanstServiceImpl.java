package com.lumen.fastivr.IVRCANST.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCANST.Dto.*;
import com.lumen.fastivr.IVRCANST.entity.IVRCanstEntity;
import com.lumen.fastivr.IVRCANST.helper.IVRCanstHelper;
import com.lumen.fastivr.IVRCANST.helper.IVRCanstPagerText;
import com.lumen.fastivr.IVRCANST.helper.IvrCanstAsyncService;
import com.lumen.fastivr.IVRCANST.repository.IVRCanstCacheService;
import com.lumen.fastivr.IVRCANST.utils.IVRCANSTConstants;
import com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.CurrentAssignmentRequestTnDto;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.SEG;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketRequest;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketResponse;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentRequest;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentResponse;
import com.lumen.fastivr.IVRLFACS.IVRLfacsService;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRLFACS.LfacsValidation;
import com.lumen.fastivr.IVRLFACS.SparePairPageBuilder;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.FormatUtilities;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.IVRUtils.IVRLfacsConstants;
import com.lumen.fastivr.httpclient.IVRHttpClient;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.lumen.fastivr.IVRCANST.utils.IVRCANSTConstants.NO_ALPHA_PAGER;
import static com.lumen.fastivr.IVRCANST.utils.IVRCANSTConstants.SUCCESS;
import static com.lumen.fastivr.IVRCANST.utils.IVRCANSTConstants.*;
import static com.lumen.fastivr.IVRUtils.IVRConstants.*;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.*;


@Service
public class IVRCanstServiceImpl implements IVRCanstService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IVRCanstServiceImpl.class);

	@Autowired
	private IVRCacheService cacheService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private IVRHttpClient ivrHttpClient;

	@Autowired
	private IVRCanstCacheService ivrCanstCacheService;	

	@Autowired
	private IVRLfacsServiceHelper ivrLfacsServiceHelper;
	
	@Autowired
	private LfacsValidation tnValidation;

	@Autowired
	private IVRCanstHelper ivrCanstHelper;

	@Autowired
	private IvrCanstAsyncService asyncService;

	@Autowired
	private IVRLfacsService ivrLfacsService;
	
	@Autowired
	IVRCanstPagerText ivrCanstPagerText;
	
	@Autowired
	private SparePairPageBuilder sprPrPageBuilder;
	
	@Value("#{${firstSetoFDefectiveCodes}}")
	Map<String, String> firsetSetOFDefectiveCodes = new HashMap<String, String>();
	
	@Value("#{${secondSetoFDefectiveCodes}}")
	Map<String, String> secondSetOFDefectiveCodes = new HashMap<String, String>();
	
	@Value("${canst.assign.service.order.url}")
	private String assignServiceOrderUrl;		
	
	@Value("${canst.order.status.url}")
	private String orderStatusUrl;
	
	@Value("${cnf.change.loop.assignment.url}")
	private String changeLoopAssignmentUrl;

	@Value("${cnf.update.loop.url}")
	private String updateLoopUrl;

	@Override
	public IVRWebHookResponseDto processFTD011(String sessionId, String currentState, String userDTMFInput) {
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		IVRUserSession session = cacheService.getBySessionId(sessionId);
		
		if (session != null) {
			
			response = tnValidation.validateFacsTN(userDTMFInput, session);
			
			if (HOOK_RETURN_1.equalsIgnoreCase(response.getHookReturnCode())) {

				response.setHookReturnMessage(HOOK_RETURN_0);
				response.setHookReturnMessage(TN_FOUND_IN_TABLE);
				cacheService.updateSession(session);
				
				IVRCanstEntity ivrCanstSession = ivrCanstCacheService.getBySessionId(sessionId);
				
				if(ivrCanstSession == null) {
					
					ivrCanstSession = new IVRCanstEntity();
					ivrCanstSession.setSessionId(sessionId);
					ivrCanstCacheService.addSession(ivrCanstSession);
				}
				
			} else if(HOOK_RETURN_0.equalsIgnoreCase(response.getHookReturnCode())) {
				
				response.setHookReturnMessage(HOOK_RETURN_0);
				response.setHookReturnMessage(TN_NOT_FOUND_IN_TABLE);
			}
		} else {

			response.setHookReturnMessage(HOOK_RETURN);
			response.setHookReturnMessage(INVALID_SESSION_ID);
		}

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFTD030(String sessionId, String currentState) throws JsonMappingException, JsonProcessingException {
		
		IVRWebHookResponseDto response = ivrLfacsService.processFID020Code(sessionId, currentState);
		
		if(HOOK_RETURN_0.equalsIgnoreCase(response.getHookReturnCode())) {
			
			IVRCanstEntity ivrCanstSession = ivrCanstCacheService.getBySessionId(sessionId);
			
			IVRUserSession session = cacheService.getBySessionId(sessionId);
			
			if(ivrCanstSession != null && session != null && session.getCurrentAssignmentResponse() != null) {

				CurrentAssignmentResponseDto currentAssignmentResponseDto = objectMapper
						.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
				
				if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
						&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
						&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty() 
						&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG() != null 
						&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()) {
					
					 int segSize = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().size();
					 
					 ivrCanstSession.setOldTea(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(segSize - 1).getTEA());
					 
					 ivrCanstCacheService.updateSession(ivrCanstSession);
				}
			}
		}
		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFTD035(String sessionId, String nextState)
			throws JsonMappingException, JsonProcessingException {
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = "";
		String hookReturnMessage = "";

		IVRUserSession ivrUserSession = cacheService.getBySessionId(sessionId);

		if (ivrUserSession != null) {
			
			if (ivrUserSession.getCurrentAssignmentResponse() != null) {
				
				CurrentAssignmentResponseDto currentAssignmentResponse = objectMapper
						.readValue(ivrUserSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
				try {

					if (currentAssignmentResponse != null) {

						if (currentAssignmentResponse.getMessageStatus() != null) {

							if (IVRLfacsConstants.S
									.equals(currentAssignmentResponse.getMessageStatus().getErrorStatus())) {

								if(ivrLfacsServiceHelper.isUdcCircuit(currentAssignmentResponse, response)) {
									
									hookReturnCode = "0,0";
									hookReturnMessage = "UDC Circuit";
								} else if (ivrLfacsServiceHelper.isSpecialCircuit(currentAssignmentResponse, response)) {
									
									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_5;
									hookReturnMessage = "Special Circuit";
								} else if (ivrLfacsServiceHelper.isLineStationTransfer(currentAssignmentResponse)) {
									
									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_6;
									hookReturnMessage = "Line Station Transfer (LST)";
								} else if (ivrCanstHelper.getSegmentList(currentAssignmentResponse) > 3) {
									
									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_7;
									hookReturnMessage = "More than 3 Segments";
								} else if(ivrCanstHelper.isDPA(currentAssignmentResponse)) {
								
									hookReturnCode = "0,1";
									hookReturnMessage = "Different Premise Address (DPA)";
								} else if(ivrCanstHelper.isCKID(currentAssignmentResponse) && ivrLfacsServiceHelper.isSeviceOrder(currentAssignmentResponse)) {
									
									IVRCanstEntity ivrCanstSession = ivrCanstCacheService.getBySessionId(sessionId);
									
									String serviceOrderNo = ivrCanstHelper.getSeviceOrder(currentAssignmentResponse);
									if(ivrCanstSession != null) {
										
										ivrCanstSession.setServiceOrderNo(serviceOrderNo);
										ivrCanstCacheService.updateSession(ivrCanstSession);
									}
									response.setParameters(ivrLfacsServiceHelper.addParamterData(serviceOrderNo));
									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_8;
									hookReturnMessage = "TN found in LFACS with Service Order";
								} else if(ivrCanstHelper.isCKID(currentAssignmentResponse) && !ivrLfacsServiceHelper.isSeviceOrder(currentAssignmentResponse)) {
								
									response.setParameters(ivrLfacsServiceHelper.addParamterData(ivrCanstHelper.getCKID(currentAssignmentResponse)));
									hookReturnCode = "0,2";
									hookReturnMessage = "TN found in LFACS with NO Service Order";
								} else if (ivrCanstHelper.isCKID(currentAssignmentResponse) || currentAssignmentResponse.getMessageStatus().getHostErrorList().get(0)
										.getErrorList().get(0).getErrorMessage().contains("L400-192")
										|| currentAssignmentResponse.getMessageStatus().getHostErrorList().get(0)
												.getErrorList().get(0).getErrorMessage().contains("L410-292")) {
									
									hookReturnCode = "0,3";
									hookReturnMessage = "No TN in LFACS";
								}
							} else {

								IVRWebHookResponseDto fastivrErrorResp = ivrLfacsServiceHelper.findFastivrError(
										sessionId, nextState, currentAssignmentResponse.getMessageStatus());
								hookReturnCode = fastivrErrorResp.getHookReturnCode();
								hookReturnMessage = fastivrErrorResp.getHookReturnMessage();
							}

						} else {

							hookReturnMessage = "Message Status Not Found";

						}
					} else {
						hookReturnMessage = "Current Assignment not found";

					}

				} catch (Exception ex) {
					LOGGER.error("Exception stack trace: ", ex);
					hookReturnCode = HOOK_RETURN;
					hookReturnMessage = FASTIVR_BACKEND_ERR;
				}
			} else {
				hookReturnMessage = "Current Assignment is null";
			}
		} else {
			hookReturnMessage = INVALID_SESSION_ID;
		}
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info(hookReturnMessage, sessionId);
		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFTD060(String sessionId, String currentState, List<String> userInputDTMFList)
			throws JsonMappingException, JsonProcessingException {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);

		response.setCurrentState(currentState);

		response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_0);
		
		response.setHookReturnMessage("Too few digits");

		if (userInputDTMFList != null && !userInputDTMFList.isEmpty()) {

			String preFix = "";

			String postFix = "";

			String postFixNo = userInputDTMFList.get(0);

			String preFixNo = userInputDTMFList.get(1);

			if (StringUtils.isNotBlank(preFixNo)) {

				switch (preFixNo) {

				case IVRConstants.DTMF_INPUT_1:

					preFix = "C";
					break;

				case IVRConstants.DTMF_INPUT_2:

					preFix = "N";
					break;

				case IVRConstants.DTMF_INPUT_3:

					preFix = "T";
					break;
				}
			} else {

				return response;
			}

			if (StringUtils.isNotBlank(postFixNo)) {

				StringBuilder serviceOrderNo = new StringBuilder();

				if (postFixNo.contains("*")) {

					String convertedInputs = ivrCanstHelper.convertInputCodesToAlphabets(postFixNo);

					if (convertedInputs == null) {

						response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN);
						response.setHookReturnMessage("Input Codes are entered incorrectly");

						return response;
					}

					serviceOrderNo.append(convertedInputs);
					
					char beginIndex = serviceOrderNo.charAt(0);
					
					if (beginIndex == 'C' || beginIndex == 'N' || beginIndex == 'T') {

						postFix = serviceOrderNo.toString().substring(1);
					} else {
						
						postFix = serviceOrderNo.toString();
					}
					
					postFix = postFix + postFixNo.substring(postFixNo.lastIndexOf("*") + 1);
				} else {

					postFix = serviceOrderNo.append(postFixNo).toString();
				}

				int postFixLength = postFix.length();

				if (postFixLength == 7 || postFixLength == 8 || postFixLength == 9) {

					response.setHookReturnCode(HOOK_RETURN_2);

					response.setHookReturnMessage("OK");

					List<IVRParameter> ivrParameterList = new ArrayList<IVRParameter>();

					IVRParameter ivrParameter = new IVRParameter();
					String serviceOrderNumber = preFix + postFix;
					ivrParameter.setData(serviceOrderNumber);

					ivrParameterList.add(ivrParameter);
					response.setParameters(ivrParameterList);
					
					IVRCanstEntity canstSession = ivrCanstCacheService.getBySessionId(sessionId);

					if (canstSession == null) {

						canstSession = new IVRCanstEntity();

						canstSession.setSessionId(sessionId);
						canstSession.setServiceOrderNo(serviceOrderNumber);
						ivrCanstCacheService.addSession(canstSession);
					}else {
						canstSession.setServiceOrderNo(serviceOrderNumber);
						ivrCanstCacheService.updateSession(canstSession);
					}
				} else if (postFixLength < 7) {

					response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_0);
					response.setHookReturnMessage("Too few digits");
				} else if (postFixLength > 9) {

					response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_1);
					response.setHookReturnMessage("Too many digits");
				}
			}
		}

		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFTD120(String sessionId, String currentState) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		String hookReturnCode = "";

		String hookReturnMessage = "";

		try {
			IVRUserSession userSession = cacheService.getBySessionId(sessionId);

			if (userSession == null) {

				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(INVALID_SESSION_ID);

				return response;
			}

			response.setSessionId(sessionId);

			response.setCurrentState(currentState);

			String losDbResponseJsonString = userSession.getLosDbResponse();

			TNInfoResponse losDbResponse = ivrLfacsServiceHelper
					.extractTNInfoFromLosDBResponse(losDbResponseJsonString);

			String primaryNpa = "";
			String primaryNxx = "";
			if (losDbResponse != null) {

				primaryNpa = losDbResponse.getPrimaryNPA();
				primaryNxx = losDbResponse.getPrimaryNXX();
			} else {

				throw new RuntimeException(CANNOT_FETCH_CURR_ASSG_API_LOSDB_NULL);
			}

			IVRCanstEntity canstSession = ivrCanstCacheService.getBySessionId(sessionId);

			if (canstSession == null) {

				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(CANST_INVALID_SESSION_ID);
				return response;
			}			

			OrderStatusRequest orderStatusRequest = ivrCanstHelper.buildOrderStatusRequest(primaryNpa, primaryNxx, canstSession, userSession);			
			
			String jsonRequest = objectMapper.writeValueAsString(orderStatusRequest);
			
			String assignServiceOrderResultJson = ivrHttpClient.httpPostCall(jsonRequest, orderStatusUrl,
					sessionId, ORDER_STATUS_REQUEST);

			String cleanJsonStr = ivrLfacsServiceHelper.cleanResponseString(assignServiceOrderResultJson);

			canstSession.setOrderStatusResp(cleanJsonStr);

			ivrCanstCacheService.updateSession(canstSession);
			
			OrderStatusResponse responseObject = objectMapper.readValue(cleanJsonStr,
					OrderStatusResponse.class);
			
			if(responseObject != null && responseObject.getMessageStatus() != null && "S".equalsIgnoreCase(responseObject.getMessageStatus().getErrorStatus())) {
				
				hookReturnCode = HOOK_RETURN_0;
				hookReturnMessage = "OK";
			} else {
				
				hookReturnCode = HOOK_RETURN_1;
				hookReturnMessage = "Backendwrapper returning Failure response";
			}

		} catch (Exception e) {
			hookReturnCode = HOOK_RETURN_1;
			hookReturnMessage = GPDOWN_ERR_MSG;
			LOGGER.error("CANST FND120 Exception stack trace: ", e);			
		}

		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);		

		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFTD135(String sessionId, String currentState, String userDTMFInput) {
		
		return processFTD011(sessionId, currentState, userDTMFInput);
	}
	
	@Override
	public IVRWebHookResponseDto processFTD160(String sessionId, String currentState)
			throws JsonMappingException, JsonProcessingException {

		IVRWebHookResponseDto response = ivrLfacsService.processFID020Code(sessionId, currentState);

		if (HOOK_RETURN_0.equalsIgnoreCase(response.getHookReturnCode())) {

			IVRCanstEntity ivrCanstSession = ivrCanstCacheService.getBySessionId(sessionId);

			IVRUserSession session = cacheService.getBySessionId(sessionId);

			if (ivrCanstSession != null && session != null && session.getCurrentAssignmentResponse() != null) {

				CurrentAssignmentResponseDto currentAssignmentResponseDto = objectMapper
						.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);

				if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
						&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
						&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()) {

					int segSize = 0;
					
					List<SEG> segements = null;
					
					if (currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG() != null
							&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()) {

						segSize = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().size();

						segements = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG();
						
						
					} else if(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null 
							&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty()
							&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG() != null
							&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().isEmpty()) {
						
						segSize = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().size();

						segements = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG();
					}
					
					ivrCanstSession.setNewTea(segements.get(segSize - 1).getTEA());

					ivrCanstCacheService.updateSession(ivrCanstSession);
				}
			}
		}
		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFTD170(String sessionId, String currentState)
			throws JsonMappingException, JsonProcessingException {
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		String hookReturnCode = "";
		String hookReturnMessage = "";

		IVRUserSession ivrUserSession = cacheService.getBySessionId(sessionId);

		if (ivrUserSession != null) {
			
			if (ivrUserSession.getCurrentAssignmentResponse() != null) {
				
				CurrentAssignmentResponseDto currentAssignmentResponse = objectMapper
						.readValue(ivrUserSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
				try {

					if (currentAssignmentResponse != null) {

						if (currentAssignmentResponse.getMessageStatus() != null) {

							if (IVRLfacsConstants.S
									.equals(currentAssignmentResponse.getMessageStatus().getErrorStatus())) {

								hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_8;
								hookReturnMessage = "All OK";
								
								if (!ivrLfacsServiceHelper.isSeviceOrder(currentAssignmentResponse)) {
									
									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_6;
									hookReturnMessage = "Service Order Not Found";
								} else if (ivrCanstHelper.getSegmentList(currentAssignmentResponse) > 3) {
									
									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_7;
									hookReturnMessage = "More than 3 Segments";
								} else if(!ivrCanstHelper.isTEA(currentAssignmentResponse)) {
								
									hookReturnCode = "0,1";
									hookReturnMessage = "No Serving Terminal Found";
								} else if(IVRHookReturnCodes.HOOK_RETURN_8.equalsIgnoreCase(hookReturnCode)) {
									
									if(StringUtils.isNotBlank(currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSEG().get(0).getTEA())) {
										
										response.setParameters(ivrLfacsServiceHelper.addParamterData(currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSEG().get(0).getTEA()));
									} else if(StringUtils.isNotBlank(currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().get(0).getTEA())) {
										
										response.setParameters(ivrLfacsServiceHelper.addParamterData(currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().get(0).getTEA()));
									}
								}
							} else if (!ivrCanstHelper.isCKID(currentAssignmentResponse) || (currentAssignmentResponse.getMessageStatus() != null 
									&& currentAssignmentResponse.getMessageStatus().getHostErrorList() != null
									&& (currentAssignmentResponse.getMessageStatus().getHostErrorList().get(0)
									.getErrorList().get(0).getErrorMessage().contains("L400-192")
									|| currentAssignmentResponse.getMessageStatus().getHostErrorList().get(0)
											.getErrorList().get(0).getErrorMessage().contains("L410-292")))) {
								
								hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_5;
								hookReturnMessage = "TN Not Found in LFACS";
							} else {

								IVRWebHookResponseDto fastivrErrorResp = ivrLfacsServiceHelper.findFastivrError(
										sessionId, currentState, currentAssignmentResponse.getMessageStatus());
								hookReturnCode = fastivrErrorResp.getHookReturnCode();
								hookReturnMessage = fastivrErrorResp.getHookReturnMessage();
							}

						} else {

							hookReturnMessage = "Message Status Not Found";

						}
					} else {
						hookReturnMessage = "Current Assignment not found";

					}

				} catch (Exception ex) {
					LOGGER.error("Exception stack trace: ", ex);
					hookReturnCode = HOOK_RETURN;
					hookReturnMessage = FASTIVR_BACKEND_ERR;
				}
			} else {
				hookReturnMessage = "Current Assignment is null";
			}
		} else {
			hookReturnMessage = INVALID_SESSION_ID;
		}
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info(hookReturnMessage, sessionId);
		return response;
	}
	

	@Override
	public IVRWebHookResponseDto processFTD190(String sessionId, String currentState)
			throws JsonMappingException, JsonProcessingException {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);

		response.setCurrentState(currentState);

		IVRCanstEntity ivrCanstSession = ivrCanstCacheService.getBySessionId(sessionId);

		if (ivrCanstSession == null) {

			response.setHookReturnCode(HOOK_RETURN);
			response.setHookReturnMessage(INVALID_SESSION_ID);

			return response;
		}

		String orderStatusRespString = ivrCanstSession.getOrderStatusResp();

		OrderStatusResponse responseObject = objectMapper.readValue(orderStatusRespString, OrderStatusResponse.class);

		if (responseObject != null && responseObject.getReturnDataSet() != null
				&& "CFA".equalsIgnoreCase(responseObject.getReturnDataSet().getLoopAssignmentStatus())) {

			response.setHookReturnCode(HOOK_RETURN_2);
			response.setHookReturnMessage("Service Order is CFA");

			return response;
		} else {

			String tea = ivrCanstSession.getNewTea();

			String oldTea = ivrCanstSession.getOldTea();

			if (tea.equalsIgnoreCase(oldTea)) {

				response.setHookReturnCode(HOOK_RETURN_1);
				response.setHookReturnMessage("Changing to the Same Serving Terminal");

				return response;
			}

			response.setHookReturnCode(HOOK_RETURN_3);
			response.setHookReturnMessage(SUCCESS);
		}

		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFTD197(String sessionId, String currentState) throws JsonMappingException, JsonProcessingException {
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		
		response.setSessionId(sessionId);
		
		response.setCurrentState(currentState);

		IVRUserSession ivrUserSession = cacheService.getBySessionId(sessionId);

		if (ivrUserSession != null && ivrUserSession.getCurrentAssignmentResponse() != null) {

			CurrentAssignmentResponseDto currentAssignmentResponse = objectMapper
					.readValue(ivrUserSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);

			if(ivrLfacsServiceHelper.isSeviceOrder(currentAssignmentResponse)) {
				
				response.setHookReturnCode(HOOK_RETURN_2);
				response.setHookReturnMessage("Service Order Exists");
			} else {
				
				response.setHookReturnCode(HOOK_RETURN_1);
				response.setHookReturnMessage("No Service Order");
			}
		}
		
		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFTD210(String sessionId, String currentState) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		
		String hookReturnCode = "";

		String hookReturnMessage = "";

		try {
			IVRUserSession userSession = cacheService.getBySessionId(sessionId);

			if (userSession == null) {

				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(INVALID_SESSION_ID);

				return response;
			}

			response.setSessionId(sessionId);

			response.setCurrentState(currentState);

			String losDbResponseJsonString = userSession.getLosDbResponse();

			TNInfoResponse losDbResponse = ivrLfacsServiceHelper
					.extractTNInfoFromLosDBResponse(losDbResponseJsonString);

			String tn = "";
			String primaryNpa = "";
			String primaryNxx = "";
			if (losDbResponse != null) {

				tn = losDbResponse.getTn();
				primaryNpa = losDbResponse.getPrimaryNPA();
				primaryNxx = losDbResponse.getPrimaryNXX();
			} else {

				throw new RuntimeException(CANNOT_FETCH_CURR_ASSG_API_LOSDB_NULL);
			}

			IVRCanstEntity canstSession = ivrCanstCacheService.getBySessionId(sessionId);

			if (canstSession == null) {

				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(CANST_INVALID_SESSION_ID);
				return response;
			}			

			AssignServiceOrderRequest assignServiceOrderRequest = ivrCanstHelper.buildAssignServiceOrderRequest(tn, primaryNpa, primaryNxx, userSession.getEmpID(), sessionId, canstSession, userSession);			
			
			String jsonRequest = objectMapper.writeValueAsString(assignServiceOrderRequest);
			
			String assignServiceOrderResultJson = ivrHttpClient.httpPostCall(jsonRequest, assignServiceOrderUrl,
					sessionId, ASSIGN_SERVICE_ORDER_REQUEST); // TODO?? assignServiceOrderUrl set this in DEV/ TEST prop

			String cleanJsonStr = ivrLfacsServiceHelper.cleanResponseString(assignServiceOrderResultJson);

			canstSession.setAssignOrderServiceResp(cleanJsonStr); // TODO ?? are we keeping this to use in later states?

			ivrCanstCacheService.updateSession(canstSession);
			
			AssignServiceOrderResponse responseObject = objectMapper.readValue(cleanJsonStr,
					AssignServiceOrderResponse.class);
			
			if(responseObject != null && responseObject.getMessageStatus() != null && "S".equalsIgnoreCase(responseObject.getMessageStatus().getErrorStatus())) {
				
				hookReturnCode = HOOK_RETURN_0;
				hookReturnMessage = "OK";
			}
		} catch (Exception e) {
			hookReturnCode = HOOK_RETURN_1;
			hookReturnMessage = GPDOWN_ERR_MSG;
			LOGGER.error("CANST FND210 Exception stack trace: ", e);			
		}

		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);		

		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFTD220(String sessionId, String currentState)
			throws JsonMappingException, JsonProcessingException {
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		
		response.setHookReturnCode(HOOK_RETURN_1);
		response.setHookReturnMessage(GPDOWN_ERR_MSG);
		
		IVRCanstEntity canstSession = ivrCanstCacheService.getBySessionId(sessionId);
		
		if(canstSession != null && canstSession.getAssignOrderServiceResp() != null) {
		
			AssignServiceOrderResponse responseObject = objectMapper.readValue(canstSession.getAssignOrderServiceResp(),
					AssignServiceOrderResponse.class);
			
			if(responseObject != null && responseObject.getMessageStatus() != null && "F".equalsIgnoreCase(responseObject.getMessageStatus().getErrorStatus())) {
				
				if(responseObject.getMessageStatus().getHostErrorList() != null && !responseObject.getMessageStatus().getHostErrorList().isEmpty()) {
					
					response.setHookReturnCode(HOOK_RETURN_1);
					response.setHookReturnMessage(GPDOWN_ERR_MSG);
				}
			} else {
				
				response.setHookReturnCode(HOOK_RETURN_8);
				response.setHookReturnMessage(SUCCESS);
			}
		}
		
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFTD300(String sessionId, String currentState)
			throws JsonMappingException, JsonProcessingException {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);

		response.setCurrentState(currentState);

		IVRUserSession ivrUserSession = cacheService.getBySessionId(sessionId);

		if (ivrUserSession != null && ivrUserSession.getCurrentAssignmentResponse() != null) {

			CurrentAssignmentResponseDto currentAssignmentResponse = objectMapper
					.readValue(ivrUserSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);

			int segmentSize = ivrCanstHelper.getSegmentList(currentAssignmentResponse);
			
			IVRCanstEntity ivrCanstSession = ivrCanstCacheService.getBySessionId(sessionId);
			
			if (segmentSize == 1) {

				response.setHookReturnCode(HOOK_RETURN_1);
				response.setHookReturnMessage("F1");
				ivrCanstSession.setSegmentRead("F1");
			} else if (segmentSize == 2) {

				response.setHookReturnCode(HOOK_RETURN_2);
				response.setHookReturnMessage("F2");
				ivrCanstSession.setSegmentRead("F2");
			} else if (segmentSize == 3) {

				response.setHookReturnCode(HOOK_RETURN_3);
				response.setHookReturnMessage("F3");
				ivrCanstSession.setSegmentRead("F3");
			}
			
			ivrCanstCacheService.updateSession(ivrCanstSession);
		}
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFTD315(String sessionId, String currentState, List<String> userInputDTMFList) {
	
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);

		response.setCurrentState(currentState);
		
		if(userInputDTMFList != null && !userInputDTMFList.isEmpty() && userInputDTMFList.size() == 3) {
			
			String firstSet = userInputDTMFList.get(0);
			
			String secondSet = userInputDTMFList.get(1);
			
			String dmInput = userInputDTMFList.get(2);
			
			if(!"0".equals(firstSet)) {
				
				response.setParameters(ivrLfacsServiceHelper.addParamterData(firsetSetOFDefectiveCodes.get(firstSet)));
			} else if(!"0".equals(secondSet)) {
				
				response.setParameters(ivrLfacsServiceHelper.addParamterData(secondSetOFDefectiveCodes.get(secondSet)));
			} 
			
			IVRUserSession userSession = cacheService.getBySessionId(sessionId);
			
			IVRCanstEntity canstSesion = ivrCanstCacheService.getBySessionId(sessionId);
			
			if(userSession != null && canstSesion != null) {
				
				String ctt = ivrCanstHelper.generateCableTroubleTicket(userSession.getEc());
				
				canstSesion.setTroubleTicketNo(ctt);
				
				ivrCanstCacheService.updateSession(canstSesion);
			}
			response.setHookReturnCode(dmInput);
			response.setHookReturnMessage(SUCCESS);
		}

		return response;
	}

	@Override
	public IVRWebHookResponseDto processFTD370(String sessionId, String nextState, List<String> userInputDTMFList) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		
		String hookReturnCode = "";
		String hookReturnMessage = "";


			try {

				IVRUserSession userSession = cacheService.getBySessionId(sessionId);
				

				if (userSession == null) {

					response.setHookReturnCode(HOOK_RETURN);
					response.setHookReturnMessage(INVALID_SESSION_ID);

					return response;
				}
				String losDbResponseJsonString = userSession.getLosDbResponse();

				TNInfoResponse losDbResponse = ivrLfacsServiceHelper
						.extractTNInfoFromLosDBResponse(losDbResponseJsonString);

				String primaryNpa = "";
				String primaryNxx = "";
				if (losDbResponse != null) {

					primaryNpa = losDbResponse.getPrimaryNPA();
					primaryNxx = losDbResponse.getPrimaryNXX();
				} else {

					throw new RuntimeException(CANNOT_FETCH_CURR_ASSG_API_LOSDB_NULL);
				}

				IVRCanstEntity canstSession = ivrCanstCacheService.getBySessionId(sessionId);

				if (canstSession == null) {

					response.setHookReturnCode(HOOK_RETURN);
					response.setHookReturnMessage(CANST_INVALID_SESSION_ID);
					return response;
				}			

				CurrentAssignmentRequestTnDto requestObject = ivrCanstHelper
						.buildCurrentAssignmentInqRequest(primaryNpa, primaryNxx, userInputDTMFList, userSession);
				String jsonRequest = objectMapper.writeValueAsString(requestObject);
				String responseString = ivrLfacsServiceHelper.callCurrentAssignmentInquiryLfacs(jsonRequest,
						userSession);

				responseString = ivrLfacsServiceHelper.cleanResponseString(responseString);
				
				LOGGER.info("Session id:" + userSession.getSessionId()
						+ ", Response from Current Assignment API: " + responseString);
				
				CurrentAssignmentResponseDto responseObject = objectMapper.readValue(responseString,
						CurrentAssignmentResponseDto.class);
				
				
				if (responseObject != null) {
					
					canstSession.setCanstInqType(INQUIRY_BY_CABLE_PAIR);
					ivrCanstCacheService.updateSession(canstSession);
					userSession.setCurrentAssignmentResponse(responseString);
				}
				hookReturnCode = HOOK_RETURN_0;
				hookReturnMessage = "OK";

			} catch (RuntimeException e) {
				hookReturnCode = HOOK_RETURN_1;
				hookReturnMessage = e.getLocalizedMessage();
				LOGGER.error("Exception stack trace: ", e);

			} catch (Exception e) {
				hookReturnCode = HOOK_RETURN_500;
				hookReturnMessage = GPDOWN_ERR_MSG;
				LOGGER.error("Exception stack trace: ", e);
			}
		

		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;

	}

	@Override
	public IVRWebHookResponseDto processFTD371(String sessionId, String nextState, List<String> userDTMFInput)
			throws JsonMappingException, JsonProcessingException {
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = "";
		String hookReturnMessage = "";

		IVRUserSession ivrUserSession = cacheService.getBySessionId(sessionId);

		if (ivrUserSession != null) {
			if (ivrUserSession.getCurrentAssignmentResponse() != null) {
				CurrentAssignmentResponseDto currentAssignmentResponse = objectMapper
						.readValue(ivrUserSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
				try {

					if (currentAssignmentResponse != null) {

						if (currentAssignmentResponse.getMessageStatus() != null) {

							if (IVRLfacsConstants.S.equals(currentAssignmentResponse.getMessageStatus().getErrorStatus())) {

								hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_8;
								hookReturnMessage ="Successful Results of INQ FASG";
								
								
								if(StringUtils.isNotBlank(ivrUserSession.getFacsInqType())) {
									if(IVRConstants.INQUIRY_BY_CABLE_PAIR.equalsIgnoreCase(ivrUserSession.getFacsInqType())) {
										hookReturnMessage = IVRLfacsConstants.ENQUIRY_CP_MSG;
									}
									
								}
								
							} else {

								 if (currentAssignmentResponse.getMessageStatus().getHostErrorList().get(0)
											.getErrorList().get(0).getErrorMessage().contains("L400-160: CABLE PAIR")) {

										hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_6;
										hookReturnMessage = IVRCANSTConstants.CP_NOT_EXIST;

										List<IVRParameter> parameterList = new ArrayList<IVRParameter>();
										IVRParameter parameter = new IVRParameter();
										parameter.setData(ivrUserSession.getCable());
										parameterList.add(parameter);
										
										IVRParameter parameter1 = new IVRParameter();
										parameter1.setData(ivrUserSession.getPair());
										parameterList.add(parameter1);
										response.setParameters(parameterList);
									}
									else if (currentAssignmentResponse.getMessageStatus().getHostErrorList().get(0)
											.getErrorList().get(0).getErrorMessage().contains("L400-160: CABLE")) {

										hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_5;
										hookReturnMessage = IVRCANSTConstants.CABLE_NOT_EXIST;

										List<IVRParameter> parameterList = new ArrayList<IVRParameter>();
										IVRParameter parameter = new IVRParameter();
										parameter.setData(ivrUserSession.getCable());
										parameterList.add(parameter);
										response.setParameters(parameterList);
									}

                           else if (!currentAssignmentResponse.getMessageStatus().getHostErrorList().get(0)
										.getErrorList().get(0).getErrorMessage().contains("L400-160")) {

									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_7;
									hookReturnMessage = IVRCANSTConstants.CP_ON_WORKING;

									List<IVRParameter> parameterList = new ArrayList<IVRParameter>();
									IVRParameter parameter = new IVRParameter();
									parameter.setData(userDTMFInput.get(0));
									parameterList.add(parameter);
									response.setParameters(parameterList);
								}

								else {
									IVRWebHookResponseDto fastivrErrorResp = ivrLfacsServiceHelper.findFastivrError(
											sessionId, nextState, currentAssignmentResponse.getMessageStatus());
									hookReturnCode = fastivrErrorResp.getHookReturnCode();
									hookReturnMessage = fastivrErrorResp.getHookReturnMessage();
								}
							}

						} else {

							hookReturnMessage = "Message Status Not Found";

						}
					} else {
						hookReturnMessage = "Current Assignment not found";

					}

				} catch (Exception ex) {
					LOGGER.error("Exception stack trace: ", ex);
					hookReturnCode = HOOK_RETURN;
					hookReturnMessage = FASTIVR_BACKEND_ERR;

				}
			} else {
				hookReturnMessage = "Current Assignment is null";
			}
		} else {
			hookReturnMessage = INVALID_SESSION_ID;
		}
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info(hookReturnMessage, sessionId);
		return response;
	}

	
	@Override
	public IVRWebHookResponseDto processFTD317(String sessionId, String nextState) throws JsonProcessingException {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode="";
		String hookReturnMessage="";
		
		IVRUserSession ivrUserSession = cacheService.getBySessionId(sessionId);

		if (ivrUserSession != null && ivrUserSession.getCurrentAssignmentResponse() != null) {

			CurrentAssignmentResponseDto currentAssignmentResponse = objectMapper.readValue(ivrUserSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);

			if(!ivrLfacsServiceHelper.isSeviceOrder(currentAssignmentResponse)) {
				
				hookReturnCode=HOOK_RETURN_2;
				hookReturnMessage="Service Order is Empty";
			}
			else {
				
				String losDbResponseJsonString = ivrUserSession.getLosDbResponse();
				TNInfoResponse losDbResponse = ivrLfacsServiceHelper.extractTNInfoFromLosDBResponse(losDbResponseJsonString);
				String tn = "";
				String primaryNpa = "";
				String primaryNxx = "";
				if (losDbResponse != null) {
					tn = losDbResponse.getTn();
					primaryNpa = losDbResponse.getPrimaryNPA();
					primaryNxx = losDbResponse.getPrimaryNXX();
				} 
				else {
					throw new RuntimeException(CANNOT_FETCH_CURR_ASSG_API_LOSDB_NULL);
				}

				IVRCanstEntity canstSession = ivrCanstCacheService.getBySessionId(sessionId);
				ChangeLoopAssignmentRequest changeLoopAssignmentRequest = ivrCanstHelper.buildChangeLoopAssignmentRequest(tn, primaryNpa, primaryNxx, sessionId, canstSession, ivrUserSession);			
				String jsonRequest = objectMapper.writeValueAsString(changeLoopAssignmentRequest);
				String responseStr = ivrHttpClient.httpPostCall(jsonRequest, changeLoopAssignmentUrl,sessionId, IVRCNFConstants.CHANGE_LOOP_ASSIGNMENT_REQUEST); 
				String cleanJsonStr = ivrLfacsServiceHelper.cleanResponseString(responseStr);

				ChangeLoopAssignmentResponse changeLoopAssignmentResponse = objectMapper.readValue(responseStr,ChangeLoopAssignmentResponse.class);
				
				if(changeLoopAssignmentResponse != null) {
					canstSession.setChangeServTermResp(cleanJsonStr);
					
				}
				ivrCanstCacheService.updateSession(canstSession);
				
				if (changeLoopAssignmentResponse != null && IVRLfacsConstants.S.equals(changeLoopAssignmentResponse.getMessageStatus().getErrorStatus())) {
					
					hookReturnCode = HOOK_RETURN_0;
					hookReturnMessage = "Success";
					
					String pageText=ivrCanstPagerText.getChangeNewSerTermPagerText(sessionId);
					if (ivrUserSession.isCanBePagedMobile()) {
						ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_CHANGE_NEW_SERV_TERM_INQUIRY,pageText, NET_PHONE_DEVICE, ivrUserSession);
					}

					if (ivrUserSession.isCanBePagedEmail()) {
						ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_CHANGE_NEW_SERV_TERM_INQUIRY,pageText, NET_MAIL_DEVICE, ivrUserSession);
					}
					 
				} 
				else {

					hookReturnCode = HOOK_RETURN_1;
					hookReturnMessage = GPDOWN_ERR_MSG;
				}
				
				
				
			}
		}
	
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFTD320(String sessionId, String nextState) throws JsonMappingException, JsonProcessingException, HttpTimeoutException, InterruptedException, ExecutionException {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		
		response.setHookReturnCode(HOOK_RETURN_2);
		response.setHookReturnMessage(SUCCESS);

		IVRUserSession ivrUserSession = cacheService.getBySessionId(sessionId);
		
		if(!ivrUserSession.isCanBePagedMobile()) {
			response.setHookReturnCode(HOOK_RETURN_3);
		}
		else {
			issueCandPairsReq(response, ivrUserSession);
			cacheService.updateSession(ivrUserSession);
		}
		return response;
	}
	
	private void issueCandPairsReq(IVRWebHookResponseDto response, IVRUserSession ivrUserSession) throws JsonMappingException, JsonProcessingException, HttpTimeoutException, InterruptedException, ExecutionException {

		IVRCanstEntity canstSession = ivrCanstCacheService.getBySessionId(ivrUserSession.getSessionId());
		if (ivrUserSession.getCurrentAssignmentResponse() != null) {

			CurrentAssignmentResponseDto currentAssignmentResponse = objectMapper
					.readValue(ivrUserSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);

			if (currentAssignmentResponse != null) {
				
				String losDbResponseJsonString = ivrUserSession.getLosDbResponse();
				TNInfoResponse losDbResponse = ivrLfacsServiceHelper
						.extractTNInfoFromLosDBResponse(losDbResponseJsonString);
				String tn = "";
				String primaryNpa = "";
				String primaryNxx = "";
				if (losDbResponse != null) {
					tn = losDbResponse.getTn();
					primaryNpa = losDbResponse.getPrimaryNPA();
					primaryNxx = losDbResponse.getPrimaryNXX();
				} else {
					throw new RuntimeException(CANNOT_FETCH_CURR_ASSG_API_LOSDB_NULL);
				}
				
				int segSize = currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSEG().size();
				int currSegNo = segSize-1;
				if (StringUtils.isBlank(ivrCanstHelper.getSeviceOrder(currentAssignmentResponse))) {
					//Tech will get maintenance candidate pairs
					canstSession.setCanstInqType(IVRConstants.MAINTENANCE_CANDIDATE_PAIRS);// service order is present -> loop assignment
					processRetrieveLoopAssignmentInquiry(ivrUserSession.getSessionId(), currSegNo, segSize, ivrUserSession,
							currentAssignmentResponse, tn, primaryNpa, primaryNxx,response);
				} else {
					// no service order -> maintenance ticket change
					processRetrieveMaintenanceTicketChangeInquiry(ivrUserSession.getSessionId(), currSegNo, segSize, ivrUserSession,
							currentAssignmentResponse, tn, primaryNpa, primaryNxx,response);
					
					//Tech will get Installation candidate pairs
					canstSession.setCanstInqType(IVRConstants.INSTALLATION_CANDIDATE_PAIRS);
				}
				
			}
		}
	}
	
	private void processRetrieveMaintenanceTicketChangeInquiry(String sessionId, int currSegNo, int segSize, IVRUserSession userSession,
			CurrentAssignmentResponseDto currentAssignresponseObject, String tn, String primaryNpa, String primaryNxx,IVRWebHookResponseDto response)
			throws JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException,
			JsonMappingException {
		response.setHookReturnMessage(HOOK_RETURN_1);
		RetrieveMaintenanceChangeTicketRequest request = ivrLfacsServiceHelper
				.buildRetriveMainInqRequest(tn, primaryNpa, primaryNxx, currentAssignresponseObject, userSession, currSegNo);

		String jsonRequest = objectMapper.writeValueAsString(request);
		String responseString = ivrLfacsServiceHelper.callSparePairInquiryLfacs(jsonRequest,userSession);

		RetrieveMaintenanceChangeTicketResponse responseObject = objectMapper.readValue(responseString,RetrieveMaintenanceChangeTicketResponse.class);
		
		if (responseObject != null) {
			userSession.setRtrvMaintChngeMsgName(responseString);
			userSession.setCandPairCounter(-1);
			response.setHookReturnMessage(HOOK_RETURN_2);
		}
		
		if(userSession.isCanBePagedMobile()) {
			sprPrPageBuilder.pageForRetrieveMaintenanceChangeTicket(responseObject, 
					sessionId, currSegNo, NET_PHONE_DEVICE, FormatUtilities.FormatTelephoneNNNXXXX(tn));
		} else {
			sprPrPageBuilder
			.pageForRetrieveMaintenanceChangeTicket(responseObject, 
					sessionId, currSegNo, NET_MAIL_DEVICE, FormatUtilities.FormatTelephoneNNNXXXX(tn));
		}
		
	}

	private void processRetrieveLoopAssignmentInquiry(String sessionId, int currSegNo, int segSize, IVRUserSession userSession,
			CurrentAssignmentResponseDto currentAssignresponseObject, String tn, String primaryNpa, String primaryNxx,IVRWebHookResponseDto response)
			throws JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException,
			JsonMappingException {
			response.setHookReturnMessage(HOOK_RETURN_1);
			RetrieveLoopAssignmentRequest request = ivrLfacsServiceHelper
					.buildRetriveLoopAssignInqRequest(tn, primaryNpa, primaryNxx,currentAssignresponseObject, userSession, currSegNo);

			String jsonRequest = objectMapper.writeValueAsString(request);
			String responseString = ivrLfacsServiceHelper.callSparePairLoopAssignInquiryLfacs(jsonRequest,userSession);

			RetrieveLoopAssignmentResponse responseObject = objectMapper.readValue(responseString,RetrieveLoopAssignmentResponse.class);
			
			if (responseObject != null) {
				userSession.setRtrvLoopAssgMsgName(responseString);
				userSession.setCandPairCounter(-1);
				response.setHookReturnMessage(HOOK_RETURN_2);
			}
			
			//userInput is the Segment which Spare pair will cover
			if(userSession.isCanBePagedMobile()) {
				sprPrPageBuilder.pageForRetrieveLoopAssignment(responseObject, 
						sessionId, currSegNo, NET_PHONE_DEVICE, FormatUtilities.FormatTelephoneNNNXXXX(tn));
			} else {
				sprPrPageBuilder
				.pageForRetrieveLoopAssignment(responseObject, 
						sessionId, currSegNo, NET_MAIL_DEVICE, FormatUtilities.FormatTelephoneNNNXXXX(tn));
			}
	}


	@Override
	public IVRWebHookResponseDto processFTD231(String sessionId) throws JsonProcessingException {


		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		IVRCanstEntity canstSession = ivrCanstCacheService.getBySessionId(sessionId);
		IVRUserSession userSession = cacheService.getBySessionId(sessionId);

		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;

		if (null != userSession && null != userSession.getCurrentAssignmentResponse()) {

			currentAssignmentResponseDto = objectMapper.readValue(userSession.getCurrentAssignmentResponse(),
					CurrentAssignmentResponseDto.class);

			response = ivrCanstHelper.getColourCode(response, currentAssignmentResponseDto, canstSession);

		}

		ivrCanstCacheService.updateSession(canstSession);
		response.setSessionId(sessionId);
		response.setCurrentState(STATE_FTD231);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_FTD231);
		return response;

	}

	@Override
	public IVRWebHookResponseDto processFTD240(String sessionId) {


		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		String hookReturnCode = new String();
		String hookReturnMessage = new String();

		IVRCanstEntity canstSession = ivrCanstCacheService.getBySessionId(sessionId);
		IVRUserSession userSession = cacheService.getBySessionId(sessionId);

		if (null != userSession && userSession.isCanBePagedMobile()) {
			hookReturnCode = HOOK_RETURN_2;
			hookReturnMessage = ALPHA_PAGER;
		} else {
			hookReturnCode = HOOK_RETURN_1;
			hookReturnMessage = NO_ALPHA_PAGER;
		}

		ivrCanstCacheService.updateSession(canstSession);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		response.setSessionId(sessionId);
		response.setCurrentState(STATE_FTD240);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_FTD240 + " hookReturnCode: " + hookReturnCode + " hookReturnMessage: " + hookReturnMessage);
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFTD400(String sessionId) throws JsonProcessingException, HttpTimeoutException {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		String hookReturnCode = "";

		String hookReturnMessage = "";

		String losDbResponseJsonString = "";

		IVRUserSession userSession = cacheService.getBySessionId(sessionId);

		if(null != userSession) {
			 losDbResponseJsonString = userSession.getLosDbResponse();

		}
			TNInfoResponse losDbResponse = ivrLfacsServiceHelper
					.extractTNInfoFromLosDBResponse(losDbResponseJsonString);


		String primaryNpa = "";
		String primaryNxx = "";

		if (null != losDbResponse) {
			primaryNpa = losDbResponse.getPrimaryNPA();
			primaryNxx = losDbResponse.getPrimaryNXX();
		}

		IVRCanstEntity canstSession = ivrCanstCacheService.getBySessionId(sessionId);

		UpdateLoopRequestDto request = ivrCanstHelper.buildUpdateLoopAssignmentRequest(primaryNpa,
					primaryNxx, userSession, canstSession);

			String jsonRequest = objectMapper.writeValueAsString(request);
		asyncService.updateLoopRequest(sessionId, jsonRequest, updateLoopUrl, userSession, canstSession);


				if (null != userSession && userSession.isCanBePagedMobile()) {
					hookReturnCode = HOOK_RETURN_3;
					hookReturnMessage = ALPHA_PAGER;
				} else {
					hookReturnCode = HOOK_RETURN_2;
					hookReturnMessage = NO_ALPHA_PAGER;
				}


		ivrCanstCacheService.updateSession(canstSession);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		response.setSessionId(sessionId);
		response.setCurrentState(STATE_FTD400);
		LOGGER.info("Session:  " + sessionId + "," + " current state:" + STATE_FTD400 + " hookReturnCode: " + hookReturnCode + " hookReturnMessage: " + hookReturnMessage);
		return response;
	}

	public IVRWebHookResponseDto processFTD330(String sessionId, String nextState)throws JsonMappingException, JsonProcessingException {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode="";
		String hookReturnMessage="";
		ChangeLoopAssignmentResponse changeLoopAssignmentResponse =null;
		IVRCanstEntity canstSession = ivrCanstCacheService.getBySessionId(sessionId);
		
		if(canstSession!=null && canstSession.getChangeServTermResp()!=null) {
			 changeLoopAssignmentResponse = objectMapper.readValue(canstSession.getChangeServTermResp(),ChangeLoopAssignmentResponse.class);

			 if(changeLoopAssignmentResponse!=null) {
				 
				 if(IVRLfacsConstants.S.equals(changeLoopAssignmentResponse.getMessageStatus().getErrorStatus())) {
					 hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_8;
					 hookReturnMessage = "Success";
				 }
				 else {
					 if(changeLoopAssignmentResponse.getReturnDataSet().getChangeLoopAssignmentCandidatePairInfo()==null) {
						 hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_5;
						 hookReturnMessage = "No Candidate Pair Available";
					 }
					 else {
					 
						 hookReturnCode = HOOK_RETURN_1;
						 hookReturnMessage = GPDOWN_ERR_MSG;
					 }
				 
				 }
			 }
		}
		
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		
		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFTD351(String sessionId, String nextState, String userDTMFInput) {
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(nextState);

		response.setHookReturnCode(HOOK_RETURN_1);
		response.setHookReturnMessage(SUCCESS);
		if (userDTMFInput.contains("*")) {
			String convertedInput = ivrCanstHelper.convertInputCodesToAlphabets(userDTMFInput);

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
			
			List<IVRParameter> ivrParameterList = ivrLfacsServiceHelper.addParamterData(userDTMFInput);
			response.setParameters(ivrParameterList);
		}
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFTD380(String sessionId, String nextState)throws JsonMappingException, JsonProcessingException {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode="";
		String hookReturnMessage="";
		ChangeLoopAssignmentResponse changeLoopAssignmentResponse =null;
		IVRCanstEntity canstSession = ivrCanstCacheService.getBySessionId(sessionId);
		
		if(canstSession!=null && canstSession.getChangeServTermResp()!=null) {
			 changeLoopAssignmentResponse = objectMapper.readValue(canstSession.getChangeServTermResp(),ChangeLoopAssignmentResponse.class);

			 if(changeLoopAssignmentResponse!=null) {
				 
				 if(IVRLfacsConstants.S.equals(changeLoopAssignmentResponse.getMessageStatus().getErrorStatus())) {
					 hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_8;
					 hookReturnMessage = "Success";
				 }
				 else {
					 if(changeLoopAssignmentResponse.getReturnDataSet().getChangeLoopAssignmentCandidatePairInfo()==null) {
						 hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_5;
						 hookReturnMessage = "No Candidate Pair Available";
					 }
					 else {
					 
						 hookReturnCode = HOOK_RETURN_1;
						 hookReturnMessage = GPDOWN_ERR_MSG;
					 }
				 
				 }
			 }
		}
		
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		
		return response;
	}
}
