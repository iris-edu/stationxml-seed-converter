package edu.iris.dmc.station.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.Record;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.index.B011;
import edu.iris.dmc.seed.control.index.B011.Row;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.seed.control.station.B052;
import edu.iris.dmc.seed.control.station.ResponseBlockette;
import edu.iris.dmc.seed.record.StationRecord;
import edu.iris.dmc.seed.writer.SeedFileWriter;
import edu.iris.dmc.station.Application;
import edu.iris.dmc.station.mapper.SeedStringBuilder;

public class FileConverterRunner {

	@Test
	public static void main(String[] args) throws Exception{
		URL url = FileConverterRunner.class.getClassLoader().getResource("CU_ANWB_BH2.xml");
		args = new String[] {"-v","--input", url.getPath()};
		Application.main(args);
		
		
		url = FileConverterRunner.class.getClassLoader().getResource("dataless.CI.DJJB.061013");
		args = new String[] {"-v","--input", url.getPath()};
		Application.main(args);
		
	}

}
