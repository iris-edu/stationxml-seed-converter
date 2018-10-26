package edu.iris.dmc.station;

import java.io.File;
import java.io.IOException;

import edu.iris.dmc.station.converter.MetadataFileFormatConverter;
import edu.iris.dmc.station.converter.SeedToXmlFileConverter;
import edu.iris.dmc.station.converter.XmlToSeedFileConverter;

public class Application {

	// private static ResourceBundle rb = ResourceBundle.getBundle("application");
	private boolean debug;

	public static void main(String[] args) throws Exception {
		Application application = new Application();
		application.run(args);
	}

	public void run(String... args) {

		if (args == null || args.length == 0) {
			exitWithError("Invalid number of arguments");
		}

		File source = null;
		File target = null;

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if ("--verbose".equals(arg) || "-v".equals(arg)) {
				debug = true;
				if(debug) {
					System.out.println("SEED >< XML CONVERTER");
				}
			} else if ("--help".equals(arg) || "-h".equals(arg)) {
				help();
				System.exit(0);
			} else if ("--prettyprint".equals(arg) || "-p".equals(arg)) {
				//config.put("prettyprint", "true");
			} else if ("--input".equals(arg) || "-i".equals(arg)) {
				i = i + 1;
				source = new File(args[i]);
			} else if ("--output".equals(arg) || "-i".equals(arg)) {
				i = i + 1;
				target = new File(args[i]);
			} else {
				System.err.println("Unkown argument: ["+args[i]+"]");
				help();
				System.exit(0);
			}
		}

		try {
			if (source == null) {
				exitWithError("no source file is provided.");
			} else {
				if(debug) {
					System.out.println("Preparing to convert "+source);
				}
				convert(source, target);
			}

		} catch (Exception e) {
			e.printStackTrace();
			exitWithError(e.getMessage());
		}
	}

	private void convert(File source, File target) throws MetadataConverterException, IOException {
		if (source == null || source.isHidden()) {
			return;
		}

		if (source.isDirectory()) {
			File[] listOfFiles = source.listFiles();
			for (File f : listOfFiles) {
				convert(f, target);
			}
		} else {
			if (source.length() == 0) {
				return;
			}
			MetadataFileFormatConverter<File> converter = null;
			String extension = null;
			if (source.getName().endsWith("xml")) {
				converter = XmlToSeedFileConverter.getInstance();
				extension = "dataless";
			} else {
				converter = SeedToXmlFileConverter.getInstance();
				extension = "xml";
			}
			if (target == null) {
				target = new File(source.getPath() + ".converted." + extension);
			} else {
				if (target.isDirectory()) {
					target = new File(target.getPath() + "/" + source.getName() + ".converted." + extension);
				}
			}
			try {
				if (debug) {
					System.out.println(source + "   ->   " + target);
				}

				converter.convert(source, target);
			} catch (FileConverterException e) {
				e.printStackTrace();
			}
		}
	}

	private static void exitWithError(String errorMsg) {

		System.err.println("\nError: " + errorMsg + "\n\n");
		help();

		System.exit(1);
	}

	private static void help() {
		System.out.println("Usage:");
		System.out.println("java -jar java -jar stationxml-converter.jar [options...] arguments...");

		System.out.println("	  [inputFile ...], usage = \"input file\"");
		System.out.println("	-h, aliases = \"--help\", usage = \"print this message\"");
		System.out.println("	-V, aliases = \"--version\", usage = \"Print version number and exit.\"");
		System.out.println("	-s, aliases = \"--seed\", usage = \"Seed format");
		System.out.println(
				"	-v, aliases = \"--verbose\", usage = \"Print out verbose information during conversation.\"");
		System.out.println("	-x, aliases = \"--xml\", usage = \"XML format\"");
		System.out.println("	-p, aliases = \"--prettyprint\", usage = \"Only when output is xml.\"");
		System.out.println(
				"	-se, aliases = \"--sender\", metaVar = \"<path>\", usage = \"Originating organization, or FDSNStationXML <Sender> element.\"");
		System.out.println(
				"	-so, aliases = \"--source\", metaVar = \"<path>\", usage = \"Originating organization, or FDSNStationXML <Source> element.\"");
		System.out.println(
				"	-A, aliases = \"--useragent\", usage = \"Originating organization, or FDSNStationXML <Source> element.\"");
		System.out.println("	-i, aliases = \"--input\", usage = \"Input as a file or URL - for System.in.\"");
		System.out.println("	-o, aliases = \"--output\" }, metaVar = \"<file>\", usage = \"use given output file\"");
		System.exit(1);
	}

	class Args {
		boolean debug;

		boolean isDebug() {
			return debug;
		}
	}
}
