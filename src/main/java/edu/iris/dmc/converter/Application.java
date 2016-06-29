package edu.iris.dmc.converter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import edu.iris.dmc.converter.control.Controller;
import edu.iris.dmc.converter.control.SeedToStationRequestController;
import edu.iris.dmc.converter.control.StationToSeedRequestController;

public class Application {
	private static ResourceBundle rb = ResourceBundle.getBundle("application");

	public static void main(String[] args) throws Exception {
		//args = new String[] { "--verbose", "-so", "RESIF", "-se", "RESIF", "-s", "--seed", "--output",
				//"ATD.LKI.dataless", "/Users/Suleiman/Downloads/ATD.LKI.xml" };

		Application application = new Application();
		application.run(args);
	}

	public void run(String... args) {

		CommandLineArgs commandLineArgs = new CommandLineArgs();
		CmdLineParser cmdLineParser = new CmdLineParser(commandLineArgs);
		if (args == null) {
			exitWithError("Invalid number of arguments", cmdLineParser, commandLineArgs.getUsagePrefix());
		}
		Controller controller = null;
		try {
			cmdLineParser.parseArgument(args);
			Logger logManager = LogManager.getLogManager().getLogger("");
			if (commandLineArgs.isVerbose()) {
				for (Handler h : logManager.getHandlers()) {
					h.setLevel(Level.INFO);
				}
			} else {
				for (Handler h : logManager.getHandlers()) {
					h.setLevel(Level.OFF);
				}
			}
			if (commandLineArgs.isVersion()) {
				System.out.println(rb.getString("application.version"));
				System.exit(0);
			}

			if (commandLineArgs.isHelp()) {
				System.err.println(commandLineArgs.getUsagePrefix());
				cmdLineParser.printUsage(System.err);
				System.exit(0);
			}

			if (commandLineArgs.isSeed()) {
				controller = new StationToSeedRequestController();
			} else if (commandLineArgs.isXml()) {
				if (commandLineArgs.getSource() == null) {
					System.err.println("No source specified (required for XML output), try including the -so option");
					System.err.println("Use the -h option to see the help message");
					System.exit(3);
				}

				controller = new SeedToStationRequestController();
				controller.getProperties().put("sender", commandLineArgs.getSender());
				controller.getProperties().put("source", commandLineArgs.getSource());
				controller.getProperties().put("format", commandLineArgs.isPrettyPrint());
				controller.getProperties().put("module", this.getModule());
			} else {
				exitWithError("No output format specified! <--xml,--seed>", cmdLineParser,
						commandLineArgs.getUsagePrefix());
			}

			List<String> inputList = new ArrayList<String>();

			if (commandLineArgs.getInputFile() != null) {
				FileInputStream fstream = null;
				try {
					File f = new File(commandLineArgs.getInputFile());

					fstream = new FileInputStream(f);
					DataInputStream in = new DataInputStream(fstream);
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String strLine;
					while ((strLine = br.readLine()) != null) {
						if (strLine != null && strLine.length() > 0) {
							inputList.add(strLine);
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (fstream != null) {
						try {
							fstream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			} else {
				// get the single file

				if (commandLineArgs.getInputFiles() == null || commandLineArgs.getInputFiles().isEmpty()) {
					System.err.println("Please provide input list file.");
					System.err.println("Use the -h option to see the help message");
					System.exit(3);
				} else {
					for (String strLine : commandLineArgs.getInputFiles()) {
						if (strLine != null && strLine.length() > 0) {
							inputList.add(strLine);
						}
					}
				}
			}

			assert(controller != null);
			OutputStream outStream = null;
			try {
				if (commandLineArgs.getOutputFile() != null) {
					outStream = new FileOutputStream(commandLineArgs.getOutputFile());
				} else {
					outStream = System.out;
				}
				controller.execute(inputList, outStream);
			} catch (IncompleteContentException e) {
				System.err.print(e);
				System.exit(2);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.print(e);
				System.exit(4);
			} finally {
				if (outStream != null) {
					try {
						outStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			System.exit(0);

		} catch (CmdLineException e) {
			exitWithError(e.getMessage(), cmdLineParser, commandLineArgs.getUsagePrefix());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void exitWithError(String errorMsg, CmdLineParser cmdLineParser, String usagePrefix) {

		System.err.println("\nError: " + errorMsg + "\n\n");
		System.err.println(usagePrefix);
		cmdLineParser.printUsage(System.err);

		System.exit(1);
	}

	public String getModule() {
		return "fdsn-stationxml-converter/" + rb.getString("application.version");
	}

}
