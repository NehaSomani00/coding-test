package com.lumen.fastivr.IVRCNF.service;

import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.CHANGE_LOOP_ASSIGNMENT_REQUEST;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.CNF_INVALID_SESSION_ID;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.INQUIRY_BY_SERVICE_ORDER;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.INQUIRY_BY_TROUBLE_TICKET;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.LOOP_QUAL_NII_SERVICE_REQUEST;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.MAINTENANCE_CHANGE_LOOP_REQUEST;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.STATE_FN0190;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.STATE_FN0200;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.STATE_FN0210;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.STATE_FN0211;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.STATE_FN0212;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.STATE_FN0213;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.TN_NOT_FOUND_IN_TABLE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.CANNOT_FETCH_CURR_ASSG_API_LOSDB_NULL;
import static com.lumen.fastivr.IVRUtils.IVRConstants.DTMF_INPUT_2;
import static com.lumen.fastivr.IVRUtils.IVRConstants.FASTIVR_BACKEND_ERR;
import static com.lumen.fastivr.IVRUtils.IVRConstants.GPDOWN_ERR_MSG;
import static com.lumen.fastivr.IVRUtils.IVRConstants.GPDOWN_ERR_MSG_CODE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INVALID_SESSION_ID;
import static com.lumen.fastivr.IVRUtils.IVRConstants.NETPAGE_SUBJECT_CUT_TO_NEW_FACILITY_INQUIRY;
import static com.lumen.fastivr.IVRUtils.IVRConstants.NET_MAIL_DEVICE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.NET_PHONE_DEVICE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.SUCCESS;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_0;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_1;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_2;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_3;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_4;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_5;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_6;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_7;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_8;

import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.lumen.fastivr.IVRCNF.Dto.ChangeLoopAssignmentRequestDto;
import com.lumen.fastivr.IVRCNF.Dto.ChangeLoopAssignmentResponseDto;
import com.lumen.fastivr.IVRCNF.Dto.LoopQualNIIServiceRequest;
import com.lumen.fastivr.IVRCNF.Dto.MainChangeTicketLoopRequest;
import com.lumen.fastivr.IVRCNF.Dto.MainChangeTicketLoopResponse;
import com.lumen.fastivr.IVRCNF.Dto.NIIServiceResponse;
import com.lumen.fastivr.IVRCNF.Dto.NetworkInfrastructure;
import com.lumen.fastivr.IVRCNF.entity.IVRCnfEntity;
import com.lumen.fastivr.IVRCNF.helper.IVRCnfHelper;
import com.lumen.fastivr.IVRCNF.repository.IVRCnfCacheService;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.LOSDB.CTelephone;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketRequest;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketResponse;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetLoopAssigInputData;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentRequest;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentResponse;
import com.lumen.fastivr.IVRLFACS.CTelephoneBuilder;
import com.lumen.fastivr.IVRLFACS.IVRLfacsPagerTextFormation;
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

@Service
public class IVRCnfServiceImpl implements IVRCnfService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IVRCnfServiceImpl.class);

	@Autowired
	private IVRCacheService cacheService;
	
	@Autowired
	private SparePairPageBuilder sprPrPageBuilder;

	@Autowired
	private IVRCnfCacheService ivrCnfCacheService;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private IVRHttpClient ivrHttpClient;

	@Autowired
	private LfacsValidation tnValidation;

	@Autowired
	private IVRCnfHelper ivrCnfHelper;

	@Autowired
	private IVRLfacsService ivrLfacsService;

	@Autowired
	private IVRLfacsServiceHelper ivrLfacsServiceHelper;
	
	@Autowired
	private IVRLfacsPagerTextFormation ivrLfacespagerTextFormation;

	@Autowired
	private IVRCnfPagerTextFormation ivrCnfPagerTextFormation;

	@Value("#{${inputCodes}}")
	Map<String, String> inputCodes = new HashMap<String, String>();
	
	@Value("#{${firstSetoFDefectiveCodes}}")
	Map<String, String> firsetSetOFDefectiveCodes = new HashMap<String, String>();
	
	@Value("#{${secondSetoFDefectiveCodes}}")
	Map<String, String> secondSetOFDefectiveCodes = new HashMap<String, String>();
	
	@Value("${cnf.loopqual.nii.service.url}")
	private String loopQualNIIServiceUrl;
	
	@Value("${cnf.main.change.loop.assign.url}")
	private String mainChangeLoopAssignUrl;
	
	@Value("${cnf.change.loop.assignment.url}")
	private String changeLoopAssignmentUrl;	
	
	@Value("${cnf.change.pair.status.url}")
	private String changePairStatusUrl;	

	@Override
	public IVRWebHookResponseDto processFND035(String sessionId, String currentState, List<String> userInputDTMFList)
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

					String convertedInputs = ivrCnfHelper.convertInputCodesToAlphabets(postFixNo);

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

					response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_2);

					response.setHookReturnMessage("OK");

					List<IVRParameter> ivrParameterList = new ArrayList<IVRParameter>();

					IVRParameter ivrParameter = new IVRParameter();
					String serviceOrderNumber = preFix + postFix;
					ivrParameter.setData(serviceOrderNumber);

					ivrParameterList.add(ivrParameter);
					response.setParameters(ivrParameterList);
					
					IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);

					if (cnfSession == null) {

						cnfSession = new IVRCnfEntity();

						cnfSession.setSessionId(sessionId);
						cnfSession.setServiceOrderNo(serviceOrderNumber);
						ivrCnfCacheService.addSession(cnfSession);
					}else {
						cnfSession.setServiceOrderNo(serviceOrderNumber);
						ivrCnfCacheService.updateSession(cnfSession);
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
	public IVRWebHookResponseDto processFND055(String sessionId, String currentState, String userDTMFInput) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		IVRUserSession session = cacheService.getBySessionId(sessionId);
		
		if (session != null) {
			
			session.setFacsInqType(IVRConstants.INQUIRY_BY_TN);
			response = tnValidation.validateFacsTN(userDTMFInput, session);
			
			if (HOOK_RETURN_1.equalsIgnoreCase(response.getHookReturnCode())) {

				IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);

				if (cnfSession != null) {

					cnfSession.setCnfInqType(INQUIRY_BY_SERVICE_ORDER);
				} 
				
				ivrCnfHelper.insertCurrentAssignment(session);
				
				ivrCnfCacheService.updateSession(cnfSession);
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
	public IVRWebHookResponseDto processFND059(String sessionId, String currentState)
			throws JsonMappingException, JsonProcessingException {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		String hookReturnMessage = "";

		try {
			IVRUserSession userSession = cacheService.getBySessionId(sessionId);

			CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
			//RetrieveLoopAssignmentResponse responseObject = null;

			if (userSession != null) {

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

				IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);

				currentAssignmentResponseDto = objectMapper.readValue(userSession.getCurrentAssignmentResponse(),
						CurrentAssignmentResponseDto.class);
				
				if(currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
						&& (currentAssignmentResponseDto.getReturnDataSet().getLoop() == null || "F".equalsIgnoreCase(currentAssignmentResponseDto.getMessageStatus().getErrorStatus()))) {
					
					response.setHookReturnCode(HOOK_RETURN);
					response.setHookReturnMessage("CurrentAssignment is null");
					
					return response;
				}

				RetrieveLoopAssignmentRequest request = ivrLfacsServiceHelper.buildRetriveLoopAssignInqRequest(tn,
						primaryNpa, primaryNxx, currentAssignmentResponseDto, userSession, 1);

				RetLoopAssigInputData inputData1 = request.getInputData();

				if (currentAssignmentResponseDto != null) {
					
					if(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID() != null) {
						
						inputData1.setCircuitId(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID());
					} else if(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null 
							&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty() 
							&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getCKID() != null) {
						
						inputData1.setCircuitId(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getCKID());
					}
				}
				inputData1.setRetrieveActionCode("RSOCP");

				if (cnfSession != null) {

					inputData1.setServiceOrderNumber(cnfSession.getServiceOrderNo());
				}

				request.setInputData(inputData1);

				String jsonRequest = objectMapper.writeValueAsString(request);

				String responseString = ivrLfacsServiceHelper.callSparePairLoopAssignInquiryLfacs(jsonRequest,
						userSession);

				RetrieveLoopAssignmentResponse responseObject = objectMapper.readValue(responseString,
						RetrieveLoopAssignmentResponse.class);

				if (responseObject == null) {

					response.setHookReturnCode(HOOK_RETURN_1);
					response.setHookReturnMessage(GPDOWN_ERR_MSG);
					return response;
				}
				userSession.setRtrvLoopAssgMsgName(responseString);
				response.setHookReturnCode(HOOK_RETURN_0);
				response.setHookReturnMessage(SUCCESS);
				cacheService.updateSession(userSession);
			} else {
				
				hookReturnMessage = IVRConstants.INVALID_SESSION_ID;
				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(hookReturnMessage);
			}
		} catch (MismatchedInputException e) {
			
			response.setHookReturnCode(HOOK_RETURN_1);			
			response.setHookReturnMessage(GPDOWN_ERR_MSG);
		} catch (JsonMappingException e1) {
			
			response.setHookReturnCode(HOOK_RETURN_1);			
			response.setHookReturnMessage(GPDOWN_ERR_MSG);
		} catch (HttpTimeoutException | InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	@Override
	public IVRWebHookResponseDto processFND060(String sessionId, String currentState)
			throws JsonMappingException, JsonProcessingException {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		String hookReturnCode = "";
		String hookReturnMessage = "";

		try {

			IVRUserSession userSession = cacheService.getBySessionId(sessionId);

			if (userSession != null && userSession.getRtrvLoopAssgMsgName()!=null) {
				
				IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);

				String responseString = userSession.getRtrvLoopAssgMsgName();

				RetrieveLoopAssignmentResponse	responseObject = objectMapper.readValue(responseString, RetrieveLoopAssignmentResponse.class);

				if (responseObject != null) {

					if (IVRLfacsConstants.S.equals(responseObject.getMessageStatus().getErrorStatus())) {

						List<IVRParameter> parameters = new ArrayList<IVRParameter>();
						IVRParameter ivrParameter = new IVRParameter();
						ivrParameter.setData(userSession.getInquiredTn());
						parameters.add(ivrParameter);
						response.setParameters(parameters);
						
						response.setHookReturnCode(HOOK_RETURN_8);
						response.setHookReturnMessage(SUCCESS);
					} else if (responseObject.getMessageStatus().getHostErrorList().get(0).getErrorList().get(0)
							.getErrorMessage().contains("S017-020")
							|| responseObject.getMessageStatus().getHostErrorList().get(0).getErrorList().get(0)
									.getErrorMessage().contains("L150-435")) {

						response.setHookReturnCode(HOOK_RETURN_0);
						response.setHookReturnMessage("Service order not found");
						
						List<IVRParameter> parameters = new ArrayList<IVRParameter>();
						IVRParameter ivrParameter = new IVRParameter();

						if (cnfSession != null) {
							
							ivrParameter.setData(cnfSession.getServiceOrderNo());
							parameters.add(ivrParameter);
						}

						IVRParameter ivrParameter2 = new IVRParameter();
						ivrParameter2.setData(userSession.getInquiredTn());
						parameters.add(ivrParameter2);
						response.setParameters(parameters);
					} else if (responseObject.getMessageStatus().getHostErrorList().get(0).getErrorList().get(0)
							.getErrorMessage().contains("L150-002")) {

						response.setHookReturnCode(HOOK_RETURN_5);
						response.setHookReturnMessage("No assignment for service order");
					} else if (responseObject.getMessageStatus().getHostErrorList().get(0).getErrorList().get(0)
							.getErrorMessage().contains("L150-451")) {

						response.setHookReturnCode(HOOK_RETURN_7);
						response.setHookReturnMessage("Multiple circuit terminations");
					} else if (responseObject.getMessageStatus().getHostErrorList().get(0).getErrorList().get(0)
							.getErrorMessage().contains("L150-460")) {

						response.setHookReturnCode(HOOK_RETURN_6);
						response.setHookReturnMessage("Abnormal LFACS result");
					} else {

						LOGGER.info(" Entering else part");
						IVRWebHookResponseDto fastivrErrorResp = ivrLfacsServiceHelper.findFastivrError(sessionId,
								currentState, responseObject.getMessageStatus());

						LOGGER.info(" Executed  else part : " + fastivrErrorResp.getHookReturnCode());

						hookReturnCode = fastivrErrorResp.getHookReturnCode();
						hookReturnMessage = fastivrErrorResp.getHookReturnMessage();

						response.setHookReturnCode(hookReturnCode);
						response.setHookReturnMessage(hookReturnMessage);
					}
					cacheService.updateSession(userSession);
				}
			} else {
				hookReturnMessage = IVRConstants.INVALID_SESSION_ID;
				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(hookReturnMessage);
			}
		}

		catch (Exception e) {
			response.setHookReturnCode(HOOK_RETURN);
			response.setHookReturnMessage(FASTIVR_BACKEND_ERR);
			LOGGER.error("FND059 Exception stack trace: ", e);
		}

		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFND075(String sessionId, String currentState, String userDTMFInput) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		IVRUserSession session = cacheService.getBySessionId(sessionId);

		if (session != null) {
			
			session.setFacsInqType(IVRConstants.INQUIRY_BY_TN);
			response = tnValidation.validateFacsTN(userDTMFInput, session);
			
			if (HOOK_RETURN_1.equalsIgnoreCase(response.getHookReturnCode())) {
				
				IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);

				if (cnfSession != null) {

					cnfSession.setCnfInqType(INQUIRY_BY_TROUBLE_TICKET);
				} else {

					cnfSession = new IVRCnfEntity();
					cnfSession.setSessionId(sessionId);
					cnfSession.setCnfInqType(INQUIRY_BY_TROUBLE_TICKET);
					ivrCnfCacheService.addSession(cnfSession);
				}

				ivrCnfHelper.insertCurrentAssignment(session);
				
				ivrCnfCacheService.updateSession(cnfSession);
			} else if(HOOK_RETURN_0.equalsIgnoreCase(response.getHookReturnCode())) {
				
				response.setHookReturnMessage(HOOK_RETURN_0);
				response.setHookReturnMessage(TN_NOT_FOUND_IN_TABLE);
			}
		} else {

			response.setHookReturnMessage(HOOK_RETURN);
			response.setHookReturnMessage(INVALID_SESSION_ID);
		}

		cacheService.updateSession(session);
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFND085(String sessionId, String currentState) {

		return ivrLfacsService.processFID020Code(sessionId, currentState);
	}

	@Override
	public IVRWebHookResponseDto processFND090(String sessionId, String currentState)
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
								hookReturnMessage = "OK";

								List<IVRParameter> parameterList = ivrLfacsServiceHelper
										.getCablePair(currentAssignmentResponse, 0);

								if (!parameterList.isEmpty()) {

									if (!(parameterList.get(0) != null
											&& StringUtils.isNotBlank(parameterList.get(0).getData()))) {

										hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_5;
										hookReturnMessage = IVRLfacsConstants.ABNORMAL_LFACS_RESULT;
									} else if (!(parameterList.get(1) != null
											&& StringUtils.isNotBlank(parameterList.get(1).getData()))) {

										hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_6;
										hookReturnMessage = IVRLfacsConstants.ABNORMAL_LFACS_RESULT;
									}
								} else if (ivrLfacsServiceHelper.isSeviceOrder(currentAssignmentResponse)) {

									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_7;
									hookReturnMessage = IVRLfacsConstants.SERVICE_ORDER_EXISTS;

									List<IVRParameter> ivrParameterList = ivrLfacsServiceHelper
											.addParamterData(currentAssignmentResponse.getReturnDataSet().getLoop()
													.get(0).getSO().get(0).getORD());

									response.setParameters(ivrParameterList);
								}
							} else {

								if (currentAssignmentResponse.getMessageStatus().getHostErrorList().get(0)
										.getErrorList().get(0).getErrorMessage().contains("L400-192")
										|| currentAssignmentResponse.getMessageStatus().getHostErrorList().get(0)
												.getErrorList().get(0).getErrorMessage().contains("L410-292")) {
									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_0;
									hookReturnMessage = IVRLfacsConstants.INVALID_TN;

									// fetch TN from cache
									String losDbResponseJsonString = ivrUserSession.getLosDbResponse();
									TNInfoResponse losDbResponse = ivrLfacsServiceHelper
											.extractTNInfoFromLosDBResponse(losDbResponseJsonString);
									if (losDbResponse != null) {

										List<IVRParameter> parameterList = new ArrayList<IVRParameter>();
										IVRParameter parameter = new IVRParameter();
										parameter.setData(losDbResponse.getTn());
										parameterList.add(parameter);
										response.setParameters(parameterList);
									}
								} else {
									IVRWebHookResponseDto fastivrErrorResp = ivrLfacsServiceHelper.findFastivrError(
											sessionId, currentState, currentAssignmentResponse.getMessageStatus());
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
	public IVRWebHookResponseDto processFND135(String sessionId, String currentState) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		try {

			IVRUserSession userSession = cacheService.getBySessionId(sessionId);

			CurrentAssignmentResponseDto currentAssignmentResponseDto = null;

			if (userSession != null && userSession.getCurrentAssignmentResponse() != null) {

				currentAssignmentResponseDto = objectMapper.readValue(userSession.getCurrentAssignmentResponse(),
						CurrentAssignmentResponseDto.class);
			} else {

				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(FASTIVR_BACKEND_ERR);
				return response;
			}

			if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
					&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()
					&& ((currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG() != null 
					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty())
							|| (currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null
							&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty() 
							&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG() !=  null
							&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().isEmpty()
							))) {

				List<IVRParameter> parameterList = ivrLfacsServiceHelper.getCablePair(currentAssignmentResponseDto, 0);
				
				IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);

				if (cnfSession != null) {
					
					cnfSession.setCable(parameterList.get(0).getData());
					cnfSession.setPair(parameterList.get(1).getData());
					ivrCnfCacheService.updateSession(cnfSession);
				} else {
					
					response.setHookReturnMessage(HOOK_RETURN);
					response.setHookReturnMessage(CNF_INVALID_SESSION_ID);
					
					return response;
				}
				response.setParameters(parameterList);

				response.setHookReturnCode(HOOK_RETURN_0);
				response.setHookReturnMessage(IVRConstants.SUCCESS);
			} else {

				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(FASTIVR_BACKEND_ERR);
			}
		} catch (Exception ex) {

			// TODO Need to check the HookReturnCode
			LOGGER.error("Exception stack trace: ", ex);
			response.setHookReturnCode(HOOK_RETURN);
			response.setHookReturnMessage(FASTIVR_BACKEND_ERR);
		}

		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFND141(String sessionId, String currentState) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

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

				CTelephone telphone = CTelephoneBuilder.newBuilder(userSession).setTelephone(losDbResponse.getTn()).build();
				
				tn = telphone.getLineNumber();
				
				primaryNpa = telphone.getNpa();
				
				primaryNxx = telphone.getNxx();
				
			} else {

				throw new RuntimeException(CANNOT_FETCH_CURR_ASSG_API_LOSDB_NULL);
			}

			IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);

			if (cnfSession == null) {

				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(CNF_INVALID_SESSION_ID);
				return response;
			}

			LoopQualNIIServiceRequest request = ivrCnfHelper.buildLoopQualNIIServiceRequest(tn, primaryNpa,
					primaryNxx, sessionId, cnfSession);

			String jsonRequest = objectMapper.writeValueAsString(request);

			String loopQualNIIResultJson = ivrHttpClient.httpPostCall(jsonRequest, loopQualNIIServiceUrl,
					sessionId, LOOP_QUAL_NII_SERVICE_REQUEST);

			String cleanJsonStr = ivrLfacsServiceHelper.cleanResponseString(loopQualNIIResultJson);

			cnfSession.setGetLoopQualNIIResponse(cleanJsonStr);

			NIIServiceResponse responseObject = objectMapper.readValue(cleanJsonStr,
					NIIServiceResponse.class);

			if (responseObject != null) {

				ivrCnfCacheService.updateSession(cnfSession);
				response.setHookReturnCode(HOOK_RETURN_0);
				response.setHookReturnMessage("OK");
			} else {

				response.setHookReturnCode(HOOK_RETURN_1);
				response.setHookReturnMessage(GPDOWN_ERR_MSG);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setHookReturnCode(HOOK_RETURN_1);
			response.setHookReturnMessage(GPDOWN_ERR_MSG);
			LOGGER.error("CNF FND141 Exception stack trace: ", e);			
		}
		
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFND143(String sessionId, String currentState) throws JsonMappingException, JsonProcessingException {
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		
		IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);
		
		response.setSessionId(sessionId);

		response.setCurrentState(currentState);
		
		if(cnfSession == null ) {
			
			response.setHookReturnCode(HOOK_RETURN);
			response.setHookReturnMessage(CNF_INVALID_SESSION_ID);
			return response;
		}
		
		String loopQualNIIResultJson = cnfSession.getGetLoopQualNIIResponse();
		
		NIIServiceResponse responseObject = objectMapper.readValue(loopQualNIIResultJson, NIIServiceResponse.class);
		
		if(SUCCESS.equalsIgnoreCase(responseObject.getErrorMessage())) {
			
			if(responseObject.getNetworkInfraStructure() != null && !responseObject.getNetworkInfraStructure().isEmpty()) {
				
				boolean activationFlag = responseObject.getNetworkInfraStructure().get(0).isVoiceActivationFlag();
				
				if(activationFlag) {
					
					response.setHookReturnCode(HOOK_RETURN_8);
					response.setHookReturnMessage("OK");
				} else {
					
					response.setHookReturnCode(HOOK_RETURN_7);
					response.setHookReturnMessage("OK");
				}
			}
		} else {
			
			response = ivrCnfHelper.findFastivrError(responseObject.getErrorCode(), response);
		}
		
		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFND145(String sessionId, String currentState, String userDTMFInput) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		try {

			IVRUserSession userSession = cacheService.getBySessionId(sessionId);

			CurrentAssignmentResponseDto currentAssignmentResponseDto = null;

			if (userSession != null && userSession.getCurrentAssignmentResponse() != null) {

				currentAssignmentResponseDto = objectMapper.readValue(userSession.getCurrentAssignmentResponse(),
						CurrentAssignmentResponseDto.class);
			} else {

				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(FASTIVR_BACKEND_ERR);
				return response;
			}

			if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
					&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()
					&& ((currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG() != null 
					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty())
							|| (currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null
							&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty() 
							&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG() !=  null
							&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().isEmpty()
							))) {
				
				int segmentSize = 0;
				
				if(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG() != null && !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()) {
					
					segmentSize = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().size();
				} else if(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG() !=  null && !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().isEmpty()) {
					
					segmentSize = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().size();
				}
				
				if(DTMF_INPUT_2.equalsIgnoreCase(userDTMFInput)) {
					
					processFND141(sessionId, currentState);
				}

				if (StringUtils.isBlank(userDTMFInput) || HOOK_RETURN_8.equalsIgnoreCase(userDTMFInput)) {

					IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);

					if (cnfSession != null) {
						
						cnfSession.setSegmentRead("F1");
						ivrCnfCacheService.updateSession(cnfSession);
					} else {

						response.setHookReturnMessage(HOOK_RETURN);
						response.setHookReturnMessage(CNF_INVALID_SESSION_ID);
						return response;
					}
					
					if (ivrCnfHelper.isSwitchAvailable(userSession)) {

						response.setHookReturnCode(HOOK_RETURN_5);
						response.setHookReturnMessage("F1 Cut");

						return response;
					} else {
						
						String pagerText = ivrLfacespagerTextFormation.getPageCurrentAssignment(response, userSession, currentAssignmentResponseDto);
						
						if (userSession.isCanBePagedMobile()) {
							
							LOGGER.info("FID145 Status getting success", sessionId);
							// TODO Pager Text incomplete
							ivrLfacsServiceHelper.sendTestResultToTech(NETPAGE_SUBJECT_CUT_TO_NEW_FACILITY_INQUIRY,
									pagerText, NET_PHONE_DEVICE, userSession);

							response.setHookReturnCode(HOOK_RETURN_0);
							response.setHookReturnMessage("SWITCH down for an F1 cut and tech has alpha pager");

							return response;
						} else {

							LOGGER.info("FID145 Status getting success", sessionId);
							// TODO Pager Text incomplete
							ivrLfacsServiceHelper.sendTestResultToTech(NETPAGE_SUBJECT_CUT_TO_NEW_FACILITY_INQUIRY,
									pagerText, NET_MAIL_DEVICE, userSession);

							response.setHookReturnCode(HOOK_RETURN_1);
							response.setHookReturnMessage("SWITCH down for an F1 cut and tech has NO alpha pager");

							return response;
						}
					}
				} else if (segmentSize > 1) {
					
					List<IVRParameter> parameterList = ivrLfacsServiceHelper.getCablePair(currentAssignmentResponseDto,	1);

					if (parameterList != null && !parameterList.isEmpty() && parameterList.size() == 2) {

						response.setParameters(parameterList);

						response.setHookReturnCode(HOOK_RETURN_3);
						response.setHookReturnMessage("f2 exists");
						
						IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);

						if (cnfSession != null) {
							
							cnfSession.setCable(parameterList.get(0).getData());
							cnfSession.setPair(parameterList.get(1).getData());
							ivrCnfCacheService.updateSession(cnfSession);
						} else {
							
							response.setHookReturnMessage(HOOK_RETURN);
							response.setHookReturnMessage(CNF_INVALID_SESSION_ID);
							
							return response;
						}

						return response;
					} else if (segmentSize > 2) {

						parameterList = ivrLfacsServiceHelper.getCablePair(currentAssignmentResponseDto, 2);

						if (parameterList != null && !parameterList.isEmpty() && parameterList.size() == 2) {

							response.setParameters(parameterList);

							response.setHookReturnCode(HOOK_RETURN_4);
							response.setHookReturnMessage("No f2, but f3 exists");
							
							IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);

							if (cnfSession != null) {
								
								cnfSession.setCable(parameterList.get(0).getData());
								cnfSession.setPair(parameterList.get(1).getData());
								ivrCnfCacheService.updateSession(cnfSession);
							} else {
								
								response.setHookReturnMessage(HOOK_RETURN);
								response.setHookReturnMessage(CNF_INVALID_SESSION_ID);
								
								return response;
							}

							return response;
						}
					}
				} else if (segmentSize == 1) {

					response.setHookReturnCode(HOOK_RETURN_2);
					response.setHookReturnMessage("No f2 or f3");
				}
			} else {

				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(FASTIVR_BACKEND_ERR);
			}
		} catch (Exception ex) {

			// TODO Need to check the HookReturnCode
			LOGGER.error("Exception stack trace: ", ex);
			response.setHookReturnCode(HOOK_RETURN);
			response.setHookReturnMessage(FASTIVR_BACKEND_ERR);
		}

		return response;
	}

	@Override
	public IVRWebHookResponseDto processFND155(String sessionId, String currentState, String userDTMFInput) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		if (userDTMFInput.equalsIgnoreCase("1")) {

			response.setHookReturnCode(HOOK_RETURN_2);
			response.setHookReturnMessage("F2 Cut");
			
			IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);

			if (cnfSession != null) {

				cnfSession.setSegmentRead("F2");
				ivrCnfCacheService.updateSession(cnfSession);
			} else {

				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(CNF_INVALID_SESSION_ID);

				return response;
			}
			
		} else {

			try {

				IVRUserSession userSession = cacheService.getBySessionId(sessionId);

				CurrentAssignmentResponseDto currentAssignmentResponseDto = null;

				if (userSession != null && userSession.getCurrentAssignmentResponse() != null) {

					currentAssignmentResponseDto = objectMapper.readValue(userSession.getCurrentAssignmentResponse(),
							CurrentAssignmentResponseDto.class);
				} else {

					response.setHookReturnCode(HOOK_RETURN);
					response.setHookReturnMessage(FASTIVR_BACKEND_ERR);
					return response;
				}

				if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
						&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
						&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()
						&& ((currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG() != null 
						&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty())
								|| (currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null
								&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty() 
								&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG() !=  null
								&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().isEmpty()
								))) {

					int segmentSize = 0;
					
					if(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG() != null && !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()) {
						
						segmentSize = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().size();
					} else if(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG() !=  null && !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().isEmpty()) {
						
						segmentSize = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().size();
					}

					if (segmentSize == 3) {

						List<IVRParameter> parameterList = ivrLfacsServiceHelper
								.getCablePair(currentAssignmentResponseDto, 2);

						response.setParameters(parameterList);

						response.setHookReturnCode(HOOK_RETURN_1);
						response.setHookReturnMessage("f3 exists");
						
						IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);

						if (cnfSession != null) {
							
							cnfSession.setCable(parameterList.get(0).getData());
							cnfSession.setPair(parameterList.get(1).getData());
							ivrCnfCacheService.updateSession(cnfSession);
						} else {
							
							response.setHookReturnMessage(HOOK_RETURN);
							response.setHookReturnMessage(CNF_INVALID_SESSION_ID);
							
							return response;
						}
					} else {

						response.setHookReturnCode(HOOK_RETURN_0);
						response.setHookReturnMessage("No f3");
					}
				}
			} catch (Exception ex) {

				// TODO Need to check the HookReturnCode
				LOGGER.error("Exception stack trace: ", ex);
				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(FASTIVR_BACKEND_ERR);
			}
		}

		return response;
	}

	@Override
	public IVRWebHookResponseDto processFND170(String sessionId, String currentState, String userDTMFInput) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		if (userDTMFInput.equalsIgnoreCase("1")) {

			response.setHookReturnCode(HOOK_RETURN_0);
			response.setHookReturnMessage("F3 Cut");
			
			IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);

			if (cnfSession != null) {

				cnfSession.setSegmentRead("F3");
				ivrCnfCacheService.updateSession(cnfSession);
			} else {

				response.setHookReturnMessage(HOOK_RETURN);
				response.setHookReturnMessage(CNF_INVALID_SESSION_ID);
			}
		}

		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFND215(String sessionId, String currentState, String nextState, String userDTMFInput) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		
		response.setSessionId(sessionId);
		
		response.setCurrentState(nextState);
		
		response.setHookReturnCode(HOOK_RETURN_1);
		
		String defectiveCode = null;
		
		switch(currentState) {
		
		case STATE_FN0190:
		
			defectiveCode = firsetSetOFDefectiveCodes.get(userDTMFInput);
			break;
			
		case STATE_FN0200:
			
			defectiveCode = firsetSetOFDefectiveCodes.get(userDTMFInput);
			break;	
			
		case STATE_FN0210:
			
			defectiveCode = firsetSetOFDefectiveCodes.get(userDTMFInput);
			break;
			
		case STATE_FN0211:
			
			defectiveCode = secondSetOFDefectiveCodes.get(userDTMFInput);
			break;
			
		case STATE_FN0212:
			
			defectiveCode = secondSetOFDefectiveCodes.get(userDTMFInput);
			break;
			
		case STATE_FN0213:
			
			defectiveCode = secondSetOFDefectiveCodes.get(userDTMFInput);
			break;
			
		default:
			response.setHookReturnCode(HOOK_RETURN);
			response.setHookReturnMessage("Invalid DefetiveCode");
			return response;
		}
		
		response.setParameters(ivrLfacsServiceHelper.addParamterData(defectiveCode));
		
		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFND216(String sessionId, String currentState) throws JsonMappingException, JsonProcessingException {
	
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		
		IVRCnfEntity cnfUserSession = ivrCnfCacheService.getBySessionId(sessionId);
		
		if(cnfUserSession == null) {
			
			response.setHookReturnCode(HOOK_RETURN);
			response.setHookReturnMessage(CNF_INVALID_SESSION_ID);
			return response;
		}
		
		String loopQualNIIResultJson = cnfUserSession.getGetLoopQualNIIResponse();
		
		NIIServiceResponse responseObject = objectMapper.readValue(loopQualNIIResultJson,
				NIIServiceResponse.class);
		
		if(responseObject != null && responseObject.getNetworkInfraStructure() != null && !responseObject.getNetworkInfraStructure().isEmpty()) {
			
			response.setHookReturnCode(HOOK_RETURN_0);
			response.setHookReturnMessage("NIIFlag Status True");
			if(responseObject.getNetworkInfraStructure().size() > 1) {
				
				boolean voiceActivationFlag = responseObject.getNetworkInfraStructure().get(0).isVoiceActivationFlag();
				for(NetworkInfrastructure networkInfrastructure : responseObject.getNetworkInfraStructure()) {
					
					if(voiceActivationFlag != networkInfrastructure.isVoiceActivationFlag()) {
						
						response.setHookReturnCode(HOOK_RETURN_1);
						response.setHookReturnMessage("NIIFlag Status False");
						break;
					}
				}
			}
		} else {
			
			response.setHookReturnCode(HOOK_RETURN);
			response.setHookReturnMessage("NetworkInfraStructure is empty");
		}
		
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFND700(String sessionId, String nextState, String userDTMFInput) {
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		
		response.setSessionId(sessionId);
		
		response.setCurrentState(nextState);
		
		response.setHookReturnCode(HOOK_RETURN_3);
		response.setHookReturnMessage(SUCCESS);
		
		if (userDTMFInput.contains("*")) {

			String convertedInput = ivrCnfHelper.convertInputCodesToAlphabets(userDTMFInput);
			
			if(convertedInput == null) {
				
				response.setHookReturnCode(HOOK_RETURN_0);
				response.setHookReturnMessage("Input Codes are entered incorrectly");
				return response;
			}

			String firstValues = userDTMFInput.substring(0, userDTMFInput.indexOf("*"));
					
			String lastValues = userDTMFInput.substring(userDTMFInput.lastIndexOf("*") + 1);
			
			convertedInput = firstValues + convertedInput + lastValues;

			List<IVRParameter> ivrParameterList = ivrLfacsServiceHelper.addParamterData(convertedInput);
			response.setParameters(ivrParameterList);
		} else if(StringUtils.isNotBlank(userDTMFInput) && !"#".equals(userDTMFInput)) {
			
			List<IVRParameter> ivrParameterList = ivrLfacsServiceHelper
					.addParamterData(userDTMFInput);
			response.setParameters(ivrParameterList);
		} else if("#".equals(userDTMFInput)) {
			
			IVRCnfEntity cnfUserSession = ivrCnfCacheService.getBySessionId(sessionId);
			
			List<IVRParameter> ivrParameterList = ivrLfacsServiceHelper
					.addParamterData(cnfUserSession.getCable());
			response.setParameters(ivrParameterList);
		}
		
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFND740(String sessionId, String nextState, List<String> userInputDTMFList) {
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		
		response.setSessionId(sessionId);
		
		response.setCurrentState(nextState);
		
		IVRUserSession userSession = cacheService.getBySessionId(sessionId);
		
		if (userSession != null) {

			IVRCnfEntity cnfUserSession = ivrCnfCacheService.getBySessionId(sessionId);

			if (cnfUserSession != null) {

				if (userInputDTMFList != null && userInputDTMFList.size() == 2) {

					String inputCablePair = userInputDTMFList.get(0) + userInputDTMFList.get(1);

					String oldCablePair = cnfUserSession.getCable() + cnfUserSession.getPair();

					if (inputCablePair.equalsIgnoreCase(oldCablePair)) {

						response.setHookReturnCode(HOOK_RETURN_0);
						response.setHookReturnMessage("Tech is cutting to same cable/pair");
						return response;
					} else if (INQUIRY_BY_SERVICE_ORDER.equalsIgnoreCase(cnfUserSession.getCnfInqType())) {

						response.setHookReturnCode(HOOK_RETURN_1);
						response.setHookReturnMessage("Service Order Cut");
					} else if (INQUIRY_BY_TROUBLE_TICKET.equalsIgnoreCase(cnfUserSession.getCnfInqType())) {

						response.setHookReturnCode(HOOK_RETURN_2);
						response.setHookReturnMessage("Maintenance Cut");
					}
				}
			} else {

				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(CNF_INVALID_SESSION_ID);
			}
		} else {
			
			response.setHookReturnCode(HOOK_RETURN);
			response.setHookReturnMessage(INVALID_SESSION_ID);
		}
		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFND741(String sessionId, String currentState, List<String> userInputDTMFList) {

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

			IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);

			if (cnfSession == null) {

				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(CNF_INVALID_SESSION_ID);
				return response;
			}

			String cable = userInputDTMFList.get(0);
			String pair = userInputDTMFList.get(1);
			ChangeLoopAssignmentRequestDto request = ivrCnfHelper.buildChangeLoopAssignmentRequest(tn, primaryNpa,
					primaryNxx, userSession.getEmpID(), sessionId, cnfSession, userSession, cable , pair);

			String jsonRequest = objectMapper.writeValueAsString(request);

			String changeLoopAssignmentResultJson = ivrHttpClient.httpPostCall(jsonRequest, changeLoopAssignmentUrl,
					sessionId, CHANGE_LOOP_ASSIGNMENT_REQUEST);

			String cleanJsonStr = ivrLfacsServiceHelper.cleanResponseString(changeLoopAssignmentResultJson);

			cnfSession.setGetInstReplPrsResponse(cleanJsonStr);

			ivrCnfCacheService.updateSession(cnfSession);

			ChangeLoopAssignmentResponseDto responseObject = objectMapper.readValue(cleanJsonStr,
					ChangeLoopAssignmentResponseDto.class);		
			
			// Call CNF Pager text method.
			ChangeLoopAssignmentResponseDto changeLoopAssignmentResponseDto = ivrCnfPagerTextFormation.getPageChangeLoopAssignment(request, response, 
					userSession, cnfSession, sessionId, tn, primaryNpa, primaryNxx);

			if (responseObject != null && IVRLfacsConstants.S.equals(responseObject.getMessageStatus().getErrorStatus())) {
				
				response.setParameters(ivrLfacsServiceHelper.addParamterData(cnfSession.getCable(), cable));	
				cnfSession.setCable(cable);
				cnfSession.setPair(pair);
				ivrCnfCacheService.updateSession(cnfSession);
				response =  ivrCnfHelper.processResponse(response, cnfSession.getSegmentRead(), userSession);
				return response;

			} else {

				hookReturnCode = HOOK_RETURN_1;
				hookReturnMessage = GPDOWN_ERR_MSG;
			}			
		} catch (Exception e) {
			e.printStackTrace();
			hookReturnCode = HOOK_RETURN_1;
			hookReturnMessage = GPDOWN_ERR_MSG;
			LOGGER.error("CNF FND741 Exception stack trace: ", e);			
		}
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFND742(String sessionId, String currentState, List<String> userInputDTMFList) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		String hookReturnCode = "";

		String hookReturnMessage = "";

		try {
			IVRUserSession userSession = cacheService.getBySessionId(sessionId);

			if (userSession == null) {

				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(INVALID_SESSION_ID);

				return null;
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

			IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);

			if (cnfSession == null) {

				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(CNF_INVALID_SESSION_ID);
				return response;
			}

			String cable = userInputDTMFList.get(0);
			String pair = userInputDTMFList.get(1);
			MainChangeTicketLoopRequest request = ivrCnfHelper.buildMainChangeTicketLoopRequest(tn, primaryNpa,
					primaryNxx, userSession.getEmpID(), cnfSession, userSession, cable, pair);

			String jsonRequest = objectMapper.writeValueAsString(request);

			String mainChangeLoopResultJson = ivrHttpClient.httpPostCall(jsonRequest, mainChangeLoopAssignUrl,
					sessionId, MAINTENANCE_CHANGE_LOOP_REQUEST);

			String cleanJsonStr = ivrLfacsServiceHelper.cleanResponseString(mainChangeLoopResultJson);

			cnfSession.setGetMntReplPrsResponse(cleanJsonStr);
			
			ivrCnfCacheService.updateSession(cnfSession);

			MainChangeTicketLoopResponse responseObject = objectMapper.readValue(cleanJsonStr,
					MainChangeTicketLoopResponse.class);
			
			// Call CNF Pager text method.
			ChangeLoopAssignmentRequestDto changeLoopAssignmentRequestDto = ivrCnfHelper
					.buildChangeLoopAssignmentRequest(tn, primaryNpa, primaryNxx, userSession.getEmpID(), sessionId,
							cnfSession, userSession, userInputDTMFList.get(0), userInputDTMFList.get(1));

			ChangeLoopAssignmentResponseDto changeLoopAssignmentResponseDto = ivrCnfPagerTextFormation
					.getPageChangeLoopAssignment(changeLoopAssignmentRequestDto, response, userSession, cnfSession,
							sessionId, tn, primaryNpa, primaryNxx);			

			if (responseObject != null && IVRLfacsConstants.S.equals(responseObject.getMessageStatus().getErrorStatus())) {
				
				response.setParameters(ivrLfacsServiceHelper.addParamterData(cnfSession.getCable(), cable));	
				cnfSession.setCable(cable);
				cnfSession.setPair(pair);
				ivrCnfCacheService.updateSession(cnfSession);
				return ivrCnfHelper.processResponse(response, cnfSession.getSegmentRead(), userSession);
				 
			} else {

				hookReturnCode = HOOK_RETURN_1;
				hookReturnMessage = GPDOWN_ERR_MSG;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			hookReturnCode = HOOK_RETURN_1;
			hookReturnMessage = GPDOWN_ERR_MSG;
		}
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFND746(String sessionId, String nextState, List<String> userInputDTMFList)
			throws JsonMappingException, JsonProcessingException {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		String hookReturnCode = "";
		String hookReturnMessage = "";

		try {
			IVRUserSession userSession = cacheService.getBySessionId(sessionId);

			CurrentAssignmentResponseDto currentAssignmentResponseDto = null;

			response.setSessionId(sessionId);

			response.setCurrentState(nextState);
			if (userSession != null) {

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

				if (userSession.isCanBePagedMobile()) {

					IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);
					
					int segNo = 0;
					
					if("F1".equalsIgnoreCase(cnfSession.getSegmentRead())) {
						
						segNo = 0;
					} else if("F2".equalsIgnoreCase(cnfSession.getSegmentRead())) {
						
						segNo = 1;
					} else if("F3".equalsIgnoreCase(cnfSession.getSegmentRead())) {
						
						segNo = 2;
					}
					
					if (ivrLfacsServiceHelper.isSeviceOrder(currentAssignmentResponseDto)) {

						processRetrieveLoopAssignmentInquiry(sessionId, userSession, cnfSession, tn, primaryNpa, primaryNxx, segNo);
					} else {

						processRetrieveMaintenanceTicketChangeInquiry(sessionId, userSession, cnfSession, tn, primaryNpa, primaryNxx, segNo);
					}

					hookReturnCode = HOOK_RETURN_2;
					hookReturnMessage = " Alpha pager";

				} else {

					hookReturnCode = HOOK_RETURN_3;
					hookReturnMessage = " No Alpha pager";
				}
				cacheService.updateSession(userSession);
			} else {

				hookReturnCode = GPDOWN_ERR_MSG_CODE;
				hookReturnMessage = GPDOWN_ERR_MSG;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;
	}
	
	private void processRetrieveLoopAssignmentInquiry(String sessionId, IVRUserSession userSession, IVRCnfEntity cnfSession, String tn, String primaryNpa, String primaryNxx, int segNo)
			throws JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException,
			JsonMappingException {
		
			RetrieveLoopAssignmentRequest request = ivrCnfHelper.buildRetriveLoopAssignInqRequest(null, tn, primaryNpa, primaryNxx, cnfSession);

			String jsonRequest = objectMapper.writeValueAsString(request);
			String responseString = ivrLfacsServiceHelper.callSparePairLoopAssignInquiryLfacs(jsonRequest, userSession);

			RetrieveLoopAssignmentResponse responseObject = objectMapper.readValue(responseString, RetrieveLoopAssignmentResponse.class);
			
			if (responseObject != null) {
				
				userSession.setRtrvLoopAssgMsgName(responseString);
			}
			
			if(userSession.isCanBePagedMobile()) {
				sprPrPageBuilder.pageForRetrieveLoopAssignment(responseObject, 
						sessionId, segNo, NET_PHONE_DEVICE, FormatUtilities.FormatTelephoneNNNXXXX(tn));
			} else {
				sprPrPageBuilder
				.pageForRetrieveLoopAssignment(responseObject, 
						sessionId, segNo, NET_MAIL_DEVICE, FormatUtilities.FormatTelephoneNNNXXXX(tn));
			}
	}
	
	private void processRetrieveMaintenanceTicketChangeInquiry(String sessionId, IVRUserSession userSession, IVRCnfEntity cnfSession, String tn, String primaryNpa, String primaryNxx, int segNo)
			throws JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException,
			JsonMappingException {
		
		RetrieveMaintenanceChangeTicketRequest request = ivrCnfHelper.buildRetriveMainInqRequest(tn, primaryNpa, primaryNxx, cnfSession);

		String jsonRequest = objectMapper.writeValueAsString(request);
		String responseString = ivrLfacsServiceHelper.callSparePairInquiryLfacs(jsonRequest,userSession);

		RetrieveMaintenanceChangeTicketResponse responseObject = objectMapper.readValue(responseString, RetrieveMaintenanceChangeTicketResponse.class);
		
		if (responseObject != null) {
			
			userSession.setRtrvMaintChngeMsgName(responseString);
		}
		
		if(userSession.isCanBePagedMobile()) {
			sprPrPageBuilder.pageForRetrieveMaintenanceChangeTicket(responseObject, 
					sessionId, 0, NET_PHONE_DEVICE, FormatUtilities.FormatTelephoneNNNXXXX(tn));
		} else {
			sprPrPageBuilder
			.pageForRetrieveMaintenanceChangeTicket(responseObject, 
					sessionId, 0, NET_MAIL_DEVICE, FormatUtilities.FormatTelephoneNNNXXXX(tn));
		}
	}
}
