package edu.iris.dmc.station.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.writer.SeedFileWriter;
import edu.iris.dmc.station.MetadataConverterException;

public class XmlToSeedFileConverter implements MetadataFileFormatConverter<File> {

	private static XmlToSeedFileConverter INSTANCE = new XmlToSeedFileConverter();

	public static MetadataFileFormatConverter<File> getInstance() {
		return INSTANCE;
	}

	@Override
	public void convert(File source, File target) throws MetadataConverterException, IOException {
		this.convert(source, target, null);

	}

	@Override
	public void convert(File source, File target, Map<String, String> args)
			throws MetadataConverterException, IOException {

		FDSNStationXML document;
		try {
			document = load(source);
			Volume volume = XmlToSeedDocumentConverter.getInstance().convert(document);
			volume.build();
			int logicalrecordLength = (int) Math.pow(2, volume.getB010().getNthPower());
			SeedFileWriter writer = new SeedFileWriter(target, logicalrecordLength);
			writer.write(volume);
		} catch (JAXBException e) {
			throw new IOException(e);
		} catch (SeedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public FDSNStationXML load(File file) throws IOException, JAXBException {
		try (final FileInputStream inputStream = new FileInputStream(file)) {
			return load(inputStream);
		}
	}

	public FDSNStationXML load(InputStream inputStream) throws IOException, JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(edu.iris.dmc.fdsn.station.model.ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		return (FDSNStationXML) jaxbUnmarshaller.unmarshal(inputStream);
	}

	public static void main(String[] args) {
		File source = new File(
				XmlToSeedFileConverter.class.getClassLoader().getResource("IU_ANMO_BHZ.xml").getFile());

		try {
			XmlToSeedFileConverter.getInstance().convert(source, new File("/Users/Suleiman/xx.seed"));
		} catch (MetadataConverterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
