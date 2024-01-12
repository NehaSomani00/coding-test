package com.lumen.fastivr.IVRBusinessException;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BadUserInputExceptionTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testBadUserInputException() {
		new BadUserInputException();
	}

	@Test
	void testBadUserInputExceptionString() {
		new BadUserInputException("dummyMessage");
	}

	@Test
	void testBadUserInputExceptionStringString() {
		new BadUserInputException("dummeySession","dummyMessage");
	}

	@Test
	void testBadUserInputExceptionStringStringString() {
		new BadUserInputException("dummySession", "dummyMessage", "dummyState");
	}

	@Test
	void testBadUserInputExceptionThrowable() {
		new BadUserInputException( new Throwable());
	}

	@Test
	void testBadUserInputExceptionStringThrowable() {
		new BadUserInputException("dummyMessage", new Throwable());
	}

	@Test
	void testBadUserInputExceptionStringThrowableBooleanBoolean() {
		new BadUserInputException("dummyMessage", new Throwable(), false, false);
	}

	@Test
	void testToString() {
		new BadUserInputException("dummy").toString();
	}

	@Test
	void testGetSessionId() {
		String sessionId = new BadUserInputException("session123", "message").getSessionId();
		assertEquals("session123", sessionId);
	}

	@Test
	void testGetState() {
		String state = new BadUserInputException("dummySession", "dummyMessage", "state123").getState();
		assertEquals("state123", state);
	}

}
