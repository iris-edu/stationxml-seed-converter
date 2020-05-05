package edu.iris.dmc.station.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.ResponseStage;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.dictionary.B031;
import edu.iris.dmc.seed.control.dictionary.B033;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.seed.control.station.B051;
import edu.iris.dmc.seed.control.station.B059;
import edu.iris.dmc.seed.control.station.ResponseBlockette;
import edu.iris.dmc.seed.control.station.Stage;
import edu.iris.dmc.station.converter.XmlToSeedFileConverter;
import edu.iris.dmc.station.mapper.MetadataConverterException;

public class XmlToXmlFileConverterTest {


	
	@Test
	public void ereaseStorageFormat() throws Exception {

		File xml = new File(XmlToXmlFileConverterTest.class.getClassLoader().getResource("StorageFormatTest.xml").getFile());
		FDSNStationXML documentOG = IrisUtil.readXml(xml);
		assertTrue((documentOG.getSchemaVersion().doubleValue()==1.0));
		for (Network network : documentOG.getNetwork()) {
			for (Station station : network.getStations()) {
				if (station.getChannels() != null) {
					for (Channel channel : station.getChannels()) {				 
						if(channel.getAny()!=null) {
							 System.out.println(channel.getAny().size());
							 assertTrue(channel.getAny().size()==1);
							 for(Object element : channel.getAny()) {
								assertTrue(element.toString().contains("StorageFormat"));
							 }
						 }
					}
				}
			}
		}
		File convertedXML = new File("convertedxml");
		XmlToXmlFileConverter.getInstance().convert(xml, convertedXML);
		FDSNStationXML document = IrisUtil.readXml(convertedXML);		
		assertTrue((document.getSchemaVersion().doubleValue()==1.1));		
		for (Network network : document.getNetwork()) {
			for (Station station : network.getStations()) {
				if (station.getChannels() != null) {
					for (Channel channel : station.getChannels()) {
						if(channel.getAny()!=null) {
							 assertTrue(channel.getAny().size()==0);
						 }
				}
			}
		 }

		}
	}
	
	@Test
	public void B62B58Format() throws Exception {

		File xml = new File(XmlToXmlFileConverterTest.class.getClassLoader().getResource("B62B58Unity.xml").getFile());
		FDSNStationXML documentOG = IrisUtil.readXml(xml);
		assertTrue((documentOG.getSchemaVersion().doubleValue()==1.0));
		Network network = documentOG.getNetwork().get(0);
		Station station = network.getStations().get(0); 
		Channel channel = station.getChannels().get(0); 
		ResponseStage stage = channel.getResponse().getStage().get(0);
		assertTrue(stage.getStageGain() != null);
		assertTrue(stage.getPolynomial() != null);
					 
		File convertedXML = new File("convertedxml");
		XmlToXmlFileConverter.getInstance().convert(xml, convertedXML);
		FDSNStationXML document = IrisUtil.readXml(convertedXML);		
		assertTrue((document.getSchemaVersion().doubleValue()==1.1));		
		Network network2 = document.getNetwork().get(0);
		Station station2 = network2.getStations().get(0); 
		Channel channel2 = station2.getChannels().get(0); 
		ResponseStage stage2 = channel2.getResponse().getStage().get(0);
		assertTrue(stage2.getStageGain() == null);
		assertTrue(stage2.getPolynomial() != null);

		


	}
	
	
}
