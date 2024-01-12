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

import tollgrade.loopcare.testrequestapi.Mdata;

@ExtendWith(MockitoExtension.class)
public class PlayTipGroundVoltsTest {


	@InjectMocks
    private IvrMltServiceImpl ivrMltServiceImpl;

    @Mock
    private IvrMltCacheService mltCacheService;

    @Mock
    private IvrMltHelper ivrMltHelper;

    @Mock
    private ObjectMapper objectMapper;
    
    private Mdata mdata;

    @Test
    public void testPlayTipGroundVoltsWithVoltTg() throws JsonProcessingException {
        // Arrange
        String sessionId = "session123";
        String currentState = "MLD086";

        IvrMltSession mltSession = new IvrMltSession();
        mltSession.setMltTestResult("{\"testRsp\":{\"d\":{\"item\":[{\"potsResults1\":{\"dcCraft\":{\"dcVoltTg\":\"10\"}}}]}}}");

        when(mltCacheService.getBySessionId(sessionId)).thenReturn(mltSession);
        mdata = new ObjectMapper().readValue("{\"testRsp\":{\"d\":{\"item\":[{\"potsResults1\":{\"dcCraft\":{\"dcVoltTg\":\"10\"}}}]}}}", Mdata.class);
        when(objectMapper.readValue(anyString(), eq(Mdata.class))).thenReturn(mdata);

        // Act
        IVRWebHookResponseDto response = ivrMltServiceImpl.playTipGroundVolts(sessionId, currentState);

        // Assert
        assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, response.getHookReturnCode());
        assertEquals(IVRMltConstants.TIP_GROUND_VOLTS, response.getHookReturnMessage());
        assertEquals(currentState, response.getCurrentState());
    }

    @Test
    public void testPlayTipGroundVoltsWithoutVoltTg() throws JsonProcessingException {
        // Arrange
        String sessionId = "session123";
        String currentState = "MLD086";

        IvrMltSession mltSession = new IvrMltSession();
        mltSession.setMltTestResult("{\"testRsp\":{\"d\":{\"item\":[{\"potsResults1\":{\"dcCraft\":{\"dcVoltTg\":\"\"}}}]}}}");

        when(mltCacheService.getBySessionId(sessionId)).thenReturn(mltSession);
        mdata = new ObjectMapper().readValue("{\"testRsp\":{\"d\":{\"item\":[{\"potsResults1\":{\"dcCraft\":{\"dcVoltTg\":\"\"}}}]}}}", Mdata.class);
        when(objectMapper.readValue(anyString(), eq(Mdata.class))).thenReturn(mdata);
       

        // Act
        IVRWebHookResponseDto response = ivrMltServiceImpl.playTipGroundVolts(sessionId, currentState);

        // Assert
        assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, response.getHookReturnCode());
        assertEquals(IVRMltConstants.NO_TIP_GROUND_VOLTS, response.getHookReturnMessage());
        assertEquals(currentState, response.getCurrentState());
    }

    @Test
    public void testPlayTipGroundVoltsWithInvalidTestData() throws JsonProcessingException {
        // Arrange
        String sessionId = "session123";
        String currentState = "MLD086";

        IvrMltSession mltSession = new IvrMltSession();
        mltSession.setMltTestResult("Invalid JSON");

        when(mltCacheService.getBySessionId(sessionId)).thenReturn(mltSession);
        when(objectMapper.readValue(anyString(), eq(Mdata.class))).thenThrow(JsonProcessingException.class);
        // Act
        IVRWebHookResponseDto response = ivrMltServiceImpl.playTipGroundVolts(sessionId, currentState);

        // Assert
        assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, response.getHookReturnCode());
        assertEquals("No MLT Test data found.", response.getHookReturnMessage());
        assertEquals(currentState, response.getCurrentState());
    }


}
