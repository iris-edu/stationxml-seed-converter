package edu.iris.dmc.station.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.seed.BTime;
import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.seed.control.station.B051;
import edu.iris.dmc.seed.control.station.B052;
import edu.iris.dmc.seed.control.station.B057;
import edu.iris.dmc.seed.control.station.ResponseBlockette;
import edu.iris.dmc.station.util.SeedUtils;
import edu.iris.dmc.station.util.XmlUtils;

public class DocumentConverterTest {

	@Test
	public void t1() {
		File source = null, target = null;
		try {

			source = new File(DocumentConverterTest.class.getClassLoader()
					.getResource("IM_DATALESS_I58_infrasound_BDF_20170524.dataless").getFile());

			final Volume original = SeedUtils.load(source);
			FDSNStationXML document = SeedToXmlDocumentConverter.getInstance().convert(original);

			Volume converted = XmlToSeedDocumentConverter.getInstance().convert(document);

			List<Blockette> oList = original.getAll();
			List<Blockette> cList = converted.getAll();
			
			assertEquals(oList.size(),cList.size());
			
			assertEquals(converted.getB050s().size(),original.getB050s().size());

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
