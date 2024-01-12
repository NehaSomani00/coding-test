package com.lumen.fastivr.IVRCANST.service;

import static com.lumen.fastivr.IVRCANST.utils.IVRCANSTConstants.CANST_INVALID_SESSION_ID;
import static com.lumen.fastivr.IVRCANST.utils.IVRCANSTConstants.TN_FOUND_IN_TABLE;
import static com.lumen.fastivr.IVRCANST.utils.IVRCANSTConstants.TN_NOT_FOUND_IN_TABLE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.CANNOT_FETCH_CURR_ASSG_API_LOSDB_NULL;
import static com.lumen.fastivr.IVRUtils.IVRConstants.GPDOWN_ERR_MSG;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INVALID_SESSION_ID;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_0;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_1;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_2;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_3;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCANST.Dto.AssignServiceOrderResponse;
import com.lumen.fastivr.IVRCANST.Dto.AssignServiceOrderResponseReturnDataSet;
import com.lumen.fastivr.IVRCANST.Dto.ChangeLoopAssignmentCandidatePairInfo;
import com.lumen.fastivr.IVRCANST.Dto.ChangeLoopAssignmentInputData;
import com.lumen.fastivr.IVRCANST.Dto.ChangeLoopAssignmentRequest;
import com.lumen.fastivr.IVRCANST.Dto.ChangeLoopAssignmentResponse;
import com.lumen.fastivr.IVRCANST.Dto.ChangeLoopAssignmentReturnDataSet;
import com.lumen.fastivr.IVRCANST.Dto.OrderStatusRequest;
import com.lumen.fastivr.IVRCANST.Dto.OrderStatusResponse;
import com.lumen.fastivr.IVRCANST.Dto.UpdateLoopRequestDto;
import com.lumen.fastivr.IVRCANST.Dto.UpdateLoopResponseDto;
import com.lumen.fastivr.IVRCANST.entity.IVRCanstEntity;
import com.lumen.fastivr.IVRCANST.helper.IVRCanstHelper;
import com.lumen.fastivr.IVRCANST.helper.IVRCanstPagerText;
import com.lumen.fastivr.IVRCANST.helper.IvrCanstAsyncService;
import com.lumen.fastivr.IVRCANST.repository.IVRCanstCacheService;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.CablePair;
import com.lumen.fastivr.IVRDto.CurrentAssignmentInfo;
import com.lumen.fastivr.IVRDto.CurrentAssignmentRequestTnDto;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.ErrorList;
import com.lumen.fastivr.IVRDto.HostErrorList;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.InputData;
import com.lumen.fastivr.IVRDto.LOOP;
import com.lumen.fastivr.IVRDto.LSTFN;
import com.lumen.fastivr.IVRDto.MessageStatus;
import com.lumen.fastivr.IVRDto.ReturnDataSet;
import com.lumen.fastivr.IVRDto.SEG;
import com.lumen.fastivr.IVRDto.SO;
import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketRequest;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketResponse;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentRequest;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentResponse;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceImpl;
import com.lumen.fastivr.IVRLFACS.LfacsValidation;
import com.lumen.fastivr.IVRLFACS.SparePairPageBuilder;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.IVRUtils.IVRLfacsConstants;
import com.lumen.fastivr.httpclient.IVRHttpClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import static com.lumen.fastivr.IVRUtils.IVRConstants.*;
import static com.lumen.fastivr.IVRCANST.utils.IVRCANSTConstants.*;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
public class IVRCanstServiceImplTest {

    @Mock
    private IVRCanstHelper mockIvrCanstHelper;

    @Mock
    private IVRCacheService mockCacheService;

    @Mock
    private IVRCanstCacheService mockIvrCanstCacheService;

    @Mock
    private LfacsValidation mockLfacsTNValidation;

    @Mock
    private IVRLfacsServiceHelper mockIVRLfacsServiceHelper;

    @Mock
    private IVRLfacsServiceImpl mockIVRLfacsServiceImpl;

    @Mock
    private ObjectMapper mockObjectMapper;


    @Mock
    private IvrCanstAsyncService asyncService;

	
	@Mock
	IVRCanstPagerText ivrCanstPagerText;


    @Mock
    IVRHttpClient mockIvrHttpClient;


    @InjectMocks
    private IVRCanstServiceImpl ivrCanstServiceImpl;
    
    @Mock
	private SparePairPageBuilder sprPrPageBuilder;

    MessageStatus messageStatus = new MessageStatus();
    HostErrorList hostErrorList = new HostErrorList();
    List<HostErrorList> HostErrorLists = new ArrayList<>();
    ErrorList errorList = new ErrorList();
    List<ErrorList> errorLists = new ArrayList<>();
    ReturnDataSet returnDataSet = new ReturnDataSet();
    IVRWebHookResponseDto ivrWebHookResponseDto = new IVRWebHookResponseDto();
    CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
    TNInfoResponse tnInfoResponse = null;
    String currentAssignment = null;

    SO so = new SO();
    List<SO> soList = new ArrayList<>();

    LSTFN lstfn = new LSTFN();
    List<LSTFN> lstfnList = new ArrayList<>();

    LOOP loop = new LOOP();
    List<LOOP> loopList = new ArrayList<>();
    RetrieveLoopAssignmentResponse retrieveLoopAssignmentResponse = new RetrieveLoopAssignmentResponse();

    @BeforeEach
    void setUp() throws Exception {

        currentAssignmentResponseDto = new CurrentAssignmentResponseDto();
        ReturnDataSet dataSet = new ReturnDataSet();
        List<LOOP> loopList = new ArrayList<LOOP>();
        LOOP loop = new LOOP();

        List<SEG> segList = new ArrayList<SEG>();

        SEG seg = new SEG();

        segList.add(seg);
        loop.setSEG(segList);
        loopList.add(loop);
        dataSet.setLoop(loopList);
        currentAssignmentResponseDto.setReturnDataSet(dataSet);
    }

    @Test
    void testProcessFTD210Code_HookCode_WithoutCNFSession() throws JsonMappingException, JsonProcessingException {

        String sessionId = "session123";
        String currentState = "FTD210";

        TNInfoResponse losDbResponse = new TNInfoResponse();
        losDbResponse.setTn("7637574229");
        losDbResponse.setPrimaryNPA("763");
        losDbResponse.setPrimaryNXX("757");

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        response.setSessionId(sessionId);
        response.setCurrentState(currentState);

        IVRUserSession userSession = new IVRUserSession();

        userSession.setSessionId(sessionId);

        when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(userSession.getLosDbResponse()))
                .thenReturn(losDbResponse);

        when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
        when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(null);

        IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD210(sessionId, currentState);

        assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
        assertEquals(CANST_INVALID_SESSION_ID, actualResponse.getHookReturnMessage());
    }

    @Test
    void testProcessFTD210Code_HookCode_WithoutSession() throws JsonMappingException, JsonProcessingException {

        String sessionId = "session321";
        String currentState = "FTD210";

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        response.setSessionId(sessionId);
        response.setCurrentState(currentState);

        when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);

        IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD210(sessionId, currentState);

        assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
    }

    @Test
    void testprocessFTD011() throws JsonMappingException, JsonProcessingException {

        String sessionId = "session321";
        String currentState = "FTD011";
        String userInput = "1234567";

        IVRUserSession session = new IVRUserSession();
        session.setSessionId(sessionId);
        session.setNpaPrefix("56");

        List<String> userInputs = new ArrayList<>();
        userInputs.add("32");

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        response.setSessionId(sessionId);
        response.setCurrentState(currentState);
        response.setHookReturnCode(HOOK_RETURN_1);

        when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
        when(mockLfacsTNValidation.validateFacsTN(userInput, session)).thenReturn(response);

        IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD011(sessionId, currentState, userInput);
        assertEquals(TN_FOUND_IN_TABLE, actualResponse.getHookReturnMessage());

        response.setHookReturnCode(HOOK_RETURN_0);
        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD011(sessionId, currentState, userInput);
        assertEquals(TN_NOT_FOUND_IN_TABLE, actualResponse1.getHookReturnMessage());
    }

    @Test
    void testprocessFTD011_TN_FOUND_IN_TABLE() throws JsonMappingException, JsonProcessingException {

        String sessionId = "session321";
        String currentState = "FTD011";
        String userInput = "1234567";

        IVRUserSession session = new IVRUserSession();
        session.setSessionId(sessionId);
        session.setNpaPrefix("56");

        List<String> userInputs = new ArrayList<>();
        userInputs.add("32");

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        response.setSessionId(sessionId);
        response.setCurrentState(currentState);
        response.setHookReturnCode(HOOK_RETURN_1);

        when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
        when(mockLfacsTNValidation.validateFacsTN(userInput, session)).thenReturn(response);

        IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD011(sessionId, currentState, userInput);
        assertEquals(TN_FOUND_IN_TABLE, actualResponse.getHookReturnMessage());

        //response.setHookReturnCode(HOOK_RETURN_0);
        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD011(sessionId, currentState, userInput);
        assertEquals(TN_FOUND_IN_TABLE, actualResponse1.getHookReturnMessage());
    }


    @Test
    void testprocessFTD011_HookCode_WithoutSession() throws JsonMappingException, JsonProcessingException {

        String sessionId = "session321";
        String currentState = "FTD011";
        String userInput = "1234567";

        IVRUserSession session = new IVRUserSession();
        session.setSessionId(sessionId);
        session.setNpaPrefix("56");

        List<String> userInputs = new ArrayList<>();
        userInputs.add("32");

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        response.setSessionId(sessionId);
        response.setCurrentState(currentState);
        response.setHookReturnCode(HOOK_RETURN_1);

        when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);


        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD011(sessionId, currentState, userInput);

        assertEquals(INVALID_SESSION_ID, actualResponse1.getHookReturnMessage());
    }


    @Test
    void testprocessFTD035_withoutSession() throws JsonMappingException, JsonProcessingException {

        String sessionId = "session321";
        String currentState = "FTD011";
        String userInput = "1234567";

        IVRUserSession session = new IVRUserSession();
        session.setSessionId(sessionId);
        session.setNpaPrefix("56");

        List<String> userInputs = new ArrayList<>();
        userInputs.add("32");

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        response.setSessionId(sessionId);
        response.setCurrentState(currentState);
        response.setHookReturnCode(HOOK_RETURN_1);

        when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);


        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD035(sessionId, currentState);
        assertEquals(INVALID_SESSION_ID, actualResponse1.getHookReturnMessage());
    }


	/*
	@Test
	void testprocessFTD035_HookCode_5() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session321";
		String currentState = "FTD011";
		String userInput = "1234567";
		
		CurrentAssignmentResponseDto currentAssignmentResponse = new CurrentAssignmentResponseDto();
		
		IVRUserSession session = new IVRUserSession();
		session.setSessionId(sessionId);
		session.setNpaPrefix("56");		
		
		
		so.setORD("True");		
		soList.add(so);
		so.setLSTFN(lstfnList);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);
	
		
		messageStatus.setErrorStatus("S");			
		currentAssignmentResponse.setMessageStatus(messageStatus);
		
		session.setCurrentAssignmentResponse("xyz");
		
		
		
		
		
		List<String> userInputs = new ArrayList<>();
		userInputs.add("32");		

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
	    response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_5);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
		when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
		.thenReturn(currentAssignmentResponse);
		
		when(mockIVRLfacsServiceHelper.isSpecialCircuit(currentAssignmentResponse, response))
		.thenReturn(true);
		
	
		
	
		IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD035(sessionId, currentState);
		assertEquals( IVRHookReturnCodes.HOOK_RETURN_5, actualResponse1.getHookReturnCode());			
	}
	
	*/


    @Test
    void testprocessFTD035_HookCode_6() throws JsonMappingException, JsonProcessingException {

        String sessionId = "session321";
        String currentState = "FTD011";
        String userInput = "1234567";

        CurrentAssignmentResponseDto currentAssignmentResponse = new CurrentAssignmentResponseDto();

        IVRUserSession session = new IVRUserSession();
        session.setSessionId(sessionId);
        session.setNpaPrefix("56");

        lstfn.setCKID("763 757-4229");

        lstfnList.add(lstfn);


        so.setORD("True");
        soList.add(so);
        so.setLSTFN(lstfnList);
        loop.setCKID("763 757-4229");
        loop.setSO(soList);
        loop.setTID("DPAAB");
        loopList.add(loop);
        returnDataSet.setLoop(loopList);
        returnDataSet.setPort1("test");
        currentAssignmentResponse.setReturnDataSet(returnDataSet);


        messageStatus.setErrorStatus("S");
        currentAssignmentResponse.setMessageStatus(messageStatus);

        session.setCurrentAssignmentResponse("xyz");


        List<String> userInputs = new ArrayList<>();
        userInputs.add("32");

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        response.setSessionId(sessionId);
        response.setCurrentState(currentState);
        response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_6);

        when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
        when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
                .thenReturn(currentAssignmentResponse);

        when(mockIVRLfacsServiceHelper.isLineStationTransfer(currentAssignmentResponse))
                .thenReturn(true);


        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD035(sessionId, currentState);
        assertEquals(IVRHookReturnCodes.HOOK_RETURN_6, actualResponse1.getHookReturnCode());
    }


    @Test
    void testprocessFTD035_HookCode_7() throws JsonMappingException, JsonProcessingException {

        String sessionId = "session321";
        String currentState = "FTD011";
        String userInput = "1234567";

        CurrentAssignmentResponseDto currentAssignmentResponse = new CurrentAssignmentResponseDto();

        IVRUserSession session = new IVRUserSession();
        session.setSessionId(sessionId);
        session.setNpaPrefix("56");

        lstfn.setCKID("763 757-4229");
        lstfnList.add(lstfn);

        List<SEG> segList = new ArrayList();

        SEG seg1 = new SEG();
        seg1.setSEGNO("1");
        segList.add(seg1);

        SEG seg2 = new SEG();
        seg1.setSEGNO("2");
        segList.add(seg2);

        SEG seg3 = new SEG();
        seg1.setSEGNO("3");
        segList.add(seg3);

        SEG seg4 = new SEG();
        seg1.setSEGNO("4");
        segList.add(seg4);


        so.setORD("True");
        soList.add(so);
        so.setLSTFN(lstfnList);
        loop.setSEG(segList);
        loop.setCKID("763 757-4229");
        loop.setSO(soList);
        loop.setTID("DPAAB");
        loopList.add(loop);
        returnDataSet.setLoop(loopList);
        returnDataSet.setPort1("test");
        currentAssignmentResponse.setReturnDataSet(returnDataSet);


        messageStatus.setErrorStatus("S");
        currentAssignmentResponse.setMessageStatus(messageStatus);

        session.setCurrentAssignmentResponse("xyz");


        List<String> userInputs = new ArrayList<>();
        userInputs.add("32");

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        response.setSessionId(sessionId);
        response.setCurrentState(currentState);
        response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_7);

        when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
        when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
                .thenReturn(currentAssignmentResponse);

        when(mockIvrCanstHelper.getSegmentList(currentAssignmentResponse))
                .thenReturn(segList.size());


        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD035(sessionId, currentState);
        assertEquals(IVRHookReturnCodes.HOOK_RETURN_7, actualResponse1.getHookReturnCode());
    }


    @Test
    void testprocessFTD170_withoutSession() throws JsonMappingException, JsonProcessingException {

        String sessionId = "session321";
        String currentState = "FTD011";
        String userInput = "1234567";

        IVRUserSession session = new IVRUserSession();
        session.setSessionId(sessionId);
        session.setNpaPrefix("56");

        List<String> userInputs = new ArrayList<>();
        userInputs.add("32");

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        response.setSessionId(sessionId);
        response.setCurrentState(currentState);
        response.setHookReturnCode(HOOK_RETURN_1);

        when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);


        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD170(sessionId, currentState);
        assertEquals(INVALID_SESSION_ID, actualResponse1.getHookReturnMessage());
    }


    @Test
    void testprocessFTD170_withoutCurrentAssignment() throws JsonMappingException, JsonProcessingException {

        String sessionId = "session321";
        String currentState = "FTD011";
        String userInput = "1234567";

        CurrentAssignmentResponseDto currentAssignmentResponse = new CurrentAssignmentResponseDto();

        IVRUserSession session = new IVRUserSession();
        session.setSessionId(sessionId);
        session.setNpaPrefix("56");

        List<String> userInputs = new ArrayList<>();
        userInputs.add("32");

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        response.setSessionId(sessionId);
        response.setCurrentState(currentState);
        response.setHookReturnCode(HOOK_RETURN_1);

        so.setORD("True");
        soList.add(so);
        so.setLSTFN(lstfnList);
        loop.setCKID("763 757-4229");
        loop.setSO(soList);
        loop.setTID("DPAAB");
        loopList.add(loop);
        returnDataSet.setLoop(loopList);
        returnDataSet.setPort1("test");
        currentAssignmentResponse.setReturnDataSet(returnDataSet);


        messageStatus.setErrorStatus("S");
        currentAssignmentResponse.setMessageStatus(messageStatus);

        when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
//		when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
//		.thenReturn(null);


        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD170(sessionId, currentState);
        assertEquals("Current Assignment is null", actualResponse1.getHookReturnMessage());
    }


    @Test
    void testprocessFTD170_withoutCurrentAssignment2() throws JsonMappingException, JsonProcessingException {

        String sessionId = "session321";
        String currentState = "FTD011";
        String userInput = "1234567";

        CurrentAssignmentResponseDto currentAssignmentResponse = new CurrentAssignmentResponseDto();

        IVRUserSession session = new IVRUserSession();
        session.setSessionId(sessionId);
        session.setNpaPrefix("56");
        session.setCurrentAssignmentResponse("xyz");

        List<String> userInputs = new ArrayList<>();
        userInputs.add("32");

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        response.setSessionId(sessionId);
        response.setCurrentState(currentState);
        response.setHookReturnCode(HOOK_RETURN_1);

        so.setORD("True");
        soList.add(so);
        so.setLSTFN(lstfnList);
        loop.setCKID("763 757-4229");
        loop.setSO(soList);
        loop.setTID("DPAAB");
        loopList.add(loop);
        returnDataSet.setLoop(loopList);
        returnDataSet.setPort1("test");
        currentAssignmentResponse.setReturnDataSet(returnDataSet);


        messageStatus.setErrorStatus("S");
        currentAssignmentResponse.setMessageStatus(messageStatus);

        when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
        when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
                .thenReturn(null);


        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD170(sessionId, currentState);
        assertEquals("Current Assignment not found", actualResponse1.getHookReturnMessage());
    }


    @Test
    void testprocessFTD170_3() throws JsonMappingException, JsonProcessingException {

        String sessionId = "session321";
        String currentState = "FTD011";
        String userInput = "1234567";

        CurrentAssignmentResponseDto currentAssignmentResponse = new CurrentAssignmentResponseDto();

        IVRUserSession session = new IVRUserSession();
        session.setSessionId(sessionId);
        session.setNpaPrefix("56");
        session.setCurrentAssignmentResponse("xyz");

        List<String> userInputs = new ArrayList<>();
        userInputs.add("32");

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        response.setSessionId(sessionId);
        response.setCurrentState(currentState);
        response.setHookReturnCode(HOOK_RETURN_1);

        so.setORD("True");
        soList.add(so);
        so.setLSTFN(lstfnList);
        loop.setCKID("763 757-4229");
        loop.setSO(soList);
        loop.setTID("DPAAB");
        loopList.add(loop);
        returnDataSet.setLoop(loopList);
        returnDataSet.setPort1("test");
        currentAssignmentResponse.setReturnDataSet(returnDataSet);


//		messageStatus.setErrorStatus("S");			
//		currentAssignmentResponse.setMessageStatus(messageStatus);

        when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
        when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
                .thenReturn(currentAssignmentResponse);


        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD170(sessionId, currentState);
        assertEquals("Message Status Not Found", actualResponse1.getHookReturnMessage());
    }


    @Test
    void testprocessFTD170_5() throws JsonMappingException, JsonProcessingException {

        String sessionId = "session321";
        String currentState = "FTD011";
        String userInput = "1234567";

        CurrentAssignmentResponseDto currentAssignmentResponse = new CurrentAssignmentResponseDto();

        IVRUserSession session = new IVRUserSession();
        session.setSessionId(sessionId);
        session.setNpaPrefix("56");
        session.setCurrentAssignmentResponse("xyz");

        List<String> userInputs = new ArrayList<>();
        userInputs.add("32");

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        response.setSessionId(sessionId);
        response.setCurrentState(currentState);
        response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_5);

        so.setORD("True");
        soList.add(so);
        so.setLSTFN(lstfnList);
        loop.setCKID("763 757-4229");
        loop.setSO(soList);
        loop.setTID("DPAAB");
        loopList.add(loop);
        returnDataSet.setLoop(loopList);
        returnDataSet.setPort1("test");
        currentAssignmentResponse.setReturnDataSet(returnDataSet);


        messageStatus.setErrorStatus("F");


        errorList.setErrorCode("I");
        errorList.setErrorMessage("L150-435");
        errorLists.add(errorList);

        hostErrorList.setId("LFACS");
        hostErrorList.setErrorList(errorLists);
        HostErrorLists.add(hostErrorList);

        messageStatus.setHostErrorList(HostErrorLists);

        currentAssignmentResponse.setMessageStatus(messageStatus);

        when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
        when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
                .thenReturn(currentAssignmentResponse);


        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD170(sessionId, currentState);
        assertEquals(IVRHookReturnCodes.HOOK_RETURN_5, actualResponse1.getHookReturnCode());
    }


    @Test
    void testprocessFTD170_6() throws JsonMappingException, JsonProcessingException {

        String sessionId = "session321";
        String currentState = "FTD011";
        String userInput = "1234567";

        CurrentAssignmentResponseDto currentAssignmentResponse = new CurrentAssignmentResponseDto();

        IVRUserSession session = new IVRUserSession();
        session.setSessionId(sessionId);
        session.setNpaPrefix("56");
        session.setCurrentAssignmentResponse("xyz");

        List<String> userInputs = new ArrayList<>();
        userInputs.add("32");

        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
        response.setSessionId(sessionId);
        response.setCurrentState(currentState);
        response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_6);

        so.setORD("True");
        so.setCKID("763 757-4229");
        soList.add(so);
        so.setLSTFN(lstfnList);
        loop.setCKID("763 757-4229");
        loop.setSO(soList);
        loop.setTID("DPAAB");
        loopList.add(loop);
        returnDataSet.setLoop(loopList);
        returnDataSet.setPort1("test");

        //returnDataSet = null;


        currentAssignmentResponse.setReturnDataSet(returnDataSet);


        messageStatus.setErrorStatus("S");


        errorList.setErrorCode("I");
        errorList.setErrorMessage("L150-435");
        errorLists.add(errorList);

        hostErrorList.setId("LFACS");
        hostErrorList.setErrorList(errorLists);
        HostErrorLists.add(hostErrorList);

        messageStatus.setHostErrorList(HostErrorLists);

        currentAssignmentResponse.setMessageStatus(messageStatus);

        when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
        when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
                .thenReturn(currentAssignmentResponse);

        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD170(sessionId, currentState);
        assertEquals(IVRHookReturnCodes.HOOK_RETURN_6, actualResponse1.getHookReturnCode());
    }


    @Test
    void testStateFTD240WhenAlphaPager() {

        String sessionId = "session123";

        IVRUserSession session = new IVRUserSession();
        IVRCanstEntity ivrCanstEntity = new IVRCanstEntity();

        session.setCanBePagedMobile(Boolean.TRUE);

        List<String> userInputs = new ArrayList<>();
        userInputs.add("1");

        when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
        when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(ivrCanstEntity);

        IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD240(sessionId);

        assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());

    }

    @Test
    void testStateFTD240WhenNotAlphaPager() {

        String sessionId = "session123";

        IVRUserSession session = new IVRUserSession();
        IVRCanstEntity ivrCanstEntity = new IVRCanstEntity();

        session.setCanBePagedMobile(Boolean.FALSE);

        List<String> userInputs = new ArrayList<>();
        userInputs.add("1");

        when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
        when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(ivrCanstEntity);

        IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD240(sessionId);

        assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());

    }

    @Test
    void testStateFTD231() throws JsonProcessingException {
        IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
        responseDto.setHookReturnCode(HOOK_RETURN_1);
        String sessionId = "session123";

        IVRUserSession session = new IVRUserSession();
        IVRCanstEntity ivrCanstEntity = new IVRCanstEntity();
        CurrentAssignmentResponseDto currentAssignmentResponseDto = new CurrentAssignmentResponseDto();
        currentAssignmentResponseDto.setRequestId("7443875");

        session.setCurrentAssignmentResponse("hello hi");

        List<String> userInputs = new ArrayList<>();
        userInputs.add("1");

        when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
        when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(ivrCanstEntity);
        when(mockObjectMapper.readValue("hello hi", CurrentAssignmentResponseDto.class)).thenReturn(currentAssignmentResponseDto);
        when(mockIvrCanstHelper.getColourCode(any(), any(), any())).thenReturn(responseDto);


        IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD231(sessionId);

        assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());

    }




    @Test
    void testStateFTD400ForAlphaPager() throws JsonProcessingException, HttpTimeoutException{
        IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
        responseDto.setHookReturnCode(HOOK_RETURN_3);
        String sessionId = "session123";
        TNInfoResponse response = new TNInfoResponse();
        response.setPrimaryNXX("NXX");
        response.setPrimaryNPA("NPA");

        IVRUserSession session = new IVRUserSession();

        MessageStatus messageStatus = new MessageStatus();
        messageStatus.setErrorStatus("S");
        UpdateLoopResponseDto updateLoopResponseDto = new UpdateLoopResponseDto();
        updateLoopResponseDto.setRequestId("7443875");
        updateLoopResponseDto.setMessageStatus(messageStatus);
        session.setLosDbResponse("how are you");

        session.setCurrentAssignmentResponse("hello hi");
		session.setCanBePagedMobile(Boolean.TRUE);
        UpdateLoopRequestDto requestDto = new UpdateLoopRequestDto();
        IVRCanstEntity ivrCanstEntity = new IVRCanstEntity();

        List<String> userInputs = new ArrayList<>();
        userInputs.add("1");


        when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(session.getLosDbResponse())).thenReturn(response);
        when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
        when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(ivrCanstEntity);
        when(mockObjectMapper.writeValueAsString(requestDto)).thenReturn("request");
        when(mockIvrCanstHelper.buildUpdateLoopAssignmentRequest(any(), any(), any(), any())).thenReturn(requestDto);
        when(asyncService.updateLoopRequest(any(), any(), any(), any(), any())).thenReturn(CompletableFuture.completedFuture("mock-update-response"));

        IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD400(sessionId);

        assertEquals(HOOK_RETURN_3, actualResponse.getHookReturnCode());

    }

	@Test
	void testStateFTD400ForNonAlphaPager() throws JsonProcessingException, HttpTimeoutException {
		IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
		responseDto.setHookReturnCode(HOOK_RETURN_2);
		String sessionId = "session123";
		TNInfoResponse response = new TNInfoResponse();
		response.setPrimaryNXX("NXX");
		response.setPrimaryNPA("NPA");

		IVRUserSession session = new IVRUserSession();

		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setErrorStatus("S");
		UpdateLoopResponseDto updateLoopResponseDto = new UpdateLoopResponseDto();
		updateLoopResponseDto.setRequestId("7443875");
		updateLoopResponseDto.setMessageStatus(messageStatus);
		session.setLosDbResponse("how are you");

		session.setCurrentAssignmentResponse("hello hi");
		UpdateLoopRequestDto requestDto = new UpdateLoopRequestDto();
		IVRCanstEntity ivrCanstEntity = new IVRCanstEntity();

		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");


		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(session.getLosDbResponse())).thenReturn(response);
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
		when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(ivrCanstEntity);
		when(mockObjectMapper.writeValueAsString(requestDto)).thenReturn("request");
		when(mockIvrCanstHelper.buildUpdateLoopAssignmentRequest(any(), any(), any(), any())).thenReturn(requestDto);
		when(asyncService.updateLoopRequest(any(), any(), any(), any(), any())).thenReturn(CompletableFuture.completedFuture("mock-update-response"));

		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD400(sessionId);

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());

	}


    @Test
    void testprocessFTD330Valid() throws JsonMappingException, JsonProcessingException {

        String sessionId = "session123";
        String currentState = "FTD330";

        ChangeLoopAssignmentResponse changeLoopAssignmentResponse = new ChangeLoopAssignmentResponse();

        messageStatus.setErrorStatus("S");
        changeLoopAssignmentResponse.setMessageStatus(messageStatus);

        ChangeLoopAssignmentCandidatePairInfo candidatePairInfo = new ChangeLoopAssignmentCandidatePairInfo();
        candidatePairInfo.setCableId("22");
        candidatePairInfo.setCableUnitId("1234");

        List<ChangeLoopAssignmentCandidatePairInfo> candidatePairInfoList = new ArrayList<>();
        candidatePairInfoList.add(candidatePairInfo);

        ChangeLoopAssignmentReturnDataSet returnDataSet = new ChangeLoopAssignmentReturnDataSet();
        returnDataSet.setChangeLoopAssignmentCandidatePairInfo(candidatePairInfoList);
        changeLoopAssignmentResponse.setReturnDataSet(returnDataSet);

        IVRCanstEntity session = new IVRCanstEntity();
        session.setSessionId(sessionId);
        session.setChangeServTermResp("test data");

        when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(session);
        when(mockObjectMapper.readValue(session.getChangeServTermResp(), ChangeLoopAssignmentResponse.class)).thenReturn(changeLoopAssignmentResponse);

        IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD330(sessionId, currentState);
        assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());

    }

    @Test
    void testprocessFTD330Invalid_HK5() throws JsonMappingException, JsonProcessingException {

        String sessionId = "session123";
        String currentState = "FTD330";

        ChangeLoopAssignmentResponse changeLoopAssignmentResponse = new ChangeLoopAssignmentResponse();

        messageStatus.setErrorStatus("F");
        changeLoopAssignmentResponse.setMessageStatus(messageStatus);

        ChangeLoopAssignmentReturnDataSet returnDataSet = new ChangeLoopAssignmentReturnDataSet();
        returnDataSet.setChangeLoopAssignmentCandidatePairInfo(null);
        changeLoopAssignmentResponse.setReturnDataSet(returnDataSet);

        IVRCanstEntity session = new IVRCanstEntity();
        session.setSessionId(sessionId);
        session.setChangeServTermResp("test data");

        when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(session);
        when(mockObjectMapper.readValue(session.getChangeServTermResp(), ChangeLoopAssignmentResponse.class)).thenReturn(changeLoopAssignmentResponse);

        IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD330(sessionId, currentState);
        assertEquals(IVRHookReturnCodes.HOOK_RETURN_5, actualResponse.getHookReturnCode());

    }

    @Test
    void testprocessFTD330InValid_HK1() throws JsonMappingException, JsonProcessingException {

        String sessionId = "session123";
        String currentState = "FTD330";

        ChangeLoopAssignmentResponse changeLoopAssignmentResponse = new ChangeLoopAssignmentResponse();

        messageStatus.setErrorStatus("F");
        changeLoopAssignmentResponse.setMessageStatus(messageStatus);

        ChangeLoopAssignmentCandidatePairInfo candidatePairInfo = new ChangeLoopAssignmentCandidatePairInfo();
        candidatePairInfo.setCableId("22");
        candidatePairInfo.setCableUnitId("1234");

        List<ChangeLoopAssignmentCandidatePairInfo> candidatePairInfoList = new ArrayList<>();
        candidatePairInfoList.add(candidatePairInfo);

        ChangeLoopAssignmentReturnDataSet returnDataSet = new ChangeLoopAssignmentReturnDataSet();
        returnDataSet.setChangeLoopAssignmentCandidatePairInfo(candidatePairInfoList);
        changeLoopAssignmentResponse.setReturnDataSet(returnDataSet);

        IVRCanstEntity session = new IVRCanstEntity();
        session.setSessionId(sessionId);
        session.setChangeServTermResp("test data");

        when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(session);
        when(mockObjectMapper.readValue(session.getChangeServTermResp(), ChangeLoopAssignmentResponse.class)).thenReturn(changeLoopAssignmentResponse);

        IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD330(sessionId, currentState);
        assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());

    }


	@Test
	void testprocessFTD317_HK2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FTD317";
		
		loop.setCKID("763 757-4229");
		loop.setSO(null);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		
		CurrentAssignmentResponseDto currentAssignmentRespons = new CurrentAssignmentResponseDto();
		currentAssignmentRespons.setReturnDataSet(returnDataSet);
				
		IVRUserSession ivrsession = new IVRUserSession();
		ivrsession.setSessionId(sessionId);
		ivrsession.setNpaPrefix("56");
		ivrsession.setLosDbResponse("test tn details");
		ivrsession.setCurrentAssignmentResponse("xyz");
	
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(ivrsession);
		when(mockObjectMapper.readValue(ivrsession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class)).thenReturn(currentAssignmentRespons);
		
		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD317(sessionId, currentState);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
		
	}

	@Test
	void testprocessFTD317Valid() throws JsonMappingException, JsonProcessingException {

		String sessionId = "sesion123";
		String currentState = "FTD317";
	
		ChangeLoopAssignmentResponse changeLoopAssignmentResponse=new ChangeLoopAssignmentResponse();
		messageStatus.setErrorStatus("S");
		changeLoopAssignmentResponse.setMessageStatus(messageStatus);
		
		ChangeLoopAssignmentCandidatePairInfo candidatePairInfo=new ChangeLoopAssignmentCandidatePairInfo();
		candidatePairInfo.setCableId("22");
		candidatePairInfo.setCableUnitId("1234");
		
		List<ChangeLoopAssignmentCandidatePairInfo> candidatePairInfoList=new ArrayList<>();
		candidatePairInfoList.add(candidatePairInfo);
		 
		ChangeLoopAssignmentReturnDataSet changeLoopreturnDataSet=new ChangeLoopAssignmentReturnDataSet();
		changeLoopreturnDataSet.setChangeLoopAssignmentCandidatePairInfo(candidatePairInfoList);
		changeLoopAssignmentResponse.setReturnDataSet(changeLoopreturnDataSet);
		
		IVRCanstEntity session=new IVRCanstEntity();
		session.setSessionId(sessionId);
		session.setChangeServTermResp(changeLoopAssignmentResponse.toString());
	
		so.setORD("True");	
		so.setCKID("763 757-4229");
		soList.add(so);
		so.setLSTFN(lstfnList);		
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		CurrentAssignmentResponseDto currentAssignmentRespons = new CurrentAssignmentResponseDto();
		currentAssignmentRespons.setReturnDataSet(returnDataSet);
				
		IVRUserSession ivrsession = new IVRUserSession();
		ivrsession.setSessionId(sessionId);
		ivrsession.setLosDbResponse("test tn details");
		ivrsession.setCurrentAssignmentResponse("xyz");
		ivrsession.setCanBePagedEmail(true);
		ivrsession.setCanBePagedMobile(true);
	
		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");
		
		ChangeLoopAssignmentInputData changeLoopAssignmentInputData=new ChangeLoopAssignmentInputData();
		changeLoopAssignmentInputData.setAutoSelectionFlag(true);
		changeLoopAssignmentInputData.setCurrentLoopDetails(null);
		
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa("763");
		wireCtrPrimaryNPANXX.setNxx("757");
		changeLoopAssignmentInputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		
		ChangeLoopAssignmentRequest changeLoopAssignmentRequest =new ChangeLoopAssignmentRequest();
		changeLoopAssignmentRequest.setRequestId("1");
		changeLoopAssignmentRequest.setInputData(changeLoopAssignmentInputData);
		
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(ivrsession);
		when(mockObjectMapper.readValue(ivrsession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class)).thenReturn(currentAssignmentRespons);
		when(mockIVRLfacsServiceHelper.isSeviceOrder(currentAssignmentRespons)).thenReturn(true);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(ivrsession.getLosDbResponse())).thenReturn(losDbResponse);
		
		when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(session);
		when(mockIvrCanstHelper.buildChangeLoopAssignmentRequest("7637574229","763","757","sesion123",session,ivrsession)).thenReturn(changeLoopAssignmentRequest);
		
		when(mockIvrHttpClient.httpPostCall(null,null, sessionId, "Change Loop Assignment Inquiry API")).thenReturn(changeLoopAssignmentResponse.toString());
		when(mockIVRLfacsServiceHelper.cleanResponseString(changeLoopAssignmentResponse.toString())).thenReturn(changeLoopAssignmentResponse.toString());
		when(mockObjectMapper.readValue(session.getChangeServTermResp(), ChangeLoopAssignmentResponse.class)).thenReturn(changeLoopAssignmentResponse);
		when(ivrCanstPagerText.getChangeNewSerTermPagerText(sessionId)).thenReturn("pager text");
		
		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD317(sessionId, currentState);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, actualResponse.getHookReturnCode());
		
	}
	
	@Test
	void testprocessFTD380Valid() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FTD380";
		
		ChangeLoopAssignmentResponse changeLoopAssignmentResponse=new ChangeLoopAssignmentResponse();
		
		messageStatus.setErrorStatus("S");
		changeLoopAssignmentResponse.setMessageStatus(messageStatus);
		
		ChangeLoopAssignmentCandidatePairInfo candidatePairInfo=new ChangeLoopAssignmentCandidatePairInfo();
		candidatePairInfo.setCableId("22");
		candidatePairInfo.setCableUnitId("1234");
		
		List<ChangeLoopAssignmentCandidatePairInfo> candidatePairInfoList=new ArrayList<>();
		candidatePairInfoList.add(candidatePairInfo);
		 
		ChangeLoopAssignmentReturnDataSet returnDataSet=new ChangeLoopAssignmentReturnDataSet();
		returnDataSet.setChangeLoopAssignmentCandidatePairInfo(candidatePairInfoList);
		changeLoopAssignmentResponse.setReturnDataSet(returnDataSet);
		
		IVRCanstEntity session=new IVRCanstEntity();
		session.setSessionId(sessionId);
		session.setChangeServTermResp("test data");
		
		when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(session);
		when(mockObjectMapper.readValue(session.getChangeServTermResp(), ChangeLoopAssignmentResponse.class)).thenReturn(changeLoopAssignmentResponse);
		
		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD380(sessionId, currentState);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());
		
	}

	@Test
	void testprocessFTD380Invalid_HK5() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FTD380";
		
		ChangeLoopAssignmentResponse changeLoopAssignmentResponse=new ChangeLoopAssignmentResponse();
		
		messageStatus.setErrorStatus("F");
		changeLoopAssignmentResponse.setMessageStatus(messageStatus);
		
		ChangeLoopAssignmentReturnDataSet returnDataSet=new ChangeLoopAssignmentReturnDataSet();
		returnDataSet.setChangeLoopAssignmentCandidatePairInfo(null);
		changeLoopAssignmentResponse.setReturnDataSet(returnDataSet);
		
		IVRCanstEntity session=new IVRCanstEntity();
		session.setSessionId(sessionId);
		session.setChangeServTermResp("test data");
		
		when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(session);
		when(mockObjectMapper.readValue(session.getChangeServTermResp(), ChangeLoopAssignmentResponse.class)).thenReturn(changeLoopAssignmentResponse);
		
		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD380(sessionId, currentState);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_5, actualResponse.getHookReturnCode());
		
	}
	
	@Test
	void testprocessFTD380InValid_HK1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FTD380";
		
		ChangeLoopAssignmentResponse changeLoopAssignmentResponse=new ChangeLoopAssignmentResponse();
		
		messageStatus.setErrorStatus("F");
		changeLoopAssignmentResponse.setMessageStatus(messageStatus);
		
		ChangeLoopAssignmentCandidatePairInfo candidatePairInfo=new ChangeLoopAssignmentCandidatePairInfo();
		candidatePairInfo.setCableId("22");
		candidatePairInfo.setCableUnitId("1234");
		
		List<ChangeLoopAssignmentCandidatePairInfo> candidatePairInfoList=new ArrayList<>();
		candidatePairInfoList.add(candidatePairInfo);
		 
		ChangeLoopAssignmentReturnDataSet returnDataSet=new ChangeLoopAssignmentReturnDataSet();
		returnDataSet.setChangeLoopAssignmentCandidatePairInfo(candidatePairInfoList);
		changeLoopAssignmentResponse.setReturnDataSet(returnDataSet);
		
		IVRCanstEntity session=new IVRCanstEntity();
		session.setSessionId(sessionId);
		session.setChangeServTermResp("test data");
		
		when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(session);
		when(mockObjectMapper.readValue(session.getChangeServTermResp(), ChangeLoopAssignmentResponse.class)).thenReturn(changeLoopAssignmentResponse);
		
		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD380(sessionId, currentState);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());
		
	}


	@Test
	void testProcessFTD320Code_HookCode0()
			throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "FID055";

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(true);
		
		IVRCanstEntity session=new IVRCanstEntity();
		session.setSessionId(sessionId);
		session.setChangeServTermResp("test data");
		
		when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(session);

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String mockLosDbJsonString = "mock-losdb-response-string";

		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvMaintChngeMsgName(mockLosDbJsonString);

		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("0");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD320(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFTD320Code_HookCode2_1()
			throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException {

		currentAssignment = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":[{\"UTYP\":\"10151\",\"UID\":\"QUINCE ST NW\",\"STYP\":\"COON RPDS\",\"SID\":\"MN\",\"ETYP\":\"MN\",\"EID\":\"MN\"}],\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"ONE1\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"R,43\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"BK,324\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}";

		currentAssignmentResponseDto = new ObjectMapper().readValue(currentAssignment,
				CurrentAssignmentResponseDto.class);
		
		String sessionId = "session123";
		String currentState = "FID055";
		CurrentAssignmentResponseDto currentAssignmentResponse = new CurrentAssignmentResponseDto();
		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(null);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(true);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");
		

		IVRCanstEntity session=new IVRCanstEntity();
		session.setSessionId(sessionId);
		session.setChangeServTermResp("test data");
		
		when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(session);

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		RetrieveLoopAssignmentRequest mockRequest = new RetrieveLoopAssignmentRequest();
		RetrieveLoopAssignmentResponse mockResponse = new RetrieveLoopAssignmentResponse();
		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvMaintChngeMsgName(mockLosDbJsonString);
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(mockIVRLfacsServiceHelper.buildRetriveLoopAssignInqRequest(anyString(), anyString(),
				anyString(),any(), any(), anyInt())).thenReturn(mockRequest);
		when(mockIvrCanstHelper.getSeviceOrder(any())).thenReturn(null);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		
		when(mockIVRLfacsServiceHelper.callSparePairLoopAssignInquiryLfacs(jsonRequestString, mockSession))
				.thenReturn(mockLfacsResponse);
		//when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);
		when(mockObjectMapper.readValue(mockLfacsResponse, RetrieveLoopAssignmentResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD320(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}
	
	@Test
	void testProcessFTD320Code_HookCode2()
			throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException {

		currentAssignment = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":[{\"UTYP\":\"10151\",\"UID\":\"QUINCE ST NW\",\"STYP\":\"COON RPDS\",\"SID\":\"MN\",\"ETYP\":\"MN\",\"EID\":\"MN\"}],\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"ONE1\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"R,43\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"BK,324\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}";

		currentAssignmentResponseDto = new ObjectMapper().readValue(currentAssignment,
				CurrentAssignmentResponseDto.class);
		
		String sessionId = "session123";
		String currentState = "FTD320";
		CurrentAssignmentResponseDto currentAssignmentResponse = new CurrentAssignmentResponseDto();
		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(null);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(true);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");
		

		IVRCanstEntity session=new IVRCanstEntity();
		session.setSessionId(sessionId);
		session.setChangeServTermResp("test data");
		
		when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(session);

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		RetrieveMaintenanceChangeTicketRequest mockRequest = new RetrieveMaintenanceChangeTicketRequest();
		RetrieveMaintenanceChangeTicketResponse mockResponse = new RetrieveMaintenanceChangeTicketResponse();
		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvMaintChngeMsgName(mockLosDbJsonString);
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		//when(mockIVRLfacsServiceHelper.buildRetriveMainInqRequest(mockTnInfo.getTn(), mockTnInfo.getPrimaryNPA(),
			//	mockTnInfo.getPrimaryNXX(),currentAssignmentResponseDto, mockSession, 0)).thenReturn(mockRequest);
		when(mockIVRLfacsServiceHelper.buildRetriveMainInqRequest(anyString(), anyString(),
				anyString(),any(), any(), anyInt())).thenReturn(mockRequest);
		when(mockIvrCanstHelper.getSeviceOrder(any())).thenReturn("not empty");
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIVRLfacsServiceHelper.callSparePairInquiryLfacs(jsonRequestString, mockSession))
				.thenReturn(mockLfacsResponse);
		//when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);
		when(mockObjectMapper.readValue(mockLfacsResponse, RetrieveMaintenanceChangeTicketResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD320(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}
	
	  @Test
	    void testprocessFTD030_HookCode_0() throws JsonMappingException, JsonProcessingException {

	        String sessionId = "session321";
	        String currentState = "FTD011";

	        CurrentAssignmentResponseDto currentAssignmentResponse = new CurrentAssignmentResponseDto();

	        IVRUserSession session = new IVRUserSession();
	        session.setSessionId(sessionId);
	        session.setNpaPrefix("56");

	        List<SEG> segList = new ArrayList<SEG>();

	        SEG seg1 = new SEG();
	        seg1.setSEGNO("1");
	        segList.add(seg1);

	        SEG seg2 = new SEG();
	        seg1.setSEGNO("2");
	        segList.add(seg2);

	        loop.setSEG(segList);
	        loop.setCKID("763 757-4229");
	        loop.setTID("DPAAB");
	        loopList.add(loop);
	        returnDataSet.setLoop(loopList);
	     
	        currentAssignmentResponse.setReturnDataSet(returnDataSet);

	        messageStatus.setErrorStatus("S");
	        currentAssignmentResponse.setMessageStatus(messageStatus);

	        session.setCurrentAssignmentResponse(currentAssignmentResponse.toString());

	        List<String> userInputs = new ArrayList<>();
	        userInputs.add("32");

	        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
	        response.setSessionId(sessionId);
	        response.setCurrentState(currentState);
	        response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_0);
	        
	        IVRCanstEntity cnstsession=new IVRCanstEntity();
	        cnstsession.setSessionId(sessionId);
	        cnstsession.setChangeServTermResp("test data");
			
	        when(mockIVRLfacsServiceImpl.processFID020Code(sessionId, currentState)).thenReturn(response);
	        when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
	        when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(cnstsession);
	        when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class)).thenReturn(currentAssignmentResponse);

	        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD030(sessionId, currentState);
	        assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, actualResponse1.getHookReturnCode());
	    }

	  @Test
	    void testprocessFTD060_HookCode_0() throws JsonMappingException, JsonProcessingException {

	        String sessionId = "session321";
	        String currentState = "FTD060";
	        
	        IVRUserSession session = new IVRUserSession();
	        session.setSessionId(sessionId);
	        session.setNpaPrefix("56");

	        List<String> userInputs = new ArrayList<>();
	        userInputs.add("1");
	        userInputs.add("2");
			
	        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD060(sessionId, currentState,userInputs);
	        assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, actualResponse1.getHookReturnCode());
	    }

	  
	  @Test
	    void testprocessFTD060_HookCode_2() throws JsonMappingException, JsonProcessingException {

	        String sessionId = "session321";
	        String currentState = "FTD060";
	     
	        List<String> userInputs = new ArrayList<>();
	        userInputs.add("1*");
	        userInputs.add("3");
			
	        when(mockIvrCanstHelper.convertInputCodesToAlphabets("1*")).thenReturn("1*2345678");
	        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD060(sessionId, currentState,userInputs);
	        assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse1.getHookReturnCode());
	    }
	  
	  @Test
	    void testprocessFTD060_HookCode_1() throws JsonMappingException, JsonProcessingException {

	        String sessionId = "session321";
	        String currentState = "FTD060";
	     
	        List<String> userInputs = new ArrayList<>();
	        userInputs.add("1*");
	        userInputs.add("2");
			
	        when(mockIvrCanstHelper.convertInputCodesToAlphabets("1*")).thenReturn("C1*23456789");
	        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD060(sessionId, currentState,userInputs);
	        assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse1.getHookReturnCode());
	    }
	  
	  @Test
	    void testprocessFTD060_HookCode() throws JsonMappingException, JsonProcessingException {

	        String sessionId = "session321";
	        String currentState = "FTD060";
	     
	        List<String> userInputs = new ArrayList<>();
	        userInputs.add("1*");
	        userInputs.add("1");
			
	        when(mockIvrCanstHelper.convertInputCodesToAlphabets("1*")).thenReturn(null);
	        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD060(sessionId, currentState,userInputs);
	        assertEquals(IVRHookReturnCodes.HOOK_RETURN, actualResponse1.getHookReturnCode());
	    }
	  
	  @Test
	    void testprocessFTD160_HookCode_0() throws JsonMappingException, JsonProcessingException {

	        String sessionId = "session321";
	        String currentState = "FTD011";

	        CurrentAssignmentResponseDto currentAssignmentResponse = new CurrentAssignmentResponseDto();

	        IVRUserSession session = new IVRUserSession();
	        session.setSessionId(sessionId);
	        session.setNpaPrefix("56");

	        List<SEG> segList = new ArrayList<SEG>();

	        SEG seg1 = new SEG();
	        seg1.setSEGNO("1");
	        segList.add(seg1);

	        SEG seg2 = new SEG();
	        seg1.setSEGNO("2");
	        segList.add(seg2);

	        loop.setSEG(segList);
	        loop.setCKID("763 757-4229");
	        loop.setTID("DPAAB");
	        loopList.add(loop);
	        returnDataSet.setLoop(loopList);
	     
	        currentAssignmentResponse.setReturnDataSet(returnDataSet);

	        messageStatus.setErrorStatus("S");
	        currentAssignmentResponse.setMessageStatus(messageStatus);

	        session.setCurrentAssignmentResponse(currentAssignmentResponse.toString());

	        IVRWebHookResponseDto response = new IVRWebHookResponseDto();
	        response.setSessionId(sessionId);
	        response.setCurrentState(currentState);
	        response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_0);
	        
	        IVRCanstEntity cnstsession=new IVRCanstEntity();
	        cnstsession.setSessionId(sessionId);
	        cnstsession.setChangeServTermResp("test data");
			
	        when(mockIVRLfacsServiceImpl.processFID020Code(sessionId, currentState)).thenReturn(response);
	        when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
	        when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(cnstsession);
	        when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class)).thenReturn(currentAssignmentResponse);

	        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD160(sessionId, currentState);
	        assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, actualResponse1.getHookReturnCode());
	    }

	
	@Test
	void testprocessFTD220() throws JsonMappingException, JsonProcessingException {

			String sessionId = "session123";
			String currentState = "FTD220";
			
			AssignServiceOrderResponse assignServiceOrderResponse=new AssignServiceOrderResponse();
			
			 errorList.setErrorCode("I");
		     errorList.setErrorMessage("L150-435");
		     errorLists.add(errorList);

		     hostErrorList.setId("LFACS");
		     hostErrorList.setErrorList(errorLists);
		     HostErrorLists.add(hostErrorList);

		     messageStatus.setHostErrorList(HostErrorLists);

			messageStatus.setErrorStatus("F");
			assignServiceOrderResponse.setMessageStatus(messageStatus);
			
			AssignServiceOrderResponseReturnDataSet returnDataSet=new AssignServiceOrderResponseReturnDataSet();
			returnDataSet.setCircuitId("1");
			assignServiceOrderResponse.setReturnDataSet(returnDataSet);
			
			IVRCanstEntity session=new IVRCanstEntity();
			session.setSessionId(sessionId);
			session.setChangeServTermResp("test data");
			session.setAssignOrderServiceResp(assignServiceOrderResponse.toString());
			
			when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(session);
			when(mockObjectMapper.readValue(session.getAssignOrderServiceResp(), AssignServiceOrderResponse.class)).thenReturn(assignServiceOrderResponse);
			
			IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD220(sessionId, currentState);
			assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());
			
		}
	
	@Test
	void testprocessFTD220_Valid() throws JsonMappingException, JsonProcessingException {

			String sessionId = "session123";
			String currentState = "FTD220";
			
			AssignServiceOrderResponse assignServiceOrderResponse=new AssignServiceOrderResponse();

			messageStatus.setErrorStatus("S");
			assignServiceOrderResponse.setMessageStatus(messageStatus);
			
			AssignServiceOrderResponseReturnDataSet returnDataSet=new AssignServiceOrderResponseReturnDataSet();
			returnDataSet.setCircuitId("1");
			assignServiceOrderResponse.setReturnDataSet(returnDataSet);
			
			IVRCanstEntity session=new IVRCanstEntity();
			session.setSessionId(sessionId);
			session.setChangeServTermResp("test data");
			session.setAssignOrderServiceResp(assignServiceOrderResponse.toString());
			
			when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(session);
			when(mockObjectMapper.readValue(session.getAssignOrderServiceResp(), AssignServiceOrderResponse.class)).thenReturn(assignServiceOrderResponse);
			
			IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD220(sessionId, currentState);
			assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());
			
		}
	
	@Test
	void testprocessFTD371_HookCode1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FTD371";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("1");
		response.setHookReturnMessage(IVRLfacsConstants.SYSTEM_DOWN_ERR);

		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("500");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("500");
		errorLists.add(errorList);

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());
		
		List<String> userInputs = new ArrayList<>();
		userInputs.add("0");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		//when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD371(sessionId, currentState, userInputs);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testprocessFTD371_HookCode2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FTD371";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("2");
		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("504");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("504");
		errorLists.add(errorList);

		

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		//when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD371(sessionId, currentState, null);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testprocessFTD371_HookCode3() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FTD371";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("3");
		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("503");
		messageStatus.setErrorStatus("F");
		errorList.setErrorCode("I");
		errorList.setErrorMessage("503");
		errorLists.add(errorList);

		

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		//when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD371(sessionId, currentState, null);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testprocessFTD371_HookCode4() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FTD371";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("4");
		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("400");
		messageStatus.setErrorStatus("F");
		errorList.setErrorCode("I");
		errorList.setErrorMessage("400");
		errorLists.add(errorList);
		
		

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		//when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);
		

		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD371(sessionId, currentState, null);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testprocessFTD371_HookCode5() throws JsonMappingException, JsonProcessingException {


		String sessionId = "session123";
		String currentState = "FID025";

		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("L400-160: CABLE");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("L400-160: CABLE");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse =
				  ivrCanstServiceImpl.processFTD371(sessionId, currentState, null);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_5, actualResponse.getHookReturnCode());
	}

		
		/*
		 * String sessionId = "session123"; String currentState = "FTD371";
		 * 
		 * IVRWebHookResponseDto response = new IVRWebHookResponseDto(); MessageStatus
		 * messageStatus1 = new MessageStatus(); response.setSessionId(sessionId);
		 * response.setCurrentState(currentState); response.setHookReturnCode("5");
		 * IVRUserSession userSession = new IVRUserSession();
		 * messageStatus1.setErrorCode("L400-160: CABLE");
		 * messageStatus1.setErrorStatus("F");
		 * 
		 * errorList.setErrorCode("I"); errorList.setErrorMessage("L400-160: CABLE");
		 * errorLists.add(errorList); TNInfoResponse mockTnInfo = new TNInfoResponse();
		 * mockTnInfo.setTn("1234567890"); mockTnInfo.setPrimaryNPA("123");
		 * mockTnInfo.setPrimaryNXX("456");
		 * 
		 * List<String> userInputs = new ArrayList<>(); userInputs.add("1");
		 * 
		 * messageStatus1.setHostErrorList(HostErrorLists);
		 * currentAssignmentResponseDto.setMessageStatus(messageStatus1);
		 * userSession.setCurrentAssignmentResponse("xyz");
		 * userSession.setLosDbResponse(mockTnInfo.toString());
		 * 
		 * when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		 * when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(),
		 * CurrentAssignmentResponseDto.class))
		 * .thenReturn(currentAssignmentResponseDto);
		 * when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(userSession.
		 * getLosDbResponse())) .thenReturn(mockTnInfo);
		 * 
		 * IVRWebHookResponseDto actualResponse =
		 * ivrCanstServiceImpl.processFTD371(sessionId, currentState, null);
		 * 
		 * assertEquals(IVRHookReturnCodes.HOOK_RETURN_5,
		 * actualResponse.getHookReturnCode());
		 * }
		 */
	
	

	@Test
	void testprocessFTD371_HookCode6() throws JsonMappingException, JsonProcessingException {


		String sessionId = "session123";
		String currentState = "FID025";

		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("L400-160: CABLE PAIR");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("L400-160: CABLE PAIR");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse =
				  ivrCanstServiceImpl.processFTD371(sessionId, currentState, null);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_6, actualResponse.getHookReturnCode());	}

	@Test
	void testprocessFTD371_HookCode7() throws JsonMappingException, JsonProcessingException {


		String sessionId = "session123";
		String currentState = "FID025";

		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("L400-160");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("L400-160");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);
		
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse =
				  ivrCanstServiceImpl.processFTD371(sessionId, currentState, userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	
	@Test
	void testprocessFTD371_HookCode8() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FTD371";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("8");
		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorStatus("S");
		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");
		userSession.setFacsInqType("CP");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD371(sessionId, currentState, null);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());
	}

	@Test
	void testprocessFTD371_CurrentAssignNull() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FTD371";

		IVRUserSession userSession = new IVRUserSession();

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD371(sessionId, currentState, null);

		assertEquals("Current Assignment is null", actualResponse.getHookReturnMessage());
	}

	@Test
	void testprocessFTD371_InvalidSession() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FTD371";

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD371(sessionId, currentState, null);

		assertEquals("In-Valid Session id, please try with a loaded Session", actualResponse.getHookReturnMessage());
	}

	@SuppressWarnings("unchecked")
	@Test
	void testProcessFTD370_Success() throws JsonMappingException, JsonProcessingException, IllegalArgumentException,
			IllegalAccessException, InterruptedException, ExecutionException {

		String sessionId = "session123";
		String currentState = "FTD370";

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);

		IVRCanstEntity ivrCanstEntity = new IVRCanstEntity();
		ivrCanstEntity.setSessionId(sessionId);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		//mockSession.setCable("2");
		//mockSession.setPair("1");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		CurrentAssignmentRequestTnDto mockRequest = new CurrentAssignmentRequestTnDto();
		CurrentAssignmentResponseDto mockResponse = new CurrentAssignmentResponseDto();
		mockSession.setLosDbResponse(mockTnInfo.toString());
		
		InputData id = new InputData();
		
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa("123");
		wireCtrPrimaryNPANXX.setNxx("456");
		id.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		
		CablePair cablePair = new CablePair();
		
		CurrentAssignmentInfo currentAssignmentInfo = new CurrentAssignmentInfo();

		cablePair.setCa(mockSession.getCable());
		cablePair.setPr(mockSession.getPair());
		currentAssignmentInfo.setCablePair(cablePair);
		id.setCurrentAssignmentInfo(currentAssignmentInfo);
		//mockRequest.setRequestId("1");
		mockRequest.setInputData(id);
		// mocking
		when(mockIvrCanstCacheService.getBySessionId(any())).thenReturn(ivrCanstEntity);
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockSession.getLosDbResponse())).thenReturn(mockTnInfo);
		when(mockIvrCanstHelper.buildCurrentAssignmentInqRequest( mockTnInfo.getPrimaryNPA(),
				mockTnInfo.getPrimaryNXX(), userInputDTMFList, mockSession)).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(mockRequest.toString());
		when(mockIVRLfacsServiceHelper.callCurrentAssignmentInquiryLfacs(mockRequest.toString(), mockSession))
				.thenReturn(mockLfacsResponse);
		//when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		//when(mockObjectMapper.readValue(mockLfacsResponse, CurrentAssignmentResponseDto.class))
				//.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD370(sessionId, currentState,userInputDTMFList);

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());

	}
	
	@Test
	void testProcessFTD370_Hook_Return_1() throws JsonProcessingException {
		
		String sessionId = "session123";
		String currentState = "FTD370";

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(userSession.getLosDbResponse()))
				.thenReturn(losDbResponse);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD370(sessionId, currentState,userInputs);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
		
	}
	@Test
	void testProcessFTD370_losDBNullResposne() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentState = "FTD370";

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");
		String mockLosDbJsonString = "";
		mockSession.setLosDbResponse(mockLosDbJsonString);

		// mocking
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD370(sessionId, currentState,userInputDTMFList);
		assertEquals(CANNOT_FETCH_CURR_ASSG_API_LOSDB_NULL, actualResponse.getHookReturnMessage());
	}

	@Test
	void testProcessFTD370_GPDOWN() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentState = "FTD370";
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		String jsonString = "mock-json-resp";
		mockSession.setLosDbResponse(jsonString);
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		// mocking
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		doThrow(JsonMappingException.class).when(mockIVRLfacsServiceHelper).extractTNInfoFromLosDBResponse(jsonString);
		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD370(sessionId, currentState,userInputDTMFList);
		assertEquals(GPDOWN_ERR_MSG, actualResponse.getHookReturnMessage());
	}

	@Test
	void testProcessFTD370_SESSIONidNULL() {
		String sessionId = "session123";
		String currentState = "FTD370";
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");
		
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);
		IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD370(sessionId, currentState,userInputDTMFList);
		assertEquals(INVALID_SESSION_ID, actualResponse.getHookReturnMessage());
	}
	@Test
	void testprocessFTD135() {
		String sessionId = "session123";
		String currentState = "FTD370";
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");
	IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD135(sessionId, currentState,userInputDTMFList.get(0));

	assertEquals(null, actualResponse.getHookReturnCode());
	}

	@Test
	void testStateFTD120() throws JsonProcessingException, HttpTimeoutException{
	    IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
	    responseDto.setHookReturnCode(HOOK_RETURN_3);
	    String sessionId = "session123";
	    TNInfoResponse response = new TNInfoResponse();
	    response.setPrimaryNXX("NXX");
	    response.setPrimaryNPA("NPA");

	    IVRUserSession session = new IVRUserSession();

	    MessageStatus messageStatus = new MessageStatus();
	    messageStatus.setErrorStatus("S");
	    OrderStatusResponse orderStatusResponse = new OrderStatusResponse();
	    orderStatusResponse.setRequestId("7443875");

	    session.setLosDbResponse("how are you");

	    session.setCurrentAssignmentResponse("hello hi");
	    session.setCanBePagedMobile(Boolean.TRUE);
	    OrderStatusRequest requestDto = new OrderStatusRequest();
	    IVRCanstEntity ivrCanstEntity = new IVRCanstEntity();

	    List<String> userInputs = new ArrayList<>();
	    userInputs.add("1");


	    when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(session.getLosDbResponse())).thenReturn(response);
	    when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
	    when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(ivrCanstEntity);
	    when(mockObjectMapper.writeValueAsString(requestDto)).thenReturn("request");
	    when(mockIvrCanstHelper.buildOrderStatusRequest(any(), any(), any(), any())).thenReturn(requestDto);
	    when(mockIvrHttpClient.httpPostCall("request",null, sessionId, "Order Status Inquiry API")).thenReturn("response json");
	    when(mockIVRLfacsServiceHelper.cleanResponseString("response json")).thenReturn("clean json response");
	    when(mockObjectMapper.readValue("clean json response", OrderStatusResponse.class)).thenReturn(orderStatusResponse);

	    IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD120(sessionId, "FTD120");

	    assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());

	}

	@Test
	void testStateFTD120SuccessCase() throws JsonProcessingException, HttpTimeoutException{
	    IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
	    responseDto.setHookReturnCode(HOOK_RETURN_3);
	    String sessionId = "session123";
	    TNInfoResponse response = new TNInfoResponse();
	    response.setPrimaryNXX("NXX");
	    response.setPrimaryNPA("NPA");

	    IVRUserSession session = new IVRUserSession();

	    MessageStatus messageStatus = new MessageStatus();
	    messageStatus.setErrorStatus("S");
	    OrderStatusResponse orderStatusResponse = new OrderStatusResponse();
	    orderStatusResponse.setRequestId("7443875");
	    orderStatusResponse.setMessageStatus(messageStatus);

	    session.setLosDbResponse("how are you");

	    session.setCurrentAssignmentResponse("hello hi");
	    session.setCanBePagedMobile(Boolean.TRUE);
	    OrderStatusRequest requestDto = new OrderStatusRequest();
	    IVRCanstEntity ivrCanstEntity = new IVRCanstEntity();

	    List<String> userInputs = new ArrayList<>();
	    userInputs.add("1");


	    when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(session.getLosDbResponse())).thenReturn(response);
	    when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
	    when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(ivrCanstEntity);
	    when(mockObjectMapper.writeValueAsString(requestDto)).thenReturn("request");
	    when(mockIvrCanstHelper.buildOrderStatusRequest(any(), any(), any(), any())).thenReturn(requestDto);
	    when(mockIvrHttpClient.httpPostCall("request",null, sessionId, "Order Status Inquiry API")).thenReturn("response json");
	    when(mockIVRLfacsServiceHelper.cleanResponseString("response json")).thenReturn("clean json response");
	    when(mockObjectMapper.readValue("clean json response", OrderStatusResponse.class)).thenReturn(orderStatusResponse);

	    IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD120(sessionId, "FTD120");

	    assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());

	}


	@Test
	void testStateFTD120FailureCase() throws JsonProcessingException, HttpTimeoutException{
	    IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
	    responseDto.setHookReturnCode(HOOK_RETURN_3);
	    String sessionId = "session123";
	    TNInfoResponse response = new TNInfoResponse();
	    response.setPrimaryNXX("NXX");
	    response.setPrimaryNPA("NPA");

	    IVRUserSession session = new IVRUserSession();

	    MessageStatus messageStatus = new MessageStatus();
	    messageStatus.setErrorStatus("S");
	    OrderStatusResponse orderStatusResponse = new OrderStatusResponse();
	    orderStatusResponse.setRequestId("7443875");
	    orderStatusResponse.setMessageStatus(messageStatus);

	    session.setLosDbResponse("how are you");

	    session.setCurrentAssignmentResponse("hello hi");
	    session.setCanBePagedMobile(Boolean.TRUE);
	    OrderStatusRequest requestDto = new OrderStatusRequest();
	    IVRCanstEntity ivrCanstEntity = new IVRCanstEntity();

	    List<String> userInputs = new ArrayList<>();
	    userInputs.add("1");


	    when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(session.getLosDbResponse())).thenReturn(response);
	    when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
	    when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(null);

	    IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD120(sessionId, "FTD120");

	    assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());

	}

	@Test
	void testStateFTD120ExceptionCase() throws JsonProcessingException, HttpTimeoutException{
	    IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
	    responseDto.setHookReturnCode(HOOK_RETURN_3);
	    String sessionId = "session123";
	    TNInfoResponse response = new TNInfoResponse();
	    response.setPrimaryNXX("NXX");
	    response.setPrimaryNPA("NPA");

	    IVRUserSession session = new IVRUserSession();

	    MessageStatus messageStatus = new MessageStatus();
	    messageStatus.setErrorStatus("S");
	    OrderStatusResponse orderStatusResponse = new OrderStatusResponse();
	    orderStatusResponse.setRequestId("7443875");

	    session.setLosDbResponse("how are you");

	    session.setCurrentAssignmentResponse("hello hi");
	    session.setCanBePagedMobile(Boolean.TRUE);
	    OrderStatusRequest requestDto = new OrderStatusRequest();
	    IVRCanstEntity ivrCanstEntity = new IVRCanstEntity();

	    List<String> userInputs = new ArrayList<>();
	    userInputs.add("1");


	    when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(session.getLosDbResponse())).thenReturn(response);
	    when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
	    when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(ivrCanstEntity);
	    when(mockObjectMapper.writeValueAsString(requestDto)).thenReturn("request");
	    when(mockIvrCanstHelper.buildOrderStatusRequest(any(), any(), any(), any())).thenReturn(requestDto);
	    when(mockIvrHttpClient.httpPostCall(null,null, sessionId, "Order Status Inquiry API")).thenReturn("response json");
	    when(mockIVRLfacsServiceHelper.cleanResponseString("response json")).thenReturn("clean json response");
	    when(mockObjectMapper.readValue("clean json response", OrderStatusResponse.class)).thenReturn(orderStatusResponse);

	    IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD120(sessionId, "FTD120");

	    assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());

	}

	@Test
	void testStateFTD120UserSessionNull() throws JsonProcessingException, HttpTimeoutException{
	    IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
	    responseDto.setHookReturnCode(HOOK_RETURN_3);
	    String sessionId = "session123";
	    TNInfoResponse response = new TNInfoResponse();
	    response.setPrimaryNXX("NXX");
	    response.setPrimaryNPA("NPA");

	    IVRUserSession session = new IVRUserSession();

	    MessageStatus messageStatus = new MessageStatus();
	    messageStatus.setErrorStatus("S");
	    OrderStatusResponse orderStatusResponse = new OrderStatusResponse();
	    orderStatusResponse.setRequestId("7443875");

	    session.setLosDbResponse("how are you");

	    session.setCurrentAssignmentResponse("hello hi");
	    session.setCanBePagedMobile(Boolean.TRUE);
	    OrderStatusRequest requestDto = new OrderStatusRequest();
	    IVRCanstEntity ivrCanstEntity = new IVRCanstEntity();

	    List<String> userInputs = new ArrayList<>();
	    userInputs.add("1");


	    when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);

	    IVRWebHookResponseDto actualResponse = ivrCanstServiceImpl.processFTD120(sessionId, "FTD120");

	    assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());

	}
	
	 @Test
	    void testProcessFTD197_HookCode_2() throws JsonMappingException, JsonProcessingException {

	        String sessionId = "session321";
	        String currentState = "FTD197";

	        CurrentAssignmentResponseDto currentAssignmentResponse = new CurrentAssignmentResponseDto();

	        
	        so.setORD("True");
	        soList.add(so);
	        so.setLSTFN(lstfnList);
	        loop.setCKID("763 757-4229");
	        loop.setSO(soList);
	        loop.setTID("DPAAB");
	        loopList.add(loop);
	        returnDataSet.setLoop(loopList);
	        returnDataSet.setPort1("test");
	        currentAssignmentResponse.setReturnDataSet(returnDataSet);
	        messageStatus.setErrorStatus("S");
	        currentAssignmentResponse.setMessageStatus(messageStatus);

	        IVRUserSession session = new IVRUserSession();
	        session.setSessionId(sessionId);
	        session.setNpaPrefix("56");
	        session.setCurrentAssignmentResponse(currentAssignmentResponse.toString());


	        when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
	        when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class)).thenReturn(currentAssignmentResponse);
	        when(mockIVRLfacsServiceHelper.isSeviceOrder(currentAssignmentResponse)).thenReturn(true);
	        
	        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD197(sessionId, currentState);
	        assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse1.getHookReturnCode());
	    }

	 @Test
	    void testProcessFTD197_HookCode_1() throws JsonMappingException, JsonProcessingException {

	        String sessionId = "session321";
	        String currentState = "FTD197";

	        CurrentAssignmentResponseDto currentAssignmentResponse = new CurrentAssignmentResponseDto();
	        soList.add(so);
	        so.setLSTFN(lstfnList);
	        loop.setCKID("763 757-4229");
	        loop.setSO(soList);
	        loop.setTID("DPAAB");
	        loopList.add(loop);
	        returnDataSet.setLoop(loopList);
	        returnDataSet.setPort1("test");
	        currentAssignmentResponse.setReturnDataSet(returnDataSet);
	        messageStatus.setErrorStatus("S");
	        currentAssignmentResponse.setMessageStatus(messageStatus);

	        IVRUserSession session = new IVRUserSession();
	        session.setSessionId(sessionId);
	        session.setNpaPrefix("56");
	        session.setCurrentAssignmentResponse(currentAssignmentResponse.toString());

	        when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
	        when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class)).thenReturn(currentAssignmentResponse);
	      
	        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD197(sessionId, currentState);
	        assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse1.getHookReturnCode());
	    }
	 
	  @Test
	    void testprocessFTD351_HookCode_1() throws JsonMappingException, JsonProcessingException {

	        String sessionId = "session321";
	        String currentState = "FTD060";
	     
	        List<String> userInputs = new ArrayList<>();
	        userInputs.add("1*");
	        userInputs.add("2");
			
	        when(mockIvrCanstHelper.convertInputCodesToAlphabets("1*")).thenReturn("C1*23456789");
	        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD351(sessionId, currentState,userInputs.get(0));
	        assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse1.getHookReturnCode());
	    }
	  
	  @Test
	    void testprocessFTD351_HookCode_0() throws JsonMappingException, JsonProcessingException {

	        String sessionId = "session321";
	        String currentState = "FTD060";
	     
	        List<String> userInputs = new ArrayList<>();
	        userInputs.add("1*");
	        userInputs.add("2");
			
	        when(mockIvrCanstHelper.convertInputCodesToAlphabets("1*")).thenReturn(null);
	        IVRWebHookResponseDto actualResponse1 = ivrCanstServiceImpl.processFTD351(sessionId, currentState,userInputs.get(0));
	        assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, actualResponse1.getHookReturnCode());
	    }
}
