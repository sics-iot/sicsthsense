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

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.sicsthsense.*;
import se.sics.sicsthsense.core.*;
import se.sics.sicsthsense.jdbi.*;

//@Table(name = "resource_log", uniqueConstraints = { @UniqueConstraint(columnNames = { "resource_id", "is_poll" }) })
public class ResourceLog {
	private static final int MAX_LENGTH = 4 * 1024, BODY_MAX_LENGTH = 8 * 1024;

	public long id;
	public long resourceId;
	public long creationTimestamp;
	public long responseTimestamp;
	public long responseTime;
	public boolean parsedSuccessfully = false;
	public boolean isPoll = false;
	public String body = "";
	public String method = "";
	public String headers = "";
	public String message = "";
	private int version;

	private final Logger logger = LoggerFactory.getLogger(ResourceLog.class);
	private StorageDAO storage = null;

	public ResourceLog(StorageDAO storage) {
		this.storage = storage;
		this.creationTimestamp = 0;
		this.responseTimestamp = 0;
		this.parsedSuccessfully = true;
		this.isPoll = false;
		this.body = "";
		this.method = "";
		this.headers = "";
		this.message = "";
	}
	public ResourceLog(long id, long resourceId,
			long creationTimestamp, long responseTimestamp,
			boolean parsedSuccessfully, boolean isPoll,
			String body, String method, String headers, String message) {
		super();
		this.resourceId = resourceId;
		this.creationTimestamp = creationTimestamp;
		this.responseTimestamp = responseTimestamp;
		this.parsedSuccessfully = parsedSuccessfully;
		this.isPoll = isPoll;
		this.body = body;
		this.method = method;
		this.headers = headers;
		this.message = message;
	}
	public ResourceLog(Resource resource) {
		super();
		this.resourceId = resource.getId();
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

	public static ResourceLog createOrUpdate(StorageDAO storage, long resourceId) {
		//if (resource==null) {logger.error("Resource is null"); return;}
		final Logger logger = LoggerFactory.getLogger(ResourceLog.class);
		ResourceLog resourceLog = storage.findResourceLogByResourceId(resourceId);
		if (resourceLog==null) { // make a new one
			Resource resource = storage.findResourceById(resourceId);
			if (resource==null) {logger.error("Resource does not exist!"); return null;}
			resourceLog = new ResourceLog(resource);
			resourceLog.create();
		} else {
			//resourceLog.update(resource);
		}
		return resourceLog;
	}


	public void update(boolean success, boolean is_poll, String message, long responseTimestamp) {
		this.parsedSuccessfully = parsedSuccessfully;
		this.isPoll = isPoll;
		this.message = message;
		this.responseTimestamp = responseTimestamp;
	}

  public void create() {
		// should check already exists
		StorageDAO storage = DAOFactory.getInstance();
		storage.insertResourceLog(
			this.resourceId,
			System.currentTimeMillis(),
			-1,
			this.parsedSuccessfully,
			this.isPoll,
			this.body,
			this.method,
			this.headers,
			this.message,
			1
		);
		logger.info("Just created resource log:"+this.resourceId);
	}

  public void save() {
		StorageDAO storage = DAOFactory.getInstance();
		storage.updateResourceLog(
			this.resourceId,
			this.creationTimestamp,
			this.responseTimestamp,
			this.parsedSuccessfully,
			this.isPoll,
			this.body,
			this.method,
			this.headers,
			this.message,
			1
		);
		//logger.info("Just updated resource log:"+this.resourceId);
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
		this.headers = rl.headers;
		this.message = rl.message;
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

	/*
	public void save() {
		verify();
		//super.save();
	}*/

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


	public void appendMessage(String message) { this.message += message; }

	public void setCreationTimestamp(long creationTimestamp) {
		this.creationTimestamp = (creationTimestamp <= 0) ? System.currentTimeMillis() : creationTimestamp;
	}

	public void setResponseTimestamp(long responseTimestamp) {
		this.responseTimestamp = (responseTimestamp <= 0) ? System.currentTimeMillis() : responseTimestamp;
	}

	public long getResponseTime() {
		setResponseTimestamp(this.responseTimestamp);
		return responseTime;
	}
	public long    getId()								{ return id; }
	public long    getResourceId()		{ return resourceId; }
	public long    getCreationTimestamp(){ return creationTimestamp; }
	public long    getResponseTimestamp(){ return responseTimestamp; }
	public boolean getParsedSuccessfully(){ return parsedSuccessfully; }
	public boolean getIsPoll()				{ return isPoll; }
	public String  getBody()						{ return body; }
	public String  getMethod()					{ return method; }
	public String  getHeaders()				{ return headers; }
	public String  getMessage()				{ return message; }
	public int     getVersion()						{ return version; }


	public void setId(long id)						{ this.id = id; }
	public void setResourceId(long resourceId)		{ this.resourceId = resourceId; }
	public void setIsPoll(boolean isPoll)			{ this.isPoll = isPoll; }
	public void setBody(String body)				{ this.body = body; }
	public void setMethod(String method)			{ this.method = method; }
	public void setHeaders(String headers)			{ this.headers = headers; }
	public void setMessage(String message)			{ this.message = message; }
	public void setVersion(int version)				{ this.version = version; }
	public void setResponseTime(String responseTIme)							{ this.responseTime = responseTime; }
	public void setParsedSuccessfully(boolean parsedSuccessfully) { this.parsedSuccessfully = parsedSuccessfully; }

}
