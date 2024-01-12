package com.lumen.fastivr.IVRMLT.helper;

import static com.lumen.fastivr.IVRMLT.utils.IVRMltConstants.MLT_ACK_SUCCESS;
import static com.lumen.fastivr.IVRMLT.utils.IVRMltConstants.MLT_ISSUE_FAILURE;
import static com.lumen.fastivr.IVRMLT.utils.IVRMltConstants.MLT_ISSUE_SUCCESS;
import static com.lumen.fastivr.IVRMLT.utils.IVRMltUtilities.FULLX_TEST;
import static com.lumen.fastivr.IVRMLT.utils.IVRMltUtilities.LOOPX_TEST;
import static com.lumen.fastivr.IVRMLT.utils.IVRMltUtilities.QUICKX_TEST;
import static com.lumen.fastivr.IVRMLT.utils.IVRMltUtilities.TONE_PLUS_TEST;
import static com.lumen.fastivr.IVRMLT.utils.IVRMltUtilities.TONE_REMOVAL_TEST;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_0;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_1;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_2;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_3;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_4;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_5;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_7;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_8;

import java.net.MalformedURLException;
import java.net.http.HttpTimeoutException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.LOSDB.CTelephone;
import com.lumen.fastivr.IVRMLT.Dto.CTelephoneBuilderMlt;
import com.lumen.fastivr.IVRMLT.caching.IvrMltCacheService;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRMLT.exception.InvalidNPANXXException;

import com.lumen.fastivr.IVRMLT.utils.IVRMltConstants;
import com.lumen.fastivr.IVRMLT.utils.IvrMltPager;

import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;

import tollgrade.loopcare.testrequestapi.APIServerException;
import tollgrade.loopcare.testrequestapi.DataChanObjRef;
import tollgrade.loopcare.testrequestapi.MLTTESTACK;
import tollgrade.loopcare.testrequestapi.MLTTESTREQ;
import tollgrade.loopcare.testrequestapi.MLTTESTREQCIDFMTP;
import tollgrade.loopcare.testrequestapi.MLTTESTREQDATA;
import tollgrade.loopcare.testrequestapi.MLTTESTREQHDR;
import tollgrade.loopcare.testrequestapi.MLTTESTREQKEY;
import tollgrade.loopcare.testrequestapi.MLTTESTREQKEYCID;
import tollgrade.loopcare.testrequestapi.Mdata;
import tollgrade.loopcare.testrequestapi.RequestToMLTOperations;
import tollgrade.loopcare.testrequestapi.RequestToMLTreject;

@Service
public class IvrMltLoopCareServices {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IvrMltLoopCareServices.class);
	
	@Autowired
	private IVRCacheService ivrCacheService;
	@Autowired
	private IvrMltCacheService mltCacheService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private IvrLoopCareAsyncService asyncService;
	
	@Autowired
	private IvrLoopCareOperations loopCareOperations;
	
	@Autowired
	private IvrMltSoapApiGenerator mltSoapApiGenerator;
	
	@Autowired
	private IvrMltHelper mltHelper;
	

	@Autowired
	private Jaxb2Marshaller jaxb2Marshaller;
	
	@Autowired
	MltPagerText mltPagerText;
	
	@Autowired
	IvrMltPager ivrMltPager;
	

	
	//////////////////////////////////////////////////////////////////////
	//Function: PrepareRequest
	//Description:
	//This method Issues the MLTTest Request by calling the method
	//IssueMltTestReq and then decides the appropriate hook return code
	//as follows:
	//Alpha page		Calling From the Same Line		Hook Return Code
	//----------		--------------------------		----------------
	//Yes				No								0
	//Yes				Yes								2
	//No				No								1
	//No				Yes								3
	//If the transaction could not be issued, then Hook Return code is 4
	//Precondition:None.
	//Warnings:None.
	//Return:Returns the hook return code
	//For Tone Removal (X request): 1(success) or 0(failure)
	//For invalid npa-nxx combination: returns 5
	//
	//////////////////////////////////////////////////////////////////////
	public String prepareRequest(String sessionId) throws JsonMappingException, JsonProcessingException  {
		LOGGER.info("Session id: "+ sessionId+" prepareRequest");
		IVRUserSession ivrUserSession = ivrCacheService.getBySessionId(sessionId);
		IvrMltSession mltUserSession = mltCacheService.getBySessionId(sessionId);
		String returnCode = HOOK_RETURN_0 ; // Successful request, calling from different line
		
		//generate the Soap apis and store the proxy urls in cache
		try {
			mltSoapApiGenerator.generateLoopCareSoapApi(sessionId);
			
		} catch (JsonProcessingException | HttpTimeoutException e) {
			LOGGER.error("Unable to Generate the LoopCare Soap apis ", e);
			return HOOK_RETURN_4;
		} catch (InvalidNPANXXException e) {
			//invalid npa-nxx combination
			return HOOK_RETURN_5;
		} catch (Exception e) {
			LOGGER.error("Unable to Generate the LoopCare Soap apis ", e);
			return HOOK_RETURN_4;
		}
		
		switch(mltUserSession.getTestType()) {
		case QUICKX_TEST:
		case LOOPX_TEST:
		case FULLX_TEST:
		case TONE_PLUS_TEST:
			returnCode = issueMltRequest_ValidatingPageCallLine(sessionId, ivrUserSession, mltUserSession);
			break;
		
		case TONE_REMOVAL_TEST:
			returnCode = issueMltTestReq(sessionId);
			break;
		}
		
		return returnCode;
	}


	private String issueMltRequest_ValidatingPageCallLine(String sessionId, IVRUserSession ivrUserSession, IvrMltSession mltUserSession) throws JsonMappingException, JsonProcessingException {
		String returnCode;
		if(ivrUserSession.isCanBePagedMobile()) {
			//User can be paged on mobile (alpha pager true condition)
			
			//issue the mlt request
			String mltRequestStatus = issueMltTestReq(sessionId);
			
			if(mltRequestStatus.equalsIgnoreCase(HOOK_RETURN_0)) {
				//request failed 
				returnCode = HOOK_RETURN_4;
				
			} else {
				 if(mltUserSession.getTechOnSameLine())  {
					 returnCode = HOOK_RETURN_2;  // Tech has an alpha pager and calling from same line.
				 }
				 else {
					 returnCode = HOOK_RETURN_0 ; // Successful request, calling from different line
				 }
				}
			
		} else {
			//User cannot be paged on Mobile (Alpha pager false condition)
			//issue the mlt request
			String mltRequestStatus = issueMltTestReq(sessionId);
			
			if(mltRequestStatus.equalsIgnoreCase(HOOK_RETURN_0)) {
				//request failed 
				returnCode = HOOK_RETURN_4;
				
			} else {
				 if(mltUserSession.getTechOnSameLine())  {
					 returnCode = HOOK_RETURN_3;   // Successful request, calling from the same line
				 
				 } else {
					 returnCode = HOOK_RETURN_1; // Successful request, calling from different line
				 }
				 
			}
		}
		return returnCode;
	}
	
	
	//////////////////////////////////////////////////////////////////////
	//Function: //IssueMltTestReq
	//Description:
	//Issue the Test Request for the given response type and for the 
	//given transaction.
	//Precondition:A valid session object exists for which the request needs to be issued.
	//Warnings:None.
	//Return:Returns the hook return code
	//		0 --> Failure
	//		1 --> Success
	//////////////////////////////////////////////////////////////////////
	/**
	 * Creates a data channel with MLT and sends the Test request
	 * @return
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public String issueMltTestReq(String sessionId) throws JsonMappingException, JsonProcessingException {
		LOGGER.info("Session id: "+ sessionId+" issueMltTestReq start");
		String returnCode = MLT_ISSUE_FAILURE;
		
		returnCode = initiateMltTest(sessionId);
		
		if (returnCode.equalsIgnoreCase(MLT_ISSUE_SUCCESS)) {
			//fetch response asynchronously
			CompletableFuture<String> response = asyncService.fetchMltTestResult(sessionId);
		}

		LOGGER.info("Session id: "+ sessionId+" issueMltTestReq end, Issue status: "+ returnCode);
		return returnCode;
	}
	
	/**
	 * Here we are sending request to create a data channel object using MDataChannelProxy endpoint 
	 * Then, using the data-channel id, we will send the Request payload over to TestRequestAPI endpoint 
	 * @param sessionId
	 * @return
	 * @throws APIServerException 
	 * @throws MalformedURLException 
	 * @throws RequestToMLTreject 
	 */
	private String initiateMltTest(String sessionId) {
		String returnCode = MLT_ISSUE_FAILURE; // success scenario

		try {
			// create data channel object
			createDataChannel(sessionId);

			// send test request
			returnCode = sendTestRequestProxy(sessionId) ? MLT_ISSUE_SUCCESS : MLT_ISSUE_FAILURE;

		} catch (MalformedURLException | APIServerException e) {
			e.printStackTrace();

		} catch (RequestToMLTreject e) {
			e.printStackTrace();
		}

		return returnCode;
	}

	private void createDataChannel(String sessionId) throws MalformedURLException, APIServerException {
		// TODO Auto-generated method stub
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		DataChanObjRef dataChannel = loopCareOperations
				.dataChannelFactoryOperations(mltSession.getDataChannelProxyUrl())
				.create();
		mltSession.setDatachannelId(dataChannel.getId());

		mltCacheService.updateSession(mltSession);

	}
	
	private boolean sendTestRequestProxy(String sessionId) throws MalformedURLException, RequestToMLTreject {
		
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		//prepare the request payload 
		String testType = mltSession.getTestType();
		MLTTESTREQ mltTestRequestPayload = null;
		
		switch(testType) {
		case QUICKX_TEST:
		case LOOPX_TEST:
		case FULLX_TEST:
			 mltTestRequestPayload = generateMltTestRequestPayload(sessionId);
			 break;
		case TONE_PLUS_TEST:
		case TONE_REMOVAL_TEST:
			mltTestRequestPayload = generateMltToneTestRequestPayload(sessionId);
			break;
			
		}
		
		try {
			String requestXmlStr = objectMapper.writeValueAsString(mltTestRequestPayload);
			LOGGER.info("Session: "+ sessionId + " MLT Request Body : "+requestXmlStr);
		} catch (JsonProcessingException e) {
			// Not a breaking exception, only for Logging purpose
			LOGGER.error("Error while converting MLT request body to json string ", e);
		}
		
		RequestToMLTOperations requestMltOps = loopCareOperations.mltOperations(mltSession.getTestRequestProxyUrl());
		MLTTESTACK mltTestAck = requestMltOps.sendTestRequest(mltTestRequestPayload);
		
		LOGGER.info("Session id:"+ sessionId+" Acknowledgement from MLT: Status: " + mltTestAck.getStatus());
		if(mltTestAck.getStatus().equalsIgnoreCase(MLT_ACK_SUCCESS)) {
			return true;
		}
		
		return false;
	}
	
	private MLTTESTREQ generateMltTestRequestPayload(String sessionId) {
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		String override = mltSession.getOverride();
		String tn = mltSession.getInquiredTn();
		CTelephone telephone = CTelephoneBuilderMlt.newBuilder(mltSession)
		.setTelephone(tn)
		.build();
		
		MLTTESTREQHDR mltTestReqHdr = new MLTTESTREQHDR();

		DataChanObjRef data = new DataChanObjRef();
		data.setId(mltSession.getDatachannelId());

		mltTestReqHdr.setTestRqstrType("FASTIVR");
		mltTestReqHdr.setTestRqstrId("requesterID");
		mltTestReqHdr.setTestSysId("LM50");
		mltTestReqHdr.setDataChanObjRef(data.getId());
		mltTestReqHdr.setApiversion("2.13");
		mltTestReqHdr.setStartTime(0);
		mltTestReqHdr.setStartTranTime(0);
		

		MLTTESTREQCIDFMTP fmtp = new MLTTESTREQCIDFMTP();
		fmtp.setNpa(telephone.getNpa());
		fmtp.setNnx(telephone.getNxx());
		fmtp.setLine(telephone.getLineNumber());
		fmtp.setChar1((byte) 0);

		MLTTESTREQKEYCID keyCid = new MLTTESTREQKEYCID();
		keyCid.setCktfmt((byte) 80);
		keyCid.setFmtP(fmtp);

		MLTTESTREQKEY key = new MLTTESTREQKEY();
		key.setChoice((short) 1);
		key.setCid(keyCid);

		MLTTESTREQDATA mltReqData = new MLTTESTREQDATA();
		mltReqData.setDataAvailable((byte) 78);
		mltReqData.setSsp((byte) 0);
		mltReqData.setMultipleDPA((byte) 0);
		mltReqData.setTas((byte) 0);
		mltReqData.setTac((byte) 0);
		mltReqData.setHty((byte) 0);
		mltReqData.setInwardOnlyFlag((byte) 0);

		MLTTESTREQ mltTestReq = new MLTTESTREQ();

		mltTestReq.setHdr(mltTestReqHdr);
		mltTestReq.setListId("listId");
		mltTestReq.setObjectId("objectId");
		mltTestReq.setKey(key);
		mltTestReq.setTestType(mltSession.getTestType());
		mltTestReq.setSessionInd((short) 0);
		mltTestReq.setResultsMode("DR");
		mltTestReq.setBatchMode((byte) 78);
		mltTestReq.setBufferMode((short) 0);
		mltTestReq.setChronicFlag((short) 0);
		mltTestReq.setDslamType((short) 0);
		mltTestReq.setThType((short) 0);
		mltTestReq.setCltmSlot((short) 0);
		mltTestReq.setCltAccMode((short) 0);
		mltTestReq.setUserDataIndicator((byte) 0);
		mltTestReq.setDdslbusy((byte) 0);
		mltTestReq.setSubscribedDataRate((short) 0);
		mltTestReq.setCableGaugeMakeupSet((byte) 0);
		mltTestReq.setData(mltReqData);
		mltTestReq.setGaugetype((short) 0);
		mltTestReq.setDerivedData(override);
		
		return mltTestReq;
	}
	
	private MLTTESTREQ generateMltToneTestRequestPayload(String sessionId) {
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		String tn = mltSession.getInquiredTn();
		CTelephone telephone = CTelephoneBuilderMlt.newBuilder(mltSession)
		.setTelephone(tn)
		.build();
		
		MLTTESTREQHDR mltTestReqHdr = new MLTTESTREQHDR();

		DataChanObjRef data = new DataChanObjRef();
		data.setId(mltSession.getDatachannelId());

		mltTestReqHdr.setTestRqstrType("FASTIVR");
		mltTestReqHdr.setTestRqstrId("requesterID");
		mltTestReqHdr.setTestSysId("LM50");
		mltTestReqHdr.setDataChanObjRef(data.getId());
		mltTestReqHdr.setApiversion("2.13");
		mltTestReqHdr.setStartTime(0);
		mltTestReqHdr.setStartTranTime(0);

		MLTTESTREQCIDFMTP fmtp = new MLTTESTREQCIDFMTP();
		fmtp.setNpa(telephone.getNpa());
		fmtp.setNnx(telephone.getNxx());
		fmtp.setLine(telephone.getLineNumber());
		fmtp.setChar1((byte) 0);

		MLTTESTREQKEYCID keyCid = new MLTTESTREQKEYCID();
		keyCid.setCktfmt((byte) 80);
		keyCid.setFmtP(fmtp);

		MLTTESTREQKEY key = new MLTTESTREQKEY();
		key.setChoice((short) 1);
		key.setCid(keyCid);

		MLTTESTREQDATA mltReqData = new MLTTESTREQDATA();
		mltReqData.setDataAvailable((byte) 78);
		mltReqData.setSsp((byte) 0);
		mltReqData.setMultipleDPA((byte) 0);
		mltReqData.setTas((byte) 0);
		mltReqData.setTac((byte) 0);
		mltReqData.setHty((byte) 0);
		mltReqData.setInwardOnlyFlag((byte) 0);

		MLTTESTREQ mltTestReq = new MLTTESTREQ();

		mltTestReq.setHdr(mltTestReqHdr);
		mltTestReq.setListId("listId");
		mltTestReq.setObjectId("objectId");
		mltTestReq.setKey(key);
		mltTestReq.setTesterId1("11");
		mltTestReq.setTesterId2("11");
		mltTestReq.setTesterId3("111");
		mltTestReq.setTestType(mltSession.getTestType());
		mltTestReq.setSessionInd((short) 1);
		mltTestReq.setResultsMode("DR");
		mltTestReq.setBatchMode((byte) 78);
		mltTestReq.setBufferMode((short) 0);
		mltTestReq.setChronicFlag((short) 0);
		mltTestReq.setDslamType((short) 0);
		mltTestReq.setThType((short) 0);
		mltTestReq.setCltmSlot((short) 0);
		mltTestReq.setCltAccMode((short) 0);
		mltTestReq.setUserDataIndicator((byte) 0);
		mltTestReq.setDdslbusy((byte) 0);
		mltTestReq.setSubscribedDataRate((short) 0);
		mltTestReq.setCableGaugeMakeupSet((byte) 0);
		mltTestReq.setData(mltReqData);
		mltTestReq.setGaugetype((short) 0);
		
		return mltTestReq;
	}


	
	/**
	 * Pings the MLT cache every few seconds to check if the result is available
	 * Once, the result is available : 
	 * 	Routes to ML0060 for Quick Test (8)
	 *  Routes to ML0070 for Loop & Full tests with Ver-code and Ver-code explanation
	 * Restriction: Wait time shouldn't exceed 420 seconds 
	 * Change in approach due to APIGEE's timeout restrictions
	 * Genesys will ping the backend, till backend don't have the response, Genesys has to keep on pinging 
	 * @param sessionId
	 * @return
	 */
	public String retrieveMLTTestResults(String sessionId) {
		LOGGER.info("Session: "+ sessionId + " retrieveMLTTestResults start");
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		//Fetch the cache column at every interval, until the result-column is populated 
		int waitInterval = 2*1000; //2 secs
		int maxTimes = 3; //total 6 secs holding the request  

		String testResult = mltSession.getMltTestResult();
		
		while(testResult == null && maxTimes > 0 ) {
			LOGGER.info("Session: "+ sessionId + " DB fetch count: "+ maxTimes);
			//wait for sometime before trying to look into the cache 
			try {
				Thread.sleep(waitInterval);
			} catch (InterruptedException e) {
				//thread is interrupted
			}
			
			//fetching from cache service because result can be populated at anytime from the async thread 
			testResult = mltCacheService.getBySessionId(sessionId).getMltTestResult();
			LOGGER.info("Session: "+ sessionId + " DB test-result value: :" + testResult);
			maxTimes--;
		}
		
		return testResult;
	}
	
	/**
	 * This method validates the MLT response and tests it against the negative scenarios 
	 * Return code :
	 * 1 -> Fastivr is down
	 * 2 -> MLT Error 
	 * 3 -> NPA-NXX not in LMOS 
	 * 4 -> Timeout
	 * 5 -> rc = NEQ 5/20
	 * 6 -> rc = 5/20  
	 * @param sessionId
	 * @return
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public IVRWebHookResponseDto validateMltResult(String sessionId) throws JsonMappingException, JsonProcessingException {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		String returnCode = HOOK_RETURN_1; //FAS interface down
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		String testResult = mltSession.getMltTestResult();
		
		Mdata mdata = objectMapper.readValue(testResult, Mdata.class);
		
		String testCode = "";
		String testCodeDescription = "";
		String testType = mltSession.getTestType();
		byte finalFlag = 78; //No (N)
		
		switch(testType) {
		case QUICKX_TEST:
		case LOOPX_TEST:
		case FULLX_TEST:
			testCode = mdata.getTestRsp().getF().getTestCode();
			testCodeDescription = mdata.getTestRsp().getF().getTestCodeDescription();
			LOGGER.info("Session:" + sessionId + ", validateMltResult() : VER/TEST code :" + testCode + ", Test desc: "
					+ testCodeDescription);
			
			if (testCode != null) {

				if (testCode.contains("T")) {
					// Timeout scenario
					returnCode = HOOK_RETURN_4;
				} else {
					//success scenario for loops/fullx
					List<IVRParameter> data = mltHelper.addParamterData(testCode, testCodeDescription);
					response.setParameters(data);
					returnCode = HOOK_RETURN_7;

				}
			} else {
				//success scenario for QUICKX
				returnCode = HOOK_RETURN_8;
				List<IVRParameter> data = mltHelper.addParamterData(testCodeDescription);
				response.setParameters(data);
			}
			break;
			
		case TONE_PLUS_TEST:
			//testOutcome = String.valueOf(mdata.getTestRsp().getTestOutcome());
			finalFlag = mdata.getTestRsp().getFinalFlag();
			if(finalFlag == 89) {
				returnCode = HOOK_RETURN_8; //Tone initiated
			} else {
				returnCode = HOOK_RETURN_4; //MLT error
			}
			break;
		}
		
		
		response.setHookReturnCode(returnCode);
		response.setSessionId(sessionId);
		return response;
	}
}
