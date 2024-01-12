package com.lumen.fastivr.IVREndpointSecurity.token;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class IVREndpointAuthenticationToken extends UsernamePasswordAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IVREndpointAuthenticationToken(Object principal, Object credentials) {
		super(principal, credentials);
	}

	public IVREndpointAuthenticationToken(Object principal, Object credentials,
			Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
		// TODO Auto-generated constructor stub
	}
	
}
