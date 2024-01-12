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
public class LOOP {

	
	@JsonProperty("LPNO")
	private String LPNO;
	
	@JsonProperty("CKID")
    private String CKID;
	
	@JsonProperty("TID")
    private String TID;
	
	@JsonProperty("CKID2")
    private String CKID2;
	
	@JsonProperty("CKID3")
    private String CKID3;
	
	@JsonProperty("TEA")
    private String TEA;
	
	@JsonProperty("PORT")
    private String PORT;
	
	@JsonProperty("STAT")
    private String STAT;
	
	@JsonProperty("DATESpecified")
    private boolean DATESpecified;
	
	@JsonProperty("USOC")
    private String USOC;
	
	@JsonProperty("WOL")
    private String WOL;
	
	@JsonProperty("ADL")
    private String ADL;
	
	@JsonProperty("PTP")
    private String PTP;
	
	@JsonProperty("OWS")
    private String OWS;
	
	@JsonProperty("SSC")
    private String SSC;
	
	@JsonProperty("RTF")
    private String RTF;
	
	@JsonProperty("RLTNF")
    private String RLTNF;
	
	@JsonProperty("TSP")
    private String TSP;
	
	@JsonProperty("SSP")
    private String SSP;
	
	@JsonProperty("SSM")
    private String SSM;
	
	@JsonProperty("ESL")
    private String ESL;
	
	@JsonProperty("MKSG")
    private String MKSG;
	
	@JsonProperty("ACCT")
    private String ACCT;
	
	@JsonProperty("SUS")
    private String SUS;
	
	@JsonProperty("ADSR")
    private String ADSR;
	
	@JsonProperty("SUBL")
    private String SUBL;
	
	@JsonProperty("CSWEX")
    private String CSWEX;
	
	@JsonProperty("TRM")
    private String TRM;
	
	@JsonProperty("POS")
    private String POS;
    
    @JsonProperty("JACK")
    private String JACK;
    
    @JsonProperty("WW")
    private String WW;
    
    @JsonProperty("TASRMK")
    private String TASRMK;
    
    @JsonProperty("TFRMK")
    private String TFRMK;
    
    @JsonProperty("EXK")
    private String EXK;
    
    @JsonProperty("INVU")
    private String INVU;
    
    @JsonProperty("POUT")
    private String POUT;
    
    @JsonProperty("RTNN")
    private String RTNN;
    
    @JsonProperty("LPNAME")
    private String LPNAME;
    
    @JsonProperty("DAPROV")
    private String DAPROV;
    
    @JsonProperty("SMSC")
    private String SMSC;
    
    @JsonProperty("DCAPR")
    private String DCAPR;
    
    @JsonProperty("SRVTYP")
    private String SRVTYP;
        
    @JsonProperty("ADDR")
    private List<ADDR> ADDR;
    
    @JsonProperty("OEC")
    private List<OEC> OEC;
    
    @JsonProperty("SEG")
    private List<SEG> SEG;
    
    @JsonProperty("MORESO")
    private String MORESO;
    
    @JsonProperty("MORESOLP")
    private String MORESOLP;
    
    @JsonProperty("SO")
    private List<SO> SO;
}
