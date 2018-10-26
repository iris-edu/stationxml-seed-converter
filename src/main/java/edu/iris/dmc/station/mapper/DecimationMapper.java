package edu.iris.dmc.station.mapper;

import java.math.BigInteger;

import edu.iris.dmc.fdsn.station.model.Decimation;
import edu.iris.dmc.fdsn.station.model.Float;
import edu.iris.dmc.fdsn.station.model.Frequency;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.control.dictionary.B047;
import edu.iris.dmc.seed.control.station.B057;

public class DecimationMapper extends AbstractMapper {

	public static Decimation map(B047 b) {
		Decimation decimation = factory.createDecimationType();

		Float ft = factory.createFloatType();
		ft.setValue(b.getCorrection());
		decimation.setCorrection(ft);

		ft = factory.createFloatType();
		ft.setValue(b.getEstimatedDelay());
		decimation.setDelay(ft);

		decimation.setOffset(BigInteger.valueOf(b.getDecimationOffset()));

		decimation.setFactor(BigInteger.valueOf(b.getDecimationFactor()));

		Frequency fType = factory.createFrequencyType();
		fType.setValue(b.getSampleRate());
		decimation.setInputSampleRate(fType);

		return decimation;
	}
	public static Decimation map(B057 b) {
		Decimation decimation = factory.createDecimationType();

		Float ft = factory.createFloatType();
		ft.setValue(b.getCorrection());
		decimation.setCorrection(ft);

		ft = factory.createFloatType();
		ft.setValue(b.getEstimatedDelay());
		decimation.setDelay(ft);

		decimation.setOffset(BigInteger.valueOf(b.getDecimationOffset()));

		decimation.setFactor(BigInteger.valueOf(b.getDecimationFactor()));

		Frequency fType = factory.createFrequencyType();
		fType.setValue(b.getSampleRate());
		decimation.setInputSampleRate(fType);

		return decimation;
	}
	

	public static B057 map(Decimation d) throws SeedException {

		B057 b = new B057();

		if (d.getCorrection() != null) {
			b.setCorrection(d.getCorrection().getValue());
		}

		if (d.getFactor() != null) {
			b.setDecimationFactor(d.getFactor().intValue());
		}
		if (d.getOffset() != null) {
			b.setDecimationOffset(d.getOffset().intValue());
		}
		if (d.getDelay() != null) {
			b.setEstimatedDelay(d.getDelay().getValue());
		}
		if (d.getInputSampleRate() != null) {
			b.setSampleRate(d.getInputSampleRate().getValue());
		}

		return b;
	}
}
