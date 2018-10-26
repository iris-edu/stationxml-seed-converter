package edu.iris.dmc.station.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.BlocketteFactory;
import edu.iris.dmc.seed.control.station.B052;

public class ChannelMapperTest {

	@Test
	public void btoc() throws Exception {
		String text = "0520149  BDF0000000~000000+28.209718-177.381430+0004.6000.0000.0+00.00000122.0000E+010.0000E+000000CG~2013,315,00:00:00.0000~2017,045,00:00:00.0000~N";
		B052 b052 = (B052) BlocketteFactory.create(text.getBytes());
		Channel channel = ChannelBlocketteMapper.map(b052);

		assertNotNull(channel);

		List<String> types = channel.getType();
		assertNotNull(types);
		assertEquals(2, types.size());
		assertTrue(types.contains("CONTINUOUS"));
		assertTrue(types.contains("GEOPHYSICAL"));
		
		assertEquals(b052.getStartTime().getHour(),channel.getStartDate().getHour());
		assertEquals(0,channel.getStartDate().getTimezone());
		b052 = ChannelBlocketteMapper.map(channel);
		assertEquals(text, b052.toSeedString());
	}
	
	@Test
	public void ctob() throws Exception {
		             //052015400BDF0000002~001002+47.661570-122.313320+100,00001.0000.0360.0+00.00001122.0000E+010.0000E+000000CG~2018,218,00:00:00.0000~2500,365,23:59:59.0000~N
		String text = "0520149  BDF0000000~000000+28.209718-177.381430+0004.6000.0000.0+00.00000122.0000E+010.0000E+000000CG~2013,315,00:00:00.0000~2017,045,00:00:00.0000~N";
		B052 b052 = (B052) BlocketteFactory.create(text.getBytes());
		Channel channel = ChannelBlocketteMapper.map(b052);

		assertNotNull(channel);

		List<String> types = channel.getType();
		assertNotNull(types);
		assertEquals(2, types.size());
		assertTrue(types.contains("CONTINUOUS"));
		assertTrue(types.contains("GEOPHYSICAL"));
		
		assertEquals(b052.getStartTime().getHour(),channel.getStartDate().getHour());
		assertEquals(0,channel.getStartDate().getTimezone());
		b052 = ChannelBlocketteMapper.map(channel);
		assertEquals(text, b052.toSeedString());
	}
}
