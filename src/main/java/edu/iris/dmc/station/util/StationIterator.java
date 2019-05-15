package edu.iris.dmc.station.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;

public class StationIterator implements Iterator<Station>, Closeable {

	private XMLEventReader xmlEventReader;
	private Unmarshaller unmarshaller;

	private Queue<Station> queue = new LinkedList<>();

	private FDSNStationXML root;
	private Network network;

	private InputStream inputStream;

	public StationIterator(InputStream inputStream) throws IOException {
		this.inputStream = inputStream;
		XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
		try {
			this.xmlEventReader = xmlFactory.createXMLEventReader(inputStream);

			JAXBContext jc = JAXBContext.newInstance(edu.iris.dmc.fdsn.station.model.ObjectFactory.class);
			this.unmarshaller = jc.createUnmarshaller();
			prepareNext();
		} catch (XMLStreamException | JAXBException | ParseException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean hasNext() {
		return !queue.isEmpty();
	}

	@Override
	public Station next() {
		Station station = queue.poll();
		if (station == null) {

		}
		try {
			prepareNext();
		} catch (XMLStreamException | JAXBException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return station;
	}

	private void prepareNext() throws XMLStreamException, JAXBException, ParseException {
		// prepare the next station

		while (xmlEventReader.hasNext()) {
			XMLEvent peekEvent = xmlEventReader.peek();
			switch (peekEvent.getEventType()) {
			case XMLStreamConstants.START_DOCUMENT:
			case XMLStreamConstants.COMMENT:
				xmlEventReader.nextEvent();
				break;
			case XMLStreamConstants.CHARACTERS:
				xmlEventReader.nextEvent();
				break;
			case XMLStreamConstants.END_ELEMENT:
				xmlEventReader.nextEvent();
				break;

			case XMLStreamConstants.START_ELEMENT:
				final StartElement startElement = (StartElement) peekEvent;
				if ("Network".equals(startElement.getName().getLocalPart())) {
					network = new Network();
					network.setRootDocument(root);
					Iterator<Attribute> attributes = startElement.getAttributes();
					while (attributes.hasNext()) {
						Attribute attribute = attributes.next();
						String attributeName = attribute.getName().getLocalPart();
						if (attributeName.equals("code")) {
							network.setCode(attribute.getValue());
						} else if (attributeName.equals("startDate")) {
							String startString = attribute.getValue();
							if (startString != null) {
								network.setStartDate(IrisUtil.toZonedDateTime(startString));
							}
						} else if (attributeName.equals("endDate")) {
							String endString = attribute.getValue();
							if (endString != null) {
								network.setEndDate(IrisUtil.toZonedDateTime(endString));
							}
						} else {
							// Do nothing for now
						}
					}
					xmlEventReader.next();
					while (xmlEventReader.hasNext()) {
						XMLEvent event = xmlEventReader.peek();
						if (XMLStreamConstants.START_ELEMENT == event.getEventType()) {
							StartElement e = (StartElement) event;

							if ("Description".equals(e.getName().getLocalPart())) {
								xmlEventReader.nextEvent();
								event = xmlEventReader.nextEvent();
								if (event.isCharacters()) {
									network.setDescription(((Characters) event).getData());
								}
								break;
							}

						} else {
							xmlEventReader.nextEvent();
						}
					}
				} else if ("Station".equals(startElement.getName().getLocalPart())) {
					JAXBElement<Station> stationElement = unmarshaller.unmarshal(xmlEventReader, Station.class);
					Station station = stationElement.getValue();
					station.setNetwork(network);
					queue.add(station);
					return;
				} else {
					xmlEventReader.nextEvent();
				}
				break;

			case XMLStreamConstants.END_DOCUMENT:
				return;

			default:
				xmlEventReader.nextEvent();
			}
		}

	}

	@Override
	public void close() throws IOException {
		if (this.inputStream != null) {
			this.inputStream.close();
		}
	}
}
