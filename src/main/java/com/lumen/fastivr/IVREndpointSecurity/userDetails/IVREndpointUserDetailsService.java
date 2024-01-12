package com.lumen.fastivr.IVREndpointSecurity.userDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.lumen.fastivr.IVREndpointSecurity.entity.EndpointSecurity;
import com.lumen.fastivr.IVREndpointSecurity.repository.SecurityEndpointRepository;

public class IVREndpointUserDetailsService implements UserDetailsService {

	@Autowired
	private SecurityEndpointRepository repository;
	
	@Override
	@Cacheable("users")
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		EndpointSecurity user = repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username donot exisit"));
		UserDetails userDetails = new IVREndpointUserDetails(user);
		return userDetails;
	}
	

}
