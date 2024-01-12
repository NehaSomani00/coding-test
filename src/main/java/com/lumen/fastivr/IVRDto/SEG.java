package com.lumen.fastivr.IVRDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class SEG {
	
	
	@JsonProperty("SEGNO")
	private String SEGNO;
	
	@JsonProperty("CA")
	private String CA;
	
	@JsonProperty("PR")
	private String PR;
	
	@JsonProperty("LSTAT")
	private String LSTAT;
	
	@JsonProperty("BP")
	private String BP;
	
	@JsonProperty("OBP")
	private String OBP;
	
	@JsonProperty("LPORG")
	private String LPORG;
	
	@JsonProperty("RLOE")
	private String RLOE;
	
	@JsonProperty("RLOA")
	private String RLOA ;
	
	@JsonProperty("RLOC")
	private String RLOC;
	
	@JsonProperty("LOTI")
	private String LOTI;
	
	@JsonProperty("TEA")
	private String TEA;
	
	@JsonProperty("TP")
	private String TP;
	
	@JsonProperty("RT")
	private String RT;
	
	@JsonProperty("RZ")
	private String RZ;
	
	@JsonProperty("TEPREF")
	private String TEPREF;
	
	@JsonProperty("TECA")
	private String TECA;
	
	@JsonProperty("TEPR")
	private String TEPR;
	
	@JsonProperty("TEC")
	private String TEC;
	
	@JsonProperty("RELAYRACK")
	private String RELAYRACK;
	
	@JsonProperty("PORT")
	private String PORT;
	
	@JsonProperty("MODEL")
	private String MODEL;
	
	@JsonProperty("COMM")
	private String COMM;
	
	@JsonProperty("UF")
	private String UF;
	
	@JsonProperty("SC")
	private String SC;
	
	@JsonProperty("TETE")
	private String TETE;
	
	@JsonProperty("CQ")
	private String CQ;
	
	@JsonProperty("DEF")
	private String DEF;
	
	@JsonProperty("DEFDSpecified")
	private boolean DEFDSpecified;
	
	@JsonProperty("CTT")
	private String CTT;
	
	@JsonProperty("DEFL")
	private String DEFL;
	
	@JsonProperty("LT")
	private String LT;
	
	@JsonProperty("LNOP")
	private String LNOP;
	
	@JsonProperty("SYSTP")
	private String SYSTP;
	
	@JsonProperty("PGSNO")
	private String PGSNO;
	
	@JsonProperty("FLDLTS")
	private String FLDLTS;
	
	@JsonProperty("COLTS")
	private String COLTS;
	
	@JsonProperty("CNST")
	private String CNST;
	
	@JsonProperty("ORIG")
	private String ORIG;
	
	@JsonProperty("MPROV")
	private String MPROV;
	
	@JsonProperty("MCLLI")
	private String MCLLI;
	
	@JsonProperty("MLOC")
	private String MLOC;
	
	@JsonProperty("MCA")
	private String MCA;
	
	@JsonProperty("MPR")
	private String MPR;
	
	@JsonProperty("RLA")
	private String RLA;
	
	@JsonProperty("SDP")
	private String SDP;
	
	@JsonProperty("TSP")
	private String TSP;
	
	@JsonProperty("SERIALNO")
	private String SERIALNO;
	
	@JsonProperty("INDEXNUM")
	private String INDEXNUM;
	
	@JsonProperty("TPR")
	private String TPR;
	
	@JsonProperty("LMURMK")
	private String LMURMK;
	
	@JsonProperty("EWOEWO")
	private String EWOEWO;
	
	@JsonProperty("EWOID")
	private String EWOID;
	
	@JsonProperty("EWODD")
	private String EWODD;
	
	@JsonProperty("LSTID")
	private String LSTID;
	
	@JsonProperty("SOLST")
	private String SOLST;
	
	@JsonProperty("SOITM")
	private String SOITM;
	
	@JsonProperty("SOLSTDD")
	private String SOLSTDD;
	
	@JsonProperty("RSVINFO")
	private String RSVINFO;
	
	@JsonProperty("RSVDATSpecified")
	private boolean RSVDATSpecified;
	
	@JsonProperty("RSVRMK")
	private String RSVRMK;
	
	@JsonProperty("RSTTE")
	private String RSTTE;
	
	@JsonProperty("PERM")
	private String PERM;
	
	@JsonProperty("XRST")
	private String XRST;
	
	@JsonProperty("RMK0TE")
	private String RMK0TE;
	
	@JsonProperty("RMK0PR")
	private String RMK0PR;
	
	@JsonProperty("STRLOC")
	private String STRLOC;
	
	@JsonProperty("CQC")
	private String CQC;
	
	@JsonProperty("CDC")
	private String CDC;
	
	@JsonProperty("ASGBPR")
	private String ASGBPR;
	
	@JsonProperty("ASBPSTAT")
	private String ASBPSTAT;
	
	@JsonProperty("PGSTP")
	private String PGSTP;
	
	@JsonProperty("LTS")
	private String LTS;
	
	@JsonProperty("DLE")
	private String DLE;
	
	@JsonProperty("TSI")
	private String TSI;
	
	@JsonProperty("DLERMK")
	private String DLERMK;
	
	@JsonProperty("DLERST")
	private String DLERST;
	
	@JsonProperty("DLEONU")
	private String DLEONU;
	
	@JsonProperty("ONURST")
	private String ONURST;
	
	@JsonProperty("ONUXRST")
	private String ONUXRST;
	
	@JsonProperty("ONURMK")
	private String ONURMK;
	
	@JsonProperty("TFCA1")
	private String TFCA1;
	
	@JsonProperty("TFCA2")
	private String TFCA2;
	
	@JsonProperty("TFPR1")
	private String TFPR1;
	
	@JsonProperty("TFPR2")
	private String TFPR2;
	
	@JsonProperty("FICTMED1")
	private String FICTMED1;
	
	@JsonProperty("FICTMED2")
	private String FICTMED2;
	
	@JsonProperty("FICTEA1")
	private String FICTEA1;
	
	@JsonProperty("FICTEA2")
	private String FICTEA2;
	
	@JsonProperty("FICTYPE1")
	private String FICTYPE1;
	
	@JsonProperty("FICTYPE2")
	private String FICTYPE2;
	
	@JsonProperty("ABPRSVINFO")
	private String ABPRSVINFO;
	
	@JsonProperty("ABPRSVDAT")
	private String ABPRSVDAT;
	
	@JsonProperty("ABPRSVRMK")
	private String ABPRSVRMK;
	
	@JsonProperty("ASGBP")
	private String ASGBP;
	
	@JsonProperty("TF1")
	private String TF1;

}
