package edu.iris.dmc.station.mapper;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.control.station.B050;

public class StationBlocketteMapper {

	private StationBlocketteMapper() {
	}

	public static B050 map(Station station) throws SeedException {
		
		B050 b = new B050();
		try {
		    b.setStationCode(station.getCode());
		}catch(NullPointerException e){	
		}
		try {
		    b.setLatitude(station.getLatitude().getValue());
		}catch(NullPointerException e){	
		}
		try {
		    b.setLongitude(station.getLongitude().getValue());
		}catch(NullPointerException e){	
		}
		try {
		    b.setElevation(station.getElevation().getValue());
		}catch(NullPointerException e){	
		}
		try {
		    b.setSiteName(station.getSite().getName());
		}catch(NullPointerException e){	
		}
		b.setBit32BitOrder(3210);
		b.setBit16BitOrder(10);

		b.setStartTime(IrisUtil.toBTime(station.getStartDate()));
		b.setEndTime(IrisUtil.toBTime(station.getEndDate()));
		b.setUpdateFlag('N');
		return b;


	}
}
