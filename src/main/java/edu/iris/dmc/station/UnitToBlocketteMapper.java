package edu.iris.dmc.station;

import edu.iris.dmc.fdsn.station.model.Units;
import edu.iris.dmc.seed.control.dictionary.B034;

public class UnitToBlocketteMapper {
	public static B034 map(Units units) {
		B034 b = new B034();
		b.setName(units.getName());
		b.setDescription(units.getDescription());
		return b;
	}
}
