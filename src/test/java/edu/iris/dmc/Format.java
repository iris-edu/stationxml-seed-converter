package edu.iris.dmc;

import java.text.DecimalFormat;

public class Format {

	public static void main(String[] args) {
		System.out.println(format(64000.0, "0.0000E-00"));
		System.out.println(format(0.00000064, "0.0000E-00"));

		System.out.println(format(0.000039062, "-0.0000E-00"));

	}

	public static String format(double d, String format) {
		String text = null;

		String pattern = format;
		boolean signed = false;
		if (pattern.startsWith("-")) {
			pattern = pattern.substring(1);
			signed = true;
		}

		if (pattern.contains("E-")) {
			pattern = pattern.replace("E-", "E");
			DecimalFormat df = new DecimalFormat(pattern);
			text = df.format(d);
			System.out.println(text + "     " + pattern);
			if (!text.contains("E-")) {
				text = text.replaceAll("E", "E+");
			}
		}
		if (d >= 0 && signed) {
			text=" "+text;
		}
		
		return text;
	}

}
