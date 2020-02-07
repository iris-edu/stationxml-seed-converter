package edu.iris.dmc.station.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
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
import edu.iris.dmc.station.converter.SeedToXmlDocumentConverter;
import edu.iris.dmc.station.converter.XmlToSeedDocumentConverterTest;

public class NormalizationMapper {


@Test
public void normilization_frequency() throws Exception {
	File source = new File(
			XmlToSeedDocumentConverterTest.class.getClassLoader().getResource("UW.LON.dataless.20200204125001").getFile());

	Volume original = IrisUtil.readSeed(source);
	B053 pz = null;
	 B050 datasta = original.getB050s().get(0);
	 List <B052> datachan = datasta.getB052s();
	 for (int i2=1; i2 < datachan.size(); i2++) {
		  if(datachan.get(i2).getChannelCode().matches("SNE")) {
			  pz = (B053) datachan.get(i2).getResponseStages().get(0).getBlockettes().get(0);
			  System.out.println(pz.getNormalizationFactor());
		  }
	 }
		  

	FDSNStationXML document = SeedToXmlDocumentConverter.getInstance().convert(original);
	
	
	
	JAXBContext jContext = JAXBContext.newInstance(FDSNStationXML.class);
    //creating the marshaller object
    Marshaller marshallObj = jContext.createMarshaller();
    //setting the property to show xml format output
    marshallObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    //setting the values in POJO class

    //calling the marshall method
    //marshallObj.marshal(document, System.out);
    Network net = document.getNetwork().get(0);
    Station sta = net.getStations().get(0);
    Response res = null;
    List<Channel> cha  = sta.getChannels();
    for (int i=1; i < cha.size(); i++) {
      if(cha.get(i).getCode().matches("SNE")) {
    	  res = cha.get(i).getResponse();
    	  
      }
    }
    assertEquals(res.getStage().get(0).getPolesZeros().getNormalizationFactor().doubleValue(), pz.getNormalizationFactor(), 0.1);
}
}
