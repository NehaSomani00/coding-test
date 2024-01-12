package com.lumen.fastivr.IVREndpointSecurity.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "FASTIVR_ENDPOINT_SECURITY")
@Getter
@Setter
public class EndpointSecurity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "USERNAME")
	private String username;
	
	@Column(name = "SECRET_CODE")
	private String secret;
	
	@Column(name = "ENABLED_FLAG")
	private boolean enabled;
}
