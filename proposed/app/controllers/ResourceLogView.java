package controllers;

import java.util.Date;
import models.*;

import models.ResourcePostLog;
import play.mvc.Http.HeaderNames;

import org.codehaus.jackson.JsonNode;

public class ResourceLogView {
	
	String requestBody;
	String method;
	String host;
	String uri;
	String headers;
	String timestamp;
	String message = "";
	
	public ResourceLogView(ResourcePostLog rpl) {
		requestBody = "" + rpl.request.body().asText();
		JsonNode jn = rpl.request.body().asJson();
		if (jn != null) {
			requestBody += jn.toString();
		}
		// String requestHeader = "" + rpl.request.headers().keySet() +
		// rpl.request.headers().values().toArray(String[] )
		method = rpl.request.method();
		host = rpl.request.host();
		uri = rpl.request.uri();
		headers = HeaderNames.CONTENT_TYPE + " " + rpl.request.getHeader(HeaderNames.CONTENT_TYPE) + " "
				+ HeaderNames.CONTENT_ENCODING + rpl.request.getHeader(HeaderNames.CONTENT_ENCODING);
		timestamp = new Date(rpl.creationTimestamp).toString();
		String parsed = (rpl.parsedSuccessfully) ? "Could be parsed\n" : "Failed all parsers\n" ;
		message = parsed + rpl.message;
	}
	
	public ResourceLogView(ResourcePollLog rpl) {
		requestBody = "" + rpl.response.getBody();
		JsonNode jn = rpl.response.asJson();
		if (jn != null) {
			requestBody += jn.toString();
		}
		// String requestHeader = "" + rpl.request.headers().keySet() +
		// rpl.request.headers().values().toArray(String[] )
		method = rpl.response.getStatusText();
		host = rpl.response.getUri().getHost();
		uri = rpl.response.getUri().toString();
		headers = HeaderNames.CONTENT_TYPE + " " + rpl.response.getHeader(HeaderNames.CONTENT_TYPE) + " "
				+ HeaderNames.CONTENT_ENCODING + rpl.response.getHeader(HeaderNames.CONTENT_ENCODING);
		timestamp = new Date(rpl.creationTimestamp).toString();
		String parsed = (rpl.parsedSuccessfully) ? "Could be parsed\n"
				: "Failed all parsers\n";
		message = parsed + rpl.message;
	}
	
}
