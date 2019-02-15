package edu.iris.dmc.station.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.FloatNoUnitType;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.station.mapper.MetadataConverterException;
import edu.iris.dmc.station.mapper.SeedStringBuilder;

public class XmlUtils {

	public static FDSNStationXML load(File file) throws IOException, JAXBException {
		try (final FileInputStream inputStream = new FileInputStream(file)) {
			return load(inputStream);
		}
	}

	public static FDSNStationXML load(InputStream inputStream) throws IOException, JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(edu.iris.dmc.fdsn.station.model.ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		return (FDSNStationXML) jaxbUnmarshaller.unmarshal(inputStream);
	}

	public static Iterable<Station> iterate(InputStream inputStream) throws IOException, JAXBException {
		return new Iterable<Station>() {
			@Override
			public Iterator<Station> iterator() {
				try {
					return new StationIterator(inputStream);
				} catch (XMLStreamException | JAXBException | ParseException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	public static void marshal(FDSNStationXML document, File file) throws IOException, JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(edu.iris.dmc.fdsn.station.model.ObjectFactory.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(document, file);
	}

	public static void marshal(FDSNStationXML document, OutputStream stream, boolean prettyPrint)
			throws IOException, JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(edu.iris.dmc.fdsn.station.model.ObjectFactory.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		if (prettyPrint) {
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		}
		marshaller.marshal(document, stream);
	}

	public static FloatNoUnitType createFloatNoUnitType(edu.iris.dmc.seed.control.station.Number number) {
		if (number == null) {
			return null;
		}
		FloatNoUnitType fnt = new FloatNoUnitType();
		fnt.setValue(number.getValue());
		fnt.setMinusError(number.getError());
		fnt.setPlusError(number.getError());
		return fnt;
	}
}
