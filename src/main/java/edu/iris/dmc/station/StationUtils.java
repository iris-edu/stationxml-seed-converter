package edu.iris.dmc.station;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import edu.iris.dmc.station.util.StationIterator;

public class StationUtils {

	public static StationIterator stationIterator(final InputStream input) throws IOException {
		try {
			return new StationIterator(input);
		} catch (XMLStreamException | JAXBException | ParseException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
	}
}
