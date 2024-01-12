package com.lumen.fastivr.IVRCNF.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TNTest {
	
	@InjectMocks
	private TN tn;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testTN() { 
		
		tn.setLineNumber("567");
		tn.setNpa("abc");
		tn.setNxx("nxx");
		
		assertEquals("567", tn.getLineNumber());
		assertEquals("abc", tn.getNpa());
		assertEquals("nxx", tn.getNxx());
		
	}

}
