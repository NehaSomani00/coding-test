package com.lumen.fastivr.IVRDto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MltRequestDtoTest {
	
	@InjectMocks MltRequestDto mltRequest;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testMltRequestDto() {
		mltRequest.setTn("2336363");
		mltRequest.setTestType("FULLX");
		
		mltRequest.getTn();
		String testType = mltRequest.getTestType();
		
		mltRequest.hashCode();
		mltRequest.toString();
		
		assertEquals("FULLX", testType);
	}

}
