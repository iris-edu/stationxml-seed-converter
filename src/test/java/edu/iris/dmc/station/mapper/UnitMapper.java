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
import edu.iris.dmc.fdsn.station.model.PolesZeros;
import edu.iris.dmc.fdsn.station.model.Response;
import edu.iris.dmc.fdsn.station.model.ResponseStage;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.seed.BlocketteFactory;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.dictionary.B034;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.seed.control.station.B052;
import edu.iris.dmc.seed.control.station.B053;
import edu.iris.dmc.seed.control.station.SeedResponseStage;
import edu.iris.dmc.station.converter.SeedToXmlDocumentConverter;
import edu.iris.dmc.station.converter.XmlToSeedDocumentConverterTest;

public class UnitMapper {


@SuppressWarnings("null")
@Test
public void unit_casing() throws Exception {
	File source = new File(
			XmlToSeedDocumentConverterTest.class.getClassLoader().getResource("unitConvertion.dataless").getFile());

	Volume original = IrisUtil.readSeed(source);
	 B053 pz = null;
	 B034 inputUnits =null;
	 List <B053> pzlist = null;
	 B050 datasta = original.getB050s().get(0);
	 List <B052> datachan = datasta.getB052s();
	 
	 B052 chan =  datachan.get(0);
	 
	 pz = (B053) chan.getResponseStages().get(0).getBlockettes().get(0);
	 inputUnits = (B034) original.getDictionaryBlockette(34, pz.getSignalInputUnit());
	 assertTrue(inputUnits.getName().contentEquals("PA"));	   
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
    res = cha.get(0).getResponse();
    PolesZeros pzxml = res.getStage().get(0).getPolesZeros();
	assertTrue(pzxml.getInputUnits().getName().contentEquals("Pa"));	   

    	   

    }

}

