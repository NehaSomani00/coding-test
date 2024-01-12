package com.lumen.fastivr.IVRMLT.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRMLT.caching.IvrMltCacheService;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRMLT.helper.IvrMltHelper;
import com.lumen.fastivr.IVRMLT.utils.IVRMltConstants;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;

import tollgrade.loopcare.testrequestapi.FAULTINFO1;
import tollgrade.loopcare.testrequestapi.MLTTESTRSP;
import tollgrade.loopcare.testrequestapi.Mdata;

//MLD075
@ExtendWith(MockitoExtension.class)
public class CheckVoltageInformationRequiredTest {

	@InjectMocks
    private IvrMltServiceImpl ivrMltServiceImpl;
	
	@Mock
    private IvrMltCacheService mltCacheService;
	
	@Mock
    private IvrMltHelper ivrMltHelper;
	
	@Mock
    private ObjectMapper objectMapper;


    @Test
    public void checkPlayVoltageInformation_NoVoltageInformationRequired_ReturnsHookReturnCode0() throws JsonProcessingException {
        // Arrange
        String sessionId = "session123";
        String currentState = "currentState";

        IvrMltSession mltSession = new IvrMltSession();
        mltSession.setMltTestResult("{\"testRsp\":{\"f\":{\"testCode\":\"testCode\"}}}");

        when(mltCacheService.getBySessionId(sessionId)).thenReturn(mltSession);
       // when(ivrMltHelper.voltageInformationRequired("testCode")).thenReturn(false);

        // Act
        IVRWebHookResponseDto response = ivrMltServiceImpl.checkPlayVoltageInformation(sessionId, currentState);

        // Assert
        assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, response.getHookReturnCode());
        assertEquals(IVRMltConstants.NO_VOLTAGE_INFORMATION_REQUIRED, response.getHookReturnMessage());
        assertEquals(currentState, response.getCurrentState());
    }

    @Test
    public void checkPlayVoltageInformation_VoltageInformationRequired_ReturnsHookReturnCode1() throws JsonProcessingException {
        // Arrange
        String sessionId = "session123";
        String currentState = "currentState";

        IvrMltSession mltSession = new IvrMltSession();
        mltSession.setMltTestResult("{\"testRsp\":{\"f\":{\"testCode\":\"testCode\"}}}");

        when(mltCacheService.getBySessionId(sessionId)).thenReturn(mltSession);
        Mdata mdta = new Mdata();
        MLTTESTRSP mltTESTRSP = new MLTTESTRSP();
        FAULTINFO1 f = new FAULTINFO1();
        f.setTestCode("test");
        
        mltTESTRSP.setF(f);
        mdta.setTestRsp(mltTESTRSP);
        when(objectMapper.readValue(anyString(), eq(Mdata.class))).thenReturn(mdta);
        when(ivrMltHelper.voltageInformationRequired(anyString())).thenReturn(false);

        // Act
        IVRWebHookResponseDto response = ivrMltServiceImpl.checkPlayVoltageInformation(sessionId, currentState);

        // Assert
        assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, response.getHookReturnCode());
        assertEquals(IVRMltConstants.VOLTAGE_INFORMATION_REQUIRED, response.getHookReturnMessage());
        assertEquals(currentState, response.getCurrentState());
    }

    @Test
    public void checkPlayVoltageInformation_NoMltTestDataFound_ReturnsNoMltTestDataFoundMessage() throws JsonProcessingException {
        // Arrange
        String sessionId = "session123";
        String currentState = "currentState";

        IvrMltSession mltSession = new IvrMltSession();
        mltSession.setMltTestResult("test");
        when(mltCacheService.getBySessionId(sessionId)).thenReturn(mltSession);
        when(objectMapper.readValue(anyString(), eq(Mdata.class))).thenThrow(JsonProcessingException.class);

        // Act
        IVRWebHookResponseDto response = ivrMltServiceImpl.checkPlayVoltageInformation(sessionId, currentState);

        // Assert
        assertEquals("No MLT Test data found.", response.getHookReturnMessage());
    }

    @Test
    public void checkPlayVoltageInformation_MltSessionNotFound_ReturnsHookReturnCode0() throws JsonProcessingException {
        // Arrange
        String sessionId = "session123";
        String currentState = "currentState";

        when(mltCacheService.getBySessionId(sessionId)).thenReturn(null);

        // Act
        IVRWebHookResponseDto response = ivrMltServiceImpl.checkPlayVoltageInformation(sessionId, currentState);

        // Assert
        assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, response.getHookReturnCode());
        assertEquals(IVRMltConstants.NO_VOLTAGE_INFORMATION_REQUIRED, response.getHookReturnMessage());
        assertEquals(currentState, response.getCurrentState());
    }
}