package com.lumen.fastivr.IVRUtils;

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
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.httpclient.IVRHttpClient;

@ExtendWith(MockitoExtension.class)
class AdditionalLinesReportUtilsTest {
	
	@InjectMocks AdditionalLinesReportUtils util;
	@InjectMocks IVRHttpClient ivrHttpClient;
	private CurrentAssignmentResponseDto currAssgResp;
	@Mock ObjectMapper mockObjectMapper;

	@BeforeEach
	void setUp() throws Exception {
		String jsonUnclean = "{\"ReturnDataSet\":{\"LOOP\":[{\"LPNO\":\"1\",\"CKID\":\"480 635-8126\",\"STAT\":\"WKG\",\"DATESpecified\":false,\"USOC\":\"1FB\",\"CSWEX\":\"Y\",\"TRM\":\"Y\",\"SRVTYP\":\"POTS1\",\"ADDR\":[{\"ADDRNO\":\"1\",\"BADR\":[{\"BAD\":\"720\",\"STR\":\"N COOPER RD\",\"CNA\":\"GILBERT\",\"STN\":\"AZ\"}],\"TEA\":\"I 720 N COOPER RD\",\"TP\":\"FIXED\",\"PTR\":\"861C:451\",\"RZ\":\"13\",\"ICSW\":\"8\",\"TYPE\":\"STD\",\"RMK0TE\":\"721232W\"}],\"OEC\":[{\"GRD\":\"1\",\"CLS\":\"B\",\"WIRES\":\"2\",\"MI\":\"N\",\"CTG\":\"V\",\"COTE\":\"S\",\"QUAL\":\"O\",\"SIG\":\"L\",\"MET\":\"N\"}],\"SEG\":[{\"SEGNO\":\"1\",\"CA\":\"3\",\"PR\":\"299\",\"LSTAT\":\"WKG\",\"BP\":\"124\",\"TEA\":\"X 861 N COOPER RD\",\"TP\":\"FIXED\",\"RZ\":\"13\",\"TEPREF\":\"19:1101\",\"TECA\":\"19\",\"TEPR\":\"1101\",\"DEFDSpecified\":false,\"TPR\":\"410133\",\"RSVDATSpecified\":false,\"RMK0TE\":\"521K728,621H416 7211VHE\",\"CQC\":\"Z3\"},{\"SEGNO\":\"2\",\"CA\":\"861C\",\"PR\":\"458\",\"LSTAT\":\"WKG\",\"BP\":\"8\",\"OBP\":\"458\",\"TEA\":\"I 720 N COOPER RD\",\"TP\":\"FIXED\",\"RZ\":\"13\",\"TEPREF\":\"861C:451\",\"TECA\":\"861C\",\"TEPR\":\"451\",\"DEFDSpecified\":false,\"TPR\":\"410133\",\"LMURMK\":\"Y\",\"RSVDATSpecified\":false,\"RMK0TE\":\"721232W\",\"CQC\":\"Z3\"}]}]},\"RequestId\":\"FASTFAST\",\"WebServiceName\":\"SIABusService\",\"TargetSchemaVersionUsed\":{\"TargetXSDName\":\"\",\"MajorVersionNumber\":0.0,\"MinorVersionNumber\":0.0},\"MessageStatus\":{\"ErrorCode\":\"\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\"},\"ARTISInformation\":{\"TotalTime\":\"622\",\"OverheadTime\":\"142\"},\"CompletedTimeStamp\":\"2023-10-19T05:18:54.409-05:00\",\"CompletedTimeStampSpecified\":true}";
		String json = jsonUnclean.replace("\\", "");
		currAssgResp = new ObjectMapper().readValue(json, CurrentAssignmentResponseDto.class);
	}


	@Test
	void testGetFormattedServiceAddress() throws JsonMappingException, JsonProcessingException {
		when(mockObjectMapper.readValue("mock-json", CurrentAssignmentResponseDto.class)).thenReturn(currAssgResp);
		String serviceAddress = util.getFormattedServiceAddress("Session123", "mock-json");
		String expected = "NO=720,ST=N COOPER RD";
		assertEquals(expected, serviceAddress);
	}

}
