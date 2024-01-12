package com.lumen.fastivr.IVRCNF.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCNF.Dto.InputData;
import com.lumen.fastivr.IVRCNF.Dto.*;
import com.lumen.fastivr.IVRCNF.entity.IVRCnfEntity;
import com.lumen.fastivr.IVRCNF.repository.IVRCnfCacheService;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.*;
import com.lumen.fastivr.IVRDto.LOSDB.CTelephone;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetMaintChangeTicketInputData;
import com.lumen.fastivr.IVRDto.TN;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketRequest;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetLoopAssigInputData;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentRequest;
import com.lumen.fastivr.IVRLFACS.CTelephoneBuilder;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRSignon.IVRSignOnServiceHelper;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.IVRUtils.IVRLfacsConstants;
import com.lumen.fastivr.httpclient.IVRHttpClient;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.VERIFY_CUT_REQUEST;
import static com.lumen.fastivr.IVRUtils.IVRConstants.CANNOT_FETCH_CURR_ASSG_API_LOSDB_NULL;
import static com.lumen.fastivr.IVRUtils.IVRConstants.FASTIVR_BACKEND_ERR;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.*;

@Service
public class IVRCnfHelper {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private IVRCacheService cacheService;
	
	@Autowired
	private IVRCnfCacheService ivrCnfCacheService;
	
	@Autowired
	private IVRHttpClient ivrHttpClient;

	@Autowired
	private IVRLfacsServiceHelper ivrLfacsServiceHelper;

	@Autowired
	private IVRSignOnServiceHelper ivrSignOnServiceHelper;

	@Value("#{${inputCodes}}")
	Map<String, String> inputCodes = new HashMap<String, String>();
	
	@Value("${cnf.verify.cut.service.url}")
	private String verifyCutServiceUrl;

	final static Logger LOGGER = LoggerFactory.getLogger(IVRCnfHelper.class);

	public String convertInputCodesToAlphabets(String postFixNo) {
		
		String convertionString = null;
		
		int count = StringUtils.countMatches(postFixNo, "*");
		
		if(count % 2 != 0) {
			
			return null;
		}
		
		if(count > 2) {
			
			List<Integer> indexes = IntStream.iterate(postFixNo.indexOf('*'), index -> index >= 0, index -> postFixNo.indexOf('*', index + 1)).boxed().collect(Collectors.toList());
			
			for(int i = 0; i < indexes.size();) {
				
				String convertionValue = convertAlphabets(postFixNo, indexes.get(i), indexes.get(i + 1));
				
				if(convertionValue != null && convertionString != null) {
					
					if(indexes.size() > (i+2)) {
						
						String nonConvertionValue = postFixNo.substring(indexes.get(i + 1) + 1, indexes.get(i + 2));
						
						convertionString = convertionString + convertionValue + nonConvertionValue ;
					} else {
						
						convertionString = convertionString + convertionValue;
					}
					
				} else if (convertionString == null && convertionValue != null) {
					
					if(indexes.size() > (i+2)) {
						
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
			
			if(firstIndex == lastIndex) {
				
				return null;
			}
			
			String convertAlpha = postFixNo.substring((firstIndex + 1), lastIndex);

			if (convertAlpha.length() % 2 != 0) {

				return null;
			}

			int i = 0;

			while (i < convertAlpha.length()) {
				
				String values = inputCodes.get(convertAlpha.substring(i, (i + 2)));
				
				if(StringUtils.isBlank(values)) {
					
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

	public boolean isSwitchAvailable(IVRUserSession userSession) throws JsonMappingException, JsonProcessingException {

		boolean flag = false;
		
		String losDbResponseJsonString = userSession.getLosDbResponse();

		TNInfoResponse losDbResponse = ivrLfacsServiceHelper.extractTNInfoFromLosDBResponse(losDbResponseJsonString);

		if (losDbResponse != null) {

			CTelephone telphone = CTelephoneBuilder.newBuilder(userSession).setTelephone(losDbResponse.getTn()).build();

			String npa = telphone.getNpa();

			if (npa != null && npa.equalsIgnoreCase(userSession.getNpaPrefix())) {

				flag = true;
			} else {

				if (ivrSignOnServiceHelper.isNpaPresentInDB(npa)) {

					flag = true;
				}
			}

			if (flag) {

				String sessionId = userSession.getSessionId();

				IVRCnfEntity cnfSession = ivrCnfCacheService.getBySessionId(sessionId);

				VerifyCutInformationRequest request = buildVerifyCutRequest(losDbResponse.getTn(), npa,
						losDbResponse.getPrimaryNXX(), cnfSession);

				String jsonRequest = objectMapper.writeValueAsString(request);

				String verifyResultJson = ivrHttpClient.httpPostCall(jsonRequest, verifyCutServiceUrl, sessionId,
						VERIFY_CUT_REQUEST);

				String cleanJsonStr = ivrLfacsServiceHelper.cleanResponseString(verifyResultJson);

				cnfSession.setGetVerifyCutResponse(cleanJsonStr);

				VerifyCutInformationResponse responseObject = objectMapper.readValue(cleanJsonStr,
						VerifyCutInformationResponse.class);

				if (responseObject != null && responseObject.getMessageStatus() != null
						&& "S".equalsIgnoreCase(responseObject.getMessageStatus().getErrorStatus())) {
					
					ivrCnfCacheService.updateSession(cnfSession);
					
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	public void insertCurrentAssignment(IVRUserSession userSession) {

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
			String responseString = ivrLfacsServiceHelper.callCurrentAssignmentInquiryLfacs(jsonRequest, userSession);

			responseString = ivrLfacsServiceHelper.cleanResponseString(responseString);
			LOGGER.info("Session id:" + userSession.getSessionId() + ", Response from LFACS Current Assignment API: "
					+ responseString);
			// Validating it's a good response json string from lfacs
			CurrentAssignmentResponseDto responseObject = objectMapper.readValue(responseString,
					CurrentAssignmentResponseDto.class);
			if (responseObject != null) {
				// String was getting > 4000 bytes, cannot store in VARCHAR2
				// use CURR_ASSG_RESP_CLOB to load and fetch the data
				userSession.setCurrentAssignmentResponse(responseString);
				cacheService.updateSession(userSession);
			}
		} catch (RuntimeException e) {

			LOGGER.error("Exception stack trace: ", e);

		} catch (Exception e) {

			LOGGER.error("Exception stack trace: ", e);
		}
	}

	public MainChangeTicketLoopRequest buildMainChangeTicketLoopRequest(String tn, String primaryNpa, String primaryNxx, String empId, IVRCnfEntity cnfSession, IVRUserSession userSession, String cable, String pair) throws JsonMappingException, JsonProcessingException {

		MainChangeTicketLoopRequest request = new MainChangeTicketLoopRequest();

		CurrentAssignmentInfo currentAssignmentInfo = new CurrentAssignmentInfo();

		if (StringUtils.isNotBlank(tn)) {

			TN TN = new TN();

			TN.setCkid(tn);

			currentAssignmentInfo.setTn(TN);
		}

		MainChangeTicketLoopInputRequest id = new MainChangeTicketLoopInputRequest();

		id.setEmployeeId(empId);
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa(primaryNpa);
		wireCtrPrimaryNPANXX.setNxx(primaryNxx);
		id.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		
		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;

		if (userSession != null && userSession.getCurrentAssignmentResponse() != null) {

			currentAssignmentResponseDto = objectMapper.readValue(userSession.getCurrentAssignmentResponse(),
					CurrentAssignmentResponseDto.class);
		} 
		
		List<SEG> segList = null;
		
		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()) {

			if(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG() != null && !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()) {
				
				segList = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG();
			} else  if(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null && !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty() 
					&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG() != null && !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().isEmpty()) {
				
				segList = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG();
			}
		}

		ReplacementLoopDetails replacementLoopDetails = new ReplacementLoopDetails();
		
		String segmentRead = cnfSession.getSegmentRead();
		int segmentNumber = 0;

		if ("F1".equalsIgnoreCase(segmentRead)) {

			id.setSegNumber("1");
			replacementLoopDetails.setCableTroubleTicketNumber(segList.get(0).getCTT());
		} else if ("F2".equalsIgnoreCase(segmentRead)) {
			
			id.setSegNumber("2");
			replacementLoopDetails.setCableTroubleTicketNumber(segList.get(1).getCTT());
			segmentNumber =1;
		} else if ("F3".equalsIgnoreCase(segmentRead)) {

			id.setSegNumber("3");
			replacementLoopDetails.setCableTroubleTicketNumber(segList.get(2).getCTT());
			segmentNumber =2;
		}

		request.setInputData(id);

		CurrentLoopDetails currentLoopDetails = new CurrentLoopDetails();
		SEG segment = segList.get(segmentNumber);
		currentLoopDetails.setCableId(segment.getCA());
		currentLoopDetails.setCableUnitId(segment.getPR());		
		id.setCurrentLoopDetails(currentLoopDetails);
		request.setInputData(id);

		replacementLoopDetails.setCableId(cable);
		replacementLoopDetails.setCableUnitId(pair);
		replacementLoopDetails.setFacilityChangeReason("OPN");
		id.setReplacementLoopDetails(replacementLoopDetails);
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

	public IVRWebHookResponseDto processResponse(IVRWebHookResponseDto response, String segmentRead,
			IVRUserSession userSession) throws JsonMappingException, JsonProcessingException {

		String hookReturnCode = "";
		String hookReturnMessage = "";

		if ("F1".equalsIgnoreCase(segmentRead)) {

			if (userSession.isCanBePagedMobile()) {
				if (!isSwitchAvailable(userSession)) {

					hookReturnCode = HOOK_RETURN_3;
					hookReturnMessage = "Alpha pager";
				}
			} else {
				if (!isSwitchAvailable(userSession)) {

					hookReturnCode = HOOK_RETURN_6;
					hookReturnMessage = "No Alpha pager";
				} else {

					hookReturnCode = HOOK_RETURN_5;
					hookReturnMessage = "No Alpha pager";

				}
			}
		} else if ("F2".equalsIgnoreCase(segmentRead) || "F3".equalsIgnoreCase(segmentRead)) {

			if (userSession.isCanBePagedMobile()) {

				hookReturnCode = HOOK_RETURN_4;
				hookReturnMessage = "Alpha pager";
			} else {

				hookReturnCode = HOOK_RETURN_5;
				hookReturnMessage = "NoAlpha pager";
			}
		}
		
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;
	}
	
	public ChangeLoopAssignmentRequestDto buildChangeLoopAssignmentRequest(String tn, String primaryNpa, String primaryNxx, String empId, 
			String sessionId, IVRCnfEntity cnfSession, IVRUserSession userSession, String cable, String pair) throws JsonMappingException, JsonProcessingException {

		ChangeLoopAssignmentRequestDto request = new ChangeLoopAssignmentRequestDto();

		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		if (userSession != null && userSession.getCurrentAssignmentResponse() != null) {

			currentAssignmentResponseDto = objectMapper.readValue(userSession.getCurrentAssignmentResponse(),
					CurrentAssignmentResponseDto.class);
		}

		List<SEG> segList = extracted(currentAssignmentResponseDto);		

		CurrentAssignmentInfo currentAssignmentInfo = new CurrentAssignmentInfo();

		if (StringUtils.isNotBlank(tn)) {

			TN TN = new TN();

			TN.setCkid(tn);

			currentAssignmentInfo.setTn(TN);
		}		

		ChangeLoopAssignmentRequestInputData inputData =  new ChangeLoopAssignmentRequestInputData();

		inputData.setLFACSEmployeeCode(empId);
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa(primaryNpa);
		wireCtrPrimaryNPANXX.setNxx(primaryNxx);

		inputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		request.setInputData(inputData);	

		inputData.setServiceOrderNumber(cnfSession.getServiceOrderNo());
		inputData.setCircuitId(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID() != null ? currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID() :
			currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getCKID());
		request.setInputData(inputData);

		ChangeLoopAssignmentReqInputDataReplacementLoopDetails replacementLoopDetails = new ChangeLoopAssignmentReqInputDataReplacementLoopDetails();
		//changeLoopAssignmentReqInputDataReplacementLoopDetails.setBindingPostColorCode(sessionId); // TODO WHAT to set???
		replacementLoopDetails.setCableId(cable);
		replacementLoopDetails.setCableUnitId(pair);
		//changeLoopAssignmentReqInputDataReplacementLoopDetails.setReplacementTerminalId(sessionId);  // TODO WHAT to set???
		inputData.setChangeLoopAssignmentReqInputDataReplacementLoopDetails(replacementLoopDetails);
		request.setInputData(inputData);

		inputData.setFacilityChangeReasonCode("NDC"); //TODO:  Mandatory FIELD :: WHAT to set???
		request.setInputData(inputData);

		String segmentRead = cnfSession.getSegmentRead();
		int segmentNumber = 0;
		if ("F1".equalsIgnoreCase(segmentRead)) {
			inputData.setSegNumber("1");
		} else if ("F2".equalsIgnoreCase(segmentRead)) {
			inputData.setSegNumber("2");
			segmentNumber = 1;
		} else if ("F3".equalsIgnoreCase(segmentRead)) {
			inputData.setSegNumber("3");
			segmentNumber = 2;
		}	
		request.setInputData(inputData);		

		ChangeLoopAssignmentReqInputDataCurrentLoopDetails currentLoopDetails = new ChangeLoopAssignmentReqInputDataCurrentLoopDetails();	
		SEG segment = segList.get(segmentNumber);
		currentLoopDetails.setCableId(segment.getCA());
		currentLoopDetails.setCableUnitId(segment.getPR());
		//changeLoopReqInputDataCurrentLoopDetails.setTerminalId();   // TODO WHAT to set???
		inputData.setChangeLoopAssignmentReqInputDataCurrentLoopDetails(currentLoopDetails);
		request.setInputData(inputData);		

		//inputData.SetSegmentNumberSpecified(false); // TODO WHAT to set???
		//request.setInputData(inputData);

		inputData.setChangeActionCode("CUTFAINSTREP");   // TODO Mandatory FIELD :: WHAT to set???
		request.setInputData(inputData);

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

	public List<SEG> extracted(CurrentAssignmentResponseDto currentAssignmentResponseDto) {
		
		List<SEG> segList = null;
		
		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()) {

			if(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG() != null && !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()) {
				
				segList = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG();
			} else  if(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null && !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty() 
					&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG() != null && !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().isEmpty()) {
				
				segList = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG();
			}
			return segList;
		}
		return null;
	}
	
	public RetrieveLoopAssignmentRequest buildRetriveLoopAssignInqRequest(ChangeLoopAssignmentRequestDto changeLoopAssignmentRequest, String tn, String primaryNpa,	String primaryNxx, IVRCnfEntity cnfSession) {

		RetrieveLoopAssignmentRequest request = new RetrieveLoopAssignmentRequest();

		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa(primaryNpa);
		wireCtrPrimaryNPANXX.setNxx(primaryNxx);
		
		RetLoopAssigInputData inputData=new RetLoopAssigInputData();
		inputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		
		if(changeLoopAssignmentRequest == null) {
			
			inputData.setCableId(cnfSession.getCable());
			inputData.setCableUnitId(cnfSession.getPair());
		} else {
			
			inputData.setCableId(changeLoopAssignmentRequest.getInputData().getChangeLoopAssignmentReqInputDataReplacementLoopDetails().getCableId());
			inputData.setCableUnitId(changeLoopAssignmentRequest.getInputData().getChangeLoopAssignmentReqInputDataReplacementLoopDetails().getCableUnitId());
		}
		
		inputData.setLFACSEmployeeCode("SIA");
		inputData.setLFACSEntity("A");
		
		request.setInputData(inputData);
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
	
	public RetrieveMaintenanceChangeTicketRequest buildRetriveMainInqRequest(String tn, String primaryNpa,	String primaryNxx, IVRCnfEntity cnfSession) {

		RetrieveMaintenanceChangeTicketRequest request = new RetrieveMaintenanceChangeTicketRequest();

		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa(primaryNpa);
		wireCtrPrimaryNPANXX.setNxx(primaryNxx);
		
		RetMaintChangeTicketInputData inputData=new RetMaintChangeTicketInputData();
		inputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		
		inputData.setCableId(cnfSession.getCable());
		inputData.setCableUnitId(cnfSession.getPair());
		
		inputData.setLFACSEmployeeCode("SIA");
		inputData.setLFACSEntity("A");
		
		request.setInputData(inputData);
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
	
	public LoopQualNIIServiceRequest buildLoopQualNIIServiceRequest(String tn, String primaryNpa, String primaryNxx, String sessionId, IVRCnfEntity cnfSession) {
		
		LoopQualNIIServiceRequest niiServiceRequest = new LoopQualNIIServiceRequest();
		
		ARTISRequestHeader artisRequestHeader = new ARTISRequestHeader();
		
		com.lumen.fastivr.IVRCNF.Dto.TN tnRequest = new com.lumen.fastivr.IVRCNF.Dto.TN();
		
		artisRequestHeader.setArtisCorrelationId(sessionId);
		artisRequestHeader.setHierarchyCalloutFlag(false);
		
		tnRequest.setNpa(primaryNpa);
		tnRequest.setNxx(primaryNxx);
		tnRequest.setLineNumber(tn);
		
		niiServiceRequest.setArtisRequestHeader(artisRequestHeader);
		niiServiceRequest.setTn(tnRequest);
		niiServiceRequest.setMessageSrcSystem(sessionId);
		return niiServiceRequest;
	}
	
	public IVRWebHookResponseDto findFastivrError(String errorCode, IVRWebHookResponseDto response) {
		
		String hookReturnCode = "";
		String hookReturnMessage = "";

		if ("2".equals(errorCode) || "4".equals(errorCode) || "5".equals(errorCode) || "1013".equals(errorCode)) {
			hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_2;
			hookReturnMessage = IVRLfacsConstants.NOT_RESPONDING_ERR;

		} else if ("3".equals(errorCode)) {

			hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_3;
			hookReturnMessage = IVRLfacsConstants.NOT_AVAILABLE_ERR;

		} else if ("1".equals(errorCode)) {

			hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_4;
			hookReturnMessage = IVRLfacsConstants.TRANSACTION_FAILED_ERR;

		} else if ("7".equals(errorCode) || "6".equals(errorCode)) {

			hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_5;
			hookReturnMessage = IVRLfacsConstants.ABNORMAL_LOOP_QUAL_ERR;

		} else {

			hookReturnMessage = FASTIVR_BACKEND_ERR;
		}

		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;
	}
	
	public VerifyCutInformationRequest buildVerifyCutRequest(String tn, String primaryNpa, String primaryNxx, IVRCnfEntity cnfSession) {
		
		VerifyCutInformationRequest verifyCutRequest = new VerifyCutInformationRequest();
		
		InputData request = new InputData();
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa(primaryNpa);
		wireCtrPrimaryNPANXX.setNxx(primaryNxx);
		request.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		request.setCircuitId(tn);
		request.setCableId(cnfSession.getCable());
		request.setCableUnitId(cnfSession.getPair());
		
		verifyCutRequest.setInputData(request);
		return verifyCutRequest;
	}
	
	public com.lumen.fastivr.IVRCNF.Dto.changePairStatus.InputData buildChangePairStatusRequest(IVRUserSession userSession, IVRCnfEntity cnfSession, String primaryNpa, String primaryNxx, final String defectiveCode) {
		
		com.lumen.fastivr.IVRCNF.Dto.changePairStatus.InputData inputData = new com.lumen.fastivr.IVRCNF.Dto.changePairStatus.InputData();
		
		inputData.setEmp(userSession.getEmpID());
		
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa(primaryNpa);
		wireCtrPrimaryNPANXX.setNxx(primaryNxx);
		inputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		
		inputData.setCa(cnfSession.getCable());
		inputData.setPr(cnfSession.getPair());
		inputData.setDefTP(defectiveCode);
		return inputData;
	}
}
