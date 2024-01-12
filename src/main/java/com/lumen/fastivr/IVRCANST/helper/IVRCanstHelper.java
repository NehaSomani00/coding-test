package com.lumen.fastivr.IVRCANST.helper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCANST.Dto.*;
import com.lumen.fastivr.IVRCANST.entity.IVRCanstEntity;
import com.lumen.fastivr.IVRCNF.helper.IVRCnfHelper;
import com.lumen.fastivr.IVRCNF.repository.IVRCnfCacheService;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.*;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.IVRUtils.IVRLfacsConstants;
import com.lumen.fastivr.httpclient.IVRHttpClient;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.lumen.fastivr.IVRCANST.utils.IVRCANSTConstants.*;
import static com.lumen.fastivr.IVRUtils.IVRConstants.FASTIVR_BACKEND_ERR;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INQUIRY_BY_CABLE_PAIR;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN;


@Service
public class IVRCanstHelper {

	final static Logger LOGGER = LoggerFactory.getLogger(IVRCanstHelper.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private IVRCacheService cacheService;
	
	@Autowired
	private IVRCnfCacheService ivrCnfCacheService;   
	
	@Autowired
	private IVRHttpClient ivrHttpClient;
	
	@Autowired
	private IVRCnfHelper ivrCnfHelper;

	@Autowired
	private IVRLfacsServiceHelper ivrLfacsServiceHelper;

	@Value("#{${inputCodes}}")
	Map<String, String> inputCodes = new HashMap<String, String>();

	public String convertInputCodesToAlphabets(String postFixNo) {

		String convertionString = null;

		int count = StringUtils.countMatches(postFixNo, "*");

		if (count % 2 != 0) {

			return null;
		}

		if (count > 2) {

			List<Integer> indexes = IntStream
					.iterate(postFixNo.indexOf('*'), index -> index >= 0, index -> postFixNo.indexOf('*', index + 1))
					.boxed().collect(Collectors.toList());

			for (int i = 0; i < indexes.size();) {

				String convertionValue = convertAlphabets(postFixNo, indexes.get(i), indexes.get(i + 1));

				if (convertionValue != null && convertionString != null) {

					if (indexes.size() > (i + 2)) {

						String nonConvertionValue = postFixNo.substring(indexes.get(i + 1) + 1, indexes.get(i + 2));

						convertionString = convertionString + convertionValue + nonConvertionValue;
					} else {

						convertionString = convertionString + convertionValue;
					}

				} else if (convertionString == null && convertionValue != null) {

					if (indexes.size() > (i + 2)) {

						String nonConvertionValue = postFixNo.substring(indexes.get(i + 1) + 1, indexes.get(i + 2));

						convertionString = convertionValue + nonConvertionValue;
					} else {

						convertionString = convertionValue;
					}

				} else if (convertionValue == null) {

					convertionString = null;
					break;
				}

				i = i + 2;
			}
		} else {

			int firstIndex = postFixNo.indexOf("*");

			int lastIndex = postFixNo.lastIndexOf("*");

			convertionString = convertAlphabets(postFixNo, firstIndex, lastIndex);
		}

		return convertionString;
	}

	public String convertAlphabets(String postFixNo, int firstIndex, int lastIndex) {

		try {

			StringBuilder serviceOrderNo = new StringBuilder();

			if (firstIndex == lastIndex) {

				return null;
			}

			String convertAlpha = postFixNo.substring((firstIndex + 1), lastIndex);

			if (convertAlpha.length() % 2 != 0) {

				return null;
			}

			int i = 0;

			while (i < convertAlpha.length()) {

				String values = inputCodes.get(convertAlpha.substring(i, (i + 2)));

				if (StringUtils.isBlank(values)) {

					return null;
				}

				serviceOrderNo.append(values);

				i = i + 2;
			}

			return serviceOrderNo.toString();
		} catch (Exception e) {

			return null;
		}
	}

	public OrderStatusRequest buildOrderStatusRequest(String primaryNpa, String primaryNxx, IVRCanstEntity canstSession,
			IVRUserSession userSession) {

		OrderStatusRequest orderStatusRequest = new OrderStatusRequest();

		OrderStatusInputData inputData = new OrderStatusInputData();

		inputData.setLFACSEmployeeCode(userSession.getEmpID());

		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa(primaryNpa);
		wireCtrPrimaryNPANXX.setNxx(primaryNxx);

		inputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		inputData.setServiceOrderNumber(canstSession.getServiceOrderNo());

		orderStatusRequest.setInputData(inputData);		
		return orderStatusRequest;
	}

	public AssignServiceOrderRequest buildAssignServiceOrderRequest(String tn, String primaryNpa, String primaryNxx,
			String empId, String sessionId, IVRCanstEntity canstSession, IVRUserSession userSession)
			throws JsonMappingException, JsonProcessingException {

		AssignServiceOrderRequest assignServiceOrderRequest = new AssignServiceOrderRequest();

		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;

		if (userSession != null && userSession.getCurrentAssignmentResponse() != null) {

			currentAssignmentResponseDto = objectMapper.readValue(userSession.getCurrentAssignmentResponse(),
					CurrentAssignmentResponseDto.class);
		}

		// List<SEG> segList = ivrCnfHelper.extracted(currentAssignmentResponseDto);

		CurrentAssignmentInfo currentAssignmentInfo = new CurrentAssignmentInfo();

		if (StringUtils.isNotBlank(tn)) {

			TN TN = new TN();

			TN.setCkid(tn);

			currentAssignmentInfo.setTn(TN);
		}

		AssignServiceOrderRequestInputData inputData = new AssignServiceOrderRequestInputData();

		inputData.setLFACSEmployeeCode(empId);
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa(primaryNpa);
		wireCtrPrimaryNPANXX.setNxx(primaryNxx);

		inputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		assignServiceOrderRequest.setInputData(inputData);

		inputData.setLfacsEntityCode("A"); // TODO what to set??

		inputData.setCircuitId(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID());
		inputData.setServiceOrderNumber(canstSession.getServiceOrderNo());
		inputData.setTerminalId(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getTID()); // // TODO
																											// WHAT to
																											// set???

		assignServiceOrderRequest.setInputData(inputData);

		//////
//		assignServiceOrderRequest.setRequestId("FASTFAST");
//		assignServiceOrderRequest.setWebServiceName("SIABusService");
//		assignServiceOrderRequest.setRequestPurpose("TS");
//		AuthorizationInfo authInfo = new AuthorizationInfo();
//		authInfo.setUserid("fasfast");
//		authInfo.setPassword("9312qrty!");
//		assignServiceOrderRequest.setAuthorizationInfo(authInfo);
//		TargetSchemaVersionUsed schema = new TargetSchemaVersionUsed();
//		schema.setMajorVersionNumber(0);
//		schema.setMinorVersionNumber(0);
//		assignServiceOrderRequest.setTargetSchemaVersionUsed(schema);
//		assignServiceOrderRequest.setTimeOutSecond(180);

		return assignServiceOrderRequest;
	}

	public IVRWebHookResponseDto findFastivrError(String sessionId, String nextState, MessageStatus messageStatus) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = "";
		String hookReturnMessage = "";

		if (IVRLfacsConstants.ERR_500.equals(messageStatus.getErrorCode())) {
			hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_1;
			hookReturnMessage = IVRLfacsConstants.SYSTEM_DOWN_ERR;
		} else if (IVRLfacsConstants.ERR_504.equals(messageStatus.getErrorCode())) {
			hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_2;
			hookReturnMessage = IVRLfacsConstants.NOT_RESPONDING_ERR;

		} else if (IVRLfacsConstants.ERR_503.equals(messageStatus.getErrorCode())) {

			hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_3;
			hookReturnMessage = IVRLfacsConstants.NOT_AVAILABLE_ERR;

		} else if (IVRLfacsConstants.ERR_400.equals(messageStatus.getErrorCode())) {

			hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_4;
			hookReturnMessage = IVRLfacsConstants.TRANSACTION_FAILED_ERR;

		} else {

			hookReturnMessage = FASTIVR_BACKEND_ERR;
		}

		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info(hookReturnMessage, sessionId);
		return response;
	}

	public int getSegmentList(CurrentAssignmentResponseDto currentAssignmentResponseDto) {

		List<SEG> segList = null;

		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()) {

			if (currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG() != null
					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()) {

				segList = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG();
			} else if (currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null
					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty()
					&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG() != null
					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG()
							.isEmpty()) {

				segList = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG();
			}
		}
		return segList != null ? segList.size() : 0;
	}

	public boolean isDPA(CurrentAssignmentResponseDto currentAssignmentResponseDto) {

		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()) {

			if (currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getTID() != null 
					&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getTID().equalsIgnoreCase("DPA")) {

				return true;
			} else if (currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null 
					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty()
					&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getTID() != null 
					&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getTID().equalsIgnoreCase("DPA")) {

				return true;
			}
		}
		return false;
	}

	public boolean isCKID(CurrentAssignmentResponseDto currentAssignmentResponseDto) {

		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()
				&& (StringUtils.isNotBlank(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID())
						|| (currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null
								&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty()
								&& StringUtils.isNotBlank(currentAssignmentResponseDto.getReturnDataSet().getLoop()
										.get(0).getSO().get(0).getCKID())))) {

			return true;
		}
		return false;
	}

	public boolean isTEA(CurrentAssignmentResponseDto currentAssignmentResponseDto) {

		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()) {

			if (currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG() != null 
					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()
					&& StringUtils.isNotBlank(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).getTEA())) {

				return true;
			} else if (currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null 
					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty()
					&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG() != null 
					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().isEmpty()
					&& StringUtils.isNotBlank(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().get(0).getTEA())) {

				return true;
			}
		}
		return false;
	}

	public String getSeviceOrder(CurrentAssignmentResponseDto currentAssignmentResponseDto) {

		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty()
				&& StringUtils.isNotBlank(
						currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getORD())) {

			return currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getORD();
		}

		return null;
	}

	public String getCKID(CurrentAssignmentResponseDto currentAssignmentResponseDto) {

		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()
				&& (StringUtils.isNotBlank(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID())
						|| (currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null
								&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty()
								&& StringUtils.isNotBlank(currentAssignmentResponseDto.getReturnDataSet().getLoop()
										.get(0).getSO().get(0).getCKID())))) {

			return currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID() != null
					? currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID()
					: currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getCKID();
		}
		return null;
	}


	public IVRWebHookResponseDto getColourCode(IVRWebHookResponseDto response,
											   CurrentAssignmentResponseDto currentAssignmentResponseDto, IVRCanstEntity userSession) {

		if (null != currentAssignmentResponseDto && null != currentAssignmentResponseDto.getReturnDataSet()
				&& null != currentAssignmentResponseDto.getReturnDataSet().getLoop()
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()) {

			String colourCode = null;

			String colourType = null;

			List<SEG> segmentList = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG();

			String segmentRead = userSession.getSegmentRead();

			if (StringUtils.isNotBlank(segmentRead) && F1.equalsIgnoreCase(segmentRead)) {

				colourCode = segmentList.get(0).getBP() != null ? segmentList.get(0).getBP().toUpperCase() : null;

				colourType = segmentList.get(0).getTP() != null ? segmentList.get(0).getTP().toUpperCase() : null;

			} else if ((StringUtils.isNotBlank(segmentRead) && F2.equalsIgnoreCase(segmentRead))) {

				colourCode = segmentList.get(1).getBP() != null ? segmentList.get(1).getBP().toUpperCase() : null;

				colourType = segmentList.get(1).getTP() != null ? segmentList.get(1).getTP().toUpperCase() : null;

			}
			else if ((StringUtils.isNotBlank(segmentRead) && F3.equalsIgnoreCase(segmentRead))) {

				colourCode = segmentList.get(2).getBP() != null ? segmentList.get(2).getBP().toUpperCase() : null;

				colourType = segmentList.get(2).getTP() != null ? segmentList.get(2).getTP().toUpperCase() : null;

			}


			return ivrLfacsServiceHelper.checkColourCode(colourCode, colourType, response);
		}

		response.setHookReturnCode(HOOK_RETURN);
		response.setHookReturnMessage(FASTIVR_BACKEND_ERR);

		return response;
	}

	public CurrentAssignmentRequestTnDto buildCurrentAssignmentInqRequest(String primaryNpa,
			String primaryNxx, List<String> userInputDTMFList, IVRUserSession userSession) {

		CurrentAssignmentRequestTnDto request = new CurrentAssignmentRequestTnDto();

		CurrentAssignmentInfo currentAssignmentInfo = new CurrentAssignmentInfo();


		if (INQUIRY_BY_CABLE_PAIR.equalsIgnoreCase(userSession.getFacsInqType())){

			CablePair cablePair = new CablePair();

			cablePair.setCa(userSession.getCable());
			cablePair.setPr(userSession.getPair());
			currentAssignmentInfo.setCablePair(cablePair);
		}

		InputData id = new InputData();
		id.setCurrentAssignmentInfo(currentAssignmentInfo);
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa(primaryNpa);
		wireCtrPrimaryNPANXX.setNxx(primaryNxx);
		id.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		request.setInputData(id);

//		request.setRequestId("FASTFAST");
//		request.setWebServiceName("SIABusService");
//		request.setRequestPurpose("TS");
//		AuthorizationInfo authInfo = new AuthorizationInfo();
//		authInfo.setUserid("fasfast");
//		authInfo.setPassword("9312qrty!");
//		request.setAuthorizationInfo(authInfo);
//		TargetSchemaVersionUsed schema = new TargetSchemaVersionUsed();
//		schema.setMajorVersionNumber(0);
//		schema.setMinorVersionNumber(0);
//		request.setTargetSchemaVersionUsed(schema);
//		request.setTimeOutSecond(180);

		return request;

	}


	public String getServingTerminal(CurrentAssignmentResponseDto currentAssignmentResponseDto, int segmentNo) {

		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()) {

			String servingTerminal = null;

			if (currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG() != null
					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()) {

				servingTerminal = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG()
						.get(segmentNo).getTEA();
//			} else if (currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null
//					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty()) {
//
//				servingTerminal = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(segmentNo).gettea
//			}
			}
			return servingTerminal;
		}

		return null;
	}

//	public CurrentAssignmentResponseDto getCurrentAssignment(IVRUserSession userSession) {
//
//		try {
//			// fetch TN from cache
//			String losDbResponseJsonString = userSession.getLosDbResponse();
//			TNInfoResponse losDbResponse = ivrLfacsServiceHelper
//					.extractTNInfoFromLosDBResponse(losDbResponseJsonString);
//			String tn = "";
//			String primaryNpa = "";
//			String primaryNxx = "";
//			if (losDbResponse != null) {
//				tn = losDbResponse.getTn();
//				primaryNpa = losDbResponse.getPrimaryNPA();
//				primaryNxx = losDbResponse.getPrimaryNXX();
//			} else {
//				throw new RuntimeException(CANNOT_FETCH_CURR_ASSG_API_LOSDB_NULL);
//			}
//
//			CurrentAssignmentRequestTnDto requestObject = ivrLfacsServiceHelper.buildCurrentAssignmentInqRequest(tn,
//					primaryNpa, primaryNxx, null, userSession);
//			String jsonRequest = objectMapper.writeValueAsString(requestObject);
//			String responseString = ivrLfacsServiceHelper.callCurrentAssignmentInquiryLfacs(jsonRequest, userSession);
//
//			responseString = ivrLfacsServiceHelper.cleanResponseString(responseString);
//			LOGGER.info("Session id:" + userSession.getSessionId() + ", Response from LFACS Current Assignment API: "
//					+ responseString);
//			// Validating it's a good response json string from lfacs
//			CurrentAssignmentResponseDto responseObject = objectMapper.readValue(responseString,
//					CurrentAssignmentResponseDto.class);
//			if (responseObject != null) {
//
//				return responseObject;
//			}
//		} catch (RuntimeException e) {
//
//			LOGGER.error("Exception stack trace: ", e);
//
//		} catch (Exception e) {
//
//			LOGGER.error("Exception stack trace: ", e);
//		}
//
//		return null;
//	}

	public ChangeLoopAssignmentRequest buildChangeLoopAssignmentRequest(String tn, String primaryNpa, String primaryNxx,
			String sessionId, IVRCanstEntity canstSession, IVRUserSession ivrUserSession) throws JsonMappingException, JsonProcessingException 
	{
		
		ChangeLoopAssignmentRequest request = new ChangeLoopAssignmentRequest();
		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		CurrentAssignmentInfo currentAssignmentInfo = new CurrentAssignmentInfo();

		if (ivrUserSession != null && ivrUserSession.getCurrentAssignmentResponse() != null) {
			currentAssignmentResponseDto = objectMapper.readValue(ivrUserSession.getCurrentAssignmentResponse(),CurrentAssignmentResponseDto.class);
		}

		List<SEG> segList = ivrCnfHelper.extracted(currentAssignmentResponseDto);		
		if (StringUtils.isNotBlank(tn)) {
			TN TN = new TN();
			TN.setCkid(tn);
			currentAssignmentInfo.setTn(TN);
		}	
		
		ChangeLoopAssignmentInputData inputData =  new ChangeLoopAssignmentInputData();
		
		int segmentNumber = getSegmentNumber(canstSession);	
		inputData.setSegNumber(String.valueOf(segmentNumber+1));
		
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa(primaryNpa);
		wireCtrPrimaryNPANXX.setNxx(primaryNxx);
		inputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		
		ChangeLoopAssignmentCurrentLoopDetails currentLoopDetails = new ChangeLoopAssignmentCurrentLoopDetails();	
		SEG segment = segList.get(segmentNumber);
		currentLoopDetails.setCableId(segment.getCA());
		currentLoopDetails.setCableUnitId(segment.getPR());
		currentLoopDetails.setTerminalId(segment.getTEA());
		inputData.setCurrentLoopDetails(currentLoopDetails);	
		
		ChangeLoopAssignmentReplacementLoopDetails replacementLoopDetails = new ChangeLoopAssignmentReplacementLoopDetails();
		replacementLoopDetails.setCableId(canstSession.getCable());
		replacementLoopDetails.setCableUnitId(canstSession.getPair());
		inputData.setReplacementLoopDetails(replacementLoopDetails);
		
		inputData.setLFACSEmployeeCode(ivrUserSession.getEmpID());
		inputData.setServiceOrderNumber(canstSession.getServiceOrderNo());
		inputData.setCircuitId(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID());
		inputData.setAutoSelectionFlag(true);
		inputData.setCableTroubleTicketIdentifier(canstSession.getTroubleTicketNo());
		inputData.setFacilityChangeReasonCode("NDC");
		inputData.setChangeActionCode("CUTFAINSTREP"); 
		
		request.setInputData(inputData);
		request.setRequestId("FASTFAST");
		request.setWebServiceName("SIABusService");
		request.setRequestPurpose("TS");
		AuthorizationInfo authInfo = new AuthorizationInfo();
		authInfo.setUserid("fasfast");
		authInfo.setPassword("9312qrty!");
		request.setAuthorizationInfo(authInfo);
		TargetSchemaVersionUsed schema = new TargetSchemaVersionUsed();
		schema.setMajorVersionNumber(0);
		schema.setMinorVersionNumber(0);
		request.setTargetSchemaVersionUsed(schema);
		request.setTimeOutSecond(180);

		return request;
	}



	public UpdateLoopRequestDto buildUpdateLoopAssignmentRequest(String primaryNpa, String primaryNxx,
																		   IVRUserSession userSession, IVRCanstEntity canstEntity) throws JsonProcessingException {

		UpdateLoopRequestDto request = new UpdateLoopRequestDto();

		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		if (null != userSession && null != userSession.getCurrentAssignmentResponse()) {

			currentAssignmentResponseDto = objectMapper.readValue(userSession.getCurrentAssignmentResponse(),
					CurrentAssignmentResponseDto.class);
		}


		UpdateLoopRequestInputData inputData =  new UpdateLoopRequestInputData();

		if(null != userSession) {
			inputData.setLFACSEmployeeCode(userSession.getEmpID());
		}

		if(null != canstEntity) {
			inputData.setNewTerminalAddress(canstEntity.getNewTea());
			inputData.setCurrentTerminalAddress(canstEntity.getOldTea());
		}
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa(primaryNpa);
		wireCtrPrimaryNPANXX.setNxx(primaryNxx);

		inputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		inputData.setFacilityAvailabilityFlag(Boolean.TRUE);
		inputData.setNonPublishIndicator("Y");

if(null != currentAssignmentResponseDto && null != currentAssignmentResponseDto.getReturnDataSet() &&
null != currentAssignmentResponseDto.getReturnDataSet().getLoop() &&  currentAssignmentResponseDto.getReturnDataSet().getLoop().size() > 0 ) {

	inputData.setCircuitId(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID());

}
request.setInputData(inputData);


		return request;
	}



	public int getSegmentNumber(IVRCanstEntity canstSession) {
		
		String segmentRead = canstSession.getSegmentRead();
		int segmentNumber = 0;
		if ("F1".equalsIgnoreCase(segmentRead)) {
			
		} else if ("F2".equalsIgnoreCase(segmentRead)) {
		
			segmentNumber = 1;
		} else if ("F3".equalsIgnoreCase(segmentRead)) {
		
			segmentNumber = 2;
		}
		return segmentNumber;
	}

	
	public String generateCableTroubleTicket(String ec) {
		
		LocalDateTime time = LocalDateTime.now();
		
		return ec + time.getHour() + time.getMinute() + time.getSecond();
	}

}
