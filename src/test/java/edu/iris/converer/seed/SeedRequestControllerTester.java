package edu.iris.converer.seed;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import edu.iris.dmc.converter.control.StationToSeedConverter;
import edu.iris.dmc.converter.control.StationToSeedRequestController;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;

public class SeedRequestControllerTester {

	@Test
	public void process() throws Exception {
		InputStream inputStream = SeedRequestControllerTester.class.getClassLoader().getResourceAsStream("anmo.xml");
		StationToSeedRequestController controller = new StationToSeedRequestController();
		List<Network> list = controller.load(inputStream);
		Network iu = list.get(0);
		Station anmo = iu.getStations().get(0);

		StationToSeedConverter converter = new StationToSeedConverter();
		converter.process(System.out, anmo);

		// assertTrue(result > 0);
		// System.out.println(result);x
	}
}
