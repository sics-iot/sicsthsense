package models;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.*;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.*;

import play.db.ebean.*;
import play.libs.F.*;
import play.libs.*;
import play.libs.WS.WSRequestHolder;
import play.mvc.*;
import play.mvc.Http.Request;

import models.*;
import controllers.*;
import views.html.*;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;

@Entity
@Table(name = "sources", uniqueConstraints = { 
	@UniqueConstraint(columnNames = {"owner_id", "label" }) 
	})
public class Source extends Operator {

	@Id
  public Long id;

  @ManyToOne//(cascade = CascadeType.ALL) 
  public User owner;

	/**
	 * The serialization runtime associates with each serializable class a version
	 * number, called a serialVersionUID
	 */
  private static final long serialVersionUID = 7683451697925144957L;
	@Required
  public String label = "NewSource";
  public Long pollingPeriod = 0L;
  public Long lastPolled = 0L;
	public String pollingUrl = null;
	public String pollingAuthenticationKey = null;
	public String description; // dont use!

	@OneToMany(mappedBy = "source", cascade = CascadeType.ALL)
	public List<StreamParser> streamParsers = new ArrayList<StreamParser>();

	@OneToMany(mappedBy = "source")
	public List<Stream> streams = new ArrayList<Stream>();

	/** Secret key for authenticating posts coming from outside */
	public String key;

	public static Model.Finder<Long, Source> find = new Model.Finder<Long, Source>(
			Long.class, Source.class);
	
	public Source(User owner, String label, Long pollingPeriod,
			 String pollingUrl, String pollingAuthenticationKey) {
		super();
		this.label = label;
		this.owner = owner;
		this.pollingPeriod = pollingPeriod;
		this.lastPolled = 0L;
		this.pollingUrl = pollingUrl;
		this.pollingAuthenticationKey = pollingAuthenticationKey;
	}

	public Source(String label, Long pollingPeriod,
			 String pollingUrl, String pollingAuthenticationKey) {
		this(null, label, pollingPeriod, pollingUrl, pollingAuthenticationKey);
	}

	public Source(User user) {
		this.owner = user;
		this.lastPolled=0L;
	}

	public Source() {
		super();
		this.lastPolled=0L;
	}


	/** Call to create, or update an access token */
	private String updateKey() {
		String newKey = UUID.randomUUID().toString();
		key = newKey;
		if(id > 0) {
			this.update();
		}
		return key;
	}
	
	public String getKey() {
		return key;
	}

	public void updateSource(Source source) {
		this.label = source.label;
		//this.key = source.getKey();
		this.pollingPeriod = source.pollingPeriod;
		this.lastPolled = source.lastPolled;
		this.pollingUrl = source.pollingUrl;
		this.pollingAuthenticationKey = source.pollingAuthenticationKey;
		if(key == null || "".equalsIgnoreCase(key)) {
			updateKey();
		}
		update();
	}

	// construct a synchronous connnection to the URL to be probed in rela time
	public HttpURLConnection probe() {
		Logger.warn("probe(): "+pollingUrl);
		HttpURLConnection connection = null;  
		PrintWriter outWriter = null;  
		BufferedReader serverResponse = null;  
		StringBuffer returnBuffer = new StringBuffer();  
		String line;  
		User currentUser = Secured.getCurrentUser();

		try { 
			connection = ( HttpURLConnection ) new URL( pollingUrl ).openConnection();  
			connection.setRequestMethod( "GET" );  
			//connection.setDoOutput( true );  
			/*	//CREATE A WRITER FOR OUTPUT  
			outWriter = new PrintWriter( connection.getOutputStream() );  
			buff.append( "param1=" );   
			buff.append( URLEncoder.encode( "Param 1 Value", "UTF-8" ) );  
			buff.append( "&" );  
			buff.append( "param2=" );   
			buff.append( URLEncoder.encode( "Param 2 Value", "UTF-8" ) );  
			outWriter.println( buff.toString() );  
			outWriter.flush();  
			outWriter.close();  */	
			return connection;

		} catch (MalformedURLException mue) {  
			Logger.error(mue.toString() + " Stack trace:\n" + mue.getStackTrace()[0].toString() );  
		  //return badRequest("Malformed URL");
		} catch (IOException ioe) {  
			Logger.error(ioe.toString() + " Stack trace:\n" + ioe.getStackTrace()[0].toString() );
		  //return badRequest("IO Exception on probe()");
		} finally {  
			if (connection!=null) connection.disconnect();  	
			if (serverResponse!=null) { try {serverResponse.close();} catch (Exception ex) {}  }  
		}  
		return null;
	}

	// register asychronous polling of data
	public void asynchPoll() {
      String arguments = "";
			String path = "";
      Map<String, String> queryParameters = new HashMap<String, String>();

      if(pollingUrl.indexOf('?') != -1) {
    	  arguments = pollingUrl.substring(pollingUrl.indexOf('?')+1, pollingUrl.length());
      }
      //Logger.info("[Stream] polling, URL: " + pollingUrl + " args: "+arguments);
      WSRequestHolder request = WS.url(pollingUrl);
      Pattern pattern = Pattern.compile("([^&?=]+)=([^?&]+)");
      Matcher matcher = pattern.matcher(arguments);
      while (matcher.find()) { request.setQueryParameter(matcher.group(1), matcher.group(2)); } 

      request.get().map(
        new F.Function<WS.Response, Boolean>() {
          public Boolean apply(WS.Response response) {
						String textBody = response.getBody();
						//Logger.info("Incoming data: " + response.getHeader("Content-type") + textBody);
						//Stream parsers should handle data parsing and response type checking..
						for (StreamParser sp: streamParsers) {
							sp.parseResponse(response);
						}
						return true;
          }
        }
      );
      lastPolled = System.currentTimeMillis();
      update();
    }

	public boolean poll() {
		// perform a poll() if it is time
		if (pollingUrl==null || pollingUrl.equals("")) {return false;}
		long currentTime = Utils.currentTime();
		Logger.info("time: "+currentTime+" last polled "+lastPolled+" period: "+pollingPeriod);
		if ( (lastPolled+(pollingPeriod*1000)) > currentTime) { return false; }
		//Logger.info("Poll() happening!");

		asynchPoll();

		this.lastPolled = currentTime;
		save();
		return true;
	}
	
	public Boolean checkKey(String token) {
		return key.equals(this.key);
	}

	public void setPeriod(Long period) {
		this.pollingPeriod = period;
	}

	public boolean parseAndPost(Request req) {
		boolean result = false;
		if (streamParsers != null) {
			for (StreamParser sp : streamParsers) {
				//Logger.info("handing request to stream parser");
				if (sp != null) {
					// Liam: not sure what to do here, breaking build...
					result |= sp.parseRequest(req);
				}
			}
		}
		return result;
	}

	public static Source get(Long id, String key) {
		Source source = find.byId(id);
		if (source != null && source.checkKey(key))
			return source;
		return null;
	}

	public static Source get(Long id, User user) {
		Source source = find.byId(id);
		if ( source != null && source.owner.equals(user) )
			return source;
		return null;
	}

	public static Source getByKey(String key) {
		Source source = find.where().eq("key",key).findUnique();
		return source;	
	}

	public static Source getByUserLabel(User user, String label) {
		Source source = find.where().eq("owner",user).eq("label",label).findUnique();
		return source;
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
			if(getByUserLabel(source.owner, source.label) != null) {
				source.label = source.label + new Random(new Date().getTime()).nextInt(10) + "_at_" + (new Date().toString());
			}
			source.save();
			source.updateKey();
			return source;
		}
		return null;
	}

	public static void delete(Long id) {
		Source source = find.ref(id);
		source.pollingPeriod = 0L;
		//remove references
		Stream.dattachSource(source);
		source.delete();
	}

}
