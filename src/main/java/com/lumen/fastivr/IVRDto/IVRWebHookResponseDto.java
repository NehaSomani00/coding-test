package com.lumen.fastivr.IVRDto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IVRWebHookResponseDto {

	@JsonProperty("session_id")
	private String sessionId;
	
	@JsonProperty("current_state")
	private String currentState;
	
	@JsonProperty("hook_return_code")
	private String hookReturnCode;
	
	@JsonProperty("hook_return_message")
	private String hookReturnMessage;
	
	@JsonProperty("parameters")
	private List<IVRParameter> parameters;
}
