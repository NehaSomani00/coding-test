/**
 * 
 */
package com.lumen.fastivr.IVRLFACS;

import static com.lumen.fastivr.IVRUtils.IVRConstants.ADDITIONAL_LINES_CANNOT_PAGE_MSG;
import static com.lumen.fastivr.IVRUtils.IVRConstants.ADDITIONAL_LINES_PAGE_MSG;
import static com.lumen.fastivr.IVRUtils.IVRConstants.ADDITIONAL_LINES_REQUEST;
import static com.lumen.fastivr.IVRUtils.IVRConstants.CANNOT_FETCH_CURR_ASSG_API_LOSDB_NULL;
import static com.lumen.fastivr.IVRUtils.IVRConstants.CENTRAL_OFFICE_EQUIPMENT;
import static com.lumen.fastivr.IVRUtils.IVRConstants.DEFECTIVE_PAIRS;
import static com.lumen.fastivr.IVRUtils.IVRConstants.DTMF_INPUT_5;
import static com.lumen.fastivr.IVRUtils.IVRConstants.FASTIVR_BACKEND_ERR;
import static com.lumen.fastivr.IVRUtils.IVRConstants.FID625_MSG_1;
import static com.lumen.fastivr.IVRUtils.IVRConstants.FID625_MSG_2;
import static com.lumen.fastivr.IVRUtils.IVRConstants.GPDOWN_ERR_MSG;
import static com.lumen.fastivr.IVRUtils.IVRConstants.GPDOWN_ERR_MSG_CODE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INQUIRY_BY_CABLE_PAIR;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INQUIRY_BY_TN;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INVALID_SESSION_ID;
import static com.lumen.fastivr.IVRUtils.IVRConstants.MULTIPLE_APPEARANCE_REQUEST;
import static com.lumen.fastivr.IVRUtils.IVRConstants.NETPAGE_SUBJECT_COE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.NETPAGE_SUBJECT_CURRENT_ASSIGNMENT;
import static com.lumen.fastivr.IVRUtils.IVRConstants.NETPAGE_SUBJECT_MULTIPLE_APPEARANCES;
import static com.lumen.fastivr.IVRUtils.IVRConstants.NET_MAIL_DEVICE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.NET_PHONE_DEVICE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.NO_ALPHA_PAGER;
import static com.lumen.fastivr.IVRUtils.IVRConstants.SERVICE_ADDRESS_FOUND;
import static com.lumen.fastivr.IVRUtils.IVRConstants.SERVICE_ADDRESS_NOT_FOUND;
import static com.lumen.fastivr.IVRUtils.IVRConstants.SUCCESS;
import static com.lumen.fastivr.IVRUtils.IVRConstants.WRAPPER_API_DEFAULT_EMPLOYEE_ID;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_0;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_1;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_2;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_3;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_4;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_5;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_500;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_6;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_7;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_8;

import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRBusinessException.BusinessException;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.CurrentAssignmentRequestTnDto;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.SEG;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.CandidatePairInfo;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketRequest;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketResponse;
import com.lumen.fastivr.IVRDto.additionalLines.AdditionalLinesReportRequestDto;
import com.lumen.fastivr.IVRDto.additionalLines.AdditionalLinesReportResponseDto;
import com.lumen.fastivr.IVRDto.coe.CentralOfficeEquipmentRequestDto;
import com.lumen.fastivr.IVRDto.coe.CentralOfficeEquipmentResponseDto;
import com.lumen.fastivr.IVRDto.common.IVRHttpResponseDto;
import com.lumen.fastivr.IVRDto.defectivepairs.DefectivePairsRequestDto;
import com.lumen.fastivr.IVRDto.defectivepairs.DefectivePairsResponseDto;
import com.lumen.fastivr.IVRDto.multipleappearance.MultipleAppearanceRequestDto;
import com.lumen.fastivr.IVRDto.multipleappearance.MultipleAppearanceResponseDto;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.LoopAssignCandidatePairInfo;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentRequest;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentResponse;
import com.lumen.fastivr.IVRRepository.FastIvrMnetRepository;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.AdditionalLinesReportUtils;
import com.lumen.fastivr.IVRUtils.CurrentAssignmentUtils;
import com.lumen.fastivr.IVRUtils.FormatUtilities;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.IVRUtils.IVRLfacsConstants;
import com.lumen.fastivr.httpclient.IVRHttpClient;

@Service
public class IVRLfacsServiceImpl implements IVRLfacsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IVRLfacsServiceImpl.class);

	@Autowired
	private IVRCacheService cacheService;
	
	@Autowired
	private IVRLfacsServiceHelper ivrLfacsServiceHelper;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private LfacsValidation tnValidation;

	@Autowired
	private IVRLfacsPagerTextFormation ivrLfacsPagerText;

	@Autowired
	private IVRHttpClient ivrHttpClient;

	@Autowired
	private AdditionalLinesReportUtils additionalLinesUtil;
	
	@Autowired
	private AddlLinesPageBuilder addlLinesPageBuilder;
	
	@Autowired
	private SparePairPageBuilder sprPrPageBuilder;
	
	@Autowired
	private FastIvrMnetRepository fastIvrMnetRepository;

	@Value("${lfacs.multiple.appearance.url}")
	private String multipleAppearanceUrl;

	@Value("${lfacs.additional.lines.url}")
	private String additionalLinesUrl;

	@Value("${lfacs.central.office.equipment.url}")
	private String centralOfcEquipmentUrl;

	@Value("${lfacs.defective.pairs.url}")
	private String defectivePairsUrl;

	//////////////////////////////////////////////////////////////////////
	// Function:
	// FID011(UINT)
	// Description:
	// Sets InqType to Inquiry by TN. Makes call to to
	// GetInterfaceDetails to validate the TN, set the
	// TN and obtain the WC Name by making call to
	// GetInterfaceDetials on CutCkid object (mCutCkid).
	// Precondition:
	// Must have a 7 digit number in user Input
	// Warnings:
	// None
	// Return:
	// HRCODE - Successful validate return 1, otherwise 0
	// Current State:
	// FID011
	// Next State:
	// FIE011(0), FI0015(1), FI0010(-1)
	//////////////////////////////////////////////////////////////////////
	@Override
	public IVRWebHookResponseDto processFID011(String sessionId, String currentState, String userDTMFInput) {
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
	public IVRWebHookResponseDto processFID025Code(String sessionId, String nextState, String userDTMFInput)
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

								if(ivrLfacsServiceHelper.getSegmentList(currentAssignmentResponse) > 3) {
									
									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_9;
									hookReturnMessage = "More than three segments";
								} else {
									
									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_8;
									
									if(StringUtils.isNotBlank(ivrUserSession.getFacsInqType())) {
										if(IVRConstants.INQUIRY_BY_TN.equalsIgnoreCase(ivrUserSession.getFacsInqType())) {
											hookReturnMessage = IVRLfacsConstants.ENQUIRY_TN_MSG;
										}
										else {
											hookReturnMessage = IVRLfacsConstants.ENQUIRY_CP_MSG;
										}
									}
								}
							} else {

								if (currentAssignmentResponse.getMessageStatus().getHostErrorList().get(0)
										.getErrorList().get(0).getErrorMessage().contains("L400-192")
										|| currentAssignmentResponse.getMessageStatus().getHostErrorList().get(0)
												.getErrorList().get(0).getErrorMessage().contains("L410-292")) {
									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_5;
									hookReturnMessage = IVRLfacsConstants.INVALID_TN_ERR;

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
								} else if (currentAssignmentResponse.getMessageStatus().getHostErrorList().get(0)
										.getErrorList().get(0).getErrorMessage().contains("L500-235")) {

									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_6;
									hookReturnMessage = IVRLfacsConstants.NON_STANDARD_UDC_FORMAT_ERR;

								} else if (currentAssignmentResponse.getMessageStatus().getHostErrorList().get(0)
										.getErrorList().get(0).getErrorMessage().contains("L400-160: CABLE PAIR")) {

									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_0;
									hookReturnMessage = IVRLfacsConstants.INVALID_CABLE_ERR;

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

									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_7;
									hookReturnMessage = IVRLfacsConstants.INVALID_CABLE;

									List<IVRParameter> parameterList = new ArrayList<IVRParameter>();
									IVRParameter parameter = new IVRParameter();
									parameter.setData(ivrUserSession.getCable());
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
	public IVRWebHookResponseDto processFID035Code(String sessionId, String currentState) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = cacheService.getBySessionId(sessionId);

		if (userSession != null) {

			try {

				CurrentAssignmentResponseDto currentAssignmentResponseDto = objectMapper
						.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);

				String pageText = ivrLfacsPagerText.getPageCurrentAssignment(response, userSession,
						currentAssignmentResponseDto);

				if (userSession.isCanBePagedMobile()) {

					LOGGER.info("FID035 Status getting success", sessionId);

					ivrLfacsServiceHelper.sendTestResultToTech(NETPAGE_SUBJECT_CURRENT_ASSIGNMENT, pageText,
							NET_PHONE_DEVICE, userSession);

					response.setHookReturnCode(HOOK_RETURN_3);
					response.setHookReturnMessage(SUCCESS);

				} else {

					LOGGER.info("CanBePagedEmail are False", sessionId);
					ivrLfacsServiceHelper.sendTestResultToTech(NETPAGE_SUBJECT_CURRENT_ASSIGNMENT, pageText,
							NET_MAIL_DEVICE, userSession);
					response.setHookReturnCode(HOOK_RETURN_2);
					response.setHookReturnMessage(NO_ALPHA_PAGER);
				}
			} catch (Exception ex) {

				// TODO Need to check the HookReturnCode
				LOGGER.error("Exception stack trace: ", ex);
				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(FASTIVR_BACKEND_ERR);
			}
		} else {

			LOGGER.info("SessionId is not Found in cache/DB", sessionId);
			response.setHookReturnCode(HOOK_RETURN_1);
			response.setHookReturnMessage(FASTIVR_BACKEND_ERR);
		}

		return response;
	}

	@Override
	public IVRWebHookResponseDto processFID400Code(String sessionId, String currentState, String userDTMFInput) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		try {

			IVRUserSession userSession = cacheService.getBySessionId(sessionId);

			CurrentAssignmentResponseDto currentAssignmentResponseDto = null;

			TNInfoResponse tnInfoResponse = null;

			if (userSession != null && userSession.getCurrentAssignmentResponse() != null) {

				currentAssignmentResponseDto = new ObjectMapper().readValue(userSession.getCurrentAssignmentResponse(),
						CurrentAssignmentResponseDto.class);

				tnInfoResponse = objectMapper.readValue(userSession.getLosDbResponse(), TNInfoResponse.class);
				
				if(DTMF_INPUT_5.equalsIgnoreCase(userDTMFInput)) {
					
					userSession.setSegmentRead(null);
					cacheService.updateSession(userSession);
				}
			} else {

				response.setHookReturnCode(HOOK_RETURN);
				response.setHookReturnMessage(FASTIVR_BACKEND_ERR);
				return response;
			}
			if (ivrLfacsServiceHelper.isCircuitWithNoTN(currentAssignmentResponseDto, response)) {

				response.setHookReturnCode(HOOK_RETURN_2);
				response.setHookReturnMessage("CircuitWith no TN is eligible");
			} else if (ivrLfacsServiceHelper.isUdcCircuit(currentAssignmentResponseDto, response)) {

				response.setHookReturnCode(HOOK_RETURN_4);
				response.setHookReturnMessage("UDC Circuit is eligible");

			} else if (ivrLfacsServiceHelper.isSpecialCircuit(currentAssignmentResponseDto, response)) {

				response.setHookReturnCode(HOOK_RETURN_3);
				response.setHookReturnMessage("Special Circuit is eligible");
			} else {

				response.setHookReturnCode(HOOK_RETURN_1);
				response.setHookReturnMessage("Circuit With a TN");
				if (tnInfoResponse != null && StringUtils.isNotBlank(tnInfoResponse.getTn())) {

					ivrLfacsServiceHelper.getStatusPair(currentAssignmentResponseDto, response, tnInfoResponse, false);
				}
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
	public IVRWebHookResponseDto processFID420Code(String sessionId, String currentState) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		try {

			IVRUserSession userSession = cacheService.getBySessionId(sessionId);

			CurrentAssignmentResponseDto currentAssignmentResponseDto = null;

			if (userSession != null && userSession.getCurrentAssignmentResponse() != null) {

				currentAssignmentResponseDto = objectMapper.readValue(userSession.getCurrentAssignmentResponse(),
						CurrentAssignmentResponseDto.class);

				response = ivrLfacsServiceHelper.getColourCode(response, currentAssignmentResponseDto, userSession);

				cacheService.updateSession(userSession);
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
	public IVRWebHookResponseDto processFID429Code(String sessionId, String currentState) {

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
					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()) {

				int segmentSize = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().size();

				if ("F1".equalsIgnoreCase(userSession.getSegmentRead())
						|| "F2".equalsIgnoreCase(userSession.getSegmentRead())) {
					
					List<IVRParameter> parameterList = null;
					
					List<SEG> segList = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG();
					
					if("F1".equalsIgnoreCase(userSession.getSegmentRead())) {
						
						SEG segment = segList.get(1);
						
						parameterList = ivrLfacsServiceHelper.addParamterData(segment.getCA(), segment.getPR());
						response.setHookReturnMessage("F2");
					} else if("F2".equalsIgnoreCase(userSession.getSegmentRead())) {
						
						SEG segment = segList.get(2);
						
						parameterList = ivrLfacsServiceHelper.addParamterData(segment.getCA(), segment.getPR());
						
						response.setHookReturnMessage("F3");
					}
					
					response.setParameters(parameterList);
					response.setHookReturnCode(HOOK_RETURN_1);
				} else if (segmentSize == 1 && "ALL".equalsIgnoreCase(userSession.getSegmentRead())) {

					response.setHookReturnCode(HOOK_RETURN_2);
					response.setHookReturnMessage(IVRConstants.FINISHED_F1);
				} else if (segmentSize == 2 && "ALL".equalsIgnoreCase(userSession.getSegmentRead())) {

					response.setHookReturnCode(HOOK_RETURN_3);
					response.setHookReturnMessage(IVRConstants.FINISHED_F2);
				} else if (segmentSize == 3 && "ALL".equalsIgnoreCase(userSession.getSegmentRead())) {

					response.setHookReturnCode(HOOK_RETURN_4);
					response.setHookReturnMessage(IVRConstants.FINISHED_F3);
				} else {

					response.setHookReturnCode(HOOK_RETURN);
					response.setHookReturnMessage(IVRConstants.WRONG_SEGMENT);
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
	public IVRWebHookResponseDto processFID500Code(String sessionId, String nextState, List<String> userInputDTMFList)
			throws JsonMappingException, JsonProcessingException {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = HOOK_RETURN_6;
		String hookReturnMessage = "Initial current assignment results are OK";

		IVRUserSession userSession = cacheService.getBySessionId(sessionId);

		if (userSession != null) {

			String jsonString = userSession.getCurrentAssignmentResponse();

			CurrentAssignmentResponseDto dto = objectMapper.readValue(jsonString, CurrentAssignmentResponseDto.class);

			if (INQUIRY_BY_TN.equalsIgnoreCase(userSession.getFacsInqType())) {

				try {

					if (dto.getReturnDataSet().getLoop() != null
							&& dto.getReturnDataSet().getLoop().get(0).getSEG().size() > 3) {
						hookReturnCode = HOOK_RETURN_1;
						hookReturnMessage = "More than three segments";
					} else if (dto.getReturnDataSet().getLoop() != null
							&& dto.getReturnDataSet().getLoop().get(0).getSEG().size() == 1
							&& userInputDTMFList.contains("2")) {
						List<IVRParameter> parameterList = new ArrayList<IVRParameter>();
						IVRParameter ivrParameter = new IVRParameter();
						ivrParameter.setData(userSession.getInquiredTn());
						parameterList.add(ivrParameter);
						response.setParameters(parameterList);
						hookReturnCode = HOOK_RETURN_2;
						hookReturnMessage = "F2 requested but not F2(2)";
					} else if (dto.getReturnDataSet().getLoop() != null
							&& dto.getReturnDataSet().getLoop().get(0).getSEG().size() < 3
							&& userInputDTMFList.contains("3")) {
						
						List<IVRParameter> parameterList = new ArrayList<IVRParameter>();
						IVRParameter ivrParameter = new IVRParameter();
						ivrParameter.setData(userSession.getInquiredTn());
						parameterList.add(ivrParameter);
						response.setParameters(parameterList);
						hookReturnCode = HOOK_RETURN_3;
						hookReturnMessage = "F3 requested but not F3(3)";
					} else if(ivrLfacsServiceHelper.isLineStationTransfer(dto)) {
						
						List<IVRParameter> parameterList = new ArrayList<IVRParameter>();
						IVRParameter ivrParameter = new IVRParameter();
						ivrParameter.setData(userSession.getInquiredTn());
						parameterList.add(ivrParameter);
						response.setParameters(parameterList);
						hookReturnCode = HOOK_RETURN_4;
						hookReturnMessage = "TN is part of a LST";
					} else if(ivrLfacsServiceHelper.isSpecialCircuit(dto, response)) {
						
						response.setParameters(null);
						hookReturnCode = HOOK_RETURN_5;
						hookReturnMessage = "Special Circuit";
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(INQUIRY_BY_CABLE_PAIR.equalsIgnoreCase(userSession.getFacsInqType())) {
				
				hookReturnCode = HOOK_RETURN_7;
				hookReturnMessage = "Initial current assignment results are OK";
				
				
				if (dto.getReturnDataSet().getLoop() != null && dto.getReturnDataSet().getLoop().get(0).getSEG().size() > 3) {
					hookReturnCode = HOOK_RETURN_1;
					hookReturnMessage = "More than three segments";
				}
				else if(ivrLfacsServiceHelper.isLineStationTransfer(dto)) {
						
						List<IVRParameter> parameterList = new ArrayList<IVRParameter>();
						IVRParameter ivrParameter = new IVRParameter();
						ivrParameter.setData(userSession.getInquiredTn());
						parameterList.add(ivrParameter);
						response.setParameters(parameterList);
						hookReturnCode = HOOK_RETURN_4;
						hookReturnMessage = "TN is part of a LST";
				}
				else if(ivrLfacsServiceHelper.isSpecialCircuit(dto, response)) {
					
					response.setParameters(null);
					hookReturnCode = HOOK_RETURN_5;
					hookReturnMessage = "Special Circuit";
				}


			}
		}
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFID045Code(String sessionId, String nextState) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = "";
		String hookReturnMessage = "";

		IVRUserSession userSession = cacheService.getBySessionId(sessionId);

		if (userSession != null) {

			if (userSession.isCanBePagedMobile()) {

				hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_1;
				hookReturnMessage = IVRLfacsConstants.ALPHA_PAGER;

			} else {

				hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_0;
				hookReturnMessage = IVRLfacsConstants.NO_ALPHA_PAGER;
			}
		} else {
			hookReturnMessage = IVRConstants.INVALID_SESSION_ID;
		}

		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info(hookReturnMessage, sessionId);
		return response;

	}
	
	/**
	 * Spare pair flow by TN
	 */
	@Override
	public IVRWebHookResponseDto processFID055Code(String sessionId, String nextState, String userInputStr)
			throws JsonMappingException, JsonProcessingException {
		int currSegNo = Integer.parseInt(userInputStr);
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = "";
		String hookReturnMessage = "";
		String pageResult = "";
		int segSize = 1;

		IVRUserSession userSession = cacheService.getBySessionId(sessionId);

		try {
			if (userSession != null) {
				
				CurrentAssignmentResponseDto currentAssignresponseObject = objectMapper.readValue(userSession.getCurrentAssignmentResponse(), 
						CurrentAssignmentResponseDto.class);
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
				
					if (currSegNo == 1) {
						//send page for F1
						hookReturnCode = HOOK_RETURN_2;
						hookReturnMessage = "F1 Alpha Pager";
					} else if (currSegNo== 2) {
						//send page for F2
						hookReturnCode = HOOK_RETURN_3;
						hookReturnMessage = "F2 Alpha Pager";
					} else if (currSegNo== 3) {
						//send page for F3
						hookReturnCode = HOOK_RETURN_4;
						hookReturnMessage = "F3 Alpha Pager";
					} else if (currSegNo== 4) {
						segSize = currentAssignresponseObject.getReturnDataSet().getLoop().get(0).getSEG().size();
						currSegNo = 1;
						hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_5;
						hookReturnMessage = "All Alpha Pager";
					}
					
				if (!userSession.isCanBePagedMobile()) {
						hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_0;
						hookReturnMessage = "No Alpha Pager";
				}

				
				if (userSession.getCurrentAssignmentResponse() != null) {

					if (isServiceOrderExist(currentAssignresponseObject)) {

						// service order is present -> loop assignment
						processRetrieveLoopAssignmentInquiry(sessionId, currSegNo, segSize, userSession,
								currentAssignresponseObject, tn, primaryNpa, primaryNxx);
					} else {
						// no service order -> maintenance ticket change
						processRetrieveMaintenanceTicketChangeInquiry(sessionId, currSegNo, segSize, userSession,
								currentAssignresponseObject, tn, primaryNpa, primaryNxx);

					}

				}

				if (!hookReturnCode.equals("1") && !userSession.isCutPageSent()) {
					LOGGER.info("Tech has CUT PAGE");
					userSession.setCutPageSent(true);
					ivrLfacsServiceHelper.issueCutPageSent(userSession);
					pageResult += ": " + ivrLfacsServiceHelper.getCutPageFormat(tn);
					ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_SPARE_PAIR,pageResult, NET_PHONE_DEVICE, userSession);				
				}

				cacheService.updateSession(userSession);

			} else {
				hookReturnMessage = "User Session Not Found";
			}

		} catch (Exception ex) {
			LOGGER.error("Exception stack trace: ", ex);
			response.setHookReturnCode(HOOK_RETURN);
			response.setHookReturnMessage(FASTIVR_BACKEND_ERR);
		}

		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;
	}

	private void processRetrieveMaintenanceTicketChangeInquiry(String sessionId, int currSegNo, int segSize, IVRUserSession userSession,
			CurrentAssignmentResponseDto currentAssignresponseObject, String tn, String primaryNpa, String primaryNxx)
			throws JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException,
			JsonMappingException {
		
		for(int i = 0 ; i < segSize ;i++, currSegNo++) {
		RetrieveMaintenanceChangeTicketRequest request = ivrLfacsServiceHelper
				.buildRetriveMainInqRequest(tn, primaryNpa, primaryNxx, currentAssignresponseObject, userSession, currSegNo);

		String jsonRequest = objectMapper.writeValueAsString(request);
		String responseString = ivrLfacsServiceHelper.callSparePairInquiryLfacs(jsonRequest,userSession);

		RetrieveMaintenanceChangeTicketResponse responseObject = objectMapper.readValue(responseString,RetrieveMaintenanceChangeTicketResponse.class);
		
		if (responseObject != null) {
			userSession.setRtrvMaintChngeMsgName(responseString);
			userSession.setCandPairCounter(-1);

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
	}

	private void processRetrieveLoopAssignmentInquiry(String sessionId, int currSegNo, int segSize, IVRUserSession userSession,
			CurrentAssignmentResponseDto currentAssignresponseObject, String tn, String primaryNpa, String primaryNxx)
			throws JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException,
			JsonMappingException {
		
		for(int i = 0 ; i < segSize ;i++, currSegNo++) {
			RetrieveLoopAssignmentRequest request = ivrLfacsServiceHelper
					.buildRetriveLoopAssignInqRequest(tn, primaryNpa, primaryNxx,currentAssignresponseObject, userSession, currSegNo);

			String jsonRequest = objectMapper.writeValueAsString(request);
			String responseString = ivrLfacsServiceHelper.callSparePairLoopAssignInquiryLfacs(jsonRequest,userSession);

			RetrieveLoopAssignmentResponse responseObject = objectMapper.readValue(responseString,RetrieveLoopAssignmentResponse.class);
			
			if (responseObject != null) {
				userSession.setRtrvLoopAssgMsgName(responseString);
				userSession.setCandPairCounter(-1);

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
	}

	private boolean isServiceOrderExist(CurrentAssignmentResponseDto currentAssignresponseObject) {
		return currentAssignresponseObject.getReturnDataSet().getLoop() != null
				&& currentAssignresponseObject.getReturnDataSet().getLoop().get(0).getSO() != null
				&& currentAssignresponseObject.getReturnDataSet().getLoop().get(0).getSO().get(0).getORD() != null;
	}

	@Override
	public IVRWebHookResponseDto processFID455Code(String sessionId, String currentState) {

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
					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().isEmpty()) {

				String serviceAddress = ivrLfacsServiceHelper.getServiceAddress(currentAssignmentResponseDto);

				List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

				IVRParameter parameter = new IVRParameter();

				parameter.setData(serviceAddress);

				parameterList.add(parameter);

				response.setParameters(parameterList);
				response.setHookReturnCode(HOOK_RETURN_1);
				response.setHookReturnMessage(SUCCESS);
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
	public IVRWebHookResponseDto processFID445Code(String sessionId, String previousState, String currentState, int userDTMFInput) {

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
					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()) {

				List<SEG> seg = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG();

				List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

				IVRParameter parameter = new IVRParameter();

				if (userDTMFInput == 2 || (userDTMFInput == 0 && seg.size() == 2)) {

					response.setHookReturnCode(HOOK_RETURN_2);
					response.setHookReturnMessage(SUCCESS);

					if (IVRConstants.STATE_FI0450.equalsIgnoreCase(previousState)
							|| IVRConstants.STATE_FI0460.equalsIgnoreCase(previousState)) {

						parameter.setData(IVRConstants.STATE_FI0442);
						parameterList.add(parameter);
						response.setParameters(parameterList);
					} else {

						parameter.setData(seg.get(1).getTEA());

						parameterList.add(parameter);

						response.setParameters(parameterList);
					}
				} else if (userDTMFInput == 3 || (userDTMFInput == 0 && seg.size() == 3)) {

					response.setHookReturnCode(HOOK_RETURN_3);
					response.setHookReturnMessage(SUCCESS);

					if (IVRConstants.STATE_FI0450.equalsIgnoreCase(previousState)
							|| IVRConstants.STATE_FI0460.equalsIgnoreCase(previousState)) {

						parameter.setData(IVRConstants.STATE_FI0443);
						parameterList.add(parameter);
						response.setParameters(parameterList);
					} else {

						parameter.setData(seg.get(2).getTEA());

						parameterList.add(parameter);

						response.setParameters(parameterList);
					}
				} else if(userDTMFInput == 1 || (userDTMFInput == 0 && seg.size() == 1)) {

					response.setHookReturnCode(HOOK_RETURN_1);
					response.setHookReturnMessage(SUCCESS);

					if (IVRConstants.STATE_FI0450.equalsIgnoreCase(previousState)
							|| IVRConstants.STATE_FI0460.equalsIgnoreCase(previousState)) {

						parameter.setData(IVRConstants.STATE_FI0441);
						parameterList.add(parameter);
						response.setParameters(parameterList);
					} else {

						parameter.setData(seg.get(0).getTEA());

						parameterList.add(parameter);

						response.setParameters(parameterList);
					}
				}
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
	public IVRWebHookResponseDto processFID020Code(String sessionId, String nextState) {

		IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
		responseDto.setSessionId(sessionId);
		responseDto.setCurrentState(nextState);
		String hookReturnCode = "";
		String hookReturnMessage = "";

		IVRUserSession userSession = cacheService.getBySessionId(sessionId);

		if (userSession != null) {
			try {
				// fetch TN from cache
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

				CurrentAssignmentRequestTnDto requestObject = ivrLfacsServiceHelper.buildCurrentAssignmentInqRequest(tn,
						primaryNpa, primaryNxx, null, userSession);
				String jsonRequest = objectMapper.writeValueAsString(requestObject);
				String responseString = ivrLfacsServiceHelper.callCurrentAssignmentInquiryLfacs(jsonRequest,
						userSession);

				responseString = ivrLfacsServiceHelper.cleanResponseString(responseString);
				LOGGER.info("Session id:" + userSession.getSessionId()
						+ ", Response from LFACS Current Assignment API: " + responseString);
				// Validating it's a good response json string from lfacs
				CurrentAssignmentResponseDto responseObject = objectMapper.readValue(responseString,
						CurrentAssignmentResponseDto.class);
				if (responseObject != null) {
					// String was getting > 4000 bytes, cannot store in VARCHAR2
					// use CURR_ASSG_RESP_CLOB to load and fetch the data
					userSession.setCurrentAssignmentResponse(responseString);
				}
				hookReturnCode = HOOK_RETURN_0;
				hookReturnMessage = "OK";

			} catch (RuntimeException e) {
				hookReturnCode = HOOK_RETURN_1;
				hookReturnMessage = e.getLocalizedMessage();
				LOGGER.error("Exception stack trace: ", e);

			} catch (Exception e) {
				hookReturnCode = HOOK_RETURN_1;
				hookReturnMessage = GPDOWN_ERR_MSG;
				LOGGER.error("Exception stack trace: ", e);
			}
		} else {
			hookReturnMessage = INVALID_SESSION_ID;
		}

		cacheService.updateSession(userSession);
		responseDto.setHookReturnCode(hookReturnCode);
		responseDto.setHookReturnMessage(hookReturnMessage);
		return responseDto;

	}

	@Override
	public IVRWebHookResponseDto processFID060Code(String sessionId, String nextState) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);

		IVRUserSession userSession = cacheService.getBySessionId(sessionId);

		if (userSession != null) {
			if (userSession.isCanBePagedEmail() || userSession.isCanBePagedMobile()) {

				response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_1);
				response.setHookReturnMessage(IVRLfacsConstants.ALPHA_PAGER);
				LOGGER.info("FID060 Status :" + IVRLfacsConstants.ALPHA_PAGER, sessionId);
			} else {

				response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_0);
				response.setHookReturnMessage(IVRLfacsConstants.NO_ALPHA_PAGER);
				LOGGER.info("FID060 Status :" + IVRLfacsConstants.NO_ALPHA_PAGER, sessionId);
			}
		} else {
			response.setHookReturnCode(HOOK_RETURN);
			response.setHookReturnMessage(IVRConstants.INVALID_SESSION_ID);
			LOGGER.info("FID060 Status :" + IVRLfacsConstants.INVALID, sessionId);
		}

		return response;
	}

	@Override
	public IVRWebHookResponseDto processFID515Code(String sessionId, String nextState)
			throws JsonMappingException, JsonProcessingException {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = "";
		String hookReturnMessage = "";

		IVRUserSession ivrUserSession = cacheService.getBySessionId(sessionId);
		try {
			if (ivrUserSession != null) {

				if (ivrUserSession.getCurrentAssignmentResponse() != null) {

					CurrentAssignmentResponseDto currentAssignmentResponse = objectMapper.readValue(
							ivrUserSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);

					if (currentAssignmentResponse.getMessageStatus() != null && !IVRLfacsConstants.S
							.equals(currentAssignmentResponse.getMessageStatus().getErrorStatus())) {
						IVRWebHookResponseDto fastivrErrorResp = ivrLfacsServiceHelper.findFastivrError(sessionId,
								nextState, currentAssignmentResponse.getMessageStatus());
						hookReturnCode = fastivrErrorResp.getHookReturnCode();
						hookReturnMessage = fastivrErrorResp.getHookReturnMessage();
					} else if (ivrLfacsServiceHelper.isSpecialCircuit(currentAssignmentResponse, response)) {
						hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_5;
						hookReturnMessage = "Special Circuit";
					} else {
						String enqType = "";
						if (isServiceOrderExist(currentAssignmentResponse)) {
							enqType = "RETRIEVE_LOOP_ASSSIGN";
							if (ivrUserSession.getRtrvLoopAssgMsgName() != null) {

								RetrieveLoopAssignmentResponse retAssignmentResponse = objectMapper.readValue(
										ivrUserSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class);

								if (IVRLfacsConstants.S
										.equals(retAssignmentResponse.getMessageStatus().getErrorStatus())) {
									IVRWebHookResponseDto sparePairResp = ivrLfacsServiceHelper.findSparePair(sessionId,
											nextState, ivrUserSession, enqType);
									hookReturnCode = sparePairResp.getHookReturnCode();
									hookReturnMessage = sparePairResp.getHookReturnMessage();
								}
							}
						} else {
							enqType = "RETRIEVE_MAINT_CHANGE";
							if (ivrUserSession.getRtrvMaintChngeMsgName() != null) {

								RetrieveMaintenanceChangeTicketResponse retAssignmentResponse = objectMapper.readValue(
										ivrUserSession.getRtrvMaintChngeMsgName(),
										RetrieveMaintenanceChangeTicketResponse.class);

								if (IVRLfacsConstants.S
										.equals(retAssignmentResponse.getMessageStatus().getErrorStatus())) {
									IVRWebHookResponseDto sparePairResp = ivrLfacsServiceHelper.findSparePair(sessionId,
											nextState, ivrUserSession, enqType);
									hookReturnCode = sparePairResp.getHookReturnCode();
									hookReturnMessage = sparePairResp.getHookReturnMessage();
								}
							}

						}

					}

				}
			} else {
				hookReturnMessage = INVALID_SESSION_ID;
			}

		} catch (Exception ex) {
			LOGGER.error("Exception stack trace: ", ex);
			hookReturnCode = HOOK_RETURN;
			hookReturnMessage = FASTIVR_BACKEND_ERR;
		}

		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info(hookReturnMessage, sessionId);
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFID525Code(String sessionId, String nextState, List<String> userInputDTMFList)
			throws JsonMappingException, JsonProcessingException {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = "";
		String hookReturnMessage = "";
		List<IVRParameter> parameters = new ArrayList<>();
		try {

			IVRUserSession ivrUserSession = cacheService.getBySessionId(sessionId);
			if (ivrUserSession != null) {

				if (ivrUserSession.getCurrentAssignmentResponse() != null) {
					CurrentAssignmentResponseDto currentAssignmentResponse = objectMapper.readValue(
							ivrUserSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
					Integer candPairListSize = 0;
					Integer candidatePairCounter = ivrUserSession.getCandPairCounter();
					String reqType = "";
					RetrieveLoopAssignmentResponse retrieveAssignmentResponse = null;
					RetrieveMaintenanceChangeTicketResponse retrieveMaintenanceChangeTicketResponse = null;
					List<LoopAssignCandidatePairInfo> candPairList=null;
					List<CandidatePairInfo>candPairListRMT=null;
					if (isServiceOrderExist(currentAssignmentResponse)) {

						if (ivrUserSession.getRtrvLoopAssgMsgName() != null) {
							retrieveAssignmentResponse = objectMapper.readValue(ivrUserSession.getRtrvLoopAssgMsgName(),
									RetrieveLoopAssignmentResponse.class);
							candPairListSize = retrieveAssignmentResponse.getReturnDataSet().getCandidatePairInfo()
									.size();
							 candPairList=retrieveAssignmentResponse.getReturnDataSet().getCandidatePairInfo();
						}
						reqType = "RETRIEVE_LOOP_ASSSIGN";
					} else {

						if (ivrUserSession.getRtrvMaintChngeMsgName() != null) {
							retrieveMaintenanceChangeTicketResponse = objectMapper.readValue(
									ivrUserSession.getRtrvMaintChngeMsgName(),
									RetrieveMaintenanceChangeTicketResponse.class);
							candPairListSize = retrieveMaintenanceChangeTicketResponse.getReturnDataSet()
									.getCandidatePairInfo().size();
							candPairListRMT=retrieveMaintenanceChangeTicketResponse.getReturnDataSet().getCandidatePairInfo();
									
						}
						reqType = "RETRIEVE_MAINT_CHANGE";
					}
					
					if (candidatePairCounter == -1) {
						
						if (candPairListSize > 0) {
							Integer undesirableCount=ivrLfacsServiceHelper.getUndesirableCandPair(reqType,candPairListRMT,candPairList);
							if(undesirableCount==0) {
							
								if (candPairListSize == 1) {
									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_3;
									hookReturnMessage = "Before reading one spair pair";
								}
								else {
									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_4;
									hookReturnMessage = "Before reading n spair pair";

									IVRParameter ivrParameter = new IVRParameter();
									ivrParameter.setData(candPairListSize.toString());
									parameters.add(ivrParameter);
								}
							
							}else {
							
								if (candPairListSize == 1) {
									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_1;
									hookReturnMessage = "Before reading one undesirable spair pair";
								}
								else{
									hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_2;
									hookReturnMessage = "Before reading n undesirable spair pair";

									IVRParameter ivrParameter = new IVRParameter();
									ivrParameter.setData(candPairListSize.toString());
									parameters.add(ivrParameter);
								}
							}
						
						candidatePairCounter++;
					}
	
					} else {

						if (userInputDTMFList.contains("1")) {

							candidatePairCounter = candidatePairCounter - 1;

						} else if (userInputDTMFList.contains("2")) {

							candidatePairCounter = candidatePairCounter - 2;
						} else if (userInputDTMFList.contains("3")) {

						} else if (userInputDTMFList.contains("4")) {

							candidatePairCounter = 0;
						}

						if (candidatePairCounter < candPairListSize) {
							hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_8;
							hookReturnMessage = "Read the cable pair";

							String data = "";
							String status="";
							if (reqType.equals("RETRIEVE_LOOP_ASSSIGN")) {
								data = retrieveAssignmentResponse.getReturnDataSet().getCandidatePairInfo().get(candidatePairCounter).getCableId()
										+ " / "+ retrieveAssignmentResponse.getReturnDataSet().getCandidatePairInfo().get(candidatePairCounter).getCableUnitId();
								
								status=	 ivrLfacsServiceHelper.getCablePairStatus(retrieveAssignmentResponse.getReturnDataSet().getCandidatePairInfo().get(candidatePairCounter).getCandidatePairStatus());
												
														
							} else {
								data = retrieveMaintenanceChangeTicketResponse.getReturnDataSet().getCandidatePairInfo().get(candidatePairCounter).getCableId()
										+ " / "
										+ retrieveMaintenanceChangeTicketResponse.getReturnDataSet().getCandidatePairInfo().get(candidatePairCounter).getCableUnitId();
													
								status= ivrLfacsServiceHelper.getCablePairStatus(retrieveMaintenanceChangeTicketResponse.getReturnDataSet().getCandidatePairInfo().get(candidatePairCounter).getPairStatus());
																		
							}

							IVRParameter ivrParameter = new IVRParameter();
							ivrParameter.setData(data);
							parameters.add(ivrParameter);
							
							ivrParameter = new IVRParameter();
							ivrParameter.setData(status);
							parameters.add(ivrParameter);

							candidatePairCounter++;

						} else if (candidatePairCounter >= candPairListSize) {

							if (candidatePairCounter == 1) {
								hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_5;
								hookReturnMessage = "Only one pair has been read";
							} else {
								hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_6;
								hookReturnMessage = "More than one pair has been read";

							}
						}

					}
					ivrUserSession.setCandPairCounter(candidatePairCounter);
					cacheService.updateSession(ivrUserSession);

				}
			} else {
				hookReturnMessage = INVALID_SESSION_ID;
			}

		} catch (Exception ex) {
			LOGGER.error("Exception stack trace: ", ex);
			hookReturnCode = HOOK_RETURN;
			hookReturnMessage = FASTIVR_BACKEND_ERR;
		}

		response.setParameters(parameters);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info(hookReturnMessage, sessionId);
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFID532Code(String sessionId, String nextState)
			throws JsonMappingException, JsonProcessingException {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = "";
		String hookReturnMessage = "";

		try {

			IVRUserSession ivrUserSession = cacheService.getBySessionId(sessionId);
			if (ivrUserSession != null) {
				if (ivrUserSession.getCandPairCounter() > 0) {
					if (ivrUserSession.getCandPairCounter() == 1) {
						hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_2;
						hookReturnMessage = "Only one pair has been read";
					} else {
						hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_1;
						hookReturnMessage = "More than one pair has been read";
					}
				} else {
					hookReturnMessage = "No Spare pair has been read";
				}

			} else {
				hookReturnMessage = INVALID_SESSION_ID;
			}

		} catch (Exception ex) {
			LOGGER.error("Exception stack trace: ", ex);
			hookReturnCode = HOOK_RETURN;
			hookReturnMessage = FASTIVR_BACKEND_ERR;
		}

		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info(hookReturnMessage, sessionId);
		return response;
	}

	public IVRWebHookResponseDto processFID211Code(String sessionId, String nextState, String userDTMFInput) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		IVRUserSession session = cacheService.getBySessionId(sessionId);

		if (session == null) {
			response.setHookReturnMessage(INVALID_SESSION_ID);
			response.setHookReturnCode(HOOK_RETURN);
			return response;
		}

		response = tnValidation.validateFacsTN(userDTMFInput, session);
		session.setFacsInqType(INQUIRY_BY_CABLE_PAIR);
		session.setSegmentRead(null);
		cacheService.updateSession(session);
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);

		return response;
	}

	/**
	 * This method is to Check if Service Address exists for the current Assignment
	 * 
	 * @param sessionid
	 * @param currentstate
	 * @return {@link IVRWebHookResponseDto}
	 */
	@Override
	public IVRWebHookResponseDto processFID600Code(String sessionId, String currentState) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		IVRUserSession session = cacheService.getBySessionId(sessionId);
		String hookReturnCode = HOOK_RETURN_0;

		if (session != null) {
			String tn;
			List<IVRParameter> params = null;
			try {
				tn = ivrLfacsServiceHelper.extractTNInfoFromLosDBResponse(session.getLosDbResponse()).getTn();
				params = ivrLfacsServiceHelper.addParamterData(tn);
				hookReturnCode = ivrLfacsServiceHelper.checkIfServiceAddrExists(session);
				if (hookReturnCode.equalsIgnoreCase(HOOK_RETURN_0)) {
					// no service address case
					response.setParameters(params);
					response.setHookReturnMessage(SERVICE_ADDRESS_NOT_FOUND);
				} else {
					response.setHookReturnMessage(SERVICE_ADDRESS_FOUND);
				}

			} catch (Exception e) {
				LOGGER.error("Session: " + sessionId + ", Cannot check for service address due to the error: ", e);
				response.setHookReturnMessage(SERVICE_ADDRESS_NOT_FOUND);
				response.setParameters(params);
			}

		} else {
			response.setHookReturnMessage(INVALID_SESSION_ID);
		}

		cacheService.updateSession(session);
		response.setHookReturnCode(hookReturnCode);
		return response;
	}
	
	@Override
	public IVRWebHookResponseDto processFID224Code(String sessionId, String nextState, List<String> userInputDTMFList) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String userDTMFInput = userInputDTMFList.get(0);
		
		if(userDTMFInput.isBlank()) {
			
			response.setHookReturnCode(HOOK_RETURN_2);
			response.setHookReturnMessage("Input Codes are empty");
			return response;
		} else if (userDTMFInput.contains("*")) {

			String convertedInput = ivrLfacsServiceHelper.convertInputCodesToAlphabets(userDTMFInput);
			
			if(convertedInput == null) {
				
				response.setHookReturnCode(HOOK_RETURN_2);
				response.setHookReturnMessage("Input Codes are entered incorrectly");
				return response;
			}

			String firstValues = userDTMFInput.substring(0, userDTMFInput.indexOf("*"));
					
			String lastValues = userDTMFInput.substring(userDTMFInput.lastIndexOf("*") + 1);
			
			convertedInput = firstValues + convertedInput + lastValues;
			
			List<IVRParameter> parameterList = ivrLfacsServiceHelper.addParamterData(convertedInput);
			
			response.setParameters(parameterList);
		} else {
			
			List<IVRParameter> parameterList = ivrLfacsServiceHelper.addParamterData(userDTMFInput);
			
			response.setParameters(parameterList);
		}
		
		response.setHookReturnCode(HOOK_RETURN_1);
		response.setHookReturnMessage(SUCCESS);
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFID237Code(String sessionId, String nextState, List<String> userInputDTMFList) {

		IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
		responseDto.setSessionId(sessionId);
		responseDto.setCurrentState(nextState);
		String hookReturnCode = "";
		String hookReturnMessage = "";

		IVRUserSession userSession = cacheService.getBySessionId(sessionId);

		if (userSession != null) {

			try {

				if (userInputDTMFList != null && userInputDTMFList.size() == 2
						&& INQUIRY_BY_CABLE_PAIR.equalsIgnoreCase(userSession.getFacsInqType())) {
					String convertedInput = userInputDTMFList.get(0);
					if (userInputDTMFList.get(0).contains("*")) {
						convertedInput = ivrLfacsServiceHelper.convertInputCodesToAlphabets(userInputDTMFList.get(0));
						String firstValues = userInputDTMFList.get(0).substring(0, userInputDTMFList.get(0).indexOf("*"));
						
						String lastValues = userInputDTMFList.get(0).substring(userInputDTMFList.get(0).lastIndexOf("*") + 1);
						
						convertedInput = firstValues + convertedInput + lastValues;
					}

					userSession.setCable(convertedInput);
					userSession.setPair(userInputDTMFList.get(1));
				}

				// fetch TN from cache
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

				CurrentAssignmentRequestTnDto requestObject = ivrLfacsServiceHelper
						.buildCurrentAssignmentInqRequest(null, primaryNpa, primaryNxx, userInputDTMFList, userSession);
				String jsonRequest = objectMapper.writeValueAsString(requestObject);
				String responseString = ivrLfacsServiceHelper.callCurrentAssignmentInquiryLfacs(jsonRequest,
						userSession);

				responseString = ivrLfacsServiceHelper.cleanResponseString(responseString);
				LOGGER.info("Session id:" + userSession.getSessionId()
						+ ", Response from LFACS Current Assignment API: " + responseString);
				// Validating it's a good response json string from lfacs
				CurrentAssignmentResponseDto responseObject = objectMapper.readValue(responseString,
						CurrentAssignmentResponseDto.class);
				if (responseObject != null) {
					// String was getting > 4000 bytes, cannot store in VARCHAR2
					// use CURR_ASSG_RESP_CLOB to load and fetch the data
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
		} else {
			hookReturnMessage = INVALID_SESSION_ID;
		}

		cacheService.updateSession(userSession);
		responseDto.setHookReturnCode(hookReturnCode);
		responseDto.setHookReturnMessage(hookReturnMessage);
		return responseDto;

	}

	/**
	 * For TN flow Method to check for alpha pager and Issue Additional Lines
	 * request This is called from FID600 when service address is found in current
	 * assignment response. 
	 * Action: Calls the additional lines request. 
	 * Checks if Technician has pager, then sends the result to Tech's pager (mobile) If
	 * Pager(mobile) is not configured, then results are sent to Email and by Voice
	 * Current State: FID080 
	 * Next State: FI0085 
	 *             (3) - If Pager is present
	 *              GPDOWN(500) - Application Exception 
	 *              (2) - Additional Lines by Voice 
	 *              (2) - If Pager is not present
	 * 
	 * @param sessionId
	 * @param currentState
	 * @return {@link IVRWebHookResponseDto}
	 */
	@Override
	public IVRWebHookResponseDto processFID080Code(String sessionId, String currentState) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		IVRUserSession session = cacheService.getBySessionId(sessionId);
		String hookReturnCode = GPDOWN_ERR_MSG_CODE;
		String hookReturnMessage = GPDOWN_ERR_MSG;
		try {
			if (session != null) {
				
				AdditionalLinesReportResponseDto responseDto = processAdditionalLinesResponse(session);
				String tn = session.getInquiredTn();
				// check for pager (mobile) setup
				if (session.isCanBePagedMobile()) {
					// send the api test result to mobile
					//process and PAGE to the required device
					addlLinesPageBuilder.processAddlLinesResponse(responseDto, session,
							FormatUtilities.FormatTelephoneNNNXXXX(tn), IVRConstants.NET_PHONE_DEVICE);
					hookReturnCode = HOOK_RETURN_3;
					hookReturnMessage = ADDITIONAL_LINES_PAGE_MSG;
				} else {
					// send api test result to email
					//process and PAGE to the required device
					addlLinesPageBuilder.processAddlLinesResponse(responseDto, session,
							FormatUtilities.FormatTelephoneNNNXXXX(tn), IVRConstants.NET_MAIL_DEVICE);
					// Start voice flow
					hookReturnCode = HOOK_RETURN_2;
					hookReturnMessage = ADDITIONAL_LINES_CANNOT_PAGE_MSG;
				}

			} else {
				hookReturnMessage = INVALID_SESSION_ID;
			}
		} catch (Exception e) {
			LOGGER.info("Session: " + sessionId + ", Error while processing FID080 ", e);
		}

		cacheService.updateSession(session);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;
	}

	/**
	 * For Cable Pair flow Method to check for alpha pager and Issue Additional
	 * Lines request This is called from FID600 when service address is found in
	 * current assignment response. Action: Calls the additional lines request.
	 * Checks if Technician has pager, then sends the result to Tech's pager
	 * (mobile) If Pager(mobile) is not configured, then results are sent to Email
	 * and by Voice 
	 * Current State: FID080 
	 * Next State: FI0085 
	 * (3) - If Pager is present 
	 * GPDOWN(500) - Application Exception 
	 * (2)Additional Lines by Voice &  If Pager is not present
	 * 
	 * @param sessionId
	 * @param currentState
	 * @return {@link IVRWebHookResponseDto}
	 */
	@Override
	public IVRWebHookResponseDto processFID285Code(String sessionId, String currentState) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		IVRUserSession session = cacheService.getBySessionId(sessionId);
		String hookReturnCode = GPDOWN_ERR_MSG_CODE;
		String hookReturnMessage = GPDOWN_ERR_MSG;
		try {
			if (session != null) {
				
				AdditionalLinesReportResponseDto responseDto = processAdditionalLinesResponse(session);
				String tn = session.getInquiredTn();
				
				// check for pager (mobile) setup
				if (session.isCanBePagedMobile()) {
					// send the api test result to mobile
					// TODO : PAGER LOGIC
					addlLinesPageBuilder.processAddlLinesResponse(responseDto, session,
							FormatUtilities.FormatTelephoneNNNXXXX(tn), IVRConstants.NET_PHONE_DEVICE);
					hookReturnCode = HOOK_RETURN_3;
					hookReturnMessage = ADDITIONAL_LINES_PAGE_MSG;

				} else {
					// send api test result to email
					addlLinesPageBuilder.processAddlLinesResponse(responseDto, session,
							FormatUtilities.FormatTelephoneNNNXXXX(tn), IVRConstants.NET_MAIL_DEVICE);
					// Start voice flow
					hookReturnCode = HOOK_RETURN_2;
					hookReturnMessage = ADDITIONAL_LINES_CANNOT_PAGE_MSG;
				}
			} else {
				hookReturnMessage = IVRConstants.INVALID_SESSION_ID;
			}
		} catch (Exception ex) {
			hookReturnCode = GPDOWN_ERR_MSG_CODE;
			hookReturnMessage = GPDOWN_ERR_MSG;
		}

		cacheService.updateSession(session);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info(hookReturnMessage, sessionId);
		return response;

	}
	
	public AdditionalLinesReportResponseDto processAdditionalLinesResponse(IVRUserSession session) throws JsonMappingException, JsonProcessingException {
		TNInfoResponse losDbResponse = ivrLfacsServiceHelper
				.extractTNInfoFromLosDBResponse(session.getLosDbResponse());
		String primaryNpa = "";
		String primaryNxx = "";
		if (losDbResponse != null) {
			primaryNpa = losDbResponse.getPrimaryNPA();
			primaryNxx = losDbResponse.getPrimaryNXX();
			
		} else {
			throw new RuntimeException(CANNOT_FETCH_CURR_ASSG_API_LOSDB_NULL);
		}
		String empCode = StringUtils.isNotBlank(session.getEc()) ? session.getEc()
				: WRAPPER_API_DEFAULT_EMPLOYEE_ID;

		String sessionId = session.getSessionId();
		String serviceAddress = additionalLinesUtil.getFormattedServiceAddress(sessionId,
				session.getCurrentAssignmentResponse());

		AdditionalLinesReportRequestDto request = ivrLfacsServiceHelper
				.buildAdditionalLinesReportRequest(serviceAddress, empCode, primaryNpa, primaryNxx);
		String jsonRequest = objectMapper.writeValueAsString(request);
		String addLinesResultJson = ivrHttpClient.httpPostCall(jsonRequest, additionalLinesUrl, sessionId,
				ADDITIONAL_LINES_REQUEST);
		String cleanJsonStr = ivrHttpClient.cleanResponseString(addLinesResultJson);

		AdditionalLinesReportResponseDto responseDto = objectMapper.readValue(cleanJsonStr,
				AdditionalLinesReportResponseDto.class);
		session.setAdditionalLinesResponse(cleanJsonStr);
		
		return responseDto;
	}
	
	@Override
	public IVRWebHookResponseDto processFID535Code(String sessionId, String nextState)
			throws JsonMappingException, JsonProcessingException {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = "";
		String hookReturnMessage = "";

		try {
			IVRUserSession ivrUserSession = cacheService.getBySessionId(sessionId);
			if (ivrUserSession != null) {
				Integer candidatePairCounter = ivrUserSession.getCandPairCounter();
				if (ivrUserSession.getCurrentAssignmentResponse() != null) {
					CurrentAssignmentResponseDto currentAssignmentResponse = objectMapper.readValue(
							ivrUserSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
					
					if (isServiceOrderExist(currentAssignmentResponse)) {

						if (ivrUserSession.getRtrvLoopAssgMsgName() != null) {

							RetrieveLoopAssignmentResponse retrieveLoopAssignmentResponse = objectMapper.readValue(
									ivrUserSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class);

							response = ivrLfacsServiceHelper.getColourCodeRetrieveLoopAssignment(response,
									retrieveLoopAssignmentResponse, ivrUserSession);
							candidatePairCounter++;

							if (response != null) {

								hookReturnCode = response.getHookReturnCode();
								hookReturnMessage = response.getHookReturnMessage();

							}
						}

					} else {

						if (ivrUserSession.getRtrvMaintChngeMsgName() != null) {

							RetrieveMaintenanceChangeTicketResponse retrieveMaintenanceChangeTicketResponse = objectMapper
									.readValue(ivrUserSession.getRtrvMaintChngeMsgName(),
											RetrieveMaintenanceChangeTicketResponse.class);

							response = ivrLfacsServiceHelper.getColourCodeRetrieveMaintChange(response,
									retrieveMaintenanceChangeTicketResponse, ivrUserSession);
							candidatePairCounter++;
							

							if (response != null) {

								hookReturnCode = response.getHookReturnCode();
								hookReturnMessage = response.getHookReturnMessage();

							}
						}
					}
				} else {
					hookReturnMessage = "Retrieve Response Not Found";
				}
				//ivrUserSession.setCandPairCounter(candidatePairCounter);
				//cacheService.updateSession(ivrUserSession);

			} else {
				hookReturnMessage = INVALID_SESSION_ID;
			}

		} catch (Exception ex) {

			LOGGER.error("Exception stack trace: ", ex);
			hookReturnCode = HOOK_RETURN;
			hookReturnMessage = FASTIVR_BACKEND_ERR;
		}
	
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFID560Code(String sessionId, String nextState) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = "";
		String hookReturnMessage = "";

		try {
			IVRUserSession ivrUserSession = cacheService.getBySessionId(sessionId);
			if (ivrUserSession != null) {

				if (ivrUserSession.getFacsInqType() != null) {

					resetSessionCounter(ivrUserSession);
					if (INQUIRY_BY_TN.equalsIgnoreCase(ivrUserSession.getFacsInqType())) {
						hookReturnCode = HOOK_RETURN_1;
						hookReturnMessage = "Enquiry By TN";
					} else if (INQUIRY_BY_CABLE_PAIR.equalsIgnoreCase(ivrUserSession.getFacsInqType())) {
						hookReturnCode = HOOK_RETURN_2;
						hookReturnMessage = "Enquiry By Cable Pair";
					} else {
						hookReturnMessage = "Invalid Enquiry Type";
					}

				} else {
					hookReturnMessage = "Enquiry Type Not Found";
				}
			} else {
				hookReturnMessage = INVALID_SESSION_ID;
			}
		} catch (Exception ex) {

			LOGGER.error("Exception stack trace: ", ex);
			hookReturnCode = HOOK_RETURN;
			hookReturnMessage = FASTIVR_BACKEND_ERR;
		}

		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFID068Code(String sessionId, String nextState, List<String> userInputDTMFList) {

		IVRWebHookResponseDto ivrWebHookResponseDto = new IVRWebHookResponseDto();

		ivrWebHookResponseDto.setSessionId(sessionId);
		ivrWebHookResponseDto.setCurrentState(nextState);

		IVRUserSession userSession = cacheService.getBySessionId(sessionId);
		String hookReturnCode = HOOK_RETURN_6;
		String hookReturnMessage = "Initial current assignment results are OK";

		if (userSession != null) {

			String jsonString = userSession.getCurrentAssignmentResponse();

			try {
				CurrentAssignmentResponseDto dto = objectMapper.readValue(jsonString,
						CurrentAssignmentResponseDto.class);

				if (dto.getReturnDataSet().getLoop() != null
						&& dto.getReturnDataSet().getLoop().get(0).getSEG().size() > 3) {

					hookReturnCode = HOOK_RETURN_1;
					hookReturnMessage = "More than three segments";

				}

				else if (dto.getReturnDataSet().getLoop() != null
						&& dto.getReturnDataSet().getLoop().get(0).getSEG().size() == 1
						&& userInputDTMFList.contains("2")) {

					if (userInputDTMFList.contains(IVRConstants.DTMF_INPUT_2)) {
						hookReturnCode = HOOK_RETURN_2;
						hookReturnMessage = "F2 requested but not F2(2)";
					}

				} else if (dto.getReturnDataSet().getLoop() != null
						&& dto.getReturnDataSet().getLoop().get(0).getSEG().size() < 3
						&& userInputDTMFList.contains("3")) {

					if (userInputDTMFList.contains(IVRConstants.DTMF_INPUT_3)) {
						hookReturnCode = HOOK_RETURN_3;
						hookReturnMessage = "F3 requested but not F3(3)";
					}

				}  else if(ivrLfacsServiceHelper.isLineStationTransfer(dto)) {
					
					List<IVRParameter> parameterList = new ArrayList<IVRParameter>();
					IVRParameter ivrParameter = new IVRParameter();
					ivrParameter.setData(userSession.getInquiredTn());
					parameterList.add(ivrParameter);
					ivrWebHookResponseDto.setParameters(parameterList);
					hookReturnCode = HOOK_RETURN_4;
					hookReturnMessage = "TN is part of a LST";
				} else if(ivrLfacsServiceHelper.isSpecialCircuit(dto, ivrWebHookResponseDto)) {
					
					ivrWebHookResponseDto.setParameters(null);
					hookReturnCode = HOOK_RETURN_5;
					hookReturnMessage = "Special Circuit";
				}

			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

			cacheService.updateSession(userSession);
		} else {
			hookReturnCode = GPDOWN_ERR_MSG_CODE;
			hookReturnMessage = GPDOWN_ERR_MSG;
		}

		ivrWebHookResponseDto.setHookReturnCode(hookReturnCode);
		ivrWebHookResponseDto.setHookReturnMessage(hookReturnMessage);
		return ivrWebHookResponseDto;

	}

	@Override
	public IVRWebHookResponseDto processFID070Code(String sessionId, String nextState, int segmentNumber) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = HOOK_RETURN_1;
		String hookReturnMessage = GPDOWN_ERR_MSG;

		IVRUserSession userSession = cacheService.getBySessionId(sessionId);

		if (userSession != null) {

			try {

				CurrentAssignmentResponseDto currentAssignmentResponseDto = objectMapper
						.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);

				String responseString = getMultipleAppearaceJsonResponse(userSession, segmentNumber, sessionId,
						currentAssignmentResponseDto);

				// validate response is in json format or not , by reading response string in
				// dto
				MultipleAppearanceResponseDto responseObject = objectMapper.readValue(responseString,
						MultipleAppearanceResponseDto.class);

				if (responseObject != null) {
					userSession.setMultipleAppearanceResponse(responseString);

					String pageText = ivrLfacsPagerText.getPageMultipleAppearences(userSession,
							currentAssignmentResponseDto, responseObject, segmentNumber);

					if (userSession.isCanBePagedMobile()) {
						ivrLfacsServiceHelper.sendTestResultToTech(NETPAGE_SUBJECT_MULTIPLE_APPEARANCES, pageText,
								NET_PHONE_DEVICE, userSession);
					}

					if (userSession.isCanBePagedEmail()) {
						ivrLfacsServiceHelper.sendTestResultToTech(NETPAGE_SUBJECT_MULTIPLE_APPEARANCES, pageText,
								NET_MAIL_DEVICE, userSession);
					}

					switch (segmentNumber) {
					case 1:
						hookReturnCode = HOOK_RETURN_2;
						hookReturnMessage = "F1";
						break;
					case 2:
						hookReturnCode = HOOK_RETURN_3;
						hookReturnMessage = "F2";
						break;
					case 3:
						hookReturnCode = HOOK_RETURN_4;
						hookReturnMessage = "F3";
						break;

					default:
						hookReturnCode = HOOK_RETURN;
						hookReturnMessage = "Invalid Segment";
						break;
					}
				}
			} catch (RuntimeException e) {
				hookReturnCode = HOOK_RETURN_1;
				hookReturnMessage = e.getLocalizedMessage();
				LOGGER.error("Exception stack trace: ", e);

			} catch (Exception e) {
				hookReturnCode = HOOK_RETURN_1;
				hookReturnMessage = GPDOWN_ERR_MSG;
				LOGGER.error("Exception stack trace: ", e);
			}

		}

		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		cacheService.updateSession(userSession);
		return response;
	}

	private String getMultipleAppearaceJsonResponse(IVRUserSession userSession, int segmentNumber, String sessionId,CurrentAssignmentResponseDto currentAssignmentResponseDto)
			throws JsonMappingException, JsonProcessingException {
		String cable = null;
		String cableUnitId = null;

		if (segmentNumber == 0) {
			cable = userSession.getCable();
			cableUnitId = userSession.getPair();
		} else if (CurrentAssignmentUtils.isSEGNotEmpty(currentAssignmentResponseDto)) {

			// set cable name and cable units here
			cable = CurrentAssignmentUtils.getCABySegmentNumber(currentAssignmentResponseDto, segmentNumber);
			cableUnitId = CurrentAssignmentUtils.getCablePairBySegmentNumber(currentAssignmentResponseDto,
					segmentNumber);

		}

		TNInfoResponse losDbResponse = ivrLfacsServiceHelper
				.extractTNInfoFromLosDBResponse(userSession.getLosDbResponse());
		String primaryNpa = null;
		String primaryNxx = null;
		if (losDbResponse != null) {
			primaryNpa = losDbResponse.getPrimaryNPA();
			primaryNxx = losDbResponse.getPrimaryNXX();
		} else {
			throw new BusinessException(IVRConstants.CANNOT_FETCH_MULTI_APPEARANCE_API_LOSDB_NULL);
		}

		String employeeId = StringUtils.isNotBlank(userSession.getEc()) ? userSession.getEc()
				: IVRConstants.WRAPPER_API_DEFAULT_EMPLOYEE_ID;

		MultipleAppearanceRequestDto requestObject = ivrLfacsServiceHelper.buildMultipleAppearanceInqRequest(cable,
				cableUnitId, employeeId, primaryNpa, primaryNxx);

		String responseString = ivrHttpClient.httpPostCall(objectMapper.writeValueAsString(requestObject),
				multipleAppearanceUrl, sessionId, MULTIPLE_APPEARANCE_REQUEST);

		LOGGER.info("Session id:" + userSession.getSessionId() + ", Response from Multiple Appearance API: "
				+ responseString);
		return ivrHttpClient.cleanResponseString(responseString);
	}

	@Override
	public IVRWebHookResponseDto processFID271Code(String sessionId, String currentState) {

		IVRUserSession userSession = null;

		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		try {

			userSession = cacheService.getBySessionId(sessionId);

			if (userSession != null && userSession.getCurrentAssignmentResponse() != null) {

				currentAssignmentResponseDto = objectMapper.readValue(userSession.getCurrentAssignmentResponse(),
						CurrentAssignmentResponseDto.class);

				String pageText = ivrLfacsPagerText.getPageCurrentAssignment(response, userSession,
						currentAssignmentResponseDto);

				if (!IVRConstants.FAILURE.equalsIgnoreCase(pageText) && userSession.isCanBePagedMobile()) {

					ivrLfacsServiceHelper.sendTestResultToTech(NETPAGE_SUBJECT_CURRENT_ASSIGNMENT, pageText,
							NET_PHONE_DEVICE, userSession);

					response.setHookReturnCode(HOOK_RETURN_3);
					response.setHookReturnMessage("Alpha Pager");
				} else if (userSession != null && !userSession.isCanBePagedMobile()) {

					ivrLfacsServiceHelper.sendTestResultToTech(NETPAGE_SUBJECT_CURRENT_ASSIGNMENT, pageText,
							NET_MAIL_DEVICE, userSession);
					response.setHookReturnCode(HOOK_RETURN_2);
					response.setHookReturnMessage("No Alpha Pager");
				}
			} else {

				response.setHookReturnCode(HOOK_RETURN_1);
				response.setHookReturnMessage(FASTIVR_BACKEND_ERR);
			}

		} catch (Exception ex) {

			// TODO Need to check the HookReturnCode
			LOGGER.error("Exception stack trace: ", ex);
			response.setHookReturnCode(HOOK_RETURN_1);
			response.setHookReturnMessage(FASTIVR_BACKEND_ERR);
		}

		return response;
	}

	@Override
	public IVRWebHookResponseDto processFID250Code(String sessionId, String currentState, int dmInput) {

		IVRUserSession userSession = null;

		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		try {

			userSession = cacheService.getBySessionId(sessionId);

			if (userSession != null && userSession.getCurrentAssignmentResponse() != null) {

				currentAssignmentResponseDto = objectMapper.readValue(userSession.getCurrentAssignmentResponse(),
						CurrentAssignmentResponseDto.class);

				if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
						&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
						&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()) {

					List<IVRParameter> cablePairStatusList = new ArrayList<IVRParameter>();

					cablePairStatusList = ivrLfacsServiceHelper.getCablePairStatus(currentAssignmentResponseDto,
							cablePairStatusList);

					if (!cablePairStatusList.isEmpty() && cablePairStatusList.get(0) != null
							&& ("*CT".equalsIgnoreCase(cablePairStatusList.get(0).getData())
									|| "*CF".equalsIgnoreCase(cablePairStatusList.get(0).getData())
									|| "CT".equalsIgnoreCase(cablePairStatusList.get(0).getData())
									|| "CF".equalsIgnoreCase(cablePairStatusList.get(0).getData()))) {

						if (1 == dmInput) {

							response.setHookReturnCode(HOOK_RETURN_1);
							response.setHookReturnMessage("OK");
						} else {

							List<IVRParameter> cablePairList = ivrLfacsServiceHelper.getCablePair(currentAssignmentResponseDto, 0);

							cablePairList.addAll(cablePairStatusList);

							response.setHookReturnCode(HOOK_RETURN_8);
							response.setHookReturnMessage("No Circuit Id Associated");
							response.setParameters(cablePairList);
						}
					} else if (!cablePairStatusList.isEmpty() && cablePairStatusList.get(0) != null
							&& "SPR".equalsIgnoreCase(cablePairStatusList.get(0).getData())) {

						response.setHookReturnCode(HOOK_RETURN_7);
						response.setHookReturnMessage("Spare Cable and Pair");
					} else if ((!cablePairStatusList.isEmpty() && cablePairStatusList.get(0) != null
							&& "PCF".equalsIgnoreCase(cablePairStatusList.get(0).getData()))
							|| StringUtils
									.isBlank(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID())
							|| "NONE".equalsIgnoreCase(
									currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID())) {

						List<IVRParameter> cablePairList = ivrLfacsServiceHelper.getCablePair(currentAssignmentResponseDto, 0);

						cablePairList.addAll(cablePairStatusList);

						response.setHookReturnCode(HOOK_RETURN_8);
						response.setHookReturnMessage("No Circuit Id Associated");
						response.setParameters(cablePairList);
					} else {

						switch (dmInput) {
						case 1:

							response.setHookReturnCode(HOOK_RETURN_1);
							response.setHookReturnMessage("OK");
							break;

						case 2:

							response.setHookReturnCode(HOOK_RETURN_2);
							response.setHookReturnMessage("OK");
							break;

						case 4:

							response.setHookReturnCode(HOOK_RETURN_4);
							response.setHookReturnMessage("OK");
							break;

						case 5:

							response.setHookReturnCode(HOOK_RETURN_5);
							response.setHookReturnMessage("OK");
							break;
						}
					}
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
	public IVRWebHookResponseDto processFID274Code(String sessionId, String nextState) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = "";
		String hookReturnMessage = "";

		try {
			IVRUserSession userSession = cacheService.getBySessionId(sessionId);

			if (userSession != null) {

				if (!userSession.isCanBePagedMobile() && !userSession.isCanBePagedEmail()) {
					
					hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_2;
					hookReturnMessage = IVRLfacsConstants.NO_ALPHA_PAGER;

				} else {

					hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_1;
					hookReturnMessage = IVRLfacsConstants.ALPHA_PAGER;
				}
			} 
			else {hookReturnMessage = IVRConstants.INVALID_SESSION_ID;}
				

		} catch (Exception ex) {
			hookReturnCode = GPDOWN_ERR_MSG_CODE;
			hookReturnMessage = GPDOWN_ERR_MSG;
		}
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info(hookReturnMessage, sessionId);
		return response;
	}

	// This method is written for 2 state code together (#FID090 & #FID700)
	@Override
	public IVRWebHookResponseDto processFID090Code(String sessionId, String nextState) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = HOOK_RETURN_1;
		String hookReturnMessage = GPDOWN_ERR_MSG;
		String pageResult = "Currently PAGE Text formation is under progress";
		String device = NET_PHONE_DEVICE;

		IVRUserSession userSession = cacheService.getBySessionId(sessionId);

		if (userSession != null) {

			try {
				IVRHttpResponseDto responseString = getCentralOfficeEquipmentJsonResponse(userSession, sessionId);

				if (responseString.getStatusCode() == HttpStatus.REQUEST_TIMEOUT.value()) {
					hookReturnCode = HOOK_RETURN_2;
					hookReturnMessage = "SWITCH did not respond.";
					response.setCurrentState(IVRConstants.STATE_FID700);

				} else if (responseString.getStatusCode() == HttpStatus.BAD_REQUEST.value()) {
					hookReturnCode = HOOK_RETURN_4;
					hookReturnMessage = "SWITCH trans.failed";
					response.setCurrentState(IVRConstants.STATE_FID700);

				} else if (responseString.getStatusCode() == HttpStatus.BAD_GATEWAY.value()) {
					hookReturnCode = HOOK_RETURN_3;
					hookReturnMessage = "SWITCH is currently not available";
					response.setCurrentState(IVRConstants.STATE_FID700);

				} else {

					// validate response is in json format or not , by reading response string in
					// dto

					CentralOfficeEquipmentResponseDto responseObject = objectMapper.readValue(
							ivrHttpClient.cleanResponseString(responseString.getResponseBody()),
							CentralOfficeEquipmentResponseDto.class);

					if (responseObject != null && responseObject.getMessageStatus() != null
							&& !StringUtils.isNotBlank(responseObject.getMessageStatus().getErrorCode())) {

						pageResult = ivrLfacsPagerText.getCentralOfficePageText(response, userSession, responseObject);

						userSession.setCentralOfficeEquipmentResponse(responseString.getResponseBody());
						cacheService.updateSession(userSession);
						if (userSession.isCanBePagedMobile()) {
							ivrLfacsServiceHelper.sendTestResultToTech(NETPAGE_SUBJECT_COE, pageResult, device,
									userSession);
							hookReturnCode = HOOK_RETURN_3;
							hookReturnMessage = "Alpha Pager";

						} else {
							response.setCurrentState(IVRConstants.STATE_FID700);
							device = NET_MAIL_DEVICE;
							ivrLfacsServiceHelper.sendTestResultToTech(NETPAGE_SUBJECT_COE, pageResult, device,
									userSession);
							if (StringUtils.isNotBlank(responseObject.getReturnDataSet().getSwitchNetworkUnitId())) {
								List<IVRParameter> parameterList = new ArrayList<>();
								parameterList.add(
										new IVRParameter(responseObject.getReturnDataSet().getSwitchNetworkUnitId()));
								response.setParameters(parameterList);
								hookReturnCode = HOOK_RETURN_8;
								hookReturnMessage = "OE Found";
							} else {
								hookReturnCode = HOOK_RETURN_5;
								hookReturnMessage = "Unable to find OE";
							}

						}

					} else {
						hookReturnCode = HOOK_RETURN_4;
						hookReturnMessage = "SWITCH transaction failed.";
						response.setCurrentState(IVRConstants.STATE_FID700);
					}
				}

			} catch (Exception e) {
				LOGGER.error("Exception in FID090 : ", e);
			}
		}

		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);

		return response;
	}

	@Override
	public IVRWebHookResponseDto processFID300Code(String sessionId, String currentState) {

		return processFID090Code(sessionId, currentState);
	}

	private IVRHttpResponseDto getCentralOfficeEquipmentJsonResponse(IVRUserSession userSession, String sessionId)
			throws JsonMappingException, JsonProcessingException {

		TNInfoResponse losDbResponse = ivrLfacsServiceHelper
				.extractTNInfoFromLosDBResponse(userSession.getLosDbResponse());
		if (losDbResponse != null) {

			CentralOfficeEquipmentRequestDto requestObject = ivrLfacsServiceHelper.buildCentralOfcEquipmentInqRequest(
					losDbResponse.getTn(), losDbResponse.getPrimaryNPA(), losDbResponse.getPrimaryNXX());

			IVRHttpResponseDto ivrHttpResponseDto;
			try {
				ivrHttpResponseDto = ivrHttpClient.httpPostApiCall(objectMapper.writeValueAsString(requestObject),
						centralOfcEquipmentUrl, sessionId, CENTRAL_OFFICE_EQUIPMENT);
			} catch (HttpTimeoutException e) {
				ivrHttpResponseDto = new IVRHttpResponseDto();
				ivrHttpResponseDto.setStatusCode(HttpStatus.REQUEST_TIMEOUT.value());
				LOGGER.info("Session id:" + userSession.getSessionId() + ", Http Timeout Exception Occured : "
						+ e.getMessage());
			}

			LOGGER.info("Session id:" + userSession.getSessionId() + ", Response from Central Office Equipment API: "
					+ ivrHttpResponseDto);
			return ivrHttpResponseDto;
		} else {
			throw new BusinessException(IVRConstants.CANNOT_FETCH_COE_API_LOSDB_NULL);
		}
	}

	@Override
	public IVRWebHookResponseDto processFID291Code(String sessionId, String nextState)
			throws JsonMappingException, JsonProcessingException {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = "";
		String hookReturnMessage = "";
		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();
		IVRParameter parameter = new IVRParameter();

		IVRUserSession ivrUserSession = cacheService.getBySessionId(sessionId);
		String losDbResponseJsonString = ivrUserSession.getLosDbResponse();
		TNInfoResponse losDbResponse = ivrLfacsServiceHelper.extractTNInfoFromLosDBResponse(losDbResponseJsonString);

		if (losDbResponse != null) {

			if (losDbResponse.getTn() == null || losDbResponse.getTn().equals("")) {
				hookReturnCode = HOOK_RETURN_1;
				hookReturnMessage = "No Fax Number in user profile";
			} else {
				hookReturnCode = HOOK_RETURN_2;
				hookReturnMessage = "Fax Number in user profile";

				parameter.setData(losDbResponse.getTn());
				parameterList.add(parameter);
				response.setParameters(parameterList);

			}

		} else {
			hookReturnMessage = "LossDB response Not Found";
		}
		;

		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFID630Code(String sessionId, String currentState, int dmInput) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);

		response.setCurrentState(currentState);

		IVRUserSession userSession = cacheService.getBySessionId(sessionId);

		if (userSession != null && userSession.getAdditionalLinesResponse() != null) {

			try {

				AdditionalLinesReportResponseDto responseDto = objectMapper
						.readValue(userSession.getAdditionalLinesResponse(), AdditionalLinesReportResponseDto.class);

				if (responseDto != null && responseDto.getReturnDataSet() != null) {

					List<String> additionalLinesList = responseDto.getReturnDataSet();

					int additionalLinesCount = additionalLinesList.size();

					int tnIndex = userSession.getAdditionalLinesCounter();

					String hookReturnCode = "5";
					String hookReturnMessage = "More than one TN has been read";

					if (dmInput == 1) {
						
						tnIndex = tnIndex - 1;

					} else if (dmInput ==  2) {
						
						tnIndex = tnIndex - 2;

					} else if (dmInput ==  4) {
						
						tnIndex = 0;
					}

					if (tnIndex < additionalLinesCount) {
						
						// still has TN to read
						String tn = additionalLinesList.get(tnIndex);
						
						if (tn.equalsIgnoreCase(IVRConstants.ADDL_LINES_SPL_CKT)) {
							hookReturnCode = "1";
							hookReturnMessage = "Special ckt";

						} else if (tn.equalsIgnoreCase(IVRConstants.ADDL_LINES_UDC_CKT)) {
							
							hookReturnCode = "2";
							hookReturnMessage = "UDC ckt";
						} else {
							
							hookReturnCode = "3";
							hookReturnMessage = "TN";
							List<IVRParameter> paramterData = ivrLfacsServiceHelper.addParamterData(tn);
							response.setParameters(paramterData);
						}
						tnIndex++;

					} else {
						// no more Tns to read
						if (additionalLinesCount == 1) {
							hookReturnCode = "4";
							hookReturnMessage = "Only One TN has been Read";
						}
					}

					userSession.setAdditionalLinesCounter(tnIndex);

					cacheService.updateSession(userSession);

					response.setHookReturnMessage(hookReturnMessage);
					response.setHookReturnCode(hookReturnCode);

					return response;
				}
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return response;
	}

	/**
	 * Check the Additional Lines Inquiry Results Return 4 - No other Circuits
	 * Return 8 - There are ADLs to speak
	 */
	@Override
	public IVRWebHookResponseDto processAdditonalLinesVoiceFID615(String sessionId, String currentState) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		String hookReturnCode = GPDOWN_ERR_MSG_CODE;
		String hookReturnMessage = GPDOWN_ERR_MSG;

		IVRUserSession session = null;
		try {
			session = cacheService.getBySessionId(sessionId);
			if (session != null) {
				String additionalLinesResponse = session.getAdditionalLinesResponse();
				AdditionalLinesReportResponseDto addLinesResp = objectMapper.readValue(additionalLinesResponse,
						AdditionalLinesReportResponseDto.class);
				if (ivrLfacsServiceHelper.getNumberOfAddlLines(addLinesResp) > 0) {
						hookReturnCode = HOOK_RETURN_8;
						hookReturnMessage = IVRConstants.FID615_MSG_8;
					} else {
						hookReturnCode = HOOK_RETURN_5;
						hookReturnMessage = IVRConstants.FID615_MSG_5;

					}

			} else {
				hookReturnMessage = INVALID_SESSION_ID;
			}

		} catch (Exception e) {
			LOGGER.error("Error in Additional Lines By Voice FID615 :", e);
		}

		cacheService.updateSession(session);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;
	}

	/**
	 * Get the number of additional lines (exclude the inquired tn.) If one
	 * additional Line -> return 1 
	 * else if more than one additional lines --> return 2
	 */
	@Override
	public IVRWebHookResponseDto processAdditonalLinesVoiceFID625(String sessionId, String currentState) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		String hookReturnCode = GPDOWN_ERR_MSG_CODE;
		String hookReturnMessage = GPDOWN_ERR_MSG;

		IVRUserSession session = null;
		try {
			session = cacheService.getBySessionId(sessionId);
			if (session != null) {

				String additionalLinesResponse = session.getAdditionalLinesResponse();
				AdditionalLinesReportResponseDto addLinesResp = objectMapper.readValue(additionalLinesResponse,
						AdditionalLinesReportResponseDto.class);
				
				int countADL = ivrLfacsServiceHelper.getNumberOfAddlLines(addLinesResp);
				if (countADL == 1) {
					hookReturnCode = HOOK_RETURN_1;
					hookReturnMessage = FID625_MSG_1;
					response.setParameters(ivrLfacsServiceHelper.addParamterData(addLinesResp.getReturnDataSet().get(countADL - 1)));
					session.setAdditionalLinesCounter(countADL);
				} else {
					hookReturnCode = HOOK_RETURN_2;
					hookReturnMessage = FID625_MSG_2;
					response.setParameters(
							ivrLfacsServiceHelper.addParamterData(String.valueOf(countADL)));
				}

			} else {
				hookReturnMessage = INVALID_SESSION_ID;
			}
		} catch (Exception e) {
			LOGGER.error("Error in Additional Lines By Voice FID615 :", e);
		}

		cacheService.updateSession(session);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;
	}

	/**
	 * Check to see how many TNs have been read. Called from FI0630, FI633, FI0634
	 * If list is interrupted (0) , then this method is called
	 */
	@Override
	public IVRWebHookResponseDto processAdditonalLinesVoiceFID635(String sessionId) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(IVRConstants.STATE_FID635);
		String hookReturnCode = GPDOWN_ERR_MSG_CODE;
		String hookReturnMessage = GPDOWN_ERR_MSG;
		IVRUserSession session = null;
		try {
			session = cacheService.getBySessionId(sessionId);
			if (session != null) {
				int count = session.getAdditionalLinesCounter();
				if (count == 1) {
					hookReturnCode = HOOK_RETURN_1;
					hookReturnMessage = IVRConstants.FID635_HK_1_MSG;
				} else if (count > 1) {
					hookReturnCode = HOOK_RETURN_2;
					hookReturnMessage = IVRConstants.FID635_HK_2_MSG;
				}
			} else {
				hookReturnMessage = INVALID_SESSION_ID;
			}
		} catch (Exception e) {
			LOGGER.error("Error in FID635 :", e);
		}

		LOGGER.info("Session: " + sessionId + ", hookReturnCode: " + hookReturnCode + ", hookReturnMessage: "
				+ hookReturnMessage);
		cacheService.updateSession(session);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFID273Code(String sessionId, String nextState, List<String> userInputDTMFList) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnMessage = "";
		
		IVRUserSession userSession = cacheService.getBySessionId(sessionId);

		if (userSession != null) {
			try {
				CurrentAssignmentResponseDto currentAssignmentResponseDto = objectMapper
						.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
				// Set segemntNo 0
				String responseString = getMultipleAppearaceJsonResponse(userSession, 0, sessionId,currentAssignmentResponseDto);

				MultipleAppearanceResponseDto responseObject = objectMapper.readValue(responseString,
						MultipleAppearanceResponseDto.class);

				if (responseObject != null) {
					userSession.setMultipleAppearanceResponse(responseString);
				}
					response.setHookReturnCode(HOOK_RETURN_2);
					response.setHookReturnMessage(NO_ALPHA_PAGER);

					String pageText = ivrLfacsPagerText.getPageMultipleAppearences(userSession,
							currentAssignmentResponseDto, responseObject, 0);
					if (userSession.isCanBePagedMobile()) {
						ivrLfacsServiceHelper.sendTestResultToTech(NETPAGE_SUBJECT_MULTIPLE_APPEARANCES, pageText,
								NET_PHONE_DEVICE, userSession);
						response.setHookReturnCode(HOOK_RETURN_3);
						response.setHookReturnMessage(SUCCESS);
					}

					if (userSession.isCanBePagedEmail()) {
						ivrLfacsServiceHelper.sendTestResultToTech(NETPAGE_SUBJECT_MULTIPLE_APPEARANCES, pageText,
								NET_MAIL_DEVICE, userSession);
						response.setHookReturnCode(HOOK_RETURN_3);
						response.setHookReturnMessage(SUCCESS);
					}
				

			} catch (Exception e) {
				response.setHookReturnCode(HOOK_RETURN_1);
				response.setHookReturnMessage(GPDOWN_ERR_MSG);
				LOGGER.error("FID273 Exception stack trace: ", e);
			}
		} else {
			hookReturnMessage = IVRConstants.INVALID_SESSION_ID;
			response.setHookReturnCode(HOOK_RETURN);
			response.setHookReturnMessage(hookReturnMessage);
			LOGGER.info("FID273 Status :" + IVRLfacsConstants.INVALID, sessionId);
		}
		cacheService.updateSession(userSession);
		return response;
	}

	@Override
	public IVRWebHookResponseDto processFID282Code(String sessionId, String nextState) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = HOOK_RETURN_1;
		String hookReturnMessage = GPDOWN_ERR_MSG;
		String pagerTextMob = null;
		String pagerTextEmail = null;

		IVRUserSession userSession = cacheService.getBySessionId(sessionId);

		if (userSession != null) {

			try {
				DefectivePairsRequestDto dto = new DefectivePairsRequestDto();
				Map<String, Object> reqAndRespMap = getDefectivePairsRequestAndResponse(userSession, dto);

				IVRHttpResponseDto ivrHttpResponseDto = (IVRHttpResponseDto) reqAndRespMap.get("response");
				DefectivePairsResponseDto responseObject = objectMapper.readValue(
						ivrHttpClient.cleanResponseString(ivrHttpResponseDto.getResponseBody()),
						DefectivePairsResponseDto.class);

				DefectivePairsRequestDto requestDto = (DefectivePairsRequestDto) reqAndRespMap.get("request");

				if (responseObject != null && responseObject.getMessageStatus().getErrorStatus()
						.equalsIgnoreCase(IVRConstants.WRAPPER_API_ERROR_STATUS_SUCCESS)) {
					userSession.setDefectivePairResponse(ivrHttpResponseDto.getResponseBody());
					hookReturnCode = HOOK_RETURN_0;
					hookReturnMessage = "Success";

					if (userSession.isCanBePagedMobile()) {
						pagerTextMob = ivrLfacsPagerText.formatDefectivePairsPagerTextForMobile(responseObject,
								requestDto.getInputData().getCableId(),
								String.valueOf(requestDto.getInputData().getCablePairRange().getLowPair()));
					}
					if (userSession.isCanBePagedEmail()) {
						Optional<String> optionalLastName = fastIvrMnetRepository
								.findLastNameByCuid(userSession.getCuid());
						String lastName = "";
						if (optionalLastName.isPresent()) {
							lastName = optionalLastName.get();
						}
						pagerTextEmail = ivrLfacsPagerText.formatDefectivePairsPagerTextForEmail(responseObject,
								requestDto, lastName);
					}
				} else {
					CSLPage cslPage = new CSLPage();
					cslPage.setReqType("DPR");
					cslPage.setReqCable(requestDto.getInputData().getCableId());
					cslPage.setReqPair(String.valueOf(requestDto.getInputData().getCablePairRange().getLowPair()));
					pagerTextMob = cslPage.FormatError();
					pagerTextEmail = pagerTextMob;
				}

				if (userSession.isCanBePagedMobile()) {
					ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_DEFECTIVE_PAIRS,
							pagerTextMob, NET_PHONE_DEVICE, userSession);
				}
				if (userSession.isCanBePagedEmail()) {
					ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_DEFECTIVE_PAIRS,
							pagerTextEmail, NET_MAIL_DEVICE, userSession);
				}

			} catch (Exception e) {
				LOGGER.error("Exception stack trace: ", e);
			}

		}
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		cacheService.updateSession(userSession);
		return response;
	}

	private Map<String, Object> getDefectivePairsRequestAndResponse(IVRUserSession userSession,
			DefectivePairsRequestDto requestObject) throws JsonMappingException, JsonProcessingException {

		Map<String, Object> map = new HashMap<>();
		TNInfoResponse losDbResponse = ivrLfacsServiceHelper
				.extractTNInfoFromLosDBResponse(userSession.getLosDbResponse());
		if (losDbResponse != null && StringUtils.isNotBlank(userSession.getCurrentAssignmentResponse())) {
			String employeeCode = StringUtils.isNotBlank(userSession.getEc()) ? userSession.getEc()
					: IVRConstants.WRAPPER_API_DEFAULT_EMPLOYEE_ID;

			requestObject = ivrLfacsServiceHelper.buildDefectivePairsRequest(userSession.getCable(), losDbResponse.getPrimaryNPA(),
					losDbResponse.getPrimaryNXX(), employeeCode, userSession.getPair());
			map.put("request", requestObject);

			IVRHttpResponseDto ivrHttpResponseDto;
			try {
				ivrHttpResponseDto = ivrHttpClient.httpPostApiCall(objectMapper.writeValueAsString(requestObject),
						defectivePairsUrl, userSession.getSessionId(), DEFECTIVE_PAIRS);
			} catch (JsonProcessingException | HttpTimeoutException e) {
				ivrHttpResponseDto = new IVRHttpResponseDto();
				ivrHttpResponseDto.setStatusCode(HttpStatus.REQUEST_TIMEOUT.value());
				e.printStackTrace();
			}

			LOGGER.info("Session id:" + userSession.getSessionId() + ", Response from Defective Pairs API: "
					+ ivrHttpResponseDto);
			map.put("response", ivrHttpResponseDto);
			return map;
		} else {
			throw new BusinessException(IVRConstants.CANNOT_FETCH_DEFECTIVE_PAIR_API_LOSDB_NULL);
		}
	}

	/**
	 * Spare pair flow by Cable Pair 
	 */
	@Override
	public IVRWebHookResponseDto processFID272Code(String sessionId, String nextState, List<String> userInputDTMFList) {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = "";
		String hookReturnMessage = "";

		try {
			IVRUserSession userSession = cacheService.getBySessionId(sessionId);

			CurrentAssignmentResponseDto currentAssignmentResponseDto = null;

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

				currentAssignmentResponseDto = objectMapper.readValue(userSession.getCurrentAssignmentResponse(),
						CurrentAssignmentResponseDto.class);
				//default values
				int segNo = ivrLfacsServiceHelper.getSegmentFromCablePair(currentAssignmentResponseDto, userSession.getCable(), userSession.getPair());
				LOGGER.info("Session:" + sessionId +", Spare Pair by Cable Pair: TN:" + userSession.getInquiredTn()+ " Segment identified:"+ segNo);
				int segSize = 1;
				if (ivrLfacsServiceHelper.isSeviceOrder(currentAssignmentResponseDto)) {
					
					processRetrieveLoopAssignmentInquiry(sessionId, segNo, segSize, userSession,
							currentAssignmentResponseDto, tn, primaryNpa, primaryNxx);
				
				} else {

					processRetrieveMaintenanceTicketChangeInquiry(sessionId, segNo, segSize, userSession,
							currentAssignmentResponseDto, tn, primaryNpa, primaryNxx);
				
				}

				//For Cut page issue 
				String pageText = ivrLfacsPagerText.formatPage(tn);
				
				if (!userSession.isCutPageSent()) {

					userSession.setCutPageSent(true);
					ivrLfacsServiceHelper.issueCutPageSent(userSession);
				}
				
				if(userSession.isCanBePagedMobile()) {	
					
					ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_SPARE_PAIR, pageText,
							NET_PHONE_DEVICE, userSession);

					hookReturnCode = HOOK_RETURN_3;
					hookReturnMessage = "Alpha pager";
				} else {

					ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_SPARE_PAIR, pageText,
							NET_MAIL_DEVICE, userSession);

					hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_2;
					hookReturnMessage = "No Alpha Pager";
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

	// #Note : This method only written for the case of FI0095 to FID700 (In other
	// case use processFID090(#,#) for #FID700)
	@Override
	public IVRWebHookResponseDto processFID700Code(String sessionId, String nextState)
			throws JsonMappingException, JsonProcessingException {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = HOOK_RETURN_1;
		String hookReturnMessage = GPDOWN_ERR_MSG;

		IVRUserSession userSession = cacheService.getBySessionId(sessionId);

		if (userSession != null) {

			CentralOfficeEquipmentResponseDto responseObject = objectMapper.readValue(
					ivrHttpClient.cleanResponseString(userSession.getCentralOfficeEquipmentResponse()), CentralOfficeEquipmentResponseDto.class);

			String pageResult = ivrLfacsPagerText.getCentralOfficePageText(response, userSession, responseObject);
			// If in FID090 it will check alpha pager is true then it's passed to FI0095
			// from there it's passed to FID700. So no need to check Alpha Pager again, directly
			// passing NET_PHONE_DEVICE
			ivrLfacsServiceHelper.sendTestResultToTech(NETPAGE_SUBJECT_COE, pageResult, NET_PHONE_DEVICE, userSession);

			if (StringUtils.isNotBlank(responseObject.getReturnDataSet().getSwitchNetworkUnitId())) {
				List<IVRParameter> parameterList = new ArrayList<>();
				parameterList.add(new IVRParameter(responseObject.getReturnDataSet().getSwitchNetworkUnitId()));
				response.setParameters(parameterList);
				hookReturnCode = HOOK_RETURN_8;
				hookReturnMessage = "OE Found";
			} else {
				hookReturnCode = HOOK_RETURN_5;
				hookReturnMessage = "Unable to find OE";
			}
		}
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;
	}
	
	private int processTN(List<String> additionalLinesList, int tnIndex, IVRUserSession userSession, IVRWebHookResponseDto response) {
		
		String telePhoneNo = additionalLinesList.get(tnIndex);

		if (telePhoneNo.contains(IVRConstants.ADDL_LINES_UDC_CKT)) {

			response.setHookReturnCode(HOOK_RETURN_2);
			response.setHookReturnMessage("UDC Circuit");
		} else if (telePhoneNo.contains(IVRConstants.ADDL_LINES_SPL_CKT)) {

			response.setHookReturnCode(HOOK_RETURN_1);
			response.setHookReturnMessage("Special Circuit");
		} else {

			response.setHookReturnCode(HOOK_RETURN_3);
			response.setHookReturnMessage("Read TN");
			List<IVRParameter> ivrParameterList = new ArrayList<IVRParameter>();

			IVRParameter ivrParameter = new IVRParameter();
			ivrParameter.setData(telePhoneNo);

			ivrParameterList.add(ivrParameter);
			response.setParameters(ivrParameterList);
		}
		
		tnIndex++;
		
		return tnIndex;
	}
	
	private void resetSessionCounter(IVRUserSession ivrUserSession) {
		
		ivrUserSession.setAdditionalLinesCounter(0);
		ivrUserSession.setSegmentRead(null);
		ivrUserSession.setCandPairCounter(-1);
		cacheService.updateSession(ivrUserSession);
	}
}