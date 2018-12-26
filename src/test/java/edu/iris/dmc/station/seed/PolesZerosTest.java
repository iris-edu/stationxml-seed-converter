package edu.iris.dmc.station.seed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import edu.iris.dmc.fdsn.station.model.PoleZero;
import edu.iris.dmc.fdsn.station.model.PolesZeros;
import edu.iris.dmc.seed.BlocketteFactory;
import edu.iris.dmc.seed.builder.BlocketteBuilder;
import edu.iris.dmc.seed.control.station.B053;
import edu.iris.dmc.seed.control.station.Pole;
import edu.iris.dmc.station.mapper.PolesZerosMapper;

public class PolesZerosTest {

	@Test
	public void sxs() throws Exception {

		String text = "0530382A00000000+3.14096E+02+1.00000E+00003+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00-1.70000E-01+0.00000E+00+0.00000E+00+0.00000E+00004+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00-3.14000E+02+0.00000E+00+0.00000E+00+0.00000E+00-1.88000E-01+0.00000E+00+0.00000E+00+0.00000E+00-4.40000E-02+0.00000E+00+0.00000E+00+2.00000E-04";
		B053 b053 = BlocketteBuilder.build053(text.getBytes());

		// String text =
		// "0530382A01001002+3.14096E+02+1.00000E+00003+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00-1.70000E-01+0.00000E+00+0.00000E+00+0.00000E+00004+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00-3.14000E+02+0.00000E+00+0.00000E+00+0.00000E+00-1.88000E-01+0.00000E+00+0.00000E+00+0.00000E+00-4.40000E-02+0.00000E+00+2.00000E-04+2.00000E-04";
		// B053 b053 = (B053) BlocketteFactory.create(text.getBytes());

		assertNotNull(b053.getPoles());
		assertEquals(4, b053.getPoles().size());
		Pole pole = b053.getPoles().get(3);
		assertNotNull(pole.getImaginary());
		assertEquals(0, pole.getImaginary().getValue(), 0.0000000001);
		assertEquals(0.00020E+00, pole.getImaginary().getError(), 0.0000000001);
		assertEquals(text, b053.toSeedString());

		PolesZeros pz = PolesZerosMapper.map(b053);

		List<PoleZero> list = pz.getPole();
		assertNotNull(list);
		assertEquals(4, list.size());

		PoleZero p = list.get(3);
		assertNotNull(p.getReal());
		assertEquals(-4.40000E-02, p.getReal().getValue(), 0.001);
		assertEquals(0, p.getReal().getMinusError(), 0.00000000000000000000000001);
		assertEquals(0, p.getReal().getPlusError(), 0.00000000000000000000000001);

		assertNotNull(p.getImaginary());
		assertEquals(0, p.getImaginary().getValue(), 0.0000000001);
		assertEquals(0.00020E+00, p.getImaginary().getMinusError(), 0.00000000001);
		assertEquals(0.00020E+00, p.getImaginary().getPlusError(), 0.00000000001);

		b053 = PolesZerosMapper.map(pz);
		assertNotNull(b053.getPoles());
		assertEquals(4, b053.getPoles().size());
		pole = b053.getPoles().get(3);
		assertNotNull(pole.getImaginary());
		assertEquals(0, pole.getImaginary().getValue(), 0.0000000001);
		assertEquals(0.00020E+00, pole.getImaginary().getError(), 0.0000000001);

		assertEquals(text, b053.toSeedString());

	}
}
