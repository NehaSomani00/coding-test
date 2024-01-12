package com.lumen.fastivr.IVRDto.multipleappearance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class TerminalDetail {
	
	@JsonProperty("TerminalAddress")
	private String terminalAddress;
	
	@JsonProperty("TerminalType")
	private String terminalType;
	
	@JsonProperty("CandidatePairStatus")
	private String candidatePairStatus;
	
	@JsonProperty("BindingPostColorCode")
	private String bindingPostColorCode;
	

}
