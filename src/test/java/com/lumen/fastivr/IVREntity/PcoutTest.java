package com.lumen.fastivr.IVREntity;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
@ExtendWith(MockitoExtension.class)
class PcoutTest {

	@InjectMocks Pcout pcout;
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testPcOut() {
		pcout.setPgCntr("");
		pcout.setTimestamp(Date.valueOf(LocalDate.now()));
		
		pcout.getPgCntr();
		pcout.getTimestamp();
		
		pcout.toString();
	}

}
