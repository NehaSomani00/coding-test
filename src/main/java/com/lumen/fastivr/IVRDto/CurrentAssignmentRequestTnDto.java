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
public class CurrentAssignmentRequestTnDto {
	
//	@JsonProperty("RequestId")
//    private String requestId;
//
//    @JsonProperty("WebServiceName")
//    private String webServiceName;
//
//    @JsonProperty("RequestPurpose")
//    private String requestPurpose;
//
//    @JsonProperty("AuthorizationInfo")
//    private AuthorizationInfo authorizationInfo;
//
//    @JsonProperty("TargetSchemaVersionUsed")
//    private TargetSchemaVersionUsed targetSchemaVersionUsed;
//
//    @JsonProperty("TimeOutSecond")
//    private int timeOutSecond;

    @JsonProperty("InputData")
    private InputData inputData;


    
}

 


 


 

 


 

