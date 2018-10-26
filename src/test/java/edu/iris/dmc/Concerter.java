package edu.iris.dmc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import edu.iris.dmc.station.Application;
import edu.iris.dmc.station.FileConverterException;
import edu.iris.dmc.station.MetadataConverterException;
import edu.iris.dmc.station.converter.MetadataFileFormatConverter;
import edu.iris.dmc.station.converter.SeedToXmlFileConverter;
import edu.iris.dmc.station.converter.XmlToSeedFileConverter;

public class Concerter {

	public static void main(String[] args) throws Exception {
		args = new String[] { "--input", "/Users/Suleiman/loaded-dataless" };

		File source = new File("/Users/Suleiman/loaded-dataless/error");
		convert(source);

	}

	private static int total = 0;
	private static int cnt=1;

	private static void convert(File source) throws MetadataConverterException, IOException {

		if (source == null || source.isHidden()) {
			return;
		}

		if (source.isDirectory()) {
			if (source.getName().equals("converted") ) {
				return;
			}
			File[] listOfFiles = source.listFiles();
			total += listOfFiles.length;
			for (File f : listOfFiles) {
				convert(f);
			}
		} else {
			if (source.length() == 0) {
				return;
			}
			System.out.println("Converting: " + source+" >< "+cnt++ +" of "+total);
			MetadataFileFormatConverter<File> converter = null;
			String extension = null;
			if (source.getName().endsWith("xml")) {
				converter = XmlToSeedFileConverter.getInstance();
				extension = "dataless";
			} else {
				converter = SeedToXmlFileConverter.getInstance();
				extension = "xml";
			}
			File target = new File(
					source.getParentFile().getPath() + "/converted/" + source.getName() + "." + extension);

			try {
				converter.convert(source, target);
			} catch (FileConverterException e) {
				System.out.println(e.getFile());
				e.printStackTrace();
				File error = new File(source.getParentFile().getPath() + "/error/" + source.getName());
				Path temp = Files.move(Paths.get(source.getPath()), Paths.get(error.getPath()),
						StandardCopyOption.REPLACE_EXISTING);

			}
		}
	}

}
