package edu.iris.dmc.station.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.EOFException;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Distance;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Latitude;
import edu.iris.dmc.fdsn.station.model.Longitude;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Response;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.seed.BlocketteFactory;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.seed.control.station.B052;
import edu.iris.dmc.seed.control.station.B053;
import edu.iris.dmc.station.converter.SeedToXmlDocumentConverter;
import edu.iris.dmc.station.converter.XmlToSeedDocumentConverterTest;

public class ElevationLatitudeMapper {


@SuppressWarnings("null")
@Test
public void normilization_frequency() throws Exception {
	File source = new File(
			XmlToSeedDocumentConverterTest.class.getClassLoader().getResource("UW.LON.dataless.20200204125001").getFile());

	Volume original = IrisUtil.readSeed(source);
	 double elevdata = 1;
	 double latdata =1;
	 double londata = 1; 
	 
	 B050 datasta = original.getB050s().get(0);
	 List <B052> datachan = datasta.getB052s();
	 try {
	 for (int i2=0; i2 < datachan.size(); i2++) {
		// if(datachan.get(i2).getResponseStages().get(0).getBlockettes().get(0)==null) {
	
		 //}else {
		 // pzlist.add(i2, (B053) datachan.get(i2).getResponseStages().get(0).getBlockettes().get(0));
		// }
		  if(datachan.get(i2).getChannelCode().matches("SNE")) {
			  elevdata =  datachan.get(i2).getElevation();
			  latdata = datachan.get(i2).getLatitude();
			  londata = datachan.get(i2).getLongitude();
			 // System.out.println(pz.getNormalizationFactor());
		  }
	 }
	 
	 }catch (Exception e) {
         /* This is a generic Exception handler which means it can handle
          * all the exceptions. This will execute if the exception is not
          * handled by previous catch blocks.
          */
         System.out.println("Exception occurred first loop");
      }		  
	FDSNStationXML document = SeedToXmlDocumentConverter.getInstance().convert(original);
	
	
	
	JAXBContext jContext = JAXBContext.newInstance(FDSNStationXML.class);
    //creating the marshaller object
    Marshaller marshallObj = jContext.createMarshaller();
    //setting the property to show xml format output
    marshallObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

    Network net = document.getNetwork().get(0);
    Station sta = net.getStations().get(0);
    Response res = null;
    Distance elevxml =null;
    Latitude latxml=null;
    Longitude lonxml=null;
    
    ///TODO FInd the null pointer and remove it. 
    List<Channel> cha  = sta.getChannels();
    System.out.println(cha.size());
    int n=0;
    try {
    for (int i=0; i < cha.size(); i++) {
    	if(cha.get(i).getCode().matches("SNE")) {
			  elevxml = cha.get(i).getElevation();
			  latxml = cha.get(i).getLatitude();
			  lonxml = cha.get(i).getLongitude();
    	   
      }
    }
	}catch (Exception e) {
         /* This is a generic Exception handler which means it can handle
          * all the exceptions. This will execute if the exception is not
          * handled by previous catch blocks.
          */
         System.out.println("Exception occurred second loop");
     }
    //System.out.println(elevdata);
    //System.out.println(latxml.getValue());
    //System.out.println(lonxml.getValue());
    assertEquals(elevdata, elevxml.getValue(), 0.001);
    assertEquals(latdata, latxml.getValue(), 0.001);
    assertEquals(londata, lonxml.getValue(), 0.001);


    //assertEquals(Double.toString(pz.getNormalizationFactor()).length(), 9);
}
}
