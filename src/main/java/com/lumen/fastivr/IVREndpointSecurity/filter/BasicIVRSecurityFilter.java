package com.lumen.fastivr.IVREndpointSecurity.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lumen.fastivr.IVREndpointSecurity.token.IVREndpointAuthenticationToken;


@Service
public class BasicIVRSecurityFilter extends OncePerRequestFilter {
	
	private final AuthenticationManager manager;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(BasicIVRSecurityFilter.class);
	
	public BasicIVRSecurityFilter(@Lazy AuthenticationManager manager) {
		this.manager = manager;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		try {
			String authValue = request.getHeader("Authorization");
			Map<String, String> base64Decoder = base64Decoder(authValue);
			String username = base64Decoder.get("username");
			String password = base64Decoder.get("password");
			GrantedAuthority authority = () -> "USER";
			List<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(authority);
			Authentication authRequest = new IVREndpointAuthenticationToken(username, password, authorities);
			Authentication authResult = manager.authenticate(authRequest);

			if (authResult.isAuthenticated()) {
				SecurityContextHolder.getContext().setAuthentication(authResult);
				filterChain.doFilter(request, response);
			} else {
				LOGGER.info("Bad credentials");
				throw new BadCredentialsException("User cannot be authenticated");
			}
		} catch (BadCredentialsException e) {
			LOGGER.error("Unauthorized access", e);
			response.sendError(401, "Unauthorized access");
		} catch(UsernameNotFoundException e) {
			LOGGER.error("Unauthorized access", e);
			response.sendError(401, "Unauthorized access");
		} catch (Exception e) {
			LOGGER.error("Unauthorized access", e);
			response.sendError(401, "Unauthorized access");
		}
	}
	
	/**
	 * don't apply the filter for add users
	 * If it returns true ,Then that request will bypass all the filters and go straight to controller  
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		boolean matches = request.getServletPath().matches("/ServiceAssurance/v1/Trouble/voiceIvrWebhook/security/addUser");
		LOGGER.info("servlet path: "+ request.getServletPath()+ ", matches: "+ matches);
		return matches;
//		return false;
	}



	private Map<String,String> base64Decoder(String authHeader) {
		byte[] decodeBytes = Base64.getDecoder().decode(authHeader.split(" ")[1]);
		String decodedString = new String(decodeBytes);
		String username = decodedString.substring(0, decodedString.indexOf(":"));
		String password = decodedString.substring(decodedString.indexOf(":")+1);
		
		Map<String,String> credentialsMap = new HashMap<>();
		credentialsMap.put("username", username);
		credentialsMap.put("password", password);
		
		return credentialsMap;
		
	}

}
