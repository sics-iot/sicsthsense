package controllers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

//import actions.CheckPermissionsAction;

import play.*;
import play.libs.F.*;
import play.libs.*;
import play.libs.WS.WSRequestHolder;
import play.mvc.*;

import models.*;

public class Poller extends Controller {
  
  public static void pollAll() {
		//Logger.info("Poller pollAll()");
    List<Resource> withPolling = Resource.find.where()
        .gt("pollingPeriod", 0)
        .findList();
    for(Resource resource: withPolling) {
			//Logger.info("Poller poll a resource");
      resource.poll();
    }      
  }
  
/*  public static void poll(final Resource resource) {
    final EndPoint endPoint = resource.getEndPoint();
    final long current = Utils.currentTime();
    //Logger.info("[Streams] periodic timer: " + resource.fullPath() + ", period: " + resource.pollingPeriod + ", last polled: " + (current-resource.lastPolled));
    if (current >= resource.lastPolled + resource.pollingPeriod) {
      String path = resource.path;
      String arguments = "";
      if(path.indexOf('?') != -1) {
    	  path = resource.path.substring(0, resource.path.indexOf('?'));
    	  arguments = resource.path.substring(resource.path.indexOf('?')+1, resource.path.length());
      }
      final String url = Utils.concatPath(endPoint.url, path);
      Pattern pattern = Pattern.compile("([^&?=]+)=([^?&]+)");
      Matcher matcher = pattern.matcher(arguments);
      Map<String, String> queryParameters = new HashMap<String, String>();
      Logger.info("[Resources] polling: " + resource.fullPath() + ", URL: " + url);
      WSRequestHolder request = WS.url(url);
      while (matcher.find()) {
    	  request.setQueryParameter(matcher.group(1), matcher.group(2));
      } 
      request.get().map(
        new Function<WS.Response, Boolean>() {
          public Boolean apply(WS.Response response) {
        	System.out.println("type " + response.getHeader("Content-type"));
        	JsonNode jsonBody = null;
        	String textBody = null;
        	String strBody = null;
        	switch(response.getHeader("Content-type")) {
        		case "application/json":
        			jsonBody = response.asJson();
        			strBody = jsonBody.asText();
        			break;
        		default:
        			textBody = response.getBody();
        			strBody = textBody.length() + " bytes";
        			break;
        	}
            Logger.info("[Resources] polling response for: " + resource.fullPath() + ", content type: " + response.getHeader("Content-Type") + ", payload: " + strBody);
            parseResponse(endPoint, jsonBody, textBody, resource);
            return true;
          }
        }
      );
      resource.lastPolled = current;
      resource.update();
    }
  }
  public static void insertSample(EndPoint endPoint, String path, float data) {
    String fullPath = Utils.concatPath(endPoint.fullPath(), path);
    Logger.info("[Resources] new sample for: " + fullPath + ", data: " + data);
    Resource resource = endPoint.getOrCreateResource(path);
    resource.post(data, Utils.currentTime());
  }
  
  public static boolean parseResponse(EndPoint endPoint, JsonNode jsonBody, String textBody, Resource resource) {
    if(jsonBody != null) {
      if(!parseJsonResponse(endPoint, jsonBody, resource.path)) return false;
    } else {
      if(textBody != null) {
    	 if(!resource.inputParser.equals("")) {
    		 Pattern pattern = Pattern.compile(resource.inputParser);
    		 Matcher matcher = pattern.matcher(textBody);
    		 if (matcher.find()) {
    			 textBody = matcher.group(1);
    		 }
    	 }
        insertSample(endPoint, resource.path, Float.parseFloat(textBody));
      } else {
        return false;
      }
    }
    return true;
  }
  
  public static boolean parseJsonResponse(EndPoint endPoint, JsonNode jsonNode, String path) {
    if(jsonNode.isValueNode()) {
      insertSample(endPoint, path, (float)jsonNode.getDoubleValue());
    } else {
      Iterator<String> it = jsonNode.getFieldNames();
      while(it.hasNext()) {
        String field = it.next();
        if(!parseJsonResponse(endPoint, jsonNode.get(field), Utils.concatPath(path, field))) return false;
      }
    }
    return true;
  }
  
  public static Result post(String userName, String endPointName, String path) {
    User user = User.getByUserName(userName);
    if(user == null) return notFound();
    EndPoint endPoint = EndPoint.getOrCreateByLabel(user, endPointName);
    try {
      JsonNode jsonBody = request().body().asJson();
      String textBody = request().body().asText();
      String strBody = (jsonBody != null) ? jsonBody.toString() : textBody;
      Resource resource = endPoint.getOrCreateResource(path);
      Logger.info("[Resources] post received from: " + Utils.concatPath(userName, endPointName, path) + ", URI " + request().uri() + ", content type: " + request().getHeader("Content-Type") + ", payload: " + strBody);
      if(!parseResponse(endPoint, jsonBody, textBody, resource)) return badRequest("Bad request: Can't parse!");
    } catch (Exception e) {
    	Logger.info("[Resources] Exception " + e.getMessage());
      return badRequest("Bad request: Error!");
    }
    return ok("ok");
  }
  
	//@Security.Authenticated(Secured.class)
	public static Result getSecured(String userName, String endPointName,
			String path, Long tail, Long last, Long since) {
	
		//Logger.info("getSecured() "+userName+" "+endPointName+" "+path);
		final User user = User.getByUserName(userName);
		if (user == null)
			return notFound();
		
		// removed final keywords to allow reaassignment
		// also, label's will be checked if the device doesnt exist now
		Resource resource = null;
		EndPoint endPoint = EndPoint.getByLabel(user, endPointName);
		if (endPoint != null) { // found a valid endPoint
			resource = Resource.getByPath(endPoint, path);
		} else { // otherwise check if there exists a matching label/alias
			resource = Resource.getByLabel(user, endPointName);
		}
		if (resource == null)
			return notFound();

		if (CheckPermissionsAction.canAccessResource(session("id"), resource.id)) {
			return get(userName, resource.getEndPoint().label, resource.path, tail, last, since);
		} else {
			return CheckPermissionsAction.onUnauthorized();
		}
	}

	@Security.Authenticated(Secured.class)
	  private static Result get(String userName, String endPointName, String path, Long tail, Long last, Long since){
	
		//Logger.info("get()  "+userName+" endPoint:"+endPointName+" path:"+path);
	    final User user = User.getByUserName(userName);
	    if(user == null) return notFound();
	    final EndPoint endPoint = EndPoint.getByLabel(user, endPointName);
	    if(endPoint == null) return notFound();
	    final Resource resource = Resource.getByPath(endPoint, path);
	    if(resource == null) return notFound();
	
	    List<DataPoint> dataSet = null;
	    if(tail<0 && last <0 && since <0){
	    	tail=1L;
	    }
	    if(tail >= 0) {
	    	dataSet = DataPoint.getByStreamTail(resource, tail);
	    } else if(last >= 0) {
	      dataSet = DataPoint.getByStreamLast(resource, last);
	    } else if(since >= 0) {
	    	dataSet = DataPoint.getByStreamSince(resource, since);
	    } else {
	    	throw new RuntimeException("This cannot happen!");
	    }
	    
	    ObjectNode result = Json.newObject();
	    ArrayNode time= result.putArray("time");
	    ArrayNode data= result.putArray("data");
	    
	    for(DataPoint dataPoint: dataSet) {
	    	time.add(dataPoint.timestamp);
	    	data.add(dataPoint.data);

	    }
	    
	    return ok(result);
 

	  }
*/
}
