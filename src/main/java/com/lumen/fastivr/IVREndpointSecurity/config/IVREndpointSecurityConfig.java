package com.lumen.fastivr.IVREndpointSecurity.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.lumen.fastivr.IVREndpointSecurity.authProvider.IVRAuthenticationProvider;
import com.lumen.fastivr.IVREndpointSecurity.filter.BasicIVRSecurityFilter;
import com.lumen.fastivr.IVREndpointSecurity.userDetails.IVREndpointUserDetailsService;

@Configuration
public class IVREndpointSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private BasicIVRSecurityFilter customFilter;
	
	@Autowired
	private IVRAuthenticationProvider authenticationProvider;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests()
		.antMatchers("/ServiceAssurance/v1/Trouble/voiceIvrWebhook/security/addUser").permitAll()
		.anyRequest().authenticated()
		.and()
		.httpBasic();
		http.addFilterAt(customFilter, BasicAuthenticationFilter.class);
		http.csrf().disable();
	}

	@Bean
	public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) throws Exception {
		JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
		userDetailsManager.setEnableAuthorities(false);
		userDetailsManager.setEnableGroups(true);
		String createUserSql = "insert into FASTIVR_ENDPOINT_SECURITY (USERNAME,SECRET_CODE,ENABLED_FLAG ) values ( ?, ?, ?)";
		String updateUserSql = "update FASTIVR_ENDPOINT_SECURITY set SECRET_CODE = ?, ENABLED_FLAG = ? where USERNAME = ?";
		userDetailsManager.setCreateUserSql(createUserSql);
		userDetailsManager.setUpdateUserSql(updateUserSql);
		return userDetailsManager;
	}
	
	@Bean
	public UserDetailsService userDetailsService() {
		 IVREndpointUserDetailsService userDetailsService = new IVREndpointUserDetailsService();
		 return userDetailsService;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider);
	}
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	
}
