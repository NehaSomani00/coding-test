package com.lumen.fastivr.IVRDto.retrieveLoopAssignment;

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
public class RetLoopAssigReturnDataSet {
	
	 @JsonProperty("CircuitId")
	 private String circuitId;
    
	 @JsonProperty("TerminationId")
	 private String terminationId;
	
	 @JsonProperty("TerminalId")
	 private String terminalId;
	 
	 @JsonProperty("ServiceLocation")
	 private ServiceLocation serviceLocation;
	
	 @JsonProperty("CandidatePairInfo")
	 private List<LoopAssignCandidatePairInfo> candidatePairInfo;

	 @JsonProperty("TerminalRemarks")
	 private String terminalRemarks;

	 @JsonProperty("OrderRemarks")
	 private String orderRemarks;
}

