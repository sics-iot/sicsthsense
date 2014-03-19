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

import java.util.Date;
import java.net.HttpURLConnection;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.sicsthsense.*;
import se.sics.sicsthsense.jdbi.*;

//@Table(name = "resource_log", uniqueConstraints = { @UniqueConstraint(columnNames = { "resource_id", "is_poll" }) })
public class ResourceLog {
	private static final int MAX_LENGTH = 4 * 1024, BODY_MAX_LENGTH = 8 * 1024;

	public long id;
	public long resourceId;
	public long creationTimestamp;
	public long responseTimestamp;
	public boolean parsedSuccessfully = false;
	public boolean isPoll = false;
	public String body = "";
	public String method = "";
	public String host = "";
	public String uri = "";
	public String headers = "";
	public String message = "";
	private int version;
	public String timestamp = "";
	// Poll roundtrip time
	public String responseTime = "";

	private final Logger logger = LoggerFactory.getLogger(ResourceLog.class);
	private StorageDAO storage = null;

	public ResourceLog() {
		StorageDAO storage = DAOFactory.getInstance();
	}
	public ResourceLog(Resource resource) {
	}
	// deprecated
	public ResourceLog(long id, long resourceId, long creationTimestamp,
			long responseTimestamp, boolean parsedSuccessfully, boolean isPoll,
			String body, String method, String host, String uri, String headers,
			String message) {
		super();
		this.id = id;
		this.resourceId = resourceId;
		this.parsedSuccessfully = parsedSuccessfully;
		this.isPoll = isPoll;
		this.body = body;
		this.method = method;
		this.host = host;
		this.uri = uri;
		this.headers = headers;
		this.message = message;
		setCreationTimestamp(creationTimestamp);
		setResponseTimestamp(responseTimestamp);
	}


	public ResourceLog(long resourceId, long creationTimestamp, HttpURLConnection conn) {
		try {
			this.resourceId = resourceId;
			this.isPoll = false;
			if (conn != null) {
				body = getRequestBody(conn);
				/*
				body = "" + request.body().asText();
				try {
					JsonNode jn = request.body().asJson();
					if (jn != null) {
						body += jn.toString();
					}
				} catch (Exception e) {
				}*/
				// String requestHeader = "" + rpl.request.headers().keySet() +
				// rpl.request.headers().values().toArray(String[] )
				method = conn.getRequestMethod();
				host = conn.getURL().toString();
				uri = conn.getURL().toString();
				headers = "headerplaceholder";
				/*
					HeaderNames.CONTENT_TYPE + " "
					+ request.getHeader(HeaderNames.CONTENT_TYPE) + "\n"
					+ HeaderNames.CONTENT_ENCODING + " "
					+ request.getHeader(HeaderNames.CONTENT_ENCODING)	+ "\n"				
					+ HeaderNames.CONTENT_LENGTH + " "
					+ request.getHeader(HeaderNames.CONTENT_LENGTH) + "\n";
					*/
			}
			setCreationTimestamp(creationTimestamp);
			// String parsed = (parsedSuccessfully) ? "Could be parsed\n"
			// : "Failed all parsers\n";
			// message = parsed + message;
			// message = "Never polled!";
		} catch (Exception e) {
			logger.error(e.getMessage() + e.getStackTrace()[0].toString() + e.toString());
		}
	}

	public ResourceLog(long resourceId, HttpURLConnection conn, long creationTimestamp, long responseTimestamp) {
		try {
			this.resourceId = resourceId;
			this.isPoll = true;
			if (conn != null) {
				body += conn.getResponseMessage();
				// try {
				// JsonNode jn = response.asJson();
				// if (jn != null) {
				// body += jn.toString();
				// }
				// } catch (Exception e) {
				//
				// }
				// String requestHeader = "" + rpl.request.headers().keySet() +
				// rpl.request.headers().values().toArray(String[] )
				method = "";
				host = "";
				uri = conn.getURL().toString();
				headers = "header placeholder";
					/*
					  "Status " + response.statusText() + "\n"
						+ HeaderNames.CONTENT_TYPE + " "
						+ response.contentType() + "\n"
						+ HeaderNames.CONTENT_ENCODING + " "
						+ response.contentEncoding()	+ "\n"				
						+ HeaderNames.CONTENT_LENGTH + " "
						+ response.contentLength() + "\n";
						*/
			}
			setCreationTimestamp(creationTimestamp);
			setResponseTimestamp(responseTimestamp);
			// this.parsedSuccessfully = parsedSuccessfully;
			// String parsed = (parsedSuccessfully) ? "Could be parsed\n"
			// : "Failed all parsers\n";
			// message = parsed + message;
			// message = "Never polled!";
			// timestamp = new Date().toString();
		} catch (Exception e) {
			logger.error(e.getMessage() + e.getStackTrace()[0].toString()
					+ e.toString());
		}
	}

	public static void createOrUpdate(Resource resource) {
		if (resource==null) {return;}
		StorageDAO storage = DAOFactory.getInstance();
		ResourceLog resourceLog = storage.findResourceLogByResourceId(resource.getId());
		if (resourceLog==null) { // make a new one
			resourceLog = new ResourceLog(resource);
		} else {
			//resourceLog.update(resource);
		}
		
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void appendMessage(String message) {
		this.message += message;
	}

	public void setCreationTimestamp(long creationTimestamp) {
		this.creationTimestamp = (creationTimestamp <= 0) ? System.currentTimeMillis() : creationTimestamp;
		this.timestamp = new Date(creationTimestamp).toString();
	}

	public void setResponseTimestamp(long responseTimestamp) {
		this.responseTimestamp = (responseTimestamp <= 0) ? System.currentTimeMillis() : responseTimestamp;
//		this.responseTime = controllers.Utils.timeStr(this.responseTimestamp - creationTimestamp);
	}

	public String getTimestamp() {
		setCreationTimestamp(this.creationTimestamp);
		return timestamp;
	}

	public String getResponseTime() {
		setResponseTimestamp(this.responseTimestamp);
		return responseTime;
	}

	public void updateParsedSuccessfully(boolean parsedSuccessfully) {
		this.parsedSuccessfully = parsedSuccessfully;
		if (id != -1) {
			this.update();
		}
	}

	public void updateMessages(String msg) {
		this.message = msg;
		if (id != -1) {
			this.update();
		}
	}

	public boolean updateResourceLog(ResourceLog rl) {
		this.resourceId = rl.resourceId;
		this.creationTimestamp = rl.creationTimestamp;
		this.responseTimestamp = rl.responseTimestamp;
		this.parsedSuccessfully = rl.parsedSuccessfully;
		this.isPoll = rl.isPoll;
		this.body = rl.body;
		this.method = rl.method;
		this.host = rl.host;
		this.uri = rl.uri;
		this.headers = rl.headers;
		this.message = rl.message;
		this.timestamp = rl.timestamp;
		this.responseTime = rl.responseTime;
		if (id != -1) {
			this.update();
			return true;
		}
		return false;
	}

	public static String getRequestBody(HttpURLConnection conn) {
    String body = "";
		/*
    if (conn.getHeaderField("Content-Type").equals("text/plain")) {
      // XXX: asText() does not work unless ContentType is // "text/plain"
      body = conn.body().asText();
    } else if (conn.getHeaderField("Content-Type").equals("application/json")) {
      body = (conn.body().asJson() != null) ? conn.body().asJson().toString() : "";
    } else {
      logger.error("[CtrlResource] request() did not have a recognised Content-Type");
      body = "";
    }
    logger.info("[Resources] post received from URI: " + request.uri() 
      + ", content type: " + conn.getHeaderField("Content-Type") 
      + ", payload: " + body);
			*/
    return body;
  }

	// trim strings longer than maximum length
	public void verify() {
		body = body.trim();
		if (body.length() > BODY_MAX_LENGTH) {
			body = body.substring(0, BODY_MAX_LENGTH - 2) + "";
			// logger.warn("[ResourceLog] trimming body: " + resource.label + " " +
			// body);
		}

		headers = headers.trim();
		if (headers.length() > MAX_LENGTH) {
			headers = headers.substring(0, MAX_LENGTH - 2) + "";
			// logger.warn("[ResourceLog] trimming body: " + resource.label + " " +
			// body);
		}

		message = message.trim();
		if (message.length() > MAX_LENGTH) {
			message = message.substring(0, MAX_LENGTH - 2) + "";
			// logger.warn("[ResourceLog] trimming body: " + resource.label + " " +
			// body);
		}

		uri = uri.trim();
		if (uri.length() > 255) {
			uri = uri.substring(0, 255 - 2) + "";
			// logger.warn("[ResourceLog] trimming body: " + resource.label + " " +
			// body);
		}

		host = host.trim();
		if (host.length() > 255) {
			host = host.substring(0, 255 - 2) + "";
			// logger.warn("[ResourceLog] trimming body: " + resource.label + " " +
			// body);
		}

		method = method.trim();
		if (method.length() > 255) {
			body = body.substring(0, 255 - 2) + "";
			// logger.warn("[ResourceLog] trimming body: " + resource.label + " " +
			// body);
		}

		// trimString(body);
		// trimString();
		// trimString(host);
		// trimString(uri);
		// trimString(headers);
		// trimString(message);
	}

	public void save() {
		verify();
		//super.save();
	}

	public void update() {
		verify();
		//super.update();
	}

/*
	public static ResourceLog createOrUpdate(ResourceLog resourceLog) {
		try {
			if (resourceLog.resourceId != -1) {
				if (resourceLog.creationTimestamp == 0L) {
					resourceLog.creationTimestamp = System.currentTimeMillis();
				}
				ResourceLog rplCopy = getByResource(resourceLog.resource, resourceLog.isPoll);
				if (rplCopy != null) {
					rplCopy.updateResourceLog(resourceLog);
					logger.info("[ResourceLog] updating existing for id: " + resourceLog.resourceId);
					return rplCopy;
				} else {
					resourceLog.save();
					logger.warn("[ResourceLog] creating new for id: " + resourceLog.resourceId);
					return resourceLog;
				}

			} else {
				logger.warn("[ResourceLog] resource null");
			}
		} catch (Exception e) {
			logger.error(e.getMessage() + e.getStackTrace()[0].toString() + e.toString());
		}
		return null;
	}
*/

	public long getId()								{ return id; }
	public long getResourceId()		{ return resourceId; }
	public long getCreationTimestamp(){ return creationTimestamp; }
	public long getResponseTimestamp(){ return responseTimestamp; }
	public boolean getParsedSuccessfully(){ return parsedSuccessfully; } 
	public boolean getIsPoll()				{ return isPoll; } 
	public String getBody()						{ return body; } 
	public String getMethod()					{ return method; }
	public String getHost()						{ return host; }
	public String getUri()						{ return uri; } 
	public String getHeaders()				{ return headers; }
	public String getMessage()				{ return message; } 
	public int getVersion()						{ return version; }


	public void setId(long id)										{ this.id = id; }
	public void getResourceId(long resourceId)		{ this.resourceId = resourceId; }
	public void getIsPoll(boolean isPoll)					{ this.isPoll = isPoll; } 
	public void getBody(String body)							{ this.body = body; } 
	public void getMethod(String method)					{ this.method = method; }
	public void getHost(String host)							{ this.host = host; }
	public void getUri(String uri)								{ this.uri = uri; } 
	public void getHeaders(String headers)				{ this.headers = headers; }
	public void getMessage(String message)				{ this.message = message; } 
	public void getVersion(int version)						{ this.version = version; }
	public void getTimestamp(String timestamp)		{ this.timestamp = timestamp; } 
	public void getResponseTime(String responseTIme)							{ this.responseTime = responseTime; } 
	public void getCreationTimestamp(long creationTimestamp)			{ this.creationTimestamp = creationTimestamp; }
	public void getResponseTimestamp(long responseTimestamp)			{ this.responseTimestamp = responseTimestamp; }
	public void getParsedSuccessfully(boolean parsedSuccessfully) { this.parsedSuccessfully = parsedSuccessfully; } 

}
