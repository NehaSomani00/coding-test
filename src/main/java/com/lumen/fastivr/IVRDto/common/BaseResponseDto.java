package com.lumen.fastivr.IVRDto.common;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lumen.fastivr.IVRDto.ARTISInformation;
import com.lumen.fastivr.IVRDto.MessageStatus;
import com.lumen.fastivr.IVRDto.TargetSchemaVersionUsed;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class BaseResponseDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("RequestId")
    private String requestId;
	
	@JsonProperty("WebServiceName")
    private String webServiceName;
	
	@JsonProperty("TargetSchemaVersionUsed")
    private TargetSchemaVersionUsed targetSchemaVersionUsed;
	
	@JsonProperty("MessageStatus")
    private MessageStatus messageStatus;
	
	@JsonProperty("ARTISInformation")
    private ARTISInformation artisInformation;
	
	@JsonProperty("CompletedTimeStamp")
    private String completedTimeStamp;
	
	@JsonProperty("CompletedTimeStampSpecified")
    private boolean completedTimeStampSpecified;
}
