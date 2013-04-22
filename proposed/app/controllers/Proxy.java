/*
Copyright (c) 2013, Swedish Institute of Computer Science
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the <organization> nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package controllers;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

import play.*;

import play.core.Router.Routes;
import play.libs.F.*;
import play.libs.*;
import play.libs.F.Promise;
import play.libs.WS.Response;
import play.libs.WS.WSRequestHolder;
import play.mvc.*;
import play.mvc.Http.HeaderNames;
import play.mvc.Http.RequestBody;
import play.data.*;

import models.*;
import views.html.*;

public class Proxy extends Controller {
	/*** Request timeout (in ms) **/
	public final static Long REQUEST_TIMEOUT = 30000L; // 30 seconds

	@Security.Authenticated(Secured.class)
	public static Result forwardById(Long id, String arguments) {
		User currentUser = Secured.getCurrentUser();
		Resource resource = Resource.get(id, currentUser);
		if (resource == null) {
			return badRequest("Resource does not exist: " + id);
		}
		Pattern pattern = Pattern.compile("([^&?=]+)=([^?&]+)");
		Matcher matcher = pattern.matcher(arguments);
		Map<String, String> queryParameters = new HashMap<String, String>();
		while (matcher.find()) {
			queryParameters.put(matcher.group(1), matcher.group(2));
		}
		return forward(resource, queryParameters);
	}

	public static Result forwardByKey(String key, String arguments) {
		Resource resource = Resource.getByKey(key);
		if (resource == null) {
			return badRequest("Resource does not exist: " + key);
		}
		Pattern pattern = Pattern.compile("([^&?=]+)=([^?&]+)");
		Matcher matcher = pattern.matcher(arguments);
		Map<String, String> queryParameters = new HashMap<String, String>();
		while (matcher.find()) {
			queryParameters.put(matcher.group(1), matcher.group(2));
		}
		return forward(resource, queryParameters);
	}

	private static Result forward(final Resource resource,
			final Map<String, String> queryParameters) {
		final String method = request().method();
		final String body = request().body().asText();
		final Map<String, String[]> headers = request().headers();

		final String url = resource.getUrl();
		return async(Akka.future(new Callable<Result>() {
			public Result call() {
				Logger.info("[Proxy] forwarding method: " + method + ", to: " + url
						+ ", body: " + body);
				try {
					Promise<Response> promise = null;
					WSRequestHolder request = WS.url(url);
					if (queryParameters != null) {
						for (String name : queryParameters.keySet()) {
							request.setQueryParameter(name, queryParameters.get(name));
						}
					}
					for (String name : headers.keySet()) {
						/*
						 * Don't accept gzip, not supported yet by play 2.1 requests
						 */
						if (name.equalsIgnoreCase(ACCEPT_ENCODING)) {
							 request = request.setHeader(name, "");
						}
						/*
						 * Forge host
						 */
						else if (!name.equalsIgnoreCase( HeaderNames.HOST ))
							request = request.setHeader(name, headers.get(name)[0]);
					}
					if (method.equals("GET")) {
						promise = request.get();
					} else if (method.equals("POST")) {
						promise = request.post(body);
					} else if (method.equals("PUT")) {
						promise = request.put(body);
					} else if (method.equals("DELETE")) {
						promise = request.delete();
					}
					Response response = Akka.asPromise(promise.getWrappedPromise()).get(
							REQUEST_TIMEOUT, TimeUnit.MILLISECONDS);
					String encoding = response.getHeader(HeaderNames.CONTENT_ENCODING);
					String contentType = response.getHeader(HeaderNames.CONTENT_TYPE);
					int contentLength = response.getBody().length();
					Logger.info("[Proxy] got response for: " + method + ", to: " + url
							+ ", encoding: " + encoding + ", content-type: " + contentType +", body length: "
							+ contentLength + " bytes");
//					String body="GZIP does not work!";
//					if(encoding != null && encoding.contains("gzip")) {
//						String charSet = ( contentType.indexOf("charset=")!=-1 && contentType.indexOf("charset=") + 8 < contentType.length() ) ? contentType.substring( contentType.indexOf("charset=")+8 ).toUpperCase() : "UTF-8";
//						body = gzipDeflator(response.getBody().getBytes(), contentLength, charSet);
//					} else {
//						body = response.getBody();
//					}
					String body = response.getBody();
					return status(response.getStatus(), body);
				} catch (Exception e) {
					Logger.info("[Proxy] forwarding failed: " + e.getMessage());
					return badRequest(e.getMessage());
				}
			}
		}));
	}
	
	//trying gzip deflating ... --> incorrect header check
//	private static String gzipDeflator(byte [] input, int compressedDataLength, String charset) throws java.io.UnsupportedEncodingException, java.util.zip.DataFormatException {
//		String outputString=null; 
////		try {		 
//			 // Decompress the bytes
//	     Inflater decompresser = new Inflater();
//	     decompresser.setInput(input, 0, compressedDataLength);
//	     byte[] result = new byte[10*compressedDataLength];
//	     int resultLength = decompresser.inflate(result);
//	     decompresser.end();
//	     // Decode the bytes into a String
//	     outputString = new String(result, 0, resultLength, charset); //charset "UTF-8"
////	 } catch(java.io.UnsupportedEncodingException e) {
////			Logger.info("[Proxy gzipDeflator UnsupportedEncodingException] failed: " + e.getMessage());
////	 } catch (java.util.zip.DataFormatException e) {
////			Logger.info("[Proxy gzipDeflator DataFormatException] failed: " + e.getMessage());
////	 }
//		 return outputString;
//	}
}
