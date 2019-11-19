package edu.iris.dmc.station.mapper;

import edu.iris.dmc.fdsn.station.model.Sensitivity;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.control.station.B058;

public class InstrumentSensitivityToBlocketteMapper {

	public static B058 map(Sensitivity g) throws SeedException {
		if (g == null) {
			return null;
		}
		B058 b = new B058();
		if (g.getFrequency() != null) {
			b.setFrequency(g.getFrequency());
		}
		if (g.getValue() != null) {
			b.setSensitivity(g.getValue());
		}
		return b;
	}
}
