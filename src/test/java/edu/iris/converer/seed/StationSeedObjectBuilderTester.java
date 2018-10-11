package edu.iris.converer.seed;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import edu.iris.Fissures.seed.container.Blockette;
import edu.iris.Fissures.seed.container.SeedObjectContainer;
import edu.iris.dmc.converter.control.StationToSeedRequestController;
import edu.iris.dmc.converter.seed.StationSeedObjectBuilder;
import edu.iris.dmc.fdsn.station.model.Azimuth;
import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Dip;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;

public class StationSeedObjectBuilderTester {

	@Test
	public void build() throws Exception {

		InputStream inputStream = StationSeedObjectBuilderTester.class.getClassLoader().getResourceAsStream("anmo.xml");
		StationToSeedRequestController controller = new StationToSeedRequestController();

		List<Network> list = controller.load(inputStream);
		Network network = list.get(0);
		network.setDescription("Puerto Peñasco");
		Station anmo = network.getStations().get(0);

		StationSeedObjectBuilder builder = new StationSeedObjectBuilder();
		int stationLookup = builder.build(anmo);

		assertTrue(stationLookup > 0);
		SeedObjectContainer container = (SeedObjectContainer) builder.getContainer();
		assertNotNull(container);
		Blockette blockette = (Blockette) container.get(stationLookup);
		assertNotNull(blockette);
		assertEquals(50, blockette.getType());
		String code = (String) blockette.getFieldVal(3, 0);
		assertEquals(anmo.getCode(), code);

		String networkCode = (String) blockette.getFieldVal(16, 0);
		assertEquals(network.getCode(), networkCode);

		Blockette dictionaryBlockette = container.getDictionaryBlockette(blockette, 10);
		String networkDescription = (String) dictionaryBlockette.getFieldVal(4, 0);
		assertEquals("Puerto Penasco", networkDescription);

		List<Channel> channels = anmo.getChannels();
		Channel channel = channels.get(0);

		Azimuth azimuth = new Azimuth();
		azimuth.setValue(0);
		channel.setAzimuth(azimuth);

		int channelLookup = builder.build(channel, stationLookup);

		assertTrue(channelLookup > 0);
		blockette = (Blockette) container.get(channelLookup);
		assertNotNull(blockette);
		assertEquals(52, blockette.getType());
		String location = (String) blockette.getFieldVal(3, 0);
		String locationCode = "  ";
		if (!channel.getLocationCode().isEmpty()) {
			locationCode = channel.getLocationCode();
		}
		assertEquals(locationCode, location);
		code = (String) blockette.getFieldVal(4, 0);
		assertEquals(channel.getCode(), code);

		channel.getAzimuth().getValue();

		assertEquals(0.0, blockette.getFieldVal(14, 0));

		azimuth = new Azimuth();
		azimuth.setValue(0);
		channel.setAzimuth(azimuth);

		Dip dip = new Dip();
		dip.setValue(90);
		channel.setDip(dip);

		channelLookup = builder.build(channel, stationLookup);

		assertTrue(channelLookup > 0);
		blockette = (Blockette) container.get(channelLookup);
		assertNotNull(blockette);
		assertEquals(52, blockette.getType());
		location = (String) blockette.getFieldVal(3, 0);
		locationCode = "  ";
		if (!channel.getLocationCode().isEmpty()) {
			locationCode = channel.getLocationCode();
		}
		assertEquals(locationCode, location);
		code = (String) blockette.getFieldVal(4, 0);
		assertEquals(channel.getCode(), code);

		channel.getAzimuth().getValue();

		assertEquals(360.0, blockette.getFieldVal(14, 0));

		azimuth = new Azimuth();
		azimuth.setValue(32);
		channel.setAzimuth(azimuth);

		dip = new Dip();
		dip.setValue(90);
		channel.setDip(dip);

		channelLookup = builder.build(channel, stationLookup);

		assertTrue(channelLookup > 0);
		blockette = (Blockette) container.get(channelLookup);
		assertNotNull(blockette);
		assertEquals(52, blockette.getType());
		location = (String) blockette.getFieldVal(3, 0);
		locationCode = "  ";
		if (!channel.getLocationCode().isEmpty()) {
			locationCode = channel.getLocationCode();
		}
		assertEquals(locationCode, location);
		code = (String) blockette.getFieldVal(4, 0);
		assertEquals(channel.getCode(), code);

		channel.getAzimuth().getValue();

		assertEquals(32.0, blockette.getFieldVal(14, 0));

		/*
		 * SeedExportBuilder exportBuilder = new SeedExportBuilder("dataless");
		 * if (this.sender != null) {
		 * exportBuilder.setOrganizationName(this.sender); }
		 * exportBuilder.open(out); SeedExportDirector director = new
		 * SeedExportDirector(); director.assignBuilder(exportBuilder);
		 * director.assignContainer(builder.getContainer());
		 * director.assignTemplate(new ExportTemplate());
		 * director.fillTemplate(null);
		 * 
		 * director.construct(); exportBuilder.close();//added this
		 */

	}
}
