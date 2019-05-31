package edu.iris.dmc.station.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Comment;
import edu.iris.dmc.fdsn.station.model.Equipment;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Polynomial;
import edu.iris.dmc.fdsn.station.model.ResponseStage;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.io.SeedFormatter;
import edu.iris.dmc.seed.BTime;
import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.DictionaryIndex;
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
import edu.iris.dmc.seed.io.BlocketteOutputStream;
import edu.iris.dmc.seed.writer.SeedFileWriter;
import edu.iris.dmc.station.ChannelCommentToBlocketteMapper;
import edu.iris.dmc.station.mapper.ChannelBlocketteMapper;
import edu.iris.dmc.station.mapper.CoefficientsMapper;
import edu.iris.dmc.station.mapper.DecimationMapper;
import edu.iris.dmc.station.mapper.FirToBlocketteMapper;
import edu.iris.dmc.station.mapper.InstrumentSensitivityToBlocketteMapper;
import edu.iris.dmc.station.mapper.MetadataConverterException;
import edu.iris.dmc.station.mapper.PolesZerosMapper;
import edu.iris.dmc.station.mapper.PolynomialMapper;
import edu.iris.dmc.station.mapper.SensitivityToBlocketteMapper;
import edu.iris.dmc.station.mapper.StageGainToBlocketteMapper;
import edu.iris.dmc.station.mapper.StationBlocketteMapper;
import edu.iris.dmc.station.mapper.StationCommentToBlocketteMapper;
import edu.iris.dmc.station.mapper.UnitsMapper;
import edu.iris.dmc.station.util.StationIterator;

public class XmlToSeedFileConverter implements MetadataFileFormatConverter<File> {
	private final Logger logger = Logger.getLogger(XmlToSeedFileConverter.class.getName());
	private static XmlToSeedFileConverter INSTANCE = new XmlToSeedFileConverter();

	public static MetadataFileFormatConverter<File> getInstance() {
		return INSTANCE;
	}

	public void convertLarge(File source, File target, Map<String, String> args) throws IOException {
		int recordSize = 4096;
		Map<String, Integer> map = new HashMap<>();
		B010 b010 = new B010();
		b010.setVolumeTime(BTime.now());
		b010.setOrganization("IRIC DMC");
		b010.setVersion("02.4");
		b010.setLabel("Converted from XML");
		DictionaryIndex dictionary = new DictionaryIndex();
		logger.log(Level.FINER, "Writing temperoray station file...");

		File stationTempFile = File.createTempFile("station", "dataless.temp");
		stationTempFile.deleteOnExit();

		try (StationIterator it = IrisUtil.newStationIterator(source);
				BlocketteOutputStream out = new BlocketteOutputStream(new FileOutputStream(stationTempFile),
						recordSize)) {
			while (it.hasNext()) {
				Station station = it.next();
				logger.log(Level.FINE, "processing: " + station.getCode() + "...");
				B050 b050 = StationBlocketteMapper.map(station);

				Network network = station.getNetwork();
				b050.setNetworkCode(station.getNetwork().getCode().trim());
				if (network.getDescription() != null) {
					B033 b033 = new B033();
					b033.setDescription(network.getDescription());
					b033 = (B033) dictionary.put(b033);
					b050.setNetworkIdentifierCode(b033.getLookupKey());
				}
				b050.setNumberOfComments(station.getComment() == null ? 0 : station.getComment().size());
				int sequence = out.write(b050);
				if (map.get(station.getCode().trim()) == null) {
					map.put(station.getCode().trim(), sequence);
				}
				if (station.getComment() != null) {
					for (Comment comment : station.getComment()) {
						if (comment.getBeginEffectiveTime() == null) {
							comment.setBeginEffectiveTime(station.getStartDate());
						}
						B051 b051 = StationCommentToBlocketteMapper.map(comment);
						B031 b031 = new B031();
						b031.setClassCode('S');
						b031.setDescription(comment.getValue());
						b031.setUnitsOfCommentLevel(0);// set to zero for now

						b031 = (B031) dictionary.put(b031);
						b051.setLookupKey(b031.getLookupKey());
						out.write(b051);
					}

					b050.setNumberOfChannels(station.getChannels().size());
					for (Channel channel : station.getChannels()) {
						B052 b052 = ChannelBlocketteMapper.map(channel);
						// is this really needed?
						b052.setSubChannelCode(0);

						B030 b03016 = new B030();
						b03016.setName("Undefined data format");
						b03016.setDataFamilyType(100);
						b03016 = (B030) dictionary.put(b03016);
						b052.setDataFormatIdentifier(b03016.getLookupKey());
						b052.setNumberOfComments(channel.getComment().size());

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
							b03306 = (B033) dictionary.put(b03306);
							b052.setInstrumentIdentifier(b03306.getLookupKey());

						}

						out.write(b052);
						for (Comment comment : channel.getComment()) {
							if (comment.getBeginEffectiveTime() == null) {
								comment.setBeginEffectiveTime(channel.getStartDate());
							}
							B059 b059 = ChannelCommentToBlocketteMapper.map(comment);
							B031 b031 = new B031();
							b031.setClassCode('S');
							b031.setDescription(comment.getValue());
							b031.setUnitsOfCommentLevel(0);// set to zero for
															// now

							b031 = (B031) dictionary.put(b031);
							b059.setLookupKey(b031.getLookupKey());
							out.write(b059);
						}

						if (channel.getResponse() != null) {
							B034 b03408 = null;
							if (channel.getResponse().getInstrumentSensitivity() != null
									&& channel.getResponse().getInstrumentSensitivity().getInputUnits() != null) {
								b03408 = UnitsMapper
										.map(channel.getResponse().getInstrumentSensitivity().getInputUnits());
							} else if (channel.getResponse().getInstrumentPolynomial() != null
									&& channel.getResponse().getInstrumentPolynomial().getInputUnits() != null) {
								b03408 = UnitsMapper
										.map(channel.getResponse().getInstrumentPolynomial().getInputUnits());
							} else {
								// throw exception
							}
							if (b03408 != null) {
								b03408 = (B034) dictionary.put(b03408);
								b052.setUnitsOfSignalResponse(b03408.getLookupKey());
							}

							if (channel.getCalibrationUnits() != null) {
								B034 b03409 = UnitsMapper.map(channel.getCalibrationUnits());
								b03409 = (B034) dictionary.put(b03409);
								b052.setUnitsOfCalibrationInput(b03409.getLookupKey());
							}

							List<ResponseStage> stages = channel.getResponse().getStage();
							if (stages != null) {
								for (ResponseStage stage : stages) {
									if (stage.getPolesZeros() != null) {
										B053 b053 = PolesZerosMapper.map(stage.getPolesZeros());
										if (stage.getPolesZeros().getInputUnits() != null) {
											B034 b03405 = UnitsMapper.map(stage.getPolesZeros().getInputUnits());
											b03405 = (B034) dictionary.put(b03405);
											b053.setSignalInputUnit(b03405.getLookupKey());
										}
										if (stage.getPolesZeros().getOutputUnits() != null) {
											B034 b03406 = UnitsMapper.map(stage.getPolesZeros().getOutputUnits());
											b03406 = (B034) dictionary.put(b03406);
											b053.setSignalOutputUnit(b03406.getLookupKey());
										}
										b053.setStageSequence(stage.getNumber().intValue());
										out.write(b053);
									}
									if (stage.getCoefficients() != null) {
										B054 b054 = CoefficientsMapper.map(stage.getCoefficients());
										if (stage.getCoefficients().getInputUnits() != null) {
											B034 b03405 = UnitsMapper.map(stage.getCoefficients().getInputUnits());
											b03405 = (B034) dictionary.put(b03405);
											b054.setSignalInputUnit(b03405.getLookupKey());
										}
										if (stage.getCoefficients().getOutputUnits() != null) {
											B034 b03406 = UnitsMapper.map(stage.getCoefficients().getOutputUnits());
											b03406 = (B034) dictionary.put(b03406);
											b054.setSignalOutputUnit(b03406.getLookupKey());
										}
										b054.setStageSequence(stage.getNumber().intValue());
										List<Blockette> blockettes = b054.split();
										for (Blockette oBlockette : blockettes) {
											out.write(oBlockette);
										}
									}
									if (stage.getResponseList() != null) {

									}
									if (stage.getDecimation() != null) {
										B057 b057 = DecimationMapper.map(stage.getDecimation());
										b057.setStageSequence(stage.getNumber().intValue());
										out.write(b057);
									}
									if (stage.getStageGain() != null) {
										B058 b058 = StageGainToBlocketteMapper.map(stage.getStageGain());
										b058.setStageSequence(stage.getNumber().intValue());
										out.write(b058);
									}
									if (stage.getFIR() != null) {
										B061 b061 = FirToBlocketteMapper.map(stage.getFIR());
										if (stage.getFIR().getInputUnits() != null) {
											B034 b03406 = UnitsMapper.map(stage.getFIR().getInputUnits());
											b03406 = (B034) dictionary.put(b03406);
											b061.setSignalInputUnit(b03406.getLookupKey());
										}
										if (stage.getFIR().getOutputUnits() != null) {
											B034 b03407 = UnitsMapper.map(stage.getFIR().getOutputUnits());
											b03407 = (B034) dictionary.put(b03407);
											b061.setSignalOutputUnit(b03407.getLookupKey());
										}

										b061.setStageSequence(stage.getNumber().intValue());
										int s = out.write(b061);
									}
									if (stage.getPolynomial() != null) {
										B062 b062 = PolynomialMapper.map(stage.getPolynomial());
										if (stage.getPolynomial().getInputUnits() != null) {
											B034 b03406 = UnitsMapper.map(stage.getPolynomial().getInputUnits());
											b03406 = (B034) dictionary.put(b03406);
											b062.setSignalInputUnit(b03406.getLookupKey());
										}
										if (stage.getPolynomial().getOutputUnits() != null) {
											B034 b03407 = UnitsMapper.map(stage.getPolynomial().getOutputUnits());
											b03407 = (B034) dictionary.put(b03407);
											b062.setSignalOutputUnit(b03407.getLookupKey());
										}

										b062.setStageSequence(stage.getNumber().intValue());
										out.write(b062);
									}
								}
								/*
								 * if (channel.getResponse().getInstrumentSensitivity() != null) { B058 b058 =
								 * InstrumentSensitivityToBlocketteMapper
								 * .map(channel.getResponse().getInstrumentSensitivity());
								 * b058.setStageSequence(0); out.write(b058); }
								 */
							}
							// stage zero

							if (channel.getResponse().getInstrumentSensitivity() != null) {
								B058 b058 = SensitivityToBlocketteMapper
										.map(channel.getResponse().getInstrumentSensitivity());
								b058.setStageSequence(0);
								out.write(b058);
							}

							Polynomial polynomial = channel.getResponse().getInstrumentPolynomial();
							if (polynomial != null) {
								B062 b062 = PolynomialMapper.map(polynomial);
								if (polynomial.getInputUnits() != null) {
									B034 b03406 = UnitsMapper.map(polynomial.getInputUnits());
									b03406 = (B034) dictionary.put(b03406);
									b062.setSignalInputUnit(b03406.getLookupKey());
								}
								if (polynomial.getOutputUnits() != null) {
									B034 b03407 = UnitsMapper.map(polynomial.getOutputUnits());
									b03407 = (B034) dictionary.put(b03407);
									b062.setSignalOutputUnit(b03407.getLookupKey());
								}

								b062.setStageSequence(0);
								out.write(b062);
							}
						}
					}
				}
			}
		} catch (SeedException e) {
			throw new MetadataConverterException(e);
		}

		// We need to build b011 and find out the b050s actual sequences
		int numberOfDictionaryRecords = 0;
		File dictionaryTempFile = File.createTempFile("dictionary", "dataless.temp");
		dictionaryTempFile.deleteOnExit();
		logger.log(Level.FINE, "Writing temperoray dictionary file [" + dictionaryTempFile.getAbsolutePath() + "]");
		try (BlocketteOutputStream a = new BlocketteOutputStream(new FileOutputStream(dictionaryTempFile))) {
			for (Blockette b : dictionary.getAll()) {
				numberOfDictionaryRecords = a.write(b);
			}
		}

		logger.log(Level.INFO, "Writing the dataless file " + target.getAbsolutePath());
		try (BlocketteOutputStream theFile = new BlocketteOutputStream(new FileOutputStream(target))) {
			// calculate b011
			int numberOfStations = map.size();
			int volumeSize = b010.getLength() + 10 + (numberOfStations * 11);

			// How many records the volum header would take and therefore what would the
			// sequence be
			int sequence = numberOfDictionaryRecords + (int) Math.ceil((double) volumeSize / recordSize);
			B011 b011 = new B011();
			map.forEach((k, v) -> b011.add(k, v + sequence));
			// write volume

			theFile.write(b010);
			int startSequence = theFile.write(b011);
			// copy dictionary
			try (InputStream is = new FileInputStream(dictionaryTempFile)) {
				byte[] bytes = new byte[recordSize];

				startSequence++;
				while (is.read(bytes) > 0) {
					byte[] sequenceBytes = SeedFormatter.format(startSequence, 6).getBytes();
					System.arraycopy(sequenceBytes, 0, bytes, 0, 6);
					theFile.writeRaw(bytes);
					startSequence++;
				}
			}
			// copy station
			try (InputStream is = new FileInputStream(stationTempFile)) {
				byte[] bytes = new byte[recordSize];
				while (is.read(bytes) > 0) {
					byte[] sequenceBytes = SeedFormatter.format(startSequence, 6).getBytes();
					System.arraycopy(sequenceBytes, 0, bytes, 0, 6);
					theFile.writeRaw(bytes);
					startSequence++;
				}
			}
		} catch (SeedException e) {
			throw new IOException(e);
		}
		if (stationTempFile != null) {
			stationTempFile.delete();
		}
	}

	@Override
	public void convert(File source, File target) throws IOException {
		this.convert(source, target, null);
	}

	@Override
	public void convert(File source, File target, Map<String, String> args) throws IOException {
		if (args != null) {
			String large = args.get("large");
			if (large != null && Boolean.valueOf(large)) {
				this.convertLarge(source, target, null);
				return;
			}
		}
		FDSNStationXML document = null;
		Volume volume = null;
		try {
			document = IrisUtil.readXml(source);
			volume = XmlToSeedDocumentConverter.getInstance().convert(document);
			volume.build();
		} catch (JAXBException | SeedException e) {
			throw new IOException(e);
		}

		int logicalrecordLength = (int) Math.pow(2, volume.getB010().getNthPower());
		try (SeedFileWriter writer = new SeedFileWriter(target, logicalrecordLength)) {
			writer.write(volume);
		}

	}

}
