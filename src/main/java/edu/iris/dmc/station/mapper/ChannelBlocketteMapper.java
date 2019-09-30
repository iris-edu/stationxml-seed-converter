package edu.iris.dmc.station.mapper;

import java.math.BigDecimal;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.fdsn.station.model.Azimuth;
import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Channel.ClockDrift;
import edu.iris.dmc.fdsn.station.model.Dip;
import edu.iris.dmc.fdsn.station.model.Distance;
import edu.iris.dmc.fdsn.station.model.Latitude;
import edu.iris.dmc.fdsn.station.model.Longitude;
import edu.iris.dmc.fdsn.station.model.SampleRate;
import edu.iris.dmc.seed.BTime;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.control.station.B052;

public class ChannelBlocketteMapper extends AbstractMapper {

	public static Channel map(B052 blockette) throws Exception {
		String location = blockette.getLocationCode();
		String chanCode = blockette.getChannelCode();

		Channel channel = factory.createChannelType();

		channel.setCode(chanCode);
		channel.setLocationCode(location.trim());

		BTime bTime = blockette.getStartTime();
		if (bTime != null) {
			channel.setStartDate(IrisUtil.toZonedDateTime(bTime));
		}

		bTime = blockette.getEndTime();
		if (bTime != null) {
			channel.setEndDate(IrisUtil.toZonedDateTime(bTime));
		}

		Latitude latitude = factory.createLatitudeType();
		latitude.setValue(blockette.getLatitude());
		channel.setLatitude(latitude);

		Longitude longitude = factory.createLongitudeType();
		longitude.setValue(blockette.getLongitude());
		channel.setLongitude(longitude);

		Distance elevation = factory.createDistanceType();
		elevation.setValue(blockette.getElevation());
		channel.setElevation(elevation);

		Distance depth = factory.createDistanceType();
		depth.setValue((Double) blockette.getLocalDepth());
		channel.setDepth(depth);

		Dip dip = factory.createDipType();
		dip.setValue(blockette.getDip());
		channel.setDip(dip);

		Double azimuthValue = blockette.getAzimuth();

		if (azimuthValue != null) {
			Azimuth azimuth = factory.createAzimuthType();
			if (dip != null && (dip.getValue() == 90 || dip.getValue() == -90)) {
				if (BigDecimal.ZERO.compareTo(BigDecimal.valueOf(azimuthValue)) != 0) {
					if (azimuthValue.intValue() >= 360) {
						azimuth.setValue(Double.valueOf(0));
					} else {
						azimuth.setValue(azimuthValue);
					}
				}
			} else {
				azimuth.setValue(azimuthValue);
			}
			channel.setAzimuth(azimuth);
		}

		SampleRate sampleRate = factory.createSampleRateType();
		sampleRate.setValue(blockette.getSampleRate());
		channel.setSampleRate(sampleRate);

		ClockDrift clockDrift = factory.createChannelTypeClockDrift();
		clockDrift.setValue(blockette.getMaxClockDrift());
		channel.setClockDrift(clockDrift);

		blockette.getChannelFlags();
		String cType = blockette.getChannelFlags();
		if (cType != null) {
			for (int i = 0; i < cType.length(); i++) {
				char kar = cType.charAt(i);
				if ('C' == kar) {
					channel.addType("CONTINUOUS");
				} else if ('G' == kar) {
					channel.addType("GEOPHYSICAL");
				} else if ('T' == kar) {
					channel.addType("TRIGGERED");
				} else if ('H' == kar) {
					channel.addType("HEALTH");
				} else if ('W' == kar) {
					channel.addType("WEATHER");
				} else if ('F' == kar) {
					channel.addType("FLAG");
				} else if ('S' == kar) {
					channel.addType("SYNTHESIZED");
				} else if ('I' == kar) {
					channel.addType("INPUT");
				} else if ('E' == kar) {
					channel.addType("EXPERIMENTAL");
				} else if ('M' == kar) {
					channel.addType("MAINTENANCE");
				} else if ('B' == kar) {
					channel.addType("BEAM");
				} else {

				}
			}
		}

		return channel;

	}

	public static B052 map(Channel channel) throws SeedException {

		B052 b = new B052();

		b.setLocationCode(channel.getLocationCode());
		b.setChannelCode(channel.getCode());
		b.setOptionalComment(null);

		b.setLatitude(channel.getLatitude().getValue());
		b.setLongitude(channel.getLongitude().getValue());
		if (channel.getElevation() != null) {
			b.setElevation(channel.getElevation().getValue());
		}

		if (channel.getDepth() != null) {
			b.setLocalDepth(channel.getDepth().getValue());
		}
		if (channel.getAzimuth() != null) {
			b.setAzimuth(channel.getAzimuth().getValue());
		}
		if (channel.getDip() != null) {
			b.setDip(channel.getDip().getValue());
		}
		b.setLocalDepth(channel.getDepth().getValue());
		try {
		    b.setAzimuth(channel.getAzimuth().getValue());
		    b.setDip(channel.getDip().getValue());
		}catch(NullPointerException e) {
			}

		b.setDataRecordLength(12);
		if (channel.getSampleRate() != null) {
			b.setSampleRate(channel.getSampleRate().getValue());
		}
		if (channel.getClockDrift() != null) {
			b.setMaxClockDrift(channel.getClockDrift().getValue());
		}

		StringBuilder sb = new StringBuilder();

		for (String s : channel.getType()) {
			if ("CONTINUOUS".equalsIgnoreCase(s)) {
				sb.append("C");
			} else if ("GEOPHYSICAL".equalsIgnoreCase(s)) {
				sb.append("G");
			} else if ("TRIGGERED".equalsIgnoreCase(s)) {
				sb.append("T");
			} else if ("HEALTH".equalsIgnoreCase(s)) {
				sb.append("H");
			} else if ("WEATHER".equalsIgnoreCase(s)) {
				sb.append("W");
			} else if ("FLAG".equalsIgnoreCase(s)) {
				sb.append("F");
			} else if ("SYNTHESIZED".equalsIgnoreCase(s)) {
				sb.append("S");
			} else if ("INPUT".equalsIgnoreCase(s)) {
				sb.append("I");
			} else if ("EXPERIMENTAL".equalsIgnoreCase(s)) {
				sb.append("E");
			} else if ("MAINTENANCE".equalsIgnoreCase(s)) {
				sb.append("M");
			} else if ("BEAM".equalsIgnoreCase(s)) {
				sb.append("B");
			} else {
				sb.append("^");
			}
		}
		b.setChannelFlags(sb.toString());

		b.setStartTime(IrisUtil.toBTime(channel.getStartDate()));
		b.setEndTime(IrisUtil.toBTime(channel.getEndDate()));
		b.setUpdateFlag('N');
		return b;

	}
}
