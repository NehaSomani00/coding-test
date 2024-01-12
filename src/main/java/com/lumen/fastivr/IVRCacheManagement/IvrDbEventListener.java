package com.lumen.fastivr.IVRCacheManagement;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class IvrDbEventListener implements ApplicationRunner{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IvrDbEventListener.class);

	 private final DataSource dataSource;

	    @Autowired
	    public IvrDbEventListener(DataSource dataSource) {
	        this.dataSource = dataSource;
	    }

//	    @EventListener
//	    public void onApplicationShutdown(ContextClosedEvent event) {
//	        closeDatabaseConnections();
//	    }
	    
	    @Override
	    public void run(ApplicationArguments args) throws Exception {
	    	
	    	Runnable hookRunnable = () -> {
	    		//performs shutdown activities
	    		LOGGER.info("Application is shutting down, Performing cleanup");
	    		closeDatabaseConnections();
	    	};
	    	Thread hook = new Thread(hookRunnable, "ivr-shutdown-0");
	    	Runtime.getRuntime().addShutdownHook(hook);
	    }

	    private void closeDatabaseConnections() {
	    	LOGGER.info("Closing database connections at application shutdown");
	        if (dataSource instanceof javax.sql.DataSource) {
	            try {
	                ((javax.sql.DataSource) dataSource).getConnection().close();
	            } catch (SQLException e) {
	                // Handle or log the exception
	               LOGGER.error("Error while closing DB connections at application shutdown");
	            }
	        }
	    }


}
