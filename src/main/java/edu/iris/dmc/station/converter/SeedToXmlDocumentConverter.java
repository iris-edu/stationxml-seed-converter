package edu.iris.dmc.station.converter;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Coefficients;
import edu.iris.dmc.fdsn.station.model.Comment;
import edu.iris.dmc.fdsn.station.model.Decimation;
import edu.iris.dmc.fdsn.station.model.Equipment;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.FIR;
import edu.iris.dmc.fdsn.station.model.ResponseType;
import edu.iris.dmc.fdsn.station.model.Gain;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.PolesZeros;
import edu.iris.dmc.fdsn.station.model.Polynomial;
import edu.iris.dmc.fdsn.station.model.Response;
import edu.iris.dmc.fdsn.station.model.ResponseList;
import edu.iris.dmc.fdsn.station.model.ResponseStage;
import edu.iris.dmc.fdsn.station.model.Sensitivity;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.fdsn.station.model.Units;
import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.dictionary.B031;
import edu.iris.dmc.seed.control.dictionary.B033;
import edu.iris.dmc.seed.control.dictionary.B034;
import edu.iris.dmc.seed.control.dictionary.B041;
import edu.iris.dmc.seed.control.dictionary.B042;
import edu.iris.dmc.seed.control.dictionary.B043;
import edu.iris.dmc.seed.control.dictionary.B044;
import edu.iris.dmc.seed.control.dictionary.B045;
import edu.iris.dmc.seed.control.dictionary.B047;
import edu.iris.dmc.seed.control.dictionary.B048;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.seed.control.station.B051;
import edu.iris.dmc.seed.control.station.B052;
import edu.iris.dmc.seed.control.station.B053;
import edu.iris.dmc.seed.control.station.B054;
import edu.iris.dmc.seed.control.station.B055;
import edu.iris.dmc.seed.control.station.B057;
import edu.iris.dmc.seed.control.station.B058;
import edu.iris.dmc.seed.control.station.B059;
import edu.iris.dmc.seed.control.station.B060;
import edu.iris.dmc.seed.control.station.B061;
import edu.iris.dmc.seed.control.station.B062;
import edu.iris.dmc.seed.control.station.ResponseBlockette;
import edu.iris.dmc.seed.control.station.SeedResponseStage;
import edu.iris.dmc.station.mapper.ChannelBlocketteMapper;
import edu.iris.dmc.station.mapper.CoefficientsMapper;
import edu.iris.dmc.station.mapper.CommentMapper;
import edu.iris.dmc.station.mapper.DecimationMapper;
import edu.iris.dmc.station.mapper.FilterBuilder;
import edu.iris.dmc.station.mapper.FirMapper;
import edu.iris.dmc.station.mapper.GainMapper;
import edu.iris.dmc.station.mapper.MetadataConverterException;
import edu.iris.dmc.station.mapper.PolesZerosMapper;
import edu.iris.dmc.station.mapper.PolynomialMapper;
import edu.iris.dmc.station.mapper.ResponseListMapper;
import edu.iris.dmc.station.mapper.StationMapper;
import edu.iris.dmc.station.mapper.UnitsMapper;

public class SeedToXmlDocumentConverter implements MetadataDocumentFormatConverter<Volume, FDSNStationXML> {

	private static SeedToXmlDocumentConverter INSTANCE = new SeedToXmlDocumentConverter();

	public static MetadataDocumentFormatConverter<Volume, FDSNStationXML> getInstance() {
		return INSTANCE;
	}

	@Override
	public FDSNStationXML convert(Volume volume) throws MetadataConverterException, IOException {

		if (volume == null) {
			throw new IllegalArgumentException("Container cannot be null");
		}

		FDSNStationXML document = new FDSNStationXML();
		try {
			document.setSource("IRIS-DMC");
			document.setCreated(IrisUtil.now());
			document.setSchemaVersion(BigDecimal.valueOf(1.0));
			document.setModule("IRIS converter | version: ");
			document.setModuleURI("https://seiscode.iris.washington.edu/projects/stationxml-converter/wiki");

			volume.getB010();
			// volume.getB011();

			Network network = null;
			boolean endSwitch = false;

			boolean overWrite = true;
			for (B050 b050 : volume.getB050s()) {
				Station station = StationMapper.map(b050);
				String networkCode = b050.getNetworkCode();
				int networkIdentifierCode = b050.getNetworkIdentifierCode();
				if (network == null || !networkCode.equals(network.getCode())) {
					network = new Network();
					network.setCode(networkCode);
					B033 b03310 = (B033) volume.getDictionaryBlockette(33, networkIdentifierCode);
					if (b03310 != null) {
						network.setDescription(b03310.getDescription());
					}
					document.getNetwork().add(network);
				}

				if (network.getStartDate() == null) {
					network.setStartDate(station.getStartDate());
				} else {
					if (network.getStartDate().isAfter(station.getStartDate())) {
						network.setStartDate(station.getStartDate());
					}
				}
				if (overWrite) {
					if (station.getEndDate() == null) {
						network.setEndDate(null);
						overWrite = false;
					} else {
						if (network.getEndDate() == null) {
							network.setEndDate(station.getEndDate());
						} else {
							if (network.getEndDate().isBefore(station.getEndDate())) {
								network.setEndDate(station.getEndDate());
							}
						}
					}
				}

				network.addStation(station);

				if (b050.getB051s() != null && !b050.getB051s().isEmpty()) {
					for (B051 b051 : b050.getB051s()) {
						Comment stationComment = CommentMapper.buildForStation(b051);
						B031 b031 = (B031) volume.getDictionaryBlockette(31, b051.getLookupKey());
						stationComment.setValue(b031.getDescription());
						station.add(stationComment);
					}
				}
				for (B052 b052 : b050.getB052s()) {

					Channel channel = ChannelBlocketteMapper.map(b052);
					station.addChannel(channel);
					B033 b03306 = (B033) volume.getDictionaryBlockette(33, b052.getInstrumentIdentifier());

					if (b03306 != null) {
						Equipment equipment = new Equipment();
						equipment.setDescription(b03306.getDescription());
						channel.setSensor(equipment);
					}
					B034 b03408 = (B034) volume.getDictionaryBlockette(34, b052.getUnitsOfSignalResponse());
					if (b03408 != null) {
						Units units = new Units();
						units.setName(b03408.getName());
						units.setDescription(b03408.getDescription());
					}

					B034 b03409 = (B034) volume.getDictionaryBlockette(34, b052.getUnitsOfCalibrationInput());
					if (b03409 != null) {
						channel.setCalibrationUnits(UnitsMapper.map(b03409));
					}

					// Not needed for station xml
					b052.getDataFormatIdentifier();
					b052.getOptionalComment();

					if (b052.getB059s() != null) {
						for (B059 b059 : b052.getB059s()) {
							Comment channelComment = CommentMapper.buildForChannel(b059);
							B031 b031 = (B031) volume.getDictionaryBlockette(31, b059.getLookupKey());
							if (b031 != null) {
								channelComment.setValue(b031.getDescription());
								channel.add(channelComment);
							}
						}
					}

					if (b052.getResponseStages() != null) {
						Response response = new Response();
						channel.setResponse(response);
						for (SeedResponseStage seedStage : b052.getResponseStages()) {

							if (seedStage.getSequence() == 0) {
								for (ResponseBlockette b : seedStage.getBlockettes()) {
									final int type = b.getType();
									switch (type) {
									case 58:
										B058 b058 = (B058) b;
										Sensitivity sensitivity = new Sensitivity();

										// sensitivity.setInputUnits(value);
										// sensitivity.setOutputUnits(value);

										sensitivity.setFrequency(b058.getFrequency());
										// sensitivity.setFrequencyDBVariation();
										// sensitivity.setFrequencyEnd(value);
										// sensitivity.setFrequencyStart(value);
										sensitivity.setValue(b058.getSensitivity());
										response.setInstrumentSensitivity(sensitivity);
										break;
									case 62:
										B062 b062 = (B062) b;
										Polynomial polynomial = PolynomialMapper.map(b062);
										B034 b03405 = (B034) volume.getDictionaryBlockette(34,
												b062.getSignalInputUnit());
										if (b03405 != null) {
											polynomial.setInputUnits(UnitsMapper.map(b03405));
										}
										B034 b03406 = (B034) volume.getDictionaryBlockette(34,
												b062.getSignalOutputUnit());
										if (b03406 != null) {
											polynomial.setOutputUnits(UnitsMapper.map(b03406));
										}
										response.setInstrumentPolynomial(polynomial);
										break;
									}
								}
							} else {
								ResponseStage stage = new ResponseStage();
								stage.setNumber(BigInteger.valueOf(seedStage.getSequence()));
								channel.getResponse().getStage().add(stage);
								for (ResponseBlockette b : seedStage.getBlockettes()) {
									final int type = b.getType();
									switch (type) {
									case 53:
										B053 b053 = (B053) b;
										PolesZeros polesZeros = PolesZerosMapper.map(b053);
										B034 b03405 = (B034) volume.getDictionaryBlockette(34,
												b053.getSignalInputUnit());
										if (b03405 != null) {
											polesZeros.setInputUnits(UnitsMapper.map(b03405));
										}
										B034 b03406 = (B034) volume.getDictionaryBlockette(34,
												b053.getSignalOutputUnit());
										if (b03406 != null) {
											polesZeros.setOutputUnits(UnitsMapper.map(b03406));
										}
										stage.add(polesZeros);
										break;
									case 54:
										B054 b054 = (B054) b;

										Coefficients coefficients = CoefficientsMapper.map(b054);
										b03405 = (B034) volume.getDictionaryBlockette(34, b054.getSignalInputUnit());
										if (b03405 != null) {
											coefficients.setInputUnits(UnitsMapper.map(b03405));
										}
										b03406 = (B034) volume.getDictionaryBlockette(34, b054.getSignalOutputUnit());
										if (b03406 != null) {
											coefficients.setOutputUnits(UnitsMapper.map(b03406));
										}
										stage.add(coefficients);
										break;
									case 55:
										B055 b055 = (B055) b;

										ResponseList responseList = ResponseListMapper.map(b055);
										b03405 = (B034) volume.getDictionaryBlockette(34, b055.getSignalInputUnit());
										if (b03405 != null) {
											responseList.setInputUnits(UnitsMapper.map(b03405));
										}
										b03406 = (B034) volume.getDictionaryBlockette(34, b055.getSignalOutputUnit());
										if (b03406 != null) {
											responseList.setOutputUnits(UnitsMapper.map(b03406));
										}
										stage.add(responseList);
										break;
									case 57:
										B057 b057 = (B057) b;

										Decimation decimation = DecimationMapper.map(b057);
										stage.setDecimation(decimation);
										break;
									case 58:
										B058 b058 = (B058) b;
										Gain gain = GainMapper.build(b058);
										stage.setStageGain(gain);
										break;
									case 60:
										B060 b060 = (B060) b;
										List<edu.iris.dmc.seed.control.station.Stage> list = b060.getStages();
										for (edu.iris.dmc.seed.control.station.Stage s : list) {
											int sequence = s.getSequence();
											if (sequence > channel.getResponse().getStage().size()) {
												stage = new ResponseStage();
												stage.setNumber(BigInteger.valueOf(sequence));
												channel.addStage(stage);
											} else {
												stage = channel.getResponse().getStage().get(sequence - 1);
											}
											for (Integer lookupKey : s.getResponses()) {
												Blockette referenceBlockette = volume
														.getResponseDictionaryBlockette(lookupKey);
												switch (referenceBlockette.getType()) {

												case 41:
													stage.setFIR(FirMapper.map((B041) referenceBlockette));
													break;
												case 42:
													stage.setPolynomial(
															PolynomialMapper.map((B042) referenceBlockette));
													break;
												case 43:
													stage.setPolesZeros(
															PolesZerosMapper.map((B043) referenceBlockette));
													break;
												case 44:
													stage.setCoefficients(
															CoefficientsMapper.map((B044) referenceBlockette));
													break;
												case 45:
													stage.setResponseList(
															ResponseListMapper.map((B045) referenceBlockette));
													break;
												case 46:
													// no generic response
													break;
												case 47:
													stage.setDecimation(
															DecimationMapper.map((B047) referenceBlockette));
													break;
												case 48:
													stage.setStageGain(GainMapper.build((B048) referenceBlockette));
													break;
												case 49:
													stage.setPolynomial(
															PolynomialMapper.map((B042) referenceBlockette));
													break;
												default:
													ResponseType filter = FilterBuilder.build(referenceBlockette);
													stage.add(filter);
													break;
												}
											}
										}
										break;
									case 61:
										B061 b061 = (B061) b;
										FIR fir = FirMapper.build(b061);
										b03405 = (B034) volume.getDictionaryBlockette(34, b061.getSignalInputUnit());
										if (b03405 != null) {
											fir.setInputUnits(UnitsMapper.map(b03405));
										}
										b03406 = (B034) volume.getDictionaryBlockette(34, b061.getSignalOutputUnit());
										if (b03406 != null) {
											fir.setOutputUnits(UnitsMapper.map(b03406));
										}
										stage.add(fir);
										break;
									case 62:
										B062 b062 = (B062) b;
										Polynomial polynomial = PolynomialMapper.map(b062);
										b03405 = (B034) volume.getDictionaryBlockette(34, b062.getSignalInputUnit());
										if (b03405 != null) {
											polynomial.setInputUnits(UnitsMapper.map(b03405));
										}
										b03406 = (B034) volume.getDictionaryBlockette(34, b062.getSignalOutputUnit());
										if (b03406 != null) {
											polynomial.setOutputUnits(UnitsMapper.map(b03406));
										}
										stage.add(polynomial);
										break;
									default:
										throw new MetadataConverterException(
												String.format("\"Unkown blockette type [%s]", type));
									}
								}
							}
						}
						updateInstrumentSensitivityUnits(channel);
						updateInstrumentPolynomial(channel);
					}
				}
			}
		} catch (DatatypeConfigurationException e1) {
			throw new MetadataConverterException(e1);
		} catch (Exception e) {
			throw new MetadataConverterException(e);
		}
		return document;
	}

	private void updateInstrumentSensitivityUnits(Channel channel) {
		if (channel == null) {
			return;
		}
		Response response = channel.getResponse();
		if (response == null) {
			return;
		}
		Sensitivity sensitivity = channel.getResponse().getInstrumentSensitivity();
		if (sensitivity == null) {
			return;
		}

		List<ResponseStage> list = response.getStage();
		if (list != null && !list.isEmpty()) {
			ResponseStage first = list.get(0);
			Units inputUnits = extractInputUnits(first);
			sensitivity.setInputUnits(inputUnits);
			ResponseStage last = list.get(list.size() - 1);
			Units outputUnits = extractOutputUnits(last);
			sensitivity.setOutputUnits(outputUnits);
		}

	}

	private void updateInstrumentPolynomial(Channel channel) {
		if (channel == null) {
			return;
		}
		Response response = channel.getResponse();
		if (response == null) {
			return;
		}
		Polynomial polynomial = channel.getResponse().getInstrumentPolynomial();
		if (polynomial == null) {
			return;
		}

		List<ResponseStage> list = response.getStage();
		if (list != null && !list.isEmpty()) {
			ResponseStage first = list.get(0);
			Units inputUnits = extractInputUnits(first);
			polynomial.setInputUnits(inputUnits);
			ResponseStage last = list.get(list.size() - 1);
			Units outputUnits = extractOutputUnits(last);
			polynomial.setOutputUnits(outputUnits);
		}

	}

	private Units extractInputUnits(ResponseStage stage) {
		if (stage.getCoefficients() != null) {
			return stage.getCoefficients().getInputUnits();
		}

		if (stage.getPolesZeros() != null) {
			return stage.getPolesZeros().getInputUnits();
		}
		if (stage.getPolynomial() != null) {
			return stage.getPolynomial().getInputUnits();
		}

		if (stage.getFIR() != null) {
			return stage.getFIR().getInputUnits();
		}

		if (stage.getResponseList() != null) {
			return stage.getResponseList().getInputUnits();
		}
		return null;
	}

	private Units extractOutputUnits(ResponseStage stage) {
		if (stage.getCoefficients() != null) {
			return stage.getCoefficients().getOutputUnits();
		}

		if (stage.getPolesZeros() != null) {
			return stage.getPolesZeros().getOutputUnits();
		}
		if (stage.getPolynomial() != null) {
			return stage.getPolynomial().getOutputUnits();
		}

		if (stage.getFIR() != null) {
			return stage.getFIR().getOutputUnits();
		}

		if (stage.getResponseList() != null) {
			return stage.getResponseList().getOutputUnits();
		}
		return null;
	}
}
