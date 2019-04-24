---
title: Home
layout: default
---


# User's Guide to the IRIS stationxml-seed-converter

## Description

The stationxml-seed-converter is a tool used to convert seismological metadata between dataless seed and FDSN StationXML file formats. The converter is a command line utility written in Java and should be portable to an environment utilizing Java 8 or Java 11. It is designed to convert metadata with minimal losses, but inherent difference between dataless SEED and stationXML schema lead to tranformation inconsistancies. Documentation outlining differences between dataless SEED and FDSN-StationXML metadata can be found at [Variations between Dataless Seed and FDSN-StationXML](http://www.fdsn.org/xml/station/Variations-FDSNSXML-SEED.txt)   

## Usage

The stationxml-seed-converter accepts the arguments `--input`, `--output` and `--large`. Either a dataless or stationXML file can be supplied as an `--input` argument. The converter automatically detects the format of input metadata based on the file's extention. If the extention is `.xml` or `.XML` than the converter processes the input as a `.xml` document, otherwise the file is assumed to be formatted as dataless SEED. The `--output` argument should be the full path leading to a desired output file location and name. 

  `java -jar PATH/TO/stationxml-seed-converter-2.1.0.jar --input /PATH/TO/StnXML_file.xml --output /PATH/TO/StnXML_file.dataless`

  `java -jar /PATH/TO/stationxml-seed-converter-2.1.0.jar --input /PATH/TO/Dataless_file.dataless --output /PATH/TO/Dataless_file.xml`
 
The flag `--large` allows stationXML files > 500 MB to be changed to dataless SEED. This flag is only pertinent when converting from stationXML to dataless seed. 

 `java -jar PATH/TO/stationxml-seed-converter-2.1.0.jar --large --input /PATH/TO/StnXML_file.xml --output /PATH/TO/StnXML_file.dataless`


### Errror

The converter presents exception errors if the byte length of stationXML values exceeds the acceptable length of corresponding  dataless SEED values. These errors only occur when converting from stationXML to dataless SEED. Errors can also occur if metadata files are mis-formatted or corrupt. Refer to the [SEED manual](https://www.fdsn.org/seed_manual/SEEDManual_V2.4.pdf) for further help and documetation. 




