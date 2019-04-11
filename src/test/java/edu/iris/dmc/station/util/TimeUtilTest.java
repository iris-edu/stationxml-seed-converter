package edu.iris.dmc.station.util;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import org.junit.Test;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.seed.BTime;

public class TimeUtilTest {

	@Test
	public void toTime() throws Exception {
		BTime bTime = new BTime(2000, 1, 10, 10, 10, 10);
		System.out.println(bTime.toSeedString());
		ZonedDateTime zonedDateTime = IrisUtil.toZonedDateTime(bTime);
		System.out.println(bTime.toSeedString()+"    "+zonedDateTime);
	}
	// @Test
	/*
	 * public void toBtime() throws Exception { GregorianCalendar c = new
	 * GregorianCalendar(TimeZone.getTimeZone("GMT"));
	 * 
	 * c.set(1990, 0, 25, 9, 22, 22); c.set(Calendar.MILLISECOND, 22);
	 * XMLGregorianCalendar xCal =
	 * DatatypeFactory.newInstance().newXMLGregorianCalendar(c); BTime bTime =
	 * TimeUtil.toBTime(xCal); assertEquals("1990,025,09:22:22.0022",
	 * bTime.toSeedString());
	 * 
	 * c = new GregorianCalendar(TimeZone.getTimeZone("GMT-7")); c.set(1990, 0, 25,
	 * 2, 22, 22); c.set(Calendar.MILLISECOND, 22); System.out.println(c); xCal =
	 * DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
	 * 
	 * bTime = TimeUtil.toBTime(xCal); assertEquals("1990,025,09:22:22.0022",
	 * bTime.toSeedString()); }
	 * 
	 * @Test public void noTimeZone() throws Exception { GregorianCalendar c = new
	 * GregorianCalendar(TimeZone.getTimeZone("GMT"));
	 * 
	 * c.set(1990, 0, 25, 9, 22, 22); c.set(Calendar.MILLISECOND, 22);
	 * XMLGregorianCalendar xCal =
	 * DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
	 * xCal.setTimezone(DatatypeConstants.FIELD_UNDEFINED); BTime bTime =
	 * TimeUtil.toBTime(xCal); assertEquals("1990,025,09:22:22.0022",
	 * bTime.toSeedString());
	 * 
	 * c = new GregorianCalendar(TimeZone.getTimeZone("GMT-7")); c.set(1990, 0, 25,
	 * 2, 22, 22); c.set(Calendar.MILLISECOND, 22); System.out.println(c); xCal =
	 * DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
	 * 
	 * bTime = TimeUtil.toBTime(xCal); assertEquals("1990,025,09:22:22.0022",
	 * bTime.toSeedString()); }
	 */
}
