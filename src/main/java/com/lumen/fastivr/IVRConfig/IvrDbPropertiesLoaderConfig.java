package com.lumen.fastivr.IVRConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

@Configuration
public class IvrDbPropertiesLoaderConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(IvrDbPropertiesLoaderConfig.class);

	// Name of the custom property source to be added by this post-processor class.
	private static final String PROPERTY_SOURCE_NAME = "application-db-properties";
	@Autowired
	private DataSource dataSource;

	@Autowired
	private ConfigurableApplicationContext applicationContext;

	@PostConstruct
	public void loadDbProperties() {
		// to get hold of the values in application-{env}.properties
		ConfigurableEnvironment env = applicationContext.getEnvironment();
		Map<String, Object> propertySources = new HashMap<>();
		try (Connection conn = dataSource.getConnection();
				PreparedStatement ps = conn.prepareStatement("SELECT * from FASTIVR_PROPERTY");) {

			ResultSet resultSet = ps.executeQuery();
			LOGGER.info("Loading properties from DB");
			while (resultSet.next()) {
				if (resultSet.getString("ENABLE_IND").equalsIgnoreCase("Y")) {
					// the property is enabled
					String key = resultSet.getString("PROP_NAME");
					String value = resultSet.getString("PROP_VALUE");
					LOGGER.info("Key: " + key + ", Value: " + value);
					propertySources.put(key, value);
				}
			}

			resultSet.close();
			ps.clearParameters();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// set the database properties in the environment level
		MapPropertySource mapPropertySource = new MapPropertySource(PROPERTY_SOURCE_NAME, propertySources);
		env.getPropertySources().addFirst(mapPropertySource);
	}
}
