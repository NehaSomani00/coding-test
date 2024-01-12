package com.lumen.fastivr.IVRCANST.helper;

import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_1;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCANST.Dto.AssignServiceOrderRequest;
import com.lumen.fastivr.IVRCANST.Dto.AssignServiceOrderRequestInputData;
import com.lumen.fastivr.IVRCANST.Dto.ChangeLoopAssignmentRequest;
import com.lumen.fastivr.IVRCANST.Dto.OrderStatusInputData;
import com.lumen.fastivr.IVRCANST.Dto.OrderStatusRequest;
import com.lumen.fastivr.IVRCANST.Dto.UpdateLoopRequestDto;
import com.lumen.fastivr.IVRCANST.Dto.UpdateLoopRequestInputData;
import com.lumen.fastivr.IVRCANST.entity.IVRCanstEntity;
import com.lumen.fastivr.IVRCANST.repository.IVRCanstCacheService;
import com.lumen.fastivr.IVRCNF.Dto.ChangeLoopAssignmentReqInputDataCurrentLoopDetails;
import com.lumen.fastivr.IVRCNF.Dto.ChangeLoopAssignmentReqInputDataReplacementLoopDetails;
import com.lumen.fastivr.IVRCNF.Dto.ChangeLoopAssignmentRequestDto;
import com.lumen.fastivr.IVRCNF.Dto.ChangeLoopAssignmentRequestInputData;
import com.lumen.fastivr.IVRCNF.Dto.InputData;
import com.lumen.fastivr.IVRCNF.helper.IVRCnfHelper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.AuthorizationInfo;
import com.lumen.fastivr.IVRDto.CurrentAssignmentInfo;
import com.lumen.fastivr.IVRDto.CurrentAssignmentRequestTnDto;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.LOOP;
import com.lumen.fastivr.IVRDto.MessageStatus;
import com.lumen.fastivr.IVRDto.ReturnDataSet;
import com.lumen.fastivr.IVRDto.SEG;
import com.lumen.fastivr.IVRDto.SO;
import com.lumen.fastivr.IVRDto.TN;
import com.lumen.fastivr.IVRDto.TargetSchemaVersionUsed;
import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRSignon.IVRSignOnServiceHelper;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.IVRUtils.IVRLfacsConstants;
import com.lumen.fastivr.httpclient.IVRHttpClient;

@ExtendWith(MockitoExtension.class)
public class IVRCanstHelperTest {
	
	@Mock
	private ObjectMapper mockObjectMapper;

	@Mock
	private IVRCacheService mockCacheService;

	@Mock
	private IVRCanstCacheService mockivrCanstCacheService ;

	@Mock
	private IVRLfacsServiceHelper mockIVRLfacsServiceHelper;

	@Mock
	private IVRSignOnServiceHelper mockIvrSignOnServiceHelper;

	@Mock
	IVRHttpClient mockIvrHttpClient;

	@InjectMocks
	private IVRCanstHelper ivrCanstHelper;
	
	@Mock
	private IVRCnfHelper ivrCnfHelper;

	@Mock
	private InputData inputData;
	
	CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
	
	String currentAssignment = null;

	ReturnDataSet returnDataSet = new ReturnDataSet();
	SO so = new SO();
	List<SO> soList = new ArrayList<>();
	LOOP loop = new LOOP();
	List<LOOP> loopList = new ArrayList<>();
	
	@BeforeEach
	void setUp() throws Exception {
		
		Field fieldLmos = IVRCanstHelper.class.getDeclaredField("inputCodes");
		fieldLmos.setAccessible(true);
		Map<String, String> map = new HashMap();
		map.put("11", "-");
		map.put("12", ".");
		map.put("13", "+");
		map.put("02", "&");
		map.put("21", "A");
		map.put("22", "B");
		map.put("23", "C");
		map.put("31", "D");
		map.put("32", "E");
		map.put("33", "F");
		map.put("41", "G");
		map.put("42", "H");
		map.put("43", "I");
		map.put("51", "J");
		map.put("52", "K");
		map.put("53", "L");
		map.put("61", "M");
		map.put("62", "N");
		map.put("63", "O");
		map.put("71", "P");
		map.put("01", "Q");
		map.put("72", "R");
		map.put("73", "S");
		map.put("81", "T");
		map.put("82", "U");
		map.put("83", "V");
		map.put("91", "W");
		map.put("92", "X");
		map.put("93", "Y");
		map.put("03", "Z");
		fieldLmos.set(ivrCanstHelper, map);

		currentAssignmentResponseDto = new CurrentAssignmentResponseDto();
		ReturnDataSet dataSet = new ReturnDataSet();
		List<LOOP> loopList = new ArrayList<LOOP>();
		LOOP loop = new LOOP();

		List<SEG> segList = new ArrayList<SEG>();

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
		
		currentAssignment = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":[{\"UTYP\":\"10151\",\"UID\":\"QUINCE ST NW\",\"STYP\":\"COON RPDS\",\"SID\":\"MN\",\"ETYP\":\"MN\",\"EID\":\"MN\"}],\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"ONE1\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"R,43\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"BK,324\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}";
		
		currentAssignmentResponseDto = new ObjectMapper().readValue(currentAssignment,
				CurrentAssignmentResponseDto.class);		
	}
	
	@Test
	void testAssignServiceOrderRequestRequest() throws JsonMappingException, JsonProcessingException {
		
		String sessionid = "session123";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");

		List<SEG> segList = new ArrayList<SEG>();
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
		returnDataSet.setLoop(loopList);
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionid);
		userSession.setCurrentAssignmentResponse("Test");

		IVRCanstEntity canstSession = new IVRCanstEntity();
		canstSession.setSessionId(sessionid);
		canstSession.setSegmentRead("F1");
		canstSession.setCable("1");
		canstSession.setPair("3");

		AssignServiceOrderRequest request = new AssignServiceOrderRequest();

		CurrentAssignmentInfo currentAssignmentInfo = new CurrentAssignmentInfo();

		TN TN = new TN();

		TN.setCkid("12345678");

		currentAssignmentInfo.setTn(TN);

		AssignServiceOrderRequestInputData id = new AssignServiceOrderRequestInputData();

		id.setLFACSEmployeeCode("123");
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa("123");
		wireCtrPrimaryNPANXX.setNxx("321");
		id.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);

		id.setServiceOrderNumber(
				currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getORD());
		id.setCircuitId(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID());
		id.setTerminalId(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getTID());
		request.setInputData(id);

//		request.setRequestId("FASTFAST");
//		request.setWebServiceName("SIABusService");
//		request.setRequestPurpose("TS");
//		AuthorizationInfo authInfo = new AuthorizationInfo();
//		authInfo.setUserid("fasfast");
//		authInfo.setPassword("9312qrty!");
//		request.setAuthorizationInfo(authInfo);
//		TargetSchemaVersionUsed schema = new TargetSchemaVersionUsed();
//		schema.setMajorVersionNumber(0);
//		schema.setMinorVersionNumber(0);
//		request.setTargetSchemaVersionUsed(schema);
//		request.setTimeOutSecond(180);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		AssignServiceOrderRequest actualResponse = ivrCanstHelper.buildAssignServiceOrderRequest("12345678", "123",
				"321", "123", canstSession.getSessionId(), canstSession, userSession);

		assertEquals("123", actualResponse.getInputData().getLFACSEmployeeCode());
		assertEquals("DPAAB", actualResponse.getInputData().getTerminalId());
		
	}
	
	@Test
	void testFindFastivrError() {
		
		String sessionid = "session123";
		String nextState = "nextState";
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionid);
		
		MessageStatus messageStatus = new MessageStatus();
		
		messageStatus.setErrorCode(IVRLfacsConstants.ERR_500);
		
		IVRWebHookResponseDto actualResponse = ivrCanstHelper.findFastivrError(sessionid, nextState, messageStatus);
		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
		assertEquals(IVRLfacsConstants.SYSTEM_DOWN_ERR, actualResponse.getHookReturnMessage());
		
		messageStatus.setErrorCode(IVRLfacsConstants.ERR_504);
		IVRWebHookResponseDto actualResponse1 = ivrCanstHelper.findFastivrError(sessionid, nextState, messageStatus);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse1.getHookReturnCode());
		assertEquals(IVRLfacsConstants.NOT_RESPONDING_ERR, actualResponse1.getHookReturnMessage());
		
		messageStatus.setErrorCode(IVRLfacsConstants.ERR_503);
		IVRWebHookResponseDto actualResponse2 = ivrCanstHelper.findFastivrError(sessionid, nextState, messageStatus);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse2.getHookReturnCode());
		assertEquals(IVRLfacsConstants.NOT_AVAILABLE_ERR, actualResponse2.getHookReturnMessage());	
		
		messageStatus.setErrorCode(IVRLfacsConstants.ERR_400);
		IVRWebHookResponseDto actualResponse3 = ivrCanstHelper.findFastivrError(sessionid, nextState, messageStatus);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_4, actualResponse3.getHookReturnCode());
		assertEquals(IVRLfacsConstants.TRANSACTION_FAILED_ERR, actualResponse3.getHookReturnMessage());
		
		//FASTIVR_BACKEND_ERR
		//IVRWebHookResponseDto actualResponse4 = ivrCanstHelper.findFastivrError(sessionid, nextState, messageStatus);
		//assertEquals(FASTIVR_BACKEND_ERR, actualResponse4.getHookReturnCode());	
	}
	
	@Test
	void testGetSegmentList() throws JsonMappingException, JsonProcessingException {
		
		int expectedList = ivrCanstHelper.getSegmentList(currentAssignmentResponseDto);
		assertNotNull(expectedList);	
	}
	
	@Test
	void testIsDPA() throws JsonMappingException, JsonProcessingException {
		
		boolean actual = false;
		boolean expected = ivrCanstHelper.isDPA(currentAssignmentResponseDto);
		assertEquals(actual, expected);
	}
	
	@Test
	void testIsCKID() throws JsonMappingException, JsonProcessingException {
	
		boolean actual = true;
		boolean expected = ivrCanstHelper.isCKID(currentAssignmentResponseDto);
		assertEquals(actual, expected);
		
		currentAssignment = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":[{\"UTYP\":\"10151\",\"UID\":\"QUINCE ST NW\",\"STYP\":\"COON RPDS\",\"SID\":\"MN\",\"ETYP\":\"MN\",\"EID\":\"MN\"}],\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"ONE1\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"R,43\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"BK,324\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}";
		
		currentAssignmentResponseDto = new ObjectMapper().readValue(currentAssignment,
				CurrentAssignmentResponseDto.class);		
		
		boolean actual1 = false;
		boolean expected1 = ivrCanstHelper.isCKID(currentAssignmentResponseDto);
		assertEquals(actual1, expected1);
	}	
	
	@Test
	void testGetServiceOrder() throws JsonMappingException, JsonProcessingException {
		
		String actualServiceOrder = null;
		String expectedServiceOrder = ivrCanstHelper.getSeviceOrder(currentAssignmentResponseDto);
		assertEquals(actualServiceOrder, expectedServiceOrder);		
	}		
	
	@Test
	void testGetCKID() throws JsonMappingException, JsonProcessingException {
		
		
		String actual = "763 757-4229";
		String expected = ivrCanstHelper.getCKID(currentAssignmentResponseDto);
		assertEquals(actual, expected);

		currentAssignment = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":[{\"UTYP\":\"10151\",\"UID\":\"QUINCE ST NW\",\"STYP\":\"COON RPDS\",\"SID\":\"MN\",\"ETYP\":\"MN\",\"EID\":\"MN\"}],\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"ONE1\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"R,43\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"BK,324\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}";
		
		currentAssignmentResponseDto = new ObjectMapper().readValue(currentAssignment,
				CurrentAssignmentResponseDto.class);		
		
		String expected1 = ivrCanstHelper.getCKID(currentAssignmentResponseDto);
		assertEquals(null, expected1);
	}
	
	@Test
	void testBuildChangeLoopAssignmentRequestValid() throws JsonMappingException, JsonProcessingException {

		String sessionid = "session123";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");

		List<SEG> segList = new ArrayList<SEG>();
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
		returnDataSet.setLoop(loopList);
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionid);
		userSession.setCurrentAssignmentResponse("Test");
		userSession.setEmpID("123");
		
		IVRCanstEntity ivrCanstEntity = new IVRCanstEntity();
		ivrCanstEntity.setSessionId(sessionid);
		ivrCanstEntity.setSegmentRead("F1");
		ivrCanstEntity.setCable("1");
		ivrCanstEntity.setPair("3");

		ChangeLoopAssignmentRequestDto request = new ChangeLoopAssignmentRequestDto();
		ChangeLoopAssignmentRequestInputData id = new ChangeLoopAssignmentRequestInputData();

		id.setLFACSEmployeeCode("123");
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa("123");
		wireCtrPrimaryNPANXX.setNxx("321");
		
		id.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		id.setServiceOrderNumber(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getORD());
		id.setCircuitId(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID());
		id.setSegNumber("1");
	
		ChangeLoopAssignmentReqInputDataCurrentLoopDetails currentLoopDetails = new ChangeLoopAssignmentReqInputDataCurrentLoopDetails();
		currentLoopDetails.setCableId(ivrCanstEntity.getCable());
		currentLoopDetails.setCableUnitId(ivrCanstEntity.getPair());
		id.setChangeLoopAssignmentReqInputDataCurrentLoopDetails(currentLoopDetails);
		
		ChangeLoopAssignmentReqInputDataReplacementLoopDetails replacementLoopDetails = new ChangeLoopAssignmentReqInputDataReplacementLoopDetails();
		replacementLoopDetails.setCableId(ivrCanstEntity.getCable());
		replacementLoopDetails.setCableUnitId(ivrCanstEntity.getPair());
		id.setChangeLoopAssignmentReqInputDataReplacementLoopDetails(replacementLoopDetails);
		
		id.setFacilityChangeReasonCode("NDC");
		id.setChangeActionCode("CUTFAINSTREP");
		request.setInputData(id);

//		request.setRequestId("FASTFAST");
//		request.setWebServiceName("SIABusService");
//		request.setRequestPurpose("TS");
//		AuthorizationInfo authInfo = new AuthorizationInfo();
//		authInfo.setUserid("fasfast");
//		authInfo.setPassword("9312qrty!");
//		request.setAuthorizationInfo(authInfo);
//		TargetSchemaVersionUsed schema = new TargetSchemaVersionUsed();
//		schema.setMajorVersionNumber(0);
//		schema.setMinorVersionNumber(0);
//		request.setTargetSchemaVersionUsed(schema);
//		request.setTimeOutSecond(180);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class)).thenReturn(currentAssignmentResponseDto);
		when(ivrCnfHelper.extracted(currentAssignmentResponseDto)).thenReturn(segList);
		
		ChangeLoopAssignmentRequest actualResponse = ivrCanstHelper.buildChangeLoopAssignmentRequest("12345678", "123","321", ivrCanstEntity.getSessionId(), ivrCanstEntity, userSession);

		assertEquals("123", actualResponse.getInputData().getLFACSEmployeeCode());
	}

	@Test
	void testGetColorCodeWhenSegmentReadF1() {

		String sessionid = "session123";
		List<LOOP> loopList = new ArrayList<>();
		LOOP loop = new LOOP();
		List<SEG> segList = new ArrayList<>();

		SEG seg = new SEG();
		seg.setCA("3274374");
		seg.setBP("Bp");
		seg.setTP("Tp");
		segList.add(seg);
		loop.setSEG(segList);
		loopList.add(loop);
		ReturnDataSet returnDataSet = new ReturnDataSet();
		returnDataSet.setLoop(loopList);
		CurrentAssignmentResponseDto responseDto = new CurrentAssignmentResponseDto();
		responseDto.setReturnDataSet(returnDataSet);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionid);
		response.setHookReturnCode(HOOK_RETURN_1);

		IVRCanstEntity ivrCanstEntity = new IVRCanstEntity();
		ivrCanstEntity.setSessionId(sessionid);
		ivrCanstEntity.setSegmentRead("F1");


		when(mockIVRLfacsServiceHelper.checkColourCode(any(), any(), any())).thenReturn(response);

		IVRWebHookResponseDto actualResponse = ivrCanstHelper.getColourCode(response, responseDto, ivrCanstEntity);
		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());


	}

	@Test
	void testGetColorCodeWhenSegmentReadF2() {

		String sessionid = "session123";
		List<LOOP> loopList = new ArrayList<>();
		LOOP loop = new LOOP();
		List<SEG> segList = new ArrayList<>();

		SEG seg = new SEG();
		SEG seg1 = new SEG();
		seg.setCA("3274374");
		seg.setBP("Bp");
		seg.setTP("Tp");
		seg1.setCA("3274374");
		seg1.setBP("Bp");
		seg1.setTP("Tp");
		segList.add(seg);
		segList.add(seg1);
		loop.setSEG(segList);
		loopList.add(loop);
		ReturnDataSet returnDataSet = new ReturnDataSet();
		returnDataSet.setLoop(loopList);
		CurrentAssignmentResponseDto responseDto = new CurrentAssignmentResponseDto();
		responseDto.setReturnDataSet(returnDataSet);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionid);
		response.setHookReturnCode(HOOK_RETURN_1);

		IVRCanstEntity ivrCanstEntity = new IVRCanstEntity();
		ivrCanstEntity.setSessionId(sessionid);
		ivrCanstEntity.setSegmentRead("F2");

		when(mockIVRLfacsServiceHelper.checkColourCode(any(), any(), any())).thenReturn(response);

		IVRWebHookResponseDto actualResponse = ivrCanstHelper.getColourCode(response, responseDto, ivrCanstEntity);
		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());


	}

	@Test
	void testGetColorCodeWhenSegmentReadF3() {

		String sessionid = "session123";
		List<LOOP> loopList = new ArrayList<>();
		LOOP loop = new LOOP();
		List<SEG> segList = new ArrayList<>();

		SEG seg = new SEG();
		SEG seg1 = new SEG();
		SEG seg2 = new SEG();
		seg.setCA("3274374");
		seg.setBP("Bp");
		seg.setTP("Tp");
		seg1.setCA("3274374");
		seg1.setBP("Bp");
		seg1.setTP("Tp");
		seg2.setCA("3274374");
		seg2.setBP("Bp");
		seg2.setTP("Tp");
		segList.add(seg);
		segList.add(seg1);
		segList.add(seg2);
		loop.setSEG(segList);
		loopList.add(loop);

		ReturnDataSet returnDataSet = new ReturnDataSet();
		returnDataSet.setLoop(loopList);
		CurrentAssignmentResponseDto responseDto = new CurrentAssignmentResponseDto();
		responseDto.setReturnDataSet(returnDataSet);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionid);
		response.setHookReturnCode(HOOK_RETURN_1);

		IVRCanstEntity ivrCanstEntity = new IVRCanstEntity();
		ivrCanstEntity.setSessionId(sessionid);
		ivrCanstEntity.setSegmentRead("F3");

		when(mockIVRLfacsServiceHelper.checkColourCode(any(), any(), any())).thenReturn(response);

		IVRWebHookResponseDto actualResponse = ivrCanstHelper.getColourCode(response, responseDto, ivrCanstEntity);
		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());


	}

	@Test
	void testGetColorCodeWhenCurrentAssignmentNull() {

		String sessionid = "session123";
		List<LOOP> loopList = new ArrayList<>();
		LOOP loop = new LOOP();
		List<SEG> segList = new ArrayList<>();

		SEG seg = new SEG();
		seg.setCA("3274374");
		seg.setBP("Bp");
		seg.setTP("Tp");
		segList.add(seg);
		loop.setSEG(segList);
		loopList.add(loop);
		ReturnDataSet returnDataSet = new ReturnDataSet();
		returnDataSet.setLoop(loopList);
		CurrentAssignmentResponseDto responseDto = null;


		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionid);
		response.setHookReturnCode(HOOK_RETURN);

		IVRCanstEntity ivrCanstEntity = new IVRCanstEntity();
		ivrCanstEntity.setSessionId(sessionid);
		ivrCanstEntity.setSegmentRead("F1");


		IVRWebHookResponseDto actualResponse = ivrCanstHelper.getColourCode(response, responseDto, ivrCanstEntity);
		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());


	}

	@Test
	void testBuildChangeLoopAssignmentRequest() throws JsonMappingException, JsonProcessingException {

		String sessionid = "session123";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");

		List<SEG> segList = new ArrayList<SEG>();
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
		returnDataSet.setLoop(loopList);
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionid);
		userSession.setCurrentAssignmentResponse("Test");

		IVRCanstEntity canstSession = new IVRCanstEntity();
		canstSession.setSessionId(sessionid);
		canstSession.setSegmentRead("F1");
		canstSession.setCable("1");
		canstSession.setPair("3");

		ChangeLoopAssignmentRequestDto request = new ChangeLoopAssignmentRequestDto();

		CurrentAssignmentInfo currentAssignmentInfo = new CurrentAssignmentInfo();

		TN TN = new TN();

		TN.setCkid("12345678");

		currentAssignmentInfo.setTn(TN);

		ChangeLoopAssignmentRequestInputData id = new ChangeLoopAssignmentRequestInputData();

		id.setLFACSEmployeeCode("123");
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa("123");
		wireCtrPrimaryNPANXX.setNxx("321");
		id.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);

		id.setServiceOrderNumber(
				currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getORD());
		id.setCircuitId(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID());

		id.setSegNumber("1");
		request.setInputData(id);

		ChangeLoopAssignmentReqInputDataCurrentLoopDetails currentLoopDetails = new ChangeLoopAssignmentReqInputDataCurrentLoopDetails();
		currentLoopDetails.setCableId(canstSession.getCable());
		currentLoopDetails.setCableUnitId(canstSession.getPair());
		id.setChangeLoopAssignmentReqInputDataCurrentLoopDetails(currentLoopDetails);
		request.setInputData(id);

		ChangeLoopAssignmentReqInputDataReplacementLoopDetails replacementLoopDetails = new ChangeLoopAssignmentReqInputDataReplacementLoopDetails();
		replacementLoopDetails.setCableId(canstSession.getCable());
		replacementLoopDetails.setCableUnitId(canstSession.getPair());
		id.setChangeLoopAssignmentReqInputDataReplacementLoopDetails(replacementLoopDetails);
		request.setInputData(id);

		id.setFacilityChangeReasonCode("NDC");
		id.setChangeActionCode("CUTFAINSTREP");
		request.setInputData(id);

//		request.setRequestId("FASTFAST");
//		request.setWebServiceName("SIABusService");
//		request.setRequestPurpose("TS");
//		AuthorizationInfo authInfo = new AuthorizationInfo();
//		authInfo.setUserid("fasfast");
//		authInfo.setPassword("9312qrty!");
//		request.setAuthorizationInfo(authInfo);
//		TargetSchemaVersionUsed schema = new TargetSchemaVersionUsed();
//		schema.setMajorVersionNumber(0);
//		schema.setMinorVersionNumber(0);
//		request.setTargetSchemaVersionUsed(schema);
//		request.setTimeOutSecond(180);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);
		
		when(ivrCnfHelper.extracted(currentAssignmentResponseDto)).thenReturn(segList);

		ChangeLoopAssignmentRequest actualResponse = ivrCanstHelper.buildChangeLoopAssignmentRequest("12345678", "123", "321", sessionid, canstSession, userSession);

		assertNotNull(actualResponse);
	}
	
	@Test
	void testBuildCurrentAssignmentInqRequest() throws JsonMappingException, JsonProcessingException {

		String sessionid = "session123";
		
		List<String> userInputDTMFList = new ArrayList<String>();
		userInputDTMFList.add("2");
		userInputDTMFList.add("1");		
		
		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionid);
		userSession.setCurrentAssignmentResponse("Test");

		IVRCanstEntity canstSession = new IVRCanstEntity();
		canstSession.setSessionId(sessionid);
		canstSession.setSegmentRead("F1");
		canstSession.setCable("1");
		canstSession.setPair("3");
		
		CurrentAssignmentRequestTnDto request = new CurrentAssignmentRequestTnDto();
		
		CurrentAssignmentInfo currentAssignmentInfo = new CurrentAssignmentInfo();
		TN TN = new TN();
		TN.setCkid("12345678");
		currentAssignmentInfo.setTn(TN);
		
		InputData id = new InputData();
		//id.setCurrentAssignmentInfo(currentAssignmentInfo);
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa("");
		wireCtrPrimaryNPANXX.setNxx("");
		id.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		//request.setInputData(id);

//		request.setRequestId("FASTFAST");
//		request.setWebServiceName("SIABusService");
//		request.setRequestPurpose("TS");
//		AuthorizationInfo authInfo = new AuthorizationInfo();
//		authInfo.setUserid("fasfast");
//		authInfo.setPassword("9312qrty!");
//		request.setAuthorizationInfo(authInfo);
//		TargetSchemaVersionUsed schema = new TargetSchemaVersionUsed();
//		schema.setMajorVersionNumber(0);
//		schema.setMinorVersionNumber(0);
//		request.setTargetSchemaVersionUsed(schema);
//		request.setTimeOutSecond(180);

		CurrentAssignmentRequestTnDto actualResponse = ivrCanstHelper.buildCurrentAssignmentInqRequest("123", "321", userInputDTMFList, userSession);		

		assertNotNull(actualResponse);
		
	}

	@Test
	void testGetServingTerminal() throws JsonMappingException, JsonProcessingException {
		
		String actualServiceOrder = "X 10000 PALM ST NW";
		String expectedServiceOrder = ivrCanstHelper.getServingTerminal(currentAssignmentResponseDto, 1);
		assertEquals(actualServiceOrder, expectedServiceOrder);		
	}
	
	@Test
	void testIsTEA() throws JsonMappingException, JsonProcessingException {
		
		boolean actualServiceOrder = true;
		boolean expectedServiceOrder = ivrCanstHelper.isTEA(currentAssignmentResponseDto);
		assertEquals(actualServiceOrder, expectedServiceOrder);		
	}
	
	@Test
	void testBuildOrderStatusRequest() throws JsonMappingException, JsonProcessingException {

		String sessionid = "session123";
		
		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionid);
		userSession.setCurrentAssignmentResponse("Test");
		
		IVRCanstEntity canstSession = new IVRCanstEntity();
		canstSession.setSessionId(sessionid);
		canstSession.setSegmentRead("F1");
		canstSession.setCable("1");
		canstSession.setPair("3");		
		
		OrderStatusRequest request = new OrderStatusRequest();

		OrderStatusInputData inputData = new OrderStatusInputData();
		
		InputData id = new InputData();
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa("");
		wireCtrPrimaryNPANXX.setNxx("");
		id.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);

		inputData.setLFACSEmployeeCode("123");
		inputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		inputData.setServiceOrderNumber("");


		OrderStatusRequest actualResponse = ivrCanstHelper.buildOrderStatusRequest("123", "321", canstSession, userSession);		

		assertNotNull(actualResponse);
		
	}	

	@Test
	void testconvertInputCodesToAlphabets_Null() {

		assertNull(ivrCanstHelper.convertInputCodesToAlphabets("/*21/*"));
	}
	
	@Test
	void testconvertInputCodesToAlphabets() {
		
		assertNull(ivrCanstHelper.convertInputCodesToAlphabets("21*22*23*D*E"));
	}	

	@Test
	void testGetSegmentNumber() throws JsonMappingException, JsonProcessingException {
		
		String sessionId = "session123";
		
		IVRCanstEntity canstSession = new IVRCanstEntity();
		canstSession.setSessionId(sessionId);
		canstSession.setSegmentRead("F1");
		
		int expected = ivrCanstHelper.getSegmentNumber(canstSession);
		assertEquals(0, expected);	
		
		canstSession.setSegmentRead("F2");
		int expected1 = ivrCanstHelper.getSegmentNumber(canstSession);
		assertEquals(1, expected1);	
		
		canstSession.setSegmentRead("F3");
		int expected2 = ivrCanstHelper.getSegmentNumber(canstSession);
		assertEquals(2, expected2);	
	}
	
	@Test
	void testGenerateCableTroubleTicket() {
		
		String  expected  = ivrCanstHelper.generateCableTroubleTicket("ec");
		assertNotNull(expected);
	}
	
	@Test
	void testBuildUpdateLoopAssignmentRequest() throws JsonMappingException, JsonProcessingException {

		String sessionid = "session123";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");

		List<SEG> segList = new ArrayList<SEG>();
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
		returnDataSet.setLoop(loopList);
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionid);
		userSession.setCurrentAssignmentResponse("Test");

		IVRCanstEntity canstSession = new IVRCanstEntity();
		canstSession.setSessionId(sessionid);
		canstSession.setSegmentRead("F1");
		canstSession.setCable("1");
		canstSession.setPair("3");

		UpdateLoopRequestDto request = new UpdateLoopRequestDto();

		CurrentAssignmentInfo currentAssignmentInfo = new CurrentAssignmentInfo();

		TN TN = new TN();

		TN.setCkid("12345678");

		currentAssignmentInfo.setTn(TN);

		UpdateLoopRequestInputData id = new UpdateLoopRequestInputData();

		id.setLFACSEmployeeCode("123");
		id.setCircuitId(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID());
		id.setNewTerminalAddress("123");
		id.setCurrentTerminalAddress("");
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa("123");
		wireCtrPrimaryNPANXX.setNxx("321");
		id.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);			
		id.setCircuitId("");

		ChangeLoopAssignmentReqInputDataReplacementLoopDetails replacementLoopDetails = new ChangeLoopAssignmentReqInputDataReplacementLoopDetails();
		replacementLoopDetails.setCableId(canstSession.getCable());
		replacementLoopDetails.setCableUnitId(canstSession.getPair());
		
		request.setInputData(id);
		request.setRequestId("FASTFAST");
		request.setWebServiceName("SIABusService");
		request.setRequestPurpose("TS");
		AuthorizationInfo authInfo = new AuthorizationInfo();
		authInfo.setUserid("fasfast");
		authInfo.setPassword("9312qrty!");
		request.setAuthorizationInfo(authInfo);
		TargetSchemaVersionUsed schema = new TargetSchemaVersionUsed();
		schema.setMajorVersionNumber(0);
		schema.setMinorVersionNumber(0);
		request.setTargetSchemaVersionUsed(schema);
		request.setTimeOutSecond(180);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);
		
		//when(ivrCnfHelper.extracted(currentAssignmentResponseDto)).thenReturn(segList);

		UpdateLoopRequestDto actualResponse = ivrCanstHelper.buildUpdateLoopAssignmentRequest("12345678", "123", userSession, canstSession);

		assertNotNull(actualResponse);
	}
	
	
}			



