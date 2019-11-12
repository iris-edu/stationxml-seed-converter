package edu.iris.dmc.station.mapper;

import edu.iris.dmc.fdsn.station.model.StageGain;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.control.station.B058;

public class StageGainToBlocketteMapper {

	private StageGainToBlocketteMapper() {}
	
	public static B058 map(StageGain g) throws SeedException {
		B058 b = new B058();
		b.setFrequency(g.getFrequency());
		b.setSensitivity(g.getValue());
		return b;
	}
}
