package edu.iris.dmc.fdsn.station.model;

public interface Filter {

	//public Integer getType();
	public Units getInputUnits();
	public Units getOutputUnits();
	public String getResourceId();
	public String getName();
	public String getDescription();
}
