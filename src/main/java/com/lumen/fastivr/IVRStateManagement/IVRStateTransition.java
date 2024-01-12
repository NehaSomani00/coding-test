package com.lumen.fastivr.IVRStateManagement;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class IVRStateTransition {
	
	private IVRState nextState;
	private String dtmfInput;
	
	//this is for the states for whose transitions dtmf input is not (0-9) 
//	public IVRStateTransition(IVRState nextState) {
//		this.nextState = nextState;
//	}

	
	public IVRStateTransition(IVRState nextState, String dtmfInput) {
		this.nextState = nextState;
		this.dtmfInput = dtmfInput;
	}
	
}
