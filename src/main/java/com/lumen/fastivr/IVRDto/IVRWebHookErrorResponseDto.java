package com.lumen.fastivr.IVRDto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IVRWebHookErrorResponseDto {
	
	@JsonProperty("session_id")
	private String sessionId ;
	
	@JsonProperty("current_state")
	private String state;
	
	@JsonProperty("hook_return_code")
	private String responseCode;
	
	@JsonProperty("hook_return_message")
	private String message;
	

}
