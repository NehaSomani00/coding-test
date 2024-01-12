package com.lumen.fastivr.IVREndpointSecurity.userDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lumen.fastivr.IVREndpointSecurity.entity.EndpointSecurity;


public class IVREndpointUserDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private EndpointSecurity user;
	
	public IVREndpointUserDetails(EndpointSecurity user) {
		this.user = user;
	}

	public IVREndpointUserDetails(EndpointSecurity user, PasswordEncoder encoder) {
		user.setSecret(encoder.encode(user.getSecret()));
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		GrantedAuthority authority = () -> "USER";
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(authority);
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return user.getSecret();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return user.isEnabled();
	}

}
