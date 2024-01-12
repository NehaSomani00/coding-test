package com.lumen.fastivr.IVREntity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TECHNICIAN", schema = "FASTIVR_OWNER")
public class FastIvrUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CUID")
	private String cuid; // Mnet cuid

	@Column(name = "EMPID")
	private String empID; // Login Code

	@Column(name = "FAX_TELEPHONE")
	private String faxTN; // fax Telephone Number

	@Column(name = "NPA_PREFIX")
	private String npaPrefix; // default NPA

	@Column(name = "CUT_PG_SENT")
	private String cutPageSent; // cut page (Y) or (N)

	@Column(name = "ISENABLED")
	private String isEnabledFlag; // enabled (Y) or disabled (N)

	@Column(name = "PASSWORD")
	private String password; // Password

	@Column(name = "EC")
	private String ec; // Employee Code

	@Column(name = "MC")
	private String mc; // Maintenance Center

	@Column(name = "EXPIREDATE")
	private LocalDate expireDate; // sysdate + age

	@Column(name = "AGE")
	private Integer age; // Age of the password (30)

	@Column(name = "BIRTHDATE")
	private String birthdate; // BirthDate

	@Column(name = "BIRTHDATECTR")
	private Integer birthdateCounter; // Counts birthdate entered attempts

	@Column(name = "PASSWORDCTR")
	private Integer passwordCounter; // Counts Login Password attempts

	@Column(name = "LOGINJEP")
	private String loginJeopardyFlag; // (Y) or (N)

	@Column(name = "SPLICER")
	private String isTechASplicer;

	@Column(name = "COMMENTS")
	private String comments;

	@Column(name = "DEBUGFLAG")
	private String debugFlag;

	@Column(name = "PAGERCO")
	private String pagerCo;
	
	public int getAge() {
		return age != null ? age : 0;
	}
	
	public void setAge(Integer age) {
		this.age = age;
	}
	
	public int getBirthdateCounter() {
		return birthdateCounter!=null ? birthdateCounter: 0;
	}
	
	public void setBirthdateCounter(Integer birthdateCounter) {
		this.birthdateCounter = birthdateCounter;
	}
	
	public int getPasswordCounter() {
		return passwordCounter!=null ? passwordCounter: 0;
	}
	
	public void setPasswordCounter(Integer passwordCounter) {
		this.passwordCounter = passwordCounter;
	}
}
