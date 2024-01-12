package com.lumen.fastivr.IVRLFACS;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.InputData;
import com.lumen.fastivr.IVRDto.LSTFN;
import com.lumen.fastivr.IVRDto.ReturnDataSet;
import com.lumen.fastivr.IVRDto.SO;
import com.lumen.fastivr.IVRDto.LOSDB.CTelephone;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.defectivepairs.CablePairRange;
import com.lumen.fastivr.IVRDto.defectivepairs.DefectivePairDetailsT;
import com.lumen.fastivr.IVRDto.defectivepairs.DefectivePairsInputData;
import com.lumen.fastivr.IVRDto.defectivepairs.DefectivePairsRequestDto;
import com.lumen.fastivr.IVRDto.defectivepairs.DefectivePairsResponseDto;
import com.lumen.fastivr.IVRDto.defectivepairs.DefectivePairsReturnDataSet;
import com.lumen.fastivr.IVREntity.TNInfo;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;

@ExtendWith(MockitoExtension.class)
public class IVRLfacsPagerTextFormationTest {

	@Mock
	private ObjectMapper mockObjectMapper;
	
	@Mock
	private IVRLfacsServiceHelper mockIVRLfacsServiceHelper;
	
	@InjectMocks
	private IVRLfacsPagerTextFormation ivrLfacsPagerTextFormation;
	
	CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
	
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
		
		currentAssignment = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"TID\":null,\"CKID2\":null,\"CKID3\":null,\"TEA\":null,\"PORT\":null,\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"WOL\":null,\"ADL\":null,\"PTP\":null,\"OWS\":null,\"SSC\":null,\"RTF\":null,\"RLTNF\":null,\"TSP\":null,\"SSP\":null,\"SSM\":null,\"ESL\":null,\"MKSG\":null,\"ACCT\":null,\"SUS\":null,\"ADSR\":null,\"SUBL\":null,\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"POS\":null,\"JACK\":null,\"WW\":null,\"TASRMK\":null,\"TFRMK\":null,\"EXK\":null,\"INVU\":null,\"POUT\":null,\"RTNN\":null,\"LPNAME\":null,\"DAPROV\":null,\"SMSC\":null,\"DCAPR\":null,\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"SUPL\":[{\"UTYP\":\"10151\",\"UID\":\"QUINCE ST NW\",\"STYP\":\"COON RPDS\",\"SID\":\"MN\",\"ETYP\":\"MN\",\"EID\":\"MN\"}],\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"TEC\":null,\"XRST\":null,\"PTR\":\"10000P:751\",\"RT\":null,\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\",\"RSTTE\":null,\"RSTLU\":null,\"RMK0TE\":null,\"RMK0LU\":null,\"BSTE\":null,\"BSTE2\":null,\"MISCLU\":null,\"PNDLPS\":null,\"PNDORD\":null}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"NLI\":null,\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\",\"DSP\":null}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"24\",\"LSTAT\":\"WKG\",\"BP\":\"ONE1\",\"OBP\":null,\"LPORG\":null,\"RLOE\":\"BSI2IP\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"RT 9902 NW REDWOOD ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"R,43\",\"OBP\":\"1278\",\"LPORG\":null,\"RLOE\":\"BBGIGO\",\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RT\":null,\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":\"DSLAM 10002 NW PALM ST\",\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":null,\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"BB\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"792\",\"LSTAT\":\"WKG\",\"BP\":\"BK,324\",\"OBP\":\"792\",\"LPORG\":null,\"RLOE\":null,\"RLOA\":null,\"RLOC\":null,\"LOTI\":null,\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RT\":null,\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"TEC\":null,\"RELAYRACK\":null,\"PORT\":null,\"MODEL\":null,\"COMM\":null,\"UF\":null,\"SC\":null,\"TETE\":null,\"CQ\":null,\"DEF\":null,\"DEFDSpecified\":false,\"CTT\":null,\"DEFL\":null,\"LT\":null,\"LNOP\":null,\"SYSTP\":null,\"PGSNO\":null,\"FLDLTS\":null,\"COLTS\":null,\"CNST\":null,\"ORIG\":null,\"MPROV\":null,\"MCLLI\":null,\"MLOC\":null,\"MCA\":null,\"MPR\":null,\"RLA\":null,\"SDP\":null,\"TSP\":null,\"SERIALNO\":null,\"INDEXNUM\":null,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"EWOEWO\":null,\"EWOID\":null,\"EWODD\":null,\"LSTID\":null,\"SOLST\":null,\"SOITM\":null,\"SOLSTDD\":null,\"RSVINFO\":null,\"RSVDATSpecified\":false,\"RSVRMK\":null,\"RSTTE\":null,\"PERM\":null,\"XRST\":null,\"RMK0TE\":null,\"RMK0PR\":null,\"STRLOC\":null,\"CQC\":\"Z6\",\"CDC\":null,\"ASGBPR\":null,\"ASBPSTAT\":null,\"PGSTP\":null,\"LTS\":null,\"DLE\":null,\"TSI\":null,\"DLERMK\":null,\"DLERST\":null,\"DLEONU\":null,\"ONURST\":null,\"ONUXRST\":null,\"ONURMK\":null,\"TFCA1\":null,\"TFCA2\":null,\"TFPR1\":null,\"TFPR2\":null,\"FICTMED1\":null,\"FICTMED2\":null,\"FICTEA1\":null,\"FICTEA2\":null,\"FICTYPE1\":null,\"FICTYPE2\":null,\"ABPRSVINFO\":null,\"ABPRSVDAT\":null,\"ABPRSVRMK\":null,\"ASGBP\":null,\"TF1\":null}],\"MORESO\":null,\"MORESOLP\":null,\"SO\":null}],\"PORT1\":null},\"RequestId\":\"FASFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"1\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\",\"HostErrorList\":null},\"ARTISInformation\":{\"TotalTime\":\"419\",\"OverheadTime\":\"92\"},\"CompletedTimeStamp\":\"2023-09-12T09:40:36.75-05:00\",\"CompletedTimeStampSpecified\":true}";
		
		currentAssignmentResponseDto = new ObjectMapper().readValue(currentAssignment,
				CurrentAssignmentResponseDto.class);
	}
	
	@Test
	void testStatusPair() {

		String sessionid = "session123"; 
		
		IVRUserSession session = new IVRUserSession();
		
		session.setSessionId(sessionid);
		
		session.setLosDbResponse(tnInfoResponse.toString());
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		
		TNInfoResponse tnInfoResponse = new TNInfoResponse();
		
		tnInfoResponse.setTn("1");
		
		String actualResponse = ivrLfacsPagerTextFormation.getPageCurrentAssignment(response, session,
				currentAssignmentResponseDto);

		assertNotNull(actualResponse);
	}
	
	@Test
	void testStatusPair_LineStateTransfer() throws JsonMappingException, JsonProcessingException {

		String sessionid = "session123"; 
		
		IVRUserSession session = new IVRUserSession();
		
		TNInfoResponse tnInfoResponse = new TNInfoResponse();
		
		tnInfoResponse.setTn("1");
		
		session.setSessionId(sessionid);
		
		session.setLosDbResponse(tnInfoResponse.toString());
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		
		List<SO> soList = new ArrayList<SO>();
		
		SO so = new SO();
		
		List<LSTFN> lstFNList = new ArrayList<LSTFN>();
		LSTFN lstFN = new LSTFN();
		lstFN.setLST("1");
		lstFNList.add(lstFN);
		so.setLSTFN(lstFNList);
		
		soList.add(so);
		
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).setSO(soList);
		
		when(mockObjectMapper.readValue(session.getLosDbResponse(), TNInfoResponse.class)).thenReturn(tnInfoResponse);
		
		String actualResponse = ivrLfacsPagerTextFormation.getPageCurrentAssignment(response, session,
				currentAssignmentResponseDto);

		assertNotNull(actualResponse);
	}
	
	@Test
	void testStatusPair_Else() throws JsonMappingException, JsonProcessingException {

		String sessionid = "session123";

		IVRUserSession session = new IVRUserSession();

		TNInfoResponse tnInfoResponse = new TNInfoResponse();

		tnInfoResponse.setTn("1");

		session.setSessionId(sessionid);

		session.setLosDbResponse(tnInfoResponse.toString());

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		List<SO> soList = new ArrayList<SO>();

		SO so = new SO();


		List<LSTFN> lstFNList = new ArrayList<LSTFN>();
		LSTFN lstFN = new LSTFN();
		lstFN.setLST("1");
		lstFNList.add(lstFN);
		so.setLSTFN(lstFNList);

		soList.add(so);

		when(mockObjectMapper.readValue(session.getLosDbResponse(), TNInfoResponse.class)).thenReturn(tnInfoResponse);

		when(mockIVRLfacsServiceHelper.isSpecialCircuit(currentAssignmentResponseDto, null)).thenReturn(true);

		when(mockIVRLfacsServiceHelper.isUdcCircuit(currentAssignmentResponseDto, null)).thenReturn(false);

		String actualResponse = ivrLfacsPagerTextFormation.getPageCurrentAssignment(response, session,
				currentAssignmentResponseDto);

		assertNotNull(actualResponse);
	}
	
	@Test
	void testFormatDefectivePairsPagerTextForMobile() {
	    // create a DefectivePairsResponseDto object with some data
	    DefectivePairsResponseDto defectivePairsResponse = new DefectivePairsResponseDto();
	    DefectivePairsReturnDataSet returnDataSet = new DefectivePairsReturnDataSet();
	    List<DefectivePairDetailsT> defectivePairDetailsList = new ArrayList<>();
	    DefectivePairDetailsT defectivePairDetails1 = new DefectivePairDetailsT();
	    defectivePairDetails1.setPairId("pair1");
	    defectivePairDetails1.setDefectCode("code1");
	    defectivePairDetails1.setDefectStartDate("date1");
	    defectivePairDetails1.setPairStatus("status1");
	    defectivePairDetailsList.add(defectivePairDetails1);
	    DefectivePairDetailsT defectivePairDetails2 = new DefectivePairDetailsT();
	    defectivePairDetails2.setPairId("pair2");
	    defectivePairDetails2.setDefectCode("code2");
	    defectivePairDetails2.setDefectStartDate("date2");
	    defectivePairDetails2.setPairStatus("status2");
	    defectivePairDetailsList.add(defectivePairDetails2);
	    returnDataSet.setDefectivePairDetails(defectivePairDetailsList);
	    defectivePairsResponse.setReturnDataSet(returnDataSet);
	    defectivePairsResponse.getReturnDataSet().setAdditionalDefectivePairsFlag(false);

	    // call the method being tested
	    String result = ivrLfacsPagerTextFormation.formatDefectivePairsPagerTextForMobile(defectivePairsResponse, "cable1", "pair1");

	    // assert the result
	    assertEquals("DPR ca: cable1 pr: pair1| pair1 code1 date1 status1 |  pair2 code2 date2 status2 |  No more pairs", result);
	}
	

	@Test
	void testFormatDefectivePairsPagerTextForMobile_NullDefectivePairDetails() {
	    DefectivePairsResponseDto defectivePairsResponse = new DefectivePairsResponseDto();
	    DefectivePairsReturnDataSet returnDataSet = new DefectivePairsReturnDataSet();
	    returnDataSet.setDefectivePairDetails(null);
	    defectivePairsResponse.setReturnDataSet(returnDataSet);
	    String reqCable = "cable";
	    String reqPair = "pair";
	    String expected = "DPR ca: cable pr: pair| | No defective pairs in range No more pairs";
	    String actual = ivrLfacsPagerTextFormation.formatDefectivePairsPagerTextForMobile(defectivePairsResponse, reqCable, reqPair);
	    assertEquals(expected, actual);
	}

	@Test
	void testFormatDefectivePairsPagerTextForMobile_NullPairId() {
	    DefectivePairDetailsT defectivePairDetails = new DefectivePairDetailsT();
	    defectivePairDetails.setPairId(null);
	    List<DefectivePairDetailsT> defectivePairDetailsList = new ArrayList<>();
	    defectivePairDetailsList.add(defectivePairDetails);
	    DefectivePairsResponseDto defectivePairsResponse = new DefectivePairsResponseDto();
	    DefectivePairsReturnDataSet returnDataSet = new DefectivePairsReturnDataSet();
	    returnDataSet.setDefectivePairDetails(defectivePairDetailsList);
	    defectivePairsResponse.setReturnDataSet(returnDataSet);
	    String reqCable = "cable";
	    String reqPair = "pair";
	    String expected = "DPR ca: cable pr: pair| |  No more pairs";
	    String actual = ivrLfacsPagerTextFormation.formatDefectivePairsPagerTextForMobile(defectivePairsResponse, reqCable, reqPair);
	    assertEquals(expected, actual);
	}

	@Test
	void testFormatDefectivePairsPagerTextForMobile_NullDefectCode() {
	    DefectivePairDetailsT defectivePairDetails = new DefectivePairDetailsT();
	    defectivePairDetails.setPairId("pairId");
	    defectivePairDetails.setDefectCode(null);
	    List<DefectivePairDetailsT> defectivePairDetailsList = new ArrayList<>();
	    defectivePairDetailsList.add(defectivePairDetails);
	    DefectivePairsResponseDto defectivePairsResponse = new DefectivePairsResponseDto();
	    DefectivePairsReturnDataSet returnDataSet = new DefectivePairsReturnDataSet();
	    returnDataSet.setDefectivePairDetails(defectivePairDetailsList);
	    defectivePairsResponse.setReturnDataSet(returnDataSet);
	    String reqCable = "cable";
	    String reqPair = "pair";
	    String expected = "DPR ca: cable pr: pair| pairId |  No more pairs";
	    String actual = ivrLfacsPagerTextFormation.formatDefectivePairsPagerTextForMobile(defectivePairsResponse, reqCable, reqPair);
	    assertEquals(expected, actual);
	}

	@Test
	void testFormatDefectivePairsPagerTextForMobile_NullDefectStartDate() {
	    DefectivePairDetailsT defectivePairDetails = new DefectivePairDetailsT();
	    defectivePairDetails.setPairId("pairId");
	    defectivePairDetails.setDefectCode("defectCode");
	    defectivePairDetails.setDefectStartDate(null);
	    List<DefectivePairDetailsT> defectivePairDetailsList = new ArrayList<>();
	    defectivePairDetailsList.add(defectivePairDetails);
	    DefectivePairsResponseDto defectivePairsResponse = new DefectivePairsResponseDto();
	    DefectivePairsReturnDataSet returnDataSet = new DefectivePairsReturnDataSet();
	    returnDataSet.setDefectivePairDetails(defectivePairDetailsList);
	    defectivePairsResponse.setReturnDataSet(returnDataSet);
	    String reqCable = "cable";
	    String reqPair = "pair";
	    String expected = "DPR ca: cable pr: pair| pairId defectCode |  No more pairs";
	    String actual = ivrLfacsPagerTextFormation.formatDefectivePairsPagerTextForMobile(defectivePairsResponse, reqCable, reqPair);
	    assertEquals(expected, actual);
	}

	@Test
	void testFormatDefectivePairsPagerTextForMobile_NullPairStatus() {
	    DefectivePairDetailsT defectivePairDetails = new DefectivePairDetailsT();
	    defectivePairDetails.setPairId("pairId");
	    defectivePairDetails.setDefectCode("defectCode");
	    defectivePairDetails.setDefectStartDate("defectStartDate");
	    defectivePairDetails.setPairStatus(null);
	    List<DefectivePairDetailsT> defectivePairDetailsList = new ArrayList<>();
	    defectivePairDetailsList.add(defectivePairDetails);
	    DefectivePairsResponseDto defectivePairsResponse = new DefectivePairsResponseDto();
	    DefectivePairsReturnDataSet returnDataSet = new DefectivePairsReturnDataSet();
	    returnDataSet.setDefectivePairDetails(defectivePairDetailsList);
	    defectivePairsResponse.setReturnDataSet(returnDataSet);
	    String reqCable = "cable";
	    String reqPair = "pair";
	    String expected = "DPR ca: cable pr: pair| pairId defectCode defectStartDate |  No more pairs";
	    String actual = ivrLfacsPagerTextFormation.formatDefectivePairsPagerTextForMobile(defectivePairsResponse, reqCable, reqPair);
	    assertEquals(expected, actual);
	}

	@Test
	void testFormatDefectivePairsPagerTextForMobile_AdditionalDefectivePairsFlag() {
	    DefectivePairDetailsT defectivePairDetails = new DefectivePairDetailsT();
	    defectivePairDetails.setPairId("pairId");
	    defectivePairDetails.setDefectCode("defectCode");
	    defectivePairDetails.setDefectStartDate("defectStartDate");
	    defectivePairDetails.setPairStatus("pairStatus");
	    List<DefectivePairDetailsT> defectivePairDetailsList = new ArrayList<>();
	    defectivePairDetailsList.add(defectivePairDetails);
	    DefectivePairsResponseDto defectivePairsResponse = new DefectivePairsResponseDto();
	    DefectivePairsReturnDataSet returnDataSet = new DefectivePairsReturnDataSet();
	    returnDataSet.setDefectivePairDetails(defectivePairDetailsList);
	    defectivePairsResponse.setReturnDataSet(returnDataSet);
	    defectivePairsResponse.getReturnDataSet().setAdditionalDefectivePairsFlag(true);
	    String reqCable = "cable";
	    String reqPair = "pair";
	    String expected = "DPR ca: cable pr: pair| pairId defectCode defectStartDate pairStatus |  | More def pairs";
	    String actual = ivrLfacsPagerTextFormation.formatDefectivePairsPagerTextForMobile(defectivePairsResponse, reqCable, reqPair);
	    assertEquals(expected, actual);
	}
	
	//email
	@Test
	void testFormatDefectivePairsPagerTextForEmail() {
	    DefectivePairsRequestDto defectivePairsRequestDto = new DefectivePairsRequestDto();
	    CablePairRange cablePairRange = new CablePairRange();
	    cablePairRange.setLowPair(1);
	    
	    
	    DefectivePairsInputData inputData = new DefectivePairsInputData.Builder().cableId("Cable1")
				.cablePairRange(cablePairRange).lfacsEmployeeCode("test")
				.build();
	    defectivePairsRequestDto.setInputData(inputData);

	    DefectivePairsReturnDataSet defectivePairsReturnDataSet = new DefectivePairsReturnDataSet();
	    List<DefectivePairDetailsT> defectivePairDetailsList = new ArrayList<>();
	    DefectivePairDetailsT defectivePairDetailsT1 = new DefectivePairDetailsT();
	    defectivePairDetailsT1.setPairId("Pair1");
	    defectivePairDetailsT1.setDefectCode("Code1");
	    defectivePairDetailsT1.setDefectStartDate("2022-01-01");
	    defectivePairDetailsT1.setPairStatus("Status1");
	    defectivePairDetailsList.add(defectivePairDetailsT1);
	    DefectivePairDetailsT defectivePairDetailsT2 = new DefectivePairDetailsT();
	    defectivePairDetailsT2.setPairId("Pair2");
	    defectivePairDetailsT2.setDefectCode("Code2");
	    defectivePairDetailsT2.setDefectStartDate("2022-01-02");
	    defectivePairDetailsT2.setPairStatus("Status2");
	    defectivePairDetailsList.add(defectivePairDetailsT2);
	    defectivePairsReturnDataSet.setDefectivePairDetails(defectivePairDetailsList);
	    defectivePairsReturnDataSet.setAdditionalDefectivePairsFlag(false);

	    DefectivePairsResponseDto defectivePairsResponseDto = new DefectivePairsResponseDto();
	    DefectivePairsReturnDataSet returnDataSet = new DefectivePairsReturnDataSet();
	    returnDataSet.setDefectivePairDetails(defectivePairDetailsList);
	    returnDataSet.setAdditionalDefectivePairsFlag(false);
	    defectivePairsResponseDto.setReturnDataSet(returnDataSet);

	    String expectedPageText = "Up To 14 Immediate Defective Pairs For Cable : Cable1 Pair : 1\n" +
	            "-------------------------------------------------------------------------------\n" +
	            "     Pair     Defect Type     Date     Status\n" +
	            "-------------------------------------------------------------------------------\n" +
	            "     Pair1         Code1       2022-01-01     Status1\n" +
	            "     Pair2         Code2       2022-01-02     Status2\n" +
	            "\nNo more defective pairs available in this 100 count";

	    String actualPageText = ivrLfacsPagerTextFormation.formatDefectivePairsPagerTextForEmail(defectivePairsResponseDto, defectivePairsRequestDto,"Test");

	    assertEquals(expectedPageText, actualPageText);
	}

	@Test
	void testFormatDefectivePairsPagerTextForEmailWithNoDefectivePairs() {
	    DefectivePairsRequestDto defectivePairsRequestDto = new DefectivePairsRequestDto();
	    CablePairRange cablePairRange = new CablePairRange();
	    cablePairRange.setLowPair(1);
	   

	    DefectivePairsInputData inputData = new DefectivePairsInputData.Builder().cableId("Cable1")
				.cablePairRange(cablePairRange).lfacsEmployeeCode("test")
				.build();
	    
	    defectivePairsRequestDto.setInputData(inputData);
	    
	    DefectivePairsReturnDataSet defectivePairsReturnDataSet = new DefectivePairsReturnDataSet();
	    defectivePairsReturnDataSet.setDefectivePairDetails(new ArrayList<>());
	    defectivePairsReturnDataSet.setAdditionalDefectivePairsFlag(false);

	    DefectivePairsResponseDto defectivePairsResponseDto = new DefectivePairsResponseDto();
	    DefectivePairsReturnDataSet returnDataSet = new DefectivePairsReturnDataSet();
	    returnDataSet.setDefectivePairDetails(new ArrayList<>());
	    returnDataSet.setAdditionalDefectivePairsFlag(false);
	    defectivePairsResponseDto.setReturnDataSet(returnDataSet);

	    String expectedPageText = "Up To 14 Immediate Defective Pairs For Cable : Cable1 Pair : 1\n" +
	            "-------------------------------------------------------------------------------\n" +
	            "     Pair     Defect Type     Date     Status\n" +
	            "-------------------------------------------------------------------------------\n" +
	            "\nNo more defective pairs available in this 100 count";

	    String actualPageText = ivrLfacsPagerTextFormation.formatDefectivePairsPagerTextForEmail(defectivePairsResponseDto, defectivePairsRequestDto,"Test");

	    assertEquals(expectedPageText, actualPageText);
	}

	
}
