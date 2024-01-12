package com.lumen.fastivr.IVRCNF.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SvcAddressTest {

	@InjectMocks
	private SvcAddress svcAddress;

	@InjectMocks
	private Unit unit;

	@InjectMocks
	private Elevation elevation;

	@InjectMocks
	private Structure structure;	


	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testChangeLoopAssignmentResponseDtoTest() { 

		svcAddress.setItem("");
		svcAddress.setCity("LA");
		svcAddress.setItemElementName("");
		svcAddress.setStateProvince("");
		svcAddress.setStateProvinceSpecified("");
		svcAddress.setWireCtrCLLICode("");


		unit.setName("test");
		unit.hashCode();
		unit.equals(new Unit());
		unit.toString();

		elevation.setName("test");
		elevation.hashCode();
		elevation.equals(new Elevation());
		elevation.toString();	

		structure.setName("test");
		structure.hashCode();
		structure.equals(new Structure());
		structure.toString();	

		svcAddress.setUnit(unit);
		svcAddress.setStructure(structure);
		svcAddress.setItemElementName("");
		svcAddress.setElevation(elevation);

		assertEquals("LA", svcAddress.getCity());

	}


}
