package edu.iris.dmc.converter.control;

import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

public interface Controller {
	public void execute(List<String> queries, OutputStream outputStream)throws Exception ;
	public Properties getProperties();
}
