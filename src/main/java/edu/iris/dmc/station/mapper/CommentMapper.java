package edu.iris.dmc.station.mapper;

import edu.iris.dmc.fdsn.station.model.Comment;
import edu.iris.dmc.fdsn.station.model.ObjectFactory;
import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.control.station.B051;
import edu.iris.dmc.seed.control.station.B059;
import edu.iris.dmc.station.util.TimeUtil;

public class CommentMapper {
	private static ObjectFactory factory = new ObjectFactory();

	public static Comment buildForChannel(Blockette blockette) throws Exception {
		Comment comment = factory.createCommentType();
		if (blockette instanceof B059) {
			B059 b059 = (B059) blockette;
			if (b059.getStartTime() != null) {
				comment.setBeginEffectiveTime(TimeUtil.toCalendar(b059.getStartTime()));
			}
			if (b059.getEndTime() != null) {
				comment.setEndEffectiveTime(TimeUtil.toCalendar(b059.getEndTime()));
			}
			return comment;
		} else {
			throw new IllegalArgumentException("Only B059 accepted.");
		}
	}

	public static Comment buildForStation(Blockette blockette) throws Exception {
		Comment comment = factory.createCommentType();
		if (blockette instanceof B051) {
			B051 b051 = (B051) blockette;
			if (b051.getStartTime() != null) {
				comment.setBeginEffectiveTime(TimeUtil.toCalendar(b051.getStartTime()));
			}
			if (b051.getEndTime() != null) {
				comment.setEndEffectiveTime(TimeUtil.toCalendar(b051.getEndTime()));
			}
			return comment;
		} else {
			throw new IllegalArgumentException("Only B051 accepted.");
		}
	}
}
