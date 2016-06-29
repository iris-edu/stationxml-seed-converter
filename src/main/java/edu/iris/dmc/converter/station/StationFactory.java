package edu.iris.dmc.converter.station;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import edu.iris.Fissures.seed.container.Blockette;
import edu.iris.Fissures.seed.container.Btime;
import edu.iris.Fissures.seed.container.SeedObjectContainer;
import edu.iris.dmc.converter.seed.SeedUtil;
import edu.iris.dmc.fdsn.station.model.AngleType;
import edu.iris.dmc.fdsn.station.model.Azimuth;
import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Coefficients;
import edu.iris.dmc.fdsn.station.model.Comment;
import edu.iris.dmc.fdsn.station.model.Decimation;
import edu.iris.dmc.fdsn.station.model.Dip;
import edu.iris.dmc.fdsn.station.model.Distance;
import edu.iris.dmc.fdsn.station.model.Equipment;
import edu.iris.dmc.fdsn.station.model.FIR;
import edu.iris.dmc.fdsn.station.model.Filter;
import edu.iris.dmc.fdsn.station.model.Float;
import edu.iris.dmc.fdsn.station.model.FloatNoUnitType;
import edu.iris.dmc.fdsn.station.model.Frequency;
import edu.iris.dmc.fdsn.station.model.Gain;
import edu.iris.dmc.fdsn.station.model.Latitude;
import edu.iris.dmc.fdsn.station.model.Longitude;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.ObjectFactory;
import edu.iris.dmc.fdsn.station.model.PoleZero;
import edu.iris.dmc.fdsn.station.model.PolesZeros;
import edu.iris.dmc.fdsn.station.model.Polynomial;
import edu.iris.dmc.fdsn.station.model.Response;
import edu.iris.dmc.fdsn.station.model.ResponseList;
import edu.iris.dmc.fdsn.station.model.ResponseListElement;
import edu.iris.dmc.fdsn.station.model.ResponseStage;
import edu.iris.dmc.fdsn.station.model.SampleRate;
import edu.iris.dmc.fdsn.station.model.Sensitivity;
import edu.iris.dmc.fdsn.station.model.Site;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.fdsn.station.model.Units;
import edu.iris.dmc.fdsn.station.model.Channel.ClockDrift;
import edu.iris.dmc.fdsn.station.model.FIR.NumeratorCoefficient;
import edu.iris.dmc.fdsn.station.model.Polynomial.Coefficient;

public class StationFactory {
	private static final Logger LOGGER = Logger.getLogger(StationFactory.class.getName());
	private static final StationFactory instance = new StationFactory();
	private static ObjectFactory factory = new ObjectFactory();

	private StationFactory() {
	}

	public static StationFactory getInstance() {
		return instance;
	}

	public Collection<Network> buildNetwork(SeedObjectContainer container, boolean doResponse) throws Exception {
		Map<String, Network> map = new HashMap<String, Network>();
		if (container == null) {
			return map.values();
		}

		// Blockette bb=(Blockette) container.get(11000001);
		while (true) {
			Object object = container.getNext();
			if (object == null) {
				break;
			}

			Blockette blockette = null;
			if (object instanceof Blockette) {
				blockette = (Blockette) object;
				int type = blockette.getType();

				if (type == 50) {
					String netCode = (String) blockette.getFieldVal(16);
					if (netCode == null) {
						throw new Exception("Invalid blockette 050:Field=16 <network identifier>");
					}
					Blockette b = container.getDictionaryBlockette(blockette, 10);

					String description = "";
					if (b != null) {
						description = (String) b.getFieldVal(4);
					}
					Network network = map.get(netCode);
					if (network == null) {
						network = new Network();
						network.setCode(netCode);
						network.setDescription(description);
						map.put(netCode, network);
					}
					Station station = buildStation(container, blockette, doResponse);
					if (station != null) {
						LOGGER.finest("\n\nProcessing station:" + netCode + "/" + station.getCode());
						if (network.getStations() == null) {
							network.addStation(station);
						} else {
							network.merge(station);
						}
					}
				} else if (type == 59) {
					LOGGER.finest(blockette.toBlkFldString());
				}
			}
		}
		return map.values();
	}

	public Station buildStation(SeedObjectContainer container, Blockette stationBlockette, boolean doResponse)
			throws Exception {
		String code = (String) stationBlockette.getFieldVal(3);
		Station station = new Station();
		station.setCode(code);

		SimpleDateFormat fmt = new SimpleDateFormat("YYYY,D,H:m:ss.SSSS");
		Btime d = (Btime) stationBlockette.getFieldVal(13);
		if (d != null && d.getStringTime() != null) {
			Date dt = fmt.parse(d.getStringTime());
			station.setCreationDate(SeedUtil.dateTimeToXMLGregorianCalendar(dt));
			station.setStartDate(SeedUtil.dateTimeToXMLGregorianCalendar(dt));
		}

		d = (Btime) stationBlockette.getFieldVal(14);
		if (d != null && d.getStringTime() != null) {
			Date dt = fmt.parse(d.getStringTime());
			station.setEndDate(SeedUtil.dateTimeToXMLGregorianCalendar(dt));
		}

		Latitude latitude = factory.createLatitudeType();
		latitude.setValue((Double) stationBlockette.getFieldVal(4));
		station.setLatitude(latitude);

		Longitude longitude = factory.createLongitudeType();
		longitude.setValue((Double) stationBlockette.getFieldVal(5));
		station.setLongitude(longitude);

		Distance elevation = factory.createDistanceType();
		elevation.setValue((Double) stationBlockette.getFieldVal(6));
		station.setElevation(elevation);

		Site site = factory.createSiteType();
		site.setName((String) stationBlockette.getFieldVal(9));
		station.setSite(site);

		int numberofChildBlockettes = stationBlockette.numberofChildBlockettes();

		for (int i = 0; i < numberofChildBlockettes; i++) {
			Blockette blockette = stationBlockette.getChildBlockette(i);
			int type = blockette.getType();
			if (52 == type) {
				Channel channel = buildChannel(container, blockette, doResponse);
				LOGGER.finest("Processing channel: " + channel.getCode() + "/" + channel.getLocationCode() + " ["
						+ (i + 1) + "/" + numberofChildBlockettes + "]");
				station.addChannel(channel);
			} else if (type == 51) {
				Comment comment = factory.createCommentType();
				station.add(comment);
				Btime effectiveBeginTime = (Btime) blockette.getFieldVal(3);
				if (effectiveBeginTime != null) {
					Date dt = fmt.parse(effectiveBeginTime.getStringTime());
					comment.setBeginEffectiveTime(SeedUtil.dateTimeToXMLGregorianCalendar(dt));
				}

				Btime effectiveEndTime = (Btime) blockette.getFieldVal(4);
				if (effectiveEndTime != null) {
					Date dt = fmt.parse(effectiveEndTime.getStringTime());
					comment.setEndEffectiveTime(SeedUtil.dateTimeToXMLGregorianCalendar(dt));
				}

				Blockette commentText = container.getDictionaryBlockette(blockette, 5);
				String text = (String) commentText.getFieldVal(5);
				comment.setValue(text);
				continue;
			}
		}

		return station;
	}

	public Channel buildChannel(SeedObjectContainer container, Blockette channelBlockette, boolean doResponse)
			throws Exception {
		String location = channelBlockette.getFieldVal(3).toString();
		String chanCode = channelBlockette.getFieldVal(4).toString();

		Channel channel = factory.createChannelType();

		channel.setCode(chanCode);
		channel.setLocationCode(location.trim());
		SimpleDateFormat fmt = new SimpleDateFormat("YYYY,D,H:m:ss.SSSS");

		Btime bTime = (Btime) channelBlockette.getFieldVal(22);
		if (bTime != null) {
			Date dt = fmt.parse(bTime.getStringTime());
			channel.setStartDate(SeedUtil.dateTimeToXMLGregorianCalendar(dt));
		}

		bTime = (Btime) channelBlockette.getFieldVal(23);
		if (bTime != null) {
			Date dt = fmt.parse(bTime.getStringTime());
			channel.setEndDate(SeedUtil.dateTimeToXMLGregorianCalendar(dt));
		}

		Blockette calibUnits = container.getDictionaryBlockette(channelBlockette, 9);

		if (calibUnits != null) {
			Units u = factory.createUnitsType();
			u.setName((String) calibUnits.getFieldVal(4));
			u.setDescription((String) calibUnits.getFieldVal(5));
			channel.setCalibrationUnits(u);
		}

		Latitude latitude = factory.createLatitudeType();
		latitude.setValue((Double) channelBlockette.getFieldVal(10));
		channel.setLatitude(latitude);

		Longitude longitude = factory.createLongitudeType();
		longitude.setValue((Double) channelBlockette.getFieldVal(11));
		channel.setLongitude(longitude);

		Distance elevation = factory.createDistanceType();
		elevation.setValue((Double) channelBlockette.getFieldVal(12));
		channel.setElevation(elevation);

		Distance depth = factory.createDistanceType();
		depth.setValue((Double) channelBlockette.getFieldVal(13));
		channel.setDepth(depth);

		Dip dip = factory.createDipType();
		double dipValue = (Double) channelBlockette.getFieldVal(15);
		dip.setValue(dipValue);
		channel.setDip(dip);

		Double azimuthValue = (Double) channelBlockette.getFieldVal(14);

		if (azimuthValue != null) {
			Azimuth azimuth = factory.createAzimuthType();
			if (dip != null && (dip.getValue() == 90 || dip.getValue() == -90)) {
				if (BigDecimal.ZERO.compareTo(BigDecimal.valueOf(azimuthValue)) != 0) {
					if (azimuthValue.intValue() >= 360) {
						azimuth.setValue(0);
					} else {
						azimuth.setValue(azimuthValue);
					}
				}
			} else {
				azimuth.setValue(azimuthValue);
			}
			channel.setAzimuth(azimuth);
		}

		SampleRate sampleRate = factory.createSampleRateType();
		sampleRate.setValue((Double) channelBlockette.getFieldVal(18));
		channel.setSampleRate(sampleRate);

		ClockDrift clockDrift = factory.createChannelTypeClockDrift();
		clockDrift.setValue((Double) channelBlockette.getFieldVal(19));
		channel.setClockDrift(clockDrift);

		String cType = (String) channelBlockette.getFieldVal(21);
		if (cType != null) {
			for (int i = 0; i < cType.length(); i++) {
				char kar = cType.charAt(i);
				if ('C' == kar) {
					channel.addType("CONTINUOUS");
				} else if ('G' == kar) {
					channel.addType("GEOPHYSICAL");
				} else if ('T' == kar) {
					channel.addType("TRIGGERED");
				} else if ('H' == kar) {
					channel.addType("HEALTH");
				} else if ('W' == kar) {
					channel.addType("WEATHER");
				} else if ('F' == kar) {
					channel.addType("FLAG");
				} else if ('S' == kar) {
					channel.addType("SYNTHESIZED");
				} else if ('I' == kar) {
					channel.addType("INPUT");
				} else if ('E' == kar) {
					channel.addType("EXPERIMENTAL");
				} else if ('M' == kar) {
					channel.addType("MAINTENANCE");
				} else if ('B' == kar) {
					channel.addType("BEAM");
				} else {

				}
			}
		}

		// Blockette commentBlokette =
		// container.getDictionaryBlockette(channelBlockette, 20);

		Blockette instrumentB = container.getDictionaryBlockette(channelBlockette, 6);

		if (instrumentB != null) {
			String s = (String) instrumentB.getFieldVal(4);

			if (s != null) {
				Equipment equipment = factory.createEquipmentType();
				equipment.setDescription(s);
				channel.setSensor(equipment);
			}
		}

		Sensitivity instrumentSensitivity = null;
		Response response = factory.createResponseType();
		Units responseInputUnits = null;
		channel.setResponse(response);

		int count = channelBlockette.numberofChildBlockettes();

		int currentStage = -1;
		ResponseStage responseStage = null;
		Units inputUnits = null;
		Units outputUnits = null;
		Filter filter = null;
		for (int index = 0; index < count; index++) {
			Decimation decimation = null;
			Gain gain = null;
			Blockette blockette = channelBlockette.getChildBlockette(index);
			int type = blockette.getType();

			int stage = -1;
			if (type == 59) {
				Comment comment = factory.createCommentType();

				Btime effectiveBeginTime = (Btime) blockette.getFieldVal(3);
				if (effectiveBeginTime != null) {
					Date dt = fmt.parse(effectiveBeginTime.getStringTime());
					comment.setBeginEffectiveTime(SeedUtil.dateTimeToXMLGregorianCalendar(dt));
				}

				Btime effectiveEndTime = (Btime) blockette.getFieldVal(4);
				if (effectiveEndTime != null) {
					Date dt = fmt.parse(effectiveEndTime.getStringTime());
					comment.setEndEffectiveTime(SeedUtil.dateTimeToXMLGregorianCalendar(dt));
				}

				Blockette commentText = container.getDictionaryBlockette(blockette, 5);
				if (commentText != null) {
					String text = (String) commentText.getFieldVal(5);
					comment.setValue(text);
					channel.add(comment);
				}
				continue;
			}
			if (type == 53 || type == 54 || type == 62 || type == 60) {
				stage = (Integer) blockette.getFieldVal(4);
			} else {
				stage = (Integer) blockette.getFieldVal(3);
			}

			if (stage == 0 && (type == 58 || type == 62)) {
				if (type == 58) {
					instrumentSensitivity = factory.createSensitivityType();
					response.setInstrumentSensitivity(instrumentSensitivity);
					Double sensGain = (Double) blockette.getFieldVal(4);
					response.getInstrumentSensitivity().setValue(sensGain);
					Double frequency = (Double) blockette.getFieldVal(5);
					response.getInstrumentSensitivity().setFrequency(frequency);
				} else {
					Polynomial polynomial = (Polynomial) do62(container, blockette);
					response.setInstrumentPolynomial(polynomial);
				}
				continue;
			}

			if (currentStage != stage) {
				currentStage = stage;
				responseStage = factory.createResponseStageType();
				responseStage.setNumber(BigInteger.valueOf(stage));
				response.getStage().add(responseStage);

				LOGGER.finest("\nProcessing stage " + currentStage);
			}

			switch (type) {
			case 53:
				filter = do53(container, blockette);
				outputUnits = filter.getOutputUnits();
				break;
			case 54:
				filter = do54(container, blockette);
				outputUnits = filter.getOutputUnits();
				break;
			case 55:
				filter = do55(container, blockette);
				outputUnits = filter.getOutputUnits();
				break;
			case 57:
				decimation = do57(container, blockette);
				filter = null;
				break;
			case 58:
				gain = do58(container, blockette);
				filter = null;
				break;
			case 60:
				filter = null;
				Filter f = null;
				int numberOfStages = (Integer) blockette.getFieldVal(3);

				for (int i = 0; i < numberOfStages; i++) {
					int stageSequence = (Integer) blockette.getFieldVal(4);
					if (currentStage != stageSequence) {
						currentStage = stageSequence;
						responseStage = factory.createResponseStageType();
						responseStage.setNumber(BigInteger.valueOf(stageSequence));
						response.getStage().add(responseStage);
					}

					int numberOfResponses = (Integer) blockette.getFieldVal(5);
					for (int x = 0; x < numberOfResponses; x++) {
						Vector vec = (Vector) blockette.getFieldVal(6);

						for (int z = 0; z < vec.size(); z++) {
							Blockette dBlockette = container.getDictionaryBlockette(blockette, 6, x, z);
							if (dBlockette == null) {
								// TODO: not sure what to do here. Needs
								// attention
								continue;
							}
							int dType = dBlockette.getType();

							switch (dType) {
							case 41:
								f = do61(container, dBlockette);
								break;
							case 42:
								f = do62(container, dBlockette);
								break;
							case 43:
								f = do53(container, dBlockette);
								break;
							case 44:
								f = do54(container, dBlockette);
								break;
							case 45:
								f = do55(container, dBlockette);
								break;
							case 46:
								// no 56 found yet::::f = do56(container,
								// dBlockette, debug, ps);
								break;
							case 47:
								decimation = do57(container, blockette);
								filter = null;
								break;
							case 48:
								gain = do58(container, blockette);
								filter = null;
								break;
							case 49:
								f = do62(container, dBlockette);
								break;
							default:
								break;
							}

							if (f != null) {
								responseStage.add(f);
							}

							if (decimation != null) {
								responseStage.setDecimation(decimation);
							}

							if (gain != null) {
								responseStage.setStageGain(gain);
							}
						}
					}
				}
				break;
			case 61:
				filter = do61(container, blockette);

				outputUnits = filter.getOutputUnits();
				break;
			case 62:
				filter = do62(container, blockette);
				outputUnits = filter.getOutputUnits();
				break;
			default:
				break;
			}
			if (filter != null) {
				responseStage.add(filter);
			}

			if (filter != null && stage == 1) {
				LOGGER.finest("Setting inputunits to: " + filter.getInputUnits().getName() + "  "
						+ filter.getInputUnits().getDescription());
				responseInputUnits = filter.getInputUnits();
			}

			if (decimation != null) {
				responseStage.setDecimation(decimation);
			}

			if (gain != null) {
				responseStage.setStageGain(gain);
			}
		}

		if (instrumentSensitivity != null) {
			instrumentSensitivity.setOutputUnits(outputUnits);
			instrumentSensitivity.setInputUnits(responseInputUnits);
		}
		return channel;
	}

	public Filter do53(SeedObjectContainer container, Blockette blockette) throws Exception {
		LOGGER.finest("\t B53 ");

		String temp = (String) blockette.getFieldVal(3);
		String transferFunction = null;
		if ("A".equals(temp)) {
			transferFunction = "LAPLACE (RADIANS/SECOND)";
		} else if ("B".equals(temp)) {
			transferFunction = "LAPLACE (HERTZ)";
		} else if ("D".equals(temp)) {
			transferFunction = "DIGITAL (Z-TRANSFORM)";
		} else {
			throw new Exception("Invalid blockette 053 transfer function: " + temp);
		}
		int numberOfComplexZeros = (Integer) blockette.getFieldVal(9);
		int numberOfComplexPoles = (Integer) blockette.getFieldVal(14);
		PolesZeros pzs = factory.createPolesZerosType();

		pzs.setPzTransferFunctionType(transferFunction);

		int lookupId = (Integer) blockette.getFieldVal(5);
		// Blockette input = (Blockette)
		// container.get(lookupId);
		Blockette input = container.getDictionaryBlockette(blockette, 5);
		if (input != null) {
			Units ut = factory.createUnitsType();
			ut.setDescription((String) input.getFieldVal(5));
			ut.setName((String) input.getFieldVal(4));
			pzs.setInputUnits(ut);
		}

		lookupId = (Integer) blockette.getFieldVal(6);
		Blockette output = container.getDictionaryBlockette(blockette, 6);
		if (output != null) {
			Units ut = factory.createUnitsType();
			ut.setDescription((String) output.getFieldVal(5));
			ut.setName((String) output.getFieldVal(4));
			pzs.setOutputUnits(ut);
		}

		double normalizationFactor = (Double) blockette.getFieldVal(7);
		pzs.setNormalizationFactor(normalizationFactor);
		double normalizationFreq = (Double) blockette.getFieldVal(8);
		Frequency frequency = factory.createFrequencyType();
		frequency.setValue(normalizationFreq);
		pzs.setNormalizationFrequency(frequency);

		long counter = 0;

		if (numberOfComplexZeros > 0) {
			for (int z = 0; z < numberOfComplexZeros; z++) {
				PoleZero pz = factory.createPoleZeroType();
				pz.setNumber(BigInteger.valueOf(counter));
				double real = (Double) blockette.getFieldVal(10, z);
				FloatNoUnitType fnutReal = factory.createFloatNoUnitType();
				fnutReal.setValue(real);
				double error = (Double) blockette.getFieldVal(12, z);
				fnutReal.setMinusError(error);
				fnutReal.setPlusError(error);
				pz.setReal(fnutReal);

				double imag = (Double) blockette.getFieldVal(11, z);
				FloatNoUnitType fnutImag = factory.createFloatNoUnitType();
				fnutImag.setValue(imag);
				error = (Double) blockette.getFieldVal(13, z);
				fnutImag.setMinusError(error);
				fnutImag.setPlusError(error);
				pz.setImaginary(fnutImag);
				pzs.getZero().add(pz);
				counter++;
			}
		}
		counter = 0;
		if (numberOfComplexPoles > 0) {
			for (int z = 0; z < numberOfComplexPoles; z++) {
				PoleZero pz = factory.createPoleZeroType();
				pz.setNumber(BigInteger.valueOf(counter));
				double real = (Double) blockette.getFieldVal(15, z);
				FloatNoUnitType fnutReal = factory.createFloatNoUnitType();
				fnutReal.setValue(real);
				double error = (Double) blockette.getFieldVal(17, z);
				fnutReal.setMinusError(error);
				fnutReal.setPlusError(error);
				pz.setReal(fnutReal);

				double imag = (Double) blockette.getFieldVal(16, z);
				FloatNoUnitType fnutImag = factory.createFloatNoUnitType();
				fnutImag.setValue(imag);
				error = (Double) blockette.getFieldVal(18, z);
				fnutImag.setMinusError(error);
				fnutImag.setPlusError(error);
				pz.setImaginary(fnutImag);
				pzs.getPole().add(pz);
				counter++;
			}
		}

		return pzs;
	}

	public Filter do54(SeedObjectContainer container, Blockette blockette) throws Exception {
		LOGGER.finest("B54 ");

		Coefficients coefficients = factory.createCoefficientsType();

		Blockette input = container.getDictionaryBlockette(blockette, 5);
		if (input != null) {
			Units ut = factory.createUnitsType();
			ut.setDescription((String) input.getFieldVal(5));
			ut.setName((String) input.getFieldVal(4));
			coefficients.setInputUnits(ut);
		}

		Blockette output = container.getDictionaryBlockette(blockette, 6);
		if (output != null) {
			Units ut = factory.createUnitsType();
			ut.setDescription((String) output.getFieldVal(5));
			ut.setName((String) output.getFieldVal(4));
			coefficients.setOutputUnits(ut);
		}

		String temp = (String) blockette.getFieldVal(3);
		String transferFunction = "";
		if ("A".equals(temp)) {
			transferFunction = "ANALOG (RADIANS/SECOND)";
		} else if ("B".equals(temp)) {
			transferFunction = "ANALOG (HERTZ)";
		} else if ("D".equals(temp)) {
			transferFunction = "DIGITAL";
		}

		coefficients.setCfTransferFunctionType(transferFunction);

		Integer numberOfNumerators = (Integer) blockette.getFieldVal(7);
		if (numberOfNumerators > 0) {
			for (int z = 0; z < numberOfNumerators; z++) {
				// blockette.getFieldVal(9, z);

				Double numerator = (Double) blockette.getFieldVal(8, z);

				Float ft = factory.createFloatType();
				ft.setValue(numerator);
				double error = (Double) blockette.getFieldVal(9, z);
				ft.setMinusError(error);
				ft.setPlusError(error);
				coefficients.getNumerator().add(ft);

			}
		}
		Integer numberOfDenominators = (Integer) blockette.getFieldVal(10);
		if (numberOfDenominators > 0) {
			for (int z = 0; z < numberOfDenominators; z++) {
				Double denominator = (Double) blockette.getFieldVal(11, z);
				blockette.getFieldVal(12, z);

				Float ft = factory.createFloatType();
				ft.setValue(denominator);
				double error = (Double) blockette.getFieldVal(12, z);
				ft.setMinusError(error);
				ft.setPlusError(error);
				coefficients.getDenominator().add(ft);
			}
		}
		return coefficients;
	}

	public Filter do55(SeedObjectContainer container, Blockette blockette) throws Exception {
		LOGGER.finest(" B55 ");
		ResponseList rType = factory.createResponseListType();

		Blockette input = container.getDictionaryBlockette(blockette, 4);
		if (input != null) {
			Units ut = factory.createUnitsType();
			ut.setDescription((String) input.getFieldVal(5));
			ut.setName((String) input.getFieldVal(4));
			rType.setInputUnits(ut);
		}

		Blockette output = container.getDictionaryBlockette(blockette, 5);
		if (output != null) {
			Units ut = factory.createUnitsType();
			ut.setDescription((String) output.getFieldVal(5));
			ut.setName((String) output.getFieldVal(4));
			rType.setOutputUnits(ut);
		}

		Integer numberOfResponses = (Integer) blockette.getFieldVal(6);
		if (numberOfResponses > 0) {
			for (int z = 0; z < numberOfResponses; z++) {
				ResponseListElement rlet = factory.createResponseListElementType();

				Double frequency = (Double) blockette.getFieldVal(7, z);
				if (frequency != null) {
					Frequency ft = factory.createFrequencyType();
					ft.setValue(frequency);
					rlet.setFrequency(ft);
				}

				Double amplitude = (Double) blockette.getFieldVal(8, z);
				if (amplitude != null) {
					Frequency ft = factory.createFrequencyType();
					ft.setValue(amplitude);
					double error = (Double) blockette.getFieldVal(9, z);
					ft.setMinusError(error);
					ft.setPlusError(error);
					rlet.setAmplitude(ft);
				}

				Double phase = (Double) blockette.getFieldVal(10, z);
				if (phase != null) {
					AngleType at = factory.createAngleType();
					at.setValue(phase);
					double error = (Double) blockette.getFieldVal(11, z);
					at.setMinusError(error);
					at.setPlusError(error);
					rlet.setPhase(at);
				}

				rType.getResponseListElement().add(rlet);
			}
		}
		return rType;
	}

	public Decimation do57(SeedObjectContainer container, Blockette blockette) throws Exception {
		LOGGER.finest(" B57 ");

		Decimation decimation = factory.createDecimationType();
		Double correction = (Double) blockette.getFieldVal(8);
		Float ft = factory.createFloatType();
		if (correction != null) {
			ft.setValue(correction);
			decimation.setCorrection(ft);
		}

		Double delay = (Double) blockette.getFieldVal(7);
		if (delay != null) {
			ft = factory.createFloatType();
			ft.setValue(delay);
			decimation.setDelay(ft);
		}
		Integer offset = (Integer) blockette.getFieldVal(6);
		if (offset != null) {
			ft = factory.createFloatType();
			ft.setValue(offset);
			decimation.setOffset(BigInteger.valueOf(offset));
		}

		Integer factor = (Integer) blockette.getFieldVal(5);
		if (factor != null) {
			ft = factory.createFloatType();
			ft.setValue(offset);
			decimation.setFactor(BigInteger.valueOf(factor));
		}

		Double sRate = (Double) blockette.getFieldVal(4);
		if (sRate != null) {
			Frequency fType = factory.createFrequencyType();
			fType.setValue(sRate);
			decimation.setInputSampleRate(fType);
		}
		return decimation;
	}

	public Gain do58(SeedObjectContainer container, Blockette blockette) throws Exception {
		LOGGER.finest(" B58 ");

		Gain gainType = factory.createGainType();

		Double sensGain = (Double) blockette.getFieldVal(4);
		gainType.setValue(sensGain);

		Double frequency = (Double) blockette.getFieldVal(5);
		gainType.setFrequency(frequency);
		return gainType;
	}

	public Filter do61(SeedObjectContainer container, Blockette blockette) throws Exception {
		LOGGER.finest(" B61 ");
		FIR fType = factory.createFIRType();

		Blockette input = container.getDictionaryBlockette(blockette, 6);
		if (input != null) {
			Units ut = factory.createUnitsType();
			ut.setDescription((String) input.getFieldVal(5));
			ut.setName((String) input.getFieldVal(4));
			fType.setInputUnits(ut);

		}

		Blockette output = container.getDictionaryBlockette(blockette, 7);
		if (output != null) {
			Units ut = factory.createUnitsType();
			ut.setDescription((String) output.getFieldVal(5));
			ut.setName((String) output.getFieldVal(4));
			fType.setOutputUnits(ut);
		}

		String symmetryCode = (String) blockette.getFieldVal(5);
		if (symmetryCode != null) {
			if ("A".equals(symmetryCode)) {
				fType.setSymmetry("NONE");
			} else if ("B".equals(symmetryCode)) {
				fType.setSymmetry("ODD");
			} else if ("C".equals(symmetryCode)) {
				fType.setSymmetry("EVEN");
			} else {
				fType.setSymmetry("NONE");
			}

		}

		String responseName = (String) blockette.getFieldVal(4);
		if (responseName != null) {
			fType.setName(responseName);
		}

		int numberOfCoefficients = (Integer) blockette.getFieldVal(8);
		if (numberOfCoefficients > 0) {
			for (int z = 0; z < numberOfCoefficients; z++) {
				Double num = (Double) blockette.getFieldVal(9, z);
				NumeratorCoefficient nc = factory.createFIRTypeNumeratorCoefficient();
				// nc.setI(BigInteger.valueOf(index));
				nc.setValue(num);
				fType.getNumeratorCoefficient().add(nc);
			}
		}
		return fType;
	}

	public Filter do62(SeedObjectContainer container, Blockette blockette) throws Exception {
		LOGGER.finest(" B62 ");

		Polynomial pType = factory.createPolynomialType();

		Blockette input = container.getDictionaryBlockette(blockette, 5);
		if (input != null) {
			Units ut = factory.createUnitsType();
			ut.setDescription((String) input.getFieldVal(5));
			ut.setName((String) input.getFieldVal(4));
			pType.setInputUnits(ut);
		} else {
			// pType.setInputUnits("ERROR");
		}

		Blockette output = container.getDictionaryBlockette(blockette, 6);
		if (output != null) {
			Units ut = factory.createUnitsType();
			ut.setDescription((String) output.getFieldVal(5));
			ut.setName((String) output.getFieldVal(4));
			pType.setOutputUnits(ut);
		} else {
			// pType.setOutputUnits("ERROR");
		}

		String approximationType = (String) blockette.getFieldVal(7);
		if (approximationType != null) {
			if ("M".equals(approximationType)) {
				approximationType = "MACLAURIN";
			}
			pType.setApproximationType(approximationType);
		}

		Double approxLowerBound = (Double) blockette.getFieldVal(11);
		if (approxLowerBound != null) {
			pType.setApproximationLowerBound(BigDecimal.valueOf(approxLowerBound));
		}

		Double approxUpperBound = (Double) blockette.getFieldVal(12);
		if (approxUpperBound != null) {
			pType.setApproximationUpperBound(BigDecimal.valueOf(approxUpperBound));
		}

		Double lowerValidFrequency = (Double) blockette.getFieldVal(9);
		if (lowerValidFrequency != null) {
			Frequency ft = factory.createFrequencyType();
			ft.setValue(lowerValidFrequency);
			pType.setFrequencyLowerBound(ft);
		}

		Double upperValidFrequency = (Double) blockette.getFieldVal(10);
		if (upperValidFrequency != null) {
			Frequency ft = factory.createFrequencyType();
			ft.setValue(upperValidFrequency);
			pType.setFrequencyUpperBound(ft);
		}

		Double maxError = (Double) blockette.getFieldVal(13);
		if (maxError != null) {
			pType.setMaximumError(BigDecimal.valueOf(maxError));
		}

		Integer numberOfCoefficients = (Integer) blockette.getFieldVal(14);
		int index = 0;
		if (numberOfCoefficients > 0) {
			for (int z = 0; z < numberOfCoefficients; z++) {
				Coefficient co = factory.createPolynomialTypeCoefficient();
				Double num = (Double) blockette.getFieldVal(15, z);
				co.setNumber(BigInteger.valueOf(z + 1));
				co.setNumber(BigInteger.valueOf(index));
				double error = (Double) blockette.getFieldVal(16, z);
				co.setMinusError(error);
				co.setPlusError(error);
				co.setValue(num);
				pType.getCoefficient().add(co);
				index++;
			}
		}
		return pType;
	}
}
