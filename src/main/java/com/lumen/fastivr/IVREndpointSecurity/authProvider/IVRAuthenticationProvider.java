package com.lumen.fastivr.IVREndpointSecurity.authProvider;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lumen.fastivr.IVREndpointSecurity.token.IVREndpointAuthenticationToken;


@Service
public class IVRAuthenticationProvider implements AuthenticationProvider  {
	
	private final PasswordEncoder passwordEncoder;
	private final UserDetailsService userDetailsService;
	
	public IVRAuthenticationProvider(@Lazy PasswordEncoder passwordEncoder, @Lazy UserDetailsService userDetailsService) {
		this.passwordEncoder = passwordEncoder;
		this.userDetailsService = userDetailsService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		//authentication class should be of CustomAuthentication type
		if (!supports(authentication.getClass())) {
			return null;
		}
		
		//this has to be fetched from DB or application properties file
		String username = authentication.getName();
		String secret = String.valueOf(authentication.getCredentials());
		UserDetails securedUser = userDetailsService.loadUserByUsername(username);
		
		if (passwordEncoder.matches(secret,securedUser.getPassword())) {
			return new UsernamePasswordAuthenticationToken(username, secret,securedUser.getAuthorities());
		}
			
		throw new BadCredentialsException("Wrong credentials or User is disabled");
	}

	/**
	 * Checks the authentication class type 
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return IVREndpointAuthenticationToken.class.equals(authentication);
	}

}
