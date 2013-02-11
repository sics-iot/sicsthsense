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
  
  public static Result forward(final Resource resource, final Map<String, String> queryParameters) {
    /* The two next lines are commented out, enabling proxying to arbitrary path */
//    final Resource resource = Resource.getByPath(endPoint, path);
//    if(resource == null) return notFound();
    final String method = request().method();
    final String body = request().body().asText();
    final Map<String, String[]> headers = request().headers();;
    final String url = resource.getUrl();
    return async(
        Akka.future(
          new Callable<Result>() {
            public Result call() {
              Logger.info("[Proxy] forwarding method: " + method + ", to: " + url + ", body: " + body);
              try {
                Promise<Response> promise = null;
                WSRequestHolder request = WS.url(url);
                if(queryParameters != null) {
                  for(String name: queryParameters.keySet()) {
                    request.setQueryParameter(name, queryParameters.get(name));
                  }
                }
                for(String name: headers.keySet()) {
                  if(name.equals("ACCEPT-ENCODING")) request = request.setHeader(name, ""); /* Don't accept gzip, not supported yet by play 2.0.4 requests */
                  else if(!name.equals("HOST")) request = request.setHeader(name, headers.get(name)[0]); /* Forge host */
                }
                if (method.equals("GET")) { promise = request.get(); }
                else if (method.equals("POST")) { promise = request.post(body); }
                else if (method.equals("PUT")) { promise = request.put(body); }
                else if (method.equals("DELETE")) { promise = request.delete(); }
                Response response = Akka.asPromise(promise.getWrappedPromise()).get(10000L, TimeUnit.MILLISECONDS);
                String encoding = response.getHeader("Content-encoding"); 
                Logger.info("[Proxy] got response for: " + method + ", to: " + url + ", encoding: " + encoding + ", body: " + response.getBody().length() + " bytes");
                String body = response.getBody();
                return status(response.getStatus(), body);
              } catch (Exception e) {
                Logger.info("[Proxy] forwarding failed: " + e.getMessage());
                return badRequest(e.getMessage());
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
    final Resource resource = Resource.getByPath(endPoint, path);
    if(resource == null) return notFound();
    return forward(resource, null);  
  }
    
  public static Result forwardById(Long id, String arguments) {
    Resource resource = Resource.get(id);
    if(resource == null) return notFound();
    Pattern pattern = Pattern.compile("([^&?=]+)=([^?&]+)");
    Matcher matcher = pattern.matcher(arguments);
    Map<String, String> queryParameters = new HashMap<String, String>();
    while (matcher.find()) {
      queryParameters.put(matcher.group(1), matcher.group(2));
    } 
    return forward(resource, queryParameters);
  }
  
}
