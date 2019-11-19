package edu.iris.dmc.station.mapper;

import edu.iris.dmc.fdsn.station.model.Sensitivity;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.control.station.B058;

public class SensitivityToBlocketteMapper {

	public static B058 map(Sensitivity s) throws SeedException {
		if (s == null) {
			return null;
		}
		B058 b = new B058();
		if (s.getFrequency() != null) {
			b.setFrequency(s.getFrequency());
		}
		if (s.getValue() != null) {
			b.setSensitivity(s.getValue());
		}
		return b;
	}
}
