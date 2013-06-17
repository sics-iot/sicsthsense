package models;

import java.util.regex.Pattern;

import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.elasticsearch.common.regex.Regex;

import controllers.Utils;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import protocol.Request;
import protocol.Response;

public class Representation extends Model {
    /**
     * 
     */
    private static final long serialVersionUID = -1198020774968640869L;
    
    @Id
    public long id;
    @Required
    public long timestamp;
    @Required
    public long expires;
    @Required
    public String contentType;
    @Required
    public String content;
    @ManyToOne
    public Resource parent = null;

    public static final Model.Finder<Long, Representation> find =
            new Model.Finder<Long, Representation>(Long.class, Representation.class);

    public long getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getExpires() {
        return expires;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }

    public Resource getParent() {
        return parent;
    }
    
    public static Representation fromRequest(Request req) {
        Representation repr = new Representation();
        
        repr.content = req.body();
        repr.contentType = req.contentType();
        repr.expires = 0;
        repr.timestamp = Utils.currentTime();
        
        return repr;
    }
    
    public static Representation fromResponse(Response res) {
        Representation repr = new Representation();
        
        repr.content = res.body();
        repr.contentType = res.contentType();
        repr.expires = res.expires();
        repr.timestamp = res.receivedAt();
        
        return repr;
    }

    public static Representation getByResourceId(long id) {
        return find.where().eq("resource", id).findUnique();
    }
}
