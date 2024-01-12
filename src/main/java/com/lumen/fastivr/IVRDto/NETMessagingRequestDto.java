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
public class NETMessagingRequestDto {

	@JsonProperty("applicationId")
	private String applicationId;
	
	@JsonProperty("applicationKey")
	private String applicationKey;
	
	@JsonProperty("to")
	private String to;
	
	@JsonProperty("from")
	private String from;
	
	@JsonProperty("sendType")
	private String sendType;
	
	@JsonProperty("device")
	private String device;
	
	@JsonProperty("subject")
	private String subject;
	
	@JsonProperty("messageText")
	private String messageText;
}
