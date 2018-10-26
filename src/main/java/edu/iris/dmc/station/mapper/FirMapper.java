package edu.iris.dmc.station.mapper;

import edu.iris.dmc.fdsn.station.model.FIR;
import edu.iris.dmc.fdsn.station.model.NumeratorCoefficient;
import edu.iris.dmc.seed.control.dictionary.B041;
import edu.iris.dmc.seed.control.station.B061;

public class FirMapper extends AbstractMapper {

	public static FIR map(B041 b) {
		FIR fType = factory.createFIRType();

		char symmetryCode = b.getSymetryCode();

		if ('A' == symmetryCode) {
			fType.setSymmetry("NONE");
		} else if ('B' == (symmetryCode)) {
			fType.setSymmetry("ODD");
		} else if ('C' == symmetryCode) {
			fType.setSymmetry("EVEN");
		} else {
			fType.setSymmetry("NONE");
		}

		fType.setName(b.getName());

		for (Double d : b.getCoefficients()) {
			NumeratorCoefficient nc = new NumeratorCoefficient();
			nc.setValue(d);
			fType.getNumeratorCoefficient().add(nc);
		}

		return fType;
	}
	
	public static FIR build(B061 b) {
		FIR fType = factory.createFIRType();

		char symmetryCode = b.getSymetryCode();

		if ('A' == symmetryCode) {
			fType.setSymmetry("NONE");
		} else if ('B' == (symmetryCode)) {
			fType.setSymmetry("ODD");
		} else if ('C' == symmetryCode) {
			fType.setSymmetry("EVEN");
		} else {
			fType.setSymmetry("NONE");
		}

		fType.setName(b.getName());

		for (Double d : b.getCoefficients()) {
			NumeratorCoefficient nc = new NumeratorCoefficient();
			nc.setValue(d);
			fType.getNumeratorCoefficient().add(nc);
		}

		return fType;
	}
}
