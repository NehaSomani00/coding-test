package com.lumen.fastivr.IVRCNF.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ARTISResponseHeaderTest {

	@InjectMocks
	private NIIServiceResponse niiServiceResponse;

	@InjectMocks
	private ARTISResponseHeader artisResponseHeader;
	
	@InjectMocks
	private ARTISInfoObject artisInfoObject;

	@InjectMocks
	private ResponderId responderId;

	@InjectMocks
	private SvcAddress svcAddress;

	@InjectMocks
	private TN tn;
	
	@InjectMocks
	private NetworkInfrastructure networkInfrastructure;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testARTISResponseHeader() { 

		artisResponseHeader.setArtisCorrelationId("id1");
		niiServiceResponse.setUsageMessage("msg");

		artisInfoObject.setOverheadTime("");
		artisInfoObject.setTotalTime("");
		artisInfoObject.hashCode();
		artisInfoObject.equals(new ARTISInfoObject());
		artisInfoObject.toString();
		
		responderId.setHostName("123");
		responderId.setServerName("");
		responderId.setServiceName("");
		responderId.hashCode();
		responderId.equals(new ResponderId());
		responderId.toString();
		
		svcAddress.setItem("");
		svcAddress.setCity("LA");
		svcAddress.setItemElementName("");
		svcAddress.setStateProvince("");
		svcAddress.setStateProvinceSpecified("");
		svcAddress.setWireCtrCLLICode("");
		svcAddress.hashCode();
		svcAddress.equals(new SvcAddress());
		svcAddress.toString();		
		
		tn.setLineNumber("567");
		tn.setNpa("abc");
		tn.setNxx("nxx");
		tn.hashCode();
		tn.equals(new TN());
		tn.toString();	
		
		networkInfrastructure.setDesc("");
		networkInfrastructure.setDownstreamTransportCode("");
		networkInfrastructure.setNetworkInfrastructureIndicatorCode("");
		networkInfrastructure.setNetworkTopologyCode("");
		networkInfrastructure.setPairBondingFlag("");
		networkInfrastructure.setUpstreamTransportCode("");
		networkInfrastructure.setVoiceActivationFlag(false);
		
		artisResponseHeader.setArtisInfoObject(artisInfoObject);
		artisResponseHeader.setResponderId(responderId);
		niiServiceResponse.setTn(tn);
		
		assertEquals("id1", artisResponseHeader.getArtisCorrelationId());
		assertEquals("567", niiServiceResponse.getTn().getLineNumber());
	}
}
