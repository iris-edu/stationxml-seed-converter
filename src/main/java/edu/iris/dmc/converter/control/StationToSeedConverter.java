package edu.iris.dmc.converter.control;

import java.io.OutputStream;
import java.util.List;
import java.util.logging.Logger;

import edu.iris.Fissures.seed.builder.SeedExportBuilder;
import edu.iris.Fissures.seed.container.SeedObjectContainer;
import edu.iris.Fissures.seed.director.ExportTemplate;
import edu.iris.Fissures.seed.director.SeedExportDirector;
import edu.iris.dmc.converter.seed.SeedUtil;
import edu.iris.dmc.converter.seed.StationSeedObjectBuilder;
import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Coefficients;
import edu.iris.dmc.fdsn.station.model.Comment;
import edu.iris.dmc.fdsn.station.model.Decimation;
import edu.iris.dmc.fdsn.station.model.FIR;
import edu.iris.dmc.fdsn.station.model.Gain;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Polynomial;
import edu.iris.dmc.fdsn.station.model.ResponseList;
import edu.iris.dmc.fdsn.station.model.ResponseStage;
import edu.iris.dmc.fdsn.station.model.Sensitivity;
import edu.iris.dmc.fdsn.station.model.Station;

public class StationToSeedConverter{

	private static final Logger LOGGER = Logger.getLogger(StationToSeedConverter.class.getName());

	private StationSeedObjectBuilder seedObjectBuilder;
	// private PrintStream logging = System.err;

	public void process(OutputStream stream, List<Network> list, String sender) throws Exception {

		seedObjectBuilder = new StationSeedObjectBuilder();

		LOGGER.info("processing " + list.size() + " network(s)");
		for (Network network : list) {
			for (Station station : network.getStations()) {
				process(stream, station);
			}
		}
		SeedExportBuilder exportBuilder = getSeedExportBuilder("dataless");
		if (sender != null) {
			exportBuilder.setOrganizationName(sender);
		}
		exportBuilder.open(stream);
		SeedExportDirector director = new SeedExportDirector();
		director.assignBuilder(exportBuilder);
		director.assignContainer(seedObjectBuilder.getContainer());
		director.assignTemplate(new ExportTemplate());
		director.fillTemplate(null);

		director.construct();
		exportBuilder.close();// added this
	}

	public void process(OutputStream stream, Station station) throws Exception {
		if (seedObjectBuilder == null) {
			seedObjectBuilder = new StationSeedObjectBuilder();
		}
		LOGGER.info("processing " + station);

		int stationLookupId = seedObjectBuilder.build(station);

		if (station.getComment() != null && !station.getComment().isEmpty()) {
			LOGGER.info("processing " + station.getComment().size() + " comments for station " + station.getCode());
			for (Comment c : station.getComment()) {
				seedObjectBuilder.build(c, stationLookupId);
			}
		}

		if (station.getChannels() != null && !station.getChannels().isEmpty()) {
			LOGGER.info("processing comments for station " + station.getCode());
			for (Channel channel : station.getChannels()) {
				process(stream, channel, stationLookupId);
			}
		} else {
			LOGGER.warning("Station " + station.getCode() + " has no channels");
		}
	}

	public void process(OutputStream stream, Channel channel, int stationLookupId) throws Exception {

		int channelLookupId = seedObjectBuilder.build(channel, stationLookupId);
		if (channel.getComment() != null && !channel.getComment().isEmpty()) {
			for (Comment c : channel.getComment()) {
				seedObjectBuilder.build(c, channelLookupId);
			}
		}
		SeedObjectContainer container = (SeedObjectContainer) seedObjectBuilder.getContainer();
		if (channel.getResponse() != null) {
			for (ResponseStage stage : channel.getResponse().getStage()) {
				LOGGER.info("\t\tProcessing stage:" + stage.getNumber());

				if (stage.getPolesZeros() != null) {
					LOGGER.info("\t\t\tAdding response blockette:53");
					int inputUnitsId = 0;
					if (stage.getPolesZeros().getInputUnits().getName() != null) {
						inputUnitsId = seedObjectBuilder.build(34, stage.getPolesZeros().getInputUnits().getName(),
								stage.getPolesZeros().getInputUnits().getDescription());
					}
					int outputUnitsId = 0;
					if (stage.getPolesZeros().getOutputUnits().getName() != null) {
						outputUnitsId = seedObjectBuilder.build(34, stage.getPolesZeros().getOutputUnits().getName(),
								stage.getPolesZeros().getOutputUnits().getDescription());
					}

					container.setParent(channelLookupId);
					String s = SeedUtil.toSeedString(stage.getNumber().intValue(), stage.getPolesZeros(), inputUnitsId,
							outputUnitsId);
					LOGGER.info("\t\t" + s);
					seedObjectBuilder.build(s);
					seedObjectBuilder.store();
				}

				if (stage.getCoefficients() != null) {

					Coefficients coef = stage.getCoefficients();
					LOGGER.info("\t\t\tAdding response blockette:54");

					int inputUnitsId = 0;
					if (coef.getInputUnits().getName() != null) {
						inputUnitsId = seedObjectBuilder.build(34, coef.getInputUnits().getName(),
								coef.getInputUnits().getDescription());
					}
					int outputUnitsId = 0;
					if (coef.getOutputUnits().getName() != null) {
						outputUnitsId = seedObjectBuilder.build(34, coef.getOutputUnits().getName(),
								coef.getOutputUnits().getDescription());
					}

					container.setParent(channelLookupId);

					String s = SeedUtil.toSeedString(stage.getNumber().intValue(), coef, coef.getDenominator(),
							coef.getNumerator(), inputUnitsId, outputUnitsId);
					LOGGER.info(s);

					seedObjectBuilder.build(s);
					seedObjectBuilder.store();

				}

				if (stage.getFIR() != null) {

					FIR fir = stage.getFIR();
					LOGGER.info("\t\t\tAdding response blockette:61");

					int inputUnitsId = 0;
					if (fir.getInputUnits().getName() != null) {
						inputUnitsId = seedObjectBuilder.build(34, fir.getInputUnits().getName(),
								fir.getInputUnits().getDescription());
					}
					int outputUnitsId = 0;
					if (fir.getOutputUnits().getName() != null) {
						outputUnitsId = seedObjectBuilder.build(34, fir.getOutputUnits().getName(),
								fir.getOutputUnits().getDescription());
					}

					container.setParent(channelLookupId);

					String s = SeedUtil.toSeedString(stage.getNumber().intValue(), fir, inputUnitsId, outputUnitsId);
					LOGGER.info("\t\t" + s);

					seedObjectBuilder.build(s);
					seedObjectBuilder.store();
				}

				if (stage.getPolynomial() != null) {

					LOGGER.info("\t\t\tAdding response blockette:62");

					Polynomial polynomial = stage.getPolynomial();

					int inputUnitsId = 0;
					if (polynomial.getInputUnits().getName() != null) {
						inputUnitsId = seedObjectBuilder.build(34, polynomial.getInputUnits().getName(),
								polynomial.getInputUnits().getDescription());
					}
					int outputUnitsId = 0;
					if (polynomial.getOutputUnits().getName() != null) {
						outputUnitsId = seedObjectBuilder.build(34, polynomial.getOutputUnits().getName(),
								polynomial.getOutputUnits().getDescription());
					}

					container.setParent(channelLookupId);
					String s = SeedUtil.toSeedString(stage.getNumber().intValue(), polynomial, inputUnitsId,
							outputUnitsId);
					LOGGER.info("\t\t" + s);

					seedObjectBuilder.build(s);
					seedObjectBuilder.store();
				}
				if (stage.getResponseList() != null) {

					ResponseList rlt = stage.getResponseList();
					LOGGER.info("\t\t\tAdding response blockette:55");

					int inputUnitsId = 0;
					if (rlt.getInputUnits().getName() != null) {
						inputUnitsId = seedObjectBuilder.build(34, rlt.getInputUnits().getName(),
								rlt.getInputUnits().getDescription());
					}
					int outputUnitsId = 0;
					if (rlt.getOutputUnits().getName() != null) {
						outputUnitsId = seedObjectBuilder.build(34, rlt.getOutputUnits().getName(),
								rlt.getOutputUnits().getDescription());
					}

					container.setParent(channelLookupId);
					String s = SeedUtil.toSeedString(stage.getNumber().intValue(), rlt, inputUnitsId, outputUnitsId);
					LOGGER.info("\t\t" + s);

					seedObjectBuilder.build(s);
					seedObjectBuilder.store();
				}

				if (stage.getDecimation() != null) {

					Decimation decimation = stage.getDecimation();
					LOGGER.info("\t\t\tAdding response blockette:57");

					String s = SeedUtil.toSeedString(stage.getNumber().intValue(), decimation);
					LOGGER.info("\t\t" + s);

					seedObjectBuilder.build(s);
					seedObjectBuilder.store();
				}

				Gain gain = stage.getStageGain();
				if (gain != null) {
					LOGGER.info("\t\t\tAdding stage sensitivity:58");

					String s = SeedUtil.toSeedString(stage.getNumber().intValue(), gain);
					LOGGER.info("\t\t" + s);

					seedObjectBuilder.build(s);
					seedObjectBuilder.store();
				}
			}
		}

		if (channel.getResponse() != null && channel.getResponse().getInstrumentSensitivity() != null) {
			Sensitivity sensitivity = channel.getResponse().getInstrumentSensitivity();
			StringBuilder seedString = new StringBuilder();
			seedString.append("58|0|");
			seedString.append(0).append("|").append(SeedUtil.toSeedString(sensitivity));
			seedObjectBuilder.build(seedString.toString());
			seedObjectBuilder.store();
		}
	}

	public SeedExportBuilder getSeedExportBuilder(String mode) {
		SeedExportBuilder exportBuilder = new SeedExportBuilder(mode);
		return exportBuilder;
	}

}
