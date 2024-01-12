package com.lumen.fastivr.IVREntity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@IdClass(NpaId.class)
@Table(name = "tn_info",schema = "FASTIVR_OWNER")
public class TNInfo {
	@Id
	@Column(name="NPA_PRFX")
	private String npaPrefix;
	@Id
	@Column(name="NXX_PRFX")
	private String nxxPrefix;
	@Column(name="BT_NPA_PRFX")
	private String btNpaprfx;
	@Column(name="TIMESTAMP")
	private Timestamp npaTimestamp;
	
	
	
	

}