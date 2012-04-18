package models;
import java.util.*;

import play.db.ebean.*;

import javax.persistence.*;

@Entity
public class Thing extends Model {
  @Id
  public String id;
  public String addr;

  public static Finder<String,Thing> find = new Finder(
      String.class, Thing.class
      );

  public static List<Thing> all() {
    return find.all();
  }

  public static boolean register(String id, String addr) {
    /* Exemple TCP client */
    //Promise<Response> promise = WS.url("http://127.0.0.1:9000/things/test48/addr").get();
    //String body = promise.get().getBody();

    if(find.byId(id) != null) {
      return false;
    } else {
      new Thing(id, addr).save();
      return true;
    }
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

  public Thing(String id, String addr) {
    this.id = id;
    this.addr = addr;
  }
}
