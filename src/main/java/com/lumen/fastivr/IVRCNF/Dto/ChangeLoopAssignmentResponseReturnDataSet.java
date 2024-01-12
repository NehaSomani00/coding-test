package com.lumen.fastivr.IVRCNF.Dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class ChangeLoopAssignmentResponseReturnDataSet {
	
	@JsonProperty("CircuitId")
	private String circuitId;
	
	@JsonProperty("TerminationId")
	private String terminationId;
	
	// TODO New field
	@JsonProperty("RequestStatus")
	private String requestStatus;
	
	@JsonProperty("TerminalId")
	private String terminalId;
	
	@JsonProperty("CandidatePairInfo")
	private List<String> changeLoopAssignmentResponseReturnDataSetCandidatePairInfo;	

}
