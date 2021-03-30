package edu.iris.dmc.station.mapper;

import java.util.ArrayList;
import java.util.List;

import edu.iris.dmc.fdsn.station.model.AngleType;
import edu.iris.dmc.fdsn.station.model.FloatNoUnitType;
import edu.iris.dmc.fdsn.station.model.Frequency;
import edu.iris.dmc.fdsn.station.model.ObjectFactory;
import edu.iris.dmc.fdsn.station.model.ResponseList;
import edu.iris.dmc.fdsn.station.model.ResponseListElement;
import edu.iris.dmc.seed.control.dictionary.B045;
import edu.iris.dmc.seed.control.station.B055;
import edu.iris.dmc.seed.control.station.B055.Response;
import edu.iris.dmc.seed.control.station.B057;

public class ResponseListMapper extends AbstractMapper {

	public static ResponseList map(B045 b) {
		ResponseList rType = new ObjectFactory().createResponseListType();

		for (edu.iris.dmc.seed.control.dictionary.B045.Response response : b.getResponses()) {
			ResponseListElement rlet = factory.createResponseListElementType();
			rlet.setFrequency(rlet.getFrequency());

			if (response.getFrequency() != null) {
				Frequency ft = factory.createFrequencyType();
				ft.setValue(response.getFrequency());
				rlet.setFrequency(rlet.getFrequency());
			}

			if (response.getAmplitude() != null) {
				FloatNoUnitType ft = factory.createFloatNoUnitType();
				ft.setValue(response.getAmplitude());
				ft.setMinusError(response.getAmplitudeError());
				ft.setPlusError(response.getAmplitudeError());
				rlet.setAmplitude(ft);
			}

			if (response.getPhaaeAngle() != null) {
				AngleType at = factory.createAngleType();
				at.setValue(response.getPhaaeAngle());
				at.setMinusError(response.getPhaseError());
				at.setPlusError(response.getPhaseError());
				rlet.setPhase(at);
			}

			rType.getResponseListElement().add(rlet);
		}
		return rType;
	}
	public static ResponseList map(B055 b) {

		ResponseList rType = new ObjectFactory().createResponseListType();

		for (edu.iris.dmc.seed.control.station.B055.Response response : b.getResponses()) {
			ResponseListElement rlet = factory.createResponseListElementType();
			//rlet.setFrequency(rlet.getFrequency());
			
			if (response.getFrequency() != null) {
				Frequency ft = factory.createFrequencyType();
				ft.setValue(response.getFrequency());
				rlet.setFrequency(ft);
			}

			if (response.getAmplitude() != null) {
				FloatNoUnitType ft = factory.createFloatNoUnitType();
				ft.setValue(response.getAmplitude());
				ft.setMinusError(response.getAmplitudeError());
				ft.setPlusError(response.getAmplitudeError());
				rlet.setAmplitude(ft);
			}

			if (response.getPhaaeAngle() != null) {
				AngleType at = factory.createAngleType();
				at.setValue(response.getPhaaeAngle());
				at.setMinusError(response.getPhaseError());
				at.setPlusError(response.getPhaseError());
				rlet.setPhase(at);
			}

			rType.getResponseListElement().add(rlet);
		}
		return rType;
	}
	
	public static B055 map(ResponseList r) {
		B055 b = new B055();

		List<Response> rlist = new ArrayList<Response>();
		for (ResponseListElement response : r.getResponseListElement()) {
			Response rlet =  b.new Response();

			if (response.getFrequency() != null) {
				rlet.setFrequency(response.getFrequency().getValue());
			}

			if (response.getAmplitude() != null) {
				rlet.setAmplitude(response.getAmplitude().getValue());
				try {
				Double minerror = response.getAmplitude().getMinusError();
				Double pluserror = response.getAmplitude().getPlusError();

				if(minerror < pluserror) {
				   rlet.setAmplitudeError(pluserror);
				}
				else {
				   rlet.setAmplitudeError(minerror);
				}
                }
                catch(Exception nullPointerException) {
                	rlet.setPhaseError(0.0);
                }
			}

			if (response.getPhase() != null) {
				rlet.setPhaaeAngle(response.getPhase().getValue());
                    try {
					Double minerror = response.getPhase().getMinusError();
					Double pluserror = response.getPhase().getPlusError();

					if(minerror < pluserror) {
					   rlet.setPhaseError(pluserror);
					}
					else{
					   rlet.setPhaseError(minerror);
					}
                    }
                    catch(Exception nullPointerException) {
                    	rlet.setPhaseError(0.0);
                    }

			}

			rlist.add(rlet);
		}
		b.setResponses(rlist);
		return b;
	}
}
