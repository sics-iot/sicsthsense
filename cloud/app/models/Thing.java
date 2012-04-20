package models;
import java.util.*;

import play.libs.F;
import play.libs.F.Promise;
import play.libs.WS.Response;
import play.api.libs.ws.*;
import play.db.ebean.*;
import play.libs.WS;
import scala.collection.mutable.LinkedList;

import javax.persistence.*;

import akka.dispatch.*;

@Entity
public class Thing extends Model {
  @Id
  public String id;
  public String url;
  public String resources;
  
  public static Finder<String,Thing> find = new Finder(
      String.class, Thing.class
      );

  public static List<Thing> all() {
    return find.all();
  }

  public static boolean register(String url) {
    String id;
    try {
      Promise<Response> p = WS.url(url).get();
      id = p.get().getBody();
    } catch (Exception e) {
      return false;
    }
    if(find.byId(id) != null) {
      return false;
    } else {
      new Thing(id, getBaseUrl(url)).save();
      return true;
    }
  }

  private static String getBaseUrl(String url) {
    String[] split = url.split("/");
    return split[0] + "//" + split[2];
  }

  public static boolean remove(String id) {
    if(find.byId(id) != null) {
      find.byId(id).delete();
      return true;
    } else {
      return false;
    }
  }

  public static Thing get(String id) {
    return find.byId(id);
  }
  
  public void fetchResources() {
    Promise<Response> p = WS.url(url + "/resources").get();
    p.onRedeem(
        new F.Callback<Response>() {
          public void invoke(Response response) throws Throwable {
            resources = response.getBody();
            save();
          }
        }
      );
  }

  public Thing(String id, String url) {
    this.id = id;
    this.url = url;
    this.resources = "";
    this.fetchResources();
  }
}
