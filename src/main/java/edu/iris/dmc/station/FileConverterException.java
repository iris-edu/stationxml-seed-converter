package edu.iris.dmc.station;

public class FileConverterException extends RuntimeException {

	private static final long serialVersionUID = -6125316393288240840L;

	private String file;

	public FileConverterException(String file, String msg) {
		super(msg);
		this.file = file;
	}

	public FileConverterException(Throwable t, String file) {
		super(t);
		this.file = file;
	}

	public String getFile() {
		return this.file;
	}
}
