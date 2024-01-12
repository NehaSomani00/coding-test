package com.lumen.fastivr.IVREntity;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "MNET")
public class MnetEmp {
	
	@Id
	private String mnetId;
	private String cuid;
	private String type;
	private String companyId;
	private String companyName;
	private String lastName;
	private String firstName;
	private String middle;
	private String workPhone;
	private String pager;
	private String pagerPin;
	private String room;
	private String street;
	private String city;
	private String state;
	private String mgrCuid;	
    private Date mstrchgDate;
    private String rc;
    private String smtpAddr;
    private String postalCode;
    private Long mgrMnetId;
}
