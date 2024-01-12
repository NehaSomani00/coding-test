package com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class RetMaintChangeTicketInputData {

	
	 @JsonProperty("LFACSEmployeeCode")
	 private String LFACSEmployeeCode;
    
	 @JsonProperty("LFACSEntity")
	 private String LFACSEntity;
	
	 @JsonProperty("WireCtrPrimaryNPANXX")
	 private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;
	 
	 @JsonProperty("CableId")
	 private String cableId;

	 @JsonProperty("CableUnitId")
	 private String cableUnitId;
}
