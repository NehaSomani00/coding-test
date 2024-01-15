package com.test.userReg.UserService;


import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import static com.test.userReg.utils.UserConstants.COUNTRY_CANADA;
import static com.test.userReg.utils.UserConstants.NOT_REGISTER_MESSAGE;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {


	private DatabaseReader dbReader;

	@Override
	public String processUser(String ipAddress) throws GeoIp2Exception, IOException {
		File database = new File("C:\\Users\\yyyyy\\IdeaProjects\fileTest");
		dbReader = new DatabaseReader.Builder(database).build();
		InetAddress inetAddress = InetAddress.getByName(ipAddress);
		CityResponse response = dbReader.city(inetAddress);

		String countryName = response.getCountry().getName();

		if(COUNTRY_CANADA.equalsIgnoreCase(countryName)) {
			return String.valueOf(Math.random()).concat(countryName);
		}
		else {
			return NOT_REGISTER_MESSAGE;
		}

	}}
