package edu.iris.dmc.station;

import javax.xml.datatype.DatatypeConfigurationException;

import edu.iris.dmc.fdsn.station.model.Comment;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.control.station.B059;
import edu.iris.dmc.station.util.TimeUtil;

public class ChannelCommentToBlocketteMapper {

	public static B059 map(Comment comment) throws SeedException {
		B059 b059 = new B059();
		b059.setLevel(0);// set to zero for now;
		try {
			b059.setStartTime(TimeUtil.toBTime(comment.getBeginEffectiveTime()));
			b059.setEndTime(TimeUtil.toBTime(comment.getEndEffectiveTime()));
			b059.setLevel(0);
			return b059;
		} catch (DatatypeConfigurationException e) {
			throw new SeedException(e);
		}
	}
}
