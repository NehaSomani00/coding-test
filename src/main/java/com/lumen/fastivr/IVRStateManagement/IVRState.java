package com.lumen.fastivr.IVRStateManagement;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IVRState {
	
	private String stateName;
	private List<IVRStateTransition> transitions;
	
	public IVRState(String stateName) {
		this.stateName = stateName;
		transitions = new ArrayList<>();
	}
	
	public void addTransitions(IVRStateTransition transition) {
		transitions.add(transition);
	}
	
	public IVRStateTransition getTransitionForInput(String dtmf) {
		for (IVRStateTransition transition : transitions) {
			if (transition.getDtmfInput().equalsIgnoreCase(dtmf)) {
				return transition;
			}
		}
		return null;
	}
	
}
