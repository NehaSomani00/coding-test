package com.lumen.fastivr.IVRDto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class NETTechDevices {
	
	@JsonProperty("deviceId")
	private JsonNode devices;
	
	@JsonProperty("error")
	private String error;

}
