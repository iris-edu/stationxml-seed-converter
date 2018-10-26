package edu.iris.dmc.station.seed;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.iris.dmc.fdsn.station.model.Polynomial;
import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.BlocketteFactory;
import edu.iris.dmc.seed.control.station.B062;
import edu.iris.dmc.station.mapper.PolynomialMapper;

public class PolynomialTest {

	@Test
	public void b062() throws Exception {

		             //0620130P00000000MB+0.00000E+00+0.00000E+00+0.00000E+00+5.00000E+01+0.00000E+000002-8.39688E+01+0.00000E+00+2.56250E-03+0.00000E+00
		String text = "0620129P00021020MB 0.00000E+00 0.00000E+00 0.00000E+00 5.00000E+01 0.00000E+00002-8.39688E+01 0.00000E+00 2.56250E-03 0.00000E+00";
		B062 b062 = (B062) BlocketteFactory.create(text.getBytes());
		Polynomial polynomial = PolynomialMapper.map(b062);
		B062 b = PolynomialMapper.map(polynomial);
		assertEquals("0620129P00000000MB+0.00000E+00+0.00000E+00+0.00000E+00+5.00000E+01+0.00000E+00002-8.39688E+01+0.00000E+00+2.56250E-03+0.00000E+00", b.toSeedString());
	}
}
