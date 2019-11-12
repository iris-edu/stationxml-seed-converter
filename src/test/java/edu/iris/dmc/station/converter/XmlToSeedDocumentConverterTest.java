package edu.iris.dmc.station.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.seed.control.station.B051;
import edu.iris.dmc.seed.control.station.B052;
import edu.iris.dmc.seed.control.station.B058;
import edu.iris.dmc.seed.control.station.ResponseBlockette;
import edu.iris.dmc.seed.control.station.SeedResponseStage;

public class XmlToSeedDocumentConverterTest {

	@Test
	public void t1() {
		File source = null, target = null;
		try {
			source = new File(
					XmlToSeedDocumentConverterTest.class.getClassLoader().getResource("IU_ANMO_BHZ.xml").getFile());

			final FDSNStationXML document = IrisUtil.readXml(source);
			Volume volume = XmlToSeedDocumentConverter.getInstance().convert(document);

			assertFalse(volume.isEmpty());

			List<Blockette> blockettes = volume.getControlBlockettes();
			Blockette b = blockettes.get(1);

			assertTrue(b instanceof B051);
			byte[] bytes = b.toSeedString().getBytes();
	     	assertEquals(b.getSize(), bytes.length);
	     	
	     	//Blockette are now all built to 9999 in length
	     	/// This could be a problem
	     	
	     	
			/*
			 * blockettes = volume.find("IU", "ANMO", "BHZ", null); assertEquals(8,
			 * blockettes.size());
			 * 
			 * B050 anmo = (B050) blockettes.get(0); BTime start = anmo.getStartTime();
			 * System.out.println(start.toSeedString());
			 * assertEquals("1989,241,00:00:00.0000", start.toSeedString());
			 * anmo.getEndTime(); assertEquals(5, anmo.getNumberOfChannels());
			 * assertEquals(3, anmo.getNumberOfComments());
			 */

			/*
			 * <Value>Time may be 1 minute slow.</Value>
			 * <BeginEffectiveTime>1991-01-31T00:00:00</BeginEffectiveTime>
			 * <EndEffectiveTime>1991-02-16T00:00:00</EndEffectiveTime> </Comment> <Comment>
			 * <Value>Time correction does not include leap second.</Value>
			 * <BeginEffectiveTime>1992-06-30T00:00:00</BeginEffectiveTime>
			 * <EndEffectiveTime>1993-03-22T18:50:00</EndEffectiveTime> </Comment> <Comment>
			 * <Value>TEST DATA: Calibration, and testing in progress.</Value>
			 * <BeginEffectiveTime>1993-02-08T23:00:00</BeginEffectiveTime>
			 * <EndEffectiveTime>1993-02-09T17:50:00</EndEffectiveTime> </Comment>
			 */

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (source != null) {
			}

			if (target != null) {

			}
		}

	}

	@Test
	public void anmoOneEpoch() {
		File source = null, target = null;
		try {

			source = new File(
					XmlToSeedDocumentConverterTest.class.getClassLoader().getResource("ANMO-one-epoch.xml").getFile());

			final FDSNStationXML document = IrisUtil.readXml(source);
			Volume volume = XmlToSeedDocumentConverter.getInstance().convert(document);

			assertFalse(volume.isEmpty());

			// List<Blockette> blockettes = volume.getControlBlockettes();
			List<B050> b050s = volume.getB050s();
			assertEquals(2, b050s.size());
			B050 epoch = b050s.get(0);
			List<B052> b052s = epoch.getB052s();
			assertEquals(1, b052s.size());
			B052 b052 = b052s.get(0);

			List<SeedResponseStage> response = b052.getResponseStages();
			assertNotNull(response);


			SeedResponseStage stage = b052.getResponseStage(1);
			assertNotNull(stage);

			List<ResponseBlockette> blockettes = stage.getBlockettes();
			B058 b058 = (B058) blockettes.get(0);

			System.out.println(b058.getSignalInputUnit());
			System.out.println(b058.getSignalOutputUnit());
			System.out.println(b058.toSeedString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (source != null) {
			}

			if (target != null) {

			}
		}

	}

}
