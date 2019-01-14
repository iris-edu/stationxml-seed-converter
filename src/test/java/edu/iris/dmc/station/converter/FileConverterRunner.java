package edu.iris.dmc.station.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.Record;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.index.B011;
import edu.iris.dmc.seed.control.index.B011.Row;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.seed.control.station.B052;
import edu.iris.dmc.seed.control.station.ResponseBlockette;
import edu.iris.dmc.seed.record.StationRecord;
import edu.iris.dmc.seed.writer.SeedFileWriter;
import edu.iris.dmc.station.mapper.SeedStringBuilder;
import edu.iris.dmc.station.util.SeedUtils;

public class FileConverterRunner {

	public static void main(String[] args) {
		File source = null, target = null;

		// source = new
		// File("/Users/Suleiman/seed/AK_CCB.dataless");//dataless-archive/IU.dataless");
		source = new File("/Users/Suleiman/dataless-archive/IU.dataless");
		Volume volume;
		try {
			volume = SeedUtils.load(source);

			FDSNStationXML document = SeedToXmlDocumentConverter.getInstance().convert(volume);
			volume=null;
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
			convertedVolume.build();
			int logicalrecordLength = (int) Math.pow(2, volume.getB010().getNthPower());
			SeedFileWriter writer = new SeedFileWriter(new File("/Users/Suleiman/iu.converted.seed"),
					logicalrecordLength);
			writer.write(convertedVolume);
			writer.close();

			B011 b011 = convertedVolume.getB011();
			for (Row row : b011.getRows()) {
				System.out.println(row.getSequence() + " " + row.getCode());
				System.out.println(new String(convertedVolume.getRecord(row.getSequence()).getBytes()));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
