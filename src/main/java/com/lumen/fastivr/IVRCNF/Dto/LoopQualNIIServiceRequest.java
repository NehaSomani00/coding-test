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
public class LoopQualNIIServiceRequest {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("ARTISRequestHeader")
	private ARTISRequestHeader artisRequestHeader;
	
	@JsonProperty("TN")
	private TN tn;
	
	@JsonProperty("MessageSrcSystem")
	private String messageSrcSystem;
}
