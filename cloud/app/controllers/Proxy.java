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
  
  public static Result forward(final EndPoint endPoint, final String path) {
    /* The two next lines are commented out, enabling proxying to arbitrary path */
//    final Resource resource = Resource.getByPath(endPoint, path);
//    if(resource == null) return notFound();
    final String method = request().method();
    final String body = request().body().asText();
    final String contentType = request().getHeader("Content-Type");
    return async(
        Akka.future(
          new Callable<Result>() {
            public Result call() {
              String url = Utils.concatPath(endPoint.url, path);
              Logger.info("Proxy: forwarding " + method + " to: " + url + " with body: " + body);
              try {
                Promise<Response> promise = null;
                WSRequestHolder request = WS.url(url).setHeader("Content-Type", contentType);
                if (method.equals("GET")) { promise = request.get(); }
                else if (method.equals("POST")) { promise = request.post(body); }
                else if (method.equals("PUT")) { promise = request.put(body); }
                else if (method.equals("DELETE")) { promise = request.delete(); }
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
  
  public static Result forwardByPath(String userName, String endPointName, final String path) {
    final User user = User.getByUserName(userName);
    if(user == null) return notFound();
    final EndPoint endPoint = EndPoint.getByLabel(user, endPointName);
    if(endPoint == null) return notFound();
    return forward(endPoint, path);  
  }
    
  public static Result forwardById(Long id, String arguments) {
    Resource resource = Resource.get(id);
    return forward(resource.getEndPoint(), Utils.concatPath(resource.path,arguments));
  }
  
}
