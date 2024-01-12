package com.lumen.fastivr.IVRStateManagement;

import static com.lumen.fastivr.IVRUtils.IVRConstants.DIRECT_STATE_TRANSFER;
import static com.lumen.fastivr.IVRUtils.IVRConstants.DTMF_INPUT_1;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IVRStateSystemTest {
	
	@BeforeEach
	void setUp() throws Exception {
		stateSystem = new IVRStateSystem();
	}
	
	private IVRStateSystem stateSystem;

	@Test
	void testStateChange_SS0110_SSD110() {
		String genesysState = "SS0110";
		String userInput = DIRECT_STATE_TRANSFER;
		IVRState ivrStateGenesys = stateSystem.getStateMap().get(genesysState);
		IVRState ivrStateFast = ivrStateGenesys.getTransitionForInput(userInput).getNextState();
		
		String actual = ivrStateFast.getStateName();
		String expected = "SSD110";
		assertEquals(expected, actual);
	}
	
	@Test
	void testStateChange_SS0120_SSD120() {
		String genesysState = "SS0120";
		String userInput = DIRECT_STATE_TRANSFER;
		IVRState ivrStateGenesys = stateSystem.getStateMap().get(genesysState);
		IVRState ivrStateFast = ivrStateGenesys.getTransitionForInput(userInput).getNextState();
		
		String actual = ivrStateFast.getStateName();
		String expected = "SSD120";
		assertEquals(expected, actual);
	}
	
	@Test
	void testStateChange_SS0135_SSD135() {
		String genesysState = "SS0135";
		String userInput = DTMF_INPUT_1;
		IVRState ivrStateGenesys = stateSystem.getStateMap().get(genesysState);
		IVRState ivrStateFast = ivrStateGenesys.getTransitionForInput(userInput).getNextState();
		
		String actual = ivrStateFast.getStateName();
		String expected = "SSD135";
		assertEquals(expected, actual);
	}
	
	@Test
	void testStateChange_SS0180_SSD180() {
		String genesysState = "SS0180";
		String userInput = DTMF_INPUT_1;
		IVRState ivrStateGenesys = stateSystem.getStateMap().get(genesysState);
		IVRState ivrStateFast = ivrStateGenesys.getTransitionForInput(userInput).getNextState();
		
		String actual = ivrStateFast.getStateName();
		String expected = "SSD180";
		assertEquals(expected, actual);
	}
	
	@Test
	void testStateChange_SS0190_SSD190() {
		String genesysState = "SS0190";
		String userInput = DIRECT_STATE_TRANSFER;
		IVRState ivrStateGenesys = stateSystem.getStateMap().get(genesysState);
		IVRState ivrStateFast = ivrStateGenesys.getTransitionForInput(userInput).getNextState();
		
		String actual = ivrStateFast.getStateName();
		String expected = "SSD190";
		assertEquals(expected, actual);
	}
	
	@Test
	void testStateChange_SS0400_SSD150() {
		String genesysState = "SS0400";
		String userInput = DTMF_INPUT_1;
		IVRState ivrStateGenesys = stateSystem.getStateMap().get(genesysState);
		IVRState ivrStateFast = ivrStateGenesys.getTransitionForInput(userInput).getNextState();
		
		String actual = ivrStateFast.getStateName();
		String expected = "SSD150";
		assertEquals(expected, actual);
	}
	
	@Test
	void testStateChange_SS0150_SSD150() {
		String genesysState = "SS0150";
		String userInput = DTMF_INPUT_1;
		IVRState ivrStateGenesys = stateSystem.getStateMap().get(genesysState);
		IVRState ivrStateFast = ivrStateGenesys.getTransitionForInput(userInput).getNextState();
		
		String actual = ivrStateFast.getStateName();
		String expected = "SSD150";
		assertEquals(expected, actual);
	}
	
	@Test
	void testStateChange_SS0210_SSD210() {
		String genesysState = "SS0210";
		String userInput = DIRECT_STATE_TRANSFER;
		IVRState ivrStateGenesys = stateSystem.getStateMap().get(genesysState);
		IVRState ivrStateFast = ivrStateGenesys.getTransitionForInput(userInput).getNextState();
		
		String actual = ivrStateFast.getStateName();
		String expected = "SSD210";
		assertEquals(expected, actual);
	}
	
	@Test
	void testStateChange_SS0300_SSD300() {
		String genesysState = "SS0300";
		String userInput = DIRECT_STATE_TRANSFER;
		IVRState ivrStateGenesys = stateSystem.getStateMap().get(genesysState);
		IVRState ivrStateFast = ivrStateGenesys.getTransitionForInput(userInput).getNextState();
		
		String actual = ivrStateFast.getStateName();
		String expected = "SSD300";
		assertEquals(expected, actual);
	}
	
	@Test
	void testStateChange_SS0160_SSD160() {
		String genesysState = "SS0160";
		String userInput = DIRECT_STATE_TRANSFER;
		IVRState ivrStateGenesys = stateSystem.getStateMap().get(genesysState);
		IVRState ivrStateFast = ivrStateGenesys.getTransitionForInput(userInput).getNextState();
		
		String actual = ivrStateFast.getStateName();
		String expected = "SSD160";
		assertEquals(expected, actual);
	}
	
	@Test
	void testStateChange_SS0165_SSD165() {
		String genesysState = "SS0165";
		String userInput = DTMF_INPUT_1;
		IVRState ivrStateGenesys = stateSystem.getStateMap().get(genesysState);
		IVRState ivrStateFast = ivrStateGenesys.getTransitionForInput(userInput).getNextState();
		
		String actual = ivrStateFast.getStateName();
		String expected = "SSD165";
		assertEquals(expected, actual);
	}
	
	@Test
	void testStateChange_SS0170_SSD111() {
		String genesysState = "SS0170";
		String userInput = DIRECT_STATE_TRANSFER;
		IVRState ivrStateGenesys = stateSystem.getStateMap().get(genesysState);
		IVRState ivrStateFast = ivrStateGenesys.getTransitionForInput(userInput).getNextState();
		
		String actual = ivrStateFast.getStateName();
		String expected = "SSD170";
		assertEquals(expected, actual);
	}
	

}
