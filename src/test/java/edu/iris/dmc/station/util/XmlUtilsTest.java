package edu.iris.dmc.station.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.station.converter.SeedToXmlDocumentConverter;
import edu.iris.dmc.station.converter.UsgsAnmoConverterTest;
import edu.iris.dmc.station.converter.XmlToSeedDocumentConverterTest;
import edu.iris.dmc.station.mapper.StationBlocketteMapper;

public class XmlUtilsTest {

	@Test
	public void load() throws Exception {
		try {
			File source = new File(XmlUtilsTest.class.getClassLoader().getResource("ANMO-one-epoch.xml").getFile());
         	InputStream is = new FileInputStream(source);
			
			Iterator<Station> it = XmlUtils.iterate(is).iterator();
		
			while (it.hasNext()) {
				Station s = it.next();
				B050 b050 = StationBlocketteMapper.map(s);
				System.out.println(s.getNetwork().getCode()+"/"+s.getCode());
			}


			// start new record per station

		}finally {

		}
	}
}
