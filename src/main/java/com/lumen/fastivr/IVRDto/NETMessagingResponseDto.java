package com.lumen.fastivr.IVRDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class NETMessagingResponseDto {
	
	@JsonProperty("eventId")
	private String eventId;
	
	@JsonProperty("eventTime")
	private String eventTime;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("reasonCode")
	private String reasonCode;
	
	@JsonProperty("reasonDescription")
	private String reasonDescription;

}
