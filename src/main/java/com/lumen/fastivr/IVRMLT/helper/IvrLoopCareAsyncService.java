package com.lumen.fastivr.IVRMLT.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRMLT.caching.IvrMltCacheService;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRMLT.utils.IvrMltPager;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tollgrade.loopcare.testrequestapi.*;

import java.net.MalformedURLException;
import java.util.concurrent.CompletableFuture;

import static com.lumen.fastivr.IVRMLT.utils.IVRMltConstants.*;
import static com.lumen.fastivr.IVRMLT.utils.IVRMltUtilities.*;

@Service
public class IvrLoopCareAsyncService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IvrLoopCareAsyncService.class);
	
	@Autowired
	private IVRCacheService ivrCacheService;
	@Autowired
	private IvrMltCacheService mltCacheService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private IvrMltPager mltPager;
	
	@Autowired
	private IvrLoopCareOperations loopCareOperations;
	
	@Autowired
	MltPagerText mltPagerText;


	/**
	 * This method will ping LoopCare apis to check whether MLT has completed processing 
	 * After the Results are received , close the connection with MLT 
	 * Check for Tech's Alpha Pager condition, 
	 * If Tech has Mobile active (alpha pager true) --> Send test results to his Mobile 
	 * Else, store the result in MLT cache 
	 * @return
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@Async("threadPoolTaskExecutor")
	public CompletableFuture<String> fetchMltTestResult(String sessionId) throws JsonMappingException, JsonProcessingException {
		LOGGER.info("Execute method with configured executor - " + Thread.currentThread().getName());
		LOGGER.info("Session id: "+ sessionId+" fetchMltTestResult start");
		
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		IVRUserSession ivrSession = ivrCacheService.getBySessionId(sessionId);
		String status = MLT_TEST_FAILURE;
		String device = NET_MAIL_DEVICE; //by default we send to email, if mobile not present 
		
		//send the pull request to MDataChannelProxyEndpoint
		try {
			status = pullMltResponse(mltSession);
			
		} catch (APIServerException e) {
			LOGGER.info("Session id: "+sessionId+ " Failed to Pull the response ",e);
		} catch (JsonProcessingException e) {
			LOGGER.info("Session id: "+sessionId+ " Failed to Pull the response ",e);
		} catch (MalformedURLException e) {
			LOGGER.info("Session id: "+sessionId+ " Failed to Pull the response ",e);
		}
		
		
		//send free request to MDataChannelProxyEndpoint 
		sendFreeRequest(mltSession);
		
		//check if tech has mobile pager set , then send page results to Tech
		if(ivrSession.isCanBePagedMobile()) {
			device = IVRConstants.NET_PHONE_DEVICE;
		}else if(ivrSession.isCanBePagedEmail()){
			device=IVRConstants.NET_MAIL_DEVICE;
		}
		
		String pageText=mltPagerText.getPagerText(mltSession.getTestType(),sessionId);
		mltPager.sendPage(pageText, device, ivrSession);
		
		return CompletableFuture.completedFuture(status);
	}
	

	/**
	 * Pulls the response from MLT , and stores the result in cache 
	 * @param mltSession
	 * @throws APIServerException 
	 * @throws JsonProcessingException 
	 * @throws MalformedURLException 
	 */
	public String pullMltResponse(IvrMltSession mltSession) throws APIServerException, JsonProcessingException, MalformedURLException {
		String sessionId = mltSession.getSessionId();
		String testType = mltSession.getTestType();
		LOGGER.info("Session id: "+ sessionId+" pullMltResponse start");
		
		String dataChanId = mltSession.getDatachannelId();
		LOGGER.info("Session id: "+ sessionId+" Datachannel id: "+ dataChanId);
		DataChanObjRef dataChanObjRef = new DataChanObjRef();
		dataChanObjRef.setId(dataChanId);
		short timeoutSeconds = 7;
		
		PullMessage pullRequest = new PullMessage();
		pullRequest.setChannelObjRef(dataChanObjRef);
		pullRequest.setTimeoutSeconds(timeoutSeconds);
		
		int count = 1;
		
		MDataChannelFactoryOperations dataChannelOps = createDataChannelOps(mltSession);
		Mdata pullResponse = dataChannelOps.pull(pullRequest);
		
		switch(testType) {
		case QUICKX_TEST:
		case LOOPX_TEST:
		case FULLX_TEST:
			pullResponse = pullMltTestResponse(sessionId, pullRequest, count, dataChannelOps, pullResponse);
			break;
		case TONE_PLUS_TEST:
		case TONE_REMOVAL_TEST:
			pullResponse = pullToneTestResponse(sessionId, pullRequest, count, dataChannelOps, pullResponse);
			break;
		}
		
		
		String mltResponseJson = objectMapper.writeValueAsString(pullResponse);
		LOGGER.info("Session "+ sessionId+ " MLT Response: "+ mltResponseJson);
		mltSession.setMltTestResult(mltResponseJson);
		
		mltCacheService.updateSession(mltSession);
		
		LOGGER.info("Session id: "+ sessionId+" pullMltResponse end");
		return MLT_TEST_SUCCESS;
		
	}


	private Mdata pullMltTestResponse(String sessionId, PullMessage pullRequest, int count,
			MDataChannelFactoryOperations dataChannelOps, Mdata pullResponse) throws APIServerException {
		//Wait for maximum of 5 minutes for the result to be completed
		//Need to check this waiting time limit from Howard
		while(pullResponse == null && count < 60) {
			LOGGER.info("Session id: "+ sessionId+" Pull count: "+count);
			pullResponse = dataChannelOps.pull(pullRequest);
			count++;
		}
		return pullResponse;
	}
	
	private Mdata pullToneTestResponse(String sessionId, PullMessage pullRequest, int count,
			MDataChannelFactoryOperations dataChannelOps, Mdata pullResponse) throws APIServerException {
		LOGGER.info("Inside pullToneTestResponse");
		//Wait for maximum of 5 minutes for the result to be completed
		//Need to check this waiting time limit from Howard
		byte yes = 89;
		byte no = 78;
		
		while(pullResponse == null || pullResponse.getTestRsp().getFinalFlag()==no && count < 60) {
			LOGGER.info("Session id: "+ sessionId+" Pull count: "+count);
			pullResponse = dataChannelOps.pull(pullRequest);
			count++;
		}
		return pullResponse;
	}

	private MDataChannelFactoryOperations createDataChannelOps(IvrMltSession mltSession) throws MalformedURLException {
		String dataChannelProxyUrl = mltSession.getDataChannelProxyUrl();
		MDataChannelFactoryOperations dataChannelOps = loopCareOperations
				.dataChannelFactoryOperations(dataChannelProxyUrl);
		return dataChannelOps;
	}
	
	/**
	 * To safely close the connection with MLT LoopCare Service
	 * @param mltSession
	 */
	public void sendFreeRequest(IvrMltSession mltSession) {
		String datachannelId = mltSession.getDatachannelId();
		String sessionId = mltSession.getSessionId();
		DataChanObjRef dataChanObjRef = new DataChanObjRef();
		dataChanObjRef.setId(datachannelId);
		
		try {
			MDataChannelFactoryOperations dataChannelOps = createDataChannelOps(mltSession);
			dataChannelOps.free(dataChanObjRef);
			LOGGER.info("Session id: " + sessionId + " Data channel :" + datachannelId
					+ " closed");
		} catch (APIServerException e) {
			// not a breaking exception so we are catching it
			LOGGER.info("Session id: "+ sessionId+ " Failed to close the data channel ",e);
		} catch (MalformedURLException e) {
			LOGGER.info("Session id: "+ sessionId+ " Failed to close the data channel ",e);
		}

	}
	
}
