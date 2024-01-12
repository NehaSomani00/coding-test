package com.lumen.fastivr.IVRDto.defectivepairs;

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
public class DefectivePairsInputData {
	
	@JsonProperty("LFACSEmployeeCode")
	private String lfacsEmployeeCode;
	
	@JsonProperty("CableId")
	private String cableId;
	
	@JsonProperty("WireCtrPrimaryNPANXX")
	private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;
	
	@JsonProperty("CablePairRange")
	private CablePairRange cablePairRange;

	private DefectivePairsInputData(Builder builder) {
		this.lfacsEmployeeCode = builder.lfacsEmployeeCode;
		this.cableId = builder.cableId;
		this.wireCtrPrimaryNPANXX = builder.wireCtrPrimaryNPANXX;
		this.cablePairRange = builder.cablePairRange;
	}
	
	public static class Builder {
		private String lfacsEmployeeCode;
		private String cableId;
		private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;
		private CablePairRange cablePairRange;
		
		public Builder lfacsEmployeeCode(String lfacsEmployeeCode) {
			this.lfacsEmployeeCode = lfacsEmployeeCode;
			return this;
		}
		
		public Builder cableId(String cableId) {
			this.cableId = cableId;
			return this;
		}
		
		public Builder wireCtrPrimaryNPANXX(WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX) {
			this.wireCtrPrimaryNPANXX = wireCtrPrimaryNPANXX;
			return this;
		}
		
		public Builder cablePairRange(CablePairRange cablePairRange) {
			this.cablePairRange = cablePairRange;
			return this;
		}
		
		public DefectivePairsInputData build() {
			return new DefectivePairsInputData(this);
		}
	}
}
