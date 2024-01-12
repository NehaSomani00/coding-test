package com.lumen.fastivr.IVREndpointSecurity.service;

import com.lumen.fastivr.IVREndpointSecurity.entity.EndpointSecurity;

public interface EndpointSecurityService {
	
	void addUser(EndpointSecurity user);
	
	void updateUser(EndpointSecurity user);

}
