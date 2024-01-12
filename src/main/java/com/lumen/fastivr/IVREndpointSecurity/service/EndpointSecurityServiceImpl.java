package com.lumen.fastivr.IVREndpointSecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

import com.lumen.fastivr.IVREndpointSecurity.entity.EndpointSecurity;
import com.lumen.fastivr.IVREndpointSecurity.userDetails.IVREndpointUserDetails;


@Service
public class EndpointSecurityServiceImpl implements EndpointSecurityService {

	@Autowired
	private JdbcUserDetailsManager userDetailsManager;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public void addUser(EndpointSecurity user) {
		IVREndpointUserDetails userDetails = new IVREndpointUserDetails(user, passwordEncoder);
		userDetailsManager.createUser(userDetails);
	}

	@Override
	public void updateUser(EndpointSecurity user) {
		IVREndpointUserDetails userDetails = new IVREndpointUserDetails(user, passwordEncoder);
		userDetailsManager.updateUser(userDetails);
		
	}

}
