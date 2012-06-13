package controllers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.codehaus.jackson.JsonNode;

import com.ning.http.client.Request;

import play.*;

import play.core.Router.Routes;
import play.libs.F.*;
import play.libs.*;
import play.libs.F.Promise;
import play.libs.WS.Response;
import play.libs.WS.WSRequestHolder;
import play.mvc.*;
import play.mvc.Http.RequestBody;
import play.data.*;

import models.*;
import views.html.*;

public class Proxy extends Controller {
    
  public static Result forward(String userName, String label, final String path, final String method) {
    final User user = User.getByUserName(userName);
    if(user == null) return notFound();
    final EndPoint endPoint = EndPoint.getByLabel(user, label);
    if(endPoint == null) return notFound();
    final Resource resource = Resource.getByPath(endPoint, path);
    if(resource == null) return notFound();
    final String body = request().body().asText();
    return async(
        Akka.future(
          new Callable<Result>() {
            public Result call() {
              String url = Utils.concatPath(endPoint.url, path);
              Logger.info("Proxy: forwarding " + method + " to: " + url);
              try {
                Promise<Response> promise = null;
                switch(method) {
                  case "GET":     promise = WS.url(url).get(); break;
                  case "POST":    promise = WS.url(url).post(body); break;
                  case "PUT":     promise = WS.url(url).put(body); break;
                  case "DELETE":  promise = WS.url(url).delete(); break;
                }
                Response response = promise.get();
                return status(response.getStatus(), response.getBody());
              } catch (Exception e) {
                return badRequest();
              }
            }
          }
        )
      );
  }
  
  public static Result get(String userName, String label, String path) {
    return forward(userName, label, path, "GET");
  }
  
  public static Result post(String userName, String label, String path) {
    return forward(userName, label, path, "POST");
  }
  
  public static Result put(String userName, String label, String path) {
    return forward(userName, label, path, "PUT");
  }
  
  public static Result delete(String userName, String label, String path) {
    return forward(userName, label, path, "DELETE");
  }
    
}
