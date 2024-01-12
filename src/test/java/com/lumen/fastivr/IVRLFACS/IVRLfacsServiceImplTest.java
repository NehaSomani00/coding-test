/**
 * 
 */
package com.lumen.fastivr.IVRLFACS;

import static com.lumen.fastivr.IVRUtils.IVRConstants.CANNOT_FETCH_CURR_ASSG_API_LOSDB_NULL;
import static com.lumen.fastivr.IVRUtils.IVRConstants.GPDOWN_ERR_MSG;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INVALID_SESSION_ID;
import static com.lumen.fastivr.IVRUtils.IVRConstants.STATE_FID011;
import static com.lumen.fastivr.IVRUtils.IVRConstants.VALID_TN_MSG;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_0;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_1;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_2;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_3;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_4;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_7;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRCacheManagement.IVRSessionRedisInterface;
import com.lumen.fastivr.IVRDto.ADDR;
import com.lumen.fastivr.IVRDto.BADR;
import com.lumen.fastivr.IVRDto.CurrentAssignmentRequestTnDto;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.ErrorList;
import com.lumen.fastivr.IVRDto.HostErrorList;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.LOOP;
import com.lumen.fastivr.IVRDto.MessageStatus;
import com.lumen.fastivr.IVRDto.ReturnDataSet;
import com.lumen.fastivr.IVRDto.SEG;
import com.lumen.fastivr.IVRDto.SO;
import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;
import com.lumen.fastivr.IVRDto.LOSDB.CTelephone;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.CandidatePairInfo;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetMaintChangeTicketReturnDataSet;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketRequest;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketResponse;
import com.lumen.fastivr.IVRDto.additionalLines.AdditionalLinesReportResponseDto;
import com.lumen.fastivr.IVRDto.coe.CentralOfficeEquipmentInputData;
import com.lumen.fastivr.IVRDto.coe.CentralOfficeEquipmentRequestDto;
import com.lumen.fastivr.IVRDto.coe.CentralOfficeEquipmentResponseDto;
import com.lumen.fastivr.IVRDto.common.IVRHttpResponseDto;
import com.lumen.fastivr.IVRDto.multipleappearance.MultipleAppearanceResponseDto;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.LoopAssignCandidatePairInfo;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetLoopAssigReturnDataSet;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentRequest;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentResponse;
import com.lumen.fastivr.IVREntity.TNInfo;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.AdditionalLinesReportUtils;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.IVRUtils.IVRLfacsConstants;
import com.lumen.fastivr.httpclient.IVRHttpClient;

/**
 * 
 */
@ExtendWith(MockitoExtension.class)
public class IVRLfacsServiceImplTest {

	@Mock
	private IVRCacheService mockCacheService;

	@Mock
	private IVRLfacsServiceHelper mockIVRLfacsServiceHelper;

	@Mock
	private ObjectMapper mockObjectMapper;

	@Mock
	private LfacsValidation mockLfacsTNValidation;

	@Mock
	private IVRLfacsPagerTextFormation mockIVRLfacsPagerText;

	@InjectMocks
	private IVRLfacsServiceImpl ivrLfacsServiceImpl;

	@Mock
	AdditionalLinesReportUtils mockAdditionalLinesReportUtils;

	@Mock
	IVRSessionRedisInterface mockCache;
	@Mock
	HttpClient mockHttpClient;
	@Mock
	HttpResponse<String> mockHttpResponse;
	@Mock
	IVRHttpClient mockIvrHttpClient;

	MessageStatus messageStatus = new MessageStatus();
	HostErrorList hostErrorList = new HostErrorList();
	List<HostErrorList> HostErrorLists = new ArrayList<>();
	ErrorList errorList = new ErrorList();
	List<ErrorList> errorLists = new ArrayList<>();
	CurrentAssignmentResponseDto currentAssignmentResponse = new CurrentAssignmentResponseDto();
	SO so = new SO();
	List<SO> soList = new ArrayList<>();
	SEG seg = new SEG();
	List<SEG> segList = new ArrayList<>();
	LOOP loop = new LOOP();
	List<LOOP> loopList = new ArrayList<>();
	ReturnDataSet returnDataSet = new ReturnDataSet();
	IVRWebHookResponseDto ivrWebHookResponseDto = new IVRWebHookResponseDto();
	CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
	TNInfoResponse tnInfoResponse = null;
	String currentAssignment = null;

	CentralOfficeEquipmentResponseDto centralOfficeResponseDto = null;
	CentralOfficeEquipmentInputData coeInputData = null;
	CentralOfficeEquipmentRequestDto coeRequest = null;

	MultipleAppearanceResponseDto multipleAppearanceResponseDto = null;
	String multipleAppearance = null;

	String centralOffice = null;

	@BeforeEach
	void setUp() throws Exception {

		coeInputData = new CentralOfficeEquipmentInputData();
		coeRequest = new CentralOfficeEquipmentRequestDto();

		coeInputData.setCircuitId("12");

		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa("12");
		wireCtrPrimaryNPANXX.setNxx("324");
		coeInputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);

		coeRequest.setInputData(coeInputData);

//		coeRequest.setRequestId(IVRConstants.WRAPPER_API_REQUEST_ID);
//		coeRequest.setWebServiceName(IVRConstants.WRAPPER_API_WEB_SERVICE_NAME);
//		coeRequest.setRequestPurpose(IVRConstants.WRAPPER_API_REQUEST_PURPOSE);

//		AuthorizationInfo authInfo = new AuthorizationInfo();
//		authInfo.setUserid(IVRConstants.WrapperAuthorizationInfo.USERID);
//		authInfo.setPassword(IVRConstants.WrapperAuthorizationInfo.PASSWORD);
//		coeRequest.setAuthorizationInfo(authInfo);
//
//		TargetSchemaVersionUsed schema = new TargetSchemaVersionUsed();
//		schema.setMajorVersionNumber(0);
//		schema.setMinorVersionNumber(0);
//
//		coeRequest.setTargetSchemaVersionUsed(schema);
//		coeRequest.setTimeOutSecond(180);

		tnInfoResponse = new TNInfoResponse();
		TNInfo tnInfo = new TNInfo();
		String npaPrefix = "219";
		tnInfo.setNpaPrefix(npaPrefix);
		IVRUserSession session = new IVRUserSession();
		session.setSessionId("Session123");
		CTelephone telephone = new CTelephone();
		telephone.setNpa("123");
		telephone.setNxx("456");
		telephone.setLineNumber("7890");
		tnInfoResponse.setTn("23423");

		ReturnDataSet dataSet = new ReturnDataSet();
		List<LOOP> loopList = new ArrayList<LOOP>();
		LOOP loop = new LOOP();
		List<ADDR> addrList = new ArrayList<ADDR>();
		ADDR addr = new ADDR();
		List<BADR> BADR = new ArrayList<BADR>();

		currentAssignment = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":[{\"UTYP\":\"10151\",\"UID\":\"QUINCE ST NW\",\"STYP\":\"COON RPDS\",\"SID\":\"MN\",\"ETYP\":\"MN\",\"EID\":\"MN\"}],\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"ONE1\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"R,43\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"BK,324\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}";

		currentAssignmentResponseDto = new ObjectMapper().readValue(currentAssignment,
				CurrentAssignmentResponseDto.class);

		centralOffice = "{\"ReturnDataSet\":{\"SWITCHNetworkUnitId\":\"219-1-13-00\"},\"RequestId\":\"FASTFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\"},\"ARTISInformation\":{\"TotalTime\":\"2726\",\"OverheadTime\":\"2726\"},\"CompletedTimeStamp\":\"2023-10-26T05:42:39.025-05:00\",\"CompletedTimeStampSpecified\":true}";

		centralOfficeResponseDto = new ObjectMapper().readValue(centralOffice, CentralOfficeEquipmentResponseDto.class);

		multipleAppearance = "{\"ReturnDataSet\":{\"TerminalDetail\":[{\"TerminalAddress\":\"X 726 S NEBRASKA ST\",\"TerminalType\":\"FIXD\",\"CandidatePairStatus\":\"XC\",\"BindingPostColorCode\":\"131\"},{\"TerminalAddress\":\"36 RL14\",\"TerminalType\":\"RA\",\"CandidatePairStatus\":\"*\",\"BindingPostColorCode\":\"BL-W+R-BL\"},{\"TerminalAddress\":\"31 RL14\",\"TerminalType\":\"RA\",\"CandidatePairStatus\":\"*\",\"BindingPostColorCode\":\"BL-W+R-BL\"},{\"TerminalAddress\":\"40 RL14\",\"TerminalType\":\"RA\",\"CandidatePairStatus\":\"*\",\"BindingPostColorCode\":\"BL-W+R-BL\"},{}],\"AdditionalTerminalsFlag\":false},\"RequestId\":\"FASTFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\"},\"ARTISInformation\":{\"TotalTime\":\"1342\",\"OverheadTime\":\"1342\"},\"CompletedTimeStamp\":\"2023-11-02T09:54:16.52-05:00\",\"CompletedTimeStampSpecified\":true}";
		multipleAppearanceResponseDto = new ObjectMapper().readValue(multipleAppearance,
				MultipleAppearanceResponseDto.class);
	}

	@Test
	void processFID011_VALIDSESSION() {
		String sessionid = "session123";
		String userInput = "1234567";
		String currentState = STATE_FID011;
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		mockResponse.setHookReturnMessage(VALID_TN_MSG);
		mockResponse.setHookReturnCode(HOOK_RETURN_1);
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionid);
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
		when(mockLfacsTNValidation.validateFacsTN(userInput, mockSession)).thenReturn(mockResponse);

		IVRWebHookResponseDto response = ivrLfacsServiceImpl.processFID011(sessionid, currentState, userInput);
		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());
		assertEquals(VALID_TN_MSG, response.getHookReturnMessage());
	}

	@Test
	void processFID011_INVALIDSESSION() {
		String sessionid = "session123";
		String userInput = "1234567";
		String currentState = STATE_FID011;
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		mockResponse.setHookReturnMessage(INVALID_SESSION_ID);
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionid);
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(null);

		IVRWebHookResponseDto response = ivrLfacsServiceImpl.processFID011(sessionid, currentState, userInput);
		assertEquals(INVALID_SESSION_ID, response.getHookReturnMessage());
	}

	@Test
	void testProcessFID035Code_HookCode3() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID035";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setCanBePagedEmail(true);
		session.setCanBePagedMobile(true);

		session.setCurrentAssignmentResponse(currentAssignment);

		when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(session);

		when(mockIVRLfacsPagerText.getPageCurrentAssignment(Mockito.any(IVRWebHookResponseDto.class),
				Mockito.any(IVRUserSession.class), Mockito.any(CurrentAssignmentResponseDto.class))).thenReturn("");

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID035Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID035Code_HookCode2() {

		String sessionId = "session123";

		String currentState = "FID035";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setCanBePagedEmail(false);
		session.setCanBePagedMobile(false);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(session);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID035Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID035Code_HookCode1() {

		String sessionId = "session123";

		String currentState = "FID035";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setCanBePagedEmail(false);
		session.setCanBePagedMobile(false);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID035Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID400Code_HookCode1() {

		String sessionId = "session123";

		String currentState = "FID400";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"424\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"1678\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"UNK,O-W+Y-O\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}");

		when(mockIVRLfacsServiceHelper.isCircuitWithNoTN(Mockito.any(CurrentAssignmentResponseDto.class),
				Mockito.any(IVRWebHookResponseDto.class))).thenReturn(false);
		when(mockIVRLfacsServiceHelper.isSpecialCircuit(Mockito.any(CurrentAssignmentResponseDto.class),
				Mockito.any(IVRWebHookResponseDto.class))).thenReturn(false);
		when(mockIVRLfacsServiceHelper.isUdcCircuit(Mockito.any(CurrentAssignmentResponseDto.class),
				Mockito.any(IVRWebHookResponseDto.class))).thenReturn(false);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID400Code(sessionId, currentState, "0");

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID011_INVALID_SESSION() {
		String sessionid = "session123";
		String currentState = STATE_FID011;
		String userInput = "";

		when(mockCacheService.getBySessionId(sessionid)).thenReturn(null);
		IVRWebHookResponseDto response = ivrLfacsServiceImpl.processFID011(sessionid, currentState, userInput);
		assertEquals(INVALID_SESSION_ID, response.getHookReturnMessage());

	}

	@Test
	void testProcessFID400Code_HookCode2() {

		String sessionId = "session123";

		String currentState = "FID400";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"424\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"1678\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"UNK,O-W+Y-O\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}");

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.isCircuitWithNoTN(Mockito.any(CurrentAssignmentResponseDto.class),
				Mockito.any(IVRWebHookResponseDto.class))).thenReturn(true);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID400Code(sessionId, currentState, "0");

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID400Code_HookCode3() {

		String sessionId = "session123";

		String currentState = "FID400";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"424\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"1678\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"UNK,O-W+Y-O\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}");

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.isSpecialCircuit(Mockito.any(CurrentAssignmentResponseDto.class),
				Mockito.any(IVRWebHookResponseDto.class))).thenReturn(true);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID400Code(sessionId, currentState, "0");

		assertEquals(HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID400Code_HookCode4() {

		String sessionId = "session123";

		String currentState = "FID400";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"424\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"1678\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"UNK,O-W+Y-O\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}");

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.isUdcCircuit(Mockito.any(CurrentAssignmentResponseDto.class),
				Mockito.any(IVRWebHookResponseDto.class))).thenReturn(true);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID400Code(sessionId, currentState, "0");

		assertEquals(HOOK_RETURN_4, actualResponse.getHookReturnCode());
	}

	@Test
	void testFID400Code_Error() {

		String sessionId = "session123";
		String currentState = "FID400";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession mockUser = new IVRUserSession();

		mockUser.setSessionId(sessionId);
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenThrow(RuntimeException.class);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID400Code(sessionId, currentState, "0");

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID420Code() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID420";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_2);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse("");

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		when(mockIVRLfacsServiceHelper.getColourCode(Mockito.any(IVRWebHookResponseDto.class),
				Mockito.any(CurrentAssignmentResponseDto.class), Mockito.any(IVRUserSession.class)))
				.thenReturn(response);

		when(mockCacheService.updateSession(Mockito.any(IVRUserSession.class))).thenReturn(userSession);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID420Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID420CodeWithoutCurrentAssignment() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID420";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);
		userSession.setCuid("");

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);

		response = ivrLfacsServiceImpl.processFID420Code(sessionId, currentState);

		assertEquals(HOOK_RETURN, response.getHookReturnCode());
	}

	@Test
	void testFID420Code_Error() {

		String sessionId = "session123";
		String currentState = "FID420";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession mockUser = new IVRUserSession();

		mockUser.setSessionId(sessionId);
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenThrow(RuntimeException.class);

		response = ivrLfacsServiceImpl.processFID420Code(sessionId, currentState);

		assertEquals(HOOK_RETURN, response.getHookReturnCode());
	}

	@Test
	void testProcessFID429Code_Segment3() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID429";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSegmentRead("ALL");
		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse("");

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);

		String currentAssignment = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"424\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"1678\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"UNK,O-W+Y-O\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}";

		CurrentAssignmentResponseDto currentAssignmentResponse = new ObjectMapper().readValue(currentAssignment,
				CurrentAssignmentResponseDto.class);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		response = ivrLfacsServiceImpl.processFID429Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_4, response.getHookReturnCode());
	}

	@Test
	void testProcessFID429Code_Segment2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID429";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSegmentRead("ALL");
		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse("");

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);

		String currentAssignment = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":[{\"UTYP\":\"10151\",\"UID\":\"QUINCE ST NW\",\"STYP\":\"COON RPDS\",\"SID\":\"MN\",\"ETYP\":\"MN\",\"EID\":\"MN\"}],\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"ONE1\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"BK,324\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}";

		CurrentAssignmentResponseDto currentAssignmentResponse = new ObjectMapper().readValue(currentAssignment,
				CurrentAssignmentResponseDto.class);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		response = ivrLfacsServiceImpl.processFID429Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_3, response.getHookReturnCode());
	}

	@Test
	void testProcessFID429Code_Segment1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID429";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSegmentRead("ALL");
		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse("");

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);

		String currentAssignment = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":[{\"UTYP\":\"10151\",\"UID\":\"QUINCE ST NW\",\"STYP\":\"COON RPDS\",\"SID\":\"MN\",\"ETYP\":\"MN\",\"EID\":\"MN\"}],\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"ONE1\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}";

		CurrentAssignmentResponseDto currentAssignmentResponse = new ObjectMapper().readValue(currentAssignment,
				CurrentAssignmentResponseDto.class);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		response = ivrLfacsServiceImpl.processFID429Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_2, response.getHookReturnCode());
	}

	@Test
	void testProcessFID429CodeWithoutUserSession_Segment2() {

		String sessionId = "session123";

		String currentState = "FID429";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSegmentRead("F4");
		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"424\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"1678\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}");

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);

		response = ivrLfacsServiceImpl.processFID429Code(sessionId, currentState);

		assertEquals(HOOK_RETURN, response.getHookReturnCode());
	}

	@Test
	void testProcessFID429CodeF1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID429";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSegmentRead("F1");
		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse("");

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);

		String currentAssignment = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":[{\"UTYP\":\"10151\",\"UID\":\"QUINCE ST NW\",\"STYP\":\"COON RPDS\",\"SID\":\"MN\",\"ETYP\":\"MN\",\"EID\":\"MN\"}],\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"ONE1\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}";

		CurrentAssignmentResponseDto currentAssignmentResponse = new ObjectMapper().readValue(currentAssignment,
				CurrentAssignmentResponseDto.class);
		
		List<SEG> segList = currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSEG();
		
		SEG seg = new SEG();
		
		seg.setCA("1");
		seg.setPR("2");
		
		segList.add(seg);
		
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		response = ivrLfacsServiceImpl.processFID429Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());
	}
	
	@Test
	void testProcessFID429CodeF2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID429";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSegmentRead("F2");
		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse("");

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);

		String currentAssignment = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":[{\"UTYP\":\"10151\",\"UID\":\"QUINCE ST NW\",\"STYP\":\"COON RPDS\",\"SID\":\"MN\",\"ETYP\":\"MN\",\"EID\":\"MN\"}],\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"ONE1\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}";

		CurrentAssignmentResponseDto currentAssignmentResponse = new ObjectMapper().readValue(currentAssignment,
				CurrentAssignmentResponseDto.class);
		
		List<SEG> segList = currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSEG();
		
		SEG seg = new SEG();
		
		seg.setCA("1");
		seg.setPR("2");
		
		SEG seg1 = new SEG();
		
		seg1.setCA("1");
		seg1.setPR("2");
		
		segList.add(seg);
		segList.add(seg1);
		
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		response = ivrLfacsServiceImpl.processFID429Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());
	}

	@Test
	void testProcessFID429Code_No_Segment() {

		String sessionId = "session123";

		String currentState = "FID429";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSegmentRead("F4");
		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"424\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}");

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);

		response = ivrLfacsServiceImpl.processFID429Code(sessionId, currentState);

		assertEquals(HOOK_RETURN, response.getHookReturnCode());
	}

	@Test
	void testFID429Code_Error() {

		String sessionId = "session123";
		String currentState = "FID429";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN);
		response.setHookReturnMessage(IVRConstants.FASTIVR_BACKEND_ERR);

		IVRUserSession mockUser = new IVRUserSession();

		mockUser.setSessionId(sessionId);
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenThrow(RuntimeException.class);

		ivrLfacsServiceImpl.processFID429Code(sessionId, currentState);

		assertEquals(HOOK_RETURN, response.getHookReturnCode());
	}

	@SuppressWarnings("unchecked")
	@Test
	void testProcessFID020Code_Success() throws JsonMappingException, JsonProcessingException, IllegalArgumentException,
			IllegalAccessException, InterruptedException, ExecutionException {

		String sessionId = "session123";
		String currentState = "FID020";

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		mockSession.setCable("2");
		mockSession.setPair("1");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		CurrentAssignmentRequestTnDto mockRequest = new CurrentAssignmentRequestTnDto();
		CurrentAssignmentResponseDto mockResponse = new CurrentAssignmentResponseDto();
		mockSession.setLosDbResponse(mockLosDbJsonString);

		// mocking
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(mockIVRLfacsServiceHelper.buildCurrentAssignmentInqRequest(mockTnInfo.getTn(), mockTnInfo.getPrimaryNPA(),
				mockTnInfo.getPrimaryNXX(), null, mockSession)).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIVRLfacsServiceHelper.callCurrentAssignmentInquiryLfacs(jsonRequestString, mockSession))
				.thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, CurrentAssignmentResponseDto.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID020Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID020Code_losDBNullResposne() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentState = "FID020";

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

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID020Code(sessionId, currentState);
		assertEquals(CANNOT_FETCH_CURR_ASSG_API_LOSDB_NULL, actualResponse.getHookReturnMessage());
	}

	@Test
	void testProcessFID020Code_GPDOWN() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentState = "FID020";
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		String jsonString = "mock-json-resp";
		mockSession.setLosDbResponse(jsonString);
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		// mocking
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		doThrow(JsonMappingException.class).when(mockIVRLfacsServiceHelper).extractTNInfoFromLosDBResponse(jsonString);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID020Code(sessionId, currentState);
		assertEquals(GPDOWN_ERR_MSG, actualResponse.getHookReturnMessage());
	}

	@Test
	void testProcessFID020Code_SESSIONidNULL() {
		String sessionId = "session123";
		String currentState = "FID020";
		List<String> userInputDTMFList = new ArrayList<>();
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID020Code(sessionId, currentState);
		assertEquals(INVALID_SESSION_ID, actualResponse.getHookReturnMessage());
	}

	private IVRUserSession loadIvrSession() {
		IVRUserSession session = new IVRUserSession();
		session.setSessionId("session123");
		return session;
	}

	private IVRUserSession loadIvrSession(String sessionId, String empid, boolean isAuthenticated) {
		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setAuthenticated(isAuthenticated);
		userSession.setEmpID(empid);
		return userSession;
	}

/////////////////////////////FID025 test cases/////////////////////////////////////////////////

	@Test
	void testProcessFID025Code_HookCode1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID025";

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

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponse.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse(currentAssignmentResponse.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID025Code(sessionId, currentState, null);

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID025Code_HookCode2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID025";

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

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponse.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID025Code(sessionId, currentState, null);

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID025Code_HookCode3() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID025";

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

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponse.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID025Code(sessionId, currentState, null);

		assertEquals(HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID025Code_HookCode4() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID025";

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

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponse.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID025Code(sessionId, currentState, null);

		assertEquals(HOOK_RETURN_4, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID025Code_HookCode5() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID025";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("5");
		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("L400-192");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("L400-192");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponse.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID025Code(sessionId, currentState, null);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_5, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID025Code_HookCode6() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID025";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("6");
		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("L500-235");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("L500-235");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponse.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID025Code(sessionId, currentState, null);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_6, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID025Code_HookCode7() throws JsonMappingException, JsonProcessingException {

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
		currentAssignmentResponse.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID025Code(sessionId, currentState, "1");

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_7, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID025Code_HookCode0() throws JsonMappingException, JsonProcessingException {

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
		currentAssignmentResponse.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID025Code(sessionId, currentState, "1");

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID025Code_HookCode8() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID025";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("8");
		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorStatus("S");
		currentAssignmentResponse.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID025Code(sessionId, currentState, null);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID025Code_CurrentAssignNull() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID025";

		IVRUserSession userSession = new IVRUserSession();

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID025Code(sessionId, currentState, null);

		assertEquals("Current Assignment is null", actualResponse.getHookReturnMessage());
	}

	@Test
	void testProcessFID025Code_InvalidSession() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID025";

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID025Code(sessionId, currentState, null);

		assertEquals("In-Valid Session id, please try with a loaded Session", actualResponse.getHookReturnMessage());
	}

///////////////////////////FID045 test cases////////////////////////////////////

	@Test
	void testProcessFID045Code_valid() {

		String sessionId = "session123";
		String currentState = "FID045";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setCanBePagedEmail(true);
		userSession.setCanBePagedMobile(true);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID045Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID045Code_Invalid() {

		String sessionId = "session123";
		String currentState = "FID045";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setCanBePagedEmail(false);
		userSession.setCanBePagedMobile(false);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID045Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID045Code_InvalidSession() {

		String sessionId = "session123";
		String currentState = "FID045";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID045Code(sessionId, currentState);

		assertEquals("In-Valid Session id, please try with a loaded Session", actualResponse.getHookReturnMessage());
	}

	@Test
	void testProcessFID055Code_HookCode0()
			throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException {

		String sessionId = "session123";
		String currentState = "FID055";

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(false);

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
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID055Code(sessionId, currentState,
				userInputDTMFList.get(0));

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID055Code_HookCode2()
			throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "FID055";

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
		when(mockIVRLfacsServiceHelper.buildRetriveMainInqRequest(mockTnInfo.getTn(), mockTnInfo.getPrimaryNPA(),
				mockTnInfo.getPrimaryNXX(),currentAssignmentResponse, mockSession, 0)).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIVRLfacsServiceHelper.callSparePairInquiryLfacs(jsonRequestString, mockSession))
				.thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, RetrieveMaintenanceChangeTicketResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID055Code(sessionId, currentState,
				userInputDTMFList.get(0));

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID055Code_HookCode3()
			throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "FID055";

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
		userInputDTMFList.add("2");

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
		when(mockIVRLfacsServiceHelper.buildRetriveMainInqRequest(mockTnInfo.getTn(), mockTnInfo.getPrimaryNPA(),
				mockTnInfo.getPrimaryNXX(),currentAssignmentResponse, mockSession, 0)).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIVRLfacsServiceHelper.callSparePairInquiryLfacs(jsonRequestString, mockSession))
				.thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, RetrieveMaintenanceChangeTicketResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID055Code(sessionId, currentState,
				userInputDTMFList.get(0));

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID055Code_HookCode4()
			throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "FID055";

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
		mockSession.setCanBePagedEmail(true);
		mockSession.setCurrentAssignmentResponse("xyz");

		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("3");

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
		when(mockIVRLfacsServiceHelper.buildRetriveMainInqRequest(mockTnInfo.getTn(), mockTnInfo.getPrimaryNPA(),
				mockTnInfo.getPrimaryNXX(),currentAssignmentResponse, mockSession, 0)).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIVRLfacsServiceHelper.callSparePairInquiryLfacs(jsonRequestString, mockSession))
				.thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, RetrieveMaintenanceChangeTicketResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID055Code(sessionId, currentState,
				userInputDTMFList.get(0));

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_4, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID055Code_HookCode5()
			throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "FID055";
		SEG seg1 = new SEG();
		SEG seg2 = new SEG();
		segList.add(seg1);
		segList.add(seg2);
		loop.setSEG(segList);
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
		userInputDTMFList.add("4");

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
		
		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
		.thenReturn(currentAssignmentResponse);
		
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		
		when(mockIVRLfacsServiceHelper.buildRetriveMainInqRequest(anyString(),anyString(),anyString(),any(),any(),anyInt()))
		.thenReturn(mockRequest);
		
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		
		when(mockIVRLfacsServiceHelper.callSparePairInquiryLfacs(anyString(), any()))
				.thenReturn(mockLfacsResponse);
		
		
		
		when(mockObjectMapper.readValue(mockLfacsResponse, RetrieveMaintenanceChangeTicketResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID055Code(sessionId, currentState,
				userInputDTMFList.get(0));

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_5, actualResponse.getHookReturnCode());
	}

//	@Test
//	void testProcessFID055Code_HookCode1()
//			throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException {
//
//		String sessionId = "session123";
//		String currentState = "FID055";
//
//		so.setORD("True");
//		soList.add(so);
//		loop.setCKID("763 757-4229");
//		loop.setSO(null);
//		loop.setTID("DPAAB");
//		loopList.add(loop);
//		returnDataSet.setLoop(loopList);
//		returnDataSet.setPort1("test");
//		currentAssignmentResponse.setReturnDataSet(returnDataSet);
//		
//		messageStatus.setErrorStatus("F");
//		
//		IVRUserSession mockSession = new IVRUserSession();
//		mockSession.setSessionId(sessionId);
//		mockSession.setCanBePagedMobile(true);
//		mockSession.setCurrentAssignmentResponse("xyz");
//
//		List<String> userInputDTMFList = new ArrayList<>();
//		userInputDTMFList.add("5");
//
//		TNInfoResponse mockTnInfo = new TNInfoResponse();
//		mockTnInfo.setTn("1234567890");
//		mockTnInfo.setPrimaryNPA("123");
//		mockTnInfo.setPrimaryNXX("456");
//		String jsonRequestString = "mock-request-string";
//		String mockLfacsResponse = "mock-lfacs-response";
//		String mockLosDbJsonString = "mock-losdb-response-string";
//		RetrieveMaintenanceChangeTicketRequest mockRequest = new RetrieveMaintenanceChangeTicketRequest();
//		RetrieveMaintenanceChangeTicketResponse mockResponse = new RetrieveMaintenanceChangeTicketResponse();
//		mockSession.setLosDbResponse(mockLosDbJsonString);
//		mockSession.setRtrvMaintChngeMsgName(mockLosDbJsonString);
//		mockResponse.setMessageStatus(messageStatus);
//		
//		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
//		
//		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
//		
//		when(mockIVRLfacsServiceHelper.buildRetriveMainInqRequest(anyString(), anyString(), anyString(), any(),any(), anyInt())).thenReturn(mockRequest);
//		
//		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
//		
//		when(mockIVRLfacsServiceHelper.callSparePairInquiryLfacs(jsonRequestString, mockSession))
//				.thenReturn(mockLfacsResponse);
//		
//		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
//				.thenReturn(currentAssignmentResponse);
//		
//		when(mockObjectMapper.readValue(mockLfacsResponse, RetrieveMaintenanceChangeTicketResponse.class))
//				.thenReturn(mockResponse);
//
//		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID055Code(sessionId, currentState,
//				userInputDTMFList.get(0));
//
//		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());
//	}

	@Test
	void testProcessFID055Code_RetLoopAssignHookCode2()
			throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "FID055";
		
		seg.setCA("301SW");
		seg.setPR("301SW");
		segList.add(seg);

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setSEG(segList);

		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(true);
		mockSession.setCanBePagedEmail(true);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

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
		mockSession.setRtrvLoopAssgMsgName(mockLosDbJsonString);;
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);
		
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class)).thenReturn(currentAssignmentResponse);
		when(mockIVRLfacsServiceHelper
				.buildRetriveLoopAssignInqRequest(anyString(), anyString(), anyString(), any(),any(), anyInt())).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIVRLfacsServiceHelper.callSparePairLoopAssignInquiryLfacs(jsonRequestString, mockSession)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, RetrieveLoopAssignmentResponse.class)).thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID055Code(sessionId, currentState,userInputDTMFList.get(0));

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID055Code_RetLoopAssignHookCode2_CutPage()
			throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "FID055";
		
		seg.setCA("301SW");
		seg.setPR("301SW");
		segList.add(seg);

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setSEG(segList);

		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(true);
		mockSession.setCanBePagedEmail(true);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

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
		mockSession.setRtrvLoopAssgMsgName(mockLosDbJsonString);
		messageStatus.setErrorStatus("F");
		mockResponse.setMessageStatus(messageStatus);
		
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class)).thenReturn(currentAssignmentResponse);
		when(mockIVRLfacsServiceHelper.buildRetriveLoopAssignInqRequest(mockTnInfo.getTn(), mockTnInfo.getPrimaryNPA(),
				mockTnInfo.getPrimaryNXX(),currentAssignmentResponse, mockSession, 1)).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIVRLfacsServiceHelper.callSparePairLoopAssignInquiryLfacs(jsonRequestString, mockSession)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, RetrieveLoopAssignmentResponse.class)).thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID055Code(sessionId, currentState,userInputDTMFList.get(0));

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID055Code_InvalidUserSession() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID055";
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = null;

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID055Code(sessionId, currentState,
				userInputDTMFList.get(0));

		assertEquals("User Session Not Found", actualResponse.getHookReturnMessage());
	}

	/******************
	 * TEST CASES FOR FID060 START FROM HERE
	 ************************/

	@Test
	void shouldReturnUserIsAlphaPagerForProcessFID060() {

		String sessionId = "session123";
		String currentState = "FID060";

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setCanBePagedEmail(true);
		userSession.setCanBePagedMobile(true);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID060Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());
		assertEquals(IVRLfacsConstants.ALPHA_PAGER, actualResponse.getHookReturnMessage());
	}

	@Test
	void shouldReturnUserNotBeAlphaPagerForProcessFID060() {

		String sessionId = "session123";
		String currentState = "FID060";

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setCanBePagedMobile(false);
		userSession.setCanBePagedEmail(false);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID060Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, actualResponse.getHookReturnCode());
		assertEquals(IVRLfacsConstants.NO_ALPHA_PAGER, actualResponse.getHookReturnMessage());
	}

	@Test
	void shouldReturnInvalidSessionForProcessFID060() {

		String sessionId = "session123";
		String currentState = "FID060";

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID060Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	/******************
	 * TEST CASES FOR FID060 START FROM HERE
	 * 
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 ************************/

	@Test
	void testProcessFID445Code_Session_Empty() {

		String sessionId = "session123";
		String currentState = "FID445";
		String previousState = "FI0450";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		IVRParameter parameter = new IVRParameter();

		parameter.setData("");

		parameterList.add(parameter);

		response.setParameters(parameterList);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);

		response = ivrLfacsServiceImpl.processFID445Code(sessionId, previousState, currentState, 1);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN, response.getHookReturnCode());
	}

	@Test
	void testProcessFID445Code_1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID445";

		String previousState = "FI0460";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		IVRParameter parameter = new IVRParameter();

		parameter.setData("");

		parameterList.add(parameter);

		response.setParameters(parameterList);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse("");
		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);

		String currentAssignment = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":[{\"UTYP\":\"10151\",\"UID\":\"QUINCE ST NW\",\"STYP\":\"COON RPDS\",\"SID\":\"MN\",\"ETYP\":\"MN\",\"EID\":\"MN\"}],\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"ONE1\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}";

		CurrentAssignmentResponseDto currentAssignmentResponse = new ObjectMapper().readValue(currentAssignment,
				CurrentAssignmentResponseDto.class);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		response = ivrLfacsServiceImpl.processFID445Code(sessionId, previousState, currentState, 1);

		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());
	}

	@Test
	void testProcessFID445Code_Other_1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID445";

		String previousState = "FI0442";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		IVRParameter parameter = new IVRParameter();

		parameter.setData("");

		parameterList.add(parameter);

		response.setParameters(parameterList);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse("");
		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);

		String currentAssignment = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":[{\"UTYP\":\"10151\",\"UID\":\"QUINCE ST NW\",\"STYP\":\"COON RPDS\",\"SID\":\"MN\",\"ETYP\":\"MN\",\"EID\":\"MN\"}],\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"ONE1\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}";

		CurrentAssignmentResponseDto currentAssignmentResponse = new ObjectMapper().readValue(currentAssignment,
				CurrentAssignmentResponseDto.class);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		response = ivrLfacsServiceImpl.processFID445Code(sessionId, previousState, currentState, 1);

		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());
	}

	@Test
	void testProcessFID445Code_2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID445";

		String previousState = "FI0460";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		IVRParameter parameter = new IVRParameter();

		parameter.setData("");

		parameterList.add(parameter);

		response.setParameters(parameterList);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse("");
		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);

		String currentAssignment = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":[{\"UTYP\":\"10151\",\"UID\":\"QUINCE ST NW\",\"STYP\":\"COON RPDS\",\"SID\":\"MN\",\"ETYP\":\"MN\",\"EID\":\"MN\"}],\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"ONE1\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"BK,324\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}";

		CurrentAssignmentResponseDto currentAssignmentResponse = new ObjectMapper().readValue(currentAssignment,
				CurrentAssignmentResponseDto.class);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		response = ivrLfacsServiceImpl.processFID445Code(sessionId, previousState, currentState, 2);

		assertEquals(HOOK_RETURN_2, response.getHookReturnCode());
	}

	@Test
	void testProcessFID445Code_Other_2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID445";

		String previousState = "FI0442";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		IVRParameter parameter = new IVRParameter();

		parameter.setData("");

		parameterList.add(parameter);

		response.setParameters(parameterList);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse("");
		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);

		String currentAssignment = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":[{\"UTYP\":\"10151\",\"UID\":\"QUINCE ST NW\",\"STYP\":\"COON RPDS\",\"SID\":\"MN\",\"ETYP\":\"MN\",\"EID\":\"MN\"}],\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"ONE1\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"BK,324\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}";

		CurrentAssignmentResponseDto currentAssignmentResponse = new ObjectMapper().readValue(currentAssignment,
				CurrentAssignmentResponseDto.class);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		response = ivrLfacsServiceImpl.processFID445Code(sessionId, previousState, currentState, 2);

		assertEquals(HOOK_RETURN_2, response.getHookReturnCode());
	}

	@Test
	void testProcessFID445Code() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID445";

		String previousState = "FI0450";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		IVRParameter parameter = new IVRParameter();

		parameter.setData("");

		parameterList.add(parameter);

		response.setParameters(parameterList);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse("");
		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		response = ivrLfacsServiceImpl.processFID445Code(sessionId, previousState, currentState, 3);

		assertEquals(HOOK_RETURN_3, response.getHookReturnCode());
	}

	@Test
	void testProcessFID445Code_Other() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID445";

		String previousState = "FI0441";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		IVRParameter parameter = new IVRParameter();

		parameter.setData("");

		parameterList.add(parameter);

		response.setParameters(parameterList);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse("");
		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		response = ivrLfacsServiceImpl.processFID445Code(sessionId, previousState, currentState, 3);

		assertEquals(HOOK_RETURN_3, response.getHookReturnCode());
	}

	@Test
	void testProcessFID455Code_Session_Empty() {

		String sessionId = "session123";
		String currentState = "FID455";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		IVRParameter parameter = new IVRParameter();

		parameter.setData("");

		parameterList.add(parameter);

		response.setParameters(parameterList);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);

		response = ivrLfacsServiceImpl.processFID455Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN, response.getHookReturnCode());
	}

	@Test
	void testProcessFID455Code() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID455";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		IVRParameter parameter = new IVRParameter();

		parameter.setData("");

		parameterList.add(parameter);

		response.setParameters(parameterList);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse("");
		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		response = ivrLfacsServiceImpl.processFID455Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());
	}

	@Test
	void testFID445Code_Error() {

		String sessionId = "session123";
		String currentState = "FID445";
		String previousState = "FI0441";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession mockUser = new IVRUserSession();

		mockUser.setSessionId(sessionId);
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenThrow(RuntimeException.class);

		response = ivrLfacsServiceImpl.processFID445Code(sessionId, previousState, currentState, 1);

		assertEquals(HOOK_RETURN, response.getHookReturnCode());
	}

	@Test
	void testFID455Code_Error() {

		String sessionId = "session123";
		String currentState = "FID429";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN);
		response.setHookReturnMessage(IVRConstants.FASTIVR_BACKEND_ERR);

		IVRUserSession mockUser = new IVRUserSession();

		mockUser.setSessionId(sessionId);
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenThrow(RuntimeException.class);

		ivrLfacsServiceImpl.processFID455Code(sessionId, currentState);

		assertEquals(HOOK_RETURN, response.getHookReturnCode());
	}

	@Test
	void testProcessFID515Code_HookCode1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID515";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("1");
		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("500");
		currentAssignmentResponse.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID515Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID515Code_HookCode2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID515";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("2");
		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("504");
		currentAssignmentResponse.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID515Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID515Code_HookCode3() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID515";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("3");
		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("503");
		currentAssignmentResponse.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID515Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID515Code_HookCode4() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID515";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("4");
		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("400");
		currentAssignmentResponse.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID515Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_4, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID515Code_HookCode8() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID515";
		String enqType = "RETRIEVE_MAINT_CHANGE";

		loop.setSEG(segList);
		loop.setSO(null);
		loop.setCKID("763 757-4229");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		CandidatePairInfo candidatePairInfo = new CandidatePairInfo();
		candidatePairInfo.setCableId("28");

		List<CandidatePairInfo> listCandidatePairInfo = new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);

		RetMaintChangeTicketReturnDataSet returnDataSet = new RetMaintChangeTicketReturnDataSet();
		returnDataSet.setCandidatePairInfo(listCandidatePairInfo);
		messageStatus.setErrorStatus("S");

		RetrieveMaintenanceChangeTicketResponse retrieveMaintenanceChangeTicketResponse = new RetrieveMaintenanceChangeTicketResponse();
		retrieveMaintenanceChangeTicketResponse.setReturnDataSet(returnDataSet);
		retrieveMaintenanceChangeTicketResponse.setMessageStatus(messageStatus);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCurrentAssignmentResponse("xyz");
		userSession.setCandPairCounter(1);
		userSession.setRtrvMaintChngeMsgName("abc");

		IVRWebHookResponseDto ivrWebHookResponseDto = new IVRWebHookResponseDto();
		ivrWebHookResponseDto.setHookReturnCode("8");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);
		when(mockObjectMapper.readValue(userSession.getRtrvMaintChngeMsgName(),
				RetrieveMaintenanceChangeTicketResponse.class)).thenReturn(retrieveMaintenanceChangeTicketResponse);
		when(mockIVRLfacsServiceHelper.findSparePair(sessionId, currentState, userSession, enqType))
				.thenReturn(ivrWebHookResponseDto);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID515Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID515Code_RetLoopAssignHookCode8() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID515";
		String enqType = "RETRIEVE_LOOP_ASSSIGN";

		so.setORD("True");
		soList.add(so);
		loop.setSEG(segList);
		loop.setSO(soList);
		loop.setCKID("763 757-4229");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		LoopAssignCandidatePairInfo candidatePairInfo = new LoopAssignCandidatePairInfo();
		candidatePairInfo.setCableId("28");

		List<LoopAssignCandidatePairInfo> listCandidatePairInfo = new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);

		RetLoopAssigReturnDataSet returnDataSet = new RetLoopAssigReturnDataSet();
		returnDataSet.setCandidatePairInfo(listCandidatePairInfo);
		messageStatus.setErrorStatus("S");

		RetrieveLoopAssignmentResponse retrieveLoopAssignmentResponse = new RetrieveLoopAssignmentResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);
		retrieveLoopAssignmentResponse.setMessageStatus(messageStatus);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCurrentAssignmentResponse("xyz");
		userSession.setCandPairCounter(1);
		userSession.setRtrvLoopAssgMsgName("abc");

		IVRWebHookResponseDto ivrWebHookResponseDto = new IVRWebHookResponseDto();
		ivrWebHookResponseDto.setHookReturnCode("8");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);
		when(mockObjectMapper.readValue(userSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class))
				.thenReturn(retrieveLoopAssignmentResponse);
		when(mockIVRLfacsServiceHelper.findSparePair(sessionId, currentState, userSession, enqType))
				.thenReturn(ivrWebHookResponseDto);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID515Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID515Code_InvalidUserSession() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID055";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = null;

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID515Code(sessionId, currentState);

		assertEquals("In-Valid Session id, please try with a loaded Session", actualResponse.getHookReturnMessage());
	}

	@Test
	void processFID211_VALIDSESSION() {
		String sessionid = "session123";
		String userInput = "1234567";
		String currentState = "FID211";
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		mockResponse.setHookReturnMessage(VALID_TN_MSG);
		mockResponse.setHookReturnCode(HOOK_RETURN_1);
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionid);
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
		when(mockLfacsTNValidation.validateFacsTN(userInput, mockSession)).thenReturn(mockResponse);

		IVRWebHookResponseDto response = ivrLfacsServiceImpl.processFID211Code(sessionid, currentState, userInput);
		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());
		assertEquals(VALID_TN_MSG, response.getHookReturnMessage());
	}

	@Test
	void processFID211_INVALIDSESSION() {
		String sessionid = "session123";
		String userInput = "1234567";
		String currentState = "FID211";
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		mockResponse.setHookReturnMessage(INVALID_SESSION_ID);
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionid);
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(null);

		IVRWebHookResponseDto response = ivrLfacsServiceImpl.processFID211Code(sessionid, currentState, userInput);
		assertEquals(INVALID_SESSION_ID, response.getHookReturnMessage());
	}

	@Test
	void testProcessFID532Code_HookCode1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID532";

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCandPairCounter(3);
		userSession.setRtrvLoopAssgMsgName("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID532Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID532Code_HookCode2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID532";

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCandPairCounter(1);
		userSession.setRtrvLoopAssgMsgName("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID532Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID525Code_HookCode8() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID525";
		String reqType = "RETRIEVE_MAINT_CHANGE";
		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(null);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		CandidatePairInfo candidatePairInfo = new CandidatePairInfo();
		candidatePairInfo.setCableId("28");

		List<CandidatePairInfo> listCandidatePairInfo = new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);

		RetMaintChangeTicketReturnDataSet returnDataSet = new RetMaintChangeTicketReturnDataSet();
		returnDataSet.setCandidatePairInfo(listCandidatePairInfo);

		RetrieveMaintenanceChangeTicketResponse retrieveLoopAssignmentResponse = new RetrieveMaintenanceChangeTicketResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCandPairCounter(0);
		userSession.setCurrentAssignmentResponse("xyz");
		userSession.setRtrvMaintChngeMsgName(retrieveLoopAssignmentResponse.toString());

		List<String> userInputs = new ArrayList<>();
		userInputs.add("");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getRtrvMaintChngeMsgName(),
				RetrieveMaintenanceChangeTicketResponse.class)).thenReturn(retrieveLoopAssignmentResponse);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID525Code(sessionId, currentState,
				userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID525Code_LoopAssignHookCode8() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID525";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		LoopAssignCandidatePairInfo candidatePairInfo = new LoopAssignCandidatePairInfo();
		candidatePairInfo.setCableId("28");

		List<LoopAssignCandidatePairInfo> listCandidatePairInfo = new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);

		RetLoopAssigReturnDataSet returnDataSet = new RetLoopAssigReturnDataSet();
		returnDataSet.setCandidatePairInfo(listCandidatePairInfo);

		RetrieveLoopAssignmentResponse retrieveLoopAssignmentResponse = new RetrieveLoopAssignmentResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCandPairCounter(0);
		userSession.setCurrentAssignmentResponse("xyz");
		userSession.setRtrvLoopAssgMsgName(retrieveLoopAssignmentResponse.toString());

		List<String> userInputs = new ArrayList<>();
		userInputs.add("");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class))
				.thenReturn(retrieveLoopAssignmentResponse);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID525Code(sessionId, currentState,
				userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID525Code_HookCode81() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID525";

		loop.setCKID("763 757-4229");
		loop.setSO(null);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		CandidatePairInfo candidatePairInfo = new CandidatePairInfo();
		candidatePairInfo.setCableId("28");
		CandidatePairInfo candidatePairInfo1 = new CandidatePairInfo();
		candidatePairInfo1.setCableId("28");
		CandidatePairInfo candidatePairInfo2 = new CandidatePairInfo();
		candidatePairInfo2.setCableId("28");

		List<CandidatePairInfo> listCandidatePairInfo = new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);
		listCandidatePairInfo.add(candidatePairInfo1);
		listCandidatePairInfo.add(candidatePairInfo2);

		RetMaintChangeTicketReturnDataSet returnDataSet = new RetMaintChangeTicketReturnDataSet();
		returnDataSet.setCandidatePairInfo(listCandidatePairInfo);

		RetrieveMaintenanceChangeTicketResponse retrieveLoopAssignmentResponse = new RetrieveMaintenanceChangeTicketResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCandPairCounter(1);
		userSession.setCurrentAssignmentResponse("xyz");
		userSession.setRtrvMaintChngeMsgName(retrieveLoopAssignmentResponse.toString());

		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getRtrvMaintChngeMsgName(),
				RetrieveMaintenanceChangeTicketResponse.class)).thenReturn(retrieveLoopAssignmentResponse);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID525Code(sessionId, currentState,
				userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID525Code_HookCode82() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID525";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(null);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		CandidatePairInfo candidatePairInfo = new CandidatePairInfo();
		candidatePairInfo.setCableId("28");
		CandidatePairInfo candidatePairInfo1 = new CandidatePairInfo();
		candidatePairInfo1.setCableId("28");
		CandidatePairInfo candidatePairInfo2 = new CandidatePairInfo();
		candidatePairInfo2.setCableId("28");

		List<CandidatePairInfo> listCandidatePairInfo = new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);
		listCandidatePairInfo.add(candidatePairInfo1);
		listCandidatePairInfo.add(candidatePairInfo2);

		RetMaintChangeTicketReturnDataSet returnDataSet = new RetMaintChangeTicketReturnDataSet();
		returnDataSet.setCandidatePairInfo(listCandidatePairInfo);

		RetrieveMaintenanceChangeTicketResponse retrieveLoopAssignmentResponse = new RetrieveMaintenanceChangeTicketResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCandPairCounter(3);
		userSession.setCurrentAssignmentResponse("xyz");
		userSession.setRtrvMaintChngeMsgName(retrieveLoopAssignmentResponse.toString());

		List<String> userInputs = new ArrayList<>();
		userInputs.add("2");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getRtrvMaintChngeMsgName(),
				RetrieveMaintenanceChangeTicketResponse.class)).thenReturn(retrieveLoopAssignmentResponse);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID525Code(sessionId, currentState,
				userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID525Code_HookCode84() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID525";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(null);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		CandidatePairInfo candidatePairInfo = new CandidatePairInfo();
		candidatePairInfo.setCableId("28");
		CandidatePairInfo candidatePairInfo1 = new CandidatePairInfo();
		candidatePairInfo1.setCableId("28");
		CandidatePairInfo candidatePairInfo2 = new CandidatePairInfo();
		candidatePairInfo2.setCableId("28");

		List<CandidatePairInfo> listCandidatePairInfo = new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);
		listCandidatePairInfo.add(candidatePairInfo1);
		listCandidatePairInfo.add(candidatePairInfo2);

		RetMaintChangeTicketReturnDataSet returnDataSet = new RetMaintChangeTicketReturnDataSet();
		returnDataSet.setCandidatePairInfo(listCandidatePairInfo);

		RetrieveMaintenanceChangeTicketResponse retrieveLoopAssignmentResponse = new RetrieveMaintenanceChangeTicketResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCandPairCounter(0);
		userSession.setCurrentAssignmentResponse("xyz");
		userSession.setRtrvMaintChngeMsgName(retrieveLoopAssignmentResponse.toString());

		List<String> userInputs = new ArrayList<>();
		userInputs.add("4");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getRtrvMaintChngeMsgName(),
				RetrieveMaintenanceChangeTicketResponse.class)).thenReturn(retrieveLoopAssignmentResponse);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID525Code(sessionId, currentState,
				userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID525Code_HookCode83() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID525";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(null);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		CandidatePairInfo candidatePairInfo = new CandidatePairInfo();
		candidatePairInfo.setCableId("28");
		CandidatePairInfo candidatePairInfo1 = new CandidatePairInfo();
		candidatePairInfo1.setCableId("28");
		CandidatePairInfo candidatePairInfo2 = new CandidatePairInfo();
		candidatePairInfo2.setCableId("28");

		List<CandidatePairInfo> listCandidatePairInfo = new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);
		listCandidatePairInfo.add(candidatePairInfo1);
		listCandidatePairInfo.add(candidatePairInfo2);

		RetMaintChangeTicketReturnDataSet returnDataSet = new RetMaintChangeTicketReturnDataSet();
		returnDataSet.setCandidatePairInfo(listCandidatePairInfo);

		RetrieveMaintenanceChangeTicketResponse retrieveLoopAssignmentResponse = new RetrieveMaintenanceChangeTicketResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCandPairCounter(0);
		userSession.setCurrentAssignmentResponse("xyz");
		userSession.setRtrvMaintChngeMsgName(retrieveLoopAssignmentResponse.toString());

		List<String> userInputs = new ArrayList<>();
		userInputs.add("3");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getRtrvMaintChngeMsgName(),
				RetrieveMaintenanceChangeTicketResponse.class)).thenReturn(retrieveLoopAssignmentResponse);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID525Code(sessionId, currentState,
				userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID525Code_HookCode6() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID525";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(null);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		CandidatePairInfo candidatePairInfo = new CandidatePairInfo();
		candidatePairInfo.setCableId("28");
		CandidatePairInfo candidatePairInfo1 = new CandidatePairInfo();
		candidatePairInfo1.setCableId("28");
		CandidatePairInfo candidatePairInfo2 = new CandidatePairInfo();
		candidatePairInfo2.setCableId("28");

		List<CandidatePairInfo> listCandidatePairInfo = new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);
		listCandidatePairInfo.add(candidatePairInfo1);
		listCandidatePairInfo.add(candidatePairInfo2);

		RetMaintChangeTicketReturnDataSet returnDataSet = new RetMaintChangeTicketReturnDataSet();
		returnDataSet.setCandidatePairInfo(listCandidatePairInfo);

		RetrieveMaintenanceChangeTicketResponse retrieveLoopAssignmentResponse = new RetrieveMaintenanceChangeTicketResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCandPairCounter(3);
		userSession.setCurrentAssignmentResponse("xyz");
		userSession.setRtrvMaintChngeMsgName(retrieveLoopAssignmentResponse.toString());

		List<String> userInputs = new ArrayList<>();
		userInputs.add("");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getRtrvMaintChngeMsgName(),
				RetrieveMaintenanceChangeTicketResponse.class)).thenReturn(retrieveLoopAssignmentResponse);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID525Code(sessionId, currentState,
				userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_6, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID525Code_HookCode5() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID525";
		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(null);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		CandidatePairInfo candidatePairInfo = new CandidatePairInfo();
		candidatePairInfo.setCableId("28");

		List<CandidatePairInfo> listCandidatePairInfo = new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);

		RetMaintChangeTicketReturnDataSet returnDataSet = new RetMaintChangeTicketReturnDataSet();
		returnDataSet.setCandidatePairInfo(listCandidatePairInfo);

		RetrieveMaintenanceChangeTicketResponse retrieveLoopAssignmentResponse = new RetrieveMaintenanceChangeTicketResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCandPairCounter(1);
		userSession.setCurrentAssignmentResponse("xyz");
		userSession.setRtrvMaintChngeMsgName(retrieveLoopAssignmentResponse.toString());

		List<String> userInputs = new ArrayList<>();
		userInputs.add("");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getRtrvMaintChngeMsgName(),
				RetrieveMaintenanceChangeTicketResponse.class)).thenReturn(retrieveLoopAssignmentResponse);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID525Code(sessionId, currentState,
				userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_5, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID525Code_HookCode4() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID525";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(null);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		CandidatePairInfo candidatePairInfo = new CandidatePairInfo();
		candidatePairInfo.setCableId("28");
		candidatePairInfo.setBindingPostColorCode("");
		
		CandidatePairInfo candidatePairInfo1 = new CandidatePairInfo();
		candidatePairInfo1.setCableId("28");
		candidatePairInfo.setBindingPostColorCode("");
		

		List<CandidatePairInfo> listCandidatePairInfo = new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);
		listCandidatePairInfo.add(candidatePairInfo1);
		
		RetMaintChangeTicketReturnDataSet returnDataSet = new RetMaintChangeTicketReturnDataSet();
		returnDataSet.setCandidatePairInfo(listCandidatePairInfo);

		RetrieveMaintenanceChangeTicketResponse retrieveLoopAssignmentResponse = new RetrieveMaintenanceChangeTicketResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCandPairCounter(-1);
		userSession.setRtrvMaintChngeMsgName(retrieveLoopAssignmentResponse.toString());
		userSession.setCurrentAssignmentResponse("xyz");

		List<String> userInputs = new ArrayList<>();
		userInputs.add("");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getRtrvMaintChngeMsgName(),
				RetrieveMaintenanceChangeTicketResponse.class)).thenReturn(retrieveLoopAssignmentResponse);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID525Code(sessionId, currentState,
				userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_4, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID525Code_HookCode1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID525";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(null);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		CandidatePairInfo candidatePairInfo = new CandidatePairInfo();
		candidatePairInfo.setCableId("28");
		candidatePairInfo.setPairSelectionInfo("-");

		List<CandidatePairInfo> listCandidatePairInfo = new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);

		RetMaintChangeTicketReturnDataSet returnDataSet = new RetMaintChangeTicketReturnDataSet();
		returnDataSet.setCandidatePairInfo(listCandidatePairInfo);

		RetrieveMaintenanceChangeTicketResponse retrieveLoopAssignmentResponse = new RetrieveMaintenanceChangeTicketResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCandPairCounter(-1);
		userSession.setCurrentAssignmentResponse("xyz");
		userSession.setRtrvMaintChngeMsgName(retrieveLoopAssignmentResponse.toString());

		List<String> userInputs = new ArrayList<>();
		userInputs.add("");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getRtrvMaintChngeMsgName(),
				RetrieveMaintenanceChangeTicketResponse.class)).thenReturn(retrieveLoopAssignmentResponse);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);
		when(mockIVRLfacsServiceHelper.getUndesirableCandPair("RETRIEVE_MAINT_CHANGE", listCandidatePairInfo,null)).thenReturn(1);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID525Code(sessionId, currentState,
				userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID525Code_HookCode2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID525";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(null);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);
		
		CandidatePairInfo candidatePairInfo=new CandidatePairInfo();
		candidatePairInfo.setPairSelectionInfo("-");
		CandidatePairInfo candidatePairInfo1=new CandidatePairInfo();
		candidatePairInfo1.setPairSelectionInfo("-");

		List<CandidatePairInfo> listCandidatePairInfo = new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);
		listCandidatePairInfo.add(candidatePairInfo1);

		RetMaintChangeTicketReturnDataSet returnDataSet = new RetMaintChangeTicketReturnDataSet();
		returnDataSet.setCandidatePairInfo(listCandidatePairInfo);
		

		RetrieveMaintenanceChangeTicketResponse retrieveLoopAssignmentResponse = new RetrieveMaintenanceChangeTicketResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCandPairCounter(-1);
		userSession.setCurrentAssignmentResponse("xyz");
		userSession.setRtrvMaintChngeMsgName(retrieveLoopAssignmentResponse.toString());

		List<String> userInputs = new ArrayList<>();
		userInputs.add("");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getRtrvMaintChngeMsgName(),
				RetrieveMaintenanceChangeTicketResponse.class)).thenReturn(retrieveLoopAssignmentResponse);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);
		when(mockIVRLfacsServiceHelper.getUndesirableCandPair("RETRIEVE_MAINT_CHANGE", listCandidatePairInfo,null)).thenReturn(2);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID525Code(sessionId, currentState,
				userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());

	}

	@SuppressWarnings("unchecked")
	@Test
	void testProcessFID237Code_Success() throws JsonMappingException, JsonProcessingException, IllegalArgumentException,
			IllegalAccessException, InterruptedException, ExecutionException {

		String sessionId = "session123";
		String currentState = "FID237";

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		List<String> mockUserInputDTMFList = new ArrayList<>();
		mockUserInputDTMFList.add("123");
		mockUserInputDTMFList.add("321");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		mockSession.setCable("2");
		mockSession.setPair("1");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		CurrentAssignmentRequestTnDto mockRequest = new CurrentAssignmentRequestTnDto();
		CurrentAssignmentResponseDto mockResponse = new CurrentAssignmentResponseDto();
		mockSession.setLosDbResponse(mockLosDbJsonString);

		// mocking
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(mockIVRLfacsServiceHelper.buildCurrentAssignmentInqRequest(mockTnInfo.getTn(), mockTnInfo.getPrimaryNPA(),
				mockTnInfo.getPrimaryNXX(), mockUserInputDTMFList, mockSession)).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIVRLfacsServiceHelper.callCurrentAssignmentInquiryLfacs(jsonRequestString, mockSession))
				.thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, CurrentAssignmentResponseDto.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID237Code(sessionId, currentState,
				mockUserInputDTMFList);

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID237Code_losDBNullResposne() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentState = "FID237";

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

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID237Code(sessionId, currentState,
				userInputDTMFList);
		assertEquals(CANNOT_FETCH_CURR_ASSG_API_LOSDB_NULL, actualResponse.getHookReturnMessage());
	}

	@Test
	void testProcessFID237Code_GPDOWN() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentState = "FID237";
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		String jsonString = "mock-json-resp";
		mockSession.setLosDbResponse(jsonString);
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		// mocking
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		doThrow(JsonMappingException.class).when(mockIVRLfacsServiceHelper).extractTNInfoFromLosDBResponse(jsonString);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID237Code(sessionId, currentState,
				userInputDTMFList);
		assertEquals(GPDOWN_ERR_MSG, actualResponse.getHookReturnMessage());
	}

	@Test
	void testProcessFID237Code_SESSIONidNULL() {
		String sessionId = "session123";
		String currentState = "FID237";
		List<String> userInputDTMFList = new ArrayList<>();
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID237Code(sessionId, currentState,
				userInputDTMFList);
		assertEquals(INVALID_SESSION_ID, actualResponse.getHookReturnMessage());
	}

	@Test
	void testProcessFID500Valid() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID500";

		List<String> userInputDTMFList = new ArrayList<String>();

		userInputDTMFList.add("2");

		seg.setSEGNO("1");
		segList.add(seg);
		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSEG(segList);
		loop.setSO(soList);
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);
		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setFacsInqType(IVRConstants.INQUIRY_BY_TN);
		mockSession.setCurrentAssignmentResponse("xyz");
		String mockLosDbJsonString = "mock-losdb-response-string";
		mockSession.setLosDbResponse(mockLosDbJsonString);
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		mockSession.setCurrentAssignmentResponse(currentAssignmentResponse.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID500Code(sessionId, currentState,
				userInputDTMFList);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID500Valid_1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID500";

		List<String> userInputDTMFList = new ArrayList<String>();
		userInputDTMFList.add("1");
		userInputDTMFList.add("2");
		userInputDTMFList.add("3");
		userInputDTMFList.add("4");

		seg.setSEGNO("1");
		segList.add(seg);
		SEG seg1 = new SEG();
		seg1.setSEGNO("2");
		segList.add(seg1);
		SEG seg2 = new SEG();
		seg2.setSEGNO("3");
		segList.add(seg2);
		SEG seg3 = new SEG();
		seg3.setSEGNO("4");
		segList.add(seg3);
		loop.setSEG(segList);
		loop.setSO(soList);
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);
		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setFacsInqType(IVRConstants.INQUIRY_BY_TN);
		mockSession.setCurrentAssignmentResponse("xyz");
		String mockLosDbJsonString = "mock-losdb-response-string";
		mockSession.setLosDbResponse(mockLosDbJsonString);
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		mockSession.setCurrentAssignmentResponse(currentAssignmentResponse.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID500Code(sessionId, currentState,
				userInputDTMFList);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID500Valid_2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID500";

		List<String> userInputDTMFList = new ArrayList<String>();
		userInputDTMFList.add("3");

		seg.setSEGNO("1");
		segList.add(seg);
		SEG seg1 = new SEG();
		seg1.setSEGNO("2");
		segList.add(seg1);

		loop.setCKID("763 757-4229");
		loop.setSEG(segList);

		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setFacsInqType(IVRConstants.INQUIRY_BY_TN);
		mockSession.setCurrentAssignmentResponse("xyz");
		String mockLosDbJsonString = "mock-losdb-response-string";
		mockSession.setLosDbResponse(mockLosDbJsonString);
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		mockSession.setCurrentAssignmentResponse(currentAssignmentResponse.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID500Code(sessionId, currentState,
				userInputDTMFList);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID500Valid_4() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID500";

		List<String> userInputDTMFList = new ArrayList<String>();
		userInputDTMFList.add("1");

		seg.setSEGNO("1");
		segList.add(seg);
		SEG seg1 = new SEG();
		seg1.setSEGNO("2");
		segList.add(seg1);
		SEG seg2 = new SEG();
		seg2.setSEGNO("3");
		segList.add(seg2);

		loop.setCKID("763 757-4229");
		loop.setSEG(segList);

		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setFacsInqType(IVRConstants.INQUIRY_BY_TN);
		mockSession.setCurrentAssignmentResponse("xyz");
		String mockLosDbJsonString = "mock-losdb-response-string";
		mockSession.setLosDbResponse(mockLosDbJsonString);
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		mockSession.setCurrentAssignmentResponse(currentAssignmentResponse.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID500Code(sessionId, currentState,
				userInputDTMFList);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_6, actualResponse.getHookReturnCode());
	}
	
	@Test
	void testProcessFID500Valid_5() throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException {

		String sessionId = "session123";
		String currentState = "FID500";

		List<String> userInputDTMFList = new ArrayList<String>();
		userInputDTMFList.add("1");

		seg.setSEGNO("1");
		segList.add(seg);
		SEG seg1 = new SEG();
		seg1.setSEGNO("2");
		segList.add(seg1);
		SEG seg2 = new SEG();
		seg2.setSEGNO("3");
		segList.add(seg2);

		loop.setCKID("763 757-4229");
		loop.setSEG(segList);

		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn(null);
		IVRUserSession mockSession = new IVRUserSession();
		String mockLosDbJsonString = "mock-losdb-response-string";
		mockSession.setSessionId(sessionId);
		mockSession.setFacsInqType(IVRConstants.INQUIRY_BY_CABLE_PAIR);
		mockSession.setCurrentAssignmentResponse("xyz");
		mockSession.setLosDbResponse(mockLosDbJsonString);
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID500Code(sessionId, currentState,
				userInputDTMFList);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_7, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID535Code() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID535";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(null);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_2);

		CandidatePairInfo candidatePairInfo = new CandidatePairInfo();
		candidatePairInfo.setBindingPostColorCode("2");

		List<CandidatePairInfo> candidatePairInfoList = new ArrayList<>();
		candidatePairInfoList.add(candidatePairInfo);

		RetMaintChangeTicketReturnDataSet retMaintChangeTicketReturnDataSet = new RetMaintChangeTicketReturnDataSet();
		retMaintChangeTicketReturnDataSet.setCandidatePairInfo(candidatePairInfoList);

		RetrieveMaintenanceChangeTicketResponse retrieveMaintenanceResponse = new RetrieveMaintenanceChangeTicketResponse();
		retrieveMaintenanceResponse.setReturnDataSet(retMaintChangeTicketReturnDataSet);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse("abc");
		userSession.setCandPairCounter(1);
		userSession.setRtrvMaintChngeMsgName("abc");

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getRtrvMaintChngeMsgName(),
				RetrieveMaintenanceChangeTicketResponse.class)).thenReturn(retrieveMaintenanceResponse);

		when(mockIVRLfacsServiceHelper.getColourCodeRetrieveMaintChange(Mockito.any(IVRWebHookResponseDto.class),
				Mockito.any(RetrieveMaintenanceChangeTicketResponse.class), Mockito.any(IVRUserSession.class)))
				.thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID535Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID535CodeRetLoopAssign() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID535";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_2);

		LoopAssignCandidatePairInfo candidatePairInfo = new LoopAssignCandidatePairInfo();
		candidatePairInfo.setBindingPostColorCode("2");

		List<LoopAssignCandidatePairInfo> candidatePairInfoList = new ArrayList<>();
		candidatePairInfoList.add(candidatePairInfo);

		RetLoopAssigReturnDataSet retLoopAssigReturnDataSet = new RetLoopAssigReturnDataSet();
		retLoopAssigReturnDataSet.setCandidatePairInfo(candidatePairInfoList);

		RetrieveLoopAssignmentResponse retrieveMaintenanceResponse = new RetrieveLoopAssignmentResponse();
		retrieveMaintenanceResponse.setReturnDataSet(retLoopAssigReturnDataSet);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse("abc");
		userSession.setCandPairCounter(1);
		userSession.setRtrvLoopAssgMsgName("abc");

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class))
				.thenReturn(retrieveMaintenanceResponse);

		when(mockIVRLfacsServiceHelper.getColourCodeRetrieveLoopAssignment(Mockito.any(IVRWebHookResponseDto.class),
				Mockito.any(RetrieveLoopAssignmentResponse.class), Mockito.any(IVRUserSession.class)))
				.thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID535Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID535Code_InvalidUserSession() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID535";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = null;

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID535Code(sessionId, currentState);

		assertEquals("In-Valid Session id, please try with a loaded Session", actualResponse.getHookReturnMessage());
	}

	@Test
	void testProcessFID535Code_RetrieveResponseNotFound() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID535";

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCanBePagedEmail(true);
		userSession.setCanBePagedMobile(true);
		userSession.setCutPageSent(true);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID535Code(sessionId, currentState);

		assertEquals("Retrieve Response Not Found", actualResponse.getHookReturnMessage());

	}

	@Test
	void testProcessFID560Code_HookCode1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID560";

		IVRUserSession userSession = new IVRUserSession();
		userSession.setFacsInqType("TN");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID560Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID560Code_HookCode2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID560";

		IVRUserSession userSession = new IVRUserSession();
		userSession.setFacsInqType("CP");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID560Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID560Code_InvalidEnquiry() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID560";

		IVRUserSession userSession = new IVRUserSession();
		userSession.setFacsInqType("AB");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID560Code(sessionId, currentState);

		assertEquals("Invalid Enquiry Type", actualResponse.getHookReturnMessage());
	}

	@Test
	void testProcessFID560Code_InvalidUserSession() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID560";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = null;

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID560Code(sessionId, currentState);

		assertEquals("In-Valid Session id, please try with a loaded Session", actualResponse.getHookReturnMessage());
	}

	@Test
	void testProcessFID068Valid() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID068";

		List<String> userInputDTMFList = new ArrayList<String>();

		userInputDTMFList.add("2");

		seg.setSEGNO("1");
		segList.add(seg);
		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSEG(segList);
		loop.setSO(soList);
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCanBePagedEmail(true);
		userSession.setCanBePagedMobile(true);
		userSession.setCurrentAssignmentResponse(currentAssignmentResponse.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID068Code(sessionId, currentState,
				userInputDTMFList);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID068Valid_1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID068";

		List<String> userInputDTMFList = new ArrayList<String>();
		userInputDTMFList.add("1");
		userInputDTMFList.add("2");
		userInputDTMFList.add("3");
		userInputDTMFList.add("4");

		seg.setSEGNO("1");
		segList.add(seg);
		SEG seg1 = new SEG();
		seg1.setSEGNO("2");
		segList.add(seg1);
		SEG seg2 = new SEG();
		seg2.setSEGNO("3");
		segList.add(seg2);
		SEG seg3 = new SEG();
		seg3.setSEGNO("4");
		segList.add(seg3);
		loop.setSEG(segList);
		loop.setSO(soList);
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCanBePagedEmail(true);
		userSession.setCanBePagedMobile(true);
		userSession.setCurrentAssignmentResponse(currentAssignmentResponse.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID068Code(sessionId, currentState,
				userInputDTMFList);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID068Valid_2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID068";

		List<String> userInputDTMFList = new ArrayList<String>();
		userInputDTMFList.add("3");

		seg.setSEGNO("1");
		segList.add(seg);
		SEG seg1 = new SEG();
		seg1.setSEGNO("2");
		segList.add(seg1);

		loop.setCKID("763 757-4229");
		loop.setSEG(segList);

		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCanBePagedEmail(true);
		userSession.setCanBePagedMobile(true);
		userSession.setCurrentAssignmentResponse(currentAssignmentResponse.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID068Code(sessionId, currentState,
				userInputDTMFList);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());

	}

	@Test
	void testProcessFID068Valid_4() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID068";

		List<String> userInputDTMFList = new ArrayList<String>();
		userInputDTMFList.add("1");

		seg.setSEGNO("1");
		segList.add(seg);
		SEG seg1 = new SEG();
		seg1.setSEGNO("2");
		segList.add(seg1);
		SEG seg2 = new SEG();
		seg2.setSEGNO("3");
		segList.add(seg2);

		loop.setCKID("763 757-4229");
		loop.setSEG(segList);

		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCanBePagedEmail(true);
		userSession.setCanBePagedMobile(true);
		userSession.setCurrentAssignmentResponse(currentAssignmentResponse.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID068Code(sessionId, currentState,
				userInputDTMFList);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_6, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID271Code_HookCode3() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID271";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setCanBePagedMobile(true);

		session.setCurrentAssignmentResponse(currentAssignment);

		when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(session);

		when(mockIVRLfacsPagerText.getPageCurrentAssignment(Mockito.any(IVRWebHookResponseDto.class),
				Mockito.any(IVRUserSession.class), Mockito.any(CurrentAssignmentResponseDto.class)))
				.thenReturn(IVRConstants.SUCCESS);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID271Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID271Code_HookCode2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID271";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setCanBePagedMobile(false);

		session.setCurrentAssignmentResponse(currentAssignment);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(session);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID271Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID271Code_HookCode1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID271";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setCanBePagedMobile(false);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID271Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID250Code_HookCode_1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID250";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setCanBePagedMobile(false);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID250Code(sessionId, currentState, 1);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID250Code_HookCode1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID250";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setCanBePagedMobile(true);

		session.setCurrentAssignmentResponse(currentAssignment);

		List<IVRParameter> cablePairStatusList = new ArrayList<IVRParameter>();

		IVRParameter parameter = new IVRParameter();

		parameter.setData("CT");

		cablePairStatusList.add(parameter);

		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).setCKID(null);

		when(mockIVRLfacsServiceHelper.getCablePairStatus(Mockito.any(CurrentAssignmentResponseDto.class),
				Mockito.any(List.class))).thenReturn(cablePairStatusList);

		when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(session);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID250Code(sessionId, currentState, 1);

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID250Code_HookCode2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID250";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setCanBePagedMobile(true);

		session.setCurrentAssignmentResponse(currentAssignment);

		when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(session);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID250Code(sessionId, currentState, 2);

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID250Code_HookCode4() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID250";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setCanBePagedMobile(true);

		session.setCurrentAssignmentResponse(currentAssignment);

		when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(session);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID250Code(sessionId, currentState, 4);

		assertEquals(HOOK_RETURN_4, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID250Code_HookCode7() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID271";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setCanBePagedMobile(true);

		session.setCurrentAssignmentResponse(currentAssignment);

		List<IVRParameter> cablePairStatusList = new ArrayList<IVRParameter>();

		IVRParameter parameter = new IVRParameter();

		parameter.setData("SPR");

		cablePairStatusList.add(parameter);

		when(mockIVRLfacsServiceHelper.getCablePairStatus(Mockito.any(CurrentAssignmentResponseDto.class),
				Mockito.any(List.class))).thenReturn(cablePairStatusList);

		when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(session);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID250Code(sessionId, currentState, 7);

		assertEquals(HOOK_RETURN_7, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID250Code_HookCode8() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FID271";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);

		session.setCanBePagedMobile(true);

		List<IVRParameter> ivrParameterList = new ArrayList<IVRParameter>();

		IVRParameter ivrParameter = new IVRParameter();

		ivrParameter.setData("1");

		ivrParameterList.add(ivrParameter);

		session.setCurrentAssignmentResponse(currentAssignment);

		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).setCKID(null);

		when(mockIVRLfacsServiceHelper.getCablePairStatus(Mockito.any(CurrentAssignmentResponseDto.class),
				Mockito.any(List.class))).thenReturn(ivrParameterList);

		when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(session);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID250Code(sessionId, currentState, 8);

		assertEquals(HOOK_RETURN_8, actualResponse.getHookReturnCode());
	}

	

	@Test
	void testProcessFID560Code_InvalidEnquiryType() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID560";

		IVRUserSession userSession = new IVRUserSession();
		userSession.setFacsInqType(null);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID560Code(sessionId, currentState);

		assertEquals("Enquiry Type Not Found", actualResponse.getHookReturnMessage());
	}

	@Test
	void testProcessFID025Code_InvalidMessageStatus() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID025";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("2");
		IVRUserSession userSession = new IVRUserSession();

		currentAssignmentResponse.setMessageStatus(null);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID025Code(sessionId, currentState, null);

		assertEquals("Message Status Not Found", actualResponse.getHookReturnMessage());
	}

	@Test
	void testProcessFID025Code_InvalidCurrentAssignment() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID025";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("2");
		IVRUserSession userSession = new IVRUserSession();

		currentAssignmentResponse.setMessageStatus(null);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID025Code(sessionId, currentState, null);

		assertEquals("Current Assignment not found", actualResponse.getHookReturnMessage());
	}

	@Test
	void testProcessFID274Code_HookCode1() {

		String sessionId = "session123";
		String currentState = "FID274";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setCanBePagedMobile(true);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID274Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID274Code_HookCode2() {

		String sessionId = "session123";
		String currentState = "FID274";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setCanBePagedMobile(false);
		userSession.setCanBePagedEmail(false);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID274Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID274Code_InvalidSession() {

		String sessionId = "session123";
		String currentState = "FID274";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID274Code(sessionId, currentState);

		assertEquals(IVRConstants.INVALID_SESSION_ID, actualResponse.getHookReturnMessage());
	}

	@Test
	void testProcessFID300Code_HookCode3() throws JsonMappingException, JsonProcessingException, HttpTimeoutException {

		String sessionId = "session123";

		String currentState = "FID300";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);

		session.setCanBePagedMobile(true);

		session.setCentralOfficeEquipmentResponse("");

		String mockLosDbJsonString = "";

		session.setLosDbResponse(mockLosDbJsonString);

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(tnInfoResponse);

		when(mockIVRLfacsServiceHelper.buildCentralOfcEquipmentInqRequest("23423", null, null)).thenReturn(coeRequest);

		when(mockObjectMapper.readValue(eq(centralOffice), eq(CentralOfficeEquipmentResponseDto.class)))
				.thenReturn(centralOfficeResponseDto);

		when(mockIvrHttpClient.httpPostApiCall(any(), any(), any(), anyString()))
				.thenReturn(new IVRHttpResponseDto(1, centralOffice));

		when(mockIvrHttpClient.cleanResponseString(any())).thenReturn(centralOffice);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(session);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID300Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID300Code_HookCode8() throws JsonMappingException, JsonProcessingException, HttpTimeoutException {

		String sessionId = "session123";

		String currentState = "FID300";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);

		session.setCanBePagedMobile(false);

		session.setCentralOfficeEquipmentResponse("");

		String mockLosDbJsonString = "";

		session.setLosDbResponse(mockLosDbJsonString);

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(tnInfoResponse);

		when(mockIVRLfacsServiceHelper.buildCentralOfcEquipmentInqRequest("23423", null, null)).thenReturn(coeRequest);

		when(mockObjectMapper.readValue(eq(centralOffice), eq(CentralOfficeEquipmentResponseDto.class)))
				.thenReturn(centralOfficeResponseDto);

		when(mockIvrHttpClient.httpPostApiCall(any(), any(), any(), anyString()))
				.thenReturn(new IVRHttpResponseDto(1, centralOffice));

		when(mockIvrHttpClient.cleanResponseString(any())).thenReturn(centralOffice);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(session);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID300Code(sessionId, currentState);

		assertEquals(HOOK_RETURN_8, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID291Code_HookCode2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID291";

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setLosDbResponse("losDb response");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(userSession.getLosDbResponse()))
				.thenReturn(losDbResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID291Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID291Code_HookCode1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID291";

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn(null);
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setLosDbResponse("losDb response");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(userSession.getLosDbResponse()))
				.thenReturn(losDbResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID291Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID291Code_InvalidLosDBResponse() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID291";

		TNInfoResponse losDbResponse = null;

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setLosDbResponse("losDb response");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(userSession.getLosDbResponse()))
				.thenReturn(losDbResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID291Code(sessionId, currentState);

		assertEquals("LossDB response Not Found", actualResponse.getHookReturnMessage());
	}

	@Test
	void testProcessFID630Code_HookCode1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID630";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_1);
		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setCanBePagedMobile(true);
		userSession.setAdditionalLinesResponse("1");

		AdditionalLinesReportResponseDto addLinesResp = new AdditionalLinesReportResponseDto();

		List<String> tnList = new ArrayList<String>();

		tnList.add("special");
		tnList.add("special");

		addLinesResp.setReturnDataSet(tnList);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);

		when(mockObjectMapper.readValue(userSession.getAdditionalLinesResponse(),
				AdditionalLinesReportResponseDto.class)).thenReturn(addLinesResp);

//		when(mockIVRLfacsServiceHelper.getAdditionalLinesExceptInquiredTN(Mockito.any(IVRUserSession.class),
//				Mockito.any(List.class))).thenReturn(tnList);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID630Code(sessionId, currentState, 0);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID630Code_HookCode2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID630";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_2);
		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setCanBePagedMobile(true);
		userSession.setAdditionalLinesResponse("1");

		AdditionalLinesReportResponseDto addLinesResp = new AdditionalLinesReportResponseDto();

		List<String> tnList = new ArrayList<String>();
		tnList.add("udc ckt");
		tnList.add("udc ckt");

		addLinesResp.setReturnDataSet(tnList);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);

		when(mockObjectMapper.readValue(userSession.getAdditionalLinesResponse(),
				AdditionalLinesReportResponseDto.class)).thenReturn(addLinesResp);

//		when(mockIVRLfacsServiceHelper.getAdditionalLinesExceptInquiredTN(Mockito.any(IVRUserSession.class),
//				Mockito.any(List.class))).thenReturn(tnList);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID630Code(sessionId, currentState, 0);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID630Code_HookCode3() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID630";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_3);
		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setCanBePagedMobile(true);
		userSession.setAdditionalLinesResponse("1");

		AdditionalLinesReportResponseDto addLinesResp = new AdditionalLinesReportResponseDto();

		List<String> tnList = new ArrayList<String>();
		tnList.add("112 321-1234");
		tnList.add("112 321-1235");

		addLinesResp.setReturnDataSet(tnList);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);

		when(mockObjectMapper.readValue(userSession.getAdditionalLinesResponse(),
				AdditionalLinesReportResponseDto.class)).thenReturn(addLinesResp);

//		when(mockIVRLfacsServiceHelper.getAdditionalLinesExceptInquiredTN(Mockito.any(IVRUserSession.class),
//				Mockito.any(List.class))).thenReturn(tnList);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID630Code(sessionId, currentState, 0);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID630Code_HookCode4() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID630";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_4);
		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setCanBePagedMobile(true);
		userSession.setAdditionalLinesResponse("1");
		userSession.setAdditionalLinesCounter(2);

		AdditionalLinesReportResponseDto addLinesResp = new AdditionalLinesReportResponseDto();

		List<String> tnList = new ArrayList<String>();
		tnList.add("112 321-1234");

		addLinesResp.setReturnDataSet(tnList);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);

		when(mockObjectMapper.readValue(userSession.getAdditionalLinesResponse(),
				AdditionalLinesReportResponseDto.class)).thenReturn(addLinesResp);

//		when(mockIVRLfacsServiceHelper.getAdditionalLinesExceptInquiredTN(Mockito.any(IVRUserSession.class),
//				Mockito.any(List.class))).thenReturn(tnList);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID630Code(sessionId, currentState, 6);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_4, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID273Code_HookCode3() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID273";

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		IVRUserSession session = new IVRUserSession();
		session.setSessionId(sessionId);
		session.setCanBePagedMobile(true);
		session.setCanBePagedEmail(true);
		session.setCurrentAssignmentResponse(currentAssignment);
		session.setLosDbResponse("losDb response");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(session.getLosDbResponse()))
				.thenReturn(losDbResponse);

		when(mockObjectMapper.readValue(session.getMultipleAppearanceResponse(), MultipleAppearanceResponseDto.class))
				.thenReturn(multipleAppearanceResponseDto);

		when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		when(mockIVRLfacsPagerText.getPageMultipleAppearences(Mockito.any(IVRUserSession.class), Mockito.any(CurrentAssignmentResponseDto.class),
				Mockito.any(MultipleAppearanceResponseDto.class),Mockito.anyInt())).thenReturn("");

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID273Code(sessionId, currentState, null);

		assertEquals(HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID273Code_HookCode2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID273";

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		IVRUserSession session = new IVRUserSession();
		session.setSessionId(sessionId);
		session.setCanBePagedEmail(false);
		session.setCanBePagedMobile(false);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(session);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(session.getLosDbResponse()))
				.thenReturn(losDbResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID273Code(sessionId, currentState, null);

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID273Code_HookCode1() {

		String sessionId = "session123";

		String currentState = "FID273";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setCanBePagedEmail(false);
		session.setCanBePagedMobile(false);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID273Code(sessionId, currentState, null);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID272CodeRetLoopAssign()
			throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "FID055";

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

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(mockIVRLfacsServiceHelper.buildRetriveMainInqRequest( anyString(),anyString(),anyString(),any(),any(),anyInt())).thenReturn(mockRequest);
		
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		
		when(mockIVRLfacsServiceHelper.callSparePairInquiryLfacs(jsonRequestString, mockSession))
				.thenReturn(mockLfacsResponse);
		
		
		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);
		
		when(mockObjectMapper.readValue(mockLfacsResponse, RetrieveMaintenanceChangeTicketResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID272Code(sessionId, currentState,
				userInputDTMFList);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}
	
	@Mock SparePairPageBuilder mockSparePairPageBuilder;
	@Test
	void testProcessFID272CodeRetLoopAssign_3()
			throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "FID055";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(true);
		mockSession.setCanBePagedEmail(true);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

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
		mockSession.setRtrvLoopAssgMsgName(mockLosDbJsonString);
		mockSession.setCanBePagedMobile(true);
		mockSession.setCutPageSent(false);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(mockIVRLfacsServiceHelper.buildRetriveLoopAssignInqRequest(anyString(), anyString(),
				anyString(),any(), any(), anyInt())).thenReturn(mockRequest);
		
		when(mockIVRLfacsServiceHelper.getSegmentFromCablePair(any(), any(), any())).thenReturn(1);
		when(mockIVRLfacsServiceHelper.isSeviceOrder(any())).thenReturn(true);
		
		when(mockIVRLfacsServiceHelper.buildRetriveLoopAssignInqRequest(anyString(), anyString(), anyString(), any(), any(), anyInt())
				).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		
		when(mockIVRLfacsServiceHelper.callSparePairLoopAssignInquiryLfacs(jsonRequestString, mockSession))
				.thenReturn(mockLfacsResponse);

		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, RetrieveLoopAssignmentResponse.class))
				.thenReturn(mockResponse);
		
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID272Code(sessionId, currentState,
				userInputDTMFList);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID272CodeRetLoopAssign_2()
			throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException {

		String sessionId = "session123";
		String currentState = "FID055";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(false);
		mockSession.setCanBePagedEmail(true);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		String mockLosDbJsonString = "mock-losdb-response-string";

		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvLoopAssgMsgName(mockLosDbJsonString);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
		.thenReturn(currentAssignmentResponse);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID272Code(sessionId, currentState,
				userInputDTMFList);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}
}
