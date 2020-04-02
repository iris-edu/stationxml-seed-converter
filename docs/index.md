---
title: Home
layout: default
---


# User's Guide to the IRIS stationxml-seed-converter

## Documentation
* [Unit conversion](units.md)
* [Release Protocol](release.md)

## Description

The stationxml-seed-converter is a tool used to convert seismological metadata between dataless SEED and FDSN StationXML file formats. The converter is a command line utility written in Java and should be portable to an environment utilizing Java 8 or Java 11. It is designed to convert metadata with minimal losses, but inherent difference between dataless SEED and the StationXML schema lead to tranformation inconsistancies. Documentation outlining differences between dataless SEED and FDSN-StationXML metadata can be found at [Variations between Dataless Seed and FDSN-StationXML](http://www.fdsn.org/xml/station/Variations-FDSNSXML-SEED.txt)   

## Usage

The stationxml-seed-converter accepts both dataless and StationXML files. It automatically detects StationXML files based the metadata's contents. If the converter does not detect a file formatted as StationXML it assumes that the input file is dataless SEED. The converter's arguments include: `--input`, `--output`, `--verbose`, `--label`, `--organization`, and `--continue-on-error`. Input directories and files can be passed to the stationxml-seed-converter by supplying the converter with the file's path and no additional arguments. `--input ` may be prepended to directory/file's name as another option for supplying input metadata. The `--output` argument should be the full path leading to a desired output directory or file. If the `--output` argument is not supplied the convertered metadata are output to the same directory as the input metadata. `--output` can be pointed at a directory. During instances where no file name is supplied to `--output`, the output file's name becomes the name of the original file with converted.dataless or converted.xml appended. The `--verbose` argument changed the logging level to INFO. The arguments `--organization` and `--label`  change the contents of B10:F8 and B10:F9 when converting from StationXML to dataless SEED.
`--continue-on-error` is used to skip corrupt or misformatted files when processing entire directories.

Exmaples:

  `java -jar PATH/TO/stationxml-seed-converter-2.1.0.jar --input /PATH/TO/StnXML_file.xml --output /PATH/TO/StnXML_file.dataless`

  `java -jar /PATH/TO/stationxml-seed-converter-2.1.0.jar --input /PATH/TO/Dataless_file.dataless --output /PATH/TO/Dataless_file.xml`
 
  `java -jar /PATH/TO/stationxml-seed-converter-2.1.0.jar /PATH/TO/METADATA --output /PATH/TO/NEW/METADATA`

### Errror

The converter presents exception errors if the byte length of stationXML values exceeds the acceptable length of corresponding  dataless SEED values. These errors only occur when converting from stationXML to dataless SEED. Errors can also occur if metadata files are mis-formatted or corrupt. Refer to the [SEED manual](https://www.fdsn.org/seed_manual/SEEDManual_V2.4.pdf) for further help and documetation. 

### Language 

The stationxml-seed-converted is built in an English language enviroment and assumes users have similar settings. If a user's enviroment is not C locale please instantiate a C locale enviroment using `export LC_ALL="C"` for bash, or setenv `LC_ALL C` for tcsh/csh.

