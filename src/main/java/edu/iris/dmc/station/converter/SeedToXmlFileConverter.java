package edu.iris.dmc.station.converter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.station.FileConverterException;
import edu.iris.dmc.station.MetadataConverterException;
import edu.iris.dmc.station.util.SeedUtils;

public class SeedToXmlFileConverter implements MetadataFileFormatConverter<File> {

	private static SeedToXmlFileConverter INSTANCE = new SeedToXmlFileConverter();

	private SeedToXmlFileConverter() {

	}

	public static MetadataFileFormatConverter<File> getInstance() {
		return INSTANCE;
	}

	@Override
	public void convert(File source, File target) throws FileConverterException, IOException {
		this.convert(source, target, null);

	}

	@Override
	public void convert(File source, File target, Map<String, String> args)
			throws MetadataConverterException, IOException {
		try (OutputStream stream = new FileOutputStream(target)) {
			Volume volume = SeedUtils.load(source);
			FDSNStationXML document = SeedToXmlDocumentConverter.getInstance().convert(volume);
			boolean prettyprint = false;
			if (args != null && args.get("prettyprint") != null) {
				prettyprint = Boolean.valueOf(args.get("prettyprint"));
			}
			marshal(document, stream, prettyprint);
		} catch (Exception e) {
			throw new FileConverterException(e, source.getPath());
		}
	}

	public void marshal(FDSNStationXML document, File file) throws IOException, JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(edu.iris.dmc.fdsn.station.model.ObjectFactory.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(document, file);
	}

	public void marshal(FDSNStationXML document, OutputStream stream, boolean prettyPrint)
			throws IOException, JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(edu.iris.dmc.fdsn.station.model.ObjectFactory.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		if (prettyPrint) {
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		}
		marshaller.marshal(document, stream);
	}

}
