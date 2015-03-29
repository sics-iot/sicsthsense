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

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import se.sics.sicsthsense.jdbi.*;
import se.sics.sicsthsense.core.functions.*;

public class Trigger {
	@JsonProperty
	protected long id;
	protected long stream_id;
	@JsonProperty
	protected String url;
	@JsonProperty
	protected String payload;
	@JsonProperty
	protected String operator; // < > =
	@JsonProperty
	protected double operand;

	private final Logger logger = LoggerFactory.getLogger(Trigger.class);
	private StorageDAO storage = null;

  public Trigger() {
		this.storage = DAOFactory.getInstance();
	}
  public Trigger(Long id, Long stream_id, String url, String operator, double operand, String payload) {
		super();
		this.id        = id;
		this.url       = url;
		this.operand   = operand;
		this.payload   = payload;
		this.operator  = operator;
		this.stream_id = stream_id;
	}

	public void test(DataPoint dp) {
		double value = dp.getValue();
		if (">".equals(operator)) {
			if (value>operand) {perform();}
		} else if ("<".equals(operator)) {
			if (value<operand) {perform();}
		} else if ("=".equals(operator)) {
			if (value==operand) {perform();}
		} else if (">=".equals(operator)) {
			if (value>=operand) {perform();}
		} else if ("<=".equals(operator)) {
			if (value<=operand) {perform();}
		}
	}

	public void perform() {
		if (payload != null && !"".equals(payload)) {
			performPut();
			//performPost();
		} else {
			performGet();
		}
	}

	public void performGet() {
		String inputLine;
		HttpURLConnection con = null;
		logger.info("Performing Trigger: "+toString());
		try {
			URL urlobj = new URL(url);
			con = (HttpURLConnection)urlobj.openConnection();
			con.setRequestMethod("GET"); // optional default is GET
			con.setRequestProperty("User-Agent", "SICSthSense"); //add request header
		} catch (Exception e) { logger.error("Problem with URL: "+url);}

		try {
			int responseCode = con.getResponseCode();
			logger.info("Sending 'GET' request to URL : " + url+" Response: " + responseCode);

			BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) { response.append(inputLine); }
			in.close();
		} catch (Exception e) { logger.error("Network problem: "+e); }
	}
	public void performPost() {
		String inputLine;
		HttpURLConnection con = null;
		logger.info("Performing Trigger: "+toString());
		try {
			URL urlobj = new URL(url);
			con = (HttpURLConnection)urlobj.openConnection();
			con.setRequestMethod("POST"); // optional default is GET
			con.setRequestProperty("User-Agent", "SICSthSense"); //add request header
			con.setDoOutput(true);
		} catch (Exception e) { logger.error("Problem with URL: "+url);}

		try {
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(this.payload);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : "+url+" Payload: "+payload+" response: "+responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) { response.append(inputLine); }
			in.close();
		} catch (Exception e) { logger.error("Network problem: "+e); }
	}

	public void performPut() {
		String inputLine;
		HttpURLConnection con = null;
		logger.info("Performing Trigger: "+toString());
		try {
			URL urlobj = new URL(url);
			con = (HttpURLConnection)urlobj.openConnection();
			con.setRequestMethod("PUT"); // optional default is GET
			con.setRequestProperty("User-Agent", "SICSthSense"); //add request header
			con.setDoOutput(true);
		} catch (Exception e) { logger.error("Problem with URL: "+url);}

		try {
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(this.payload);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'PUT' request to URL : "+url+" Payload: "+payload+" response: "+responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) { response.append(inputLine); }
			in.close();
		} catch (Exception e) { logger.error("Network problem: "+e); }
	}

	public String toString() {
		return "Trigger: "+operator+" on "+operand+". Causing: "+url;
	}

	public long getId()												{ return id; }
	public long getStreamId()									{ return stream_id; }
	public String getUrl()										{ return url; }
	public String getOperator()								{ return operator; }
	public double getOperand()								{ return operand; }
	public String getPayload()								{ return payload; }

	public void setId(long id)								{ this.id = id; }
	public void setStreamId(long stream_id)		{ this.stream_id = stream_id; }
	public void setUrl(String url)						{ this.url = url; }
	public void setOperator(String operator)	{ this.operator = operator; }
	public void setOperand(double operand)		{ this.operand = operand; }
	public void setPayload(String payload)		{ this.payload = payload; }

}
