package edu.iris.dmc.converter;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


//@Configuration
public class AppConfig {
	//@Autowired
	//Environment env;

	//@Bean
	CommandLineArgs getOptions() {
		return new CommandLineArgs();

	}

	//@Bean
	public String getModule(VersionProvider versionProvider) {
		return "fdsn-stationxml-converter/" + versionProvider.getVersion();
	}

	//@Bean
	public VersionProvider getVersionProvider() {
		ResourceBundle rb;
		try {
			rb = ResourceBundle.getBundle("application");
			return new VersionProvider(rb.getString("application.version"),
					rb.getString("build.date"));
		} catch (MissingResourceException e) {
			// LOGGER.warn("Resource bundle 'application' was not found or error while reading current version.");
			e.printStackTrace();
		}
		return null;
	}

}
