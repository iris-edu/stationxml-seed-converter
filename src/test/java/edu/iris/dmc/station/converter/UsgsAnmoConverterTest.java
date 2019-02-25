package edu.iris.dmc.station.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Response;
import edu.iris.dmc.fdsn.station.model.Sensitivity;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.seed.BTime;
import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.seed.control.station.B051;
import edu.iris.dmc.seed.control.station.B052;
import edu.iris.dmc.seed.control.station.B057;
import edu.iris.dmc.seed.control.station.ResponseBlockette;
import edu.iris.dmc.station.util.SeedUtils;
import edu.iris.dmc.station.util.XmlUtils;

public class UsgsAnmoConverterTest {

	@Test
	public void compare() {

		try {
			File source = new File(UsgsAnmoConverterTest.class.getClassLoader().getResource("CI.xml").getFile());
			JAXBContext jaxbContext = JAXBContext.newInstance(edu.iris.dmc.fdsn.station.model.ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			InputStream is = new FileInputStream(source);

			FDSNStationXML original = (FDSNStationXML) jaxbUnmarshaller.unmarshal(is);
			is.close();

			Volume volume = XmlToSeedDocumentConverter.getInstance().convert(original);

			// for(B050 b:volume.getB050s()){
			// System.out.println(b.getStationCode()+" "+b.getStartTime());
			// }

			FDSNStationXML converted = SeedToXmlDocumentConverter.getInstance().convert(volume);

			assertEquals(1, original.getNetwork().size());
			assertEquals(original.getNetwork().size(), converted.getNetwork().size());

			Network originalIU = original.getNetwork().get(0);
			Network convertedIU = converted.getNetwork().get(0);

			// print(originalIU);
			// print(convertedIU);

			assertEquals(1, originalIU.getStations().size());
			assertEquals(originalIU.getStations().size(), convertedIU.getStations().size());

			List<Station> originalStations = originalIU.getStations();
			List<Station> convertedStations = convertedIU.getStations();


			int index = 0;
			for (Station originalStation : originalStations) {
				compare(originalStation, convertedStations.get(index++));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public void compare(Station original, Station converted) {
		if (original.getCode().equals(converted.getCode()) && original.getEndDate().equals(converted.getEndDate())
				&& original.getStartDate().equals(converted.getStartDate())) {

			List<Channel> originalChannels = original.getChannels();
			List<Channel> convertedChannels = converted.getChannels();
			assertEquals(convertedChannels.size(), originalChannels.size());

			for (Channel originalChannel : originalChannels) {
				compare(original,originalChannel, find(convertedChannels, originalChannel));
			}
		} else {
			System.out.println("*************************************");
		}

	}

	public void compare(Station station,Channel original, Channel converted) {
		String key = station.getCode()+"  "+station.getStartDate()+"  "+original.getCode() + "   " + original.getStartDate() + "   " + original.getEndDate();
		if (original.getCode().equals(converted.getCode()) && original.getEndDate().equals(converted.getEndDate())
				&& original.getStartDate().equals(converted.getStartDate())) {

			Sensitivity originalSensitivity = original.getResponse().getInstrumentSensitivity();
			Sensitivity convertedSensitivity = converted.getResponse().getInstrumentSensitivity();
			if (originalSensitivity != null && originalSensitivity.getInputUnits() != null) {
				if (convertedSensitivity.getInputUnits() != null) {
					if (!originalSensitivity.getInputUnits().getName()
							.equals(convertedSensitivity.getInputUnits().getName())) {
						System.out.println("** "+key + "   " + originalSensitivity.getInputUnits().getName() + "    "
								+ convertedSensitivity.getInputUnits().getName()+"     "+originalSensitivity.getOutputUnits().getName() + "    "
								+ convertedSensitivity.getOutputUnits().getName());
					}
				} else {
					System.out.println(key + "   " + originalSensitivity.getInputUnits().getName() + "    NULL");
				}
			} else {
				if (convertedSensitivity != null && convertedSensitivity.getInputUnits() != null) {
					System.out.println(key + "   " + "NULL    " + convertedSensitivity.getInputUnits().getName());
				}

			}

			// System.out.println(
			// original.getResponse().getStage().size() + " " +
			// converted.getResponse().getStage().size());
		} else {
			System.out.println("c*************************************");
		}
	}

	private Channel find(List<Channel> list, Channel channel) {
		for (Channel c : list) {
			if (c.getLocationCode().equals(channel.getLocationCode()) && c.getCode().equals(channel.getCode())
					&& c.getStartDate().equals(channel.getStartDate()) && c.getEndDate().equals(channel.getEndDate())) {
				return c;
			}
		}
		return null;
	}

}
