package edu.iris.dmc.station.converter;

import java.io.File;
import java.io.IOException;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.station.mapper.MetadataConverterException;

public class ApplicationTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			XmlToSeedFileConverter.getInstance().convert(new File("/Users/Suleiman/im.xml"), new File("/Users/Suleiman/test.xml.seed"));
		} catch (MetadataConverterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			SeedToXmlFileConverter.getInstance().convert( new File("/Users/Suleiman/test.xml.seed"),new File("/Users/Suleiman/imconverted.xml"));
		} catch (MetadataConverterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
