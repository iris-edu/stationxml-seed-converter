package edu.iris.dmc.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.PolesZeros;
import edu.iris.dmc.fdsn.station.model.ResponseStage;
import edu.iris.dmc.fdsn.station.model.Units;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.dictionary.B034;
import edu.iris.dmc.seed.control.station.B052;
import edu.iris.dmc.seed.control.station.B053;
import edu.iris.dmc.seed.control.station.ResponseBlockette;
import edu.iris.dmc.seed.control.station.SeedResponseStage;
import edu.iris.dmc.station.converter.SeedToXmlDocumentConverter;
import edu.iris.dmc.station.converter.XmlToSeedDocumentConverter;
import edu.iris.dmc.station.converter.XmlToSeedDocumentConverterTest;

public class SimpleUnitFormatTest {

	@Test
	public void nullUnit() throws Exception {
		SimpleUnitFormat instance = SimpleUnitFormat.getInstance();
		Units u = instance.parse("");

		assertNotNull(u);
		assertEquals("unknown", u.getName());

		u = instance.parse(" ");

		assertNotNull(u);
		assertEquals("unknown", u.getName());

		u = instance.parse(null);

		assertNotNull(u);
		assertEquals("unknown", u.getName());

	}
	
	@Test
	public void percent() throws Exception {
		SimpleUnitFormat instance = SimpleUnitFormat.getInstance();
		Units u = instance.parse("%");

		assertNotNull(u);
		assertEquals("percent", u.getName());

	}

	@Test
	public void fromFile() throws Exception {
		try (InputStream stream = XmlToSeedDocumentConverterTest.class.getClassLoader()
				.getResourceAsStream("CUANWBBH2.xml")) {
			FDSNStationXML document = IrisUtil.readXml(stream);

			Channel channel = document.getNetwork().get(0).getStations().get(0).getChannels().get(0);

			ResponseStage stage = channel.getResponse().getStage().get(0);
			PolesZeros pzs = stage.getPolesZeros();
			assertEquals("M/S", pzs.getInputUnits().getName());
			assertEquals("Velocity in Meters Per Second", pzs.getInputUnits().getDescription());
			Volume volume = XmlToSeedDocumentConverter.getInstance().convert(document);

			B052 b052 = volume.getB050s().get(0).getB052s().get(0);

			SeedResponseStage seedStage = b052.getResponseStage(1);
			List<ResponseBlockette> rb = seedStage.getBlockettes();

			ResponseBlockette b = rb.get(0);
			assertTrue(b instanceof B053);

			B053 b053 = (B053) b;
			B034 inputUnits = (B034) volume.getDictionaryBlockette(34, b053.getSignalInputUnit());
			assertNotNull(inputUnits);

			assertEquals("M/S", inputUnits.getName());
			assertEquals("Velocity in Meters Per Second", inputUnits.getDescription());
			B034 outputUnits = (B034) volume.getDictionaryBlockette(34, b053.getSignalOutputUnit());
			assertEquals("V", outputUnits.getName());
			assertEquals("Volts", outputUnits.getDescription());

			assertNotNull(outputUnits);
			FDSNStationXML target = SeedToXmlDocumentConverter.getInstance().convert(volume);

			channel = target.getNetwork().get(0).getStations().get(0).getChannels().get(0);

			stage = channel.getResponse().getStage().get(0);
			pzs = stage.getPolesZeros();
			assertEquals("m/s", pzs.getInputUnits().getName());
			assertEquals("Velocity in Meters Per Second", pzs.getInputUnits().getDescription());


			// Files.marshal(target, System.out);

		}

	}
}
