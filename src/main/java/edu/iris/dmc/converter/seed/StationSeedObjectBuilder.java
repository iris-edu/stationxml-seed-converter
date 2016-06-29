package edu.iris.dmc.converter.seed;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import edu.iris.Fissures.seed.builder.SeedObjectBuilder;
import edu.iris.Fissures.seed.container.SeedObjectContainer;
import edu.iris.Fissures.seed.exception.BuilderException;
import edu.iris.dmc.converter.SeedConverionException;
import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Comment;
import edu.iris.dmc.fdsn.station.model.Equipment;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Response;
import edu.iris.dmc.fdsn.station.model.Sensitivity;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.fdsn.station.model.Units;

public class StationSeedObjectBuilder extends SeedObjectBuilder {

	private static final Logger LOGGER = Logger.getLogger(StationSeedObjectBuilder.class.getName());

	private Map<String, Integer> b33s = new HashMap<String, Integer>();
	private Map<String, Integer> b34s = new HashMap<String, Integer>();
	private Map<String, Integer> b31s = new HashMap<String, Integer>();
	private int dataFormatRef = -1;

	public StationSeedObjectBuilder() {
		super();
	}

	public StationSeedObjectBuilder(String diskFile) throws BuilderException {
		super(diskFile);
	}

	public int build(Station station) throws Exception {
		Network network = null;
		if (station.getNetwork() != null) {
			network = station.getNetwork();
		}
		int result = -1;
		System.out.println(":::" + network.getDescription());
		if (network.getDescription() != null) {
			result = build(33, network.getDescription());
		}

		String stationSeedString = SeedUtil.toSeedString(station.getNetwork().getCode(), station, result);
		LOGGER.info("Building B50: "+stationSeedString);
		result = build(stationSeedString);
		if (result < 0) {
			throw new SeedConverionException("Invalid content @: " + stationSeedString);
		}
		int stationLookupId = store();
		if (stationLookupId < 0) {
			throw new SeedConverionException("Cannot convert file; invalid content @: " + stationSeedString);
		}
		return stationLookupId;
	}

	public int build(Channel channel, int parentId) throws Exception {

		int sensitivityUnitRef = 0;

		Response response = channel.getResponse();

		if (response == null) {

		}

		Sensitivity sensitivity = response.getInstrumentSensitivity();
		if (sensitivity != null && sensitivity.getInputUnits() != null) {
			if (sensitivity.getInputUnits().getName() != null) {
				String unitName = sensitivity.getInputUnits().getName().trim();
				if (unitName.length() > 0) {
					sensitivityUnitRef = build(34, unitName, sensitivity.getInputUnits().getDescription());
				}
			}
		}

		int dataFormatRef = build(30, "Undefined data format");
		int instrumentRef = build(channel.getSensor(), parentId);
		int calibrationRef = build(channel.getCalibrationUnits(), parentId);

		String channelSeedString = SeedUtil.toSeedString(channel, dataFormatRef, instrumentRef, sensitivityUnitRef,
				calibrationRef);

		((SeedObjectContainer) getContainer()).setParent(parentId);
		int result = build(channelSeedString);
		if (result < 0) {
			throw new SeedConverionException("Invalid content to build@: " + channelSeedString);
		}
		result = store();
		if (result < 0) {
			throw new SeedConverionException("Invalid content to store@: " + channelSeedString);
		}
		return result;
	}

	public void build(Comment e, int lookupId) throws Exception {
		StringBuilder sb = new StringBuilder("51|0|");

		if (e.getBeginEffectiveTime() != null) {
			sb.append(SeedUtil.formatDate(e.getBeginEffectiveTime()));
		} else {
			sb.append("^");
		}

		sb.append("|");
		if (e.getEndEffectiveTime() != null) {
			sb.append(SeedUtil.formatDate(e.getEndEffectiveTime()));
		} else {
			sb.append("^");
		}
		sb.append("|");
		int code = build(31, "S", e.getValue());
		SeedObjectContainer container = (SeedObjectContainer) getContainer();
		container.setParent(lookupId);
		sb.append(code).append("|0");
		int result = build(sb.toString());
		if (result < -1) {
			throw new SeedConverionException("Error building B33:" + sb.toString());
		}
		result = store();
		if (result < -1) {
			throw new SeedConverionException("Error storing B33:" + sb.toString());
		}
	}

	public int build(Units unit, int lookupId) throws Exception {
		String calibrationUnit = "UNKNOWN";
		String calibrationUnitDescription = "";
		if (unit != null && unit.getName().trim().length() > 0) {
			calibrationUnit = unit.getName().trim();
			calibrationUnitDescription = unit.getDescription();
		}
		int calibrationUnitId = build(34, calibrationUnit, calibrationUnitDescription);
		return calibrationUnitId;
	}

	public int build(Equipment equipment, int lookupId) throws Exception {
		if (equipment == null) {

		}

		StringBuilder equipmentText = new StringBuilder();
		boolean addComma = false;
		if (equipment.getModel() != null) {
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

		String equipmentDescription = equipment.getDescription();

		String description = null;

		if (equipmentText != null) {
			description = equipmentText.toString();
		}

		int instrumentRef = build(33, description, equipmentDescription);
		return instrumentRef;
	}

	public int build(int type, String... items) throws Exception {
		LOGGER.info("Prepare blockette " + type);
		if (type != 30 && type != 33 && type != 31 && type != 34) {
			throw new SeedConverionException("Unsupported blockette type: " + type);
		}
		if (items == null) {
			throw new SeedConverionException("Error building B" + type + " description cannot be null or empty string");
		}
		StringBuilder sb = new StringBuilder();
		Integer key = null;
		if (30 == type) {
			if (dataFormatRef > 0) {
				return dataFormatRef;
			} else {
				String name = items[0];
				key = dataFormatRef = 1;
				sb.append("30|0|" + name + "|" + dataFormatRef + "|100|0|0");
			}

		} else if (33 == type) {
			String text = items[0];
			if (text.trim().isEmpty()) {
				throw new SeedConverionException(
						"Error building B" + type + " description cannot be null or empty string");
			}
			text = SeedUtil.toBaseCharacters(text);
			key = b33s.get(text);
			if (key == null) {
				key = b33s.size() + 1;
				if (key > 999) {
					throw new SeedConverionException(
							"B33:Field[3]=" + key + ": Code lookup key is too big, must be >[1000]");
				}
				b33s.put(text, key);
			} else {
				return key;
			}
			sb.append("33|0|").append(key).append("|").append(text).append("|");

		} else if (34 == type) {
			String name = items[0];
			if (name == null) {
				key = this.b34s.get(0);
				if (key == null) {
					key = Integer.valueOf(0);
					this.b34s.put("UKNOWN", key);
					sb.append("34|0|").append(0).append("|").append("UKNOWN").append("||");
				}
				return key;
			}
			name = name.trim().toUpperCase();

			String description = "";
			if (items.length > 1) {
				description = items[1];
			}

			if (this.b34s.containsKey(name)) {
				return this.b34s.get(name);
			} else {
				key = this.b34s.size() + 1;
				if (key > 999) {
					throw new SeedConverionException(
							"B34:Field[3]=" + this.b34s.size() + ": Code lookup key is too big, must be >[1000]");
				}
				this.b34s.put(name, key);
				sb.append("34|0|").append(key).append("|").append(name.trim().toUpperCase()).append("|");
				if (description != null && description.length() > 0) {
					sb.append(description);
				} else {
					sb.append("");
				}
				sb.append("|");
			}
		} else if (31 == type) {
			String classCode = items[0];
			String text = null;
			if (items.length > 1) {
				text = items[1];
			}
			if (classCode != null) {
				text = text + classCode;
			}

			key = this.b31s.get(text);

			if (key == null) {
				key = b31s.size() + 1;
				if (key > 999) {
					throw new SeedConverionException(
							"B31:Field[3]=" + key + ": Code lookup key is too big, must be >[1000]");
				}
				b31s.put(text, key);
			} else {
				return key;
			}
			sb.append("31|0|").append(key).append("|");
			if (classCode != null && !classCode.trim().isEmpty()) {
				sb.append(classCode);
			} else {
				sb.append("Z");
			}
			sb.append("|");

			if (text != null && text.length() > 0) {
				sb.append(text).append("|");
			} else {
				sb.append("^").append("|");
			}
			sb.append("0|");
		}

		LOGGER.info("Building: " + sb.toString());
		int result = build(sb.toString());
		if (result < -1) {
			throw new SeedConverionException("Error building B" + type + ":" + sb.toString());
		}
		result = store();
		if (result < -1) {
			throw new SeedConverionException("Error storing B" + type + ":" + sb.toString());
		}
		return key;
	}

}
