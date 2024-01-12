package com.lumen.fastivr.IVRDto.retrieveLoopAssignment;

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
public class BasicAddress {
	
	 @JsonProperty("StreetNumber")
	 private String StreetNumber;
    
	 @JsonProperty("StreetName")
	 private String StreetName;
	
	 @JsonProperty("Community")
	 private String Community;
	 
	 @JsonProperty("StateProvinceName")
	 private String StateProvinceName;
	 
	 @JsonProperty("AddressDesignator")
	 private List<AddressDesignator> AddressDesignator;

}
