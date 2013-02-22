package models;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.*;

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
	public Long id;
	
	@ManyToOne
	public User owner;
	
	@OneToMany(mappedBy="source", cascade=CascadeType.ALL)
	public List<Stream> outputStreams;

	public Long pollingPeriod=0L;
	public Long lastPolled=0L;
	public String pollingUrl=null;
	public String pollingAuthenticationKey=null;
  
	/** HTML, JSON */
	private String inputType=null; // to overide MIME contentType of input
	
	/** RegEx, Xpath */
	private String inputParser=null; // to parse html or xml input when posting
	
	/** Secret token for authenticating posts coming from outside */
	private String token; 
	
	public static Model.Finder<Long,Source> find = new Model.Finder<Long, Source>(Long.class, Source.class);

	/** Call to create, or update an access token */
	protected String createToken() {
		token = UUID.randomUUID().toString();
		save();
		return token;
	}
	
	protected String getToken() {
		return token;
	}
	
	public Boolean checkToken(String token) {
		return token == this.token;
	}
	
	public static Source get(Long id, String key) {
		//TODO
		Source source = find.byId(id);
		if(source != null && source.checkToken(key))
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

	private boolean parseResponse(JsonNode jsonBody,
			String textBody, String path) {
		if (jsonBody != null && parseJsonResponse(jsonBody, path)) {			
				return true;
		} else if (textBody != null) {
				if (inputParser != null && !inputParser.equals("")) {
					Pattern pattern = Pattern.compile(inputParser);
					Matcher matcher = pattern.matcher(textBody);
					if (matcher.find()) {
						Stream stream;
						int numberOfStreams = matcher.groupCount();
						if (numberOfStreams == 1) {
							stream = getOrAddByPath(path);
							textBody = matcher.group(1);
							return stream.post(Double.parseDouble(textBody),
									Utils.currentTime());
						} else {
							for (int i = 1; i <= numberOfStreams; i++) {
								stream = getOrAddByPath(path + "\stream" + Integer.toString(i));
								textBody = matcher.group(i);
								Logger.info("[Posting now] " + textBody + " at " + Utils.currentTime()
										+ " to: " + path + "\stream" + Integer.toString(i));
								stream.post(Double.parseDouble(textBody),
										Utils.currentTime());
							}
							return true;
						}
					}
				}
			}
		return false;			
	}

	private boolean parseJsonResponse(JsonNode jsonNode, String path) {
		if (jsonNode.isValueNode()) {
			Stream stream = getOrAddByPath(path);
			return stream.post(jsonNode.getDoubleValue(), Utils.currentTime());
		} else {
			Iterator<String> it = jsonNode.getFieldNames();
			while (it.hasNext()) {
				String field = it.next();
				if (!parseJsonResponse(jsonNode.get(field),	Utils.concatPath(path, field)))
					return false;
			}
		}
		return true;
	}
	
	private Stream getOrCreateByPath(String path) {
		if (owner == null) {
			Logger.error("[Source] user does not exist.");
			return null;
		}
		Vfile f = FileSystem.readFile(owner, path);
		if (f == null) {
			f = FileSystem.addFile(owner, path);
		}
		if (f.getType() == Filetype.FILE) {
			Stream stream = f.getLink();
			if (stream == null) {
				stream = Stream.create(new Stream(owner, this));
				f.setLink(stream);
				Logger.info("[Source] Creating stream at: " + owner.getUserName()
						+ path);
			}
			return stream;
		}
		Logger.error("[Source] path points to a directory.");
		return null;
	}
}
