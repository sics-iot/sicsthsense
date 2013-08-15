package com.sics.sicsthsense.core;

import java.util.List;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig;
import com.sics.sicsthsense.core.Parser;
import com.sics.sicsthsense.jdbi.StorageDAO;
 
public class Poller extends UntypedActor {
  //LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private final Logger logger = LoggerFactory.getLogger(Poller.class);
	public long resourceId;
	public String url;
	private ObjectMapper mapper;
	private StorageDAO storage;
	private URL urlobj;
	private String inputLine;
	private List<Parser> parsers;

	public Poller(StorageDAO storage, long resourceId, String url) throws MalformedURLException {
		this.resourceId=resourceId;
		this.storage = storage;
		mapper = new ObjectMapper(); // can reuse, share globally
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);                 
		rebuild();
	}

	// remake everything from the database (in case it has been changed)
	public void rebuild() throws MalformedURLException {
		logger.info("Making a poller for resource "+resourceId+" on url "+url);
		Resource resource = storage.findResourceById(resourceId);
		if (resource==null) {logger.error("Resource does not exist: "+resourceId); return; }
		this.url=resource.getPolling_url();
		urlobj = new URL(url);
		parsers = storage.findParsersByResourceId(resourceId);

		for (Parser parser: parsers) {
			// give each parser access to global entities
			//logger.info(parser.toString());
			parser.setStorage(storage);
			parser.setMapper(mapper);
		}
	}

	public void applyParsers(String data) {
		logger.info("Applying all parsers to data: "+data);
		for (Parser parser: parsers) {
			//logger.info("applying a parser "+parser.getInput_parser());
			try {
				parser.apply(data);
			} catch (Exception e) {
				logger.error("Parsing "+data+" failed!"+e);
			}
		}
	}
 
	@Override
  public void onReceive(Object message) throws Exception {
    if (message instanceof String) {
			if (message.equals("rebuild")) {
				rebuild();
			} else { // "probe"
				logger.info("Received String message: to probe: {}", url);
				//getSender().tell(message, getSelf());

				HttpURLConnection con = (HttpURLConnection)urlobj.openConnection();
				con.setRequestMethod("GET"); // optional default is GET
				con.setRequestProperty("User-Agent", "SICSthSense"); //add request header
		 
				try {
					int responseCode = con.getResponseCode();
					System.out.print("Sending 'GET' request to URL : " + url);
					System.out.println(" Response Code : " + responseCode);
			 
					BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
					StringBuffer response = new StringBuffer();

					while ((inputLine = in.readLine()) != null) { response.append(inputLine); }
					in.close();
			 
					//System.out.println(response.toString());
					applyParsers(response.toString());
				} catch (Exception e) {
					logger.error("Network problem: "+e);
				}

			}
    } else
      unhandled(message);
  }
}
