package com.lumen.fastivr.IVRCNF.Dto;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NetworkInfrastructureTest {

	@InjectMocks
	private NetworkInfrastructure networkInfrastructure;
	
	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void testNetworkInfrastructureTest() { 
		
		networkInfrastructure.setDesc("desc1");
		networkInfrastructure.setDownstreamTransportCode("code1");
		networkInfrastructure.setNetworkInfrastructureIndicatorCode("infraCode");
		networkInfrastructure.setNetworkTopologyCode("");
		networkInfrastructure.setPairBondingFlag("Flag");
		networkInfrastructure.setUpstreamTransportCode("Upcodes");
		networkInfrastructure.setVoiceActivationFlag(true);
		
		assertEquals("desc1", networkInfrastructure.getDesc());
		assertEquals("code1", networkInfrastructure.getDownstreamTransportCode());
		assertEquals("infraCode", networkInfrastructure.getNetworkInfrastructureIndicatorCode());
		assertEquals("", networkInfrastructure.getNetworkTopologyCode());
		assertEquals("Flag", networkInfrastructure.getPairBondingFlag());
		assertEquals("Upcodes", networkInfrastructure.getUpstreamTransportCode());
		assertTrue(networkInfrastructure.isVoiceActivationFlag());
	}

}
