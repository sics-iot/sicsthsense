/*
 * Copyright (c) 2013, Swedish Institute of Computer Science
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of The Swedish Institute of Computer Science nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE SWEDISH INSTITUTE OF COMPUTER SCIENCE BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

/* Description:
 * TODO:
 * */
package se.sics.sicsthsense.core;

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
import se.sics.sicsthsense.core.Parser;
import se.sics.sicsthsense.model.ParseData;
import se.sics.sicsthsense.jdbi.StorageDAO;
 
public class Poller extends UntypedActor {
	private final Logger logger = LoggerFactory.getLogger(Poller.class);
	public long resourceId;
	public String url;
	private ObjectMapper mapper;
	private ParseData parsedata;
	private StorageDAO storage;
	private URL urlobj;
	private String inputLine;
	private List<Parser> parsers;

	public Poller(StorageDAO storage, ObjectMapper mapper, long resourceId, String url) throws MalformedURLException {
		this.resourceId=resourceId;
		this.storage = storage;
		this.url = url;
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		parsedata = new ParseData(mapper);
		rebuild();
	}

	// remake everything from the database (in case it has been changed)
	public void rebuild() throws MalformedURLException {
		logger.info("Making a poller for resource "+resourceId+" on url "+url);
		Resource resource = storage.findResourceById(resourceId);
		if (resource==null) {logger.error("Resource does not exist: "+resourceId); return; }
		this.url=resource.getPolling_url();
		if (this.url==null || this.url=="") {
			logger.error("Url not valid");
			return;
		}
		try {
			urlobj = new URL(url);
		} catch (Exception e) {
			//logger.error("Bad url: "+e);
			return;
		}
		parsers = storage.findParsersByResourceId(resourceId);
	}

	public void applyParsers(String data) {
		//logger.info("Applying all parsers to data: "+data);
		for (Parser parser: parsers) {
			//logger.info("Applying a parser "+parser.getInput_parser());
			try {
				parsedata.apply(parser,data);
			} catch (Exception e) {
				//logger.error("Parsing "+data+" failed!"+e);
				logger.error("Parsing data failed! "+e+"\nParser: "+parser+"\nData: "+data.substring(0,100));
			}
		}
	}
 
	@Override
  public void onReceive(Object message) throws Exception {
		//logger.info("Received String message: to probe: {}");
    if (message instanceof String) {
			if (message.equals("rebuild")) {
				rebuild();
			} else { // "probe"
				//logger.info("Received String message: to probe: {}", url);
				//getSender().tell(message, getSelf());
				if (urlobj==null) {return;}
				HttpURLConnection con = (HttpURLConnection)urlobj.openConnection();
				con.setRequestMethod("GET"); // optional default is GET
				con.setInstanceFollowRedirects(true);
				con.setRequestProperty("User-Agent", "SICSthSense"); //add request header
		 
				try {
					int responseCode = con.getResponseCode();
					logger.info("Sending 'GET' request to URL : " + url+" Response Code : " + responseCode);
			 
					BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
					StringBuffer response = new StringBuffer();
					while ((inputLine = in.readLine()) != null) { response.append(inputLine); }
					in.close();
			 
					storage.polledResource(resourceId,System.currentTimeMillis());
					//System.out.println(response.toString());
					applyParsers(response.toString());
				} catch (Exception e) {
					logger.error("Network problem: "+e+" URL: "+url);
				}
			}
    } else {
      unhandled(message);
		}
  }
}
