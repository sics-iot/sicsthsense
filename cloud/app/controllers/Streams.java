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

import com.ning.http.client.Request;

import play.*;

import play.core.Router.Routes;
import play.libs.F.*;
import play.libs.*;
import play.mvc.*;
import play.mvc.Http.RequestBody;
import play.data.*;

import models.*;
import views.html.*;

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
    Logger.info("Periodic " + endPoint.label + "/" + resource.path + " " + resource.pollingPeriod + " " + (current-resource.lastPolled));
    if (current >= resource.lastPolled + resource.pollingPeriod) {
      String url = Utils.concatPath(endPoint.url,resource.path);
      Logger.info("Now sampling " + url);
      WS.url(url).get().map(
        new Function<WS.Response, Boolean>() {
          public Boolean apply(WS.Response response) {
            JsonNode jsonBody = response.asJson();
            String textBody = response.getBody();
            insertData(endPoint, jsonBody, textBody, resource.path);
            return true;
          }
        }
      );
      resource.lastPolled = current;
      resource.update();
    }
  }
  
  public static boolean insertData(EndPoint endPoint, JsonNode jsonBody, String textBody, String path) {
    if(jsonBody != null) {
      if(!insertDataFromJson(endPoint, jsonBody, path)) return false;
    } else {
      if(textBody != null) {
        float data = Float.parseFloat(textBody);
        Resource resource = endPoint.getOrCreateResource(path);
        resource.post(data, Utils.currentTime());
      } else {
        return false;
      }
    }
    return true;
  }
  
  public static boolean insertDataFromJson(EndPoint endPoint, JsonNode jsonNode, String path) {
    if(jsonNode.isValueNode()) {
      float data = (float)jsonNode.getDoubleValue();
      Resource resource = endPoint.getOrCreateResource(path);
      resource.post(data, Utils.currentTime());
    } else {
      Iterator<String> it = jsonNode.getFieldNames();
      while(it.hasNext()) {
        String field = it.next();
        if(!insertDataFromJson(endPoint, jsonNode.get(field), Utils.concatPath(path, field))) return false;
      }
    }
    return true;
  }
  
  public static Result post(String userName, String label, String path) {
    User user = User.getByUserName(userName);
    if(user == null) return notFound();
    EndPoint endPoint = EndPoint.getOrCreateByLabel(user, label);
    try {
      JsonNode jsonBody = request().body().asJson();
      String textBody = request().body().asText();
      if(!insertData(endPoint, jsonBody, textBody, path)) return badRequest("Bad request");
    } catch (Exception e) {
      return badRequest("Bad request");
    }
    
    return ok("ok");
  }
  
  public static Result get(String userName, String label, String path, Long n, Long t) {
    final User user = User.getByUserName(userName);
    if(user == null) return notFound();
    final EndPoint endPoint = EndPoint.getByLabel(user, label);
    if(endPoint == null) return notFound();
    final Resource resource = Resource.getByPath(endPoint, path);
    if(resource == null) return notFound();
        
    long current = Utils.currentTime();
    long since = current - t;
    ObjectNode result = Json.newObject();
    ArrayNode array = result.putArray(path);
    List<DataPoint> dataSet = null;
    if(n > 0) {
      dataSet = DataPoint.getSubsetByStreamN(resource, n);
    } else if(t > 0) {
      dataSet = DataPoint.getSubsetByStreamSince(resource, since);
    } else {
      dataSet = DataPoint.getByStream(resource);
    }
    for(DataPoint dataPoint: dataSet) {
      ObjectNode e = Json.newObject();
      e.put(new Long(dataPoint.timestamp).toString(), dataPoint.data);
      array.add(e);
    }
    
    return ok(result);
  }
  
}
