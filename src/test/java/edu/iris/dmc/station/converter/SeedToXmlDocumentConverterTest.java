package edu.iris.dmc.station.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.junit.Test;

import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.station.mapper.SeedStringBuilder;
import edu.iris.dmc.station.util.SeedUtils;

public class SeedToXmlDocumentConverterTest {

	// @Test
	public void t1() {
		File source = null, target = null;

		source = new File(
				XmlToSeedDocumentConverterTest.class.getClassLoader().getResource("CI_OAT.dataless").getFile());

		Volume volume;
		try {
			volume = SeedUtils.load(source);

			FDSNStationXML document = SeedToXmlDocumentConverter.getInstance().convert(volume);

			assertNotNull(document);
			assertNotNull(document.getNetwork());
			assertFalse(document.getNetwork().isEmpty());

			assertEquals(1, document.getNetwork().size());
			Network network = document.getNetwork().get(0);
			assertEquals("CI", network.getCode());

			assertNotNull(network.getStations());
			assertEquals(1, network.getStations().size());

			// 0500096OAT +34.343610-118.614420+1089.00037000Oat
			// Mountain~0013210101999,070,00:00:00.0000~~NCI
			Station station = network.getStations().get(0);
			assertEquals("OAT", station.getCode());

			assertNotNull(station.getSite());
			assertEquals("Oat Mountain", station.getSite().getName());

			station.getStartDate();
			station.getEndDate();
			assertEquals("1999,070,00:00:00.0000", SeedStringBuilder.formatDate(station.getStartDate()));

			// 0520126
			// ACE0000002~001002+34.343610-118.614420+1089.0000.0000.0+00.00001120.0000E+000.0000E+000000I~2014,155,20:11:00.0000~~N
			assertNotNull(station.getChannels());
			assertEquals(37, station.getChannels().size());
			Channel channel = station.getChannels().get(0);

			assertEquals("ACE", channel.getCode());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void t2() {
		File source = null, target = null;

		source = new File(
				XmlToSeedDocumentConverterTest.class.getClassLoader().getResource("CI_OAT.dataless").getFile());

		Volume volume;
		try {
			volume = SeedUtils.load(source);

			FDSNStationXML document = SeedToXmlDocumentConverter.getInstance().convert(volume);
			Volume other = XmlToSeedDocumentConverter.getInstance().convert(document);

			List<Blockette> v = volume.getControlBlockettes();
			List<Blockette> o = other.getControlBlockettes();


			assertEquals(volume.getControlBlockettes().size(), other.getControlBlockettes().size());
			assertEquals(volume.getIndexBlockettes().size(), other.getIndexBlockettes().size());
			assertEquals(volume.getDictionaryBlockettes().size(), other.getDictionaryBlockettes().size());
			assertEquals(volume.size(), other.size());

			compare(volume, other);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void compare(Volume original, Volume other) throws Exception {

		assertEquals(original.size(), other.size());

	}

}
