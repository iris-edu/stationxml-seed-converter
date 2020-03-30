package edu.iris.dmc.station.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

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

public class MainTest_ContinueOnError {
	// This class test the main method and its outputs. 
    
	ByteArrayOutputStream errContentContinue = new ByteArrayOutputStream();


    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	


	@Test
	public void continueonError() throws Exception{
	     PrintStream errps2 = new PrintStream(errContentContinue);

	     System.setErr(errps2);
	     
		URL url = MainTest_ContinueOnError.class.getClassLoader().getResource("continueonerror");
		String[] args = new String[] {"-v", "--continue-on-error", url.getPath()};
		Application.main(args);

		
         System.setErr( new PrintStream( new FileOutputStream( FileDescriptor.out ) ) );
         String syserr = errContentContinue.toString().replaceAll( "\r", "" );
         errContentContinue.close();
         errps2.close();
         boolean content1  = syserr.toString().contains("[SEVERE] edu.iris.dmc.station.Application exitWithError");
         boolean content2  = syserr.toString().contains("[SEVERE] edu.iris.dmc.station.Application run: javax.xml.bind.UnmarshalException");
         boolean content3  = syserr.toString().contains("edu.iris.dmc.seed.SeedException: Value +35478.0 too big");
         assertTrue(content1);
	     
	     
	}
	
}
