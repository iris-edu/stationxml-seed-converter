package edu.iris.dmc.station.mapper;

import edu.iris.dmc.fdsn.station.model.Latitude;
import edu.iris.dmc.seed.SeedException;

public class LatitudeMapper extends AbstractMapper {

	public static Latitude build(Double latitude) throws SeedException {
		if (latitude == null) {
			return null;
		}

		Latitude l = factory.createLatitudeType();
		l.setValue(latitude);
		l.setDatum("WGS84");
		// l.setMinusError(value);
		// l.setPlusError(value);
		l.setUnit("DEGREES");
		return l;
	}
}
