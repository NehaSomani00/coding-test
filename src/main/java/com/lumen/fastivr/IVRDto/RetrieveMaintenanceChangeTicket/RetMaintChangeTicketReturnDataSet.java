package com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket;

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
public class RetMaintChangeTicketReturnDataSet {

	@JsonProperty("LFACSScreenName")
	private String LFACSScreenName;
    
	@JsonProperty("CandidatePairInfo")
	private List<CandidatePairInfo> CandidatePairInfo;
	
	
}
