package edu.iris.dmc.station;

import edu.iris.dmc.fdsn.station.model.Gain;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.control.station.B058;

public class InstrumentSensitivityToBlocketteMapper {

	public static B058 map(Gain g) throws SeedException {
		if(g==null) {
			return null;
		}
		B058 b = new B058();
		b.setFrequency(g.getFrequency());
		b.setSensitivity(g.getValue());
		return b;
	}
}
