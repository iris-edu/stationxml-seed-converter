package edu.iris.dmc.station.mapper;

import edu.iris.dmc.fdsn.station.model.Filter;
import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.control.dictionary.B041;
import edu.iris.dmc.seed.control.dictionary.B042;
import edu.iris.dmc.seed.control.dictionary.B043;
import edu.iris.dmc.seed.control.dictionary.B044;
import edu.iris.dmc.seed.control.dictionary.B045;
import edu.iris.dmc.seed.control.dictionary.B046;
import edu.iris.dmc.seed.control.dictionary.B049;

public class FilterBuilder extends AbstractMapper {

	public static Filter build(Blockette b) throws Exception {

		switch (b.getType()) {
		case 41:
			return FirMapper.map((B041) b);
		case 42:
			return PolynomialMapper.map((B042) b);
		case 43:
			return PolesZerosMapper.map((B043) b);
		case 44:
			return CoefficientsMapper.map((B044) b);
		case 45:
			return ResponseListMapper.map((B045) b);
		case 46:
			return null;
		case 49:
			return PolynomialMapper.map((B049) b);
		default:
			return null;
		}

	}
}
