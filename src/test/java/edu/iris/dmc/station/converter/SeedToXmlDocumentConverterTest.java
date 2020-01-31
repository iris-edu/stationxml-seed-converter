package edu.iris.dmc.station.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.seed.BTime;
import edu.iris.dmc.station.mapper.SeedStringBuilder;

public class SeedToXmlDocumentConverterTest {

	 @Test
	public void t1() {
		File source = null, target = null;

		source = new File(
				XmlToSeedDocumentConverterTest.class.getClassLoader().getResource("CI_OAT.dataless").getFile());

		Volume volume;
		try {
			volume = IrisUtil.readSeed(source);

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
			
			assertEquals("counts", channel.getCalibrationUnits().getName());
			assertEquals("ACE", channel.getCode());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Test
	public void startend_time() {
		File source = null, target = null;

		source = new File(
				XmlToSeedDocumentConverterTest.class.getClassLoader().getResource("AfricaArray_20190912_incl_sansn.dataless.dlsv").getFile());

		Volume volume;
		try {
			volume = IrisUtil.readSeed(source);

			FDSNStationXML document = SeedToXmlDocumentConverter.getInstance().convert(volume);
			List<Network> netlist = document.getNetwork();
			Network net = netlist.get(0);
			
			// Determine that network start time and end time are being converted to dataless
			assertEquals(net.getStartDate().toString(), "2004-09-01T00:00Z");
			assertNull(net.getEndDate());
			
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
			volume = IrisUtil.readSeed(source);

			FDSNStationXML document = SeedToXmlDocumentConverter.getInstance().convert(volume);
			Volume other = XmlToSeedDocumentConverter.getInstance().convert(document);

	
			
			//JAXBContext jaxbContext = JAXBContext.newInstance(edu.iris.dmc.fdsn.station.model.ObjectFactory.class);
			//Marshaller marshaller = jaxbContext.createMarshaller();
			//marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			//marshaller.marshal(document, System.out);
			
			
			List<Blockette> v = volume.getControlBlockettes();
			List<Blockette> o = other.getControlBlockettes();

			for(Blockette b:volume.getIndexBlockettes()){
				System.out.println(b.toSeedString());
			}
			
			for(Blockette b:other.getIndexBlockettes()){
				System.out.println(b.toSeedString());
			}

			

			assertEquals(volume.getControlBlockettes().size(), other.getControlBlockettes().size());
		//	assertEquals(volume.getIndexBlockettes().size(), other.getIndexBlockettes().size());
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
