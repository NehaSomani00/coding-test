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
public class NIIServiceResponse {

	@JsonProperty("ARTISResponseHeader")
	private ARTISResponseHeader artisResponseHeader;
	
	@JsonProperty("SvcAddress")
	private SvcAddress svcAddress;
	
	@JsonProperty("TN")
	private TN tn;
	
	@JsonProperty("UsageMessage")
	private String usageMessage;
	
	@JsonProperty("NetworkInfrastructure")
	private List<NetworkInfrastructure> networkInfraStructure;
	
	@JsonProperty("ErrorCode")
	private String errorCode;
	
	@JsonProperty("ErrorMessage")
	private String errorMessage;
}
