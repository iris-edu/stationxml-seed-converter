package edu.iris.dmc.station.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.seed.BlocketteFactory;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.station.B052;
import edu.iris.dmc.station.converter.SeedToXmlDocumentConverter;
import edu.iris.dmc.station.converter.XmlToSeedDocumentConverter;
import edu.iris.dmc.station.converter.XmlToSeedDocumentConverterTest;

public class BlocketteOrder {
	// This is a mapper test that verifies the order of the output dataless blockettes. 

	@Test
	public void xml2dataless() throws Exception {
		// This is a manual test. Uncomment print statements to java-4-seed volume and watch the output.
		File source = null, target = null;
		try {
			source = new File(BlocketteOrder.class.getClassLoader().getResource("B61B57B58.xml").getFile());
        final FDSNStationXML document = IrisUtil.readXml(source);
        Volume volume = XmlToSeedDocumentConverter.getInstance().convert(document);
        assertFalse(volume.isEmpty());
        volume.build();

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
	public void dataless2xml() throws Exception {
		File source = null, target = null;
		// This is a manual test. Add print statements to fdsn-stationxml-model response stage and watch the output.

		source = new File(
				XmlToSeedDocumentConverterTest.class.getClassLoader().getResource("ResponeB60Order.xml.converted.dataless").getFile());

		Volume volume;
		try {
			volume = IrisUtil.readSeed(source);

			FDSNStationXML document = SeedToXmlDocumentConverter.getInstance().convert(volume);

		
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

