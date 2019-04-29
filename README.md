# stationxml-seed-converter
Complete documentation for the stationxml-seed-converter can be found in its [user guide](https://iris-edu.github.io/stationxml-seed-converter/). 

### Downloading releases

Releases of the stationxml-seed-converter can be downloaded from the project release page:

[https://github.com/iris-edu/stationxml-seed-converter/releases](https://github.com/iris-edu/stationxml-seed-converter/releases)

The compiled .jar may be used immediately and is compatible with Java 8 and Java 11.

### Basic Usage:

`java -jar stationxml-seed-converter-2.0.4-SNAPSHOT.jar --input /PATH/TO/Dataless_file.dataless --output /PATH/TO/Dataless_file.xml`

`java -jar /PATH/TO/stationxml-seed-converter-2.0.4-SNAPSHOT.jar --input /PATH/TO/XML_file.xml --output /PATH/TO/XML_file.dataless`

`java -jar /PATH/TO/stationxml-seed-converter-2.0.4-SNAPSHOT.jar --input /PATH/TO/XML_directory --output /PATH/TO/XD_Directory/xml.converted.dataless`

`java -jar /PATH/TO/stationxml-seed-converter-2.0.4-SNAPSHOT.jar --input /PATH/TO/Dataless_directory --output /PATH/TO/DX_Directory/dataless.converted.xml`
