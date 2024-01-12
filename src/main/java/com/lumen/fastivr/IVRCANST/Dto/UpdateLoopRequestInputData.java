package com.lumen.fastivr.IVRCANST.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class UpdateLoopRequestInputData {
	
	
	@JsonProperty("LFACSEmployeeCode")
	private String lFACSEmployeeCode;
	
	@JsonProperty("LFACSEntityCode")
	private String lfacsEntityCode;
	
	@JsonProperty("WireCtrPrimaryNPANXX")
	private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;
	
	@JsonProperty("CircuitId")
	private String circuitId;

	@JsonProperty("CurrentTerminalAddress")
	private String currentTerminalAddress;

	@JsonProperty("NewTerminalAddress")
	private String newTerminalAddress;

	@JsonProperty("NonPublishIndicator")
	private String nonPublishIndicator;
	
	@JsonProperty("FacilityAvailabilityFlag")
	private boolean facilityAvailabilityFlag;

	@JsonProperty("CustomerName")
	private String customerName;
	
}
