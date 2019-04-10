package edu.iris.dmc.station.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
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
import edu.iris.dmc.seed.control.index.B011;
import edu.iris.dmc.seed.control.index.B011.Row;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.seed.control.station.B052;
import edu.iris.dmc.seed.control.station.ResponseBlockette;
import edu.iris.dmc.seed.control.station.SeedResponseStage;
import edu.iris.dmc.station.mapper.SeedStringBuilder;

public class SeedToXmlConverterRunner {

	public static void main(String[] args) {
		File source = null, target = null;

		// source = new
		// File("/Users/Suleiman/seed/AK_CCB.dataless");//dataless-archive/IU.dataless");
		source = new File("/Users/Suleiman/dataless-archive/IU.dataless");
		Volume volume;
		try {
			volume = IrisUtil.readSeed(source);

			FDSNStationXML document = SeedToXmlDocumentConverter.getInstance().convert(volume);

			assertNotNull(document);
			assertNotNull(document.getNetwork());
			assertFalse(document.getNetwork().isEmpty());

			assertEquals(1, document.getNetwork().size());
			Network iu = document.getNetwork().get(0);

			Station aae = null;
			for (Station s : iu.getStations()) {
				System.out.println(s.getCode());
				if ("AAE".equals(s.getCode())) {
					aae = s;
				}
			}

			B050 aaeB = null;
			Volume convertedVolume = XmlToSeedDocumentConverter.getInstance().convert(document);
			B011 b011 = convertedVolume.getB011();
			for (Row row : b011.getRows()) {
				System.out.println(row.getSequence() + " " + row.getCode());
			}
			for (B050 b : convertedVolume.getB050s()) {
				System.out.println("B050: " + b.getStationCode());
				if ("AAE".equals(b.getStationCode())) {
					aaeB = b;
				}
			}
			assertNotNull(aaeB);
			List<B052> b052s = aaeB.getB052s();

			for (B052 b052 : b052s) {
				System.out.println("B052: " + b052.getChannelCode());
				List<SeedResponseStage> response = b052.getResponseStages();
				for (SeedResponseStage r : response) {
					// System.out.println(r.toSeedString());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
