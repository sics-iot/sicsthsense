package models;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import controllers.Utils;

import javax.persistence.*;

import com.avaje.ebean.*;
import com.avaje.ebean.annotation.EnumValue;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.db.ebean.*;
import play.Logger;
import play.libs.*;
import play.libs.WS.WSRequestHolder;
import play.mvc.Http.Request;


@Entity
@Table(name = "parsers")
public class StreamParser extends Model {
	
	

	/**
	 * The serialization runtime associates with each serializable class a version
	 * number, called a serialVersionUID
	 */
	private static final long serialVersionUID = 2972078391685154152L;
//	public static enum parserType {
//		@EnumValue("R")
//		REGEX,
//		@EnumValue("J")
//		JSON,
//		@EnumValue("X")
//		XPATH
//	};

	@Id
	public Long id;

	@ManyToOne
	public Source source;

	@ManyToOne
	public Stream stream;

	/** RegEx, Xpath, JSON path */
	public String inputParser;

	/**
	 * JSON, HTML, text, XML, ... to override MIME contentType of input Right now,
	 * it could be defined as application/json, otherwise, request's content is
	 * handled as text
	 */
	public String inputType = null;

	@Transient
	public Pattern regexPattern;

	public StreamParser() {
		super();
	}

	public StreamParser(Source source, String inputParser, String inputType, String path) {
		super();
		setInputParser(inputParser);
		this.inputType = inputType;
		this.source = source;
		this.stream = getOrCreateStreamFile(path).linkedStream;
	}

	public StreamParser(Source source, String inputParser, String inputType, Stream stream) {
		super();
		setInputParser(inputParser);
		this.inputType = inputType;
		this.source = source;
		this.stream = stream;
	}

	public static StreamParser create(StreamParser parser) {
		if (parser.source != null && parser.inputParser != null) {
			parser.save();
			return parser;
		}
		return null;
	}

	public boolean setInputParser(String inputParser) {
		this.inputParser = inputParser;
		if (inputParser != null) {
			regexPattern = Pattern.compile(inputParser);
			if (this.id != null) {
				this.update();
			}
			return true;
		}
		return false;
	}

	/**
	 * parseResponse(Request req) chooses the parser based on content-type.
	 * inputType overrides the content-type. returns: true if could post
	 */
	/*
	public boolean parseResponse(Request req) {
		try {
			if ("application/json".equalsIgnoreCase(inputType)
					|| "application/json".equalsIgnoreCase(req.getHeader("Content-Type")) ) {
				JsonNode jsonBody = req.body().asJson();
				return parseJsonResponse(jsonBody);
			} else {
				String textBody = req.body().asText();
				return parseTextResponse(textBody);
			}
		} catch (Exception e) {
			Logger.info("[Streams] Exception " + e.getMessage());
		}
		return false;
	}*/
	public boolean parseRequest(Request request) {
		try {
			if ("application/json".equalsIgnoreCase(inputType)
					|| "application/json".equalsIgnoreCase(request.getHeader("Content-Type")) ) {
				JsonNode jsonBody = request.body().asJson();
				//Logger.info("[StreamParser] as json");
				return parseJsonResponse(jsonBody);
			} else {
				String textBody = request.body().asText(); //request.body().asRaw().toString();
				//Logger.info("[StreamParser] as text");
				return parseTextResponse(textBody);
			}
		} catch (Exception e) {
			Logger.info("[StreamParser] Exception " + e.getMessage() + e.getStackTrace()[0].toString());
		}
		return false;
	}
	/**
	 * parseResponse(Request req) chooses the parser based on content-type.
	 * inputType overrides the content-type. returns: true if could post
	 */
	public boolean parseResponse(WS.Response response) {
		try {
			if ("application/json".equalsIgnoreCase(inputType)
					|| "application/json".equalsIgnoreCase(response.getHeader("Content-Type")) ) {
				JsonNode jsonBody = response.asJson();
				return parseJsonResponse(jsonBody);
			} else {
				String textBody = response.getBody();
				return parseTextResponse(textBody);
			}
		} catch (Exception e) {
			Logger.info("[StreamParser] Exception " + e.getMessage() + e.getStackTrace()[0].toString());
		}
		return false;
	}
/**
 * Parses the request using inputParser as regex and posts the first match
 *  
 * @param textBody
 * @return true if could post
 */
	private boolean parseTextResponse(String textBody) {
		if (inputParser != null && !inputParser.equalsIgnoreCase("") ) {
			regexPattern = Pattern.compile(inputParser);
			Matcher matcher = regexPattern.matcher(textBody);
			if (textBody != null && matcher.find()) {
				String result = matcher.group(1);
				return stream.post(Double.parseDouble(result), Utils.currentTime());
			}
		} else {
			return stream.post(Double.parseDouble(textBody), Utils.currentTime());
		}
		return false;
	}

	/*
	 * parses requests as JSON inputParser is used as the path to the nested json
	 * node i.e. inputParser could be: room1/sensors/temp/value
	 */
	private boolean parseJsonResponse(JsonNode jsonNode) {
		// TODO check concat path against inputParser, get the goal and stop
		// TODO (linear time) form a list of nested path elements from the gui, and
		// get the specefic nodes one by one...
		// String path = "";
		// Iterator<String> it = jsonNode.getFieldNames();
		// while (it.hasNext()) {
		// String field = it.next();
		// path += field;
		// jsonNode = jsonNode.get(field);
		//
		// if (jsonNode != null && jsonNode.isValueNode() &&
		// path.equalsIgnoreCase(inputParser)) {
		// Stream stream = vfile.getLink();
		// return stream.post(jsonNode.getDoubleValue(), Utils.currentTime());
		// } else {
		// path += ".";
		// }
		// }
		// return false;
		String pathParser = "([\\d\\w\\s-]+)+\\\\?$";
		Pattern pathPattern = Pattern.compile(pathParser);
		Matcher matcher = pathPattern.matcher(inputParser);
		int i = 0;
		while (i < matcher.groupCount() && matcher.find() && jsonNode != null) {
			String field = matcher.group(++i);
			jsonNode = jsonNode.path(field);
			if (jsonNode != null && jsonNode.isValueNode()) {
				return stream.post(jsonNode.getDoubleValue(), Utils.currentTime());
			}
		}
		return false;
	}

	private Vfile getOrCreateStreamFile(String path) {
		if (source == null || (source != null && source.owner == null)) {
			Logger.error("[StreamParser] user does not exist.");
			return null;
		}
		Vfile f = FileSystem.readFile(source.owner, path);
		if (f == null) {
			f = FileSystem.addFile(source.owner, path);
		} else if (f.getType() == Vfile.Filetype.DIR) {
			int i = 0;
			do {
				f = FileSystem.addFile( source.owner, path + "\\newstream" + Integer.toString(i) );
			} while( !FileSystem.fileExists( source.owner, path + "\\newstream" + Integer.toString(i++) ) );
		}
		if (f.getType() == Vfile.Filetype.FILE) {
			Stream stream = f.getLink();
			if (stream == null) {
				stream = Stream.create(new Stream(source.owner, this.source));
				f.setLink(stream);
				Logger.info("[StreamParser] Creating stream at: " + source.owner.getUserName()
						+ path);
			}
			return f;
		}
		Logger.error("[StreamParser] couldn't get or create a stream file in " + path);
		return null;
	}
}
