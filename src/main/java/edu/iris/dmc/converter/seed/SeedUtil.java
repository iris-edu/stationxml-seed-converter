package edu.iris.dmc.converter.seed;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Coefficients;
import edu.iris.dmc.fdsn.station.model.Decimation;
import edu.iris.dmc.fdsn.station.model.FIR;
import edu.iris.dmc.fdsn.station.model.Gain;
import edu.iris.dmc.fdsn.station.model.PoleZero;
import edu.iris.dmc.fdsn.station.model.PolesZeros;
import edu.iris.dmc.fdsn.station.model.Polynomial;
import edu.iris.dmc.fdsn.station.model.Polynomial.Coefficient;
import edu.iris.dmc.fdsn.station.model.ResponseList;
import edu.iris.dmc.fdsn.station.model.ResponseListElement;
import edu.iris.dmc.fdsn.station.model.Sensitivity;
import edu.iris.dmc.fdsn.station.model.Station;

public class SeedUtil {

	public static String toSeedString(String netCode, Station epoch, int networkIdentifierLookup) {
		StringBuilder seedString = new StringBuilder("50|0|").append(epoch.getCode()).append("|")
				.append(epoch.getLatitude().getValue()).append("|").append(epoch.getLongitude().getValue()).append("|")
				.append(epoch.getElevation().getValue()).append("|");

		int numberOfChannels = 0;
		if (epoch.getChannels() != null && !epoch.getChannels().isEmpty()) {
			numberOfChannels = epoch.getChannels().size();
		}
		seedString.append(numberOfChannels).append("|0|");

		if (epoch.getSite().getName() == null || epoch.getSite().getName().trim().length() == 0) {
			seedString.append("^").append("|");
		} else {
			seedString.append(epoch.getSite().getName()).append("|");
		}

		// add network identifier
		seedString.append(networkIdentifierLookup).append("|");

		seedString.append("3210").append("|").append("10").append("|");

		seedString.append(SeedUtil.formatDate(epoch.getStartDate())).append("|");

		if (epoch.getEndDate() != null) {
			seedString.append(SeedUtil.formatDate(epoch.getEndDate()));
		} else {
			seedString.append("^");
		}
		if (netCode.length() == 1) {
			netCode = netCode + " ";
		}
		seedString.append("|N|").append(netCode).toString();
		return seedString.toString();
	}

	public static String formatDate(XMLGregorianCalendar xmlCal) {
		if (xmlCal == null) {
			return null;
		}
		if (xmlCal.getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
			xmlCal.setTimezone(0);
		}

		SimpleDateFormat fmt = new SimpleDateFormat("yyyy,DDD,HH:mm:ss");
		fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date d = xmlCal.toGregorianCalendar().getTime();

		String s = fmt.format(d);
		return s;
	}

	public static String toSeedString(Channel epoch, int dataFormatIdentifier, int xinstrumentIdentifier,
			int sensitivituUnitIdentifier, int calibrationUbitIdentifier) {

		StringBuilder sBuilder = new StringBuilder("52|0|");
		String location = "";
		if (epoch.getLocationCode() == null) {

		} else if (epoch.getLocationCode().trim().isEmpty()) {
			location = "  ";
		} else {
			location = epoch.getLocationCode();
		}
		sBuilder.append(location).append("|");

		sBuilder.append(epoch.getCode()).append("|");
		sBuilder.append("0|");

		if (xinstrumentIdentifier < 0) {
			sBuilder.append("^");
		} else {
			sBuilder.append(xinstrumentIdentifier);
		}

		sBuilder.append("|^|");
		if (sensitivituUnitIdentifier < 0) {
			sBuilder.append("^|");
		} else {
			sBuilder.append(sensitivituUnitIdentifier).append("|");
		}
		if (calibrationUbitIdentifier < 0) {
			sBuilder.append("^|");
		} else {
			sBuilder.append(calibrationUbitIdentifier).append("|");
		}
		sBuilder.append(epoch.getLatitude().getValue()).append("|");
		sBuilder.append(epoch.getLongitude().getValue()).append("|");

		if (epoch.getElevation() != null) {
			sBuilder.append(epoch.getElevation().getValue()).append("|");
		} else {
			sBuilder.append("0|");
		}

		if (epoch.getDepth() != null) {
			sBuilder.append(epoch.getDepth().getValue()).append("|");
		} else {
			sBuilder.append("0|");
		}

		double azimuth=0.0;
		if (epoch.getAzimuth() != null) {
			if(epoch.getDip()!=null){
				if(epoch.getDip().getValue()==90 || epoch.getDip().getValue()==-90){
					if (epoch.getAzimuth().getValue() == 0) {
						azimuth=360;
					} else {
						azimuth=epoch.getAzimuth().getValue();
					}
				}
			}else{
				azimuth=epoch.getAzimuth().getValue();
			}	
		}
		
		sBuilder.append(azimuth).append("|");

		if (epoch.getDip() != null) {
			sBuilder.append(epoch.getDip().getValue()).append("|");
		} else {
			sBuilder.append("0|");
		}

		sBuilder.append(dataFormatIdentifier).append("|12|");

		if (epoch.getSampleRate() != null) {
			sBuilder.append(epoch.getSampleRate().getValue()).append("|");
		} else {
			sBuilder.append("0|");
		}

		if (epoch.getClockDrift() != null) {
			sBuilder.append(epoch.getClockDrift().getValue()).append("|");
		} else {
			sBuilder.append("0|");
		}

		sBuilder.append("0|");

		if (epoch.getType() == null || epoch.getType().size() == 0) {
			sBuilder.append("^");
		} else {
			StringBuilder sb = new StringBuilder();

			for (String s : epoch.getType()) {
				if ("CONTINUOUS".equalsIgnoreCase(s)) {
					sb.append("C");
				} else if ("GEOPHYSICAL".equalsIgnoreCase(s)) {
					sb.append("G");
				} else if ("TRIGGERED".equalsIgnoreCase(s)) {
					sb.append("T");
				} else if ("HEALTH".equalsIgnoreCase(s)) {
					sb.append("H");
				} else if ("WEATHER".equalsIgnoreCase(s)) {
					sb.append("W");
				} else if ("FLAG".equalsIgnoreCase(s)) {
					sb.append("F");
				} else if ("SYNTHESIZED".equalsIgnoreCase(s)) {
					sb.append("S");
				} else if ("INPUT".equalsIgnoreCase(s)) {
					sb.append("I");
				} else if ("EXPERIMENTAL".equalsIgnoreCase(s)) {
					sb.append("E");
				} else if ("MAINTENANCE".equalsIgnoreCase(s)) {
					sb.append("M");
				} else if ("BEAM".equalsIgnoreCase(s)) {
					sb.append("B");
				} else {
					sb.append("^");
				}
			}
			sBuilder.append(sb.toString());
		}
		sBuilder.append("|");

		if (epoch.getStartDate() != null) {
			sBuilder.append((SeedUtil.formatDate(epoch.getStartDate()))).append("|");
		} else {
			sBuilder.append("^|");
		}
		if (epoch.getEndDate() != null) {
			sBuilder.append(SeedUtil.formatDate(epoch.getEndDate())).append("|");
		} else {
			sBuilder.append("^|");
		}
		sBuilder.append("N");
		return sBuilder.toString();

	}

	public static String toSeedString(int stage, PolesZeros polesZeros, int inputUnitsId, int outputUnitsId) {
		String transferFunction = polesZeros.getPzTransferFunctionType();
		if ("LAPLACE (RADIANS/SECOND)".equals(transferFunction)) {
			transferFunction = "A";

		} else if ("LAPLACE (HERTZ)".equals(transferFunction)) {
			transferFunction = "B";
		} else if ("DIGITAL (Z-TRANSFORM)".equals(transferFunction)) {
			transferFunction = "D";
		} else {
			transferFunction = "^";
		}

		StringBuilder seedString = new StringBuilder();
		seedString.append("53|0|").append(transferFunction).append("|");
		seedString.append(stage).append("|");

		if (inputUnitsId > -1) {
			seedString.append(inputUnitsId).append("|");
		} else {
			seedString.append("^|");
		}

		if (outputUnitsId > -1) {
			seedString.append(outputUnitsId).append("|");
		} else {
			seedString.append("^|");
		}

		if (polesZeros.getNormalizationFactor() == 0) {
			seedString.append(1.0).append("|");
		} else {
			seedString.append(polesZeros.getNormalizationFactor()).append("|");
		}
		if (polesZeros.getNormalizationFrequency() != null) {
			seedString.append(polesZeros.getNormalizationFrequency().getValue()).append("|");
		} else {
			seedString.append(0).append("|");
		}

		List<PoleZero> zeros = polesZeros.getZero();
		if (zeros == null || zeros.size() == 0) {
			seedString.append(0).append("|0|0|0|0|");
		} else {
			seedString.append(zeros.size()).append("|");

			for (PoleZero zero : zeros) {
				if (zero.getReal() == null) {
					seedString.append(0).append("|");
				} else {
					seedString.append(zero.getReal().getValue()).append("|");
				}
				if (zero.getImaginary() == null) {
					seedString.append(0).append("|");
				} else {
					seedString.append(zero.getImaginary().getValue()).append("|");
				}
				if (zero.getReal() != null && zero.getReal().getMinusError() != null) {
					seedString.append(zero.getReal().getMinusError()).append("|");
				} else {
					seedString.append(0).append("|");
				}

				if (zero.getImaginary() != null && zero.getImaginary().getMinusError() != null) {
					seedString.append(zero.getImaginary().getMinusError()).append("|");
				} else {
					seedString.append(0).append("|");
				}
			}
		}

		List<PoleZero> poles = polesZeros.getPole();
		if (poles == null || poles.size() == 0) {
			seedString.append(0).append("|0|0|0|0|");
		} else {
			seedString.append(poles.size()).append("|");
			for (PoleZero pole : poles) {
				if (pole.getReal() == null) {
					seedString.append(0).append("|");
				} else {
					seedString.append(pole.getReal().getValue()).append("|");
				}

				if (pole.getImaginary() == null) {
					seedString.append(0).append("|");
				} else {
					seedString.append(pole.getImaginary().getValue()).append("|");
				}

				if (pole.getReal().getMinusError() != null) {
					seedString.append(pole.getReal().getMinusError()).append("|");
				} else {
					seedString.append(0).append("|");
				}

				if (pole.getImaginary().getMinusError() != null) {
					seedString.append(pole.getImaginary().getMinusError()).append("|");
				} else {
					seedString.append(0).append("|");
				}
			}
		}
		return seedString.toString();

	}

	public static String toSeedString(int stage, Coefficients ct,
			List<edu.iris.dmc.fdsn.station.model.Float> denominators,
			List<edu.iris.dmc.fdsn.station.model.Float> numerators, int inputUnitsId, int outputUnitsId) {
		StringBuilder seedString = new StringBuilder();
		String transferFunction = null;
		if ("ANALOG (RAD/SEC)".equals(ct.getCfTransferFunctionType())) {
			transferFunction = "A";

		} else if ("ANALOG (HZ)".equals(ct.getCfTransferFunctionType())) {
			transferFunction = "B";
		} else if ("DIGITAL".equals(ct.getCfTransferFunctionType())) {
			transferFunction = "D";
		} else {
			transferFunction = "^";
		}

		seedString.append("54|0|").append(transferFunction).append("|");
		seedString.append(stage).append("|");

		if (inputUnitsId > -1) {
			seedString.append(inputUnitsId).append("|");
		} else {
			seedString.append("^|");
		}

		if (outputUnitsId > -1) {
			seedString.append(outputUnitsId).append("|");
		} else {
			seedString.append("^|");
		}

		if (numerators != null && numerators.size() > 0) {
			seedString.append(numerators.size()).append("|");
			for (int i = 0; i < numerators.size(); i++) {
				edu.iris.dmc.fdsn.station.model.Float f = numerators.get(i);
				seedString.append(f.getValue()).append("|");
				seedString.append(f.getMinusError());
				seedString.append("|");
			}
		} else {
			seedString.append(0).append("|0|0|");
		}

		if (denominators != null && denominators.size() > 0) {
			seedString.append(denominators.size()).append("|");
			for (edu.iris.dmc.fdsn.station.model.Float f : denominators) {
				seedString.append(f.getValue()).append("|").append(f.getMinusError()).append("|");
			}
		} else {
			seedString.append(0).append("|0|0|");
		}
		return seedString.toString();
	}

	public static String toSeedString(int stage, ResponseList rlt, int inputUnitsId, int outputUnitsId) {
		StringBuilder seedString = new StringBuilder();
		seedString.append("55|0|");
		seedString.append(stage).append("|");

		if (inputUnitsId > -1) {
			seedString.append(inputUnitsId).append("|");
		} else {
			seedString.append("^");
		}

		if (outputUnitsId > -1) {
			seedString.append(outputUnitsId).append("|");
		} else {
			seedString.append("^");
		}

		List<ResponseListElement> rlet = rlt.getResponseListElement();
		if (rlet != null && rlet.size() > 0) {
			seedString.append(rlet.size()).append("|");
			for (ResponseListElement e : rlet) {
				if (e.getFrequency() != null) {
					seedString.append(e.getFrequency().getValue()).append("|");
				} else {
					seedString.append("0|");
				}
				if (e.getAmplitude() != null) {
					seedString.append(e.getAmplitude().getValue()).append("|");
					seedString.append(e.getAmplitude().getMinusError());
					seedString.append("|");
				} else {
					seedString.append("0|0|");
				}

				if (e.getPhase() != null) {
					seedString.append(e.getPhase().getValue()).append("|");
					seedString.append(e.getPhase().getMinusError());
					seedString.append("|");
				} else {
					seedString.append("0|0|");
				}
			}
		} else {
			seedString.append("0|0|0|0|0|0|");
		}

		return seedString.toString();
	}

	/*
	 * public static String toSeedString(int stage, GenericResponse gr, int
	 * inputUnitsId, int outputUnitsId) { StringBuilder seedString = new
	 * StringBuilder(); seedString.append("55|0|");
	 * seedString.append(stage).append("|");
	 * 
	 * seedString.append(inputUnitsId).append("|");
	 * seedString.append(outputUnitsId).append("|");
	 * 
	 * seedString = new StringBuilder(); seedString.append("56|0|");
	 * seedString.append(stage).append("|").append(inputUnitsId).append("|");
	 * seedString.append(outputUnitsId).append("|");
	 * 
	 * 
	 * return seedString.toString(); }
	 */
	public static String toSeedString(int stage, Decimation decimation) {
		StringBuilder seedString = new StringBuilder();
		seedString.append("57|0|");
		seedString.append(stage).append("|");
		if (decimation.getInputSampleRate() != null) {
			seedString.append(decimation.getInputSampleRate().getValue()).append("|");
		} else {
			seedString.append(0).append("|");
		}

		if (decimation.getFactor() != null) {
			seedString.append(decimation.getFactor().intValue()).append("|");
		} else {
			seedString.append(0).append("|");
		}

		if (decimation.getOffset() != null) {
			seedString.append(decimation.getOffset().intValue()).append("|");
		} else {
			seedString.append(0).append("|");
		}

		if (decimation.getDelay() != null) {
			seedString.append(decimation.getDelay().getValue()).append("|");
		} else {
			seedString.append(0).append("|");
		}

		if (decimation.getCorrection() != null) {
			seedString.append(decimation.getCorrection().getValue()).append("|");
		} else {
			seedString.append(0).append("|");
		}

		return seedString.toString();
	}

	public static String toSeedString(int stage, Gain sensitivity) {
		StringBuilder seedString = new StringBuilder();
		seedString.append("58|0|");
		seedString.append(stage).append("|");

		seedString.append(sensitivity.getValue()).append("|");

		if (sensitivity.getFrequency() > 0) {
			seedString.append(sensitivity.getFrequency()).append("|");
		} else {
			seedString.append(0).append("|");
		}

		seedString.append(0).append("|");

		return seedString.toString();
	}

	public static String toSeedString(int stage, FIR fir, int inputUnitsId, int outputUnitsId) {
		StringBuilder seedString = new StringBuilder();
		seedString.append("61|0|");
		seedString.append(stage).append("|");
		if (fir.getName() != null) {
			seedString.append(fir.getName());
		} else {
			seedString.append("^");
		}
		seedString.append("|");

		if (fir.getSymmetry() != null) {
			if ("NONE".equalsIgnoreCase(fir.getSymmetry())) {
				seedString.append("A|");
			} else if ("ODD".equalsIgnoreCase(fir.getSymmetry())) {
				seedString.append("B|");
			} else if ("EVEN".equalsIgnoreCase(fir.getSymmetry())) {
				seedString.append("C|");
			} else {
				seedString.append("Z|");
			}

		} else {
			seedString.append("Z|");
		}

		if (inputUnitsId > -1) {
			seedString.append(inputUnitsId).append("|");
		} else {
			seedString.append("^|");
		}

		if (outputUnitsId > -1) {
			seedString.append(outputUnitsId).append("|");
		} else {
			seedString.append("^|");
		}

		List<FIR.NumeratorCoefficient> n = fir.getNumeratorCoefficient();
		if (n != null && n.size() > 0) {
			seedString.append(n.size()).append("|");
			for (FIR.NumeratorCoefficient cof : n) {
				seedString.append(cof.getValue()).append("|");
			}
		} else {
			seedString.append(0).append("|0|");
		}
		return seedString.toString();
	}

	public static String toSeedString(int stage, Polynomial polynomial, int inputUnitsId, int outputUnitsId) {
		String approximationType = polynomial.getApproximationType();
		if ("MACLAURIN".equals(approximationType)) {
			approximationType = "M";

		} else {
			approximationType = "^";
		}

		StringBuilder seedString = new StringBuilder();
		seedString.append("62|0|").append("P").append("|");
		seedString.append(stage).append("|");

		if (inputUnitsId > -1) {
			seedString.append(inputUnitsId).append("|");
		} else {
			seedString.append("^|");
		}

		if (outputUnitsId > -1) {
			seedString.append(outputUnitsId).append("|");
		} else {
			seedString.append("^|");
		}

		seedString.append(approximationType).append("|");

		seedString.append("B|");
		seedString.append("|").append(polynomial.getFrequencyLowerBound().getValue()).append("|");
		seedString.append("|").append(polynomial.getFrequencyUpperBound().getValue()).append("|");
		seedString.append("|").append(polynomial.getApproximationLowerBound().doubleValue()).append("|");
		seedString.append("|").append(polynomial.getApproximationUpperBound().doubleValue()).append("|");
		seedString.append("|").append(0).append("|");

		List<Coefficient> cl = polynomial.getCoefficient();

		if (cl == null || cl.size() == 0) {
			seedString.append(0).append("|0|0|0|");
		} else {
			seedString.append(cl.size()).append("|");
			for (Coefficient c : cl) {
				seedString.append(c.getValue()).append("|");

				seedString.append(c.getMinusError()).append("|");
			}
		}
		return seedString.toString();
	}

	public static String toSeedString(Sensitivity sensitivity) {
		StringBuilder sBuilder = new StringBuilder();
		if (sensitivity == null) {
			sBuilder.append(0).append("|");
			sBuilder.append(0).append("|0|0|0|^");
			return sBuilder.toString();
		}
		sBuilder.append(sensitivity.getValue()).append("|");
		sBuilder.append(sensitivity.getFrequency()).append("|0|0|0|^");
		return sBuilder.toString();
	}

	public static XMLGregorianCalendar dateTimeToXMLGregorianCalendar(Date date) throws DatatypeConfigurationException {
		// DatatypeFactory dataTypeFactory = DatatypeFactory.newInstance();
		// XMLGregorianCalendar xcal =
		// dataTypeFactory.newXMLGregorianCalendar(dateTime.toGregorianCalendar());
		// xcal.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		// xcal.setTimezone();
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

		return xcal;
	}

	public static String wildCardToRegex(String wildcard) {
		StringBuffer s = new StringBuffer(wildcard.length());
		s.append('^');
		for (int i = 0, is = wildcard.length(); i < is; i++) {
			char c = wildcard.charAt(i);
			switch (c) {
			case '*':
				s.append(".*");
				break;
			case '?':
				s.append(".");
				break;
			// escape special regexp-characters
			case '(':
			case ')':
			case '[':
			case ']':
			case '$':
			case '^':
			case '.':
			case '{':
			case '}':
			case '|':
			case '\\':
				s.append("\\");
				s.append(c);
				break;
			default:
				s.append(c);
				break;
			}
		}
		s.append('$');
		return (s.toString());
	}

	public static final String toBaseCharacters(final String sText) {
		if (sText == null || sText.length() == 0)
			return sText;

		final char[] chars = sText.toCharArray();

		final int iSize = chars.length;

		final StringBuilder sb = new StringBuilder(iSize);

		for (int i = 0; i < iSize; i++) {
			String sLetter = new String(new char[] { chars[i] });

			sLetter = Normalizer.normalize(sLetter, Normalizer.Form.NFD);

			try {
				byte[] bLetter = sLetter.getBytes("UTF-8");

				sb.append((char) bLetter[0]);
			} catch (UnsupportedEncodingException e) {
				// the encoding is surely valid
			}
		}

		return sb.toString();
	}
}
