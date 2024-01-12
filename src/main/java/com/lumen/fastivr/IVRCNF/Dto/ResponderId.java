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
public class ResponderId {

	@JsonProperty("serviceName")
	private String serviceName;
	
	@JsonProperty("hostName")
	private String hostName;
	
	@JsonProperty("serverName")
	private String serverName;
}
