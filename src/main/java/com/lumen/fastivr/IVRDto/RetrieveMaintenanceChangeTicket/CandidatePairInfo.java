package com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class CandidatePairInfo {
	
	 @JsonProperty("CableId")
	 private String cableId;

	 @JsonProperty("CableUnitId")
	 private String cableUnitId;

	@JsonProperty("BindingPostColorCode")
	private String bindingPostColorCode;
	
	@JsonProperty("PairStatus")
	private String pairStatus;
	
	@JsonProperty("PairSelectionInfo")
	private String pairSelectionInfo;
    
	
}
