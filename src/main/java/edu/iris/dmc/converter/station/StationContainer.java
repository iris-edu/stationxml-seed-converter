package edu.iris.dmc.converter.station;

import java.util.ArrayList;
import java.util.List;

import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;

public class StationContainer {

	private List<Network> networks = new ArrayList<Network>();

	public StationContainer() {

	}

	public List<Network> getNetworks() {
		return networks;
	}

	public void merge(List<Network> networks) {
		for (Network n : networks) {
			this.add(n);
		}
	}

	public void add(Network network) {
		if(network==null){
			return;
		}
		int index = this.networks.indexOf(network);
		if (index < 0) {
			this.networks.add(network);
			return;
		}

		Network rn = this.networks.get(index);

		List<Station> rstations = rn.getStations();
		if(network.getStations()==null){
			return;
		}
		for (Station s : network.getStations()) {
			index = rstations.indexOf(s);
			if (index < 0) {
				s.setParent(rn);
				rn.getStations().add(s);
			} else {
				Station rstation = rstations.get(index);				
				
				for (Channel c : s.getChannels()) {
				//c.setStation(rstation);
					index = rstation.getChannels().indexOf(c);
					if (index < 0) {
						rstation.addChannel(c);
					}
				}
			}
		}

	}
}
