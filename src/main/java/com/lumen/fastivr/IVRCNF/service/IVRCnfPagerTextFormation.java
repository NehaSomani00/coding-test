package com.lumen.fastivr.IVRCNF.service;

import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.CHANGE_LOOP_ASSIGNMENT_REQUEST;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.VERIFY_CUT_REQUEST;
import static com.lumen.fastivr.IVRUtils.IVRConstants.NET_MAIL_DEVICE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.NET_PHONE_DEVICE;

import java.net.http.HttpTimeoutException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCNF.Dto.ChangeLoopAssignmentRequestDto;
import com.lumen.fastivr.IVRCNF.Dto.ChangeLoopAssignmentResponseDto;
import com.lumen.fastivr.IVRCNF.Dto.InputData;
import com.lumen.fastivr.IVRCNF.Dto.VerifyCutInformationRequest;
import com.lumen.fastivr.IVRCNF.Dto.VerifyCutInformationResponse;
import com.lumen.fastivr.IVRCNF.entity.IVRCnfEntity;
import com.lumen.fastivr.IVRCNF.helper.IVRCnfHelper;
import com.lumen.fastivr.IVRCNF.utils.CNFPage;
import com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants;
import com.lumen.fastivr.IVRCNF.utils.IVRCnfUtilities;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.LOOP;
import com.lumen.fastivr.IVRDto.MessageStatus;
import com.lumen.fastivr.IVRDto.SEG;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentRequest;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentResponse;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRLfacsConstants;
import com.lumen.fastivr.httpclient.IVRHttpClient;

@Service
public class IVRCnfPagerTextFormation {

	private static final Logger LOGGER = LoggerFactory.getLogger(IVRCnfPagerTextFormation.class);

	private final ObjectMapper objectMapper;

	private final IVRLfacsServiceHelper ivrLfacsServiceHelper;

	@Autowired
	private IVRHttpClient ivrHttpClient;

	@Autowired
	private IVRCnfHelper ivrCnfHelper;

	@Autowired
	private IVRCnfUtilities ivrCnfUtilities;
	
	@Value("${cnf.verfiy.cut.time.limit}")
	private int verifyCutServiceLimit;	

	@Value("${cnf.change.loop.assignment.url}")
	private String changeLoopAssignmentUrl;		

	@Value("${cnf.verify.cut.service.url}")
	private String verifyCutServiceUrl;	

	public IVRCnfPagerTextFormation(@Lazy IVRLfacsServiceHelper ivrLfacsServiceHelper, @Lazy ObjectMapper objectMapper) {

		this.ivrLfacsServiceHelper = ivrLfacsServiceHelper;
		this.objectMapper = objectMapper;
	}	

	public ChangeLoopAssignmentResponseDto getPageChangeLoopAssignment(ChangeLoopAssignmentRequestDto request, IVRWebHookResponseDto response, IVRUserSession userSession,
			IVRCnfEntity cnfSession, String sessionId, String tn, String primaryNpa, String primaryNxx) throws JsonMappingException, JsonProcessingException, InterruptedException, HttpTimeoutException, ExecutionException {

		LOGGER.info("#### Starting to build the pager text for ChangeLoopAssignment ####");
		
		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		ChangeLoopAssignmentResponseDto changeLoopAssignmentResponseDto ;
		List<LOOP> loopList = null;
		List<SEG> segList = null;

		if (userSession != null && userSession.getCurrentAssignmentResponse() != null) {

			currentAssignmentResponseDto = objectMapper.readValue(userSession.getCurrentAssignmentResponse(),
					CurrentAssignmentResponseDto.class);
			
			loopList = currentAssignmentResponseDto.getReturnDataSet().getLoop();
		}

		//String transType = userSession.getFacsInqType();  ??
		String serviceOrderNumber = ivrCnfUtilities.getServiceOrder(loopList);
		String reqCircuit = (loopList.get(0).getCKID() != null) ? (loopList.get(0).getCKID()) : (loopList.get(0).getSO().get(0).getCKID());
		int segmentNumber = ivrCnfUtilities.getSegmentNumber(cnfSession);
		segList = ivrCnfUtilities.getSegmentList(currentAssignmentResponseDto);	
		SEG segment = segList.get(segmentNumber);		
		String currentCableId = segment.getCA();
		String currentCableUnitId =	segment.getPR();	
		String replacementCableId = request.getInputData().getChangeLoopAssignmentReqInputDataReplacementLoopDetails().getCableId();
		String replacementCableUnitId = request.getInputData().getChangeLoopAssignmentReqInputDataReplacementLoopDetails().getCableUnitId();
		String transType = "SEND"; // TODO check what to set  ????

		/* if the curr-assg-req got failed : send a error page (mobile/email) ->
		 * Create a empty object of changeLoopAssignmentResponse 
		 * Set the err msg from curr-assg-req into changeLoopAssignmentResponse 
		 * return changeLoopAssignmentResponse; */
		changeLoopAssignmentResponseDto = checkCurrentAssignmentReqFailed(userSession, currentAssignmentResponseDto, serviceOrderNumber, reqCircuit,
				currentCableId, currentCableUnitId, replacementCableId, replacementCableUnitId, transType);

		// 	Check for Ckid not as "" and NONE STAT is WKG:
		changeLoopAssignmentResponseDto = checkCircuitIDAndStatus(userSession, currentAssignmentResponseDto, loopList, serviceOrderNumber, replacementCableId,
				replacementCableUnitId, transType);

		/* 	Loops over each segment, and checks if the repl. pair & cable matches any of segments' pair and cable 
			-> note the segment number in which mathc happened
			-> break from the loop 
		 */
		int segIndex = 0;		

		for (int i = 0; i < segList.size(); i++) {
			if (segList != null && !segList.isEmpty() && (StringUtils.isNotBlank(segList.get(i).getPR())) 
					&& (StringUtils.isNotBlank(segList.get(i).getCA())) ) {	

				if ((replacementCableUnitId == segList.get(i).getCA()) &&  (replacementCableId == segList.get(i).getPR())) {
					segIndex = i;
					LOGGER.info("segIndex: -" + segIndex + "-" );
					break;
				}
			}
		}


		/* 	If the status is PCF, CF, CT
		 * -> then they are seting the new BP, TEA from the exisiting if present
		 * -> Setting serviceAddress from curr-assg-resp 
		 */
		String status = loopList.get(0).getSTAT() != null ? loopList.get(0).getSTAT() : loopList.get(0).getSO().get(0).getSTAT();
		String replacementBindingPostColorCode = "";
		String replacementTerminalAddress = "";
		String serviceAddress = "";

		// PCF
		if (status.equalsIgnoreCase("PCF")) {

			LOGGER.info("PCF - replacementCableId : " + replacementCableId);
			LOGGER.info("PCF - replacementCableUnitId : " + replacementCableUnitId);

			if( (segList.get(segIndex).getBP() != null)  && (StringUtils.isNotBlank(segList.get(segIndex).getBP())) ) {
				replacementBindingPostColorCode = segList.get(segIndex).getBP();
			}

			LOGGER.info("PCF - replacementBindingPostColorCode : " + replacementBindingPostColorCode);

			if( (segList.get(segIndex).getTEA() != null)  && (StringUtils.isNotBlank(segList.get(segIndex).getTEA())) ) {
				replacementTerminalAddress = segList.get(segIndex).getTEA();
			}

			LOGGER.info("PCF - replacementTerminalAddress : " + replacementTerminalAddress);

			serviceAddress = ivrCnfUtilities.getServiceAddress(currentAssignmentResponseDto);
		}


		// CF OR CT
		if((status.equalsIgnoreCase("CF")) || (status.equalsIgnoreCase("CT"))) {

			LOGGER.info("CForCT - replacementCableId : " + replacementCableId);
			LOGGER.info("CForCT - replacementCableUnitId : " + replacementCableUnitId);

			if ((segList.get(segIndex).getBP() != null) && (!segList.get(segIndex).getBP().equals(""))) {
				replacementBindingPostColorCode = segList.get(segIndex).getBP();
			}
			LOGGER.info("CForCT - replacementBindingPostColorCode : " + replacementBindingPostColorCode);

			if ((segList.get(segIndex).getTEA() != null) && (!segList.get(segIndex).getTEA().equals(""))) {
				replacementTerminalAddress = segList.get(segIndex).getTEA();
			}

			LOGGER.info("CForCT - replacementTerminalAddress : " + replacementTerminalAddress);

			serviceAddress = ivrCnfUtilities.getServiceAddress(currentAssignmentResponseDto);
		}


		// if((statFromInqFasg == "DEF"))
		// 	If status is DEF: -> call the  Change pair status inq req api
		//  
		// 
		//
		//
		//


		/* 	STARTING TO REPLACE THE PAIR
		 *  ->call the change loop assgn inq req  api.
		 * -> CHeck for Error scenario:
			  -> construct the error page and send to mobile/email
			  -> return changeLoopAssignmentResponse;  
		 */
		LOGGER.info("STARTING TO REPLACE THE PAIR");

		String jsonRequest = objectMapper.writeValueAsString(request);

		String changeLoopAssignmentResultJson = ivrHttpClient.httpPostCall(jsonRequest, changeLoopAssignmentUrl,
				sessionId, CHANGE_LOOP_ASSIGNMENT_REQUEST);

		String cleanJsonStr = ivrLfacsServiceHelper.cleanResponseString(changeLoopAssignmentResultJson);

		cnfSession.setGetMntReplPrsResponse(cleanJsonStr);

		ChangeLoopAssignmentResponseDto responseObject = objectMapper.readValue(cleanJsonStr,
				ChangeLoopAssignmentResponseDto.class);

		if (responseObject.getReturnDataSet() == null || !"S".equalsIgnoreCase(responseObject.getMessageStatus().getErrorStatus()))
		{
			String strPageText = "";
			
			if(responseObject.getMessageStatus() != null && responseObject.getMessageStatus().getHostErrorList() == null && "4469".equals(responseObject.getMessageStatus().getErrorCode())) {
				
				strPageText =  responseObject.getMessageStatus().getErrorMessage();
			} else if (responseObject.getMessageStatus().getHostErrorList().get(0).getErrorList() != null 
					&& !responseObject.getMessageStatus().getHostErrorList().get(0).getErrorList().isEmpty() && responseObject.getMessageStatus().getHostErrorList().get(0).getErrorList().get(0).getErrorMessage().indexOf("L150-451") >= 0) {
				
				strPageText = responseObject.getMessageStatus().getHostErrorList().get(0).getErrorList().get(0).getErrorMessage();
			} else if ((responseObject.getReturnDataSet() != null) 
					&& (responseObject.getReturnDataSet().getRequestStatus() != "TRANSACTION QUEUED FOR PROCESSING")) {
				strPageText = "Unable to replace the pair. Call assignment";

			} else if (((responseObject.getReturnDataSet() != null) && responseObject.getReturnDataSet().getTerminalId() != null) 
					&& (responseObject.getReturnDataSet().getTerminalId().length() > 0)) {
				
				LOGGER.info("response.ReturnDataSet.TerminalId :-" + responseObject.getReturnDataSet().getTerminalId() + "-");

				strPageText = "MULTILEG CIRCUIT ERROR";

			} else {
				strPageText = responseObject.getMessageStatus().getErrorMessage();
			}

			CNFPage cnfPage = new CNFPage("SO Cut", reqCircuit, currentCableId, currentCableUnitId,
					replacementCableId, replacementCableUnitId, serviceOrderNumber, transType, "");

			String pagerText = cnfPage.fmtErr(strPageText);  

			if (userSession.isCanBePagedMobile()) {				

				ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_CUT_TO_NEW_FACILITY_INQUIRY,
						pagerText, NET_PHONE_DEVICE, userSession);
			}

			if (userSession.isCanBePagedEmail()) {
				ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_CUT_TO_NEW_FACILITY_INQUIRY,
						pagerText, NET_MAIL_DEVICE, userSession);
			}				
			
			LOGGER.info("REPLACING THE PAIR WITH HELP of ChangeLoopAssignmentResponse COMPLETED");			
			
			return responseObject;
		}

		/* ****************************************************************************** 
		 * 	If the response is good, we check for verify Cut LFACS .
		 *  	-> Construct the object for verifyCut facs 
		 		-> Wait for 1 minute : Thread.Sleep(60000);
			-> Call : csSoInqTnTranVfy.GetRetrieveLoopAssignmentAsync();
				-> If the response is success then check for cutVerified 
					-> Call the csSoInqTnTranVfy.GetRetrieveLoopAssignmentAsync in a interval of 30 secs for 4 times,and each time check if the replacement cable & pair is same as the resp-> cable and pair. 
					-> if matches then set cutVerified as true and break, else continue 
		/* ******************************************************************************  */
		int waitTimeCountMax = 4;  // 60 Sec at Top + 4 x 30 sec = 180 sec per Gap code  BRB 
		int waitTimeCount = 0;
		boolean cutVerified = false;

		LOGGER.info("Validating cutVerified or not using RetrieveLoopAssignment");
		
		RetrieveLoopAssignmentRequest retrieveLoopAssignmentRequest = ivrCnfHelper.buildRetriveLoopAssignInqRequest(request, tn, primaryNpa, primaryNxx, cnfSession);
		retrieveLoopAssignmentRequest.getInputData().setServiceOrderNumber(serviceOrderNumber);
		retrieveLoopAssignmentRequest.getInputData().setCircuitId(reqCircuit);
		retrieveLoopAssignmentRequest.getInputData().setRetrieveActionCode("RSOCP");
		//Thread.sleep(60000); // sleep for 60 seconds
		String retrieveLoopAssignmentJsonRequest = objectMapper.writeValueAsString(retrieveLoopAssignmentRequest);
		String responseString = ivrLfacsServiceHelper.callSparePairLoopAssignInquiryLfacs(retrieveLoopAssignmentJsonRequest, userSession);

		RetrieveLoopAssignmentResponse retrieveLoopAssignmentResponse = objectMapper.readValue(responseString, RetrieveLoopAssignmentResponse.class);

		if (retrieveLoopAssignmentResponse != null) {

			userSession.setRtrvLoopAssgMsgName(responseString);
		}

		//(csSoInqTnTranVfy.GetTranStatus() == (int)TranStatus.e_REQ_SUCCESS) &&
		while((waitTimeCount < waitTimeCountMax) && (retrieveLoopAssignmentResponse.getMessageStatus().getErrorMessage().isBlank())) { 

			if((retrieveLoopAssignmentResponse.getReturnDataSet().getCandidatePairInfo().get(segmentNumber).getCableId() != replacementCableId ) || 
					(retrieveLoopAssignmentResponse.getReturnDataSet().getCandidatePairInfo().get(segmentNumber).getCableUnitId() != replacementCableUnitId )) {

				LOGGER.info("Comparing to CableID : -" + retrieveLoopAssignmentResponse.getReturnDataSet().getCandidatePairInfo().get(segmentNumber).getCableId() + "-");
				LOGGER.info("Comparing to CableUnitID : -" + retrieveLoopAssignmentResponse.getReturnDataSet().getCandidatePairInfo().get(segmentNumber).getCableUnitId() + "-");
				LOGGER.info("instReplacePr so_inq_tn CA/PR not MATCH") ;

				cutVerified = false;

				// Thread.sleep(30000); // TODO: Commenting this line as our backend API services are giving response promptly.

				waitTimeCount ++;

				LOGGER.info(" waitTimeCount : -" + waitTimeCount +"-");
			} else {
				cutVerified = true;
				LOGGER.info("cutVerified is true, breaking the loop");
				break;
			}
		}


		// (csSoInqTnTranVfy.GetTranStatus() == (int)TranStatus.e_REQ_FAILURE) ||
		if ( !retrieveLoopAssignmentResponse.getMessageStatus().getErrorMessage().isEmpty()  || (!cutVerified)) {

			String temp = "";

			if (!retrieveLoopAssignmentResponse.getMessageStatus().getErrorMessage().isEmpty())
			{
				if(retrieveLoopAssignmentResponse.getMessageStatus().getHostErrorList().get(0).getErrorList().size() > 0)
					temp = retrieveLoopAssignmentResponse.getMessageStatus().getHostErrorList().get(0).getErrorList().get(0).getErrorMessage();
				else
					temp = "Unable to verify cut in LFACS. Call assignment";

				LOGGER.info("vfyCutLFACS transaction Failed ErrorMsg -" + temp);

			} else {

				MessageStatus messageStatus = new MessageStatus();
				messageStatus.setErrorMessage("Unable to verify cut in LFACS. Call assignment");

				LOGGER.info( "vfyCutLFACS  - Unable to verify cut in LFACS. Call assignment");

				retrieveLoopAssignmentResponse.setMessageStatus(messageStatus);
				temp = "Unable to verify cut in LFACS. Call assignment";		
			}

			CNFPage cnfPage = new CNFPage("CUT", reqCircuit, replacementCableId, replacementCableUnitId, serviceOrderNumber, transType, "");
			String pagerText = cnfPage.fmtErr(temp);			

			if (userSession.isCanBePagedMobile()) {				

				ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_CUT_TO_NEW_FACILITY_INQUIRY,
						pagerText, NET_PHONE_DEVICE, userSession);
			}

			if (userSession.isCanBePagedEmail()) {
				ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_CUT_TO_NEW_FACILITY_INQUIRY,
						pagerText, NET_MAIL_DEVICE, userSession);
			}					

		} else {

			LOGGER.info("Successfully Verified CUT in LFACS");

			String stat = ivrCnfUtilities.getCablePairStatus(currentAssignmentResponseDto);
			String bpType = ivrCnfUtilities.getBpType(segIndex+1, currentAssignmentResponseDto);
			serviceAddress = ivrCnfUtilities.getServiceAddress(currentAssignmentResponseDto);

			String pagerText = getPageCutVerifiedRR(reqCircuit, serviceOrderNumber, segmentNumber, 
					currentCableId, currentCableUnitId, replacementCableId, 
					replacementCableUnitId, stat, bpType, 
					replacementBindingPostColorCode, replacementTerminalAddress, serviceAddress);

			//Successfully Verified CUT in LFACS
			if (userSession.isCanBePagedMobile()) {
				ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_CUT_TO_NEW_FACILITY_INQUIRY,
						pagerText, NET_PHONE_DEVICE, userSession);
			}

			if (userSession.isCanBePagedEmail()) {
				ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_CUT_TO_NEW_FACILITY_INQUIRY,
						pagerText, NET_MAIL_DEVICE, userSession);
			}			
		} 


		/* ****************************************************************************** 
		 * 	For F1 cut, call verifyCutSwitch()
		 * 	       		->here u call csVerifyCut.GetVerifyCutInformationInquiry api
		 * 				-> Call this api for 4 times at 30secs interval
		 * 				-> in response check for ReturnDataSet.MatchFoundFlag
		 * 					-> 			-> If true, break the loop 
		 * 
		 *  	-> If all the calls are failed, then form a error page and send to mobile/email 		
		 * 
		 *******************************************************************************/

		LOGGER.info("***Verify cut switch***");

		LOGGER.info("SegmentNumber Inst : -" + segmentNumber +"-");

		if (segmentNumber == 0)  // For F1 cut.
		{
			LOGGER.info("if F1 cut...");
			
			boolean isMcut = false;

			VerifyCutInformationRequest verifyCutInformationRequest = ivrCnfHelper.buildVerifyCutRequest(tn, primaryNpa,primaryNxx, cnfSession);

			String jsonReq = objectMapper.writeValueAsString(verifyCutInformationRequest);

			String verifyJsonResult = ivrHttpClient.httpPostCall(jsonReq, verifyCutServiceUrl, sessionId,
					VERIFY_CUT_REQUEST);

			String cleanJsonString = ivrLfacsServiceHelper.cleanResponseString(verifyJsonResult);

			cnfSession.setGetVerifyCutResponse(cleanJsonString);

			VerifyCutInformationResponse verifyCutInformationResponse = objectMapper.readValue(cleanJsonString,
					VerifyCutInformationResponse.class);

			verifyCutSwitch(isMcut, verifyCutInformationResponse, reqCircuit, replacementCableId, replacementCableUnitId, serviceOrderNumber, userSession);
		}		

		LOGGER.info("***Verify Replace Pair Successful***");

		
		LOGGER.info("#### Building the pager text for ChangeLoopAssignment completed ####");
		
		return changeLoopAssignmentResponseDto;
	}

	public ChangeLoopAssignmentResponseDto checkCircuitIDAndStatus(IVRUserSession userSession,
			CurrentAssignmentResponseDto currentAssignmentResponseDto, List<LOOP> loopList, String serviceOrderNumber,
			String replacementCableId, String replacementCableUnitId, String transType) throws JsonProcessingException {

		LOGGER.info("Checking CircuitID And Status...");

		ChangeLoopAssignmentResponseDto changeLoopAssignmentResponseDto = new ChangeLoopAssignmentResponseDto();

		String circuitID = loopList.get(0).getCKID() != null ? loopList.get(0).getCKID() : loopList.get(0).getSO().get(0).getCKID();
		String status = loopList.get(0).getSTAT() != null ? loopList.get(0).getSTAT() : loopList.get(0).getSO().get(0).getSTAT();

		if( (!circuitID.isEmpty()) && (!circuitID.isBlank()) || (status.equalsIgnoreCase("WKG"))) {
			StringBuilder temp = new StringBuilder();
			temp.append(replacementCableId);
			temp.append("/");
			temp.append(replacementCableUnitId);
			temp.append(" already on working circuit ");
			temp.append(circuitID);

			CNFPage cnfPage = new CNFPage("SO Cut", circuitID, transType, "");
			cnfPage.setReqOrder(serviceOrderNumber);
			String pagerText = cnfPage.fmtErr(temp.toString());

			if (userSession.isCanBePagedMobile()) {				

				ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_CUT_TO_NEW_FACILITY_INQUIRY,
						pagerText, NET_PHONE_DEVICE, userSession);
			}

			if (userSession.isCanBePagedEmail()) {
				ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_CUT_TO_NEW_FACILITY_INQUIRY,
						pagerText, NET_MAIL_DEVICE, userSession);
			}			


			changeLoopAssignmentResponseDto = new ChangeLoopAssignmentResponseDto();
			MessageStatus messageStatus = new MessageStatus();
			messageStatus =  currentAssignmentResponseDto.getMessageStatus();
			messageStatus.setErrorMessage(temp.toString());
			changeLoopAssignmentResponseDto.setMessageStatus(messageStatus);
			return changeLoopAssignmentResponseDto;
		}
		
		LOGGER.info("Validation of CircuitID and Status got completed");
		
		return changeLoopAssignmentResponseDto;
	}

	public ChangeLoopAssignmentResponseDto checkCurrentAssignmentReqFailed(IVRUserSession userSession,
			CurrentAssignmentResponseDto currentAssignmentResponseDto, String serviceOrderNumber, String reqCircuit,
			String currentCableId, String currentCableUnitId, String replacementCableId, String replacementCableUnitId,
			String transType) throws JsonProcessingException {

		LOGGER.info("Checking CurrentAssignment Request Failed or not...");

		ChangeLoopAssignmentResponseDto changeLoopAssignmentResponseDto = new ChangeLoopAssignmentResponseDto();

		if(currentAssignmentResponseDto.getMessageStatus().getErrorStatus().equalsIgnoreCase("F") && 
				(!currentAssignmentResponseDto.getMessageStatus().getErrorMessage().isEmpty() ) ) {

			if (userSession.isCanBePagedMobile() || userSession.isCanBePagedEmail()) {

				CNFPage cnfPage  = new CNFPage("SO Cut", reqCircuit, currentCableId, currentCableUnitId, replacementCableId,
						replacementCableUnitId, serviceOrderNumber, transType, "");

				String pagerText = cnfPage.fmtErr(currentAssignmentResponseDto.getMessageStatus().getHostErrorList().get(0).getErrorList().get(0).getErrorMessage());

				if (userSession.isCanBePagedMobile()) {
					ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_CUT_TO_NEW_FACILITY_INQUIRY,
							pagerText, NET_PHONE_DEVICE, userSession);
				}

				if (userSession.isCanBePagedEmail()) {
					ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_CUT_TO_NEW_FACILITY_INQUIRY,
							pagerText, NET_MAIL_DEVICE, userSession);
				}					

			}

			changeLoopAssignmentResponseDto = new ChangeLoopAssignmentResponseDto();
			MessageStatus messageStatus = new MessageStatus();
			messageStatus =  currentAssignmentResponseDto.getMessageStatus();
			changeLoopAssignmentResponseDto.setMessageStatus(messageStatus);
			return changeLoopAssignmentResponseDto;	
		}
		
		LOGGER.info("Validation of CurrentAssignment Request Failed or not completed");
		
		return changeLoopAssignmentResponseDto;
	}

	/* 
	 * If Verified CUT is Successfully Verified in LFACS, 
	 * then create cut verified page.
	 * 
	 */
	private String getPageCutVerifiedRR(String circuitId, String serviceOrderNumber, int segmentNumber, 
			String currentCableId, String currentCableUnitId, String replacementCableId, 
			String replacementCableUnitId, String stat, String bpType, 
			String replacementBindingPostColorCode, String replacementTerminalAddress, String serviceAddress) {

		StringBuilder pageMessage = new StringBuilder();

		LOGGER.info("Constructing PageResults for CutVerifiedRR...");

		if (circuitId.length() >= 12)
		{
			pageMessage.append(circuitId);

		} else {
			pageMessage.append("NONE");
		}

		pageMessage.append(" CUT");

		if(serviceOrderNumber.length() > 0) {
			pageMessage.append("|").append(serviceOrderNumber.trim());
		}

		pageMessage.append("|f").append(Integer.toString(segmentNumber + 1))
		.append(": ").append(currentCableId.trim()).append("/")
		.append(currentCableUnitId.trim())
		.append("|to: ")
		.append(replacementCableId.trim()).append("/").append(replacementCableUnitId.trim()).append(" ");

		pageMessage.append(stat);

		pageMessage.append("|LFACS is updated.");

		if (stat.equals("CF") || stat.equals("CT") || stat.equals("PCF")) {

			pageMessage.append("|Break ").append(stat);

			if(bpType.equals("BP") ) {

				pageMessage.append(" BP");
			}

			if(replacementBindingPostColorCode.length() > 0) {

				pageMessage.append(" ").append(replacementBindingPostColorCode.trim());
			}

			pageMessage.append(" at tea: ").append(replacementTerminalAddress.trim());
			pageMessage.append("|addr: ").append(" ").append(serviceAddress);
		}

		LOGGER.debug("pageMessage: "+pageMessage);
		LOGGER.info("PageResults for CutVerifiedRR completed...");

		return pageMessage.toString();
	}


	private void verifyCutSwitch(boolean isMcut, VerifyCutInformationResponse verifyCutInformationResponse, String circuitId, 
			String replacementCableId, String replacementCableUnitId, String serviceOrderNumber, IVRUserSession userSession ) throws InterruptedException, JsonProcessingException {

		LOGGER.info("Starting to verify cut switch");
		
		int retryCount = 4;
		int count = 0;
		boolean result = false;
		String currentTrans = "";

		// Run Switch VerifyCut 2 times to see if update occurred
		while((count < retryCount) && !result) {

			Thread.sleep(verifyCutServiceLimit); // wait for 3- seconds before attempting verification per attempt
			count++;

			if (verifyCutInformationResponse != null && 
					IVRLfacsConstants.S.equals(verifyCutInformationResponse.getMessageStatus().getErrorStatus())) {

				result = true;
				break;
			}
		}

		if (!result) {

			LOGGER.info("*** Failed to verify the cut in SWITCH ***");

			String cutOrMcut = "";
			if (isMcut)
			{
				currentTrans = IVRCNFConstants.MNT_REPLACE_PR;
				cutOrMcut = "M CUT";
			}
			else
			{
				currentTrans = IVRCNFConstants.INST_REPLACE_PR;
				cutOrMcut = "CUT";
			}

			CNFPage cnfPage = new CNFPage(cutOrMcut, circuitId, replacementCableId, replacementCableUnitId, serviceOrderNumber, currentTrans, "");
			String pagerText = cnfPage.fmtErr("Unable to verify frame ticket in SWITCH. Call assignment");		

			if (userSession.isCanBePagedMobile()) {
				ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_CUT_TO_NEW_FACILITY_INQUIRY,
						pagerText, NET_PHONE_DEVICE, userSession);
			}

			if (userSession.isCanBePagedEmail()) {
				ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_CUT_TO_NEW_FACILITY_INQUIRY,
						pagerText, NET_MAIL_DEVICE, userSession);
			}					
		}

		LOGGER.info("VerifyCutSwitch results : " + result);
	}


}

