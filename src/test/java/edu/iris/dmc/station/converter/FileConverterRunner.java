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

public class FileConverterRunner {
	// This class test the main method and its outputs. 
    
	ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	@Test
	public void mainTest() throws Exception{
	     PrintStream ps = new PrintStream(outContent);
	     PrintStream errps = new PrintStream(errContent);

	     System.setOut(ps);
	     System.setErr(errps);
	     
		URL url = FileConverterRunner.class.getClassLoader().getResource("CU_ANWB_BH2.xml");
		String[] args = new String[] {"-v","--input", url.getPath()};
		Application.main(args);
		
        System.setOut( new PrintStream( new FileOutputStream( FileDescriptor.out ) ) );
        String stringtest = outContent.toString().replaceAll( "\r", "" );
		
        System.setErr( new PrintStream( new FileOutputStream( FileDescriptor.out ) ) );
        String syserr = errContent.toString().replaceAll( "\r", "" );
         outContent.close();
         errContent.close();
         errps.close();
         ps.close();
         
	     boolean content1  = syserr.toString().contains("[INFO  ] edu.iris.dmc.station.Application convert: Input file:");
	     boolean content2  = syserr.toString().contains("[INFO  ] edu.iris.dmc.station.Application convert: Input file is formatted as StationXml");
	     boolean content3  = syserr.toString().contains("[INFO  ] edu.iris.dmc.station.Application convert: Output file:");
	     boolean content4  = syserr.toString().contains("\n");

	     assertTrue(content1==true);
	     assertTrue(content2==true);
	     assertTrue(content3==true);
	     assertTrue(content4==true);
	     
	     
	}
	
	@Test
	public void help() throws Exception{
		//URL url = FileConverterRunner.class.getClassLoader().getResource("CU_ANWB_BH2.xml");
	    PrintStream ps = new PrintStream(outContent);
	    System.setOut(ps);
		String[] args = new String[] {"-h"};
		exit.expectSystemExitWithStatus(0);
        exit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                System.setOut( new PrintStream( new FileOutputStream( FileDescriptor.out ) ) );
                String sysout = outContent.toString().replaceAll( "\r", "" );
                ps.close();
       	        boolean content1  = sysout.toString().contains("   --help or -h         : print this message");
       	        boolean content2  = sysout.toString().contains("   --continue-on-error  : prints exceptions to stdout and processes next file");
       	        boolean content3  = sysout.toString().contains("Version");
       	        assertTrue(content1==true);
       	        assertTrue(content2==true);
       	        assertTrue(content3==true);

            }
 
           });
 		Application.main(args);	
			
	}

}
