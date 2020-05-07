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
import java.util.Properties;
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

import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.station.Application;
import edu.iris.dmc.station.converter.MetadataFileFormatConverter;
import edu.iris.dmc.station.converter.SeedToXmlFileConverter;
import edu.iris.dmc.station.converter.XmlToSeedFileConverter;
import edu.iris.dmc.station.converter.XmlToXmlFileConverter;
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
	private boolean schemaUpdate=false;

	public static void main(String[] args) throws Exception {
		Application application = new Application();
		application.run(args);
	}
	public void run(String... args) {
		if (args == null || args.length == 0) {
			exitWithError("Invalid number of arguments");
			help();	
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
			} else if ("--continue-on-error".equals(arg)) {
				map.put("continue", "true");
			
			} else if ("--large".equals(arg)) {
				map.put("large", "true");
			} else if ("--schema-update".equals(arg)) {
				map.put("schemaupdate", args[i]);
				schemaUpdate=true;
			} else if ("--align-epochs".equals(arg)) {
				map.put("align", "true");
			} else {
				if (arg.contains("--")){
				    logger.severe("Argument name is mistyped.");
				    help();
				}else {
				    source = new File(args[i]);
			    }
			}
		}

		try {
			if (source == null) {
				exitWithError("No source file is provided.");
			} else {
				convert(source, target, map);

			}

		} catch (Exception e) {
			StringBuilder message = createExceptionMessage(e);

			logger.severe(message.toString());
		}
	}
		

	private void convert(File source, File target, Map<String, String> map)
			throws MetadataConverterException, IOException, UnkownFileTypeException {

		if (source.isDirectory()) {
			File[] listOfFiles = source.listFiles();
			for (File f : listOfFiles) {
				convert(f, target, map);
			}
		} else {
			if (source == null || !source.isFile() || source.isHidden()) {
				exitWithError(new IOException("File " + source + " does not exist."), map);
			}
			MetadataFileFormatConverter<File> converter = null;
			String extension = null;

			if (isStationXml(source)) {
				logger.info("Input file: " + source.getPath());
				logger.info("Input file is formatted as StationXML");
				converter = XmlToSeedFileConverter.getInstance();
				extension = "dataless";
				if(lab==true){
					logger.info("Label [B10:F9] is set as " + map.get("label"));
				}
				if(org==true) {
					logger.info("Originating Organization [B10:F8] is set as " + map.get("organization"));
				}
				if(schemaUpdate==true) {
					logger.info("Input file: " + source.getPath());
					logger.info("Input file is formatted as StationXML");
					logger.info("Output file is formatted as StationXML 1.1");
					converter = XmlToXmlFileConverter.getInstance();
					extension = "xml";
					
				}
			} else {
				logger.info("Input file: " + source.getPath());
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
				//logger.log(Level.INFO,"Input File " + source + " Output File " + target);								
				converter.convert(source, target, map);
				logger.info("Output file: " + target + "\n");


			} catch (Exception e) {
				exitWithError(e, map);
			}
		  }
		}
	

	private static void exitWithError(Exception e, Map<String, String> map) {

		StringBuilder message = createExceptionMessage(e);
		String con = map.get("continue");	
		if (con.contains("true")==true){
			logger.severe(message.toString());
	        //null 
		}else {
			logger.severe(message.toString());
		    System.exit(1);
	    }
	}
	
	private static StringBuilder createExceptionMessage(Exception e) {
		StringBuilder message = new StringBuilder(
				"");
		if(e.getLocalizedMessage() != null) {
		    message.append(e.getLocalizedMessage());
		}
		for (StackTraceElement element : e.getStackTrace()){
			message.append(element.toString()).append("\n");
		}
		if (e.getCause() != null) {
			message.append(e.getCause().getLocalizedMessage());
			for (StackTraceElement element : e.getCause().getStackTrace()) {
				message.append(element.toString()).append("\n");
			}
		}
		return message;
	}

	private static void exitWithError(String errorMsg) {
		
		logger.severe(errorMsg);
	}

	private void help() {
		String version = "Version " + getClass().getPackage().getImplementationVersion();
		version = center(version, 62, " ");
		
		System.out.println("===============================================================");
		System.out.println("|"+ center("FDSN StationXML SEED Converter", 62, " ") + "|");
		System.out.println("|" + version + "|");
		System.out.println("================================================================");
		System.out.println("Usage:");
		System.out.println("java -jar stationxml-seed-converter <FILE> [OPTIONS]");
		System.out.println("OPTIONS:");
		System.out.println("   --help or -h         : print this message");
		// System.out.println(" -V, aliases = \"--version\", usage = \"Print
		// version
		// number and exit.\"");
		// System.out.println(" -p, aliases = \"--prettyprint\", usage = \"Only
		// when
		// output is xml.\"");
		System.out.println("   --verbose            : change the verobsity level to info; info is printed to stderr");
		System.out.println("   --input              : input as a file or directory");
		System.out.println("   --output             : output file path and name");
		System.out.println("   --label              : specify label for use in B10");
		System.out.println("   --organization       : specify organization for use in B10");
		System.out.println("   --schema-update      : updates input stationxml from version 1.0 to version 1.1; extensions are removed");
		System.out.println("   --continue-on-error  : prints exceptions to stdout and processes next file");
		System.out.println("===============================================================");
		System.exit(0);
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
	
	private static String center(String text, int length, String pad) {
		int width = length - text.length();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < width / 2; i++) {
			builder.append(pad);
		}
		builder.append(text);
		int remainder = length - builder.length();
		for (int i = 0; i < remainder; i++) {
			builder.append(pad);
		}
		return builder.toString();
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
