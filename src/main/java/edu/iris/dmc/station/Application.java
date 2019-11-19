package edu.iris.dmc.station;

/*
 Station xml/seed converter
Copyright (C) 2019  IRIS

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.iris.dmc.station.converter.MetadataFileFormatConverter;
import edu.iris.dmc.station.converter.SeedToXmlFileConverter;
import edu.iris.dmc.station.converter.XmlToSeedFileConverter;
import edu.iris.dmc.station.mapper.MetadataConverterException;

public class Application {

	private static final Logger logger = Logger.getLogger(Application.class.getName());

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

		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if ("--verbose".equals(arg) || "-v".equals(arg)) {
				debug = true;
				/*
				 * Logger rootLogger = LogManager.getLogManager().getLogger("");
				 * rootLogger.setLevel(Level.INFO); for (Handler h : rootLogger.getHandlers()) {
				 * h.setLevel(Level.INFO); } logger.log(Level.FINEST, "SEED >< XML CONVERTER");
				 */
			} else if ("--help".equals(arg) || "-h".equals(arg)) {
				help();
				System.exit(0);
			} else if ("--prettyprint".equals(arg) || "-p".equals(arg)) {
			} else if ("--input".equals(arg) || "-i".equals(arg)) {
				i = i + 1;
				source = new File(args[i]);
			} else if ("--output".equals(arg) || "-o".equals(arg)) {
				i = i + 1;
				target = new File(args[i]);
			} else if ("--large".equals(arg)) {
				map.put("large", "true");
			} else if ("--align-epochs".equals(arg)) {
				map.put("align", "true");
			}else {
				logger.log(Level.SEVERE, "Unkown argument: [" + args[i] + "]");
				System.err.println("Unkown argument: [" + args[i] + "]");
				help();
				System.exit(1);
			}
		}

		try {
			if (source == null) {
				exitWithError("no source file is provided.");
			} else {
				if (debug) {
					System.out.println("Preparing to convert " + source);
				}
				convert(source, target, map);
			}

		} catch (Exception e) {
			exitWithError(e);
		}
	}

	private void convert(File source, File target, Map<String, String> map)
			throws MetadataConverterException, IOException, UnkownFileTypeException {
		if (source == null || !source.isFile()||source.isHidden()) {
			throw new IOException("Couldn't process file "+source);
		}

		if (source.isDirectory()) {
			File[] listOfFiles = source.listFiles();
			for (File f : listOfFiles) {
				convert(f, target, map);
			}
		} else {
			if (source.length() == 0) {
				throw new IOException("Couldn't process empty file "+source);
			}
			MetadataFileFormatConverter<File> converter = null;
			String extension = null;
			if (source.getName().toLowerCase().endsWith("xml")) {
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
					logger.log(Level.FINEST, source + "   ->   " + target);
				}

				converter.convert(source, target, map);
			} catch (FileConverterException e) {
				exitWithError(e);
			}
		}
	}

	private static void exitWithError(Exception e) {
		exitWithError(e.getMessage());
	}

	private static void exitWithError(String errorMsg) {System.out.println(errorMsg);
		logger.log(Level.SEVERE, "\nError: " + errorMsg + "\n\n");
		System.err.println("\nError: " + errorMsg + "\n\n");
		help();

		System.exit(1);
	}

	private static void help() {
		System.out.println("Usage:");
		System.out.println("java -jar stationxml-converter.jar [arguments]");

		System.out.println("	-h, aliases = \"--help\", usage = \"print this message\"");
		// System.out.println(" -V, aliases = \"--version\", usage = \"Print
		// version
		// number and exit.\"");
		// System.out.println(" -p, aliases = \"--prettyprint\", usage = \"Only
		// when
		// output is xml.\"");
		System.out.println("	--input, usage = \"Input as a file or URL\"");
		System.out.println("	--output, usage = \"Output file path and name\"");
		System.exit(1);
	}

	class Args {
		boolean debug;

		boolean isDebug() {
			return debug;
		}
	}
}
