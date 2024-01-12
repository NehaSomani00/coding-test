package com.lumen.fastivr.IVRCNF.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;

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
import com.lumen.fastivr.IVRCNF.entity.IVRCnfEntity;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.LOOP;
import com.lumen.fastivr.IVRDto.SEG;
import com.lumen.fastivr.IVRDto.LOSDB.CTelephone;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVREntity.TNInfo;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;

@ExtendWith(MockitoExtension.class)
public class IVRCnfUtilitiesTest {
	
	@Mock
	private ObjectMapper mockObjectMapper;
	
	@Mock
	private IVRLfacsServiceHelper mockIVRLfacsServiceHelper;	

	@InjectMocks
	private IVRCnfUtilities ivrCnfUtilities;
	
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
	void testGetCablePairStatus() throws JsonMappingException, JsonProcessingException {

		
		String actualCablePair = "WKG";
		String cablePairStatus = ivrCnfUtilities.getCablePairStatus(currentAssignmentResponseDto);
		assertEquals(actualCablePair, cablePairStatus);		
		
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).setSTAT(null);
		
		String actualCablePair1 = "WKG";
		String cablePairStatus1 = ivrCnfUtilities.getCablePairStatus(currentAssignmentResponseDto);
		assertEquals(actualCablePair1, cablePairStatus1);				
	}	
	
	@Test
	void testGetServiceOrder() throws JsonMappingException, JsonProcessingException {
		
		String actualServiceOrder = "";
		String expectedServiceOrder = ivrCnfUtilities.getServiceOrder(currentAssignmentResponseDto.getReturnDataSet().getLoop());
		assertEquals(actualServiceOrder, expectedServiceOrder);		
	}	
	
	@Test
	void testGetCablePair() throws JsonMappingException, JsonProcessingException {
		
		String actualCablePair = "24";
		String expectedCablePair = ivrCnfUtilities.getCablePair(currentAssignmentResponseDto.getReturnDataSet().getLoop());
		assertEquals(actualCablePair, expectedCablePair);		
		
	}
	
	@Test
	void testCablePairFromSegment() throws JsonMappingException, JsonProcessingException {
		
		String actualCablePair = "24";
		String expectedCablePair = ivrCnfUtilities.getCablePairFromSegment(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG());
		assertEquals(actualCablePair, expectedCablePair);		
	}
	
	@Test
	void testCableFromSegment() throws JsonMappingException, JsonProcessingException {
		
		String actualCable = "IPG8";
		String expectedCable = ivrCnfUtilities.getCableFromSegment(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG());
		assertEquals(actualCable, expectedCable);		
	}	
	
	@Test
	void testGetSegmentNumber() throws JsonMappingException, JsonProcessingException {
		
		IVRCnfEntity cnfSession = new IVRCnfEntity();
		cnfSession.setSegmentRead("F1");
		
		int actualSegmentNo = 0;
		int expectedSegmentNo = ivrCnfUtilities.getSegmentNumber(cnfSession);
		assertEquals(actualSegmentNo, expectedSegmentNo);		
		
		cnfSession.setSegmentRead("F2");
		
		int actualSegmentNo1 = 1;
		int expectedSegmentNo1 = ivrCnfUtilities.getSegmentNumber(cnfSession);
		assertEquals(actualSegmentNo1, expectedSegmentNo1);
		
		cnfSession.setSegmentRead("F3");
		
		int actualSegmentNo2 = 2;
		int expectedSegmentNo2 = ivrCnfUtilities.getSegmentNumber(cnfSession);
		assertEquals(actualSegmentNo2, expectedSegmentNo2);		
	}		
	
	@Test
	void testGetSegmentList() throws JsonMappingException, JsonProcessingException {
		
		List<SEG> expectedList = ivrCnfUtilities.getSegmentList(currentAssignmentResponseDto);
		assertNotNull(expectedList);		
	}
	
	@Test
	void testGetServiceAddress() throws JsonMappingException, JsonProcessingException {
		
		String actualServiceAddress = "10151 QUINCE ST NW 10151 QUINCE ST NW COON RPDS MN MN MN";
		String expectedServiceAddress = ivrCnfUtilities.getServiceAddress(currentAssignmentResponseDto);
		assertEquals(actualServiceAddress, expectedServiceAddress);		
		
		
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getBADR().get(0).setBAD(null); 
		String actualServiceAddress1 = " QUINCE ST NW 10151 QUINCE ST NW COON RPDS MN MN MN";
		String expectedServiceAddress1 = ivrCnfUtilities.getServiceAddress(currentAssignmentResponseDto);
		assertEquals(actualServiceAddress1, expectedServiceAddress1);				
	}	
	
	@Test
	void testBPType() throws JsonMappingException, JsonProcessingException {
		
		String actualCable = "FIXED";
		String expectedCable = ivrCnfUtilities.getBpType(1, currentAssignmentResponseDto);
		assertEquals(actualCable, expectedCable);		
	}
}
