package edu.iris.dmc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

public class Copier {
	static Set<String> index = new HashSet<>();

	public static void main(String[] args) throws Exception {

		File folder = new File("/Users/Suleiman/names");
		File[] array = folder.listFiles();
		for (File f : array) {
			index.add(f.getName());
		}

		System.out.println(index.size());
		try {
			copy(new File("/Users/Suleiman/iris-dataless/"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copy(File source) throws IOException {
		if (source.isDirectory()) {
			File[] listOfFiles = source.listFiles();
			for (File f : listOfFiles) {
				copy(f);
			}
		}
		if (index.contains(source.getName())) {
			// System.out.println(source.getPath());
			Path temp = Files.copy(Paths.get(source.getPath()),
					Paths.get("/Users/Suleiman/loaded-dataless/" + source.getName()), StandardCopyOption.REPLACE_EXISTING);
		
		}
	}

}
