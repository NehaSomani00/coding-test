package com.test.userReg.UserService;


import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.stereotype.Service;

import java.io.File;

import static com.test.userReg.utils.UserConstants.COUNTRY_CANADA;
import static com.test.userReg.utils.UserConstants.NOT_REGISTER_MESSAGE;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {


	private DatabaseReader dbReader;

	@Override
	public String processUser(String ipAddress)  throws GeoIp2Exception {
		File database = new File("C:/Desktop/");
		dbReader = new DatabaseReader.Builder(database).build();
		CityResponse response = dbReader.city(ipAddress);

		String countryName = response.getCountry().getName();

		if(COUNTRY_CANADA.equalsIgnoreCase(countryName)) {
			return String.valueOf(Math.random()).concat(countryName);
		}
		else {
			return NOT_REGISTER_MESSAGE;
		}

	}}
