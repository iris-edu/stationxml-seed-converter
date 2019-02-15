package edu.iris.dmc.station.mapper;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import edu.iris.dmc.fdsn.station.model.Dip;
import edu.iris.dmc.fdsn.station.model.Distance;
import edu.iris.dmc.fdsn.station.model.Latitude;
import edu.iris.dmc.fdsn.station.model.Longitude;
import edu.iris.dmc.fdsn.station.model.SampleRate;

public class SeedStringBuilder {

	private List<String> builder = new ArrayList<>();
	private int type;
	private char seperator;

	public SeedStringBuilder(int type) {
		this(type, '|');
	}

	public static SeedStringBuilder ofType(int blkType) {
		return new SeedStringBuilder(blkType);
	}

	public SeedStringBuilder(int type, char seperator) {
		this.type = type;
		this.seperator = seperator;
		builder.add(Integer.toString(type));
		builder.add(Integer.toString(0));
	}

	public SeedStringBuilder append(String object) {
		builder.add(object);
		return this;
	}

	public SeedStringBuilder append(int object) {
		builder.add(Integer.toString(object));
		return this;
	}

	public SeedStringBuilder append(double object) {
		builder.add(Double.toString(object));
		return this;
	}

	public SeedStringBuilder append(Longitude longitude) {
		if (longitude == null) {
			return append(0);
		}
		return append(longitude.getValue());
	}

	public SeedStringBuilder append(Latitude latitude) {
		if (latitude == null) {
			return append(0);
		}
		return append(latitude.getValue());
	}

	public SeedStringBuilder append(Distance distance) {
		if (distance == null) {
			return append(0);
		}

		return append(distance.getValue());
	}

	public SeedStringBuilder append(Dip dip) {
		if (dip == null) {
			return append(0);
		}

		return append(dip.getValue());
	}

	public SeedStringBuilder append(SampleRate sampleRate) {
		if (sampleRate == null) {
			return append(0.0);
		}

		return append(sampleRate.getValue());
	}

	public SeedStringBuilder append(ZonedDateTime object) {
		builder.add(formatDate(object));
		return this;
	}

	public SeedStringBuilder appendNull() {
		this.builder.add(null);
		return this;
	}

	public static String formatDate(ZonedDateTime time) {
		if (time == null) {
			return null;
		}
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy,DDD,HH:mm:ss.SSSS");

		String s = dtf.format(time);
		return s;
	}

	public int getType() {
		return this.type;
	}

	public int size() {
		return this.builder.size();
	}

	public String toString() {
		return builder.stream().map(item -> item == null ? "^" : item)
				.collect(Collectors.joining(String.valueOf(this.seperator)));
	}

	public String[] toArray() {
		String[] array = new String[builder.size()];
		return builder.toArray(array);
	}
}
