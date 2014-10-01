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

package controllers;

import java.util.List;
import java.util.HashMap;

import models.DataPoint;
import models.FileSystem;
import models.Resource;
import models.Stream;
import models.User;
import models.Vfile;
import controllers.CtrlResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.HeaderNames;
import views.html.streamPage;

public class CtrlStream extends Controller {

	static private Form<Stream> streamForm = Form.form(Stream.class);
	static public int pageSize = 8;

	private static boolean canWrite(User user, Stream stream) {
		return (stream != null && user != null && stream.owner.equals(user));
	}
	private static boolean canRead(User user, Stream stream) {
		return stream.canRead(user);
	}

	// do we push simple handlers down to the Ctrls? or keep in Application?
	/*@Security.Authenticated(Secured.class) 
	public static Result streams() {
  	User currentUser = Secured.getCurrentUser();
    return ok(resourcesPage.render(currentUser.streamList, ""));
	}*/

	@Security.Authenticated(Secured.class)
	public static Result getById(Long id) {
		User currentUser = Secured.getCurrentUser();
		Stream stream = Stream.get(id);
		if (stream== null) {
			return badRequest("Stream does not exist: " + id);
		}
		streamForm = streamForm.fill(stream);
		return ok(streamPage.render(currentUser.streamList, stream, streamForm, ""));
	}

	/*
	 * Parse a JSON object and create stream
	 */
	@Security.Authenticated(Secured.class)
	public static Result createPost() {
		JsonNode root;
		String body = "";
		Logger.info("[CtrlResource] making Resource from JSON");
		try { // recusively parse JSON and add() all fields
			body = request().body().asText();
			root = request().body().asJson();
		} catch (Exception e) { // nevermind, move on...
			Logger.warn("[CtrlResource] had problems parsing JSON to make Resource:"+body);
			return badRequest("[CtrlResource] had problems parsing JSON to make Resource: "+body);
		}
		if (!validateStreamJson(root)) {
			Logger.error("JSON does not sufficiently describe Resource: "+body);
			return badRequest("JSON does not sufficiently describe Resource: "+body);
		}

		return ok("Made the parser!");
	}


	// check the JSON describes a new Resource sufficiently
	// and instantite it to be stored
	public static boolean validateStreamJson(JsonNode root) {
		Logger.info("[CtrlStream] validating and creatingi a stream parser");
		final User currentUser = Secured.getCurrentUser();
		if (currentUser==null) {return false;}
		String resourceID="";
		String parser    ="";
		String filePath  ="";
		//optional attributes:
		String contentType="application/json";
		String timeFormat ="unix";

		HashMap map = Utils.jsonToMap(root);

		if (map.get("resourceid")==null) {
			Logger.error("[CtrlStream] not set resource ID!");
			return false;
		} else { resourceID=(String)map.get("resourceid" ); }
		if (map.get("parser")==null) {
			Logger.error("[CtrlStream] not set parser!");
			return false;
		} else { parser=(String)map.get("parser" ); }
		if (map.get("filepath")==null) {
			Logger.error("[CtrlStream] not set file path!");
			return false;
		} else { filePath=(String)map.get("filepath" ); }
		if (map.get("timeFormat") !=null) {timeFormat=(String)map.get("timeFormat" );}
		if (map.get("contentType")!=null) {contentType=(String)map.get("contenttype");}

		Long rID = Long.parseLong(resourceID); // should error check!
		if (Resource.getById(rID)==null) {
			Logger.error("[CtrlStream] Resource ID "+rID+" does not exist!");
			return false;
		}
		//Logger.info("[CtrlStream] save new parser");
    CtrlResource.addParser(rID, parser, contentType, filePath, timeFormat, 1, 2, 1);
		// create the new parser
		return true;
	}

	@Security.Authenticated(Secured.class)
	public static Result modify(Long id) {
		Form<Stream> theForm = streamForm.bindFromRequest();
		if (theForm.hasErrors()) {
			return badRequest("Bad request: " + theForm.errorsAsJson().toString());
		} else {
			Stream submitted = theForm.get();
			User currentUser = Secured.getCurrentUser();
			Stream stream = Stream.get(id);
			if (stream  == null || !stream.canWrite(currentUser)) {
				return unauthorized("Unauthorized!");
			}

			// probably not the correct way to do it
			if (submitted.latitude != 0.0) {
				//String lon = streamForm.field("longitude").value();
				//String lat = streamForm.field("latitude").value();
				//Logger.error("form latlon: "+lat+","+lon);
				//Location location = new Location(lon,lat);
				//submitted.location.setLatLon(lat,lon);
			} else {
				Logger.error("location not set");
			}
			stream.updateStream(submitted);
		}
		return redirect(routes.Application.viewStream(id));
	}

	@Security.Authenticated(Secured.class)
	public static Result download(Long id) {
		final User currentUser = Secured.getCurrentUser();
		final Stream stream = Stream.get(id);
		final List<? extends DataPoint> dataSet = stream.getDataPoints();
		
		if (stream.canRead(currentUser)) {
			final String streamName = "# SicsthSense "+currentUser.username+" "+stream.file.getPath()+"\n";
			response().setContentType("text/plain");
			response().setHeader("Content-Disposition", "attachment; filename="+stream.resource.label+"-Stream.txt");
			
			Chunks<String> chunks = new StringChunks() {
				// Called when the stream is ready
				public void onReady(Chunks.Out<String> out) {
					if (dataSet == null) {
						out.close();
						return;
					}
					out.write(streamName);
					for (DataPoint dp: dataSet) {
						out.write(dp.toTSV()+"\n");
					}
					
					out.close();
				}
			};
			return ok(chunks);
		}
		return unauthorized();
	}

	@Security.Authenticated(Secured.class)
	public static Result delete(Long id) {
		final User currentUser = Secured.getCurrentUser();
		Stream stream = Stream.get(id);
		if (canWrite(currentUser, stream)) {
			stream.delete();
			currentUser.sortStreamList(); // reorder streams
			return ok();
		}
		return unauthorized();
	}

	public static Result deleteByKey(String key) {
		final Stream stream = Stream.getByKey(key);
		if (stream == null) {
			return notFound();
		} 
		stream.delete();
		return ok();
	}

	@Security.Authenticated(Secured.class)
	public static Result clear(Long id) {
		final User user = Secured.getCurrentUser();
		// if(user == null) return notFound();
		Stream stream = Stream.get(id);
		if (canWrite(user, stream)) {
			stream.clearStream();
			return ok();
		}
		return unauthorized();
	}
	
	public static Result clearByKey(String key) {
		final Stream stream = Stream.getByKey(key);
		if (stream == null) {
			return notFound();
		} 
		stream.clearStream();
		return ok();
	}

	@Security.Authenticated(Secured.class)
	public static Result setPublicAccess(Long id, Boolean pub) {
		final User user = Secured.getCurrentUser();
		Stream stream = Stream.get(id);
		if (canWrite(user, stream)) {
			return ok(Boolean.toString(stream.setPublicAccess(pub)));
		}
		return unauthorized();
	}

//	@Security.Authenticated(Secured.class)
	public static Result isPublicAccess(Long id) {
		Stream stream = Stream.get(id);
		if(stream == null) {
			return notFound();
		}
		return ok(Boolean.toString(stream.publicAccess));
	}

	@Security.Authenticated(Secured.class)
	public static Result setPublicSearch(Long id, Boolean pub) {
		final User user = Secured.getCurrentUser();
		Stream stream = Stream.get(id);
		if (canWrite(user, stream)) {
			return ok(Boolean.toString(stream.setPublicSearch(pub)));
		}
		return unauthorized();
	}


	@Security.Authenticated(Secured.class)
	public static int listStreamsLength() {
		final User user = Secured.getCurrentUser();
		return user.streamList.size();
	}
	
//@Security.Authenticated(Secured.class)
	public static Result isPublicSearch(Long id) {
		Stream stream = Stream.get(id);
		if(stream == null) {
			return notFound();
		}
		return ok(Boolean.toString(stream.publicSearch));
	}

	public static Result getByKey(String key, Long tail,
			Long last, Long since) {
		final Stream stream = Stream.getByKey(key);
		if (stream == null)
			return notFound();
		return getData(stream.owner, stream, tail, last, since);
	}

	// List data points of a user's path
	public static Result getByPath(String path, Long tail,
			Long last, Long since) {
		final User user = Secured.getCurrentUser();
		if(user == null) {
			return notFound();
		}
		return getByUserPath(user.username, path, tail, last, since);
	}

	public static Result getByUserPath(String username, String path, Long tail,
			Long last, Long since) {
		path=Utils.decodePath(path);
		final Stream stream = Stream.getByUserPath(username,"/"+path);
		final User owner = User.getByUserName(username);
		final User currentUser = Secured.getCurrentUser();
		if (stream == null) { return notFound(); }
		if (!stream.canRead(currentUser)) {
			// don't reveal this stream exists
			return notFound();
		}
		return getData(currentUser, stream, tail, last, since);
	}

	public static Result postByPath(String path) {
		final User user = Secured.getCurrentUser();
		if (user == null) {
			return notFound();
		}
		String username = user.username;
		path = Utils.decodePath(path);
		final Stream stream = Stream.getByUserPath(username, "/" + path);
		if (stream == null)
			return notFound();
		return post(user, stream);
	}

	public static Result postByKey(String key) {
		final Stream stream = Stream.getByKey(key);
		if (stream == null)
			return notFound();
		return post(stream.owner, stream);
	}

	@Security.Authenticated(Secured.class)
	public static Result post(Long id) {
		final User user = Secured.getCurrentUser();
		Stream stream = Stream.get(id);
		if(stream == null) {
			return notFound();
		}
		return post(user, stream);
	}

	private static Result post(User user, Stream stream) {
		boolean success = false;
		long currentTime = Utils.currentTime();
		String textBody="";
		try {
			if (canWrite(user, stream)) {
				// Logger.info("StreamParser: parseResponse(): post: "+stream.file.getPath());
				if ("application/json".equalsIgnoreCase(request().getHeader(
						HeaderNames.CONTENT_TYPE))
						|| "text/json".equalsIgnoreCase(request().getHeader(HeaderNames.CONTENT_TYPE))) {
					JsonNode jsonBody = request().body().asJson();
					textBody += jsonBody.asText();
					// Logger.info("[StreamParser] as json");
					success = parseJsonResponse(stream, jsonBody, currentTime);
				} else {
					textBody = request().body().asText(); // request.body().asRaw().toString();
					// Logger.info("[StreamParser] as text");
					double number = Double.parseDouble(textBody);
					success = stream.post(number, currentTime);
				}
				if (!success) {
					return badRequest("Bad request: Error! " + textBody);
				} else {
					return ok("ok");
				}
			}
		} catch (Exception e) {
			return badRequest("Bad request: Error! " + e.getMessage());
		}
		return unauthorized();
	}
	
	private static boolean parseJsonResponse(Stream stream, JsonNode root,
			Long currentTime) {
		// TODO check concat path against inputParser, get the goal and stop
		// TODO (linear time) form a list of nested path elements from the gui, and
		if (root == null) {
			return false;
		}
		JsonNode node = root;

		if (node.isValueNode()) { // it is a simple primitive
			Logger.info("posting: " + node.asDouble() + " "
					+ Utils.currentTime());
			return stream.post(node.asDouble(), Utils.currentTime());

		} else if (node.get("value") != null) { // it may be value:X
			double value = node.get("value").asDouble();
			// should be resource timestamp

			if (node.get("time") != null) { // it may have time:Y
				currentTime = node.get("time").asLong();
			}
			Logger.info("posting: " + node.asDouble() + " " + Utils.currentTime());
			return stream.post(value, currentTime);
		}
		return false;
	}

	@Security.Authenticated(Secured.class)
	public static Result getData(String ownerName, String path, Long tail,
			Long last, Long since) {
		final User user = Secured.getCurrentUser();
		final User owner = User.getByUserName(ownerName);
		// if(user == null) return notFound();
		return getData(user, owner, path, tail, last, since);
	}

	@Security.Authenticated(Secured.class)
	public static Result getDataById(Long id, Long tail, Long last, Long since) {
		final User user = Secured.getCurrentUser();
		Stream stream = Stream.get(id);
		return getData(user, stream, tail, last, since);
	}
	
	@Security.Authenticated(Secured.class)
	public static Result regenerateKey(Long id) {
		final User currentUser = Secured.getCurrentUser();
		Stream stream = Stream.get(id);
		if (stream == null || stream.owner.id != currentUser.id) {
			return badRequest("Stream does not exist: " + id);
		}
		String key = stream.updateKey();
		//return ok("Stream key reset successfully: " + id + " New key: " + key);
		return ok(key);
	}


	// @Security.Authenticated(Secured.class)
	private static Result getData(User currentUser, User owner, String path,
			Long tail, Long last, Long since) {
		Vfile f = FileSystem.readFile(owner, path);
		if (f == null) {
			return notFound();
		}
		Stream stream = f.getLink();
		if (stream == null) {
			return notFound();
		}
		return getData(currentUser, stream, tail, last, since);
	}

	//tail: number of points, last: point during last {seconds}, since: Unix timestamp
	private static Result getData(User currentUser, Stream stream, Long tail,
			Long last, Long since) {
		if (stream == null) {
			return notFound();
		}

		if (!stream.canRead(currentUser))
			return unauthorized("Private stream!");

		List<? extends DataPoint> dataSet = null;
		if (tail < 0 && last < 0 && since < 0) {
			tail = 1L;
		}
		if (tail >= 0) {
			dataSet = stream.getDataPointsTail(tail);
		} else if (last >= 0) {
			dataSet = stream.getDataPointsLast(last*1000);
		} else if (since >= 0) {
			dataSet = stream.getDataPointsSince(since);
		} else {
			throw new RuntimeException("This cannot happen!");
		}

		ObjectNode result = Json.newObject();
		ArrayNode time = result.putArray("time");
		ArrayNode data = result.putArray("data");

		if (dataSet!= null) {
			for (DataPoint dataPoint : dataSet) {
				time.add(dataPoint.timestamp);
				if (stream.getType() == Stream.StreamType.DOUBLE) {
					data.add((Double) dataPoint.getData());
				}
			}
		}
		return ok(result);
	}
}
