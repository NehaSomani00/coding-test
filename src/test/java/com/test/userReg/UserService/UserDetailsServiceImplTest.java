package com.test.userReg.UserService;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private  DatabaseReader databaseReader;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void testProcessUserForCanada() throws GeoIp2Exception, IOException {
       String dbLocation = "C:\\Users\\yyyyy\\IdeaProjects\fileTest";
        File database = new File(dbLocation);
        DatabaseReader databaseReader = new DatabaseReader.Builder(database).build();
        InetAddress inetAddress = InetAddress.getByName("142.112.163.69");
        CityResponse response = databaseReader.city(inetAddress);
        String countryName = response.getCountry().getName();

        String countryDetails = userDetailsService.processUser("142.112.163.69");
        assertEquals("Canada", countryDetails);

    }

    @Test
    void testProcessUserForNonCanada() throws GeoIp2Exception, IOException {
        String dbLocation = "C:\\Users\\yyyyy\\IdeaProjects\fileTest";
        File database = new File(dbLocation);
        DatabaseReader databaseReader = new DatabaseReader.Builder(database).build();
        InetAddress inetAddress = InetAddress.getByName("163.69");
        CityResponse response = databaseReader.city(inetAddress);
        String countryName = response.getCountry().getName();

        String countryDetails = userDetailsService.processUser("163.69");

        assertNotEquals("Canada", countryDetails);

    }

}
