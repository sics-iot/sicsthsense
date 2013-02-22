package models;
import java.util.*;

import javax.persistence.*;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.db.ebean.*;
import scala.reflect.internal.Trees.Super;

@Entity
@Table(name = "sources")
public class Source extends Model {
	 /**
	 * The serialization runtime associates with each serializable class 
	 * a version number, called a serialVersionUID
	 */
	//@Transient
	private static final long serialVersionUID = 6496834518631996535L;
	@Id
	public Lond id;
	
	@ManyToOne
	User owner;
	
	@OneToMany(mappedBy="source", cascade=CascadeType.ALL)
	public List<Stream> outputStreams;

	public Long pollingPeriod=0L;
	public Long lastPolled=0L;
	public String pollingUrl=null;
	public String pollingAuthenticationKey=null;
  
	/** HTML, JSON, RegEx */
	private String inputType=null; // to overide MIME contentType of input
	
	public static Model.Finder<Long,Source> find = new Model.Finder<Long, Source>(Long.class, Source.class);

	public Source(User user) {
		this.owner = user;
	}
	public Source() {
		super();
	}

	public static Source create(User user) {
		Source source = new Source(user);
		source.save();
	}

	public void setInputParser(String inputParser) {
		this.inputParser = inputParser;
		if(this.id != 0) {
			this.update();
		}			
	}
  
	public void setPeriod(Long period) {
		this.pollingPeriod = period;
	}

	private static boolean parseResponse(User currentUser, JsonNode jsonBody, String textBody, String path) {
		if (jsonBody != null) {
			if (!parseJsonResponse(currentUser, jsonBody, path))
				return false;
		} else {
			if (textBody != null) {
				Stream stream = getOrAddByPath(currentUser, path);
				Logger.info("[Posting now] " + textBody + " at " + Utils.currentTime() + " to: " + path);
				return stream.post(Double.parseDouble(textBody), Utils.currentTime());
			} else {
				return false;
			}
		}
		return true;
	}

	private static boolean parseJsonResponse(User currentUser, JsonNode jsonNode, String path) {
		if (jsonNode.isValueNode()) {
			Stream stream = getOrAddByPath(currentUser, path);
			return stream.post(jsonNode.getDoubleValue(), Utils.currentTime());
		} else {
			Iterator<String> it = jsonNode.getFieldNames();
			while (it.hasNext()) {
				String field = it.next();
				if (!parseJsonResponse(currentUser, jsonNode.get(field),	Utils.concatPath(path, field)))
					return false;
			}
		}
		return true;
	}

}
