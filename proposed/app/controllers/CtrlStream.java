package controllers;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.libs.Json;
import play.mvc.*;
import models.*;

public class CtrlStream extends Controller {

//	@Security.Authenticated(Secured.class)
//	public static Result post(String path) {
//			User currentUser = Secured.getCurrentUser();
//			return post(currentUser, path);
//	}
	
//	public static Result postByUserKey(String ownerToken, String path) {
//		User owner = User.getByToken(ownerToken);
//		return post(owner, path);
//	}

//	private static Result post(User currentUser, String path) {
//		try {	
//			JsonNode jsonBody = request().body().asJson();
//			String textBody = request().body().asText();
//			String strBody = (jsonBody != null) ? jsonBody.toString() : textBody;
//			Logger.info("[Streams] post received from: " + " URI "
//					+ request().uri() + ", content type: "
//					+ request().getHeader("Content-Type") + ", payload: " + strBody);
//			if (!parseResponse(currentUser, jsonBody, textBody, path))
//				return badRequest("Bad request: Can't parse!");
//		} catch (Exception e) {
//			Logger.info("[Streams] Exception " + e.getMessage());
//			Logger.info("[Streams] User null" + Boolean.toString(currentUser == null));
//			return badRequest("Bad request: Error!");
//		}
//		return ok("ok");
//}
	
	@Security.Authenticated(Secured.class)
	public static Result getById(String id) {
		return TODO;
	}
	
//	private static Stream getOrAddByPath(User currentUser, String path) {
//		if (currentUser == null)
//			return null;
//		Vfile f = FileSystem.readFile(currentUser, path);
//		if (f == null) {
//			Stream stream = (Stream)Stream.create(new Stream(currentUser));
//			f = FileSystem.addFile(currentUser, path);
//			f.setLink(stream);
//			Logger.info("[Streams] Creating stream at: " + currentUser.getUserName() + path);
//		}
//		Stream stream = f.getLink();
//		if (stream != null) {
//				return stream;
//			}
//		return null;
//	}
//
//	private static boolean parseResponse(User currentUser, JsonNode jsonBody, String textBody, String path) {
//		if (jsonBody != null) {
//			if (!parseJsonResponse(currentUser, jsonBody, path))
//				return false;
//		} else {
//			if (textBody != null) {
//				Stream stream = getOrAddByPath(currentUser, path);
//				Logger.info("[Posting now] " + textBody + " at " + Utils.currentTime() + " to: " + path);
//				return stream.post(Double.parseDouble(textBody), Utils.currentTime());
//			} else {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	private static boolean parseJsonResponse(User currentUser, JsonNode jsonNode, String path) {
//		if (jsonNode.isValueNode()) {
//			Stream stream = getOrAddByPath(currentUser, path);
//			return stream.post(jsonNode.getDoubleValue(), Utils.currentTime());
//		} else {
//			Iterator<String> it = jsonNode.getFieldNames();
//			while (it.hasNext()) {
//				String field = it.next();
//				if (!parseJsonResponse(currentUser, jsonNode.get(field),	Utils.concatPath(path, field)))
//					return false;
//			}
//		}
//		return true;
//	}
	
	public static Result getData(String ownerName, String path, Long tail, Long last, Long since) {
			final User user = Secured.getCurrentUser();
			final User owner = User.getByUserName(ownerName);
	    //if(user == null) return notFound();
	    return getData(user, owner, path, tail, last, since);
	 }
	
	public static Result getDataById(Long id, Long tail, Long last, Long since) {
		final User user = Secured.getCurrentUser();
    //if(user == null) return notFound();
		Stream stream = Stream.get(id);	
    return getData(user, (Stream)stream, tail, last, since);
 }
	
	public static Result getDataByUserKey(String user_token, String path, Long tail, Long last, Long since) {
		final User user = Secured.getCurrentUser();
		final User owner = User.getByToken(user_token);
    //if(user == null) return notFound();
    return getData(user, owner, path, tail, last, since);
 }
	
	//@Security.Authenticated(Secured.class)
	private static Result getData(User currentUser, User owner, String path, Long tail, Long last, Long since){
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

	private static Result getData(User currentUser, Stream stream, Long tail, Long last, Long since){
		if (stream == null) {
			return notFound();
		}
		
		if(!stream.canRead(currentUser))
			return unauthorized("Private stream!");
		
    List<? extends DataPoint> dataSet = null;
    if(tail<0 && last <0 && since <0){
    	tail=1L;
    }
    if(tail >= 0) {
    	dataSet = stream.getDataPointsTail(tail);
    } else if(last >= 0) {
      dataSet = stream.getDataPointsLast(last);
    } else if(since >= 0) {
    	dataSet = stream.getDataPointsSince(since);
    } else {
    	throw new RuntimeException("This cannot happen!");
    }
    
    ObjectNode result = Json.newObject();
    ArrayNode time= result.putArray("time");
    ArrayNode data= result.putArray("data");
    
    for(DataPoint dataPoint: dataSet) {
    	time.add(dataPoint.timestamp);
	    if (stream.getType() == Stream.StreamType.DOUBLE) {
	    	data.add((Double)dataPoint.getData());
			}
    }
    
    return ok(result);
	}
}
