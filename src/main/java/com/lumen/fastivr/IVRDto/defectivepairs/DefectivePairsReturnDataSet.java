package com.lumen.fastivr.IVRDto.defectivepairs;

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
public class DefectivePairsReturnDataSet {
	
	@JsonProperty("DefectivePairDetails")
	private List<DefectivePairDetailsT> defectivePairDetails;
	
	@JsonProperty("AdditionalDefectivePairsFlagSpecified")
    private boolean additionalDefectivePairsFlagSpecified;
	
	@JsonProperty("AdditionalDefectivePairsFlag")
	private boolean additionalDefectivePairsFlag;
	
}

