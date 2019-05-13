package edu.iris.dmc.station.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.station.B050;

import edu.iris.dmc.station.converter.XmlToSeedFileConverter;

public class XmlToSeedFileConverterTest {

	@Test
	public void t1() throws Exception {

		File xml = new File(XmlToSeedFileConverterTest.class.getClassLoader().getResource("IU_ANMO_BHZ.xml").getFile());

		File convertedSeedFile = new File("converted.dataless");
		XmlToSeedFileConverter.getInstance().convert(xml, convertedSeedFile);

		Volume volume = IrisUtil.readSeed(convertedSeedFile);

		List<B050> list = volume.getB050s();
		assertEquals(2, list.size());

		B050 anmo = list.get(0);

		System.out.println(anmo.getStationCode());
		assertEquals("1989,241,00:00:00.0000", anmo.getStartTime().toSeedString());

	}

	@Test
	public void t2() throws Exception {

		File xml = new File(XmlToSeedFileConverterTest.class.getClassLoader().getResource("IU_ANMO_BHZ.xml").getFile());

		File convertedSeedFile = new File("converted.dataless");
		XmlToSeedFileConverter.getInstance().convert(xml, convertedSeedFile);

		Volume volume = IrisUtil.readSeed(convertedSeedFile);

		List<B050> list = volume.getB050s();
		assertEquals(2, list.size());

		B050 anmo = list.get(0);

		assertEquals(
				"0500134ANMO +34.945900-106.457199+1850.00005003Albuquerque, New Mexico, USA~0013210101989,241,00:00:00.0000~1995,195,00:00:00.0000~NIU",
				anmo.toSeedString());

		assertEquals("ANMO", anmo.getStationCode());

	}

	// @Test
	public void t3() throws Exception {

		File xml = new File(XmlToSeedFileConverterTest.class.getClassLoader().getResource("IU_ANMO_BHZ.xml").getFile());

		File convertedSeedFile = new File("converted.dataless");
		XmlToSeedFileConverter.getInstance().convert(xml, convertedSeedFile);

		Volume volume = IrisUtil.readSeed(convertedSeedFile);

		List<B050> list = volume.getB050s();
		assertEquals(2, list.size());

		B050 anmo = list.get(0);

		assertEquals("ANMO", anmo.getStationCode());

	}

	//@Test
	public void t4() throws Exception {

		File xml = new File(XmlToSeedFileConverterTest.class.getClassLoader().getResource("IU_ANMO_BHZ.xml").getFile());

		File convertedSeedFile = new File("converted.dataless");
		XmlToSeedFileConverter.getInstance().convert(xml, convertedSeedFile);

		Volume volume = IrisUtil.readSeed(convertedSeedFile);

		List<B050> list = volume.getB050s();
		assertEquals(2, list.size());

		B050 anmo = list.get(0);

		assertEquals("ANMO", anmo.getStationCode());

	}

}
