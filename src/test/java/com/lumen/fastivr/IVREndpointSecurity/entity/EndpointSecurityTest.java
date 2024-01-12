package com.lumen.fastivr.IVREndpointSecurity.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EndpointSecurityTest {
	
	@InjectMocks EndpointSecurity entity;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testEndpointSecurityTest() {
		entity.setUsername("tester");
		entity.setSecret("tester");
		entity.setEnabled(false);
		
		entity.getUsername();
		entity.getSecret();
		assertFalse(entity.isEnabled());
	}

}
