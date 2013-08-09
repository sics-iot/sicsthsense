package com.sics.sicsthsense.core;

import java.util.List;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import javax.net.ssl.HttpsURLConnection;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.sics.sicsthsense.core.Parser;
import com.sics.sicsthsense.jdbi.StorageDAO;
 
public class Poller extends UntypedActor {
  LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	public long resourceId;
	public String url;
	private StorageDAO storage;
	private URL urlobj;
	private String inputLine;
	private List<Parser> parsers;

	public Poller(StorageDAO storage, long resourceId, String url) throws MalformedURLException {
		this.resourceId=resourceId;
		this.url=url;
		this.storage = storage;
		urlobj = new URL(url);
		parsers = storage.findParsersByResourceId(resourceId);
	}

	public void applyParsers(String data) {

		for (Parser parser: parsers) {
			parser.apply(data);
		}
	}
 
	@Override
  public void onReceive(Object message) throws Exception {
    if (message instanceof String) {
      log.info("Received String message: to probe: {}", url);
      //getSender().tell(message, getSelf());

			HttpURLConnection con = (HttpURLConnection)urlobj.openConnection();
			con.setRequestMethod("GET"); // optional default is GET
			con.setRequestProperty("User-Agent", "SICSthSense"); //add request header
	 
			int responseCode = con.getResponseCode();
			System.out.print("Sending 'GET' request to URL : " + url);
			System.out.println(" Response Code : " + responseCode);
	 
			BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) { response.append(inputLine); }
			in.close();
	 
			System.out.println(response.toString());
    } else
      unhandled(message);
  }
}
