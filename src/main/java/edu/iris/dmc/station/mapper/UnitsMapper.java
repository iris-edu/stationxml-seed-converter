package edu.iris.dmc.station.mapper;

import java.util.Objects;

import edu.iris.dmc.fdsn.station.model.Units;
import edu.iris.dmc.seed.control.dictionary.B034;
import edu.iris.dmc.unit.InvalidUnitException;
import edu.iris.dmc.unit.SimpleUnitFormat;

public class UnitsMapper extends AbstractMapper {

	public static Units map(B034 blockette) throws InvalidUnitException {
		if(blockette==null) {
			return null;
		}
		String name = blockette.getName();
		if (name == null) {
			throw new InvalidUnitException("Unit name is required and cannot be null");
		}
		
		Units units = SimpleUnitFormat.getInstance().parse(name);
		units.setDescription(blockette.getDescription());
		return units;
	}
	
	public static B034 map(Units units) throws InvalidUnitException {
		Objects.requireNonNull(units, "Units cannot be null");
		Objects.requireNonNull(units.getName(), "Unit name cannot be null");
		B034 b = new B034();
		b.setName(units.getName().toUpperCase());
		b.setDescription(units.getDescription());

		return b;
	}
}
