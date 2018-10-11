package edu.iris.dmc.fdsn.station.model;


public enum RestrictedStatus {

	OPEN("open"),
    CLOSED("closed"),
    PARTIAL("partial");
	
	private final String value;
	
    RestrictedStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RestrictedStatus fromValue(String v) {
        for (RestrictedStatus c: RestrictedStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
