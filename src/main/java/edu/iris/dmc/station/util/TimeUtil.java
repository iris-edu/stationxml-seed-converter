package edu.iris.dmc.station.util;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

import edu.iris.dmc.seed.BTime;

public class TimeUtil {

	public static ZonedDateTime now() {
		return ZonedDateTime.now(ZoneId.of("UTC"));
	}

	public static ZonedDateTime toZonedDateTime(String source) throws ParseException {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.of("UTC"));
		return ZonedDateTime.parse(source, format);
	}

	public static ZonedDateTime toZonedDateTime(BTime bTime) {
		if (bTime == null) {
			return null;
		}

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy,DDD,HH:mm:ss.nZ");
		String date = bTime.toSeedString() + "00000+0000";

		return ZonedDateTime.parse(date, dateTimeFormatter);
	}

	public static BTime toBTime(ZonedDateTime time) {
		if (time == null) {
			return null;
		}

		return new BTime(time.getYear(), time.getDayOfYear(), time.getHour(), time.getMinute(), time.getSecond(),
				time.get(ChronoField.MILLI_OF_SECOND));
	}

}
