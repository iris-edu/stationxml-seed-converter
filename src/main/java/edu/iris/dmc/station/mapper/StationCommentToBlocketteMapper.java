package edu.iris.dmc.station.mapper;

import edu.iris.dmc.fdsn.station.model.Comment;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.control.station.B051;
import edu.iris.dmc.station.util.TimeUtil;

public class StationCommentToBlocketteMapper {

	public static B051 map(Comment comment) throws SeedException {
		B051 b = new B051();
		b.setLevel(0);// set to zero for now;

		b.setStartTime(TimeUtil.toBTime(comment.getBeginEffectiveTime()));
		b.setEndTime(TimeUtil.toBTime(comment.getEndEffectiveTime()));
		b.setLevel(0);
		return b;

	}
}
