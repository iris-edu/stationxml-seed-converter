package edu.iris.dmc.converter.control;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import edu.iris.Fissures.seed.builder.ObjectBuilder;
import edu.iris.Fissures.seed.builder.SeedObjectBuilder;
import edu.iris.Fissures.seed.container.SeedObjectContainer;
import edu.iris.Fissures.seed.director.SeedImportDirector;
import edu.iris.dmc.converter.station.StationFactory;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.ObjectFactory;

public class SeedToStationRequestController extends AbstractController {

	private static final Logger LOGGER = Logger.getLogger(SeedToStationRequestController.class.getName());

	// private String sender;
	// private String source = "IRIS DMC";
	// private String module;
	// private boolean formatted = false;

	public SeedToStationRequestController() {
	}

	@Override
	public void execute(List<String> queries, OutputStream outputStream) throws Exception {
		LOGGER.info("DEBUG: Processing " + queries.size() + " files");

		int count = 1;

		ObjectFactory factory = new ObjectFactory();
		FDSNStationXML root = factory.createRootType();
		root.setSchemaVersion(new BigDecimal(1.0));

		if (this.properties.get("source") != null) {
			root.setSource((String) this.properties.get("source"));
		}

		if (this.properties.get("sender") != null) {
			root.setSender((String) this.properties.get("sender"));
		}
		if (this.properties.get("module") != null) {
			root.setModule((String) this.properties.get("module"));
		}
		root.setModuleURI("http://www.iris.edu/fdsnstationconverter");

		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		root.setCreated(date2);

		ObjectBuilder seedBuilder = new SeedObjectBuilder();

		SeedImportDirector seedDirector = new SeedImportDirector(seedBuilder);
		Collection<Network> networks = new ArrayList<Network>();
		for (String query : queries) {
			LOGGER.info("DEBUG: Processing " + query + " " + count + "/" + queries.size());
			count++;
			if ("-".equals(query)) {
				seedDirector.construct(System.in);
			} else {
				if (query.startsWith("http")) {
					throw new IOException("Url not supported");
				}
				InputStream is = new FileInputStream(query);
				seedDirector.construct(is);
			}

			SeedObjectContainer container = (SeedObjectContainer) seedBuilder.getContainer();
			container.iterate();

			Collection<Network> nets = StationFactory.getInstance().buildNetwork(container, true);
			if (nets != null) {
				networks.addAll(nets);
			}
		}

		LOGGER.info("DEBUG: merging...");

		for (Network n : networks) {
			root.merge(n);
		}

		JAXBContext jc = JAXBContext.newInstance("edu.iris.dmc.fdsn.station.model");

		Marshaller m = jc.createMarshaller();
		if (this.properties.get("format") != null && "true".equals(this.properties.get("format"))) {
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		}
		m.marshal(root, outputStream);
	}

}
