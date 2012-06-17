package controllers;

//import models.Project;
//import models.Task;
//import models.User;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.*;

import play.libs.F.*;
import play.libs.*;
import play.mvc.*;

import models.*;

public class Streams extends Controller {
  
  public static void pollAll() {
    List<Resource> withPolling = Resource.find.where()
        .gt("pollingPeriod", 0)
        .findList();
    for(Resource resource: withPolling) {
      poll(resource);
    }      
  }
  
  public static void poll(final Resource resource) {
    final EndPoint endPoint = resource.getEndPoint();
    final long current = Utils.currentTime();
    //Logger.info("[Streams] periodic timer: " + resource.fullPath() + ", period: " + resource.pollingPeriod + ", last polled: " + (current-resource.lastPolled));
    if (current >= resource.lastPolled + resource.pollingPeriod) {
      final String url = Utils.concatPath(endPoint.url,resource.path);
      Logger.info("[Streams] polling: " + resource.fullPath() + ", URL: " + url);
      WS.url(url).get().map(
        new Function<WS.Response, Boolean>() {
          public Boolean apply(WS.Response response) {
            JsonNode jsonBody = response.asJson();
            String textBody = response.getBody();
            Logger.info("[Streams] polling response for: " + resource.fullPath() + ", content type: " + response.getHeader("Content-Type") + ", payload: " + textBody);
            parseResponse(endPoint, jsonBody, textBody, resource.path);
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
    Logger.info("[Streams] new sample for: " + fullPath + ", data: " + data);
    Resource resource = endPoint.getOrCreateResource(path);
    resource.post(data, Utils.currentTime());
  }
  
  public static boolean parseResponse(EndPoint endPoint, JsonNode jsonBody, String textBody, String path) {
    if(jsonBody != null) {
      if(!parseJsonResponse(endPoint, jsonBody, path)) return false;
    } else {
      if(textBody != null) {
        insertSample(endPoint, path, Float.parseFloat(textBody));
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
      Logger.info("[Streams] post received from: " + Utils.concatPath(userName, endPointName, path) + ", content type: " + request().getHeader("Content-Type") + ", payload: " + textBody);
      if(!parseResponse(endPoint, jsonBody, textBody, path)) return badRequest("Bad request");
    } catch (Exception e) {
      return badRequest("Bad request");
    }
    return ok("ok");
  }
  
  public static Result get(String userName, String endPointName, String path, Long tail, Long last, Long since) {
    final User user = User.getByUserName(userName);
    if(user == null) return notFound();
    final EndPoint endPoint = EndPoint.getByLabel(user, endPointName);
    if(endPoint == null) return notFound();
    final Resource resource = Resource.getByPath(endPoint, path);
    if(resource == null) return notFound();
         
    ObjectNode result = Json.newObject();
    ArrayNode array = result.putArray(path);
    List<DataPoint> dataSet = null;
    
    if(tail < 0) tail = 0L;
    if(last < 0) last = 0L;
    if(since < 0) since = 0L;
    if(tail == 0 && last == 0 && since ==0) tail = 1L; /* Default behavior: return the last item only */
    
    if(tail > 0) {
      dataSet = DataPoint.getByStreamTail(resource, tail);
    } else if(last > 0) {
      dataSet = DataPoint.getByStreamLast(resource, last);
    } else if(since > 0) {
      dataSet = DataPoint.getByStreamSince(resource, since);
    } else {
      dataSet = DataPoint.getByStream(resource);
    }
    
    if(dataSet.size() == 0) { /* If no data, return the most recent data */
      dataSet = DataPoint.getByStreamTail(resource, 1);
    }
    
    for(DataPoint dataPoint: dataSet) {
      ObjectNode e = Json.newObject();
      e.put(new Long(dataPoint.timestamp).toString(), dataPoint.data);
      array.add(e);
    }
    
    return ok(result);
  }
  
}
