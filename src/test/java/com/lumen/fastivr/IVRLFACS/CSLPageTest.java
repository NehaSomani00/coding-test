package com.lumen.fastivr.IVRLFACS;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketResponse;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentResponse;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;

@ExtendWith(MockitoExtension.class)
public class CSLPageTest {

	@InjectMocks 
	private CSLPage cslPage;
	
	@Mock 
	private ObjectMapper mockObjectMapper;
	
	private IVRUserSession session;
	
	private CurrentAssignmentResponseDto currAssgnRespDto;
	
	private RetrieveMaintenanceChangeTicketResponse maintChngResp;
	
	private RetrieveLoopAssignmentResponse loopAssgResp;
	
	@BeforeEach
	void beforeSetup() throws JsonMappingException, JsonProcessingException {
		cslPage = new CSLPage(mockObjectMapper);
		session = new IVRUserSession();
		
		currAssgnRespDto = new CurrentAssignmentResponseDto();
		String jsonMaintStr = "{\"ReturnDataSet\":{\"LFACSScreenName\":\"** MAINTENANCE CHANGE TICKET **\",\"CandidatePairInfo\":[{\"CableId\":\"IPG8\",\"CableUnitId\":\"225\",\"BindingPostColorCode\":\"675\",\"PairStatus\":\"SPR\"},{\"CableId\":\"IPG8\",\"CableUnitId\":\"215\",\"BindingPostColorCode\":\"665\",\"PairStatus\":\"SPR\"},{\"CableId\":\"IPG8\",\"CableUnitId\":\"191\",\"BindingPostColorCode\":\"641\",\"PairStatus\":\"SPR\"},{\"CableId\":\"IPG8\",\"CableUnitId\":\"186\",\"BindingPostColorCode\":\"636\",\"PairStatus\":\"SPR\"},{\"CableId\":\"IPG8\",\"CableUnitId\":\"183\",\"BindingPostColorCode\":\"633\",\"PairStatus\":\"SPR\"},{\"CableId\":\"IPG8\",\"CableUnitId\":\"180\",\"BindingPostColorCode\":\"630\",\"PairStatus\":\"SPR\"},{\"CableId\":\"IPG8\",\"CableUnitId\":\"171\",\"BindingPostColorCode\":\"621\",\"PairStatus\":\"SPR\"},{\"CableId\":\"IPG8\",\"CableUnitId\":\"168\",\"BindingPostColorCode\":\"618\",\"PairStatus\":\"SPR\"},{\"CableId\":\"IPG8\",\"CableUnitId\":\"93\",\"BindingPostColorCode\":\"593\",\"PairStatus\":\"SPR\"},{\"CableId\":\"IPG8\",\"CableUnitId\":\"92\",\"BindingPostColorCode\":\"592\",\"PairStatus\":\"SPR\"},{\"CableId\":\"IPG8\",\"CableUnitId\":\"89\",\"BindingPostColorCode\":\"589\",\"PairStatus\":\"SPR\"},{\"CableId\":\"IPG8\",\"CableUnitId\":\"88\",\"BindingPostColorCode\":\"588\",\"PairStatus\":\"SPR\"}]},\"RequestId\":\"FASTFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\"},\"ARTISInformation\":{\"TotalTime\":\"1985\",\"OverheadTime\":\"1985\"},\"CompletedTimeStamp\":\"2023-11-20T04:54:18.562-06:00\",\"CompletedTimeStampSpecified\":true}";
		String jsonCurrStr = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"763 757-4229\",\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FR\",\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"SRVTYP\":\"ADSL1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"10151\",\"STR\":\"QUINCE ST NW\",\"CNA\":\"COON RPDS\",\"STN\":\"MN\"}],\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"PTR\":\"10000P:751\",\"RZ\":\"13\",\"ICSW\":\"2\",\"TYPE\":\"STD\"}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"R\",\"WIRES\":\"2\",\"PGI\":\"B\",\"MI\":\"N\",\"CTG\":\"L\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"D\",\"MET\":\"N\",\"DDR\":\"J\",\"LATY\":\"IP\"}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"IPG8\",\"PR\":\"90\",\"LSTAT\":\"WKG\",\"BP\":\"590\",\"RLOE\":\"BSI2IP\",\"TEA\":\"DSLAM 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RZ\":\"13\",\"TEPREF\":\"17:256\",\"TECA\":\"17\",\"TEPR\":\"256\",\"DEFDSpecified\":false,\"SYSTP\":\"IDISCS\",\"PGSNO\":\"140\",\"FLDLTS\":\"ES\",\"COLTS\":\"NE\",\"RLA\":\"RT 9902 NW REDWOOD ST\",\"TPR\":\"440903\",\"RSVDATSpecified\":false,\"RMK0TE\":\"DA HOTEL N660598 VECTORED SITE\",\"CQC\":\"Z6\"},{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\",\"LSTAT\":\"WKG\",\"BP\":\"1678\",\"OBP\":\"1278\",\"RLOE\":\"BBGIGO\",\"TEA\":\"X 10000 PALM ST NW\",\"TP\":\"FIXED\",\"RZ\":\"15\",\"TEPREF\":\"3:301\",\"TECA\":\"3\",\"TEPR\":\"301\",\"DEFDSpecified\":false,\"SYSTP\":\"T5K4D\",\"PGSNO\":\"1822\",\"FLDLTS\":\"DZT8\",\"COLTS\":\"NREQ\",\"RLA\":\"DSLAM 10002 NW PALM ST\",\"TPR\":\"440903\",\"RSVDATSpecified\":false,\"RMK0TE\":\"PLAT 197 N214714 N660598 VECTORED\",\"CQC\":\"BB\"},{\"SEGNO\":\"3\",\"CA\":\"10000P\",\"PR\":\"791\",\"LSTAT\":\"WKG\",\"BP\":\"UNK,O-W+Y-BL\",\"OBP\":\"791\",\"TEA\":\"10151 QUINCE\",\"TP\":\"RA\",\"RZ\":\"13\",\"TEPREF\":\"10000P:751\",\"TECA\":\"10000P\",\"TEPR\":\"751\",\"DEFDSpecified\":false,\"TPR\":\"440903\",\"LMURMK\":\"Y\",\"RSVDATSpecified\":false,\"CQC\":\"Z6\"}]}]},\"RequestId\":\"FASTFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\"},\"ARTISInformation\":{\"TotalTime\":\"542\",\"OverheadTime\":\"64\"},\"CompletedTimeStamp\":\"2023-11-20T04:32:33.068-06:00\",\"CompletedTimeStampSpecified\":true}";
		
		currAssgnRespDto = new ObjectMapper().readValue(jsonCurrStr, CurrentAssignmentResponseDto.class);
		maintChngResp = new ObjectMapper().readValue(jsonMaintStr, RetrieveMaintenanceChangeTicketResponse.class);
		loopAssgResp = new ObjectMapper().readValue(jsonMaintStr, RetrieveLoopAssignmentResponse.class);
		session.setCurrentAssignmentResponse(jsonCurrStr);
		session.setRtrvMaintChngeMsgName(jsonMaintStr);
	}
	
	@Test
	void testCSLPageData() { // (NPA)NXX-1234
		
		CSLPage cslPage = new CSLPage("FASG", "123 345-4321", "1", "2", "3", "4", "5", "6", "7",
				"8", "9");

		String actualResponse = cslPage.FormatError();
		
		assertEquals(" *Error* FASG|Tel Nbr = 123 345-4321|fr ca/pr: 1/2|to ca/pr: 3/4|Cut failed|5|6|Text: 7", actualResponse);
	}
	
	@Test
	void testformatSparePairsPage_maint() throws JsonMappingException, JsonProcessingException {
		cslPage.setInitValues("RTYPE", "CKID");
		when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currAssgnRespDto);
		String pageText = cslPage.formatSparePairsPage(session, 1, true, 10, 5, 5, null, maintChngResp);
		assertNotNull(pageText);
	}
	
	@Test
	void testformatSparePairsPage_loop() throws JsonMappingException, JsonProcessingException {
		cslPage.setInitValues("RTYPE", "CKID");
		when(mockObjectMapper.readValue(session.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currAssgnRespDto);
		String pageText = cslPage.formatSparePairsPage(session, 1, true, 10, 5, 4, loopAssgResp, null);
		assertNotNull(pageText);
	}
	
	@Test
	void testCSLPageAllData() {

		cslPage.setErrorText("S");
		cslPage.setInitValues("", "");
		cslPage.setReqCable("");
		cslPage.setReqCircuit("");
		cslPage.setReqNewCable("");
		cslPage.setReqNewPair("");
		cslPage.setReqOrder("");
		cslPage.setReqPair("");
		cslPage.setReqTea("");
		cslPage.setReqType("");
		cslPage.setSendToId("");
		cslPage.setTransType("");

		cslPage.getErrorText();
		cslPage.getReqCable();
		cslPage.getReqCircuit();
		cslPage.getReqNewCable();
		cslPage.getReqOrder();
		cslPage.getReqNewPair();
		cslPage.getReqOrder();
		cslPage.getReqPair();
		cslPage.getReqTea();
		cslPage.getReqType();
		cslPage.getSendToId();
		cslPage.getTransType();

		assertEquals("S", cslPage.getErrorText());
	}
}
