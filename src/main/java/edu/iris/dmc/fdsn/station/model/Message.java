package edu.iris.dmc.fdsn.station.model;

import java.util.ArrayList;
import java.util.List;


public class Message {

	private FDSNStationXML root;

	public Message() {
	}

	public Message(Object obj) {
		if (obj instanceof FDSNStationXML) {
			FDSNStationXML baseType = (FDSNStationXML) obj;
			this.root = baseType;
		}
	}

	public List<Network> getNetworks() {
		return this.root.getNetwork();
	}

}
