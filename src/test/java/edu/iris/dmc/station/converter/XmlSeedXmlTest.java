package edu.iris.dmc.station.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.PoleZero;
import edu.iris.dmc.fdsn.station.model.Response;
import edu.iris.dmc.fdsn.station.model.ResponseStage;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.station.util.XmlUtils;

public class XmlSeedXmlTest {

	@Test
	public void caseOne() throws Exception {
		File source = new File(
				XmlToSeedDocumentConverterTest.class.getClassLoader().getResource("CU_ANWB_BH2.xml").getFile());

		final FDSNStationXML original = XmlUtils.load(source);
		Volume volume = XmlToSeedDocumentConverter.getInstance().convert(original);

		final FDSNStationXML target = SeedToXmlDocumentConverter.getInstance().convert(volume);
		assertNotNull(target);
		assertNotNull(target.getNetwork());
		assertEquals(1, target.getNetwork().size());

		Network cu = target.getNetwork().get(0);
		assertNotNull(cu.getStations());
		assertEquals(1, cu.getStations().size());

		Station anwb = cu.getStations().get(0);

		assertNotNull(anwb);
		assertEquals(2, anwb.getChannels().size());

		Channel bh2 = anwb.getChannels().get(0);

		Response response = bh2.getResponse();

		assertNotNull(response);
		assertNotNull(response.getStage());
		assertEquals(3, response.getStage().size());

		assertNotNull(response.getInstrumentSensitivity());

		ResponseStage stage1 = response.getStage().get(0);

		assertNotNull(stage1.getPolesZeros());

		assertNotNull(stage1.getPolesZeros().getInputUnits());
		assertEquals("M/S", stage1.getPolesZeros().getInputUnits().getName());
		assertNotNull(stage1.getPolesZeros().getOutputUnits());
		assertEquals("V", stage1.getPolesZeros().getOutputUnits().getName());

		assertEquals("LAPLACE (RADIANS/SECOND)", stage1.getPolesZeros().getPzTransferFunctionType());
		assertEquals(4.92256E7, stage1.getPolesZeros().getNormalizationFactor(), 000001);
		assertEquals(0.0500000, stage1.getPolesZeros().getNormalizationFrequency().getValue(), 0.000001);

		assertNotNull(stage1.getPolesZeros().getZero());
		assertEquals(2, stage1.getPolesZeros().getZero().size());

		assertNotNull(stage1.getPolesZeros().getPole());
		assertEquals(5, stage1.getPolesZeros().getPole().size());

		PoleZero pz = stage1.getPolesZeros().getPole().get(0);

		assertNotNull(pz.getReal());
		assertEquals(0.000219135, pz.getReal().getMinusError(), 0.000001);

		assertNotNull(pz.getImaginary());
		assertEquals(0.000239435, pz.getImaginary().getMinusError(), 0.000001);

		pz = stage1.getPolesZeros().getPole().get(1);
		assertNotNull(pz.getReal());
		assertEquals(0.000219135, pz.getReal().getMinusError(), 0.000001);

		assertNotNull(pz.getImaginary());
		assertEquals(0.000239435, pz.getImaginary().getMinusError(), 0.000001);

		pz = stage1.getPolesZeros().getPole().get(2);
		assertNotNull(pz.getReal());
		assertEquals(0.0, pz.getReal().getMinusError(), 0.000001);

		assertNotNull(pz.getImaginary());
		assertEquals(0.0, pz.getImaginary().getMinusError(), 0.000001);

	}
}
