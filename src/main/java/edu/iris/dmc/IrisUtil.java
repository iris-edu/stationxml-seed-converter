package edu.iris.dmc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.seed.BTime;
import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.BlocketteFactory;
import edu.iris.dmc.seed.IncompleteBlockette;
import edu.iris.dmc.seed.Record;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.blockette.util.BItrator;
import edu.iris.dmc.seed.blockette.util.BlocketteItrator;
import edu.iris.dmc.seed.io.RecordInputStream;
import edu.iris.dmc.station.util.StationIterator;

public class IrisUtil {

	private IrisUtil() {
	}

	public static FDSNStationXML readXml(File file) throws JAXBException, IOException {
		try (final FileInputStream inputStream = new FileInputStream(file)) {
			return readXml(inputStream);
		}
	}

	public static FDSNStationXML readXml(InputStream inputStream) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(edu.iris.dmc.fdsn.station.model.ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		return (FDSNStationXML) jaxbUnmarshaller.unmarshal(inputStream);
	}

	public static void marshal(FDSNStationXML document, OutputStream stream) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(edu.iris.dmc.fdsn.station.model.ObjectFactory.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(document, stream);
	}

	public static Volume readSeed(File file) throws SeedException, IOException {
		try (final FileInputStream inputStream = new FileInputStream(file)) {
			return readSeed(inputStream);
		}
	}

	public static Volume readSeed(InputStream inputStream) throws SeedException, IOException {
		try (RecordInputStream seedRecordInputStream = new RecordInputStream(inputStream)) {
			Volume volume = null;
			Record r = null;
			while ((r = seedRecordInputStream.next()) != null) {
				if (r.isContinuation()) {
					throw new SeedException("Did not expect continuation record!");
				}

				Iterator<Blockette> it = new BItrator(r);
				while (it.hasNext()) {
					if (volume == null) {
						volume = new Volume();
					}
					Blockette blockette = it.next();
					volume.add(blockette);
					if (blockette instanceof IncompleteBlockette) {
						IncompleteBlockette incompleteBlockette = (IncompleteBlockette) blockette;

						while (incompleteBlockette.numberOfRequiredBytesToComplete() > 0) {
							r = seedRecordInputStream.next();
							if (r == null) {
								throw new SeedException("Expected a new record but was null.");
							} else if (!r.isContinuation()) {
								throw new SeedException("Expected a continuation record but received a new record.");
							} else {
								byte[] bytes = r.get(incompleteBlockette.numberOfRequiredBytesToComplete());
								incompleteBlockette.append(bytes);
							}
						}
						blockette = BlocketteFactory.create(incompleteBlockette.getBytes());
						it = new BItrator(r);
					}
				}
			}
			return volume;
		}
	}

	/**
	 * 
	 * @param file
	 * @return a closable iterator, it is important that user close this iterator or
	 *         the underlying inputstream
	 * @throws IOException
	 */
	public static StationIterator newStationIterator(File file) throws IOException {

		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException(String.format("File '%s' exists but is a directory", file.getName()));
			}
			if (!file.canRead()) {
				throw new IOException(String.format("File '%s' cannot be read", file.getName()));
			}
		} else {
			throw new FileNotFoundException(String.format("File '%s' does not exist.", file.getName()));
		}
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			return new StationIterator(inputStream);

		} catch (final IOException | RuntimeException ex) {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (final IOException e) {
				ex.addSuppressed(e);
			}
			throw ex;
		}
	}

	public static ZonedDateTime now() {
		return ZonedDateTime.now(ZoneId.of("UTC"));
	}

	public static ZonedDateTime toZonedDateTime(String source) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[z]").withZone(ZoneId.of("UTC"));
		return ZonedDateTime.parse(source, format);
	}

	public static ZonedDateTime toZonedDateTime(BTime bTime) {
		if (bTime == null) {
			return null;
		}

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy,DDD,HH:mm:ss.nZ");
		String date = bTime.toSeedString() + "00000+0000";

		return ZonedDateTime.parse(date, dateTimeFormatter);
	}

	public static BTime toBTime(ZonedDateTime time) {
		if (time == null) {
			return null;
		}

		return new BTime(time.getYear(), time.getDayOfYear(), time.getHour(), time.getMinute(), time.getSecond(),
				time.get(ChronoField.MILLI_OF_SECOND));
	}
}
