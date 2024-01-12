package com.lumen.fastivr.IVRMLT.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.net.http.HttpTimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRAppPropertyLoader.IvrDbPropertyCacheService;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.common.IVRHttpResponseDto;
import com.lumen.fastivr.IVRMLT.caching.IvrMltCacheService;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRMLT.entity.LmosRegionResponseDto;
import com.lumen.fastivr.IVRMLT.utils.MLTSoapApi;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.httpclient.IVRHttpClient;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"lmos.region.api=http://example.com/lmos/region"})
class IvrMltSoapApiGeneratorTest {

	@InjectMocks
	private IvrMltSoapApiGenerator ivrMltSoapApiGenerator;

	@Mock
	private IvrMltCacheService mltCacheService;
	
	@Mock
	private IVRCacheService mockIvrCacheService;

	@Mock
	private IVRHttpClient ivrHttpClient;

	@Mock
	private ObjectMapper objectMapper;
	
	@Mock
	private MLTSoapApi mltSoapApiCtx;
	
	@Mock
	private IvrDbPropertyCacheService mockDbPropertyCacheService;
	
	private String sessionId = "sessionId";
	private String npa = "123";
	private String nxx = "456";
	 String region = "expectedRegion";

	@BeforeEach
	void setUp() throws Exception {
		
		// Get the field object
        Field fieldLmos = IvrMltSoapApiGenerator.class.getDeclaredField("lmosRegionUrl");
        Field fieldDenver = IvrMltSoapApiGenerator.class.getDeclaredField("MLT_DENVER_FLAG");
        Field fieldOmhaha = IvrMltSoapApiGenerator.class.getDeclaredField("MLT_OMAHA_FLAG");
        // Make the field accessible
        fieldLmos.setAccessible(true);
        fieldDenver.setAccessible(true);
        fieldOmhaha.setAccessible(true);
        // Set the value of the field
        fieldLmos.set(ivrMltSoapApiGenerator, "http://mockurl.com");
        fieldDenver.set(ivrMltSoapApiGenerator, "ON");
        fieldOmhaha.set(ivrMltSoapApiGenerator, "ON");

        // Now the lmosRegionUrl field of obj has the value "new value"
		
	}
	

	@Test
	void testGenerateLoopCareSoapApi() throws JsonMappingException, JsonProcessingException, HttpTimeoutException {
	    // Arrange
	    String sessionId = "sessionId";
	   
	    String dataChannelUrl = "expectedDataChannelUrl";
	    String testRequestUrl = "expectedTestRequestUrl";
	    
	    
	    IvrMltSession expectedMltSession = new IvrMltSession();
	    IVRUserSession ivrUserSession = new IVRUserSession();
	    ivrUserSession.setLosDbResponse("testLosDbResponse");
	    
	    TNInfoResponse tnInfoResp = new TNInfoResponse();
	    tnInfoResp.setPrimaryNPA(npa);
	    tnInfoResp.setPrimaryNXX(nxx);
	    
	    IVRHttpResponseDto responDto = new IVRHttpResponseDto();
	    responDto.setResponseBody("tessResponseBody");
	    
	    LmosRegionResponseDto regionInfo = new LmosRegionResponseDto();
	    regionInfo.setRegion(region);
	    
	    when(objectMapper.readValue(anyString(), eq(TNInfoResponse.class))).thenReturn(tnInfoResp);
	    
	    expectedMltSession.setDataChannelProxyUrl(dataChannelUrl);
	    expectedMltSession.setTestRequestProxyUrl(testRequestUrl);
	    
	    when(mltCacheService.getBySessionId(sessionId)).thenReturn(expectedMltSession);
	    when(mockIvrCacheService.getBySessionId(sessionId)).thenReturn(ivrUserSession);
	    when(objectMapper.writeValueAsString(any())).thenReturn("stringLmosRegionRequestDto");
	    
	    when(ivrHttpClient.httpPostApiCall(anyString(), anyString(), anyString(), anyString())).thenReturn(responDto);
	    when(objectMapper.readValue(anyString(), eq(LmosRegionResponseDto.class))).thenReturn(regionInfo);
	    
	    when(mockDbPropertyCacheService.getValueByName(anyString())).thenReturn("ON");
	    
	    // Act
	    ivrMltSoapApiGenerator.generateLoopCareSoapApi(sessionId);
	    
	}
	
	
	@Test
	void testGetRegionFromNpaNxx() throws HttpTimeoutException, JsonProcessingException {
		 LmosRegionResponseDto regionInfo = new LmosRegionResponseDto();
		    regionInfo.setRegion(region);
		    
		    IVRHttpResponseDto responDto = new IVRHttpResponseDto();
		    responDto.setResponseBody("tessResponseBody");
		    
		    when(objectMapper.writeValueAsString(any())).thenReturn("stringLmosRegionRequestDto");
		    when(ivrHttpClient.httpPostApiCall(anyString(), anyString(), anyString(), anyString())).thenReturn(responDto);
		    when(objectMapper.readValue(anyString(), eq(LmosRegionResponseDto.class))).thenReturn(regionInfo);
		    String actualResponse  = ivrMltSoapApiGenerator.getRegionFromNpaNxx(sessionId, npa, nxx);
		    
		    assertEquals(region, actualResponse);
	}
}