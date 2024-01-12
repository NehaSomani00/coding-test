package com.lumen.fastivr.IVREntity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "NPA")
public class Npa {
	
	@Id
	@Column(name = "NPA_PRFX")
	private String npaPrefix;
	
	@Column(name = "STATE_US")
	private String stateUS;
	
	@Column(name = "NPA_DESC")
	private String npaDesc;
	
	@Column(name = "TIME_ZONE")
	private String timezone;
	
	@Column(name = "CTWEB_HL")
	private String ctweb;
	
	@Column(name = "TIMESTAMP")
	private Date timestamp;

}
