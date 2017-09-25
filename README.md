# stationxml-seed-converter
Station XML/Seed converter


Usage:
java -jar java -jar stationxml-converter.jar [options...] arguments...

	-h, --help, 		print this message
	-V, --version, 		Print version number and exit.
	-s, --seed, 		Seed format
	-v, --verbose, 		Print out verbose information during conversation.
	-x, --xml,          XML format
	-p, --prettyprint, 	Only when output is xml.
	-se,--sender, 		metaVar = <path>, Originating organization, or FDSNStationXML <Sender> element.
	-so,--source, 		metaVar = <path>, Originating organization, or FDSNStationXML <Source> element.
	-A, --useragent, 	Originating organization, or FDSNStationXML <Source> element.
	-i, --input, 		Input as a file or URL - for System.in.
	-o, --output, 		<file>, use given output file
