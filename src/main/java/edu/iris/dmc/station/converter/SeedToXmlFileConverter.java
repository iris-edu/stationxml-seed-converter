package edu.iris.dmc.station.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.station.FileConverterException;
import edu.iris.dmc.station.mapper.MetadataConverterException;

public class SeedToXmlFileConverter implements MetadataFileFormatConverter<File> {

	private static SeedToXmlFileConverter INSTANCE = new SeedToXmlFileConverter();

	private SeedToXmlFileConverter() {

	}

	public static MetadataFileFormatConverter<File> getInstance() {
		return INSTANCE;
	}

	@Override
	public void convert(File source, File target) throws FileConverterException, IOException {
		this.convert(source, target, null);

	}

	public void convert(InputStream source, OutputStream outputStream, Map<String, String> args) throws FileConverterException, IOException {
		try {
			Volume volume = IrisUtil.readSeed(source);
			FDSNStationXML document = SeedToXmlDocumentConverter.getInstance().convert(volume);
			marshal(document, outputStream);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void convert(File source, File target, Map<String, String> args)
			throws MetadataConverterException, IOException {
		try (FileInputStream fileInputStream = new FileInputStream(source);
				OutputStream fileOutputStream = new FileOutputStream(target)) {
			convert(fileInputStream, fileOutputStream,args);
		} catch (Exception e) {
			throw new FileConverterException(e, source.getPath());
		}
	}

	public void marshal(FDSNStationXML document, File file) throws IOException, JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(edu.iris.dmc.fdsn.station.model.ObjectFactory.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(document, file);
	}
	
    public void marshal(FDSNStationXML document, OutputStream stream) throws IOException, JAXBException {  
	    try {
    	JAXBContext jaxbContext = JAXBContext.newInstance(edu.iris.dmc.fdsn.station.model.ObjectFactory.class);
	    Marshaller marshaller = jaxbContext.createMarshaller();
	    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    DOMResult domResult = new DOMResult();
	    marshaller.marshal(document, domResult);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(new DOMSource(domResult.getNode()), new StreamResult(stream));
	     }catch(Exception e) {
	    	e.printStackTrace();
	     }
     }

}
