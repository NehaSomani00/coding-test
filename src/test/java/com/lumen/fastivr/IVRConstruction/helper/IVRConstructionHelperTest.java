package com.lumen.fastivr.IVRConstruction.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRConstruction.Dto.ConstructionActivityResponse;
import com.lumen.fastivr.IVRConstruction.entity.IvrConstructionSession;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IVRConstructionHelperTest {

	@InjectMocks
	private IVRConstructionHelper ivrConstructionHelper;

	@Mock
	private ObjectMapper mockObjectMapper;

	@Test
	void testBuildReferTroubleReportRequest() {
		 String tn ="1234567890";
		 String npa = "12";
		 String nxx = "22";
		 String npaState = "34";
		 String openNbr = "345";


		 IvrConstructionSession session = new IvrConstructionSession();


		RetrieveLoopAssignmentRequest request = new RetrieveLoopAssignmentRequest();
		//request.setNpa(npa);
		//request.setNxx(nxx);
		//request.setTn(tn);
		//request.setNpaState(npaState);
		//request.setOpenNbr(openNbr);


		RetrieveLoopAssignmentRequest requestReferATicket = ivrConstructionHelper.buildReferTroubleReportRequest(tn, npa, nxx, npa, openNbr);

		//assertEquals("34", requestReferATicket.getNpaState());

	}

	@Test
	void testExtractDetailsFromConstructionResponse() throws JsonProcessingException {

	ConstructionActivityResponse constructionActivityResponse = new ConstructionActivityResponse();
	constructionActivityResponse.setOpenNbr("23");

		when(mockObjectMapper.readValue("hello", ConstructionActivityResponse.class)).thenReturn(constructionActivityResponse);

		ConstructionActivityResponse response = ivrConstructionHelper.extractDetailsFromConstructionResponse("hello");

		Assert.assertNotNull(response);

	}



}
