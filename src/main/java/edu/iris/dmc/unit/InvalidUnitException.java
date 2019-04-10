package edu.iris.dmc.unit;

import edu.iris.dmc.seed.SeedException;

public class InvalidUnitException extends SeedException{

	public InvalidUnitException(String message) {
		super(message);
	}
}
