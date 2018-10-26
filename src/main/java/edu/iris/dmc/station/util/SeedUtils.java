package edu.iris.dmc.station.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.blockette.util.BlocketteItrator;
import edu.iris.dmc.seed.builder.BlocketteBuilder;
import edu.iris.dmc.seed.director.BlocketteDirector;

public class SeedUtils {

	public static Volume load(File file) throws Exception {
		try (final FileInputStream inputStream = new FileInputStream(file)) {
			return load(inputStream);
		}
	}

	public static Volume load(InputStream inputStream) throws Exception {

		BlocketteDirector director = new BlocketteDirector(new BlocketteBuilder());
		BlocketteItrator iterator = director.process(inputStream);

		Volume volume = new Volume();
		while (iterator.hasNext()) {
			Blockette blockette = iterator.next();
			volume.add(blockette);
		}
		return volume;
	}
}
