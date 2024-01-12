package com.lumen.fastivr.IVRDto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class HostErrorList {
	
	@JsonProperty("Id")    
	private String id;
	
    @JsonProperty("ErrorList")   
    private List<ErrorList> errorList;
}
