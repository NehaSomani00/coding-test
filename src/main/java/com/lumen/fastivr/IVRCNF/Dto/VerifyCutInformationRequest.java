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
public class VerifyCutInformationRequest {

//	@JsonProperty("RequestId")
//	private String requestId;
//	
//	@JsonProperty("WebServiceName")
//	private String webServiceName;
//	
//	@JsonProperty("RequestPurpose")
//    private String requestPurpose;
//	
//	@JsonProperty("AuthorizationInfo")
//	private AuthorizationInfo authorizationInfo;
//	 
//	@JsonProperty("TargetSchemaVersionUsed")
//    private TargetSchemaVersionUsed targetSchemaVersionUsed;
//	
//	@JsonProperty("TimeOutSecond")
//	private int timeOutSecond;
//	
//	@JsonProperty("SendTimeStamp")
//	private Timestamp sendTimeStamp;
	
	@JsonProperty("InputData")
	private InputData inputData;
}
