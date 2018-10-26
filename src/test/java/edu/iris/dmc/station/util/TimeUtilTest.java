package edu.iris.dmc.station.util;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

import edu.iris.dmc.seed.BTime;

public class TimeUtilTest {

	//@Test
	public void toBtime() throws Exception {
		GregorianCalendar c = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

		c.set(1990, 0, 25, 9, 22, 22);
		c.set(Calendar.MILLISECOND, 22);
		XMLGregorianCalendar xCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		BTime bTime = TimeUtil.toBTime(xCal);
		assertEquals("1990,025,09:22:22.0022", bTime.toSeedString());

		c = new GregorianCalendar(TimeZone.getTimeZone("GMT-7"));
		c.set(1990, 0, 25, 2, 22, 22);
		c.set(Calendar.MILLISECOND, 22);
		System.out.println(c);
		xCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		
		bTime = TimeUtil.toBTime(xCal);
		assertEquals("1990,025,09:22:22.0022", bTime.toSeedString());
	}
	
	@Test
	public void noTimeZone() throws Exception {
		GregorianCalendar c = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

		c.set(1990, 0, 25, 9, 22, 22);
		c.set(Calendar.MILLISECOND, 22);
		XMLGregorianCalendar xCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		xCal.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		BTime bTime = TimeUtil.toBTime(xCal);
		assertEquals("1990,025,09:22:22.0022", bTime.toSeedString());

		c = new GregorianCalendar(TimeZone.getTimeZone("GMT-7"));
		c.set(1990, 0, 25, 2, 22, 22);
		c.set(Calendar.MILLISECOND, 22);
		System.out.println(c);
		xCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		
		bTime = TimeUtil.toBTime(xCal);
		assertEquals("1990,025,09:22:22.0022", bTime.toSeedString());
	}
}
