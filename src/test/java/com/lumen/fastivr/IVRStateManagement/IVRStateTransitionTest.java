package com.lumen.fastivr.IVRStateManagement;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IVRStateTransitionTest {

	@InjectMocks IVRStateTransition stateTrans;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testHashCode() {
		stateTrans.hashCode();
	}

	@Test
	void testIVRStateTransition() {
		stateTrans.getClass();
	}

	@Test
	void testGetNextState() {
		stateTrans.getNextState();
	}

	@Test
	void testGetDtmfInput() {
		stateTrans.getDtmfInput();
	}

	@Test
	void testSetNextState() {
		stateTrans.setNextState(new IVRState("Dummy"));
	}

	@Test
	void testSetDtmfInput() {
		stateTrans.setDtmfInput("");
	}

	@Test
	void testEqualsObject() {
		stateTrans.equals(new IVRStateTransition(null, null));
	}

	@Test
	void testCanEqual() {
		stateTrans.canEqual(new IVRStateTransition(null, null));
		
	}

	@Test
	void testToString() {
		stateTrans.toString();
	}

}
