package com.lumen.fastivr.IVRMLT.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRMLT.caching.IvrMltCacheService;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRMLT.utils.IVRMltUtilities;

import tollgrade.loopcare.testrequestapi.Mdata;

@ExtendWith(MockitoExtension.class)
class MltPagerTextTest {

	@InjectMocks
	private MltPagerText mltPagerText;
	
	@Mock
	private IvrMltCacheService ivrMltCacheService;
	
	@Mock
	private ObjectMapper mockObjectMapper;

	
	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void getPagerTextTest() throws JsonMappingException, JsonProcessingException {
		
		String sessionId="session123";
		String pageText=" 4064682603 VER S DC-KOHM/VOLT AC-KOHM                  T-R                  T-G                  R-G|YES|TEST EQUIPMENT BUSY|SIMULTANEOUS TEST REQUEST MADE";
		
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionId);
		mockSession.setMltTestResult("{\"testRsp\":{\"hdr\":{\"testSysType\":\"MLT\",\"testRqstrId\":\"requesterID\",\"testSysId\":\"LM50\",\"echo1\":null,\"echo2\":null,\"echo3\":null,\"echo4\":null,\"echo5\":null,\"echo6\":null,\"dataChanObjRef\":\"1701957280.29\",\"callbackObjRef\":null,\"apiversion\":\"2.13\",\"startTime\":0,\"startTranTime\":0},\"listId\":\"listId\",\"tid\":null,\"testby\":null,\"objectId\":\"objectId\",\"language\":null,\"key\":{\"choice\":1,\"unbundleId\":null,\"exk\":null,\"oeid\":null,\"cid\":{\"cktfmt\":80,\"fmtP\":{\"npa\":\"406\",\"nnx\":\"468\",\"line\":\"2603\",\"cktid\":null,\"char1\":0,\"char4\":null,\"dpa\":null,\"lti\":null}}},\"dslamId\":null,\"drack\":null,\"dshelf\":null,\"dslot\":null,\"dline\":null,\"statusCount\":0,\"ipAddress\":null,\"dslamType\":0,\"thType\":0,\"cltmSlot\":0,\"cltAccMode\":0,\"ddslbusy\":0,\"sessionStat\":0,\"finalFlag\":89,\"line24Ind\":1,\"checkRip\":null,\"lnattr\":null,\"resultsModeReported\":\"DR\",\"testTime\":\"20231207135448+0000\",\"testOutcome\":0,\"enhNWGUrl\":null,\"errorCode\":null,\"errorDesc\":null,\"status\":null,\"tags\":null,\"hazPot\":null,\"nocweb\":null,\"bsoReq\":null,\"modemStats\":null,\"a2ModemStats\":null,\"errorStats\":null,\"rawStats\":null,\"pvcStats\":null,\"batgraph\":null,\"requesterStats\":null,\"addressStats\":null,\"routeStats\":null,\"pppoaStats\":null,\"pppoeStats\":null,\"pingStats\":null,\"http\":null,\"tputStats\":null,\"traceRoute\":null,\"htputStats\":null,\"ipoaServer\":null,\"pppoaServer\":null,\"pppoeServer\":null,\"cipStats\":null,\"cicmpStats\":null,\"cudpStats\":null,\"ctcpStats\":null,\"cinfoStats\":null,\"voip\":null,\"syncStatus\":0,\"testBufferId\":null,\"bsoprofile\":null,\"fttpdigLte\":null,\"snmpdata\":null,\"testResultsURL\":null,\"faultLocator\":null,\"d\":{\"item\":[{\"choice\":3,\"potsResults1\":{\"ringer\":{\"choice\":1,\"ringers\":{\"ringersTr\":1,\"ringersTg\":0,\"ringersRg\":0},\"therm\":null},\"balanceC\":\"10\"},\"digResults1\":null,\"detailedNarrative\":null,\"potsResults2\":{\"soakRes\":null,\"loc12Res\":null,\"coinRes\":null,\"infoRes\":null,\"coilRes\":null,\"wideband\":null,\"stiRes\":null,\"nzr\":null,\"mtelRes\":null,\"enhqualRes\":null,\"bponInfoRes\":null,\"textresCount\":0,\"textRes\":null,\"dispCount\":0,\"dispResults\":{\"item\":[{\"loopLength\":\"10\"}]},\"line24\":\"COULD NOT GAIN ACCESS\"},\"digResults2\":null},null,null,null,null,null,null,null,null,null]},\"f\":{\"testCodePrefix\":null,\"testCode\":\"S\",\"testLocation\":null,\"testPoint\":null,\"measurementType\":null,\"faultDirection\":null,\"faultLoop\":null,\"faultAction\":null,\"faultDesc\":null,\"testCodeDescription\":\"TEST EQUIPMENT BUSY;COULD NOT ACCESS;SIMULTANEOUS TEST REQUEST MADE\"},\"curCmd\":\"QUICKX\",\"resultSummary\":null,\"dcount\":1,\"gmodemStats\":null},\"sessionInfo\":null}");
				
		Mdata mdata=new Mdata();
		mdata = new ObjectMapper().readValue("{\"testRsp\":{\"hdr\":{\"testSysType\":\"MLT\",\"testRqstrId\":\"requesterID\",\"testSysId\":\"LM50\",\"echo1\":null,\"echo2\":null,\"echo3\":null,\"echo4\":null,\"echo5\":null,\"echo6\":null,\"dataChanObjRef\":\"1701957280.29\",\"callbackObjRef\":null,\"apiversion\":\"2.13\",\"startTime\":0,\"startTranTime\":0},\"listId\":\"listId\",\"tid\":null,\"testby\":null,\"objectId\":\"objectId\",\"language\":null,\"key\":{\"choice\":1,\"unbundleId\":null,\"exk\":null,\"oeid\":null,\"cid\":{\"cktfmt\":80,\"fmtP\":{\"npa\":\"406\",\"nnx\":\"468\",\"line\":\"2603\",\"cktid\":null,\"char1\":0,\"char4\":null,\"dpa\":null,\"lti\":null}}},\"dslamId\":null,\"drack\":null,\"dshelf\":null,\"dslot\":null,\"dline\":null,\"statusCount\":0,\"ipAddress\":null,\"dslamType\":0,\"thType\":0,\"cltmSlot\":0,\"cltAccMode\":0,\"ddslbusy\":0,\"sessionStat\":0,\"finalFlag\":89,\"line24Ind\":1,\"checkRip\":null,\"lnattr\":null,\"resultsModeReported\":\"DR\",\"testTime\":\"20231207135448+0000\",\"testOutcome\":0,\"enhNWGUrl\":null,\"errorCode\":null,\"errorDesc\":null,\"status\":null,\"tags\":null,\"hazPot\":null,\"nocweb\":null,\"bsoReq\":null,\"modemStats\":null,\"a2ModemStats\":null,\"errorStats\":null,\"rawStats\":null,\"pvcStats\":null,\"batgraph\":null,\"requesterStats\":null,\"addressStats\":null,\"routeStats\":null,\"pppoaStats\":null,\"pppoeStats\":null,\"pingStats\":null,\"http\":null,\"tputStats\":null,\"traceRoute\":null,\"htputStats\":null,\"ipoaServer\":null,\"pppoaServer\":null,\"pppoeServer\":null,\"cipStats\":null,\"cicmpStats\":null,\"cudpStats\":null,\"ctcpStats\":null,\"cinfoStats\":null,\"voip\":null,\"syncStatus\":0,\"testBufferId\":null,\"bsoprofile\":null,\"fttpdigLte\":null,\"snmpdata\":null,\"testResultsURL\":null,\"faultLocator\":null,\"d\":{\"item\":[{\"choice\":3,\"potsResults1\":{\"ringer\":{\"choice\":1,\"ringers\":{\"ringersTr\":1,\"ringersTg\":0,\"ringersRg\":0},\"therm\":null},\"balanceC\":\"10\"},\"digResults1\":null,\"detailedNarrative\":null,\"potsResults2\":{\"soakRes\":null,\"loc12Res\":null,\"coinRes\":null,\"infoRes\":null,\"coilRes\":null,\"wideband\":null,\"stiRes\":null,\"nzr\":null,\"mtelRes\":null,\"enhqualRes\":null,\"bponInfoRes\":null,\"textresCount\":0,\"textRes\":null,\"dispCount\":0,\"dispResults\":{\"item\":[{\"loopLength\":\"10\"}]},\"line24\":\"COULD NOT GAIN ACCESS\"},\"digResults2\":null},null,null,null,null,null,null,null,null,null]},\"f\":{\"testCodePrefix\":null,\"testCode\":\"S\",\"testLocation\":null,\"testPoint\":null,\"measurementType\":null,\"faultDirection\":null,\"faultLoop\":null,\"faultAction\":null,\"faultDesc\":null,\"testCodeDescription\":\"TEST EQUIPMENT BUSY;COULD NOT ACCESS;SIMULTANEOUS TEST REQUEST MADE\"},\"curCmd\":\"QUICKX\",\"resultSummary\":null,\"dcount\":1,\"gmodemStats\":null},\"sessionInfo\":null}",Mdata.class);
				
		when(ivrMltCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockObjectMapper.readValue(mockSession.getMltTestResult(), Mdata.class)).thenReturn(mdata);
		
		String test=IVRMltUtilities.FULLX_TEST;
		String response = mltPagerText.getPagerText(test,sessionId);
	
		String test1=IVRMltUtilities.LOOPX_TEST;
		mltPagerText.getPagerText(test1,sessionId);
		
		String test2=IVRMltUtilities.QUICKX_TEST;
		mltPagerText.getPagerText(test2,sessionId);
		
		String test3=IVRMltUtilities.TONE_PLUS_TEST;
	    mltPagerText.getPagerText(test3,sessionId);
		
		String test4=IVRMltUtilities.TONE_REMOVAL_TEST;
		mltPagerText.getPagerText(test4,sessionId);
		
		assertEquals(pageText, response);
	}
	
	@Test
	void isTestCodeAvailableTest() {
		
		boolean response=mltPagerText.isTestCodeAvailable("SU");
		assertEquals(true, response);
	}

	
	@Test
	void getAC_DCPagerTextTest() throws JsonMappingException, JsonProcessingException {
		
		String sessionId="session123";
	
		String pageText="DC-KOHM/VOLT AC-KOHM 10        10   T-R 10   10 10  T-G 10   10 10  R-G";
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionId);
		mockSession.setOverride("C");
		mockSession.setTestType("48");
		mockSession.setMltTestResult("{\"testRsp\":{\"d\":{\"item\":[{\"potsResults1\":{\"dcCraft\":{\"dcResTr\":\"10\",\"dcResTg\":\"10\",\"dcVoltTg\":\"10\",\"dcResRg\":\"10\",\"dcVoltRg\":\"10\"},\"ac\":{\"acResTr\":\"10\",\"acResTg\":\"10\",\"acResRg\":\"10\"}}}]}}}");
		
		Mdata mdata=new Mdata();
		mdata = new ObjectMapper().readValue("{\"testRsp\":{\"d\":{\"item\":[{\"potsResults1\":{\"dcCraft\":{\"dcResTr\":\"10\",\"dcResTg\":\"10\",\"dcVoltTg\":\"10\",\"dcResRg\":\"10\",\"dcVoltRg\":\"10\"},\"ac\":{\"acResTr\":\"10\",\"acResTg\":\"10\",\"acResRg\":\"10\"}}}]}}}", Mdata.class);
	    
		StringBuilder response=mltPagerText.getAC_DCPagerText(mdata);
		assertEquals(pageText, response.toString());
	}

	@Test
	void getAC_DCPagerTextInvalidTest() throws JsonMappingException, JsonProcessingException {
		
		String sessionId="session123";
	
		String pageText="DC-KOHM/VOLT AC-KOHM                  T-R                  T-G                  R-G";
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionId);
		mockSession.setOverride("C");
		mockSession.setTestType("48");
		mockSession.setMltTestResult("{\"testRsp\":{\"d\":{\"item\":[{\"potsResults1\":{\"dcCraft\":{\"dcResTr\":null,\"dcResTg\":null,\"dcVoltTg\":null,\"dcResRg\":null,\"dcVoltRg\":null},\"ac\":{\"acResTr\":null,\"acResTg\":null,\"acResRg\":null}}}]}}}");
		
		Mdata mdata=new Mdata();
		mdata = new ObjectMapper().readValue("{\"testRsp\":{\"d\":{\"item\":[{\"potsResults1\":{\"dcCraft\":{\"dcResTr\":null,\"dcResTg\":null,\"dcVoltTg\":null,\"dcResRg\":null,\"dcVoltRg\":null},\"ac\":{\"acResTr\":null,\"acResTg\":null,\"acResRg\":null}}}]}}}", Mdata.class);
	    
		StringBuilder response=mltPagerText.getAC_DCPagerText(mdata);
		assertEquals(pageText, response.toString());
	}
	
	@Test
	void getTnTextTest() throws JsonMappingException, JsonProcessingException {
		
		String sessionId="session123";
		String pageText="*4064682603 VER SU ";
		
		
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionId);
		mockSession.setMltTestResult("{\"testRsp\":{\"hdr\":{\"testSysType\":\"MLT\",\"testRqstrId\":\"requesterID\",\"testSysId\":\"LM50\",\"echo1\":null,\"echo2\":null,\"echo3\":null,\"echo4\":null,\"echo5\":null,\"echo6\":null,\"dataChanObjRef\":\"1701957280.29\",\"callbackObjRef\":null,\"apiversion\":\"2.13\",\"startTime\":0,\"startTranTime\":0},\"listId\":\"listId\",\"tid\":null,\"testby\":null,\"objectId\":\"objectId\",\"language\":null,\"key\":{\"choice\":1,\"unbundleId\":null,\"exk\":null,\"oeid\":null,\"cid\":{\"cktfmt\":80,\"fmtP\":{\"npa\":\"406\",\"nnx\":\"468\",\"line\":\"2603\",\"cktid\":null,\"char1\":0,\"char4\":null,\"dpa\":null,\"lti\":null}}},\"dslamId\":null,\"drack\":null,\"dshelf\":null,\"dslot\":null,\"dline\":null,\"statusCount\":0,\"ipAddress\":null,\"dslamType\":0,\"thType\":0,\"cltmSlot\":0,\"cltAccMode\":0,\"ddslbusy\":0,\"sessionStat\":0,\"finalFlag\":89,\"line24Ind\":1,\"checkRip\":null,\"lnattr\":null,\"resultsModeReported\":\"DR\",\"testTime\":\"20231207135448+0000\",\"testOutcome\":0,\"enhNWGUrl\":null,\"errorCode\":null,\"errorDesc\":null,\"status\":null,\"tags\":null,\"hazPot\":null,\"nocweb\":null,\"bsoReq\":null,\"modemStats\":null,\"a2ModemStats\":null,\"errorStats\":null,\"rawStats\":null,\"pvcStats\":null,\"batgraph\":null,\"requesterStats\":null,\"addressStats\":null,\"routeStats\":null,\"pppoaStats\":null,\"pppoeStats\":null,\"pingStats\":null,\"http\":null,\"tputStats\":null,\"traceRoute\":null,\"htputStats\":null,\"ipoaServer\":null,\"pppoaServer\":null,\"pppoeServer\":null,\"cipStats\":null,\"cicmpStats\":null,\"cudpStats\":null,\"ctcpStats\":null,\"cinfoStats\":null,\"voip\":null,\"syncStatus\":0,\"testBufferId\":null,\"bsoprofile\":null,\"fttpdigLte\":null,\"snmpdata\":null,\"testResultsURL\":null,\"faultLocator\":null,\"d\":{\"item\":[{\"choice\":3,\"potsResults1\":{\"ringer\":{\"choice\":1,\"ringers\":{\"ringersTr\":1,\"ringersTg\":0,\"ringersRg\":0},\"therm\":null},\"balanceC\":\"10\"},\"digResults1\":null,\"detailedNarrative\":null,\"potsResults2\":{\"soakRes\":null,\"loc12Res\":null,\"coinRes\":null,\"infoRes\":null,\"coilRes\":null,\"wideband\":null,\"stiRes\":null,\"nzr\":null,\"mtelRes\":null,\"enhqualRes\":null,\"bponInfoRes\":null,\"textresCount\":0,\"textRes\":null,\"dispCount\":0,\"dispResults\":{\"item\":[{\"loopLength\":\"10\"}]},\"line24\":\"COULD NOT GAIN ACCESS\"},\"digResults2\":null},null,null,null,null,null,null,null,null,null]},\"f\":{\"testCodePrefix\":null,\"testCode\":\"SU\",\"testLocation\":null,\"testPoint\":null,\"measurementType\":null,\"faultDirection\":null,\"faultLoop\":null,\"faultAction\":null,\"faultDesc\":null,\"testCodeDescription\":\"TEST EQUIPMENT BUSY;COULD NOT ACCESS;SIMULTANEOUS TEST REQUEST MADE\"},\"curCmd\":\"QUICKX\",\"resultSummary\":null,\"dcount\":1,\"gmodemStats\":null},\"sessionInfo\":null}");
		
		Mdata mdata=new Mdata();
		mdata = new ObjectMapper().readValue("{\"testRsp\":{\"hdr\":{\"testSysType\":\"MLT\",\"testRqstrId\":\"requesterID\",\"testSysId\":\"LM50\",\"echo1\":null,\"echo2\":null,\"echo3\":null,\"echo4\":null,\"echo5\":null,\"echo6\":null,\"dataChanObjRef\":\"1701957280.29\",\"callbackObjRef\":null,\"apiversion\":\"2.13\",\"startTime\":0,\"startTranTime\":0},\"listId\":\"listId\",\"tid\":null,\"testby\":null,\"objectId\":\"objectId\",\"language\":null,\"key\":{\"choice\":1,\"unbundleId\":null,\"exk\":null,\"oeid\":null,\"cid\":{\"cktfmt\":80,\"fmtP\":{\"npa\":\"406\",\"nnx\":\"468\",\"line\":\"2603\",\"cktid\":null,\"char1\":0,\"char4\":null,\"dpa\":null,\"lti\":null}}},\"dslamId\":null,\"drack\":null,\"dshelf\":null,\"dslot\":null,\"dline\":null,\"statusCount\":0,\"ipAddress\":null,\"dslamType\":0,\"thType\":0,\"cltmSlot\":0,\"cltAccMode\":0,\"ddslbusy\":0,\"sessionStat\":0,\"finalFlag\":89,\"line24Ind\":1,\"checkRip\":null,\"lnattr\":null,\"resultsModeReported\":\"DR\",\"testTime\":\"20231207135448+0000\",\"testOutcome\":0,\"enhNWGUrl\":null,\"errorCode\":null,\"errorDesc\":null,\"status\":null,\"tags\":null,\"hazPot\":null,\"nocweb\":null,\"bsoReq\":null,\"modemStats\":null,\"a2ModemStats\":null,\"errorStats\":null,\"rawStats\":null,\"pvcStats\":null,\"batgraph\":null,\"requesterStats\":null,\"addressStats\":null,\"routeStats\":null,\"pppoaStats\":null,\"pppoeStats\":null,\"pingStats\":null,\"http\":null,\"tputStats\":null,\"traceRoute\":null,\"htputStats\":null,\"ipoaServer\":null,\"pppoaServer\":null,\"pppoeServer\":null,\"cipStats\":null,\"cicmpStats\":null,\"cudpStats\":null,\"ctcpStats\":null,\"cinfoStats\":null,\"voip\":null,\"syncStatus\":0,\"testBufferId\":null,\"bsoprofile\":null,\"fttpdigLte\":null,\"snmpdata\":null,\"testResultsURL\":null,\"faultLocator\":null,\"d\":{\"item\":[{\"choice\":3,\"potsResults1\":{\"ringer\":{\"choice\":1,\"ringers\":{\"ringersTr\":1,\"ringersTg\":0,\"ringersRg\":0},\"therm\":null},\"balanceC\":\"10\"},\"digResults1\":null,\"detailedNarrative\":null,\"potsResults2\":{\"soakRes\":null,\"loc12Res\":null,\"coinRes\":null,\"infoRes\":null,\"coilRes\":null,\"wideband\":null,\"stiRes\":null,\"nzr\":null,\"mtelRes\":null,\"enhqualRes\":null,\"bponInfoRes\":null,\"textresCount\":0,\"textRes\":null,\"dispCount\":0,\"dispResults\":{\"item\":[{\"loopLength\":\"10\"}]},\"line24\":\"COULD NOT GAIN ACCESS\"},\"digResults2\":null},null,null,null,null,null,null,null,null,null]},\"f\":{\"testCodePrefix\":null,\"testCode\":\"SU\",\"testLocation\":null,\"testPoint\":null,\"measurementType\":null,\"faultDirection\":null,\"faultLoop\":null,\"faultAction\":null,\"faultDesc\":null,\"testCodeDescription\":\"TEST EQUIPMENT BUSY;COULD NOT ACCESS;SIMULTANEOUS TEST REQUEST MADE\"},\"curCmd\":\"QUICKX\",\"resultSummary\":null,\"dcount\":1,\"gmodemStats\":null},\"sessionInfo\":null}",Mdata.class);
			
		StringBuilder response = mltPagerText.getTnText(mdata);
		
		assertEquals(pageText, response.toString());
	}
	
	
	@Test
	void getTestDescriptionTextTest() {
		
		String testResult="SWINGING TROUBLE;HARD GROUND";
		String expectedResult="|SWINGING TROUBLE";
		
		String response = mltPagerText.getTestDescriptionText(testResult);
		
		assertEquals(expectedResult, response);
		
	}
	

}
