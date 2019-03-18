package edu.iris.dmc.station.converter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.file.OpenOption;
import java.nio.file.Path;

import edu.iris.dmc.seed.Blockette;

public class SeedRecordWriter extends Writer {

	private Writer writer;

	private SeedRecordWriter(Writer writer) {
		this.writer = writer;
	}

	public static SeedRecordWriter newSeedRecordWriter(Path path, Charset cs, OpenOption... options)
			throws IOException {
		CharsetEncoder encoder = cs.newEncoder();

		Writer writer = new OutputStreamWriter(newOutputStream(path, options), encoder);
		return new SeedRecordWriter(new BufferedWriter(writer));
	}

	public static OutputStream newOutputStream(Path path, OpenOption... options) throws IOException {
		return path.getFileSystem().provider().newOutputStream(path, options);
	}

	public SeedRecordWriter newRecord() {
		return this;
	}
	
	public void endRecord() {
		
	}

	public SeedRecordWriter append(Blockette b) {
		return this;
	}
	
	public void write(Blockette b) {

	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void flush() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}
}
