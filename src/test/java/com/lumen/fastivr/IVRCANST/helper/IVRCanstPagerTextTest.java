package com.lumen.fastivr.IVRCANST.helper;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCANST.entity.IVRCanstEntity;
import com.lumen.fastivr.IVRCANST.repository.IVRCanstCacheService;
import com.lumen.fastivr.IVRCNF.utils.IVRCnfUtilities;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.LOOP;
import com.lumen.fastivr.IVRDto.ReturnDataSet;
import com.lumen.fastivr.IVRDto.SEG;
import com.lumen.fastivr.IVRDto.SO;
import com.lumen.fastivr.IVRDto.LOSDB.CTelephone;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVREntity.TNInfo;
import com.lumen.fastivr.IVRLFACS.IVRLfacsPagerTextFormation;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;

@ExtendWith(MockitoExtension.class)
public class IVRCanstPagerTextTest {

	@InjectMocks
	private IVRCanstPagerText ivrCanstPagerText;
	
	@Mock
	private ObjectMapper mockObjectMapper;
	
	@Mock
	private IVRCanstHelper mockIvrCanstHelper;	
	
	@Mock
	private IVRCacheService mockCacheService;
	
	@Mock
	private IVRCanstCacheService mockIvrCanstCacheService;
	
	@Mock
	private IVRLfacsServiceHelper mockIVRLfacsServiceHelper;
	
	@Mock
	private IVRCnfUtilities ivrCnfUtilities;
	
	@InjectMocks
	private IVRLfacsPagerTextFormation ivrLfacsPagerTextFormation;
	
	CurrentAssignmentResponseDto currentAssignmentResponseDto = null;

	ReturnDataSet returnDataSet = new ReturnDataSet();
	SO so = new SO();
	List<SO> soList = new ArrayList<>();
	LOOP loop = new LOOP();
	List<LOOP> loopList = new ArrayList<>();
	
	String currentAssignment = null;
	
	TNInfoResponse tnInfoResponse = null;

	@BeforeEach
	void setUp() throws Exception {
		
		tnInfoResponse = new TNInfoResponse();
		TNInfo tnInfo =  new TNInfo();
		String npaPrefix = "219";
		tnInfo.setNpaPrefix(npaPrefix );
		IVRUserSession session = new IVRUserSession();
		session.setSessionId("Session123");
		CTelephone telephone = new CTelephone();
		telephone.setNpa("123");
		telephone.setNxx("456");
		telephone.setLineNumber("7890");
		tnInfoResponse.setTn("23423");
		
		currentAssignmentResponseDto = new CurrentAssignmentResponseDto();
		ReturnDataSet dataSet = new ReturnDataSet();
		List<LOOP> loopList = new ArrayList<LOOP>();
		LOOP loop = new LOOP();

		List<SEG> segList = new ArrayList<SEG>();		
		so.setORD("True");		
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);

		SEG seg = new SEG();
		seg.setCTT("1");
		seg.setCA("ca1");
		seg.setPR("pr1");
		SEG seg2 = new SEG();
		seg.setCTT("2");
		seg.setCA("ca2");
		seg.setPR("pr2");
		SEG seg3 = new SEG();
		seg.setCTT("3");
		seg.setCA("ca3");
		seg.setPR("pr3");
		segList.add(seg);
		segList.add(seg2);
		segList.add(seg3);
		loop.setSEG(segList);
		loopList.add(loop);
		dataSet.setLoop(loopList);
		currentAssignmentResponseDto.setReturnDataSet(dataSet);
	}
	
	@Test
	void testGetChangeNewSerTermPagerText() throws JsonMappingException, JsonProcessingException {
		
		String sessionId = "session123"; 
		
		currentAssignmentResponseDto = new CurrentAssignmentResponseDto();
		ReturnDataSet dataSet = new ReturnDataSet();
		List<LOOP> loopList = new ArrayList<LOOP>();
		LOOP loop = new LOOP();

		List<SEG> segList = new ArrayList<SEG>();
		
		so.setORD("True");		
		soList.add(so);
		loop.setSO(soList);

		SEG seg = new SEG();
		seg.setCTT("1");
		seg.setCA("ca1");
		seg.setPR("pr1");
		SEG seg2 = new SEG();
		seg.setCTT("2");
		seg.setCA("ca2");
		seg.setPR("pr2");
		SEG seg3 = new SEG();
		seg.setCTT("3");
		seg.setCA("ca3");
		seg.setPR("pr3");
		segList.add(seg);
		segList.add(seg2);
		segList.add(seg3);
		loop.setSEG(segList);
		loop.setSTAT("PCF");
		loop.setCKID("CKIDTest");
		loopList.add(loop);
		dataSet.setLoop(loopList);
		currentAssignmentResponseDto.setReturnDataSet(dataSet);

		IVRUserSession session = new IVRUserSession();
		session.setSessionId(sessionId);
		session.setLosDbResponse(tnInfoResponse.toString());
		session.setCurrentAssignmentResponse("test");
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		
		TNInfoResponse tnInfoResponse = new TNInfoResponse();
		tnInfoResponse.setTn("1");
		
		IVRCanstEntity canstSession=new IVRCanstEntity();
		canstSession.setSessionId(sessionId);
		canstSession.setCable("Cable");
		canstSession.setPair("pair");
				
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
		when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class)).thenReturn(currentAssignmentResponseDto);
		when(mockIvrCanstCacheService.getBySessionId(sessionId)).thenReturn(canstSession);	
		when(ivrCnfUtilities.getSegmentList(any())).thenReturn(segList);
		when(ivrCnfUtilities.getServiceOrder(anyList())).thenReturn("test");
		when(mockIvrCanstHelper.getSegmentNumber(canstSession)).thenReturn(0);
		when(ivrCnfUtilities.getCablePairStatus(currentAssignmentResponseDto)).thenReturn("CF");
		when(ivrCnfUtilities.getBpType(0+1, currentAssignmentResponseDto)).thenReturn("BP");
		
		String actualResponse = ivrCanstPagerText.getChangeNewSerTermPagerText(sessionId);
		
		assertNotNull(actualResponse);
		assertEquals("NONE CUT|test|f1: ca3/pr3|to: Cable/pair CF|LFACS is updated.|Break CF BP at tea: |addr:  null", actualResponse);		
	}

}
