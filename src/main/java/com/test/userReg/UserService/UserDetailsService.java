package com.test.userReg.UserService;

import com.maxmind.geoip2.exception.GeoIp2Exception;

import java.io.IOException;

public interface UserDetailsService {

	public String processUser(String ip) throws GeoIp2Exception, IOException;
}