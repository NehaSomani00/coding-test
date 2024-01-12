package com.test.userReg.UserService;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private  DatabaseReader databaseReader;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void testProcessUserForCanada() throws GeoIp2Exception {
        String dbLocation = "your-path";
        File database = new File(dbLocation);
        Country country = new Country();
        country.setName("Canada");
        CityResponse response = new CityResponse();
        response.setCountry(country);

        when(databaseReader.city("142.112.163.69")).thenReturn(response);

        String countryDetails = userDetailsService.processUser("142.112.163.69");
        assertEquals("Canada", countryDetails);

    }

    @Test
    void testProcessUserForNonCanada() throws GeoIp2Exception {
        String dbLocation = "your-path";
        File database = new File(dbLocation);

        Country country = new Country();
        country.setName("India");
        CityResponse response = new CityResponse();
        response.setCountry(country);

        when(databaseReader.city("163.69")).thenReturn(response);

        String countryDetails = userDetailsService.processUser("163.69");
        assertNotEquals("Canada", countryDetails);

    }

}
