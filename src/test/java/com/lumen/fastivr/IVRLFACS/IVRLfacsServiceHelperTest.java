package com.lumen.fastivr.IVRLFACS;

import static com.lumen.fastivr.IVRUtils.IVRConstants.INQUIRY_BY_CABLE_PAIR;
import static com.lumen.fastivr.IVRUtils.IVRConstants.IVRFAILURE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.IVRSUCCESS;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_2;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.CurrentAssignmentRequestTnDto;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.LOOP;
import com.lumen.fastivr.IVRDto.NETMessagingRequestDto;
import com.lumen.fastivr.IVRDto.ReturnDataSet;
import com.lumen.fastivr.IVRDto.SEG;
import com.lumen.fastivr.IVRDto.LOSDB.CTelephone;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.CandidatePairInfo;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetMaintChangeTicketReturnDataSet;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketRequest;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketResponse;
import com.lumen.fastivr.IVRDto.additionalLines.AdditionalLinesReportRequestDto;
import com.lumen.fastivr.IVRDto.additionalLines.AdditionalLinesReportResponseDto;
import com.lumen.fastivr.IVRDto.common.IVRHttpResponseDto;
import com.lumen.fastivr.IVRDto.multipleappearance.MultipleAppearanceRequestDto;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.LoopAssignCandidatePairInfo;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetLoopAssigReturnDataSet;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentRequest;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentResponse;
import com.lumen.fastivr.IVREntity.TNInfo;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.IVRUtils.IVRLfacsConstants;
import com.lumen.fastivr.httpclient.IVRHttpClient;

/**
 * 
 */
@ExtendWith(MockitoExtension.class)
public class IVRLfacsServiceHelperTest {
  
 	@Mock
	private IVRLOSDBManager mockIvrlosdbManager;
	
	@Mock
	private ObjectMapper mockObjectMapper;
	
	@Mock
	private CTelephoneBuilder mockBuilder;

	@InjectMocks
	private IVRLfacsServiceHelper ivrLfacsServiceHelper;
	
	@Mock HttpResponse<String> mockHttpResponse;
	@Mock HttpClient mockHttpClient;
	@Mock IVRCacheService mockCacheService;
	@Mock LfacsValidation mockLfacsValidation;
	@Mock IVRHttpClient mockIvrHttpClient;
	
	CurrentAssignmentResponseDto currentAssignmentResponseDto=null;
	String currentAssignment = null;

	@BeforeEach
	void setUp() throws Exception {
		
		currentAssignment = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":[{\"UTYP\":\"10151\",\"UID\":\"QUINCE ST NW\",\"STYP\":\"COON RPDS\",\"SID\":\"MN\",\"ETYP\":\"MN\",\"EID\":\"MN\"}],\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"ONE1\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"R,43\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"BK,324\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}";
		
		currentAssignmentResponseDto = new ObjectMapper().readValue(currentAssignment,
				CurrentAssignmentResponseDto.class);
	}
	
	@Test
	void testIsCircuitWithNoTN() {
		
		String sessionId = "session123";
		
		String currentState = "FID400";
		
		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		
		IVRUserSession userSession = new IVRUserSession();
		
		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"424\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"1678\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"UNK,O-W+Y-O\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}");
		
		try {
			currentAssignmentResponseDto = new ObjectMapper()
					.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(true, ivrLfacsServiceHelper.isCircuitWithNoTN(currentAssignmentResponseDto, response));
	}
	
	@Test
	void testIsSpecialCircuit() {
		
		String sessionId = "session123";
		
		String currentState = "FID400";
		
		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		
		IVRUserSession userSession = new IVRUserSession();
		
		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"424\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"1678\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"UNK,O-W+Y-O\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}");
		
		try {
			currentAssignmentResponseDto = new ObjectMapper()
					.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(false, ivrLfacsServiceHelper.isSpecialCircuit(currentAssignmentResponseDto, response));
	}
	
	@Test
	void testIsSpecialCircuitTrue() {
		
		String sessionId = "session123";
		
		String currentState = "FID400";
		
		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		
		IVRUserSession userSession = new IVRUserSession();
		
		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-42299\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"424\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"1678\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"UNK,O-W+Y-O\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}");
		
		try {
			currentAssignmentResponseDto = new ObjectMapper()
					.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(true, ivrLfacsServiceHelper.isSpecialCircuit(currentAssignmentResponseDto, response));
	}
	
	@Test
	void testIsUDCCircuit() {
		
		String sessionId = "session123";
		
		String currentState = "FID400";
		
		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		
		IVRUserSession userSession = new IVRUserSession();
		
		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"UDC\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"424\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"1678\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"UNK,O-W+Y-O\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}");
		
		try {
			currentAssignmentResponseDto = new ObjectMapper()
					.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(true, ivrLfacsServiceHelper.isUdcCircuit(currentAssignmentResponseDto, response));
	}
	
	@Test
	void testIsCircuitWithNoTNFalse() {
		
		String sessionId = "session123";
		
		String currentState = "FID400";
		
		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		
		assertEquals(false, ivrLfacsServiceHelper.isCircuitWithNoTN(currentAssignmentResponseDto, response));
	}
	
	@Test
	void testIsSpecialCircuitFalse() {
		
		String sessionId = "session123";
		
		String currentState = "FID400";
		
		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		
		assertEquals(false, ivrLfacsServiceHelper.isSpecialCircuit(currentAssignmentResponseDto, response));
	}
	
	@Test
	void testIsUDCCircuitFalse() {
		
		String sessionId = "session123";
		
		String currentState = "FID400";
		
		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		
		assertEquals(false, ivrLfacsServiceHelper.isUdcCircuit(currentAssignmentResponseDto, response));
	}
	
	@Test
	void testgetColorCodeF1() {
		
		String sessionId = "session123";
		
		String currentState = "FID420";
		
		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_5);
		response.setHookReturnMessage(IVRConstants.WHITE_TRACER_MSG);
		
		IVRUserSession userSession = new IVRUserSession();
		
		userSession.setSessionId(sessionId);
		userSession.setSegmentRead("");
		userSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"W,23\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"W,43\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"UNK,O-W+Y-O\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}");
		
		try {
			currentAssignmentResponseDto = new ObjectMapper()
					.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceHelper.getColourCode(response, currentAssignmentResponseDto, userSession);
		
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_5, actualResponse.getHookReturnCode());
	}
	
	@Test
	void testgetColorCodeF2() {
		
		String sessionId = "session123";
		
		String currentState = "FID420";
		
		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_6);
		response.setHookReturnMessage(IVRConstants.RED_TRACER_MSG);
		
		IVRUserSession userSession = new IVRUserSession();
		
		userSession.setSessionId(sessionId);
		userSession.setSegmentRead("F1");
		userSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"W,23\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"R,43\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"R,324\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}");
		
		try {
			currentAssignmentResponseDto = new ObjectMapper()
					.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceHelper.getColourCode(response, currentAssignmentResponseDto, userSession);
		
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_6, actualResponse.getHookReturnCode());
	}
	
	@Test
	void testgetColorCodeF3() {
		
		String sessionId = "session123";
		
		String currentState = "FID420";
		
		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_7);
		response.setHookReturnMessage(IVRConstants.RED_TRACER_MSG);
		
		IVRUserSession userSession = new IVRUserSession();
		
		userSession.setSessionId(sessionId);
		userSession.setSegmentRead("F2");
		userSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"W,23\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"R,43\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"BK,324\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}");
		
		try {
			currentAssignmentResponseDto = new ObjectMapper()
					.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceHelper.getColourCode(response, currentAssignmentResponseDto, userSession);
		
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_7, actualResponse.getHookReturnCode());
	}
	
	@Test
	void testgetColorCode_COMMA() {
		
		String sessionId = "session123";
		
		String currentState = "FID420";
		
		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_1);
		response.setHookReturnMessage(IVRConstants.BINDING_POST);
		
		IVRUserSession userSession = new IVRUserSession();
		
		userSession.setSessionId(sessionId);
		userSession.setSegmentRead("");
		userSession.setCurrentAssignmentResponse("{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"12\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"R,43\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"BK,324\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}");
		
		try {
			currentAssignmentResponseDto = new ObjectMapper()
					.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceHelper.getColourCode(response, currentAssignmentResponseDto, userSession);
		
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}
	
	@Test
	void testgetColorCodeF1_EMPTY() {
		
		String sessionId = "session123";
		
		String currentState = "FID420";
		
		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_2);
		response.setHookReturnMessage(IVRConstants.NO_BINDING);
		
		IVRUserSession userSession = new IVRUserSession();
		
		userSession.setSessionId(sessionId);
		userSession.setSegmentRead("");
		userSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"R,43\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"BK,324\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}");
		
		try {
			currentAssignmentResponseDto = new ObjectMapper()
					.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceHelper.getColourCode(response, currentAssignmentResponseDto, userSession);
		
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}
	
	@Test
	void testgetColorCodeF1_TONE() {
		
		String sessionId = "session123";
		
		String currentState = "FID420";
		
		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_4);
		response.setHookReturnMessage(IVRConstants.BINDING_POST_TONE);
		
		IVRUserSession userSession = new IVRUserSession();
		
		userSession.setSessionId(sessionId);
		userSession.setSegmentRead("");
		userSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"TONE\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"R,43\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"BK,324\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}");
		
		try {
			currentAssignmentResponseDto = new ObjectMapper()
					.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceHelper.getColourCode(response, currentAssignmentResponseDto, userSession);
		
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_4, actualResponse.getHookReturnCode());
	}
	
	@Test
	void testgetColorCodeF1_NOTHING() {
		
		String sessionId = "session123";
		
		String currentState = "FID420";
		
		CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_3);
		response.setHookReturnMessage(IVRConstants.COLOR_CODE);
		
		IVRUserSession userSession = new IVRUserSession();
		
		userSession.setSessionId(sessionId);
		userSession.setSegmentRead("");
		userSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"ONE1\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"R,43\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"BK,324\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}");
		
		try {
			currentAssignmentResponseDto = new ObjectMapper()
					.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceHelper.getColourCode(response, currentAssignmentResponseDto, userSession);
		
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}


	@Test
	void testGet10DigitTN() {
		String TN = "4567890";
		IVRUserSession session = new IVRUserSession();
		session.setNpaPrefix("123");
		String actual = ivrLfacsServiceHelper.get10DigitTN(session, TN);
		assertEquals("1234567890", actual);
	}

	@Test
	void testGetLOSDBInterfaceStatus_IVRSUCCESS() throws JsonMappingException, JsonProcessingException {
		String ckid = "1234567890";
		String responseString = "losdb-json-response";
		TNInfoResponse tnInfoResponse = new TNInfoResponse();
		TNInfo tnInfo =  new TNInfo();
		IVRUserSession session = new IVRUserSession();
		session.setSessionId("Session123");
		CTelephone telephone = new CTelephone();
		telephone.setNpa("123");
		telephone.setNxx("456");
		telephone.setLineNumber("7890");
		
		when(mockIvrlosdbManager.getTnInfo(telephone, session.getSessionId())).thenReturn(responseString);
		when(mockObjectMapper.readValue(responseString, TNInfoResponse.class)).thenReturn(tnInfoResponse);
		when(mockIvrlosdbManager.matchServiceProviderId(tnInfoResponse)).thenReturn(true);
		
		String ivrStatus = ivrLfacsServiceHelper.getLOSDBInterfaceStatus(session, ckid);
		assertEquals(IVRSUCCESS, ivrStatus);
	}
	
	@Test
	void testGetLOSDBInterfaceStatus_IVRSUCCESS_LOSDB_RESPONSE_NULL() throws JsonMappingException, JsonProcessingException {
		String ckid = "1234567890";
		String mockResponseString = "los-db-response";
		TNInfoResponse tnInfoResponse = new TNInfoResponse();
		TNInfo tnInfo =  new TNInfo();
		String npaPrefix = "219";
		tnInfo.setNpaPrefix(npaPrefix );
		IVRUserSession session = new IVRUserSession();
		session.setSessionId("Session123");
		CTelephone telephone = new CTelephone();
		telephone.setNpa("123");
		telephone.setNxx("456");
		telephone.setLineNumber("7890");
		
		doReturn(null).doReturn(mockResponseString).when(mockIvrlosdbManager).getTnInfo(any(CTelephone.class), anyString());
		
		when(mockIvrlosdbManager.validateNPA(telephone, session.getSessionId())).thenReturn(tnInfo);
		
		when(mockObjectMapper.readValue(mockResponseString, TNInfoResponse.class)).thenReturn(tnInfoResponse);
		when(mockIvrlosdbManager.matchServiceProviderId(tnInfoResponse)).thenReturn(true);
		
		String ivrStatus = ivrLfacsServiceHelper.getLOSDBInterfaceStatus(session, ckid);
		assertEquals(IVRSUCCESS, ivrStatus);
	}
	
	@Test
	void testGetLOSDBInterfaceStatus_BusinessException() {
		String ckid = "1234567890";
		IVRUserSession session = new IVRUserSession();
		session.setSessionId("Session123");
		
		when(mockIvrlosdbManager.getTnInfo(any(CTelephone.class), anyString())).thenReturn(null);
		when(mockIvrlosdbManager.validateNPA(any(CTelephone.class), anyString())).thenReturn(null);
		
		String ivrStatus = ivrLfacsServiceHelper.getLOSDBInterfaceStatus(session, ckid);
		assertEquals(IVRFAILURE, ivrStatus);
	}
	
	@Test
	void testGetLOSDBInterfaceStatus_BusinessException_2() {
		String ckid = "1234567890";
		IVRUserSession session = new IVRUserSession();
		session.setSessionId("Session123");
		
		doReturn(null).doReturn(null).when(mockIvrlosdbManager).getTnInfo(any(CTelephone.class), anyString());
		when(mockIvrlosdbManager.validateNPA(any(CTelephone.class), anyString())).thenReturn(new TNInfo());
		
		String ivrStatus = ivrLfacsServiceHelper.getLOSDBInterfaceStatus(session, ckid);
		assertEquals(IVRFAILURE, ivrStatus);
	}
	
	@Test
	void testGetLOSDBInterfaceStatus_BusinessException_3() throws JsonMappingException, JsonProcessingException {
		String ckid = "1234567890";
		IVRUserSession session = new IVRUserSession();
		session.setSessionId("Session123");
		String responseString = "los-db-response";
		TNInfoResponse tnInfoResponse = new TNInfoResponse();
		
		doReturn("los-db-response").when(mockIvrlosdbManager).getTnInfo(any(CTelephone.class), anyString());
		when(mockObjectMapper.readValue(responseString , TNInfoResponse.class)).thenReturn(tnInfoResponse );
		when(mockIvrlosdbManager.matchServiceProviderId(tnInfoResponse)).thenReturn(false);
		String ivrStatus = ivrLfacsServiceHelper.getLOSDBInterfaceStatus(session, ckid);
		assertEquals(IVRFAILURE, ivrStatus);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testCallCurrentAssignmentInquiryLfacs() throws IllegalArgumentException, IllegalAccessException, InterruptedException, ExecutionException {
		String sessionId = "session123";
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		String jsonRequestString = "\"mock-request-string\"";
		String responseString = "mock-lfacs-response";
		
		// mocking
		when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
				.thenReturn(CompletableFuture.completedFuture(mockHttpResponse));
		when(mockHttpResponse.body()).thenReturn(responseString);

		Field field = ReflectionUtils.findField(IVRLfacsServiceHelper.class, "currentAssignmentUrl");
		field.setAccessible(true);
		field.set(ivrLfacsServiceHelper, "http://localhost/lfacs/currentassignment");
		
		String actualJsonStr = ivrLfacsServiceHelper.callCurrentAssignmentInquiryLfacs(jsonRequestString,
				mockSession);
		assertEquals(responseString, actualJsonStr);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testcallSparePairInquiryLfacs() throws IllegalArgumentException, IllegalAccessException, InterruptedException, ExecutionException, HttpTimeoutException {
		String sessionId = "session123";
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		String jsonRequestString = "\"mock-request-string\"";
		String responseString = "mock-lfacs-response";

		IVRHttpResponseDto value = new IVRHttpResponseDto();
		value.setResponseBody(responseString);
		
		when(mockIvrHttpClient.httpPostApiCall(anyString(), anyString(), anyString(), anyString())).thenReturn(value);
		when(mockIvrHttpClient.cleanResponseString(responseString)).thenReturn(responseString);
		
		Field field = ReflectionUtils.findField(IVRLfacsServiceHelper.class, "retrieveMaintChangeUrl");
		field.setAccessible(true);
		field.set(ivrLfacsServiceHelper, "http://localhost/lfacs/retrieveMaintChangeUrl");
		
		String actualJsonStr = ivrLfacsServiceHelper.callSparePairInquiryLfacs(jsonRequestString,
				mockSession);
		assertEquals(responseString, actualJsonStr);
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	void testcallSparePairLoopAssignInquiryLfacs() throws IllegalArgumentException, IllegalAccessException, InterruptedException, ExecutionException, HttpTimeoutException {
		String sessionId = "session123";
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		String jsonRequestString = "\"mock-request-string\"";
		String responseString = "mock-lfacs-response";
		
		IVRHttpResponseDto value = new IVRHttpResponseDto();
		value.setResponseBody(responseString);
		
		when(mockIvrHttpClient.httpPostApiCall(anyString(), anyString(), anyString(), anyString())).thenReturn(value);
		when(mockIvrHttpClient.cleanResponseString(responseString)).thenReturn(responseString);

		Field field = ReflectionUtils.findField(IVRLfacsServiceHelper.class, "retrieveLoopAssignUrl");
		field.setAccessible(true);
		field.set(ivrLfacsServiceHelper, "http://localhost/lfacs/retrieveLoopAssignUrl");
		
		String actualJsonStr = ivrLfacsServiceHelper.callSparePairLoopAssignInquiryLfacs(jsonRequestString,
				mockSession);
		assertEquals(responseString, actualJsonStr);
	}
	
	@Test
	void testCleanCurrentAssignmentResponseString() {
		String lfacsResponseJson = "{\"ARTISInformation\":{\"TotalTime\":\"480\",\"OverheadTime\":\"81\"}}";
		String cleanResponse = ivrLfacsServiceHelper.cleanResponseString(lfacsResponseJson);
		assertNotNull(cleanResponse);
	}
	
	@Test
	void testBuildCurrentAssignmentInqRequest() {
		
		String sessionId = "1";
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setCable("2");
		mockSession.setPair("1");
		mockSession.setSessionId(sessionId);
		
		CurrentAssignmentRequestTnDto response = ivrLfacsServiceHelper.buildCurrentAssignmentInqRequest("123", "456", "7890", null, mockSession);
		assertEquals("456", response.getInputData().getWireCtrPrimaryNPANXX().getNpa());
	}
	
	@Test
	void testBuildCurrentAssignmentInqRequestWithCablePair() {
		
		String sessionId = "1";
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setCable("2");
		mockSession.setPair("1");
		mockSession.setSessionId(sessionId);
		mockSession.setFacsInqType(INQUIRY_BY_CABLE_PAIR);
		List<String> userInputDTMFList= new ArrayList<>();
		userInputDTMFList.add("123");
		userInputDTMFList.add("321");
		CurrentAssignmentRequestTnDto response = ivrLfacsServiceHelper.buildCurrentAssignmentInqRequest("123", "456", "7890", userInputDTMFList, mockSession);
		assertEquals("2", response.getInputData().getCurrentAssignmentInfo().getCablePair().getCa());
	}
	
	@Test
	void testExtractTNInfoFromLosDBResponse() throws JsonMappingException, JsonProcessingException {
		String responseString = "";
		TNInfoResponse response = ivrLfacsServiceHelper.extractTNInfoFromLosDBResponse(responseString);
		System.out.println(response);
	}
	
	@Test
	void testAddParamterData() {
		String data1 = "Name";
		String data2 = "Address";
		List<IVRParameter> params = new ArrayList<>();
		IVRParameter param1 = new IVRParameter();
		IVRParameter param2 = new IVRParameter();
		param1.setData(data1);
		param2.setData(data2);
		params.add(param1);
		params.add(param2);
		
		List<IVRParameter> responseList = ivrLfacsServiceHelper.addParamterData(data1, data2);
		assertEquals(data1, responseList.get(0).getData());
		assertEquals(data2, responseList.get(1).getData());
	}
	
	@Test
	void testbuildAdditionalLinesReportRequest() {
		AdditionalLinesReportRequestDto request = ivrLfacsServiceHelper.buildAdditionalLinesReportRequest("Service address", "111", "NPA", "NXX");
		assertEquals("111", request.getInputData().getLFACSEmployeeCode());
	}
	
	@Test
	void testbuildMultipleAppearanceInqRequest() {
		MultipleAppearanceRequestDto request = ivrLfacsServiceHelper.buildMultipleAppearanceInqRequest("Cable ID", "cableUnitId","111", "NPA", "NXX");
		assertEquals("111", request.getInputData().getEmployeeId());
	}
	
	@Test
	void testcheckIfServiceAddrExists_HK1() throws JsonMappingException, JsonProcessingException {
		IVRUserSession mockSession = new IVRUserSession();
		String currenAssignmentMock = "currenAssignmentMock";
		mockSession.setCurrentAssignmentResponse(currenAssignmentMock);
		CurrentAssignmentResponseDto responseDto = new CurrentAssignmentResponseDto();
		mockSession.setFacsInqType(IVRConstants.INQUIRY_BY_TN);
		
		when(mockObjectMapper.readValue(currenAssignmentMock, CurrentAssignmentResponseDto.class)).thenReturn(responseDto);
		when(mockLfacsValidation.validateServiceAddress(responseDto)).thenReturn(true);
		String hookReturnCode = ivrLfacsServiceHelper.checkIfServiceAddrExists(mockSession);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, hookReturnCode);
	}
	
	@Test
	void testcheckIfServiceAddrExists_HK2() throws JsonMappingException, JsonProcessingException {
		IVRUserSession mockSession = new IVRUserSession();
		String currenAssignmentMock = "currenAssignmentMock";
		mockSession.setCurrentAssignmentResponse(currenAssignmentMock);
		CurrentAssignmentResponseDto responseDto = new CurrentAssignmentResponseDto();
		mockSession.setFacsInqType(IVRConstants.INQUIRY_BY_CABLE_PAIR);
		
		when(mockObjectMapper.readValue(currenAssignmentMock, CurrentAssignmentResponseDto.class)).thenReturn(responseDto);
		when(mockLfacsValidation.validateServiceAddress(responseDto)).thenReturn(true);
		String hookReturnCode = ivrLfacsServiceHelper.checkIfServiceAddrExists(mockSession);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, hookReturnCode);
	}
	
	@Test
	void testcheckIfServiceAddrExists_HK0() throws JsonMappingException, JsonProcessingException {
		IVRUserSession mockSession = new IVRUserSession();
		String currenAssignmentMock = "currenAssignmentMock";
		mockSession.setCurrentAssignmentResponse(currenAssignmentMock);
		CurrentAssignmentResponseDto responseDto = new CurrentAssignmentResponseDto();
		
		when(mockObjectMapper.readValue(currenAssignmentMock, CurrentAssignmentResponseDto.class)).thenReturn(responseDto);
		when(mockLfacsValidation.validateServiceAddress(responseDto)).thenReturn(false);
		String hookReturnCode = ivrLfacsServiceHelper.checkIfServiceAddrExists(mockSession);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, hookReturnCode);
	}
	
	@Test
	void testsendTestResultToTech() throws JsonProcessingException {
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId("");
		mockSession.setCuid("");
		when(mockIvrHttpClient.buildNetRequest(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(new NETMessagingRequestDto());
		when(mockObjectMapper.writeValueAsString(any(NETMessagingRequestDto.class))).thenReturn("");
		when(mockIvrHttpClient.httpPostCall(anyString(), any(), anyString(), anyString())).thenReturn("");
		when(mockIvrHttpClient.processJsonStringNETMessaging(anyString())).thenReturn(true);
		assertTrue(ivrLfacsServiceHelper.sendTestResultToTech("", "", "", mockSession));
	}
	
	@Test
	void testCablePair() {

		List<IVRParameter> actualResponse = ivrLfacsServiceHelper.getCablePair(currentAssignmentResponseDto, 0);

		assertEquals("IPG8", actualResponse.get(0).getData());
	}
	
	@Test
	void testCablePairStatus() {

		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		List<IVRParameter> actualResponse = ivrLfacsServiceHelper.getCablePairStatus(currentAssignmentResponseDto, parameterList);

		assertEquals("WKG", actualResponse.get(0).getData());
	}
	
	@Test
	void testStatusPair_Flag() {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceHelper.getStatusPair(currentAssignmentResponseDto, response, null, true);

		assertEquals("WKG", actualResponse.getParameters().get(0).getData());
	}
	
	@Test
	void testStatusPair() {

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		
		TNInfoResponse tnInfoResponse = new TNInfoResponse();
		
		tnInfoResponse.setTn("1");

		IVRWebHookResponseDto actualResponse = ivrLfacsServiceHelper.getStatusPair(currentAssignmentResponseDto, response, tnInfoResponse, true);

		assertEquals("1", actualResponse.getParameters().get(0).getData());
	}
	
	@Test
	void testServiceAddress() {

		String actualResponse = ivrLfacsServiceHelper.getServiceAddress(currentAssignmentResponseDto);

		assertEquals("10151 QUINCE ST NW 10151 QUINCE ST NW COON RPDS MN MN MN", actualResponse);
	}
	
	@Test
	void findSparePair_HookCode8() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID525";
		String enqType="RETRIEVE_MAINT_CHANGE";
		IVRUserSession userSession = new IVRUserSession();
		userSession.setRtrvMaintChngeMsgName("abc");
		
		CandidatePairInfo candidatePairInfo=new CandidatePairInfo();
		candidatePairInfo.setCableId("28");
		
		List<CandidatePairInfo>listCandidatePairInfo=new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);
		
		RetMaintChangeTicketReturnDataSet returnDataSet=new RetMaintChangeTicketReturnDataSet();
		returnDataSet.setCandidatePairInfo(listCandidatePairInfo);
		
		RetrieveMaintenanceChangeTicketResponse retrieveLoopAssignmentResponse=new RetrieveMaintenanceChangeTicketResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);

		
		when(mockObjectMapper.readValue(userSession.getRtrvMaintChngeMsgName(), RetrieveMaintenanceChangeTicketResponse.class))
		.thenReturn(retrieveLoopAssignmentResponse);
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceHelper.findSparePair(sessionId, currentState,userSession,enqType);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());

	}
	
	@Test
	void findSparePair_HookCode0() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID525";
		String enqType="RETRIEVE_MAINT_CHANGE";
		IVRUserSession userSession = new IVRUserSession();
		userSession.setRtrvMaintChngeMsgName("abc");
		
		CandidatePairInfo candidatePairInfo=new CandidatePairInfo();
		candidatePairInfo.setCableId("28");
		candidatePairInfo.setPairStatus("CT");
		
		List<CandidatePairInfo>listCandidatePairInfo=new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);
		
		RetMaintChangeTicketReturnDataSet returnDataSet=new RetMaintChangeTicketReturnDataSet();
		returnDataSet.setCandidatePairInfo(listCandidatePairInfo);
		
		RetrieveMaintenanceChangeTicketResponse retrieveLoopAssignmentResponse=new RetrieveMaintenanceChangeTicketResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);

		
		when(mockObjectMapper.readValue(userSession.getRtrvMaintChngeMsgName(), RetrieveMaintenanceChangeTicketResponse.class))
		.thenReturn(retrieveLoopAssignmentResponse);
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceHelper.findSparePair(sessionId, currentState,userSession,enqType);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, actualResponse.getHookReturnCode());

	}
	
	@Test
	void findSparePair_HookCode7() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID525";
		String enqType="RETRIEVE_MAINT_CHANGE";
		IVRUserSession userSession = new IVRUserSession();
		userSession.setRtrvMaintChngeMsgName("abc");
	
		RetMaintChangeTicketReturnDataSet returnDataSet=new RetMaintChangeTicketReturnDataSet();
		returnDataSet.setCandidatePairInfo(null);
		
		RetrieveMaintenanceChangeTicketResponse retrieveLoopAssignmentResponse=new RetrieveMaintenanceChangeTicketResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);

		when(mockObjectMapper.readValue(userSession.getRtrvMaintChngeMsgName(), RetrieveMaintenanceChangeTicketResponse.class))
		.thenReturn(retrieveLoopAssignmentResponse);
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceHelper.findSparePair(sessionId, currentState,userSession,enqType);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_7, actualResponse.getHookReturnCode());
		
		RetrieveMaintenanceChangeTicketResponse retrieveLoopAssignmentResponseNew=new RetrieveMaintenanceChangeTicketResponse();
		retrieveLoopAssignmentResponseNew.setReturnDataSet(null);

		when(mockObjectMapper.readValue(userSession.getRtrvMaintChngeMsgName(), RetrieveMaintenanceChangeTicketResponse.class))
		.thenReturn(retrieveLoopAssignmentResponseNew);
		
		IVRWebHookResponseDto actualResponseNew = ivrLfacsServiceHelper.findSparePair(sessionId, currentState,userSession,enqType);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_6, actualResponseNew.getHookReturnCode());

	}
	
	@Test
	void testRetrieveMaintenanceChangeTicketRequest() {
		IVRUserSession session = new IVRUserSession();
		session.setFacsInqType(IVRConstants.INQUIRY_BY_CABLE_PAIR);
		session.setCable("CA");
		session.setPair("PA");
		CurrentAssignmentResponseDto currentAssignmentResponse=new CurrentAssignmentResponseDto();
		SEG seg=new SEG();
		seg.setCA("");
		seg.setPR("");
		List<SEG>segList=new ArrayList<>();
		segList.add(seg);
		
		LOOP loop=new LOOP();
		loop.setSEG(segList);
		loop.setTID("DPAAB");
		
		List<LOOP>loopList=new ArrayList<>();
		loopList.add(loop);
		
		ReturnDataSet returnDataSet=new ReturnDataSet();
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);
		
		RetrieveMaintenanceChangeTicketRequest response = ivrLfacsServiceHelper
				.buildRetriveMainInqRequest("123", "456", "7890", currentAssignmentResponse,session, 1);
		
		assertEquals("456", response.getInputData().getWireCtrPrimaryNPANXX().getNpa());
	}
	
	@Test
	void testRetrieveMaintenanceChangeTicketRequestWithCablePair() {
		IVRUserSession session = new IVRUserSession();
		session.setFacsInqType(IVRConstants.INQUIRY_BY_CABLE_PAIR);
		session.setCable("CA");
		session.setPair("PA");
		CurrentAssignmentResponseDto currentAssignmentResponse=new CurrentAssignmentResponseDto();
		SEG seg=new SEG();
		List<SEG>segList=new ArrayList<>();
		segList.add(seg);
		
		LOOP loop=new LOOP();
		loop.setSEG(segList);
		loop.setTID("DPAAB");
		
		List<LOOP>loopList=new ArrayList<>();
		loopList.add(loop);
		
		ReturnDataSet returnDataSet=new ReturnDataSet();
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);
		List<String> userInputDTMFList= new ArrayList<>();
		userInputDTMFList.add("123");
		userInputDTMFList.add("321");
	
		RetrieveMaintenanceChangeTicketRequest response = ivrLfacsServiceHelper
				.buildRetriveMainInqRequest("123", "456", "7890", currentAssignmentResponse,session, 1);
		assertEquals("CA", response.getInputData().getCableId());
	}
	
	
	@Test
	void findSparePair_retLoopAssign_HookCode8() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID525";
		String enqType="RETRIEVE_LOOP_ASSSIGN";
		IVRUserSession userSession = new IVRUserSession();
		userSession.setRtrvLoopAssgMsgName("abc");
		
		LoopAssignCandidatePairInfo candidatePairInfo=new LoopAssignCandidatePairInfo();
		candidatePairInfo.setCableId("28");
		
		List<LoopAssignCandidatePairInfo>listCandidatePairInfo=new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);
		
		RetLoopAssigReturnDataSet returnDataSet=new RetLoopAssigReturnDataSet();
		returnDataSet.setCandidatePairInfo(listCandidatePairInfo);
		
		RetrieveLoopAssignmentResponse retrieveLoopAssignmentResponse=new RetrieveLoopAssignmentResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);

		
		when(mockObjectMapper.readValue(userSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class))
		.thenReturn(retrieveLoopAssignmentResponse);
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceHelper.findSparePair(sessionId, currentState,userSession,enqType);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());

	}
	
	@Test
	void findSparePair_retLoopAssign_HookCode0() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID525";
		String enqType="RETRIEVE_LOOP_ASSSIGN";
		IVRUserSession userSession = new IVRUserSession();
		userSession.setRtrvLoopAssgMsgName("abc");
		
		LoopAssignCandidatePairInfo candidatePairInfo=new LoopAssignCandidatePairInfo();
		candidatePairInfo.setCableId("28");
		candidatePairInfo.setCandidatePairStatus("CT");
		List<LoopAssignCandidatePairInfo>listCandidatePairInfo=new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);
		
		RetLoopAssigReturnDataSet returnDataSet=new RetLoopAssigReturnDataSet();
		returnDataSet.setCandidatePairInfo(listCandidatePairInfo);
		
		RetrieveLoopAssignmentResponse retrieveLoopAssignmentResponse=new RetrieveLoopAssignmentResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);

		
		when(mockObjectMapper.readValue(userSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class))
		.thenReturn(retrieveLoopAssignmentResponse);
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceHelper.findSparePair(sessionId, currentState,userSession,enqType);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, actualResponse.getHookReturnCode());

	}
	
	@Test
	void findSparePair_retLoopAssign_HookCode7() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID525";
		String enqType="RETRIEVE_LOOP_ASSSIGN";
		IVRUserSession userSession = new IVRUserSession();
		userSession.setRtrvLoopAssgMsgName("abc");
	
		
		RetLoopAssigReturnDataSet returnDataSet=new RetLoopAssigReturnDataSet();
		returnDataSet.setCandidatePairInfo(null);
		
		RetrieveLoopAssignmentResponse retrieveLoopAssignmentResponse=new RetrieveLoopAssignmentResponse();
		retrieveLoopAssignmentResponse.setReturnDataSet(returnDataSet);
		
		when(mockObjectMapper.readValue(userSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class))
		.thenReturn(retrieveLoopAssignmentResponse);
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceHelper.findSparePair(sessionId, currentState,userSession,enqType);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_7, actualResponse.getHookReturnCode());
		
		RetrieveLoopAssignmentResponse retrieveLoopAssignmentResponseNew=new RetrieveLoopAssignmentResponse();
		retrieveLoopAssignmentResponseNew.setReturnDataSet(null);

		when(mockObjectMapper.readValue(userSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class))
		.thenReturn(retrieveLoopAssignmentResponseNew);
		
		IVRWebHookResponseDto actualResponsenew = ivrLfacsServiceHelper.findSparePair(sessionId, currentState,userSession,enqType);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_6, actualResponsenew.getHookReturnCode());

	}
	@Test
	void testRetrieveLoopAssigntRequestRequest() {
		IVRUserSession session = new IVRUserSession();
		session.setFacsInqType(IVRConstants.INQUIRY_BY_CABLE_PAIR);
		session.setCable("CA");
		session.setPair("PA");
		CurrentAssignmentResponseDto currentAssignmentResponse=new CurrentAssignmentResponseDto();
		SEG seg=new SEG();
		seg.setCA("");
		seg.setPR("");
		List<SEG>segList=new ArrayList<>();
		segList.add(seg);
		
		LOOP loop=new LOOP();
		loop.setSEG(segList);
		loop.setTID("DPAAB");
		
		List<LOOP>loopList=new ArrayList<>();
		loopList.add(loop);
		
		ReturnDataSet returnDataSet=new ReturnDataSet();
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);
		RetrieveLoopAssignmentRequest response = ivrLfacsServiceHelper
				.buildRetriveLoopAssignInqRequest("123", "456", "7890", currentAssignmentResponse,session, 1);
		
		assertEquals("456", response.getInputData().getWireCtrPrimaryNPANXX().getNpa());
	}
	
	@Test
	void testRetrieveLoopAssigntRequestWithCablePair() {
		IVRUserSession session = new IVRUserSession();
		session.setFacsInqType(IVRConstants.INQUIRY_BY_CABLE_PAIR);
		session.setCable("CA");
		session.setPair("PA");
		CurrentAssignmentResponseDto currentAssignmentResponse=new CurrentAssignmentResponseDto();
		SEG seg=new SEG();
		List<SEG>segList=new ArrayList<>();
		segList.add(seg);
		
		LOOP loop=new LOOP();
		loop.setSEG(segList);
		loop.setTID("DPAAB");
		
		List<LOOP>loopList=new ArrayList<>();
		loopList.add(loop);
		
		ReturnDataSet returnDataSet=new ReturnDataSet();
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponse.setReturnDataSet(returnDataSet);
		List<String> userInputDTMFList= new ArrayList<>();
		userInputDTMFList.add("123");
		userInputDTMFList.add("321");
		
		RetrieveLoopAssignmentRequest response = ivrLfacsServiceHelper
				.buildRetriveLoopAssignInqRequest("123", "456", "7890", currentAssignmentResponse, session, 1);
		assertEquals("CA", response.getInputData().getCableId());
	}
	
	@Test
	void testgetColourCodeRetrieveMaintChange()
	{
		String sessionId = "session123";
		String currentState = "FID535";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_2);
		
		CandidatePairInfo candidatePairInfo=new CandidatePairInfo();
		candidatePairInfo.setBindingPostColorCode("A2");
		
		List<CandidatePairInfo>candidatePairInfoList=new ArrayList<>();
		candidatePairInfoList.add(candidatePairInfo);
		
		RetMaintChangeTicketReturnDataSet retMaintChangeTicketReturnDataSet=new RetMaintChangeTicketReturnDataSet();
		retMaintChangeTicketReturnDataSet.setCandidatePairInfo(candidatePairInfoList);
		
		RetrieveMaintenanceChangeTicketResponse retrieveMaintenanceResponse=new RetrieveMaintenanceChangeTicketResponse();
		retrieveMaintenanceResponse.setReturnDataSet(retMaintChangeTicketReturnDataSet);
		
		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse("");
		userSession.setCandPairCounter(1);
		userSession.setRtrvMaintChngeMsgName("abc");
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceHelper.getColourCodeRetrieveMaintChange(response,retrieveMaintenanceResponse,userSession);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());
		
	}
	
	@Test
	void testgetColourCodeLoopAssignment()
	{
		String sessionId = "session123";
		String currentState = "FID535";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_2);
		
		LoopAssignCandidatePairInfo candidatePairInfo=new LoopAssignCandidatePairInfo();
		candidatePairInfo.setBindingPostColorCode("A2");
		
		List<LoopAssignCandidatePairInfo>candidatePairInfoList=new ArrayList<>();
		candidatePairInfoList.add(candidatePairInfo);
		
		RetLoopAssigReturnDataSet retLoopAssigReturnDataSet=new RetLoopAssigReturnDataSet();
		retLoopAssigReturnDataSet.setCandidatePairInfo(candidatePairInfoList);
		
		RetrieveLoopAssignmentResponse retrieveMaintenanceResponse=new RetrieveLoopAssignmentResponse();
		retrieveMaintenanceResponse.setReturnDataSet(retLoopAssigReturnDataSet);
		
		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);
		userSession.setCurrentAssignmentResponse("");
		userSession.setCandPairCounter(1);
		userSession.setRtrvMaintChngeMsgName("abc");
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceHelper.getColourCodeRetrieveLoopAssignment(response, retrieveMaintenanceResponse, userSession);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());
		
	}
	
	@Test
	void getCablePairStatusTest() {
		
		String statusCode1="SPR";
		String statusCode2="CF";
		String statusCode3="PCF";
		String statusCode4="XFLD";
		String statusCode5="XCON";
		String statusCode6="DEF";
		String statusCode7="CT";
		String statusCode8="RCF";
		String statusCode9 = "*RCF";
		String statusCode10= "*DEF";
		String statusCode11 = "PCOM";
		String statusCode12 = "*PCOM";
		String statusCode13 = "SCOM";
		String statusCode14 = "*SCOM";
		String statusCode15 = "*PCF";
		String statusCode16 = "*CT";
		String statusCode17 = "*CF";
		String noStatus="";
		
		String resp1=ivrLfacsServiceHelper.getCablePairStatus(statusCode1);
		assertEquals("is spair", resp1);
		
		String resp2=ivrLfacsServiceHelper.getCablePairStatus(statusCode2);
		assertEquals("is a connected facility", resp2);
		
		String resp3=ivrLfacsServiceHelper.getCablePairStatus(statusCode3);
		assertEquals("is a partially connected facility", resp3);
		
		String resp4=ivrLfacsServiceHelper.getCablePairStatus(statusCode4);
		assertEquals("is cross connected in field", resp4);
		
		String resp5=ivrLfacsServiceHelper.getCablePairStatus(statusCode5);
		assertEquals("is cross connected", resp5);
		
		String resp6=ivrLfacsServiceHelper.getCablePairStatus(statusCode6);
		assertEquals("is defective", resp6);
		
		String resp7=ivrLfacsServiceHelper.getCablePairStatus(statusCode7);
		assertEquals("is connected through to this addesss", resp7);
		
		String resp8=ivrLfacsServiceHelper.getCablePairStatus(statusCode8);
		assertEquals("is a remotely-connected facility", resp8);
		
		String resp9=ivrLfacsServiceHelper.getCablePairStatus(statusCode9);
		assertEquals("is a remotely connected facility to this address", resp9);
		
		String resp10=ivrLfacsServiceHelper.getCablePairStatus(statusCode10);
		assertEquals("is defective at this address", resp10);
		
		String resp11=ivrLfacsServiceHelper.getCablePairStatus(statusCode11);
		assertEquals("is a primary committed pair", resp11);
		
		String resp12=ivrLfacsServiceHelper.getCablePairStatus(statusCode12);
		assertEquals("is a primary committed pair at this address", resp12);
		
		String resp13=ivrLfacsServiceHelper.getCablePairStatus(statusCode13);
		assertEquals("is a secondary committed pair", resp13);
		
		String resp14=ivrLfacsServiceHelper.getCablePairStatus(statusCode14);
		assertEquals("is a secondary committed pair at this address", resp14);
		
		String resp15=ivrLfacsServiceHelper.getCablePairStatus(statusCode15);
		assertEquals("is a partially-connected facility to this address", resp15);
		
		String resp16=ivrLfacsServiceHelper.getCablePairStatus(statusCode16);
		assertEquals("is connected through to this address", resp16);
		
		String resp17=ivrLfacsServiceHelper.getCablePairStatus(statusCode17);
		assertEquals("is a connected facility to this address", resp17);
		
		String resp18=ivrLfacsServiceHelper.getCablePairStatus(noStatus);
		assertEquals("have no status", resp18);
	
		
	}
	
	@Test
	void getCutPageFormat() {
		String tn="999";
		String format=tn+": Read This if you do a CUT you Must update the LFACS database either using options 3 on FAST -OR  by calling the Assigner";
		String resp=ivrLfacsServiceHelper.getCutPageFormat(tn);
		assertEquals(format, resp);
	}
	
	@Test
	void testSparePairColourCode_HookCode7() {
		String sessionId="session123";
		String nextStae="FID535";
		
		IVRWebHookResponseDto response=new IVRWebHookResponseDto();
		response.setCurrentState(nextStae);
		response.setSessionId(sessionId);
		
		String colorCode1="3";
		String colorType1="3";
		String colorCode2="1";
		String colorCode3="4";
		String colorCode4="5";
		String colorCode5="6";
		String colorCodeNew="";
	
		IVRWebHookResponseDto actualResp1 =ivrLfacsServiceHelper.checkSparePairColourCode(colorCode1,colorType1,response);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_7, actualResp1.getHookReturnCode());
		
		IVRWebHookResponseDto actualResp2 =ivrLfacsServiceHelper.checkSparePairColourCode(colorCode2,colorType1,response);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_5, actualResp2.getHookReturnCode());
		
		IVRWebHookResponseDto actualResp3 =ivrLfacsServiceHelper.checkSparePairColourCode(colorCode3,colorType1,response);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_4, actualResp3.getHookReturnCode());
		
		IVRWebHookResponseDto actualResp4 =ivrLfacsServiceHelper.checkSparePairColourCode(colorCode4,colorType1,response);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResp4.getHookReturnCode());
		
		IVRWebHookResponseDto actualResp5 =ivrLfacsServiceHelper.checkSparePairColourCode(colorCode5,colorType1,response);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResp5.getHookReturnCode());
		
		IVRWebHookResponseDto actualRespNew =ivrLfacsServiceHelper.checkSparePairColourCode(colorCodeNew,colorType1,response);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualRespNew.getHookReturnCode());
		
	}
	
//	@Test
//	void testgetAdditionalLinesExceptInquiredTN() throws JsonMappingException, JsonProcessingException {
//		IVRUserSession session = new IVRUserSession();
//		List<String> list = new ArrayList<>();
//		List<String> resultList = ivrLfacsServiceHelper.getAdditionalLinesExceptInquiredTN(session, list);
//		assertNotNull(resultList);
//	}
	
	@Test
	void testgetNumberOfAddlLines() {
		AdditionalLinesReportResponseDto response = new AdditionalLinesReportResponseDto();
		List<String> list = new ArrayList<>();
		list.add("Inquired TN");
		list.add("Add Line1");
		response.setReturnDataSet(list);
		
		int numberOfAddlLines = ivrLfacsServiceHelper.getNumberOfAddlLines(response);
		assertEquals(2, numberOfAddlLines);
		
		list.remove(1);
		int numberOfAddlLines2 = ivrLfacsServiceHelper.getNumberOfAddlLines(response);
		assertEquals(1, numberOfAddlLines2);
	}
	
	@Test
	void getUndesirableCandPairTest() {
		CandidatePairInfo candidatePairInfo=new CandidatePairInfo();
		candidatePairInfo.setPairSelectionInfo("-");
		List<CandidatePairInfo> listCandidatePairInfo = new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);
	
		int count = ivrLfacsServiceHelper.getUndesirableCandPair("RETRIEVE_MAINT_CHANGE",listCandidatePairInfo,null);
		assertEquals(1, count);
		
		LoopAssignCandidatePairInfo loopAssignCandidatePairInfo=new LoopAssignCandidatePairInfo();
		loopAssignCandidatePairInfo.setPairSelectionField("-");
		List<LoopAssignCandidatePairInfo> listCandidatePairInfonew = new ArrayList<>();
		listCandidatePairInfonew.add(loopAssignCandidatePairInfo);
		
		int countnew = ivrLfacsServiceHelper.getUndesirableCandPair("RETRIEVE_LOOP_ASSSIGN",null,listCandidatePairInfonew);
		assertEquals(1, countnew);
		
	}
	
	@Test
	void getCandPairCTTest() {
		CandidatePairInfo candidatePairInfo=new CandidatePairInfo();
		candidatePairInfo.setPairSelectionInfo("-");
		candidatePairInfo.setPairStatus("CT");
		List<CandidatePairInfo> listCandidatePairInfo = new ArrayList<>();
		listCandidatePairInfo.add(candidatePairInfo);
	
		int count = ivrLfacsServiceHelper.getCandPairCT("RETRIEVE_MAINT_CHANGE",listCandidatePairInfo,null);
		assertEquals(1, count);
		
		LoopAssignCandidatePairInfo loopAssignCandidatePairInfo=new LoopAssignCandidatePairInfo();
		loopAssignCandidatePairInfo.setPairSelectionField("-");
		loopAssignCandidatePairInfo.setCandidatePairStatus("CT");
		List<LoopAssignCandidatePairInfo> listCandidatePairInfonew = new ArrayList<>();
		listCandidatePairInfonew.add(loopAssignCandidatePairInfo);
		
		int countnew = ivrLfacsServiceHelper.getCandPairCT("RETRIEVE_LOOP_ASSSIGN",null,listCandidatePairInfonew);
		assertEquals(1, countnew);
		
	}
	
}
