package edu.iris.dmc.converter.control;

import java.util.Properties;

public abstract class AbstractController implements Controller {

	protected Properties properties = new Properties();

	@Override
	public Properties getProperties() {
		return properties;
	}
}
