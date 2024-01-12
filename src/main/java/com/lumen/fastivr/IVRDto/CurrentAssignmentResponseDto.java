package com.lumen.fastivr.IVRDto;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class CurrentAssignmentResponseDto {
	
	@JsonProperty("ReturnDataSet")
	private ReturnDataSet returnDataSet;
	
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
