package com.lumen.fastivr.IVRDto;

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
public class MessageStatus {
	
	
	@JsonProperty("ErrorCode")
	private String errorCode;
	
	@JsonProperty("ErrorMessage")
    private String errorMessage;
	
	@JsonProperty("SeverityLevel")
    private String severityLevel;
	
	@JsonProperty("ErrorStatus")
    private String errorStatus;
	
	@JsonProperty("HostErrorList")
	private List<HostErrorList> hostErrorList;

}
