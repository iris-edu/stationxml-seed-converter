package edu.iris.dmc.station.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.PoleZero;
import edu.iris.dmc.fdsn.station.model.Response;
import edu.iris.dmc.fdsn.station.model.ResponseStage;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.seed.control.station.B052;
import edu.iris.dmc.seed.control.station.ResponseBlockette;
import edu.iris.dmc.seed.control.station.SeedResponseStage;
import edu.iris.dmc.station.util.SeedUtils;
import edu.iris.dmc.station.util.XmlUtils;

public class SeedXmlSeedTest {

	@Test
	public void caseOne() throws Exception {
		File source = new File(
				XmlToSeedDocumentConverterTest.class.getClassLoader().getResource("CU.VM_.dataless").getFile());

		Volume original = SeedUtils.load(source);

		FDSNStationXML document = SeedToXmlDocumentConverter.getInstance().convert(original);
		
		JAXBContext jContext = JAXBContext.newInstance(FDSNStationXML.class);
	    //creating the marshaller object
	    Marshaller marshallObj = jContext.createMarshaller();
	    //setting the property to show xml format output
	    marshallObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    //setting the values in POJO class

	    //calling the marshall method
	    marshallObj.marshal(document, System.out);
	    
	    

		Volume target = XmlToSeedDocumentConverter.getInstance().convert(document);
		assertNotNull(target);
		assertNotNull(target.getB050s());
		assertEquals(1, target.getB050s().size());

		B050 bcip = target.getB050s().get(0);
		assertEquals("BCIP", bcip.getStationCode());

		assertNotNull(bcip.getB052s());
		assertEquals(3, bcip.getB052s().size());

		B052 vmu = bcip.getB052s().get(0);
		assertEquals("VMU", vmu.getChannelCode());
		assertNotNull(vmu.getResponseStages());
		assertEquals(1, vmu.getResponseStages().size());
		assertNull(vmu.getResponseStage(0));
		assertNotNull(vmu.getResponseStage(1));
		List<ResponseBlockette> response = vmu.getResponseStage(1).getBlockettes();
		assertNotNull(response);
		assertEquals(1, response.size());
		assertEquals(62, response.get(0).getType());

		B052 vmv = bcip.getB052s().get(1);
		assertEquals("VMV", vmv.getChannelCode());
		assertNotNull(vmv.getResponseStages());
		assertEquals(1, vmv.getResponseStages().size());
		assertNull(vmv.getResponseStage(0));
		assertNotNull(vmv.getResponseStage(1));
		response = vmv.getResponseStage(1).getBlockettes();
		assertNotNull(response);
		assertEquals(1, response.size());
		assertEquals(62, response.get(0).getType());
		
		
		B052 vmw = bcip.getB052s().get(2);
		assertEquals("VMW", vmw.getChannelCode());

		assertNotNull(vmw.getResponseStages());
		assertEquals(1, vmw.getResponseStages().size());
		assertNull(vmw.getResponseStage(0));
		assertNotNull(vmw.getResponseStage(1));
		response = vmw.getResponseStage(1).getBlockettes();
		assertNotNull(response);
		assertEquals(1, response.size());
		assertEquals(62, response.get(0).getType());

	}
}
