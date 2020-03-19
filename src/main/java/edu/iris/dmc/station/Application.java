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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.iris.dmc.station.converter.MetadataFileFormatConverter;
import edu.iris.dmc.station.converter.SeedToXmlFileConverter;
import edu.iris.dmc.station.converter.XmlToSeedFileConverter;
import edu.iris.dmc.station.mapper.MetadataConverterException;

public class Application {

	private static Logger logger = null;
	  static {
	      System.setProperty("java.util.logging.SimpleFormatter.format",
	    		  "[%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS] [%4$-6s] %2$s:"
	    		  + " %5$s%6$s %n");
	      logger = Logger.getLogger(Application.class.getName());
	  }
	
	
	private boolean debug;
	private boolean lab=false;
	private boolean org=false;

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
		logger.setLevel(Level.WARNING);
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if ("--verbose".equals(arg) || "-v".equals(arg)) {
				debug = true;
				logger.setLevel(Level.INFO);
			} else if ("--help".equals(arg) || "-h".equals(arg)) {
				help();
				System.exit(0);
			} else if ("--input".equals(arg) || "-i".equals(arg)) {
				i = i + 1;
				source = new File(args[i]);
			} else if ("--label".equals(arg)) {
				i = i + 1;
				map.put("label", args[i]);
				lab = true;
			} else if ("--organization".equals(arg)) {
				i = i + 1;
				map.put("organization", args[i]);
				org=true;
			} else if ("--output".equals(arg) || "-o".equals(arg)) {
				i = i + 1;
				target = new File(args[i]);
			} else if ("--large".equals(arg)) {
				map.put("large", "true");
			} else if ("--align-epochs".equals(arg)) {
				map.put("align", "true");
			} else {
				source = new File(args[i]);
			}
		}

		try {
			if (source == null) {
				exitWithError("no source file is provided.");
			} else {
				convert(source, target, map);
			}

		} catch (Exception e) {
			exitWithError(e);
		}
	}

	private void convert(File source, File target, Map<String, String> map)
			throws MetadataConverterException, IOException, UnkownFileTypeException {
		if (source == null || !source.isFile() || source.isHidden()) {
			throw new IOException("File " + source + " does not exist.");
		}

		if (source.isDirectory()) {
			File[] listOfFiles = source.listFiles();
			for (File f : listOfFiles) {
				convert(f, target, map);
			}
		} else {
			if (source.length() == 0) {
				throw new IOException("Couldn't process empty file " + source);
			}
			MetadataFileFormatConverter<File> converter = null;
			String extension = null;

			if (isStationXml(source)) {
				logger.info("Input file is formatted as StationXml");
				converter = XmlToSeedFileConverter.getInstance();
				extension = "dataless";
				if(lab==true){
					logger.info("Label [B10:F9] is set as " + map.get("label"));
				}
				if(org==true) {
					logger.info("Originating Organization [B10:F8] is set as " + map.get("organization"));
				}	
			} else {
				logger.info("Input file is assumed to be formatted as Dataless SEED");
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
				logger.log(Level.INFO,"Input File " + source + " Output File " + target);
				converter.convert(source, target, map);
			} catch (FileConverterException e) {
				exitWithError(e);
			}
		  }
		}
	

	private static void exitWithError(Exception e) {
		exitWithError(e.getMessage());
	}

	private static void exitWithError(String errorMsg) {
		logger.severe(errorMsg);
		help();

		System.exit(1);
	}

	private static void help() {
		System.out.println("Usage:");
		System.out.println("java -jar stationxml-converter.jar [arguments]");

		System.out.println("	--help or -h, usage = \"print this message\"");
		// System.out.println(" -V, aliases = \"--version\", usage = \"Print
		// version
		// number and exit.\"");
		// System.out.println(" -p, aliases = \"--prettyprint\", usage = \"Only
		// when
		// output is xml.\"");
		System.out.println("        --verbose, usage = \"Increase verbosity level\"");
		System.out.println("	--input, usage = \"Input as a file or URL\"");
		System.out.println("	--output, usage = \"Output file path and name\"");
		System.out.println("	--label, usage = \"Change B10 default Label to input\"");
		System.out.println("	--organization, usage = \"Change B10 default Organzation to input\"");

		System.exit(1);
	}

	class Args {
		boolean debug;

		boolean isDebug() {
			return debug;
		}
	}

	private boolean isStationXml(File source) throws IOException {
		if (source == null) {
			throw new IOException("File not found");
		}
		ExtractorHandler handler = new ExtractorHandler();
		try (InputStream inputStream = new FileInputStream(source)) {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(true);
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(inputStream, handler);
		} catch (

		Exception e) {
			// do nothing
		}
		QName qname = handler.rootElement;
		if (qname == null) {
			return false;
		}

		if ((new QName("http://www.fdsn.org/xml/station/1", "FDSNStationXML")).equals(qname)) {
			return true;
		}
		return false;
	}

	protected static class ExtractorHandler extends DefaultHandler {

		private QName rootElement = null;

		@Override
		public void startElement(String uri, String local, String name, Attributes attributes) throws SAXException {
			this.rootElement = new QName(uri, local);
			throw new SAXException("Aborting: root element received");
		}

		QName getRootElement() {
			return rootElement;
		}
	}
}
