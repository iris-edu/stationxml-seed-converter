# stationxml-seed-converter
Please refer to the stationxml-seed-converter's [user guide](https://iris-edu.github.io/stationxml-seed-converter/) for complete documentation. 

### Downloading releases

Releases of the stationxml-seed-converter can be downloaded from the project's release page:

[https://github.com/iris-edu/stationxml-seed-converter/releases](https://github.com/iris-edu/stationxml-seed-converter/releases)

The compiled .jar may be used immediately and is compatible with Java 8 and Java 11.

### Basic Usage

  `java -jar /PATH/TO/stationxml-seed-converter-2.1.0.jar /PATH/TO/METADATA/File.extention`

  `java -jar PATH/TO/stationxml-seed-converter-2.1.0.jar --input /PATH/TO/StnXML_file.xml --output /PATH/TO/StnXML_file.dataless`

  `java -jar /PATH/TO/stationxml-seed-converter-2.1.0.jar --input /PATH/TO/Dataless_file.dataless --output /PATH/TO/Dataless_file.xml`

  `java -jar /PATH/TO/stationxml-seed-converter-2.1.0.jar --input /PATH/TO/METADATA/DIR --output /PATH/TO/METADATA/NEWDIR`


### System Language Requirments 

The stationxml-seed-converted is built in an English language environment and assumes users have similar settings. If a user's     environment is not C locale please instantiate a C locale environment using `export LC_ALL="C"` for bash, or setenv `LC_ALL C` for tcsh/csh.

