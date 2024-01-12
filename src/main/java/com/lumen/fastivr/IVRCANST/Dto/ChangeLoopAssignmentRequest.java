package com.lumen.fastivr.IVRCANST.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lumen.fastivr.IVRCNF.Dto.ChangeLoopAssignmentRequestInputData;
import com.lumen.fastivr.IVRDto.AuthorizationInfo;
import com.lumen.fastivr.IVRDto.TargetSchemaVersionUsed;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class ChangeLoopAssignmentRequest {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("RequestId")
    private String requestId;

    @JsonProperty("WebServiceName")
    private String webServiceName;

    @JsonProperty("RequestPurpose")
    private String requestPurpose;

    @JsonProperty("AuthorizationInfo")
    private AuthorizationInfo authorizationInfo;

    @JsonProperty("TargetSchemaVersionUsed")
    private TargetSchemaVersionUsed targetSchemaVersionUsed;

    @JsonProperty("TimeOutSecond")
    private int timeOutSecond;	

	@JsonProperty("InputData")
	private ChangeLoopAssignmentInputData inputData;
}
