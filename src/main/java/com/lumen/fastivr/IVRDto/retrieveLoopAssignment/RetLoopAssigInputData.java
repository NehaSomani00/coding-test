package com.lumen.fastivr.IVRDto.retrieveLoopAssignment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class RetLoopAssigInputData {
	
	 @JsonProperty("LFACSEmployeeCode")
	 private String LFACSEmployeeCode;
    
	 @JsonProperty("LFACSEntity")
	 private String LFACSEntity;
	
	 @JsonProperty("WireCtrPrimaryNPANXX")
	 private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;
	 
	 @JsonProperty("ServiceOrderNumber")
	 private String serviceOrderNumber;
	
	 @JsonProperty("CircuitId")
	 private String circuitId;

	 @JsonProperty("TerminationId")
	 private String terminationId;

	 @JsonProperty("CableId")
	 private String cableId;

	 @JsonProperty("CableUnitId")
	 private String cableUnitId;
	 
	 
	 @JsonInclude(Include.NON_NULL)
	 @JsonProperty("FacilityChangeReasonCode")
	 private String facilityChangeReasonCode;

	 @JsonProperty("FacilityChangeReasonCodeSpecified")
	 private boolean facilityChangeReasonCodeSpecified;
	 
	 
	 @JsonInclude(Include.NON_NULL)
	 @JsonProperty("SegmentNumber")
	 private String segmentNumber;
	
	 @JsonProperty("SegmentNumberSpecified")
	 private boolean segmentNumberSpecified;
	 
	 @JsonProperty("RetrieveActionCode")
	 private String retrieveActionCode;
}
