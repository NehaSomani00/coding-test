package com.lumen.fastivr.IVRCANST.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateLoopResponseReturnDataSetTest {

	@InjectMocks
	UpdateLoopResponseReturnDataSet updateLoopResponseReturnDataSet;

	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void testUpdateLoopResponseReturnDataSet() {
		
		updateLoopResponseReturnDataSet.setCircuitId("123");
		updateLoopResponseReturnDataSet.setTerminationId("");
		
		updateLoopResponseReturnDataSet.hashCode();
		updateLoopResponseReturnDataSet.equals(new UpdateLoopResponseReturnDataSet());
		updateLoopResponseReturnDataSet.toString();
		
		assertEquals("123", updateLoopResponseReturnDataSet.getCircuitId());
		assertEquals("", updateLoopResponseReturnDataSet.getTerminationId());
		
	}
}
