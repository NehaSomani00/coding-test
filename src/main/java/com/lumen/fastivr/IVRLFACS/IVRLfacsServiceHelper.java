package com.lumen.fastivr.IVRLFACS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRBusinessException.BusinessException;
import com.lumen.fastivr.IVRDto.*;
import com.lumen.fastivr.IVRDto.LOSDB.CTelephone;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.CandidatePairInfo;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetMaintChangeTicketInputData;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketRequest;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketResponse;
import com.lumen.fastivr.IVRDto.additionalLines.AdditionalLinesReportInputData;
import com.lumen.fastivr.IVRDto.additionalLines.AdditionalLinesReportRequestDto;
import com.lumen.fastivr.IVRDto.additionalLines.AdditionalLinesReportResponseDto;
import com.lumen.fastivr.IVRDto.coe.CentralOfficeEquipmentInputData;
import com.lumen.fastivr.IVRDto.coe.CentralOfficeEquipmentRequestDto;
import com.lumen.fastivr.IVRDto.defectivepairs.CablePairRange;
import com.lumen.fastivr.IVRDto.defectivepairs.DefectivePairsInputData;
import com.lumen.fastivr.IVRDto.defectivepairs.DefectivePairsRequestDto;
import com.lumen.fastivr.IVRDto.multipleappearance.MultipleAppearanceInputData;
import com.lumen.fastivr.IVRDto.multipleappearance.MultipleAppearanceRequestDto;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.LoopAssignCandidatePairInfo;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetLoopAssigInputData;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentRequest;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentResponse;
import com.lumen.fastivr.IVREntity.TNInfo;
import com.lumen.fastivr.IVRRepository.FastIvrDBInterface;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.IVRUtils.IVRLfacsConstants;
import com.lumen.fastivr.IVRUtils.IVRUtility;
import com.lumen.fastivr.httpclient.IVRHttpClient;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.lumen.fastivr.IVRUtils.IVRConstants.*;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.*;

@Service
public class IVRLfacsServiceHelper {

	@Autowired
	private IVRLOSDBManager ivrlosdbManager;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private HttpClient httpClient;

	@Value("${lfacs.current.assignment.url}")
	private String currentAssignmentUrl;

	@Value("${lfacs.retrieve.maint.change.url}")
	private String retrieveMaintChangeUrl;

	@Value("${lfacs.retrieve.loop.assign.url}")
	private String retrieveLoopAssignUrl;

	@Autowired
	private LfacsValidation lfacsValidation;

	@Autowired
	private IVRHttpClient ivrHttpClient;

	@Autowired
	private FastIvrDBInterface fastIvrDBInterface;

	@Value("${netapp.api.pager.url}")
	private String netUri;

	@Value("#{${inputCodes}}")
	Map<String, String> inputCodes = new HashMap<String, String>();

	final static Logger LOGGER = LoggerFactory.getLogger(IVRLOSDBManager.class);

	public boolean isCircuitWithNoTN(CurrentAssignmentResponseDto currentAssignmentResponseDto,
									 IVRWebHookResponseDto response) {

		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()) {

			String tn = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID();

			if (StringUtils.isEmpty(tn) || "NONE".equalsIgnoreCase(tn) || tn.length() > 12) {

				getStatusPair(currentAssignmentResponseDto, response, null, true);

				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}

	public boolean isSpecialCircuit(CurrentAssignmentResponseDto currentAssignmentResponseDto,
									IVRWebHookResponseDto response) {

		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()
				&& (StringUtils.isNotBlank(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID()) 
						|| StringUtils.isNotBlank(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getCKID()))) {

			int tnLengthBeforeConvertion = 0;

			int tnLengthAfterConvertion = 0;

			String tnAfterProcess = "";

			String tn = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID() != null ? currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID()
					: currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getCKID();

			if (tn.length() <= 12 && tn.length() > 0) {

				tnLengthBeforeConvertion = tn.length();

				ArrayList<Character> tnCharList = new ArrayList<Character>();

				for (char splitTNasChar : tn.toCharArray()) {

					tnCharList.add(splitTNasChar);
				}

				Object[] tnDigits = tnCharList.stream().filter(tnChar -> Character.isDigit(tnChar))
						.map(tnDigit -> Character.getNumericValue(tnDigit)).toArray();

				tn = "";

				for (Object tnDigit : tnDigits) {

					tn += tnDigit;
				}

				if (StringUtils.isNotBlank(tn)) {

					tnLengthAfterConvertion = tnDigits.length;
				}

				if (tnLengthAfterConvertion == 7 && (tnLengthBeforeConvertion == 7 || tnLengthAfterConvertion == 8)
						&& "-".equals(tnCharList.get(3).toString())) {

					StringBuilder tnBuilder = new StringBuilder();

					tnAfterProcess = tnBuilder.append(tn.substring(0, 3)).append("-").append(tn.substring(3, 4))
							.toString();

				} else if (tnLengthAfterConvertion == 10
						&& (tnLengthBeforeConvertion == 10 || (tnLengthBeforeConvertion == 12
						&& ("-".equals(tnCharList.get(3).toString())
						|| StringUtils.isBlank(tnCharList.get(3).toString().trim()))
						&& "-".equals(tnCharList.get(7).toString())))) {

					StringBuilder tnBuilder = new StringBuilder();

					tnAfterProcess = tnBuilder.append(tn.substring(0, 3)).append(" ").append(tn.substring(3, 6))
							.append("-").append(tn.substring(6)).toString();
				}
			}

			if (StringUtils.isNotBlank(tnAfterProcess) && tnAfterProcess.length() > 0 && tn.length() <= 12) {

				return Boolean.FALSE;
			}
		} else {

			return Boolean.FALSE;
		}

		if (response != null) {

			getStatusPair(currentAssignmentResponseDto, response, null, false);
		}

		return Boolean.TRUE;
	}

	public boolean isUdcCircuit(CurrentAssignmentResponseDto currentAssignmentResponseDto,
								IVRWebHookResponseDto response) {

		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()) {
			
			String ca = null;
			
			if(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG() != null 
					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()
					&& StringUtils.isNotBlank(
							currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).getCA())) {
				
				ca = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).getCA();
			} else if(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null
					 && !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty()
							 && StringUtils.isNotBlank(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().get(0).getCA())) {
				
				ca = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().get(0).getCA();
			}
			
			if (ca.toUpperCase().contains("UDC")) {

				if (response != null) {

					getStatusPair(currentAssignmentResponseDto, response, null, false);
				}

				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}

	public IVRWebHookResponseDto getColourCode(IVRWebHookResponseDto response,
											   CurrentAssignmentResponseDto currentAssignmentResponseDto, IVRUserSession userSession) {

		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()) {

			String colourCode = null;

			String colourType = null;

			List<SEG> segmentList = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG();

			String segmentRead = userSession.getSegmentRead();

			if (StringUtils.isNotBlank(segmentRead) && "F1".equalsIgnoreCase(segmentRead)) {

				colourCode = segmentList.get(1).getBP() != null ? segmentList.get(1).getBP().toUpperCase() : null;

				colourType = segmentList.get(1).getTP() != null ? segmentList.get(1).getTP().toUpperCase() : null;

				if (segmentList.size() == 2) {

					userSession.setSegmentRead("ALL");
				} else {

					userSession.setSegmentRead("F2");
				}
			} else if ((StringUtils.isNotBlank(segmentRead) && "F2".equalsIgnoreCase(segmentRead))
					|| "ALL".equalsIgnoreCase(segmentRead)) {

				colourCode = segmentList.get(2).getBP() != null ? segmentList.get(2).getBP().toUpperCase() : null;

				colourType = segmentList.get(2).getTP() != null ? segmentList.get(2).getTP().toUpperCase() : null;

				userSession.setSegmentRead("ALL");
			} else if (StringUtils.isEmpty(segmentRead)) {

				colourCode = segmentList.get(0).getBP() != null ? segmentList.get(0).getBP().toUpperCase() : null;

				colourType = segmentList.get(0).getTP() != null ? segmentList.get(0).getTP().toUpperCase() : null;

				if (segmentList.size() == 1) {

					userSession.setSegmentRead("ALL");
				} else {

					userSession.setSegmentRead("F1");
				}
			}

			return checkColourCode(colourCode, colourType, response);
		}

		response.setHookReturnCode(HOOK_RETURN);
		response.setHookReturnMessage(FASTIVR_BACKEND_ERR);

		return response;
	}

	public IVRWebHookResponseDto checkColourCode(String colourCode, String colourType,
												 IVRWebHookResponseDto response) {

		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();
		IVRParameter parameter = new IVRParameter();
		if (StringUtils.isNotBlank(colourCode)) {
			colourCode = colourCode.replaceAll("-", "");
		}
		if (StringUtils.isNotBlank(colourType)) {
			colourType = colourType.replaceAll("-", "");
		}

		if (StringUtils.isNotBlank(colourCode) && colourCode.contains(IVRConstants.WHITE_TRACER)) {

			response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_5);
			response.setHookReturnMessage(IVRConstants.WHITE_TRACER_MSG);
			return response;
		} else if (StringUtils.isNotBlank(colourCode) && colourCode.contains(IVRConstants.RED_TRACER)) {

			response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_6);
			response.setHookReturnMessage(IVRConstants.RED_TRACER_MSG);
			return response;
		} else if (StringUtils.isNotBlank(colourCode) && colourCode.contains(IVRConstants.BLACK_TRACER)) {

			response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_7);
			response.setHookReturnMessage(IVRConstants.BLACK_TRACER_MSG);
			return response;
		} else if ((StringUtils.isNotBlank(colourCode) && IVRUtility.numericCheck(colourCode))
				|| (colourType != null && colourType.contains(IVRConstants.BP))) {

			parameter.setData(IVRConstants.COMMA.equalsIgnoreCase(",") ? colourCode : colourType);
			parameterList.add(parameter);

			response.setParameters(parameterList);
			response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_1);
			response.setHookReturnMessage(IVRConstants.BINDING_POST);
			return response;
		} else if (IVRConstants.EMPTY.equalsIgnoreCase(colourCode)
				|| IVRConstants.UNKNOWN.equalsIgnoreCase(colourCode)) {

			response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_2);
			response.setHookReturnMessage(IVRConstants.NO_BINDING);
			parameter.setData(colourCode);
			parameterList.add(parameter);

			response.setParameters(parameterList);
			return response;
		} else if (StringUtils.isNotBlank(colourCode) && colourCode.contains(IVRConstants.TONE)) {

			response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_4);
			response.setHookReturnMessage(IVRConstants.BINDING_POST_TONE);
			return response;
		}

		parameter.setData(colourCode);
		parameterList.add(parameter);

		response.setParameters(parameterList);
		response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_3);
		response.setHookReturnMessage(IVRConstants.COLOR_CODE);

		return response;
	}

	public String get10DigitTN(IVRUserSession session, String TN) {

		if(StringUtils.isNotBlank(TN) && TN.length() == 10) {

			return TN;
		}
		String npa = session.getNpaPrefix();
		return npa.concat(TN);
	}

	public String getLOSDBInterfaceStatus(IVRUserSession session, String ckid) {
		// build the telephone object from ckid & session
		String returnCode = "";
		CTelephone telephone = CTelephoneBuilder.newBuilder(session).setTelephone(ckid).build();
		try {
			returnCode = getWireCentreDetails(session, telephone);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while Processing LOS DB Response :", e);
			returnCode = IVRFAILURE;
		} catch (BusinessException e) {
			LOGGER.error("Error while Processing LOS DB Response :" + e.getLocalizedMessage());
			returnCode = IVRFAILURE;
		} catch (Exception e) {
			LOGGER.error("Error while Fecthing LOS DB Response :", e);
			returnCode = IVRFAILURE;
		}

		return returnCode;
	}

	private String getWireCentreDetails(IVRUserSession session, CTelephone telephone)
			throws JsonMappingException, JsonProcessingException {
		String responseString = ivrlosdbManager.getTnInfo(telephone, session.getSessionId());
		// if response is null then TN might be invalid
		// so check the TN info table to see if we need to swap the NPA (border town)
		if (responseString == null || responseString.isBlank()) {
			TNInfo tninfoDB = ivrlosdbManager.validateNPA(telephone, session.getSessionId());

			// see if we have a replacement npa for the request npa in the tn_info table
			// if so then run the LOSDB request with the replacement npa
			if (tninfoDB != null) {
				telephone.setNpa(tninfoDB.getNpaPrefix());
				responseString = ivrlosdbManager.getTnInfo(telephone, session.getSessionId());
				if (responseString == null || responseString.isBlank()) {
//					throw new Exception("Invalid TN Info Request. ErrorCode: " + response.ErrorCode);
					throw new BusinessException("Invalid TN Info Request. ErrorCode: ");
				}

			} else {
				// Need to add error code
				throw new BusinessException("Invalid TN Info Request. ErrorCode: ");
			}
		}

		// make sure the service provider returned is a qwest owned service provider
		TNInfoResponse tnInfoResponse = objectMapper.readValue(responseString, TNInfoResponse.class);
		if (!ivrlosdbManager.matchServiceProviderId(tnInfoResponse)) {
			throw new BusinessException(
					"Service Provider " + tnInfoResponse.getServiceProviderName() + " is not Qwest owned");

		} else {
			// response is valid
			session.setLosDbResponse(responseString);
			session.setInquiredTn(tnInfoResponse.getTn());
			return IVRSUCCESS;
		}
	}

	/**
	 * This method extracts the TN from LOSDB response to be used further
	 *
	 * @param jsonString
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public TNInfoResponse extractTNInfoFromLosDBResponse(String jsonString)
			throws JsonMappingException, JsonProcessingException {
		TNInfoResponse tnInfoResponse = objectMapper.readValue(jsonString, TNInfoResponse.class);
		return tnInfoResponse;
	}

	/**
	 * Primarily to build the request body for current assignment inquiry by TN
	 * Re-use this code by adding parameters for Inq by CP
	 *
	 * @param tn
	 * @param primaryNpa
	 * @param primaryNxx
	 * @return
	 */
	public CurrentAssignmentRequestTnDto buildCurrentAssignmentInqRequest(String tn, String primaryNpa,
																		  String primaryNxx, List<String> userInputDTMFList, IVRUserSession userSession) {

		CurrentAssignmentRequestTnDto request = new CurrentAssignmentRequestTnDto();

		CurrentAssignmentInfo currentAssignmentInfo = new CurrentAssignmentInfo();

		if (StringUtils.isNotBlank(tn)) {

			TN TN = new TN();

			TN.setCkid(tn);

			currentAssignmentInfo.setTn(TN);
		}

		if (INQUIRY_BY_CABLE_PAIR.equalsIgnoreCase(userSession.getFacsInqType())) {

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

	public RetrieveMaintenanceChangeTicketRequest buildRetriveMainInqRequest(String tn, String primaryNpa,
																			 String primaryNxx, CurrentAssignmentResponseDto currentAssignresponseObject, IVRUserSession session,
																			 int segNo) {

		RetrieveMaintenanceChangeTicketRequest request = new RetrieveMaintenanceChangeTicketRequest();

		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa(primaryNpa);
		wireCtrPrimaryNPANXX.setNxx(primaryNxx);

		RetMaintChangeTicketInputData inputData = new RetMaintChangeTicketInputData();
		inputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);

		if (session.getFacsInqType().equalsIgnoreCase(IVRConstants.INQUIRY_BY_TN)) {
			inputData.setCableId(
					currentAssignresponseObject.getReturnDataSet().getLoop().get(0).getSEG().get(segNo - 1).getCA());
			inputData.setCableUnitId(
					currentAssignresponseObject.getReturnDataSet().getLoop().get(0).getSEG().get(segNo - 1).getPR());

		} else if (session.getFacsInqType().equalsIgnoreCase(IVRConstants.INQUIRY_BY_CABLE_PAIR)) {
			inputData.setCableId(session.getCable());
			inputData.setCableUnitId(session.getPair());
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

	public RetrieveLoopAssignmentRequest buildRetriveLoopAssignInqRequest(String tn, String primaryNpa,
																		  String primaryNxx, CurrentAssignmentResponseDto currentAssignresponseObject, IVRUserSession session,
																		  int segNo) {

		RetrieveLoopAssignmentRequest request = new RetrieveLoopAssignmentRequest();

		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa(primaryNpa);
		wireCtrPrimaryNPANXX.setNxx(primaryNxx);

		RetLoopAssigInputData inputData = new RetLoopAssigInputData();
		inputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);

		if (session.getFacsInqType().equalsIgnoreCase(IVRConstants.INQUIRY_BY_TN)) {

			List<SEG> segList = currentAssignresponseObject.getReturnDataSet().getLoop().get(0).getSEG();

			if (segList != null && !segList.isEmpty()) {

				inputData.setCableId(segList.get(segNo - 1).getCA());
				inputData.setCableUnitId(segList.get(segNo - 1).getPR());
			} else if (currentAssignresponseObject.getReturnDataSet().getLoop().get(0).getSO() != null
					&& !currentAssignresponseObject.getReturnDataSet().getLoop().get(0).getSO().isEmpty()) {

				segList = currentAssignresponseObject.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG();
				inputData.setCableId(segList.get(segNo - 1).getCA());
				inputData.setCableUnitId(segList.get(segNo - 1).getPR());
			}
		} else if (session.getFacsInqType().equalsIgnoreCase(IVRConstants.INQUIRY_BY_CABLE_PAIR)) {
			inputData.setCableId(session.getCable());
			inputData.setCableUnitId(session.getPair());
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

	public List<IVRParameter> addParamterData(String... str) {
		List<IVRParameter> params = new ArrayList<>();
		for (String s : str) {
			IVRParameter param = new IVRParameter();
			param.setData(s);
			params.add(param);
		}

		return params;
	}

	public IVRWebHookResponseDto getStatusPair(CurrentAssignmentResponseDto currentAssignmentResponseDto,
											   IVRWebHookResponseDto response, TNInfoResponse tnInfoResponse, boolean flag) {

		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		if (tnInfoResponse != null && StringUtils.isNotBlank(tnInfoResponse.getTn())) {

			IVRParameter parameter = new IVRParameter();
			parameter.setData(tnInfoResponse.getTn());
			parameterList.add(parameter);
		}

		if (flag) {

			getCablePairStatus(currentAssignmentResponseDto, parameterList);
		}

		parameterList.addAll(getCablePair(currentAssignmentResponseDto, 0));

		response.setParameters(parameterList);

		return response;
	}

	/**
	 * This is the method to call the Current assignment information inquiry by TN
	 * to Fastivr-Wrapper application. Try to re-use this method for call by
	 * Cable-pair
	 *
	 * @param jsonRequest
	 * @param userSession
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public String callCurrentAssignmentInquiryLfacs(String jsonRequest, IVRUserSession userSession)
			throws InterruptedException, ExecutionException {

		HttpRequest httpRequest = HttpRequest.newBuilder().timeout(Duration.ofMinutes(2L))
				.header("Content-Type", "application/json").uri(URI.create(currentAssignmentUrl.trim()))
				.POST(BodyPublishers.ofString(jsonRequest)).build();

		CompletableFuture<String> futureResponse = httpClient.sendAsync(httpRequest, BodyHandlers.ofString())
				.thenApply(response -> response.body());

		LOGGER.info("Session id:" + userSession.getSessionId() + ", LFACS CUrrent assignment API:"
				+ currentAssignmentUrl + ", Request json: " + jsonRequest);

		String responseString = futureResponse.get();

		return responseString;
	}

	public String callSparePairInquiryLfacs(String jsonRequest, IVRUserSession userSession)
			throws InterruptedException, ExecutionException, HttpTimeoutException {

		String responseBody = ivrHttpClient.httpPostApiCall(jsonRequest, retrieveMaintChangeUrl.trim(),
				userSession.getSessionId(), "MAINTENANCE CHANGE INQUIRY API").getResponseBody();

		String cleanResponseString = ivrHttpClient.cleanResponseString(responseBody);
		LOGGER.info("Session: " + userSession.getSessionId() + ", MAINTENANCE CHANGE INQUIRY API clean response: "
				+ cleanResponseString);
		return cleanResponseString;
	}

	public String callSparePairLoopAssignInquiryLfacs(String jsonRequest, IVRUserSession userSession)
			throws InterruptedException, ExecutionException, HttpTimeoutException {

		String responseBody = ivrHttpClient.httpPostApiCall(jsonRequest, retrieveLoopAssignUrl.trim(),
				userSession.getSessionId(), "LOOP ASSIGNMENT INQUIRY API").getResponseBody();

		String cleanResponseString = ivrHttpClient.cleanResponseString(responseBody);
		LOGGER.info("Session: " + userSession.getSessionId() + ", LOOP ASSIGNMENT INQUIRY API clean response: "
				+ cleanResponseString);
		return cleanResponseString;
	}

	public String cleanResponseString(String responseString) {
		// Since Wrapper app is sending json response enclosed within a "" and having
		// escape characters \
		String jsonString = responseString.replace("\\", "");
		return jsonString.substring(1, jsonString.length() - 1);
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

	/**
	 * Checks the service address in Current assignment response If service address
	 * is present : return 1 - Inquiry by TN return 2 - Inquiry by Cable Pair return
	 * 0 - Service address is absent
	 *
	 * @param session
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public String checkIfServiceAddrExists(IVRUserSession session)
			throws JsonMappingException, JsonProcessingException {
		String hookReturnCode = HOOK_RETURN_0;
		String currAssgResp = session.getCurrentAssignmentResponse();
		CurrentAssignmentResponseDto responseDto = objectMapper.readValue(currAssgResp,
				CurrentAssignmentResponseDto.class);

		if (lfacsValidation.validateServiceAddress(responseDto)) {
			// service address exists
			String inqType = session.getFacsInqType();
			if (inqType.equalsIgnoreCase(INQUIRY_BY_TN)) {
				hookReturnCode = HOOK_RETURN_1;
			} else if (inqType.equalsIgnoreCase(INQUIRY_BY_CABLE_PAIR)) {
				hookReturnCode = HOOK_RETURN_2;
			}
		}

		return hookReturnCode;
	}

	/**
	 * Method to call the LFACS api for Additional apprearance
	 *
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public String callAdditionalLinesInquiry(String jsonRequest, String url, String sessionId, String requestName) {

		return ivrHttpClient.httpPostCall(jsonRequest, url, sessionId, requestName);

//		HttpRequest httpRequest = HttpRequest.newBuilder()
//				.timeout(Duration.ofMinutes(2L))
//				.header("Content-Type", "application/json")
//					.uri(URI.create(additionalLinesUrl.trim()))
//					.POST(BodyPublishers.ofString(jsonRequest))
//					.build();
//
//        CompletableFuture<String> futureResponse = httpClient.sendAsync(httpRequest, BodyHandlers.ofString())
//				.thenApply(response -> response.body());
//
//		LOGGER.info("Session id:" + userSession.getSessionId() + ", LFACS Current assignment API:"
//						+ currentAssignmentUrl + ", Request json: " + jsonRequest);
//
//		String responseString = futureResponse.get();

//		return responseString;
	}

	/**
	 * Primarily to build the request body for Multiple Appearance inquiry by TN
	 *
	 * @param cableId
	 * @param cableUnitId
	 * @param employeeId
	 * @param npa
	 * @param nxx
	 * @return MultipleAppearanceRequestDto
	 */
	public MultipleAppearanceRequestDto buildMultipleAppearanceInqRequest(String cableId, String cableUnitId,
																		  String employeeId, String npa, String nxx) {
		MultipleAppearanceRequestDto multiAppRequest = new MultipleAppearanceRequestDto();
	//	setRequestCommonDetails(multiAppRequest);

		MultipleAppearanceInputData multiAppInputData = new MultipleAppearanceInputData();
		multiAppInputData.setCableId(cableId);
		multiAppInputData.setCableUnitId(cableUnitId);
		multiAppInputData.setEmployeeId(employeeId);
		multiAppInputData.setLfacsEntity("");

		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa(npa);
		wireCtrPrimaryNPANXX.setNxx(nxx);
		multiAppInputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);

		multiAppRequest.setInputData(multiAppInputData);

		return multiAppRequest;

	}

	/**
	 * Method to build the request for additional lines report request
	 *
	 * @param serviceAddress
	 * @param employeeId
	 * @param npa
	 * @param nxx
	 * @return
	 */
	public AdditionalLinesReportRequestDto buildAdditionalLinesReportRequest(String serviceAddress, String employeeId,
																			 String npa, String nxx) {
		AdditionalLinesReportRequestDto request = new AdditionalLinesReportRequestDto();
		AdditionalLinesReportInputData inputData = new AdditionalLinesReportInputData();

		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa(npa);
		wireCtrPrimaryNPANXX.setNxx(nxx);
		inputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		inputData.setLFACSEmployeeCode(employeeId);
		inputData.setServiceAddressGeneric(serviceAddress);
		request.setInputData(inputData);

//		request.setRequestId(IVRConstants.WRAPPER_API_REQUEST_ID);
//		request.setWebServiceName(IVRConstants.WRAPPER_API_WEB_SERVICE_NAME);
//		request.setRequestPurpose(IVRConstants.WRAPPER_API_REQUEST_PURPOSE);
//
//		AuthorizationInfo authInfo = new AuthorizationInfo();
//		authInfo.setUserid(IVRConstants.WrapperAuthorizationInfo.USERID);
//		authInfo.setPassword(IVRConstants.WrapperAuthorizationInfo.PASSWORD);
//		request.setAuthorizationInfo(authInfo);
//
//		TargetSchemaVersionUsed schema = new TargetSchemaVersionUsed();
//		schema.setMajorVersionNumber(0);
//		schema.setMinorVersionNumber(0);
//
//		request.setTargetSchemaVersionUsed(schema);
//		request.setTimeOutSecond(180);

		return request;
	}

	/**
	 * This method is used to process and send the Test result to Tech's PAGER
	 * device (PHONE or MAIL)
	 *
	 * @param pageResult
	 * @param device
	 * @return {@link Boolean}
	 * @throws JsonProcessingException
	 */
	public boolean sendTestResultToTech(String messageSubject, String pageResult, String device, IVRUserSession session)
			throws JsonProcessingException {

		NETMessagingRequestDto requestDto = ivrHttpClient.buildNetRequest(session.getCuid(), messageSubject, pageResult,
				device);
		String requestJsonStr = objectMapper.writeValueAsString(requestDto);
		String responseNetApi = ivrHttpClient.httpPostCall(requestJsonStr, netUri, session.getSessionId(),
				requestJsonStr);
		return ivrHttpClient.processJsonStringNETMessaging(responseNetApi);

	}

	/**
	 * Primarily to build the request body for Central Office Equipment inquiry by
	 * TN
	 *
	 * @param circuitId
	 * @param npa
	 * @param nxx
	 * @return CentralOfficeEquipmentRequestDto
	 */
	public CentralOfficeEquipmentRequestDto buildCentralOfcEquipmentInqRequest(String circuitId, String npa,
																			   String nxx) {
		CentralOfficeEquipmentRequestDto coeRequest = new CentralOfficeEquipmentRequestDto();
//		setRequestCommonDetails(coeRequest);
		CentralOfficeEquipmentInputData coeInputData = new CentralOfficeEquipmentInputData();
		coeInputData.setCircuitId(circuitId);

		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa(npa);
		wireCtrPrimaryNPANXX.setNxx(nxx);
		coeInputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);

		coeRequest.setInputData(coeInputData);

		return coeRequest;

	}

	public List<IVRParameter> getCablePairStatus(CurrentAssignmentResponseDto currentAssignmentResponseDto,
												 List<IVRParameter> parameterList) {

		IVRParameter parameter = new IVRParameter();

		if (StringUtils.isNotBlank(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSTAT())
				|| StringUtils.isNotBlank(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSTAT())) {

			String stat = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSTAT() != null ? 
					currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSTAT() : currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSTAT();
			parameter.setData(stat);
		} else if(StringUtils.isNotBlank(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).getLSTAT()) 
				|| StringUtils.isNotBlank(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().get(0).getLSTAT())) {

			String lStat = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).getLSTAT() != null ? 
					currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).getLSTAT() : currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().get(0).getLSTAT();
			parameter.setData(lStat);
		}

		parameterList.add(parameter);

		return parameterList;
	}

	public List<IVRParameter> getCablePair(CurrentAssignmentResponseDto currentAssignmentResponseDto, int index) {

		List<IVRParameter> parameterList = null;

		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()) {

			parameterList = new ArrayList<IVRParameter>();
			IVRParameter parameterCA = new IVRParameter();
			IVRParameter parameterPR = new IVRParameter();
			parameterCA.setData(
					currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(index).getCA());
			parameterList.add(parameterCA);
			parameterPR.setData(
					currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(index).getPR());
			parameterList.add(parameterPR);
		} else if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty()) {

			parameterList = new ArrayList<IVRParameter>();
			IVRParameter parameterCA = new IVRParameter();
			IVRParameter parameterPR = new IVRParameter();
			parameterCA.setData(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG()
					.get(index).getCA());
			parameterList.add(parameterCA);
			parameterPR.setData(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG()
					.get(index).getPR());
			parameterList.add(parameterPR);
		}

		return parameterList;
	}

	public String getServiceAddress(CurrentAssignmentResponseDto currentAssignmentResponseDto) {

		String serviceAddress = "";

		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().isEmpty()) {

			ADDR address = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0);

			if (address.getBADR() != null && !address.getBADR().isEmpty()) {

				if (StringUtils.isNotBlank(address.getBADR().get(0).getBAD())) {

					serviceAddress += address.getBADR().get(0).getBAD();
				}

				if (StringUtils.isNotBlank(address.getBADR().get(0).getSTR())) {

					serviceAddress += " " + address.getBADR().get(0).getSTR();
				}
			}

			if (address.getSUPL() != null && !address.getSUPL().isEmpty()) {

				SUPL supl = address.getSUPL().get(0);

				if (StringUtils.isNotBlank(supl.getUTYP())) {

					serviceAddress += " " + supl.getUTYP();
				}

				if (StringUtils.isNotBlank(supl.getUID())) {

					serviceAddress += " " + supl.getUID();
				}

				if (StringUtils.isNotBlank(supl.getSTYP())) {

					serviceAddress += " " + supl.getSTYP();
				}

				if (StringUtils.isNotBlank(supl.getSID())) {

					serviceAddress += " " + supl.getSID();
				}

				if (StringUtils.isNotBlank(supl.getETYP())) {

					serviceAddress += " " + supl.getETYP();
				}

				if (StringUtils.isNotBlank(supl.getEID())) {

					serviceAddress += " " + supl.getEID();
				}
			}
		}

		return serviceAddress;
	}

	public IVRWebHookResponseDto findSparePair(String sessionId, String nextState, IVRUserSession ivrUserSession,
											   String enqType) throws JsonMappingException, JsonProcessingException {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(nextState);
		String hookReturnCode = "";
		String hookReturnMessage = "";

		if (enqType.equalsIgnoreCase("RETRIEVE_LOOP_ASSSIGN")) {
			if (ivrUserSession.getRtrvLoopAssgMsgName() != null) {

				RetrieveLoopAssignmentResponse retrieveLoopAssignmentResponse = objectMapper
						.readValue(ivrUserSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class);
				if (retrieveLoopAssignmentResponse.getReturnDataSet() != null) {
					if (retrieveLoopAssignmentResponse.getReturnDataSet().getCandidatePairInfo() != null) {

						Integer sparePairCT_count = getCandPairCT(enqType, null,
								retrieveLoopAssignmentResponse.getReturnDataSet().getCandidatePairInfo());

						if (retrieveLoopAssignmentResponse.getReturnDataSet().getCandidatePairInfo()
								.size() == sparePairCT_count) {
							hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_0;
							hookReturnMessage = "All pairs are CT";
						} else {
							hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_8;
							hookReturnMessage = "We have spare pair to speak";
						}
					} else {
						hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_7;
						hookReturnMessage = "Spare pairs not available";
					}

				} else {
					hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_6;
					hookReturnMessage = "No candidate spare pair";
				}
			}

		} else {

			if (ivrUserSession.getRtrvMaintChngeMsgName() != null) {

				RetrieveMaintenanceChangeTicketResponse retrieveMainChangetResponse = objectMapper.readValue(
						ivrUserSession.getRtrvMaintChngeMsgName(), RetrieveMaintenanceChangeTicketResponse.class);

				if (retrieveMainChangetResponse.getReturnDataSet() != null) {
					if (retrieveMainChangetResponse.getReturnDataSet().getCandidatePairInfo() != null) {

						Integer sparePairCT_count = getCandPairCT(enqType,
								retrieveMainChangetResponse.getReturnDataSet().getCandidatePairInfo(), null);

						if (retrieveMainChangetResponse.getReturnDataSet().getCandidatePairInfo()
								.size() == sparePairCT_count) {
							hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_0;
							hookReturnMessage = "All pairs are CT";
						} else {
							hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_8;
							hookReturnMessage = "We have spare pair to speak";
						}

					} else {
						hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_7;
						hookReturnMessage = "Spare pairs not available";
					}

				} else {
					hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_6;
					hookReturnMessage = "No candidate spare pair";
				}
			}

		}

		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		LOGGER.info(hookReturnMessage, sessionId);
		return response;
	}

	/**
	 * Return the size of the Return data set excluding the Inquired TN
	 *
	 * @param response
	 * @return
	 */
	public int getNumberOfAddlLines(AdditionalLinesReportResponseDto response) {
		if (response != null && response.getReturnDataSet() != null && response.getReturnDataSet().size() > 0) {
			return response.getReturnDataSet().size();
		}
		return 0;
	}

	/**
	 * Get the list of Additional lines except the Inquired TN Assumption : Inquired
	 * TN comes in 1st position of the array : The TN list has been scanned through
	 * in FID080 and FID285 for UDC and special circuits, so, no need to validate it
	 * against {@link CTelephoneBuilder} again
	 *
	 * @param session
	 * @param telephone
	 * @param tNList
	 * @return
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
//	public List<String> getAdditionalLinesExceptInquiredTN(IVRUserSession session, List<String> tNList)
//			throws JsonMappingException, JsonProcessingException {
//
//		if(!tNList.isEmpty()) {
//			return tNList.subList(1, tNList.size());
//		}
//		return tNList;
//
//	}

	public IVRWebHookResponseDto getColourCodeRetrieveMaintChange(IVRWebHookResponseDto response,
																  RetrieveMaintenanceChangeTicketResponse retrieveMaintenanceChangeTicketResponse,
																  IVRUserSession userSession) {

		if (retrieveMaintenanceChangeTicketResponse != null
				&& retrieveMaintenanceChangeTicketResponse.getReturnDataSet() != null
				&& retrieveMaintenanceChangeTicketResponse.getReturnDataSet().getCandidatePairInfo() != null
				&& !retrieveMaintenanceChangeTicketResponse.getReturnDataSet().getCandidatePairInfo().isEmpty()) {

			String colourCode = "";
			String colourType = "";

			List<CandidatePairInfo> canPairList = retrieveMaintenanceChangeTicketResponse.getReturnDataSet()
					.getCandidatePairInfo();

			Integer candPairCount = userSession.getCandPairCounter();
			if (canPairList.size() > 0 && canPairList.get(candPairCount - 1).getBindingPostColorCode() != null) {
				colourCode = canPairList.get(candPairCount - 1).getBindingPostColorCode().toUpperCase();
				colourType = canPairList.get(candPairCount - 1).getBindingPostColorCode().toUpperCase();
			}

			return checkColourCode(colourCode, colourType, response);
		}

		response.setHookReturnCode(HOOK_RETURN);
		response.setHookReturnMessage(FASTIVR_BACKEND_ERR);

		return response;
	}

	public String getCutPageFormat(String tn) {

		String pageText = tn + IVRLfacsConstants.CUT_PAGE_FORMAT;
		return pageText;
	}

	public IVRWebHookResponseDto getColourCodeRetrieveLoopAssignment(IVRWebHookResponseDto response,
																	 RetrieveLoopAssignmentResponse retrieveLoopAssignmentResponse, IVRUserSession userSession) {

		if (retrieveLoopAssignmentResponse != null && retrieveLoopAssignmentResponse.getReturnDataSet() != null
				&& retrieveLoopAssignmentResponse.getReturnDataSet().getCandidatePairInfo() != null
				&& !retrieveLoopAssignmentResponse.getReturnDataSet().getCandidatePairInfo().isEmpty()) {

			String colourCode = "";
			String colourType = "";

			List<LoopAssignCandidatePairInfo> canPairList = retrieveLoopAssignmentResponse.getReturnDataSet()
					.getCandidatePairInfo();

			Integer candPairCount = userSession.getCandPairCounter();

			if (canPairList.size() > 0 && canPairList.get(candPairCount - 1).getBindingPostColorCode() != null) {
				colourCode = canPairList.get(candPairCount - 1).getBindingPostColorCode().toUpperCase();
				colourType = canPairList.get(candPairCount - 1).getBindingPostColorCode().toUpperCase();
			}

			return checkColourCode(colourCode, colourType, response);
		}

		response.setHookReturnCode(HOOK_RETURN);
		response.setHookReturnMessage(FASTIVR_BACKEND_ERR);

		return response;
	}

	public String getCablePairStatus(String statusCode) {

		String status = "have no status";

		if (IVRLfacsConstants.SPR.equals(statusCode)) {
			status = "is spair";
		} else if (IVRLfacsConstants.CF.equals(statusCode)) {
			status = "is a connected facility";
		} else if (IVRLfacsConstants.PCF.equals(statusCode)) {
			status = "is a partially connected facility";
		} else if (IVRLfacsConstants.XFLD.equals(statusCode)) {
			status = "is cross connected in field";
		} else if (IVRLfacsConstants.XCON.equals(statusCode)) {
			status = "is cross connected";
		} else if (IVRLfacsConstants.DEF.equals(statusCode)) {
			status = "is defective";
		} else if (IVRLfacsConstants.CT.equals(statusCode)) {
			status = "is connected through to this addesss";
		} else if (IVRLfacsConstants.RCF.equals(statusCode)) {
			status = "is a remotely-connected facility";
		} else if (IVRLfacsConstants.RCF_STAR.equals(statusCode)) {
			status = "is a remotely connected facility to this address";
		} else if (IVRLfacsConstants.DEF_STAR.equals(statusCode)) {
			status = "is defective at this address";
		} else if (IVRLfacsConstants.PCOM.equals(statusCode)) {
			status = "is a primary committed pair";
		} else if (IVRLfacsConstants.PCOM_STAR.equals(statusCode)) {
			status = "is a primary committed pair at this address";
		} else if (IVRLfacsConstants.SCOM.equals(statusCode)) {
			status = "is a secondary committed pair";
		} else if (IVRLfacsConstants.SCOM_STAR.equals(statusCode)) {
			status = "is a secondary committed pair at this address";
		} else if (IVRLfacsConstants.PCF_STAR.equals(statusCode)) {
			status = "is a partially-connected facility to this address";
		} else if (IVRLfacsConstants.CT_STAR.equals(statusCode)) {
			status = "is connected through to this address";
		} else if (IVRLfacsConstants.CF_STAR.equals(statusCode)) {
			status = "is a connected facility to this address";
		} else if (IVRLfacsConstants.RCT.equals(statusCode)) {
			status = "is remote connect through facility";
		}
		return status;

	}

	public IVRWebHookResponseDto checkSparePairColourCode(String colourCode, String colourType,
														  IVRWebHookResponseDto response) {

		String hookReturnCode = "";
		String hookReturnMessage = "";
		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		if (StringUtils.isNotBlank(colourCode) && colourCode.equals(IVRConstants.DTMF_INPUT_1)) {

			hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_5;
			hookReturnMessage = IVRConstants.WHITE_TRACER_MSG;
			IVRParameter parameter = new IVRParameter();
			parameter.setData(IVRLfacsConstants.WHITE_TRACER_MSG + colourCode);
			parameterList.add(parameter);

		} else if (StringUtils.isNotBlank(colourCode) && colourCode.equals(IVRConstants.DTMF_INPUT_2)) {

			hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_6;
			hookReturnMessage = IVRConstants.RED_TRACER_MSG;
			IVRParameter parameter = new IVRParameter();
			parameter.setData(IVRLfacsConstants.RED_TRACER_MSG + colourCode);
			parameterList.add(parameter);

		} else if (StringUtils.isNotBlank(colourCode) && colourCode.equals(IVRConstants.DTMF_INPUT_3)) {

			hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_7;
			hookReturnMessage = IVRConstants.BLACK_TRACER_MSG;
			IVRParameter parameter = new IVRParameter();
			parameter.setData(IVRLfacsConstants.BLACK_TRACER_MSG + colourCode);
			parameterList.add(parameter);

		} else if (StringUtils.isNotBlank(colourCode) && colourCode.equals(IVRConstants.DTMF_INPUT_4)) {

			hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_4;
			hookReturnMessage = IVRConstants.BINDING_POST_TONE;

		}

		else if (StringUtils.isNotBlank(colourCode) && colourCode.equals(IVRConstants.DTMF_INPUT_5)) {

			hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_1;
			hookReturnMessage = IVRConstants.BINDING_POST;
			IVRParameter parameter = new IVRParameter();
			parameter.setData(IVRLfacsConstants.BINDING_POST + colourCode);
			parameterList.add(parameter);

		} else if (StringUtils.isNotBlank(colourCode) && colourCode.equals(IVRConstants.DTMF_INPUT_6)) {

			hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_3;
			hookReturnMessage = IVRConstants.COLOR_CODE;
			IVRParameter parameter = new IVRParameter();
			parameter.setData(IVRLfacsConstants.COLOR_CODE + colourCode);
			parameterList.add(parameter);

		}

		else {

			hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_2;
			hookReturnMessage = IVRConstants.NO_BINDING;

		}

		response.setParameters(parameterList);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);

		return response;
	}

	/**
	 * Primarily to build the request body for Defective Pairs
	 *
	 * @param cableId
	 * @param npa
	 * @param nxx
	 * @param lfacsEmployeeCode
	 * @param cablePair
	 * @return
	 **/
	public DefectivePairsRequestDto buildDefectivePairsRequest(String cableId, String npa, String nxx,
															   String lfacsEmployeeCode, String cablePair) {

		DefectivePairsRequestDto defectivePairRequest = new DefectivePairsRequestDto();
		//setRequestCommonDetails(defectivePairRequest);

		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa(npa);
		wireCtrPrimaryNPANXX.setNxx(nxx);

		CablePairRange cablePairRange = new CablePairRange();
		cablePairRange.setLowPair(Integer.parseInt(cablePair) <= 9999 ? Integer.parseInt(cablePair) : 9999);
		cablePairRange.setLowPairSpecified(Boolean.TRUE);
		cablePairRange.setHighPair(cablePairRange.getLowPair() <= 9899 ? cablePairRange.getLowPair() + 100 : 9999);
		cablePairRange.setHighPairSpecified(Boolean.TRUE);

		DefectivePairsInputData defectivePairInputData = new DefectivePairsInputData.Builder().cableId(cableId)
				.cablePairRange(cablePairRange).lfacsEmployeeCode(lfacsEmployeeCode)
				.wireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX).build();

		defectivePairRequest.setInputData(defectivePairInputData);

		return defectivePairRequest;

	}

	// here we are setting all the common details which we are sending to wrapper
//	private void setRequestCommonDetails(BaseRequestDto baseRequestDto) {
//		baseRequestDto.setRequestId(IVRConstants.WRAPPER_API_REQUEST_ID);
//		baseRequestDto.setWebServiceName(IVRConstants.WRAPPER_API_WEB_SERVICE_NAME);
//		baseRequestDto.setRequestPurpose(IVRConstants.WRAPPER_API_REQUEST_PURPOSE);
//
//		AuthorizationInfo authInfo = new AuthorizationInfo();
//		authInfo.setUserid(IVRConstants.WrapperAuthorizationInfo.USERID);
//		authInfo.setPassword(IVRConstants.WrapperAuthorizationInfo.PASSWORD);
//		baseRequestDto.setAuthorizationInfo(authInfo);
//
//		TargetSchemaVersionUsed schema = new TargetSchemaVersionUsed();
//		schema.setMajorVersionNumber(0);
//		schema.setMinorVersionNumber(0);
//
//		baseRequestDto.setTargetSchemaVersionUsed(schema);
//		baseRequestDto.setTimeOutSecond(180);
//
//	}

	public boolean isSeviceOrder(CurrentAssignmentResponseDto currentAssignmentResponseDto) {

		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty()
				&& StringUtils.isNotBlank(
				currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getORD())) {

			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	public boolean isLineStationTransfer(CurrentAssignmentResponseDto currentAssignmentResponseDto) {

		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty()
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getLSTFN() != null) {

			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

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

	public int getSegmentFromCablePair(CurrentAssignmentResponseDto currResp, String cable, String pair) {
		String segNoStr = currResp.getReturnDataSet().getLoop().get(0).getSEG().stream().filter(SEG -> {
			if (SEG.getCA().equalsIgnoreCase(cable) && SEG.getPR().equalsIgnoreCase(pair))
				return true;
			else
				return false;
		}).map(SEG -> SEG.getSEGNO()).findFirst().get();

		return Integer.parseInt(segNoStr);

	}

	public void issueCutPageSent(IVRUserSession session) {
		String empid = session.getEmpID();
		boolean cutPageFlag = session.isCutPageSent();
		String cutPageFlagStr = cutPageFlag ? "Y" : "N";
		int rows = fastIvrDBInterface.updateCutPageFlagByEmpID(cutPageFlagStr, empid);

	}

	public Integer getUndesirableCandPair(String reqType, List<CandidatePairInfo> candPairListRMT,
										  List<LoopAssignCandidatePairInfo> candPairList) {
		Integer count = 0;
		if ("RETRIEVE_MAINT_CHANGE".equals(reqType)) {
			if (candPairListRMT.size() > 0) {

				for (CandidatePairInfo cpi : candPairListRMT) {
					if (cpi.getPairSelectionInfo() != null && !cpi.getPairSelectionInfo().equals("")) {
						count++;
					}
				}
			}
		} else {
			if (candPairList.size() > 0) {
				for (LoopAssignCandidatePairInfo cpi : candPairList) {
					if (cpi.getPairSelectionField() != null && !cpi.getPairSelectionField().equals("")) {
						count++;
					}
				}
			}
		}
		return count;
	}

	public Integer getCandPairCT(String reqType, List<CandidatePairInfo> candPairListRMT,
								 List<LoopAssignCandidatePairInfo> candPairList) {
		Integer count = 0;
		if ("RETRIEVE_MAINT_CHANGE".equals(reqType)) {
			if (candPairListRMT.size() > 0) {

				for (CandidatePairInfo cpi : candPairListRMT) {
					if (cpi.getPairStatus() != null && cpi.getPairStatus().equals("CT")) {
						count++;
					}
				}
			}
		} else {
			if (candPairList.size() > 0) {
				for (LoopAssignCandidatePairInfo cpi : candPairList) {
					if (cpi.getCandidatePairStatus() != null && cpi.getCandidatePairStatus().equals("CT")) {
						count++;
					}
				}
			}
		}
		return count;
	}

	public int getSegmentList(CurrentAssignmentResponseDto currentAssignmentResponseDto) {

		List<SEG> segList = null;

		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()) {

			segList = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG();

		}
		return segList != null ? segList.size() : 0;
	}
}