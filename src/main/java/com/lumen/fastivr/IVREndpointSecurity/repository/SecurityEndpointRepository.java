package com.lumen.fastivr.IVREndpointSecurity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lumen.fastivr.IVREndpointSecurity.entity.EndpointSecurity;


public interface SecurityEndpointRepository extends JpaRepository<EndpointSecurity, String> {
	
	 Optional<EndpointSecurity> findByUsername(String username);

}
