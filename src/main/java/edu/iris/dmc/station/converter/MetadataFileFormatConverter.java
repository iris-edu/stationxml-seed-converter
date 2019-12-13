package edu.iris.dmc.station.converter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import edu.iris.dmc.station.mapper.MetadataConverterException;

public interface MetadataFileFormatConverter<File> {

	public void convert(File source, File target) throws MetadataConverterException, IOException;

	public void convert(File source, File target, Map<String, String> args)
			throws MetadataConverterException, IOException;
	
	public void convert(InputStream source, OutputStream target, Map<String, String> args)
			throws MetadataConverterException, IOException;

}
