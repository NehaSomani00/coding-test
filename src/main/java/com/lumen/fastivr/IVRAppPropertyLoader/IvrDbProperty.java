package com.lumen.fastivr.IVRAppPropertyLoader;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "FASTIVR_PROPERTY", schema = "FASTIVR_OWNER")
@Getter
@Setter
@ToString
public class IvrDbProperty implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
 
	@Column(name = "PROP_ID")
	@Id
	private Integer id;
	
	@Column(name = "PROP_NAME")
	private String name;
	
	@Column(name = "PROP_VALUE")
	private String value;
	
	@Column(name = "ENABLE_IND")
	private String enable;
	
}
