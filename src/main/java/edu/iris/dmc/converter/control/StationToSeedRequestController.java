package edu.iris.dmc.converter.control;

import static java.net.HttpURLConnection.HTTP_OK;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.iris.dmc.converter.IncompleteContentException;
import edu.iris.dmc.converter.station.StationContainer;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;

public class StationToSeedRequestController extends AbstractController {

	private static final Logger LOGGER = Logger.getLogger(StationToSeedRequestController.class.getName());
	public static int DEFAULT_READ_TIMEOUT_IN_MS = 180000;
	private StationToSeedConverter converter;

	public StationToSeedRequestController() throws JAXBException {
		this.converter = new StationToSeedConverter();
	}

	public void execute(List<String> resource, OutputStream outputStream) throws Exception {
		LOGGER.info("Processing " + resource.size() + " files");

		if (resource != null && !resource.isEmpty()) {
			StationContainer stationContainer = new StationContainer();
			int count = 1;

			for (String query : resource) {
				LOGGER.info("Processing " + query + " " + count + "/" + resource.size());

				List<Network> networks = null;
				try {
					if (query.startsWith("http")) {
						networks = fetch(query);
					} else if ("-".equals(query)) {
						networks = load(System.in);
					} else {
						InputStream is = new FileInputStream(query);
						networks = load(is);
						// assume file
					}
					LOGGER.info("Found " + networks.get(0));
					stationContainer.merge(networks);
				} catch (NoDataFoundException e) {
					// keep going...
				}
				count++;
			}

			List<Network> list = stationContainer.getNetworks();
			if (list == null || list.isEmpty()) {
				throw new NoDataFoundException("No stations found.");
			}

			try {
				this.converter.process(outputStream, stationContainer.getNetworks(), "");
			} catch (IncompleteContentException e) {
				throw new IncompleteContentException("Incomplete data");
			}

			outputStream.flush();
			return;
		}
	}

	public List<Network> load(InputStream inputStream) throws IOException {

		try {
			JAXBContext jc = JAXBContext.newInstance("edu.iris.dmc.fdsn.station.model");
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());

			FDSNStationXML root = (FDSNStationXML) unmarshaller.unmarshal(inputStream);
			if (root == null) {
				throw new IOException("Failed to marshal document.");
			}
			return root.getNetwork();

		} catch (JAXBException e) {
			IOException ioe = new IOException(e.getMessage());
			ioe.setStackTrace(e.getStackTrace());
			throw ioe;
		}

	}

	public List<Network> fetch(String url) throws NoDataFoundException, IOException {
		LOGGER.info("fetching(" + url + ")");

		URL u = new URL(url);
		String query = u.getQuery();
		String[] pairs = query.split("&");
		Map<String, String> queryKeyValue = new LinkedHashMap<String, String>();
		for (String pair : pairs) {
			pair = pair.trim();
			if (pair.length() == 0) {
				continue;
			}
			int idx = pair.indexOf("=");
			queryKeyValue.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
					URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		}

		List<Network> result = null;
		HttpURLConnection connection = null;
		InputStream inputStream = null;
		try {

			connection = (HttpURLConnection) u.openConnection();
			connection.setUseCaches(false);

			connection.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);
			connection.setRequestMethod("GET");

			connection.setRequestProperty("User-Agent", "FDSN-StationXML-Converter");

			connection.setRequestProperty("Accept", "application/xml");
			connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
			connection.connect();

			int responseCode = connection.getResponseCode();
			inputStream = responseCode != HTTP_OK ? connection.getErrorStream() : connection.getInputStream();
			if ("gzip".equals(connection.getContentEncoding())) {
				inputStream = new GZIPInputStream(inputStream);
			}
			switch (responseCode) {
			case 404:
				LOGGER.warning("No data Found for the GET request " + url + getString(inputStream));
				return null;
			case 204:
				LOGGER.warning("No data Found for the GET request " + url);
				throw new NoDataFoundException("No data found for: " + url);
			case 400:
				LOGGER.severe("An error occurred while making a GET request " + url + getString(inputStream));
				throw new IOException("Bad request parameter: " + getString(inputStream));
			case 500:
				LOGGER.severe("An error occurred while making a GET request " + url + getString(inputStream));
				throw new IOException(getString(inputStream));
			case 200:
				JAXBContext jc;
				try {
					jc = JAXBContext.newInstance(edu.iris.dmc.fdsn.station.model.ObjectFactory.class);
					Unmarshaller unmarshaller = jc.createUnmarshaller();
					FDSNStationXML root = (FDSNStationXML) unmarshaller.unmarshal(inputStream);
					if (root != null) {
						result = root.getNetwork();
					}
				} catch (JAXBException e) {
					throw new IOException(e);
				}

				break;
			default:
				throw new IOException(connection.getResponseMessage());
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	private String getString(InputStream inputStream) {
		StringBuilder builder = new StringBuilder();
		try (Scanner scanner = new Scanner(inputStream)) {
			scanner.useDelimiter("\\A");
			while (scanner.hasNext()) {
				builder.append(scanner.next());
			}
		}
		return builder.toString();
	}
}
