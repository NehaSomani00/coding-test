package com.lumen.fastivr.IVRBusinessException;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BusinessExceptionTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testBusinessException() {
		new BusinessException();
	}

	@Test
	void testBusinessExceptionString() {
		new BusinessException("dummyMessage");
	}

	@Test
	void testBusinessExceptionStringString() {
		new BusinessException("dummeySession","dummyMessage");
	}

	@Test
	void testBusinessExceptionStringStringString() {
		new BusinessException("dummySession", "dummyMessage", "dummyState");
	}

	@Test
	void testBusinessExceptionThrowable() {
		new BusinessException( new Throwable());
	}

	@Test
	void testBusinessExceptionStringThrowable() {
		new BusinessException("dummyMessage", new Throwable());
	}

	@Test
	void testBusinessExceptionStringThrowableBooleanBoolean() {
		new BusinessException("dummyMessage", new Throwable(), false, false);
	}

	@Test
	void testToString() {
		new BusinessException("dummy").toString();
	}

	@Test
	void testGetSessionId() {
		String sessionId = new BusinessException("session123", "message").getSessionId();
		assertEquals("session123", sessionId);
	}

	@Test
	void testGetState() {
		String state = new BusinessException("dummySession", "dummyMessage", "state123").getState();
		assertEquals("state123", state);
	}

}
