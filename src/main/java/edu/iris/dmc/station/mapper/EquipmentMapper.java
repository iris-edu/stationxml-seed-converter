package edu.iris.dmc.station.mapper;

import edu.iris.dmc.fdsn.station.model.Equipment;
import edu.iris.dmc.seed.control.dictionary.B033;

public class EquipmentMapper {

	public static B033 map(Equipment e) {

		if (e == null) {
			return null;
		}
		StringBuilder equipmentText = new StringBuilder();
		boolean addComma = false;

		if (e.getModel() != null) {
			if (addComma) {
				equipmentText.append(",");
			}
			equipmentText.append(e.getModel());
			addComma = true;
		}

		if (e.getDescription() != null) {
			if (addComma) {
				equipmentText.append(",");
			}
			equipmentText.append(e.getDescription());
		}

		if (e.getType() != null) {
			if (addComma) {
				equipmentText.append(",");
			}
			equipmentText.append(e.getType());
		}
		B033 b033 = new B033();
		b033.setDescription(equipmentText.toString());
		return b033;
	}
}
