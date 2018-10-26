package edu.iris.dmc.station.seed;

import org.junit.Test;

import edu.iris.dmc.fdsn.station.model.PolesZeros;
import edu.iris.dmc.seed.builder.BlocketteBuilder;
import edu.iris.dmc.seed.control.station.B053;
import edu.iris.dmc.station.mapper.PolesZerosMapper;


public class PolesZerosTest {

	@Test
	public void sxs() throws Exception {

		B053 b053 = BlocketteBuilder.build053(
				"0530382A01001002+3.14096E+02+1.00000E+00003+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00-1.70000E-01+0.00000E+00+0.00000E+00+0.00000E+00004+0.00000E+00+0.00000E+00+0.00000E+00+0.00000E+00-3.14000E+02+0.00000E+00+0.00000E+00+0.00000E+00-1.88000E-01+0.00000E+00+0.00000E+00+0.00000E+00-4.40000E-02+0.00000E+00+0.00000E+00+0.00000E+00"
						.getBytes());

		PolesZeros pz = PolesZerosMapper.map(b053);
		
		
		B053 c=PolesZerosMapper.map(pz);

	}
}
