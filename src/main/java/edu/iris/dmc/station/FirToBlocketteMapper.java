package edu.iris.dmc.station;

import edu.iris.dmc.fdsn.station.model.FIR;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.control.station.B061;

public class FirToBlocketteMapper {

	public static B061 map(FIR f) throws SeedException {
		B061 b = new B061();
		b.setName(f.getName());

		String symmetryCode = f.getSymmetry();

		if ("NONE".equals(symmetryCode)) {
			b.setSymetryCode('A');
		} else if ("ODD".equals(symmetryCode)) {
			b.setSymetryCode('B');
		} else if ("EVEN".equals(symmetryCode)) {
			b.setSymetryCode('C');
		} else {

		}

		if (f.getNumeratorCoefficient() != null) {
			for (edu.iris.dmc.fdsn.station.model.NumeratorCoefficient n : f.getNumeratorCoefficient()) {
				b.addCoefficient(n.getValue());
			}
		}
		return b;

	}
}
