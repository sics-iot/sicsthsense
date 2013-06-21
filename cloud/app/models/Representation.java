package models;

import controllers.Utils;
import logic.Argument;
import logic.State;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import protocol.Request;
import protocol.Response;

import javax.persistence.*;

@Entity
@Table(name = "representations")
public class Representation extends Model {
    private static final int BODY_MAX_LENGTH = 8 * 1024;
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
    @Column(length = BODY_MAX_LENGTH)
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

    private void normalize() {
        State.notNull(content);

        content = content.substring(0, Math.min(BODY_MAX_LENGTH, content.length()));
    }

    public static Representation fromRequest(Request req, Resource parent) {
        Representation repr = new Representation();

        repr.content = req.body();
        repr.contentType = req.contentType();
        repr.expires = 0;
        repr.timestamp = Utils.currentTime();
        repr.parent = parent;

        repr.normalize();

        return repr;
    }

    public static Representation fromResponse(Response res, Resource parent) {
        Representation repr = new Representation();

        repr.content = res.body();
        repr.contentType = res.contentType();
        repr.expires = res.expires();
        repr.timestamp = res.receivedAt();
        repr.parent = parent;

        repr.normalize();

        return repr;
    }

    public static Representation getByResourceId(long id) {
        return find.where().eq("resource", id).findUnique();
    }
}
