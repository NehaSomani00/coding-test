package com.lumen.fastivr.IVREntity;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class NpaTest {
	
	@InjectMocks Npa npa;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testNpa() {
		npa.setCtweb("");
		npa.setNpaDesc("");
		npa.setNpaPrefix("");
		npa.setStateUS("");
		npa.setTimestamp(Date.valueOf(LocalDate.now()));
		npa.setTimezone("");
		
		npa.getCtweb();
		npa.getNpaDesc();
		npa.getNpaPrefix();
		npa.getStateUS();
		npa.getTimestamp();
		npa.getTimezone();
		
		npa.toString();
	}

}
