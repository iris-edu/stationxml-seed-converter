package edu.iris.dmc.station.converter;

import edu.iris.dmc.station.Application;

public class ApplicationTest {

	public static void main(String[] args) throws Exception {
		
		//args=new String[] {"--output","/Users/Suleiman/converted.xml","--input","/Users/Suleiman/dataless/II_NNA.20180906T205825.dataless"};
		
		args=new String[] {"--input","/Users/Suleiman/loaded-dataless","--output","/Users/Suleiman/test-conversion","--verbose"};
		
		Application app = new Application();
		app.main(args);

	}

}
