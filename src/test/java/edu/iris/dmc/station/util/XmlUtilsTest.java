package edu.iris.dmc.station.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.junit.Test;

import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.station.mapper.StationBlocketteMapper;

public class XmlUtilsTest {

	@Test
	public void load() throws Exception {
		try (InputStream inputStream = new FileInputStream(new File("/Users/Suleiman/GIT/stationxml-seed-converter/src/test/resources/ANMO-one-epoch.xml"))) {
			Iterator<Station> it = XmlUtils.iterate(inputStream).iterator();

			while (it.hasNext()) {
				Station s = it.next();
				B050 b050 = StationBlocketteMapper.map(s);
				System.out.println(s.getNetwork().getCode()+"/"+s.getCode());
			}


			// start new record per station

		}
	}
}
