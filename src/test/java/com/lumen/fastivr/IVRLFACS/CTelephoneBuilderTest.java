package com.lumen.fastivr.IVRLFACS;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import com.lumen.fastivr.IVRDto.LOSDB.CTelephone;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;

@ExtendWith(MockitoExtension.class)
class CTelephoneBuilderTest {
	
	@Autowired CTelephoneBuilder builder;
	
	@InjectMocks CTelephone telephone;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testBuildTelephone_13DigitPhone() { // (NPA)NXX-1234
		String ckid = "(734)256-1114";
		IVRUserSession session = new IVRUserSession();
		CTelephone tn = CTelephoneBuilder.newBuilder(session)
		.setTelephone(ckid)
		.build();
		
		assertEquals("734", tn.getNpa());
		assertEquals("256", tn.getNxx());
		assertEquals("1114", tn.getLineNumber());
	}
	
	@Test
	void testBuildTelephone_12DigitPhone() { // NPA NXX-1234
		String ckid = "734 256-1114";
		IVRUserSession session = new IVRUserSession();
		CTelephone tn = CTelephoneBuilder.newBuilder(session)
		.setTelephone(ckid)
		.build();
		
		assertEquals("734", tn.getNpa());
		assertEquals("256", tn.getNxx());
		assertEquals("1114", tn.getLineNumber());
		
	}
	
	@Test
	void testBuildTelephone_10DigitPhone() { // NPANXX1234
		String ckid = "7342561114";
		IVRUserSession session = new IVRUserSession();
		CTelephone tn = CTelephoneBuilder.newBuilder(session)
		.setTelephone(ckid)
		.build();
		
		assertEquals("734", tn.getNpa());
		assertEquals("256", tn.getNxx());
		assertEquals("1114", tn.getLineNumber());
	}
	
	@Test
	void testBuildTelephone_8DigitPhone() { // NXX-1234
		String ckid = "256-1114";
		IVRUserSession session = new IVRUserSession();
		session.setNpaPrefix("734"); 
		CTelephone tn = CTelephoneBuilder.newBuilder(session)
		.setTelephone(ckid)
		.build();
		
		assertEquals("734", tn.getNpa());
		assertEquals("256", tn.getNxx());
		assertEquals("1114", tn.getLineNumber());
	}
	
	@Test
	void testBuildTelephone_7DigitPhone() { // NXX1234
		String ckid = "2561114";
		IVRUserSession session = new IVRUserSession();
		session.setNpaPrefix("734"); 
		CTelephone tn = CTelephoneBuilder.newBuilder(session)
		.setTelephone(ckid)
		.build();
		
		assertEquals("734", tn.getNpa());
		assertEquals("256", tn.getNxx());
		assertEquals("1114", tn.getLineNumber());
	}
	
	
}
