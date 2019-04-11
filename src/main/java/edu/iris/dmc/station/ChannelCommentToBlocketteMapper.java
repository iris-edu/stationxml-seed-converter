package edu.iris.dmc.station;


import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.fdsn.station.model.Comment;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.control.station.B059;

public class ChannelCommentToBlocketteMapper {

	public static B059 map(Comment comment) throws SeedException {
		B059 b059 = new B059();
		b059.setLevel(0);// set to zero for now;

		b059.setStartTime(IrisUtil.toBTime(comment.getBeginEffectiveTime()));
		b059.setEndTime(IrisUtil.toBTime(comment.getEndEffectiveTime()));
		b059.setLevel(0);
		return b059;

	}
}
