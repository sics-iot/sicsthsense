package models;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import play.db.ebean.Model;
import com.avaje.ebean.Ebean;
import controllers.Utils;

@Entity
public class DataLog extends Model {

    private static final long serialVersionUID = 1L;

    public enum Result {
        OK,
        PARSE_ERROR
    }

    public enum Type {
        PUSH,
        POLL
    };

    @Id
    public Long id;

    @ManyToOne
    public EndPoint endPoint;

    public long timestamp;
    public Result result;
    public Type requestType;
    public String host;

    @Lob
    public String data;

    public static final Model.Finder<Long,DataLog> find = new Model.Finder<Long, DataLog>(Long.class, DataLog.class);

    public DataLog(EndPoint endPoint, long timestamp, Type type, Result result, String host, String data) {
      this.endPoint = endPoint;
      this.timestamp = timestamp;
      this.requestType = type;
      this.result = result;
      this.data = data;
    }

    public static DataLog add(EndPoint endPoint, long timestamp, Type type, Result result, String host, String data) {
      DataLog dataLog = new DataLog(endPoint, timestamp, type, result, host, data);
      dataLog.save();
      return dataLog;
    }

    public static List<DataLog> getByEndPoint(EndPoint endPoint) {
      return find.where()
          .eq("endPoint", endPoint)
          .orderBy("timestamp desc")
          .findList();
    }

    public static List<DataLog> getByEndPointTail(EndPoint endPoint, int tail) {
      List<DataLog> set = find.where()
          .eq("endPoint", endPoint)
          .orderBy("timestamp desc").setMaxRows(tail)
          .findList();
      return set;
    }

    public static List<DataLog> getByEndPointLast(EndPoint endPoint, long last) {
      return getByEndPointSince(endPoint, Utils.currentTime() - last);
    }

    public static List<DataLog> getByEndPointSince(EndPoint endPoint, long since) {
      return find.where()
          .eq("endPoint", endPoint)
          .ge("timestamp", since)
          .orderBy("timestamp desc")
          .findList();
    }

    public static void deleteByEndPoint(EndPoint endPoint) {
      List<DataLog> list = find.select("id").where()
          .eq("endPoint", endPoint)
          .findList();
      for(DataLog element: list) {
          Ebean.delete(DataLog.class, element.id);
      }
    }

}

