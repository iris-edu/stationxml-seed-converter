package edu.iris.dmc.station.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
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
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

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

	public StationIterator(InputStream inputStream) throws XMLStreamException, JAXBException, ParseException {
		this.inputStream = inputStream;
		XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
		this.xmlEventReader = xmlFactory.createXMLEventReader(inputStream);

		JAXBContext jc = JAXBContext.newInstance(edu.iris.dmc.fdsn.station.model.ObjectFactory.class);
		this.unmarshaller = jc.createUnmarshaller();
		prepareNext();
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
								network.setStartDate(TimeUtil.toZonedDateTime(startString));
							}
						} else if (attributeName.equals("endDate")) {
							String endString = attribute.getValue();
							if (endString != null) {
								network.setEndDate(TimeUtil.toZonedDateTime(endString));
							}
						} else {
							// Do nothing for now
						}
					}
					xmlEventReader.nextEvent();
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

	private void prepareNext1() {
		// prepare the next station
		try {
			XMLEvent event = null;
			while ((event = xmlEventReader.peek()) != null) {
				System.out.println(event);
				if (event.isStartElement()) {
					StartElement startElement = (StartElement) event;
					if (startElement.getName().getLocalPart().equals("FDSNStationXML")) {
						root = new FDSNStationXML();
						// we only peeked, get the actual one
						startElement = (StartElement) xmlEventReader.nextEvent();
						// Maybe parse attributes...
						while ((event = xmlEventReader.peek()) != null) {
							if (event.isStartElement()) {
								startElement = (StartElement) event;
								if (startElement.getName().getLocalPart().equals("Source")) {
									event = xmlEventReader.nextEvent();
									event = xmlEventReader.peek();
									if (event != null && event.isCharacters()) {
										event = xmlEventReader.nextEvent();
										root.setSource(event.asCharacters().getData());
									}
								} else if (startElement.getName().getLocalPart().equals("Sender")) {
									event = xmlEventReader.nextEvent();
									event = xmlEventReader.peek();
									if (event != null && event.isCharacters()) {
										event = xmlEventReader.nextEvent();
										root.setSender(event.asCharacters().getData());
									}
								} else if (startElement.getName().getLocalPart().equals("Module")) {
									event = xmlEventReader.nextEvent();
									event = xmlEventReader.peek();
									if (event != null && event.isCharacters()) {
										event = xmlEventReader.nextEvent();
										root.setModule(event.asCharacters().getData());
									}
								} else if (startElement.getName().getLocalPart().equals("ModuleURI")) {
									event = xmlEventReader.nextEvent();
									event = xmlEventReader.peek();
									if (event != null && event.isCharacters()) {
										event = xmlEventReader.nextEvent();
										root.setModuleURI(event.asCharacters().getData());
									}

								} else if (startElement.getName().getLocalPart().equals("Created")) {
									event = xmlEventReader.nextEvent();
									event = xmlEventReader.peek();
									if (event != null && event.isCharacters()) {
										event = xmlEventReader.nextEvent();
										// root.setCreated(TimeUtil.toZonedDateTime(event.asCharacters().getData()));
									}
								} else {
									break;
								}
							} else {
								xmlEventReader.nextEvent();
							}
						} // END WHILE FOR ROOT
					} else if (startElement.getName().getLocalPart().equals("Network")) {
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
									network.setStartDate(TimeUtil.toZonedDateTime(startString));
								}
							} else if (attributeName.equals("endDate")) {
								String endString = attribute.getValue();
								if (endString != null) {
									network.setEndDate(TimeUtil.toZonedDateTime(endString));
								}
							} else {
								// Do nothing for now
							}
						}
						xmlEventReader.next();
					} else if (startElement.getName().getLocalPart().equals("Description")) {
						xmlEventReader.nextEvent();
						network.setDescription(xmlEventReader.getElementText());

					} else if (startElement.getName().getLocalPart().equals("TotalNumberStations")) {
						xmlEventReader.nextEvent();
						String tnos = xmlEventReader.getElementText();
						if (tnos != null) {
							network.setTotalNumberStations(new BigInteger(tnos));
						}

					} else if (startElement.getName().getLocalPart().equals("SelectedNumberStations")) {
						xmlEventReader.nextEvent();
						String tnos = xmlEventReader.getElementText();
						if (tnos != null) {
							network.setSelectedNumberStations(new BigInteger(tnos));
						}

					} else if (startElement.getName().getLocalPart().equals("Station")) {
						JAXBElement<Station> stationElement = unmarshaller.unmarshal(xmlEventReader,
								edu.iris.dmc.fdsn.station.model.Station.class);
						Station station = stationElement.getValue();
						station.setNetwork(network);
						queue.add(station);
						break;
					} else {
						xmlEventReader.next();
					}
				} else {
					xmlEventReader.next();
				}
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException {
		if (this.inputStream != null) {
			this.inputStream.close();
		}
	}
}
