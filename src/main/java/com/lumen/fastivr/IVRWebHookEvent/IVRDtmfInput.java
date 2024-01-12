package com.lumen.fastivr.IVRWebHookEvent;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class IVRDtmfInput {
	
	@JsonProperty("dtmf_input")
	private String dtmfInput;

}
