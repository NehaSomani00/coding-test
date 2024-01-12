package com.lumen.fastivr.IVRMLT.helper;

import static com.lumen.fastivr.IVRMLT.utils.IVRMltConstants.MLT_DC_DENVER;
import static com.lumen.fastivr.IVRMLT.utils.IVRMltConstants.MLT_DC_OMAHA;

import java.net.http.HttpTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRAppPropertyLoader.IvrDbPropertyCacheService;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.common.IVRHttpResponseDto;
import com.lumen.fastivr.IVRMLT.caching.IvrMltCacheService;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRMLT.entity.LmosRegionRequestDto;
import com.lumen.fastivr.IVRMLT.entity.LmosRegionResponseDto;
import com.lumen.fastivr.IVRMLT.exception.InvalidNPANXXException;
import com.lumen.fastivr.IVRMLT.utils.MLTSoapApi;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.httpclient.IVRHttpClient;

@Service
public class IvrMltSoapApiGenerator {
	
	private static final String MLT_OMAHA_SERVER_PROP_NAME = "MLT_OMAHA_SERVER";

	private static final String MLT_DENVER_SERVER_PROP_NAME = "MLT_DENVER_SERVER";

	@Value("${MLT_DENVER_SERVER}")
	private String MLT_DENVER_FLAG;
	
	@Value("${MLT_OMAHA_SERVER}")
	private String MLT_OMAHA_FLAG;
	
	@Autowired
	private IVRCacheService ivrCacheService;
	
	@Autowired
	private IvrMltCacheService mltCacheService;
	
	@Value("${lmos.region.api}")
	private String lmosRegionUrl;
	
	@Autowired
	private IVRHttpClient ivrHttpClient;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private MLTSoapApi mltSoapApiCtx;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IvrMltSoapApiGenerator.class);
	
	/**
	 * Public method to serve the request to generate the LoopCare SOAP apis for all MLT tests 
	 * @param sessionId
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 * @throws HttpTimeoutException 
	 */
	public void generateLoopCareSoapApi(String sessionId) throws JsonMappingException, JsonProcessingException, HttpTimeoutException {
		
		String region = identifyRegion(sessionId);
		String dataCenter = identifyDatacenter(sessionId);
		
		LOGGER.info("Sesion :"+sessionId+" , MLT Data-Center : "+ dataCenter+ ", LMOS Region: "+ region);
		
		LOGGER.info("Sesion :"+sessionId+" Building the SOAP apis");
		MLTSoapApi soapApis = generateMltSoapApisFromRegionDC(region, dataCenter);

		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		mltSession.setDataChannelProxyUrl(soapApis.getDatachannel());
		mltSession.setTestRequestProxyUrl(soapApis.getTestRequest());

		mltCacheService.updateSession(mltSession);
	}
	
	/**
	 * Generates the MLT APIs needed based on TN's region(C/E/W) and datacentre(ODC/DDC)
	 * @param region
	 * @param dataCentre
	 * @return
	 */
	private MLTSoapApi generateMltSoapApisFromRegionDC(String region, String dataCentre) {
		
		MLTSoapApi mltSoapApi = mltSoapApiCtx.new MltSoapApiBuilder(region, dataCentre)
		.datachannelProxy()
		.testRequestProxy()
		.build();
		
		return mltSoapApi;
	}
	
	public String identifyRegion(String sessionId)
			throws JsonMappingException, JsonProcessingException, HttpTimeoutException {
		// identify the region based on NPA and NXX
		IVRUserSession userSession = ivrCacheService.getBySessionId(sessionId);
		String tnInfoStr = userSession.getLosDbResponse();
		TNInfoResponse tnInfo = objectMapper.readValue(tnInfoStr, TNInfoResponse.class);
		String npa = tnInfo.getPrimaryNPA();
		String nxx = tnInfo.getPrimaryNXX();

		// use npa and nxx to make the call and get the region
		String region = getRegionFromNpaNxx(sessionId, npa, nxx);

		if (region.equalsIgnoreCase("ERR")) {
			throw new InvalidNPANXXException("Invalid combination (npa-nxx): " + npa + "-" + nxx);
		}

		return region;

	}
	
	public String getRegionFromNpaNxx(String sessionId, String npa, String nxx)
			throws HttpTimeoutException, JsonProcessingException {
		
		// fetch the region from the lmos api
		String jsonRequest = generateRegionApiRequest(npa, nxx);
		IVRHttpResponseDto responseDto = ivrHttpClient.httpPostApiCall(jsonRequest, lmosRegionUrl, sessionId,
				"LMOS REGION FROM NPANXX");
		String responseString = responseDto.getResponseBody();
		LmosRegionResponseDto regionInfo = objectMapper.readValue(responseString, LmosRegionResponseDto.class);
		
		return regionInfo.getRegion();
	}

	private String generateRegionApiRequest(String npa, String nxx) throws JsonProcessingException {
		LmosRegionRequestDto region = new LmosRegionRequestDto(npa, nxx);
		String jsonString = objectMapper.writeValueAsString(region);
		return jsonString;
	}
	
	@Autowired
	private IvrDbPropertyCacheService dbPrCacheService;

	private String identifyDatacenter(String sessionId) {
		String server = "";
		
		LOGGER.info("From Property file: MLT_DENVER_FLAG= "+MLT_DENVER_FLAG+", MLT_OMAHA_FLAG= "+MLT_OMAHA_FLAG );
		loadPropertyFromCache();
		
		boolean isDenverOn = MLT_DENVER_FLAG.equalsIgnoreCase("ON") ? true : false;
		boolean isOmahaOn = MLT_OMAHA_FLAG.equalsIgnoreCase("ON") ? true : false;
		
		boolean isPartialProductionDown = isDenverOn ^ isOmahaOn;
		boolean isCompleteProductionDown = !(isDenverOn || isOmahaOn);
		
		if(isCompleteProductionDown) {
			//throw some exception or return null
			LOGGER.info("All MLT Datacenters are DOWN");
			
		} else if(isPartialProductionDown){
			server = isDenverOn ? MLT_DC_DENVER : MLT_DC_OMAHA;
			LOGGER.info("Partial MLT Datacenters are UP, Active Datacenter: "+ server);
			
		} else {
			//all data centers are up
			long data = System.currentTimeMillis();
			if(data % 2 == 0) 
				server = MLT_DC_DENVER;
			else 
				server = MLT_DC_OMAHA;
			
			LOGGER.info("All MLT Datacenters are UP, selected Data center: "+server);
		}
		
		return server;
	}

	private void loadPropertyFromCache() {
		MLT_DENVER_FLAG = dbPrCacheService.getValueByName(MLT_DENVER_SERVER_PROP_NAME);
		MLT_OMAHA_FLAG = dbPrCacheService.getValueByName(MLT_OMAHA_SERVER_PROP_NAME);

		LOGGER.info("From Database file: MLT_DENVER_FLAG= " + MLT_DENVER_FLAG + ", MLT_OMAHA_FLAG= " + MLT_OMAHA_FLAG);
	}

}
