package com.lumen.fastivr.IVRStateManagement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IVRStateTest {
	
	@InjectMocks IVRState state;
	@InjectMocks IVRStateTransition transition;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testIVRState() {
		String nextStateName = "SSD-Dummy";
		IVRState nextState = new IVRState(nextStateName);
		transition = new IVRStateTransition(nextState, "1");
		state.addTransitions(transition);
		
		String responseName = state.getTransitionForInput("1").getNextState().getStateName();
		
		assertEquals(nextStateName, responseName);
	}

}
