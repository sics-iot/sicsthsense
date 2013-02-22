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

public class CtrlSource extends Controller {

	@Security.Authenticated(Secured.class)
	public static Result post(String path) {
			User currentUser = Secured.getCurrentUser();
			return post(currentUser, path);
	}
	
	public static Result postByUserKey(String ownerToken, String path) {
		User owner = User.getByToken(ownerToken);
		return post(owner, path);
	}

	private static Result post(User currentUser, String path) {
		return TODO;
		// resolve device from device list
		// if public: good
		// if this currentUser.username is in ACL: good
		// else error message
	}
	private static Result post(Long id, String key) {
		Source source = Source.get(id, key);
		if (source != null) {
			try {
				JsonNode jsonBody = request().body().asJson();
				String textBody = request().body().asText();
				String strBody = (jsonBody != null) ? jsonBody.toString() : textBody;
				Logger.info("[Streams] post received from: " + " URI "
						+ request().uri() + ", content type: "
						+ request().getHeader("Content-Type") + ", payload: " + strBody);
				if (!source.parseResponse(jsonBody, textBody, path))
					return badRequest("Bad request: Can't parse!");
			} catch (Exception e) {
				Logger.info("[Streams] Exception " + e.getMessage());
				Logger.info("[Streams] User null"
						+ Boolean.toString(currentUser == null));
				return badRequest("Bad request: Error!");
			}
			return ok("ok");
		}
		return notFound();
	}
	
	@Security.Authenticated(Secured.class)
	public static Result getById(String id) {
		return TODO;
	}
	
	
	
	public static Result getData(String ownerName, String path, Long tail, Long last, Long since) {
			final User user = Secured.getCurrentUser();
			final User owner = User.getByUserName(ownerName);
	    //if(user == null) return notFound();
	    return getData(user, owner, path, tail, last, since);
	 }
	
	public static Result getDataById(Long id, Long tail, Long last, Long since) {
		final User user = Secured.getCurrentUser();
    //if(user == null) return notFound();
		UserOwnedResource stream = Stream.get(id);
		if(!(stream instanceof Stream)) {
			return notFound();
		}
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
		File f = FileSystem.readFile(owner, path);
		if (f == null) {
			return notFound();
		}
		UserOwnedResource resource = f.getLink();
		if (resource == null || ! (resource instanceof Stream)) {
			return notFound();
		}
		Stream stream = (Stream) resource;
		return getData(currentUser, stream, tail, last, since);
		
	}

	private static Result getData(User currentUser, Stream stream, Long tail, Long last, Long since){
		if (stream == null) {
			return notFound();
		}
		
		if(!stream.canRead(currentUser))
			return unauthorized("Private stream!");
		
    List<DataPoint> dataSet = null;
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
