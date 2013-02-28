package models;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import controllers.Utils;

import models.*;

import javax.persistence.*;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.db.ebean.*;
import scala.reflect.internal.Trees.Super;
import play.Logger;

import play.mvc.Http.Request;

@Entity
@Table(name = "sources")
public class Source extends Model {

	/**
	 * The serialization runtime associates with each serializable class a version
	 * number, called a serialVersionUID
	 */
	// @Transient
	private static final long serialVersionUID = 6496834518631996535L;
	@Id
	public Long id;

	@ManyToOne
	public User owner;

	@OneToMany(mappedBy = "source", cascade = CascadeType.ALL)
	public List<Stream> outputStreams = new ArrayList<Stream>();

	public Long pollingPeriod = 0L;
	public Long lastPolled = 0L;
	public String pollingUrl = null;
	public String pollingAuthenticationKey = null;

	@OneToMany(mappedBy = "source", cascade = CascadeType.ALL)
	public List<StreamParser> streamParsers = new ArrayList<StreamParser>();

	/** Secret token for authenticating posts coming from outside */
	private String token;

	public static Model.Finder<Long, Source> find = new Model.Finder<Long, Source>(
			Long.class, Source.class);

	/** Call to create, or update an access token */
	private String updateToken() {
		String newtoken = UUID.randomUUID().toString();
		token = newtoken;
		if(id > 0) {
			this.update();
		}
		return token;
	}
	
	public Source(User owner, Long pollingPeriod,
			 String pollingUrl, String pollingAuthenticationKey) {
		super();
		this.owner = owner;
		this.pollingPeriod = pollingPeriod;
		this.lastPolled = 0L;
		this.pollingUrl = pollingUrl;
		this.pollingAuthenticationKey = pollingAuthenticationKey;
	}

	protected String getToken() {
		return token;
	}

	public void updateSource(Source source) {
		this.pollingPeriod = source.pollingPeriod;
		this.lastPolled = source.lastPolled;
		this.pollingUrl = source.pollingUrl;
		this.pollingAuthenticationKey = source.pollingAuthenticationKey;
		if(token == null || "".equalsIgnoreCase(token)) {
			updateToken();
		}
		update();
	}
	
	public Boolean checkToken(String token) {
		return token == this.token;
	}

	public static Source get(Long id, String key) {
		Source source = find.byId(id);
		if (source != null && source.checkToken(key))
			return source;
		return null;
	}

	public static Source get(Long id, User user) {
		Source source = find.byId(id);
		if (source != null && source.owner == user)
			return source;
		return null;
	}

	public Source(User user) {
		this.owner = user;
	}

	public Source() {
		super();
	}

	public static Source create(User user) {
		if (user != null) {
			Source source = new Source(user);
			source.save();
			return source;
		}
		return null;
	}
	
	public static Source create(Source source) {
		if (source.owner != null) {
			source.save();
			source.updateToken();
			return source;
		}
		return null;
	}

	public void setPeriod(Long period) {
		this.pollingPeriod = period;
	}

	public boolean parseAndPost(Request req) {
		boolean result = false;
		for (StreamParser sp : streamParsers) {
			if (sp != null) {
				result |= sp.parseResponse(req);
			}
		}
		return result;
	}

}
