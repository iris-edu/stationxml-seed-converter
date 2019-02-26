package edu.iris.dmc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import edu.iris.dmc.station.StationUtils;
import edu.iris.dmc.station.util.StationIterator;

public class FileUtils {

	public static StationIterator stationIterator(File file) throws IOException {

		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (file.canRead() == false) {
				throw new IOException("File '" + file + "' cannot be read");
			}
		} else {
			throw new FileNotFoundException("File '" + file + "' does not exist");
		}
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			return StationUtils.stationIterator(inputStream);
		} catch (final IOException | RuntimeException ex) {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (final IOException e) {
				ex.addSuppressed(e);
			}
			throw ex;
		}
	}
}
