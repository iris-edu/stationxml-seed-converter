package edu.iris.dmc.station.mapper;

import java.math.BigInteger;

import edu.iris.dmc.fdsn.station.model.FloatNoUnitType;
import edu.iris.dmc.fdsn.station.model.Frequency;
import edu.iris.dmc.fdsn.station.model.PoleZero;
import edu.iris.dmc.fdsn.station.model.PolesZeros;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.control.dictionary.B043;
import edu.iris.dmc.seed.control.station.B053;
import edu.iris.dmc.seed.control.station.Pole;
import edu.iris.dmc.seed.control.station.Zero;

public class PolesZerosMapper extends AbstractMapper {

	public static PolesZeros map(B043 b) throws SeedException {
		PolesZeros pzs = factory.createPolesZerosType();
		String transferFunction = null;
		switch (b.getTransferFunctionType()) {
		case 'A':
			transferFunction = "LAPLACE (RADIANS/SECOND)";
			break;
		case 'B':
			transferFunction = "LAPLACE (HERTZ)";
			break;
		case 'D':
			transferFunction = "DIGITAL (Z-TRANSFORM)";
			break;
		}
		if (transferFunction == null) {
			throw new SeedException("Invalid blockette 053 transfer function: " + b.getTransferFunctionType());
		}
		pzs.setPzTransferFunctionType(transferFunction);
		pzs.setNormalizationFactor(b.getNormalizationFactor());

		Frequency frequency = factory.createFrequencyType();
		frequency.setValue(b.getNormalizationFrequency());
		pzs.setNormalizationFrequency(frequency);

		int counter = 0;

		if (b.getZeros() != null) {
			for (Zero zero : b.getZeros()) {
				PoleZero z = factory.createPoleZeroType();
				FloatNoUnitType fnt = factory.createFloatNoUnitType();
				fnt.setValue(zero.getReal().getValue());
				z.setReal(fnt);

				fnt = factory.createFloatNoUnitType();
				fnt.setValue(zero.getImaginary().getValue());
				z.setImaginary(fnt);

				z.setNumber(BigInteger.valueOf(counter++));
				pzs.getZero().add(z);
			}
		}
		counter = 0;
		if (b.getPoles() != null) {
			for (Pole pole : b.getPoles()) {
				PoleZero p = factory.createPoleZeroType();
				FloatNoUnitType fnt = factory.createFloatNoUnitType();
				fnt.setValue(pole.getReal().getValue());
				p.setReal(fnt);

				fnt = factory.createFloatNoUnitType();
				fnt.setValue(pole.getImaginary().getValue());
				p.setImaginary(fnt);
				p.setNumber(BigInteger.valueOf(counter++));
				pzs.getPole().add(p);
			}

		}

		return pzs;
	}

	public static PolesZeros map(B053 b) throws SeedException {
		PolesZeros pzs = factory.createPolesZerosType();
		String transferFunction = null;
		switch (b.getTransferFunctionType()) {
		case 'A':
			transferFunction = "LAPLACE (RADIANS/SECOND)";
			break;
		case 'B':
			transferFunction = "LAPLACE (HERTZ)";
			break;
		case 'D':
			transferFunction = "DIGITAL (Z-TRANSFORM)";
			break;
		}
		if (transferFunction == null) {
			throw new SeedException("Invalid blockette 053 transfer function: " + b.getTransferFunctionType());
		}
		pzs.setPzTransferFunctionType(transferFunction);
		pzs.setNormalizationFactor(b.getNormalizationFactor());

		Frequency frequency = factory.createFrequencyType();
		frequency.setValue(b.getNormalizationFrequency());
		pzs.setNormalizationFrequency(frequency);

		int counter = 0;

		if (b.getZeros() != null) {
			for (Zero zero : b.getZeros()) {
				PoleZero z = factory.createPoleZeroType();
				FloatNoUnitType fnt = createFloatNoUnitType(zero.getReal());
				z.setReal(fnt);

				fnt = createFloatNoUnitType(zero.getImaginary());
				z.setImaginary(fnt);

				z.setNumber(BigInteger.valueOf(counter++));
				pzs.getZero().add(z);
			}
		}

		counter = 0;
		if (b.getPoles() != null) {
			for (Pole pole : b.getPoles()) {
				PoleZero p = factory.createPoleZeroType();
				FloatNoUnitType fnt = createFloatNoUnitType(pole.getReal());
				p.setReal(fnt);

				fnt = createFloatNoUnitType(pole.getImaginary());
				p.setImaginary(fnt);

				p.setNumber(BigInteger.valueOf(counter++));
				pzs.getPole().add(p);
			}

		}

		return pzs;
	}

	public static B053 map(PolesZeros pzs) throws SeedException {

		B053 b = new B053();
		char transferFunction = 'N';
		switch (pzs.getPzTransferFunctionType()) {
		case "LAPLACE (RADIANS/SECOND)":
			transferFunction = 'A';
			break;
		case "LAPLACE (HERTZ)":
			transferFunction = 'B';
			break;
		case "DIGITAL (Z-TRANSFORM)":
			transferFunction = 'D';
			break;
		}
		if (transferFunction == 'N') {
			throw new SeedException("Invalid blockette 053 transfer function: " + pzs.getPzTransferFunctionType());
		}

		b.setTransferFunctionType(transferFunction);
		b.setNormalizationFactor(pzs.getNormalizationFactor());
		if (pzs.getNormalizationFrequency() != null) {
			b.setNormalizationFrequency(pzs.getNormalizationFrequency().getValue());
		}

		if (pzs.getZero() != null) {
			for (PoleZero zero : pzs.getZero()) {

				edu.iris.dmc.seed.control.station.Number real = createNumber(zero.getReal());

				edu.iris.dmc.seed.control.station.Number imaginary = createNumber(zero.getImaginary());
				Zero z = new Zero(real, imaginary);
				b.add(z);
			}
		}

		if (pzs.getPole() != null) {
			for (PoleZero pole : pzs.getPole()) {
				edu.iris.dmc.seed.control.station.Number real = createNumber(pole.getReal());

				edu.iris.dmc.seed.control.station.Number imaginary = createNumber(pole.getImaginary());
				Pole p = new Pole(real, imaginary);
				b.add(p);
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

	private static FloatNoUnitType createFloatNoUnitType(edu.iris.dmc.seed.control.station.Number number) {
		if (number == null) {
			return null;
		}
		FloatNoUnitType fnt = new FloatNoUnitType();
		fnt.setValue(number.getValue());
		fnt.setMinusError(number.getError());
		fnt.setPlusError(number.getError());
		return fnt;
	}
}
