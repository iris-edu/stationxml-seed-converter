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
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Response;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.seed.BlocketteFactory;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.seed.control.station.B052;
import edu.iris.dmc.seed.control.station.B053;
import edu.iris.dmc.seed.control.station.B054;
import edu.iris.dmc.station.converter.SeedToXmlDocumentConverter;
import edu.iris.dmc.station.converter.XmlToSeedDocumentConverterTest;

public class CoefficientsMapperTest {


@SuppressWarnings("null")
@Test
public void normilization_frequency() throws Exception {
	File source = new File(
			XmlToSeedDocumentConverterTest.class.getClassLoader().getResource("UW.LON.dataless.20200204125001").getFile());

	Volume original = IrisUtil.readSeed(source);
	 B054 coeff = null;
	 B050 datasta = original.getB050s().get(0);
	 List <B052> datachan = datasta.getB052s();
	 try {
	 for (int i2=0; i2 < datachan.size(); i2++) {
		// if(datachan.get(i2).getResponseStages().get(0).getBlockettes().get(0)==null) {
	
		 //}else {
		 // pzlist.add(i2, (B053) datachan.get(i2).getResponseStages().get(0).getBlockettes().get(0));
		// }
		  if(datachan.get(i2).getChannelCode().matches("SNE")) {
			  coeff = (B054) datachan.get(i2).getResponseStages().get(2).getBlockettes().get(0);
			  System.out.println(coeff.getNumerators());
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
    
    ///TODO FInd the null pointer and remove it. 
    List<Channel> cha  = sta.getChannels();
    System.out.println(cha.size());
    int n=0;
    try {
    for (int i=0; i < cha.size(); i++) {
    	if(cha.get(i).getCode().matches("SNE")) {
    	  res = cha.get(i).getResponse();
    	   
      }
    }
	}catch (Exception e) {
         /* This is a generic Exception handler which means it can handle
          * all the exceptions. This will execute if the exception is not
          * handled by previous catch blocks.
          */
         System.out.println("Exception occurred second loop");
     }
    //assertEquals(res.getStage().get(1).getCoefficients().getDenominator().get(0).getValue(), coeff.getDenominators().get(0).getValue(), 0.1);
    assertEquals(res.getStage().get(2).getCoefficients().getNumerator().get(0).getValue(), coeff.getNumerators().get(0).getValue(), 0.0001);
    assertEquals(res.getStage().get(2).getCoefficients().getNumerator().get(0).getPlusError(), coeff.getNumerators().get(0).getError(), 0.00001);


}
}
