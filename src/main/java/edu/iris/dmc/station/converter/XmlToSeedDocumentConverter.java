package edu.iris.dmc.station.converter;

import java.io.IOException;
import java.util.List;

import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Comment;
import edu.iris.dmc.fdsn.station.model.Equipment;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Polynomial;
import edu.iris.dmc.fdsn.station.model.ResponseStage;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.seed.BTime;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.dictionary.B030;
import edu.iris.dmc.seed.control.dictionary.B031;
import edu.iris.dmc.seed.control.dictionary.B033;
import edu.iris.dmc.seed.control.dictionary.B034;
import edu.iris.dmc.seed.control.index.B010;
import edu.iris.dmc.seed.control.index.B011;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.seed.control.station.B051;
import edu.iris.dmc.seed.control.station.B052;
import edu.iris.dmc.seed.control.station.B053;
import edu.iris.dmc.seed.control.station.B054;
import edu.iris.dmc.seed.control.station.B057;
import edu.iris.dmc.seed.control.station.B058;
import edu.iris.dmc.seed.control.station.B059;
import edu.iris.dmc.seed.control.station.B061;
import edu.iris.dmc.seed.control.station.B062;
import edu.iris.dmc.station.ChannelCommentToBlocketteMapper;
import edu.iris.dmc.station.FirToBlocketteMapper;
import edu.iris.dmc.station.InstrumentSensitivityToBlocketteMapper;
import edu.iris.dmc.station.MetadataConverterException;
import edu.iris.dmc.station.SensitivityToBlocketteMapper;
import edu.iris.dmc.station.StageGainToBlocketteMapper;
import edu.iris.dmc.station.StationBlocketteMapper;
import edu.iris.dmc.station.StationCommentToBlocketteMapper;
import edu.iris.dmc.station.UnitToBlocketteMapper;
import edu.iris.dmc.station.mapper.ChannelBlocketteMapper;
import edu.iris.dmc.station.mapper.CoefficientsMapper;
import edu.iris.dmc.station.mapper.DecimationMapper;
import edu.iris.dmc.station.mapper.PolesZerosMapper;
import edu.iris.dmc.station.mapper.PolynomialMapper;

public class XmlToSeedDocumentConverter implements MetadataDocumentFormatConverter<FDSNStationXML, Volume> {

	private static XmlToSeedDocumentConverter INSTANCE = new XmlToSeedDocumentConverter();

	public static MetadataDocumentFormatConverter<FDSNStationXML, Volume> getInstance() {
		return INSTANCE;
	}

	@Override
	public Volume convert(FDSNStationXML document) throws MetadataConverterException, IOException {
		if (document == null || document.getNetwork() == null || document.getNetwork().isEmpty()) {

		}

		Volume volume = new Volume();
		try {
			B010 b010 = new B010();

			// b010.setStartTime(time);
			// b010.setEndTime(endTime);
			b010.setVolumeTime(BTime.now());
			b010.setOrganization("IRIC DMC");
			b010.setVersion("02.4");
			b010.setLabel("Converted from XML");
			volume.add(b010);

			B011 b011 = new B011();
			volume.add(b011);
			for (Network network : document.getNetwork()) {
				int networkIdentifierCode = 0;
				if (network.getDescription() != null) {
					B033 b033 = new B033();
					b033.setDescription(network.getDescription());
					b033 = (B033) volume.add(b033);
					networkIdentifierCode = b033.getLookupKey();
				}
				for (Station station : network.getStations()) {
					B050 b050 = StationBlocketteMapper.map(station);
					b050.setNetworkIdentifierCode(networkIdentifierCode);
					b050.setNetworkCode(network.getCode().trim());
					volume.add(b050);

					if (station.getComment() != null) {
						b050.setNumberOfComments(station.getComment().size());
						for (Comment comment : station.getComment()) {
							B051 b051 = StationCommentToBlocketteMapper.map(comment);
							B031 b031 = new B031();
							b031.setClassCode('S');
							b031.setDescription(comment.getValue());
							b031.setUnitsOfCommentLevel(0);// set to zero for now

							b031 = (B031) volume.add(b031);
							b051.setLookupKey(b031.getLookupKey());
							volume.add(b051);
						}
					}
					if (station.getChannels() != null) {
						b050.setNumberOfChannels(station.getChannels().size());
						for (Channel channel : station.getChannels()) {
							B052 b052 = ChannelBlocketteMapper.map(channel);
							// is this really neede?
							b052.setSubChannelCode(0);
							volume.add(b052);

							B030 b03016 = new B030();
							b03016.setName("Undefined data format");
							b03016.setDataFamilyType(100);
							b03016 = (B030) volume.add(b03016);
							b052.setDataFormatIdentifier(b03016.getLookupKey());

							for (Comment comment : channel.getComment()) {
								B059 b059 = ChannelCommentToBlocketteMapper.map(comment);
								B031 b031 = new B031();
								b031.setClassCode('S');
								b031.setDescription(comment.getValue());
								b031.setUnitsOfCommentLevel(0);// set to zero for now

								b031 = (B031) volume.add(b031);
								b059.setLookupKey(b031.getLookupKey());
								volume.add(b059);

							}
							b052.setNumberOfComments(channel.getComment().size());
							// channel.getDataLogger();
							// channel.getEquipment();
							// channel.getSensor();
							Equipment equipment = channel.getSensor();
							if (equipment != null) {
								StringBuilder equipmentText = new StringBuilder();
								boolean addComma = false;

								if (equipment.getModel() != null) {
									if (addComma) {
										equipmentText.append(",");
									}
									equipmentText.append(equipment.getModel());
									addComma = true;
								}

								if (equipment.getDescription() != null) {
									if (addComma) {
										equipmentText.append(",");
									}
									equipmentText.append(equipment.getDescription());
								}

								if (equipment.getType() != null) {
									if (addComma) {
										equipmentText.append(",");
									}
									equipmentText.append(equipment.getType());
								}
								B033 b03306 = new B033();
								b03306.setDescription(equipmentText.toString());
								b03306 = (B033) volume.add(b03306);
								b052.setInstrumentIdentifier(b03306.getLookupKey());

							}

							if (channel.getResponse() != null) {
								B034 b03408 = null;
								if (channel.getResponse().getInstrumentSensitivity() != null
										&& channel.getResponse().getInstrumentSensitivity().getInputUnits() != null) {
									b03408 = UnitToBlocketteMapper
											.map(channel.getResponse().getInstrumentSensitivity().getInputUnits());
								} else if (channel.getResponse().getInstrumentPolynomial() != null
										&& channel.getResponse().getInstrumentPolynomial().getInputUnits() != null) {
									b03408 = UnitToBlocketteMapper
											.map(channel.getResponse().getInstrumentPolynomial().getInputUnits());
								} else {
									// throw exception
								}
								if (b03408 != null) {
									b03408 = (B034) volume.add(b03408);
									b052.setUnitsOfSignalResponse(b03408.getLookupKey());
								}

								if (channel.getCalibrationUnits() != null) {
									B034 b03409 = UnitToBlocketteMapper.map(channel.getCalibrationUnits());
									b03409 = (B034) volume.add(b03409);
									b052.setUnitsOfCalibrationInput(b03409.getLookupKey());
								}

								List<ResponseStage> stages = channel.getResponse().getStage();
								if (stages != null) {
									for (ResponseStage stage : stages) {
										if (stage.getPolesZeros() != null) {
											B053 b053 = PolesZerosMapper.map(stage.getPolesZeros());
											if (stage.getPolesZeros().getInputUnits() != null) {
												B034 b03405 = UnitToBlocketteMapper
														.map(stage.getPolesZeros().getInputUnits());
												b03405 = (B034) volume.add(b03405);
												b053.setSignalInputUnit(b03405.getLookupKey());
											}
											if (stage.getPolesZeros().getOutputUnits() != null) {
												B034 b03406 = UnitToBlocketteMapper
														.map(stage.getPolesZeros().getOutputUnits());
												b03406 = (B034) volume.add(b03406);
												b053.setSignalOutputUnit(b03406.getLookupKey());
											}
											b053.setStageSequence(stage.getNumber().intValue());
											volume.add(b053);
										}
										if (stage.getCoefficients() != null) {
											B054 b054 = CoefficientsMapper.map(stage.getCoefficients());
											if (stage.getCoefficients().getInputUnits() != null) {
												B034 b03405 = UnitToBlocketteMapper
														.map(stage.getCoefficients().getInputUnits());
												b03405 = (B034) volume.add(b03405);
												b054.setSignalInputUnit(b03405.getLookupKey());
											}
											if (stage.getCoefficients().getOutputUnits() != null) {
												B034 b03406 = UnitToBlocketteMapper
														.map(stage.getCoefficients().getOutputUnits());
												b03406 = (B034) volume.add(b03406);
												b054.setSignalOutputUnit(b03406.getLookupKey());
											}
											b054.setStageSequence(stage.getNumber().intValue());
											volume.add(b054);
										}
										if (stage.getResponseList() != null) {

										}
										if (stage.getDecimation() != null) {
											B057 b057 = DecimationMapper.map(stage.getDecimation());
											b057.setStageSequence(stage.getNumber().intValue());
											volume.add(b057);
										}
										if (stage.getStageGain() != null) {
											B058 b058 = StageGainToBlocketteMapper.map(stage.getStageGain());
											b058.setStageSequence(stage.getNumber().intValue());
											volume.add(b058);
										}
										if (stage.getFIR() != null) {
											B061 b061 = FirToBlocketteMapper.map(stage.getFIR());
											if (stage.getFIR().getInputUnits() != null) {
												B034 b03406 = UnitToBlocketteMapper.map(stage.getFIR().getInputUnits());
												b03406 = (B034) volume.add(b03406);
												b061.setSignalInputUnit(b03406.getLookupKey());
											}
											if (stage.getFIR().getOutputUnits() != null) {
												B034 b03407 = UnitToBlocketteMapper
														.map(stage.getFIR().getOutputUnits());
												b03407 = (B034) volume.add(b03407);
												b061.setSignalOutputUnit(b03407.getLookupKey());
											}

											b061.setStageSequence(stage.getNumber().intValue());
											volume.add(b061);
										}
										if (stage.getPolynomial() != null) {
											B062 b062 = PolynomialMapper.map(stage.getPolynomial());
											if (stage.getPolynomial().getInputUnits() != null) {
												B034 b03406 = UnitToBlocketteMapper
														.map(stage.getPolynomial().getInputUnits());
												b03406 = (B034) volume.add(b03406);
												b062.setSignalInputUnit(b03406.getLookupKey());
											}
											if (stage.getPolynomial().getOutputUnits() != null) {
												B034 b03407 = UnitToBlocketteMapper
														.map(stage.getPolynomial().getOutputUnits());
												b03407 = (B034) volume.add(b03407);
												b062.setSignalOutputUnit(b03407.getLookupKey());
											}

											b062.setStageSequence(stage.getNumber().intValue());
											volume.add(b062);
										}
									}
									if (channel.getResponse().getInstrumentSensitivity() != null) {
										B058 b058 = InstrumentSensitivityToBlocketteMapper
												.map(channel.getResponse().getInstrumentSensitivity());
										b058.setStageSequence(0);
										volume.add(b058);
									}
								}
								// stage zero

								if (channel.getResponse().getInstrumentSensitivity() != null) {
									B058 b058 = SensitivityToBlocketteMapper
											.map(channel.getResponse().getInstrumentSensitivity());
									b058.setStageSequence(0);
								}

								Polynomial polynomial = channel.getResponse().getInstrumentPolynomial();
								if (polynomial != null) {
									B062 b062 = PolynomialMapper.map(polynomial);
									if (polynomial.getInputUnits() != null) {
										B034 b03406 = UnitToBlocketteMapper.map(polynomial.getInputUnits());
										b03406 = (B034) volume.add(b03406);
										b062.setSignalInputUnit(b03406.getLookupKey());
									}
									if (polynomial.getOutputUnits() != null) {
										B034 b03407 = UnitToBlocketteMapper.map(polynomial.getOutputUnits());
										b03407 = (B034) volume.add(b03407);
										b062.setSignalOutputUnit(b03407.getLookupKey());
									}

									b062.setStageSequence(0);
									volume.add(b062);
								}
							}
						}
					}
				}
			}
			volume.build();
		} catch (

		SeedException e) {
			throw new MetadataConverterException(e);
		}
		return volume;
	}
}
