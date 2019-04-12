---
title: Home
layout: default
---


# User's Guide to the IRIS stationxml-seed-converter

## Description

The stationxml-seed-converter is a tool used to convert seismological metadata between dataless seed and FDSN StationXML file formats. The converter is a command line utility written in Java and should be portable to an environment utilizing Java 8 or Java 11. It is designed to convert metadata with minimum losses, but inherent difference between dataless SEED and stationXML schema lead to tranformation inconsistancies. Documentation outlining differences between a dataless SEED and FDSN-StationXML metadata can be found at (Variations between Dataless Seed and FDSN-StationXML.)[http://www.fdsn.org/xml/station/Variations-FDSNSXML-SEED.txt]   

## Usage

The stationxml-seed-converter only accepts the arguments `--input` and `--output`. Both dataless or stationXML files can be supplied as the `--input` argument. The converter automatically detects if input metadata are formatted as either a stationXML or dataless SEED based on the file's extention. If the extention is `.xml` or `.XML` than the convert processes the input as a `.xml` document, otherwise the file is assumed to be formatted as dataless SEED. The `--output` argument should be the full path leading to the desired output file location and name. 

  `java -jar PATH/TO/stationxml-seed-converter-2.1.0.jar --input /PATH/TO/StnXML_file.xml --output /PATH/TO/StnXML_file.dataless`

  `java -jar /PATH/TO/stationxml-seed-converter-2.1.0.jar --input /PATH/TO/Dataless_file.dataless --output /PATH/TO/Dataless_file.xml`

  `java -jar /PATH/TO/stationxml-seed-converter-2.1.0.jar --input /PATH/TO/StnXML_directory --output /PATH/TO/XD_Directory/StnXML_file.converted.dataless`

  `java -jar /PATH/TO/stationxml-seed-converter-2.1.0.jar --input /PATH/TO/Dataless_directory --output /PATH/TO/DX_Directory/dataless.converted.xml`
  

### Errror

The converter throws exception errors if the byte length of stationXML values exceeds the cooresp dataless SEED file format outlined or if dataless files are mis-formatted or corrupt. Refer to the [SEED manual](https://www.fdsn.org/seed_manual/SEEDManual_V2.4.pdf) for further help and documetation. 




