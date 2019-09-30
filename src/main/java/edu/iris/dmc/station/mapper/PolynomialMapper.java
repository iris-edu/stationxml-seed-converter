package edu.iris.dmc.station.mapper;

import java.math.BigInteger;

import edu.iris.dmc.fdsn.station.model.FloatNoUnitType;
import edu.iris.dmc.fdsn.station.model.Frequency;
import edu.iris.dmc.fdsn.station.model.Polynomial;
import edu.iris.dmc.fdsn.station.model.Polynomial.Coefficient;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.control.dictionary.B042;
import edu.iris.dmc.seed.control.dictionary.B049;
import edu.iris.dmc.seed.control.station.B062;

public class PolynomialMapper extends AbstractMapper {

	public static Polynomial map(B042 b) {

		Polynomial pType = factory.createPolynomialType();

		if ('M' == b.getApproximationType()) {
			pType.setApproximationType("MACLAURIN");
		}

		if (b.getLowerBoundOfApproximation() != null) {
			pType.setApproximationLowerBound(b.getLowerBoundOfApproximation());
		}

		if (b.getUpperBoundOfApproximation() != null) {
			pType.setApproximationUpperBound(b.getUpperBoundOfApproximation());
		}

		if (b.getLowerValidFrequencyBound() != null) {
			Frequency frequency = new Frequency();
			frequency.setValue(b.getLowerValidFrequencyBound());
			pType.setFrequencyLowerBound(frequency);
		}

		if (b.getUpperValidFrequencyBound() != null) {
			Frequency frequency = new Frequency();
			frequency.setValue(b.getUpperValidFrequencyBound());
			pType.setFrequencyUpperBound(frequency);
		}

		if (b.getMaximumAbsoluteError() != null) {
			pType.setMaximumError(b.getMaximumAbsoluteError());
		}

		if (b.getCoefficients() != null) {
			int index = 0;
			for (edu.iris.dmc.seed.control.station.Number n : b.getCoefficients()) {
				Coefficient co = factory.createPolynomialTypeCoefficient();
				co.setNumber(BigInteger.valueOf(index++));

				co.setMinusError(n.getError());
				co.setPlusError(n.getError());
				co.setValue(n.getValue());
				pType.getCoefficient().add(co);
			}
		}
		return pType;
	}

	public static Polynomial map(B049 b) {
		Polynomial pType = factory.createPolynomialType();

		if ('M' == b.getApproximationType()) {
			pType.setApproximationType("MACLAURIN");
		}

		if (b.getLowerBoundOfApproximation() != null) {
			pType.setApproximationLowerBound(b.getLowerBoundOfApproximation());
		}

		if (b.getUpperBoundOfApproximation() != null) {
			pType.setApproximationUpperBound(b.getUpperBoundOfApproximation());
		}

		if (b.getLowerValidFrequencyBound() != null) {
			Frequency frequency = new Frequency();
			frequency.setValue(b.getLowerValidFrequencyBound());
			pType.setFrequencyLowerBound(frequency);
		}

		if (b.getUpperValidFrequencyBound() != null) {
			Frequency frequency = new Frequency();
			frequency.setValue(b.getUpperValidFrequencyBound());
			pType.setFrequencyUpperBound(frequency);
		}

		if (b.getMaximumAbsoluteError() != null) {
			pType.setMaximumError(b.getMaximumAbsoluteError());
		}

		if (b.getCoefficients() != null) {
			int index = 0;
			for (edu.iris.dmc.seed.control.station.Number n : b.getCoefficients()) {
				Coefficient co = factory.createPolynomialTypeCoefficient();
				co.setNumber(BigInteger.valueOf(index++));
				co.setMinusError(n.getError());
				co.setPlusError(n.getError());
				co.setValue(n.getValue());
				pType.getCoefficient().add(co);
			}
		}
		return pType;
	}

	public static Polynomial map(B062 b) {
		if (b.getFrequencyUnit() != 'B') {

		}
		Polynomial pType = factory.createPolynomialType();

		if ('M' == (b.getApproximationType())) {
			pType.setApproximationType("MACLAURIN");
		}

		pType.setApproximationLowerBound(b.getLowerBoundOfApproximation());

		pType.setApproximationUpperBound(b.getUpperBoundOfApproximation());

		Frequency ft = factory.createFrequencyType();
		ft.setValue(b.getLowerValidFrequencyBound());
		pType.setFrequencyLowerBound(ft);

		ft = factory.createFrequencyType();
		ft.setValue(b.getUpperValidFrequencyBound());
		pType.setFrequencyUpperBound(ft);

		pType.setMaximumError(b.getMaximumAbsoluteError());

		int index = 0;
		for (edu.iris.dmc.seed.control.station.Number n : b.getCoefficients()) {
			n.getValue();
			n.getError();
			Coefficient co = factory.createPolynomialTypeCoefficient();
			co.setNumber(BigInteger.valueOf(index));

			co.setMinusError(n.getError());
			co.setPlusError(n.getError());
			co.setValue(n.getValue());
			pType.getCoefficient().add(co);
			co.setNumber(BigInteger.valueOf(index++));
		}
		return pType;
	}

	public static B062 map(Polynomial p) throws SeedException {
		B062 b = new B062();

		if ("MACLAURIN".equals(p.getApproximationType())) {
			b.setApproximationType('M');
		}

		b.setFrequencyUnit('B');

		b.setLowerBoundOfApproximation(p.getApproximationLowerBound());

		b.setUpperBoundOfApproximation(p.getApproximationUpperBound());

		if (p.getFrequencyLowerBound() != null) {
			b.setLowerValidFrequencyBound(p.getFrequencyLowerBound().getValue());
		}
		if (p.getFrequencyUpperBound() != null) {
			b.setUpperValidFrequencyBound(p.getFrequencyUpperBound().getValue());
		}

		b.setMaximumAbsoluteError(p.getMaximumError());

		if (p.getCoefficient() != null) {
			for (Coefficient c : p.getCoefficient()) {
				edu.iris.dmc.seed.control.station.Number n = createNumber(c);
				b.add(n);
			}
		}
		return b;

	}
	
	private static edu.iris.dmc.seed.control.station.Number createNumber(FloatNoUnitType fnt) {
		if (fnt == null) {
			return null;
		}

		Double minus = fnt.getMinusError();
		Double plus = fnt.getPlusError();
		double error = 0d;
		if (minus == null) {
			if (plus != null) {
				error = plus;
			}
		} else {
			if (plus == null) {
				error = minus;
			} else {
				minus = Math.abs(fnt.getMinusError());
				plus = Math.abs(fnt.getPlusError());
				error = (plus > minus) ? plus : minus;
			}
		}

		return new edu.iris.dmc.seed.control.station.Number(fnt.getValue(), error);
	}
}
