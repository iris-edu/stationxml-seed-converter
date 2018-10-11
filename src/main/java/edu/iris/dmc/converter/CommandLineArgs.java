package edu.iris.dmc.converter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.spi.OptionHandler;

public class CommandLineArgs {

	private static final String USAGE_PREFIX = "" + "Usage:\n"
			+ "java -jar java -jar stationxml-converter.jar [options...] arguments...  \\\n";
	

	@Argument(metaVar = "[inputFile ...]", usage = "input file")
	private List<String> inputFiles = new ArrayList<String>();

	@Option(name = "-h", aliases = "--help", usage = "print this message")
	private boolean help = false;

	@Option(name="-V", aliases = "--version", usage = "Print version number and exit.")
	private boolean version = false;

	@Option(name = "-s", aliases = "--seed", usage = "Seed format")
	private boolean seed = false;

	@Option(name = "-v", aliases = "--verbose", usage = "Print out verbose information during conversation.")
	private boolean verbose = false;

	@Option(name = "-x", aliases = "--xml", usage = "XML format")
	private boolean xml = false;

	@Option(name = "-p", aliases = "--prettyprint", usage = "Only when output is xml.")
	private boolean prettyPrint = false;

	@Option(name = "-se", aliases = "--sender", metaVar = "<path>", usage = "Originating organization, or FDSNStationXML <Sender> element.")
	private String sender;

	@Option(name = "-so", aliases = "--source", metaVar = "<path>", usage = "Originating organization, or FDSNStationXML <Source> element.")
	private String source;

	@Option(name = "-A", aliases = "--useragent", usage = "Originating organization, or FDSNStationXML <Source> element.")
	private String userAgent;

	@Option(name = "-i", aliases = "--input", usage = "Input as a file or URL - for System.in.")
	private String inputFile;

	@Option(name = "-o", aliases = { "--output" }, metaVar = "<file>", usage = "use given output file")
	private File outputFile;

	
	
	
	public String getUsagePrefix() {
		return USAGE_PREFIX;
	}

	public List<String> getInputFiles() {
		return inputFiles;
	}

	public void setInputFiles(List<String> inputFiles) {
		this.inputFiles = inputFiles;
	}

	public boolean isHelp() {
		return help;
	}

	public void setHelp(boolean help) {
		this.help = help;
	}

	public boolean isVersion() {
		return version;
	}

	public void setVersion(boolean version) {
		this.version = version;
	}

	public boolean isSeed() {
		return seed;
	}

	public void setSeed(boolean seed) {
		this.seed = seed;
	}

	public boolean isXml() {
		return xml;
	}

	public void setXml(boolean xml) {
		this.xml = xml;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public boolean isPrettyPrint() {
		return prettyPrint;
	}

	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}

	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	/*
	 * addOption("V", "version", false, "Print version number and exit.");
	 * addOption("A", "useragent", true, "UserAgent");
	 * addOption("v", "verbose", false,
	 * "Print out verbose information during conversation.");
	 * addOption("h", "help", false, "Print this usage information.");
	 * // addOption("f", "file", true,
	 * // "A file listing input files or URLs, one per line.");
	 * addOption("s", "seed", false, "Output format.");
	 * addOption("p", "print", true, "Pretty printing xml.");
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @Option(name = "-f", aliases = { "-file", "-buildfile" }, metaVar =
	 * "<file>",
	 * usage = "use given buildfile")
	 * private File buildFile;
	 * 
	 * @Option(name = "-nice", metaVar = "number",
	 * usage = "A niceness value for the main thread:\n"
	 * + "1 (lowest) to 10 (highest); 5 is the default")
	 * private int nice = 5;
	 * 
	 * private Map<String, String> properties = new HashMap<String, String>();
	 * 
	 * @Option(name = "-D", metaVar = "<property>=<value>",
	 * usage = "use value for given property")
	 * private void setProperty(final String property) throws CmdLineException {
	 * String[] arr = property.split("=");
	 * if(arr.length != 2) {
	 * throw new CmdLineException("Properties must be specified in the form:"+
	 * "<property>=<value>");
	 * }
	 * properties.put(arr[0], arr[1]);
	 * 
	 * private
	 */
	public CommandLineArgs() {
		/*
		 * addOption("V", "version", false, "Print version number and exit.");
		 * addOption("A", "useragent", true, "UserAgent");
		 * addOption("v", "verbose", false,
		 * "Print out verbose information during conversation.");
		 * addOption("h", "help", false, "Print this usage information.");
		 * // addOption("f", "file", true,
		 * // "A file listing input files or URLs, one per line.");
		 * addOption("s", "seed", false, "Output format.");
		 * addOption("p", "print", true, "Pretty printing xml.");
		 * 
		 * addOption("i", "input", true,
		 * "Input as a file or URL - for System.in.");
		 * 
		 * addOption("o", "output", true,
		 * "Save result to specified file, otherwise print to console.");
		 * addOption("se", "sender", true,
		 * "Originating organization, or FDSNStationXML <Sender> element.");
		 * addOption("so", "source", true,
		 * "Originating organization, or FDSNStationXML <Source> element.");
		 * addOption(OptionBuilder.withLongOpt("File Input").withType(
		 * FileInputStream.class)
		 * .withDescription("Specify an EXISTING file path").create());
		 */
	}

	@Override
	public String toString() {
		return "CommandLineArgs [inputFiles=" + inputFiles + ", help=" + help
				+ ", version=" + version + ", seed=" + seed + ", verbose="
				+ verbose + ", xml=" + xml + ", prettyPrint=" + prettyPrint
				+ ", sender=" + sender + ", source=" + source + ", userAgent="
				+ userAgent + ", inputFile=" + inputFile + ", outputFile="
				+ outputFile + "]";
	}

	
	public static void main(String[] args){
		CommandLineArgs commandLineArgs = new CommandLineArgs();
		CmdLineParser parser = new CmdLineParser(commandLineArgs);
		try {
			args= new String[]{"-v","--help","--version"};
			parser.parseArgument(args);
			System.out.println(commandLineArgs);

		} catch (CmdLineException e) {
			// if there's a problem in the command line,
			// you'll get this exception. this will report
			// an error message.
			System.err.println(e.getMessage());
			System.err.println("java -jar stationxml-converter.jar [options...] arguments...");
			// print the list of available options
			parser.printUsage(System.err);
			System.err.println();

			OptionHandlerFilter filter = new OptionHandlerFilter() {

				@Override
				public boolean select(OptionHandler o) {
					// TODO Auto-generated method stub
					return false;
				}

			};
			// print option sample. This is useful some time
			System.err.println("  Example: java -jar stationxml-converter.jar [options...] arguments..."
					+ parser.printExample(filter));

			return;
		}
	}
}
