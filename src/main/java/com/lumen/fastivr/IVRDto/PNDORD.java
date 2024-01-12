package com.lumen.fastivr.IVRDto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class PNDORD {

	@JsonProperty("SOID")
	private String SOID;
	
	@JsonProperty("ORD")
	private String ORD;
	
	@JsonProperty("DD")
	private Timestamp DD;
	
	@JsonProperty("DDSpecified")
	private boolean DDSpecified;
}
