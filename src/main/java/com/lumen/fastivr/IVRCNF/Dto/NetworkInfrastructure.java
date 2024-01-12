package com.lumen.fastivr.IVRCNF.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class NetworkInfrastructure {

	@JsonProperty("NetworkInfrastructureIndicatorCode")
	private String networkInfrastructureIndicatorCode;
	
	@JsonProperty("Desc")
	private String desc;
	
	@JsonProperty("NetworkTopologyCode")
	private String networkTopologyCode;
	
	@JsonProperty("UpstreamTransportCode")
	private String upstreamTransportCode;
	
	@JsonProperty("DownstreamTransportCode")
	private String downstreamTransportCode;
	
	@JsonProperty("PairBondingFlag")
	private String pairBondingFlag;
	
	@JsonProperty("VoiceActivationFlag")
	private boolean voiceActivationFlag;
}
