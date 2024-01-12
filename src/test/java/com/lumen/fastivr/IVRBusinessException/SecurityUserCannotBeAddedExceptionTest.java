package com.lumen.fastivr.IVRBusinessException;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

class SecurityUserCannotBeAddedExceptionTest {
	

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testSecurityUserCannotBeAddedException() {
		new SecurityUserCannotBeAddedException();
		 new SecurityUserCannotBeAddedException("Error");
		 new SecurityUserCannotBeAddedException(new Exception());
		new SecurityUserCannotBeAddedException("Error", new Exception());
	}

}
