package edu.iris.dmc;

import edu.iris.dmc.station.Application;

public class FileRunner {

	public static void main(String[] args) throws Exception {
		args = new String[] { "--input","/Users/Suleiman/ARCES_DATALESS.SEED" ,"--output","/Users/Suleiman/ARCES_DATALESS.SEED.xml" };
		Application app = new Application();
		app.main(args);

	}

}
