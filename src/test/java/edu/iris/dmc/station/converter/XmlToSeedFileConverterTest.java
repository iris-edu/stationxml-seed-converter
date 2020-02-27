package edu.iris.dmc.station.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.dictionary.B031;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.seed.control.station.B051;
import edu.iris.dmc.seed.control.station.B059;
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
	public void b11() throws Exception {

		File xml = new File(XmlToSeedFileConverterTest.class.getClassLoader().getResource("b011_test.xml").getFile());

		File convertedSeedFile = new File("converted.dataless");
		XmlToSeedFileConverter.getInstance().convert(xml, convertedSeedFile);

		Volume volume = IrisUtil.readSeed(convertedSeedFile);

		List<B050> list = volume.getB050s();
		assertEquals(11, list.size());
		assertEquals(volume.getB011().toSeedString(), "0110043003NWAO 000003SJG  000154SPA  000985");
		


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

	//@Test
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

	@Test
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
	
	@Test
	public void commentConversion() throws Exception {

		File xml = new File(XmlToSeedFileConverterTest.class.getClassLoader().getResource("comment.xml").getFile());

		File convertedSeedFile = new File("converted.dataless");
		XmlToSeedFileConverter.getInstance().convert(xml, convertedSeedFile);

		Volume volume = IrisUtil.readSeed(convertedSeedFile);

		B051 StaCommentLong = volume.getB050s().get(0).getB051s().get(0);
		B031 Stab031 = (B031) volume.getDictionaryBlockette(31, StaCommentLong.getLookupKey());
		//System.out.println(Stab031.getDescription());
		assertEquals(70, Stab031.getDescription().length());
		B051 StaCommentShort = volume.getB050s().get(0).getB051s().get(1);
		B031 Stab031Short = (B031) volume.getDictionaryBlockette(31, StaCommentShort.getLookupKey());
		//System.out.println(Stab031Short.getDescription());
		assertEquals(21, Stab031Short.getDescription().length());
		
		B059 ChaComment = volume.getB050s().get(0).getB052s().get(0).getB059s().get(0);
		B031 Chab031 = (B031) volume.getDictionaryBlockette(31, ChaComment.getLookupKey());
		//System.out.println(Chab031.getDescription());
		assertEquals(70, Chab031.getDescription().length());
		B059 ChaCommentShort = volume.getB050s().get(0).getB052s().get(0).getB059s().get(1);
		B031 Chab031Short = (B031) volume.getDictionaryBlockette(31, ChaCommentShort.getLookupKey());
		//System.out.println(Chab031Short.getDescription());
		assertEquals(23, Chab031Short.getDescription().length());
		
		
		//assertEquals(70, ChaComment.get);


	}

}
