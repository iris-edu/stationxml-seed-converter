package edu.iris.dmc.station;

import javax.xml.datatype.DatatypeConfigurationException;

import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.station.util.TimeUtil;

public class StationBlocketteMapper {

	public static B050 map(Station station) throws SeedException {
		B050 b = new B050();
		b.setStationCode(station.getCode());
		b.setLatitude(station.getLatitude().getValue());
		b.setLongitude(station.getLongitude().getValue());
		b.setElevation(station.getElevation().getValue());
		b.setSiteName(station.getSite().getName());
		b.setBit32BitOrder(3210);
		b.setBit16BitOrder(10);
		try {
			b.setStartTime(TimeUtil.toBTime(station.getStartDate()));
			b.setEndTime(TimeUtil.toBTime(station.getEndDate()));
			b.setUpdateFlag('N');
			return b;
		} catch (DatatypeConfigurationException e) {
			throw new SeedException(e);
		}
	}
}
