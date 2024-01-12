package com.lumen.fastivr.IVREntity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table(name = "PCOUT")
@Entity
@Getter
@Setter
@ToString
public class Pcout {

	@Id
	@Column(name = "PG_CNTR")
	private String pgCntr;
	
	@Column(name = "TIMESTAMP")
	private Date timestamp;
}
