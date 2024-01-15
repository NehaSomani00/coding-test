package com.test.userReg.UserController;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.test.userReg.UserEntity.User;
import com.test.userReg.UserService.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RestController
public class UserRegistrationController {

	private final UserDetailsService userDetailsService;

	final static Logger LOGGER = LoggerFactory.getLogger(UserRegistrationController.class);

	public UserRegistrationController(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@PostMapping(path = "/addUser", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addUser(@Valid @RequestBody User securityUser) {

			return new ResponseEntity<String>(String.valueOf(Math.random()).concat(securityUser.getUserName()), HttpStatus.OK);

	}
	@GetMapping("/user/{ip}")
	public ResponseEntity<String> getUserGeolocation(@PathVariable String ip) throws IOException, GeoIp2Exception {
		String response = userDetailsService.processUser(ip);
		return ResponseEntity.ok(response);
	}
	


}
