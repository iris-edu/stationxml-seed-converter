---
title: Home
layout: default
---


# User's Guide to the IRIS stationxml-seed-converter

## Description

The stationxml-seed-converter is a tool used to convert seismological metadata between dataless seed and stationxml file formats. The converter is designed to transform metadata with minimum losses. Documentation describing differences between Dataless and Stationxml metadata can be found at [variations between Dataless Seed and FDSN-StationXML](http://www.fdsn.org/xml/station/Variations-FDSNSXML-SEED.txt). 

## Usage

`--input` and `--output` are the only arguments supplied to the stationxml-seed-converter. The `--input` argument accepts either dataless or stationxml files, also paths to directories containing solely dataless or stationxml files are valid. The `--output` and `--input` arguments must have matching path levels (file/directory). If a directory is provided, the converter will output converted files with the naming convention input_file_name.converted.dataless or input_file_name.converted.xml to the output path supplied by the user.

  `java -jar PATH/TO/stationxml-seed-converter-2.0.0.jar --input /PATH/TO/StnXML_file.xml --output /PATH/TO/StnXML_file.dataless`

  `java -jar /PATH/TO/stationxml-seed-converter-2.0.0.jar --input /PATH/TO/Dataless_file.dataless --output /PATH/TO/Dataless_file.xml`

  `java -jar /PATH/TO/stationxml-seed-converter-2.0.0.jar --input /PATH/TO/StnXML_directory --output /PATH/TO/XD_Directory/StnXML_file.converted.dataless`

  `java -jar /PATH/TO/stationxml-seed-converter-2.0.0.jar --input /PATH/TO/Dataless_directory --output /PATH/TO/DX_Directory/dataless.converted.xml`
  

### Errror

The converter throws exception errors if the byte length of stationXML values exceeds the dataless predefined format or if dataless files are mis-formatted or corrupt. Refer to the [SEED manual](https://www.fdsn.org/seed_manual/SEEDManual_V2.4.pdf) for further help and documetation. 




