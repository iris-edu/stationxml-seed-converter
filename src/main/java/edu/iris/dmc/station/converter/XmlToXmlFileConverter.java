package edu.iris.dmc.station.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Comment;
import edu.iris.dmc.fdsn.station.model.Equipment;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Polynomial;
import edu.iris.dmc.fdsn.station.model.ResponseStage;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.io.SeedFormatter;
import edu.iris.dmc.seed.BTime;
import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.DictionaryIndex;
import edu.iris.dmc.seed.SeedException;
import edu.iris.dmc.seed.Volume;
import edu.iris.dmc.seed.blockette.util.BlocketteItrator;
import edu.iris.dmc.seed.control.dictionary.B030;
import edu.iris.dmc.seed.control.dictionary.B031;
import edu.iris.dmc.seed.control.dictionary.B033;
import edu.iris.dmc.seed.control.dictionary.B034;
import edu.iris.dmc.seed.control.index.B010;
import edu.iris.dmc.seed.control.index.B011;
import edu.iris.dmc.seed.control.station.B050;
import edu.iris.dmc.seed.control.station.B051;
import edu.iris.dmc.seed.control.station.B052;
import edu.iris.dmc.seed.control.station.B053;
import edu.iris.dmc.seed.control.station.B054;
import edu.iris.dmc.seed.control.station.B057;
import edu.iris.dmc.seed.control.station.B058;
import edu.iris.dmc.seed.control.station.B059;
import edu.iris.dmc.seed.control.station.B061;
import edu.iris.dmc.seed.control.station.B062;
import edu.iris.dmc.seed.director.BlocketteDirector;
import edu.iris.dmc.seed.io.BlocketteOutputStream;
import edu.iris.dmc.seed.io.RecordInputStream;
import edu.iris.dmc.seed.io.SeedBufferedOutputStream;
import edu.iris.dmc.seed.writer.SeedFileWriter;
import edu.iris.dmc.station.ChannelCommentToBlocketteMapper;
import edu.iris.dmc.station.FileConverterException;
import edu.iris.dmc.station.mapper.ChannelBlocketteMapper;
import edu.iris.dmc.station.mapper.CoefficientsMapper;
import edu.iris.dmc.station.mapper.DecimationMapper;
import edu.iris.dmc.station.mapper.FirToBlocketteMapper;
import edu.iris.dmc.station.mapper.InstrumentSensitivityToBlocketteMapper;
import edu.iris.dmc.station.mapper.MetadataConverterException;
import edu.iris.dmc.station.mapper.PolesZerosMapper;
import edu.iris.dmc.station.mapper.PolynomialMapper;
import edu.iris.dmc.station.mapper.SensitivityToBlocketteMapper;
import edu.iris.dmc.station.mapper.StageGainToBlocketteMapper;
import edu.iris.dmc.station.mapper.StationBlocketteMapper;
import edu.iris.dmc.station.mapper.StationCommentToBlocketteMapper;
import edu.iris.dmc.station.mapper.UnitsMapper;
import edu.iris.dmc.station.util.StationIterator;

public class XmlToXmlFileConverter implements MetadataFileFormatConverter<File> {
	private final Logger logger = Logger.getLogger(XmlToXmlFileConverter.class.getName());
	private static XmlToXmlFileConverter INSTANCE = new XmlToXmlFileConverter();

	public static MetadataFileFormatConverter<File> getInstance() {
		return INSTANCE;
	}

	@Override
	public void convert(File source, File target) throws IOException {
		this.convert(source, target, null);
	}

	@Override
	public void convert(File source, File target, Map<String, String> args) throws IOException {
		// Volume.build must be in this convert method so b10 can be updated by user
		// input
		try (FileInputStream fileInputStream = new FileInputStream(source);
				OutputStream fileOutputStream = new FileOutputStream(target)) {
			    convert(fileInputStream, fileOutputStream, args);
		}catch (Exception e) {
			e.printStackTrace();
		}
    }

	public void convert(InputStream inputStream, OutputStream outputStream, Map<String, String> args) throws FileConverterException, IOException {
		try {
			FDSNStationXML document = IrisUtil.readXml(inputStream);
			document.setSchemaVersion(BigDecimal.valueOf(1.1));
			for (Network network : document.getNetwork()) {
				for (Station station : network.getStations()) {
					if (station.getChannels() != null) {
						for (Channel channel : station.getChannels()) {
							channel.getAny().clear();
							 if (channel.getResponse() != null) {
									List<ResponseStage> stages = channel.getResponse().getStage();
									if (stages != null) {
										for (ResponseStage stage : stages) {
											if (stage.getStageGain() != null) {
												if(stage.getPolynomial() != null) {
													if(stage.getStageGain().getValue()!=1) {
														throw new MetadataConverterException(
														"Blockette 58 in Network " +network.getCode() +" Station " + station.getCode() 
														+ " Channel " + channel.getCode() + " stage "+ stage.getNumber()+" is non-unity. "
														+ "This stage must be manually fixed before the file can be converted.");
													}else {		
													    stage.setStageGain(null);
													    }
													}
										         }
							                  }
							 
						                    }
							             }
					                  }


				           }
			          }
		         }

	marshal(document, outputStream);
	
		} catch (JAXBException | MetadataConverterException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
