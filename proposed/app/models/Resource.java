package models;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.*;
import com.avaje.ebean.Ebean;

import com.github.cleverage.elasticsearch.Indexable;
import com.github.cleverage.elasticsearch.Index;
import com.github.cleverage.elasticsearch.IndexQuery;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;

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

import controllers.*;
import index.*;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;

@Entity
@Table(name = "resources", uniqueConstraints = { 
	@UniqueConstraint(columnNames = {"owner_id", "label" }) 
	})
public class Resource extends Operator {

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
  public String label = "NewResource"+Utils.timeStr(Utils.currentTime());
  
	public Long pollingPeriod = 0L;
  
  public Long lastPolled = 0L;
  
  //if parent is not null, pollingUrl should be a subpath under parent
  //never use field access. Always use getter...
  private String pollingUrl = null;
	
  public String getPollingUrl() {
  	return pollingUrl;
	}

	public void setPollingUrl(String pollingUrl) {
		if(pollingUrl.endsWith("/")) {
			pollingUrl = pollingUrl.substring(0, pollingUrl.length()-1);
		}
		this.pollingUrl = pollingUrl;
	}
	
	public String pollingAuthenticationKey = null;
	public String description=""; 

	@ManyToOne
	public Resource parent = null;

	@OneToMany(mappedBy = "parent")//, cascade = CascadeType.ALL)
	public List<Resource> subResources = new ArrayList<Resource>();

	@OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
	public List<StreamParser> streamParsers = new ArrayList<StreamParser>();

	@OneToMany(mappedBy = "resource")
	public List<Stream> streams = new ArrayList<Stream>();

	/** Secret key for authenticating posts coming from outside */
	@Column(name="secret_key") //key is a reserved keyword in mysql
	public String key;

	@Version //for concurrency protection
	private int version;
	
	public static Model.Finder<Long, Resource> find = new Model.Finder<Long, Resource>(
			Long.class, Resource.class);
	
	public Resource(Resource parent, User owner, String label, Long pollingPeriod,
			 String pollingUrl, String pollingAuthenticationKey, String description) {
		super();
		this.parent = parent;
		this.label = label;
		this.owner = owner;
		this.pollingPeriod = pollingPeriod;
		this.lastPolled = 0L;
		this.pollingUrl = pollingUrl;
		this.pollingAuthenticationKey = pollingAuthenticationKey;
		this.description = description;
	}
	
	public Resource(User owner, String label, Long pollingPeriod,
			 String pollingUrl, String pollingAuthenticationKey) {
		this(null, owner, label, pollingPeriod, pollingUrl, pollingAuthenticationKey, "");
	}
	
	public Resource(String label, Long pollingPeriod,
			 String pollingUrl, String pollingAuthenticationKey) {
		this(null, null, label, pollingPeriod, pollingUrl, pollingAuthenticationKey, "");
	}

	public Resource(User user) {
		this(null, user, "NewResource"+Utils.timeStr(Utils.currentTime()), 0L, null, null, "");
	}

	public Resource() {
		this(null, null, "NewResource"+Utils.timeStr(Utils.currentTime()), 0L, null, null, "");
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
	
	public boolean canRead(User user) {
		return (owner.equals(user)); // || isShare(user) || publicAccess;
	}

	public String getUrl() {
		String basePath = "";
		if(parent != null && parent.hasUrl()) {
			if(parent.getUrl().endsWith("/")) {
				basePath = parent.getUrl().substring(0, parent.getUrl().length()-1);
			} else {
				basePath = parent.getUrl();
			}
		}
  	return basePath + getPollingUrl();
	}
	
	public boolean hasUrl() {
		return (Utils.isValidURL(getUrl()));
	}

	// construct a synchronous connnection to the URL to be probed in rela time
	public HttpURLConnection probe() {
		Logger.warn("probe(): "+ getUrl());
		HttpURLConnection connection = null;  
		//PrintWriter outWriter = null;  
		BufferedReader serverResponse = null;  
		//StringBuffer returnBuffer = new StringBuffer();  
		//String line;  
		//User currentUser = Secured.getCurrentUser();

		try { 
			connection = ( HttpURLConnection ) new URL( getUrl() ).openConnection();  
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
			Logger.error(mue.toString() + getUrl() + " Stack trace:\n" + mue.getStackTrace()[0].toString() );  
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
		final Resource thisResource = this;
		lastPolled = Utils.currentTime();
		String arguments = "";
		String path = "";
		Map<String, String> queryParameters = new HashMap<String, String>();

		if (getUrl().indexOf('?') != -1) {
			arguments = getUrl().substring(getUrl().indexOf('?') + 1,
					getUrl().length());
		}
		 Logger.info("[Stream] polling, URL: " + getUrl() +
		 " args: "+arguments);
		WSRequestHolder request = WS.url(getUrl());
		Pattern pattern = Pattern.compile("([^&?=]+)=([^?&]+)");
		Matcher matcher = pattern.matcher(arguments);
		while (matcher.find()) {
			request.setQueryParameter(matcher.group(1), matcher.group(2));
		}
		
		final WSRequestHolder thisRequest = request;
		
		request.get().map(new F.Function<WS.Response, Boolean>() {
			public Boolean apply(WS.Response response) {
				// Log request
//				String textBody = response.getBody();
//				Logger.info("Incoming data: " + response.getHeader("Content-type")
//						+ textBody);
				// Stream parsers should handle data parsing and response type
				// checking..
				Long currentTime = Utils.currentTime();
				
				boolean parsedSuccessfully = false; 
				String msgs = "";
				for (StreamParser sp : streamParsers) {
					try {
						parsedSuccessfully |= sp.parseResponse(response, currentTime);
					} catch (Exception e) {
						msgs += e.getMessage() + e.getStackTrace()[0].toString() + e.toString() + "\n";
						Logger.error("Exception: " + thisResource.label + ": asynchPoll(): " + msgs);
					}
				}
				 Logger.info("[asynchPoll] before resourceLog");
				ResourceLog resourceLog = new ResourceLog(thisResource, response,
						thisResource.lastPolled, currentTime);
				 Logger.info("[asynchPoll] after resourceLog");

				resourceLog = ResourceLog.createOrUpdate(resourceLog);
				 Logger.info("[asynchPoll] after resourceLog create");

				resourceLog.updateParsedSuccessfully(parsedSuccessfully);
				if(!msgs.equalsIgnoreCase("")) {
					resourceLog.updateMessages(msgs);
				}
				return true;
			}
		});
		update();
	}

	public boolean poll() {
		// perform a poll() if it is time
		if (getUrl()==null || getUrl().equals("")) {return false;}
		long currentTime = Utils.currentTime();
		//Logger.info("time: "+currentTime+" last polled "+lastPolled+" period: "+pollingPeriod);
		if ( (lastPolled+(pollingPeriod*1000)) > currentTime) { return false; }
		//Logger.info("Poll() happening!");

		asynchPoll();

		this.lastPolled = currentTime;
		update();
		return true;
	}
	
	public Boolean checkKey(String token) {
		return key.equals(this.key);
	}
	
	public String showKey(User user){
		if(this.owner.equals(user)){
			return this.key;
		}
		return null;
	}

	public void setPeriod(Long period) {
		this.pollingPeriod = period;
	}

	public boolean parseAndPost(Request req, Long currentTime) throws Exception {
		boolean result = false;
		if (streamParsers != null) {
			for (StreamParser sp : streamParsers) {
				//Logger.info("handing request to stream parser");
				if (sp != null) {
					//Logger.info("New request: " + req.body().asText());
					result |= sp.parseRequest(req, currentTime);
				}
			}
		}
		return result;
	}
	
	public void updateResource(Resource resource) {
		this.label = resource.label;
		//this.key = resource.getKey();
		this.pollingPeriod = resource.pollingPeriod;
		this.lastPolled = resource.lastPolled;
		this.pollingUrl = resource.getPollingUrl();
		this.parent = resource.parent;
		this.description = resource.description;
		this.pollingAuthenticationKey = resource.pollingAuthenticationKey;
		if(key == null || "".equalsIgnoreCase(key)) {
			updateKey();
		}

		// update indexes
		Resource.index(this);
		update();
	}
	
	@Override
	public void delete() {
		this.pollingPeriod = 0L;
		//remove references
		Stream.dattachResource(this);
		ResourceLog.deleteByResource(this);
//		Indexer thisIndexer = Indexer.find.byId(id.toString()); 
//		if(thisIndexer != null) {
//			thisIndexer.deleteAsync();
//			//TODO: check for success
//		}
		//delete sub resources and their sub resources, etc...
		List<Resource> subResList = Ebean.find(Resource.class)  
        .select("id, parent, pollingPeriod")
        .where().eq("parent_id", this.id)
        .findList();  
		for(Resource sub : subResList) {
			sub.delete();
		}
		super.delete();
	}

	public static Resource getById(Long id) {
		Resource resource = find.byId(id);
		return resource;
	}
	public static Resource get(Long id, String key) {
		Resource resource = find.byId(id);
		if (resource != null && resource.checkKey(key))
			return resource;
		return null;
	}

	public static Resource get(Long id, User user) {
		Resource resource = find.byId(id);
		if ( resource != null && resource.owner.equals(user) )
			return resource;
		return null;
	}

	public static Resource getByKey(String key) {
		Resource resource = find.where().eq("key",key).findUnique();
		return resource;	
	}

	public static Resource getByUserLabel(User user, String label) {
		Resource resource = find.where().eq("owner",user).eq("label",label).findUnique();
		return resource;
	}

	public static List<Resource> availableResources(User user) {
		// should add public resources...
		return user.resourceList;
	}

	public static Resource create(User user) {
			Resource resource = new Resource(user);
			// Liam: not sure if we need an index creation here?
			//Beshr: I  added it in the other create()
			return Resource.create(resource);
	}

	public static void index(Resource resource) {
			try {
				// add search indexing through Elastic Search
				Logger.warn("Trying to send indexed resource");
				Indexer indexer = new Indexer();
				indexer.id    = resource.id;
				indexer.label = resource.label;
				indexer.url   = resource.getUrl(); //Beshr: to get the full url
				if (!resource.description.equals("")) { indexer.description = resource.description; }
				indexer.index();
				// Not sure if this is actually required?
				//IndexService.refresh();
			} catch (java.lang.NullPointerException e) {
				Logger.info("ElasticSearch server not available");
			} catch (Exception e) {
				Logger.error("ElasticSearch index() error!");
			}
	}
	
	public static Resource create(Resource resource) {
		if (resource.owner != null) {
			if(getByUserLabel(resource.owner, resource.label) != null) {
				resource.label = resource.label + new Random(new Date().getTime()).nextInt(10) + "_at_" + (new Date().toString());
			}
			resource.save();
			resource.updateKey();
			//Resource.index(resource);
			return resource;
		}
		return null;
	}
	
	public static void delete(Long id) {
		Resource resource = find.ref(id);
		if(resource != null) resource.delete();

		// Liam: need to delete index for this resource
		// Beshr: Maybe in the resource.delete()?
	}

}
