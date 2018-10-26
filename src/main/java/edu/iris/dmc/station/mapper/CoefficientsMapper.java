package edu.iris.dmc.station.mapper;

import edu.iris.dmc.fdsn.station.model.Coefficients;
import edu.iris.dmc.fdsn.station.model.Float;
import edu.iris.dmc.fdsn.station.model.ObjectFactory;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.control.dictionary.B044;
import edu.iris.dmc.seed.control.station.B054;

public class CoefficientsMapper extends AbstractMapper {

	public static Coefficients map(B044 b) {
		Coefficients coefficients = new ObjectFactory().createCoefficientsType();

		char responseType = b.getResponseType();
		String transferFunction = "";
		if ('A' == responseType) {
			transferFunction = "ANALOG (RADIANS/SECOND)";
		} else if ('B' == responseType) {
			transferFunction = "ANALOG (HERTZ)";
		} else if ('D' == responseType) {
			transferFunction = "DIGITAL";
		}

		coefficients.setCfTransferFunctionType(transferFunction);
		for (edu.iris.dmc.seed.control.station.Number n : b.getNumerators()) {
			Float ft = new ObjectFactory().createFloatType();
			ft.setValue(n.getValue());
			ft.setMinusError(n.getError());
			ft.setPlusError(n.getError());
			coefficients.getNumerator().add(ft);
		}
		for (edu.iris.dmc.seed.control.station.Number n : b.getDenominators()) {
			Float ft = new ObjectFactory().createFloatType();
			ft.setValue(n.getValue());
			ft.setMinusError(n.getError());
			ft.setPlusError(n.getError());
			coefficients.getDenominator().add(ft);
		}

		return coefficients;
	}

	public static Coefficients map(B054 b) {

		Coefficients coefficients = new ObjectFactory().createCoefficientsType();

		char responseType = b.getResponseType();
		String transferFunction = "";
		if ('A' == responseType) {
			transferFunction = "ANALOG (RADIANS/SECOND)";
		} else if ('B' == responseType) {
			transferFunction = "ANALOG (HERTZ)";
		} else if ('D' == responseType) {
			transferFunction = "DIGITAL";
		}

		coefficients.setCfTransferFunctionType(transferFunction);
		for (edu.iris.dmc.seed.control.station.Number n : b.getNumerators()) {
			Float ft = new ObjectFactory().createFloatType();
			ft.setValue(n.getValue());
			ft.setMinusError(n.getError());
			ft.setPlusError(n.getError());
			coefficients.getNumerator().add(ft);
		}
		for (edu.iris.dmc.seed.control.station.Number n : b.getDenominators()) {
			Float ft = new ObjectFactory().createFloatType();
			ft.setValue(n.getValue());
			ft.setMinusError(n.getError());
			ft.setPlusError(n.getError());
			coefficients.getDenominator().add(ft);
		}

		return coefficients;

	}

	public static B054 map(Coefficients c) throws SeedException {
		B054 b = new B054();

		char transferFunction = 'N';
		switch (c.getCfTransferFunctionType()) {
		case "ANALOG (RADIANS/SECOND)":
			transferFunction = 'A';
			break;
		case "ANALOG (HERTZ)":
			transferFunction = 'B';
			break;
		case "DIGITAL":
			transferFunction = 'D';
			break;
		}
		if (transferFunction == 'N') {
			throw new SeedException("Invalid blockette 054 transfer function: " + c.getCfTransferFunctionType());
		}
		b.setResponseType(transferFunction);

		if (c.getNumerator() != null) {
			for (Float f : c.getNumerator()) {
				edu.iris.dmc.seed.control.station.Number n = new edu.iris.dmc.seed.control.station.Number(f.getValue(),
						f.getPlusError());
				b.addNumerator(n);
			}
		}
		if (c.getDenominator() != null) {
			for (Float f : c.getDenominator()) {
				edu.iris.dmc.seed.control.station.Number d = new edu.iris.dmc.seed.control.station.Number(f.getValue(),
						f.getPlusError());
				b.addDenominator(d);
			}
		}

		return b;

	}
}
