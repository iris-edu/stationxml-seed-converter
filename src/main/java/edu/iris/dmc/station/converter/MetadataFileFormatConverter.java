package edu.iris.dmc.station.converter;

import java.io.IOException;
import java.util.Map;

import edu.iris.dmc.station.MetadataConverterException;

public interface MetadataFileFormatConverter<File> {

	public void convert(File source, File target) throws MetadataConverterException, IOException;

	public void convert(File source, File target, Map<String, String> args)
			throws MetadataConverterException, IOException;

}
