package controllers;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.avaje.ebean.Ebean;

import play.Logger;
import play.api.mvc.Request;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import play.data.Form;
import java.net.*;

import models.*;
import index.*;

import views.html.*;

public class CtrlResource extends Controller {

	static private Form<SkeletonResource> skeletonResourceForm = Form.form(SkeletonResource.class);
	static private Form<Resource> resourceForm = Form.form(Resource.class);
	//static private Form<ResourceLogView> logViewForm = Form.form(ResourceLogView.class);


	// poll the source data and fill the stream definition form
	// with default sensible parameters for the user to confirm
	@Security.Authenticated(Secured.class)
	public static Result initialise() {
		Form<Resource> theForm = resourceForm.bindFromRequest();
		if(theForm.hasErrors()) {
		  return badRequest("Bad request");
		} else {
			User currentUser = Secured.getCurrentUser();
			Resource submitted = theForm.get();
			StringBuffer returnBuffer = new StringBuffer();  
			BufferedReader serverResponse;
			
			if(submitted.getPollingUrl() != null && !"".equalsIgnoreCase(submitted.getPollingUrl())) {
			//fudge URL, should check HTTP
				if (!submitted.getPollingUrl().startsWith("http://") 
						&& !submitted.getPollingUrl().startsWith("https://") 
						&& !submitted.getPollingUrl().startsWith("coap://") && submitted.parent == null) {
					submitted.setPollingUrl("http://"+submitted.getPollingUrl());
				}
				// get data
				HttpURLConnection connection = submitted.probe();
				String contentType = connection.getContentType();
				Logger.warn("Probed and found contentType: "+contentType);
				try {
					serverResponse = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );  
					String line;
					while ( (line=serverResponse.readLine())!=null ) {returnBuffer.append(line);}  
				} catch (IOException ioe) {  
					Logger.error(ioe.toString() + "\nStack trace:\n" + ioe.getStackTrace()[0].toString());
					return badRequest("Error collecting data from the resource URL");
				}
				// decide to how to parse this data	
				if (contentType.matches("application/json.*") || contentType.matches("text/json.*")) {
					Logger.info("json file!");
					return parseJson(returnBuffer.toString(), submitted);
				} else if (contentType.matches("text/html.*") || contentType.matches("text/plain.*")) {
					Logger.info("html file!");
					return parseHTML(returnBuffer.toString(), submitted);
//				}  else if (contentType.matches("text/csv.*")) {
//					Logger.info("csv file!");
//					return parseCSV(returnBuffer.toString(), submitted);
				} else {
					Logger.warn("Unknown content type!");
				}	
			}
			SkeletonResource skeleton = new SkeletonResource(submitted);
			Form<SkeletonResource> skeletonResourceFormNew = skeletonResourceForm.fill(skeleton);
		  return ok(views.html.resourcePage.render(currentUser.resourceList, skeletonResourceFormNew, true));
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result autoParser(Long id) {
		Logger.warn("Auto configuring "+id);
		
		User currentUser = Secured.getCurrentUser();
		Resource resource = Resource.getById(id);
		if (resource==null) { return ok("Error getting resource"); }
		StringBuffer returnBuffer = new StringBuffer();  
		BufferedReader serverResponse;
		
		if(resource.getPollingUrl() != null && !"".equalsIgnoreCase(resource.getPollingUrl())) {
		//fudge URL, should check HTTP
			if (!resource.getPollingUrl().startsWith("http://") 
					&& !resource.getPollingUrl().startsWith("https://") 
					&& !resource.getPollingUrl().startsWith("coap://") && resource.parent == null) {
				resource.setPollingUrl("http://"+resource.getPollingUrl());
			}
			// get data
			HttpURLConnection connection = resource.probe();
			String contentType = connection.getContentType();
			Logger.warn("Probed and found contentType: "+contentType);
			try {
				serverResponse = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );  
				String line;
				while ( (line=serverResponse.readLine())!=null ) {returnBuffer.append(line);}  
			} catch (IOException ioe) {  
				Logger.error(ioe.toString() + "\nStack trace:\n" + ioe.getStackTrace()[0].toString());
				return badRequest("Error collecting data from the resource URL");
			}
			// decide to how to parse this data	
			if (contentType.matches("application/json.*") || contentType.matches("text/json.*")) {
				Logger.info("json file!");
				return parseJson(returnBuffer.toString(), resource);
			} else if (contentType.matches("text/html.*") || contentType.matches("text/plain.*")) {
				Logger.info("html file!");
				return parseHTML(returnBuffer.toString(), resource);
//				}  else if (contentType.matches("text/csv.*")) {
//					Logger.info("csv file!");
//					return parseCSV(returnBuffer.toString(), resource);
			} else {
				Logger.warn("Unknown content type!");
			}	
		}
		//return ok(routes.CtrlResource.resource(currentUser.resourceList,null,false));
		return ok("auto");
		//SkeletonResource skeleton = new SkeletonResource(submitted);
		//Form<SkeletonResource> skeletonResourceFormNew = skeletonResourceForm.fill(skeleton);
		//return ok(views.html.resourcePage.render(currentUser.resourceList, skeletonResourceFormNew, true));
	}
	
	@Security.Authenticated(Secured.class)
	public static void parseJsonNode(JsonNode node, SkeletonResource skeleton, String parents) {
		// descend to all nodes to find all primitive element paths...
		Iterator<String> nodeIt = node.getFieldNames();
		while (nodeIt.hasNext()) {
			String field = nodeIt.next();
			//Logger.info("field: "+field);
			JsonNode n = node.get(field);
			if (n.isValueNode()) {
				Logger.info("value node: "+parents+"/"+field);
				//TODO: try to guess time format instead of defaulting to "unix"!
				skeleton.addStreamParser("/"+skeleton.label+parents+"/"+field, parents+"/"+field, "application/json", "unix");
			} else {
				String fullNodeName = parents+"/"+field;
				Logger.info("Node: "+fullNodeName);
				parseJsonNode(n,skeleton,fullNodeName);
			}
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result parseJson(String data, Resource submitted) {
			Logger.info("Trying to parse Json to then auto fill in StreamParsers!");
			User currentUser = Secured.getCurrentUser();
			SkeletonResource skeleton = new SkeletonResource(submitted);

			try {
				// recusively parse JSON and add() all fields
				JsonNode root = Json.parse(data);
				parseJsonNode(root,skeleton,"");
			} catch (Exception e) {
				// nevermind, move on...
			}
	
			Form<SkeletonResource> skeletonResourceFormNew = skeletonResourceForm.fill(skeleton);
		  return ok(views.html.resourcePage.render(currentUser.resourceList, skeletonResourceFormNew, false));
	}

	@Security.Authenticated(Secured.class)
	public static Result parseHTML(String data, Resource submitted) {
			Logger.info("Adding single default Regex StreamPaser to HTML input");
			User currentUser = Secured.getCurrentUser();
			SkeletonResource skeleton = new SkeletonResource(submitted);
			//TODO: try to guess time format instead of defaulting to "yy-mm-dd kk:mm:ss"!
			skeleton.addStreamParser("/"+skeleton.label+"/"+"regex1","(.*)","text/html", "yy-mm-dd kk:mm:ss");
	
			Form<SkeletonResource> skeletonResourceFormNew = skeletonResourceForm.fill(skeleton);
		  return ok(views.html.resourcePage.render(currentUser.resourceList, skeletonResourceFormNew, true));
	}
	
//	private static Result parseCSV(String data, Resource submitted) {
//		Logger.info("Adding CSV StreamPasers");
//		User currentUser = Secured.getCurrentUser();
//		SkeletonResource skeleton = new SkeletonResource(submitted);
//
//		skeleton.addStreamParser("/"+skeleton.label+"/"+"regex1","Enter Regex","text/html");
//
//		Form<SkeletonResource> skeletonResourceFormNew = skeletonResourceForm.fill(skeleton);
//	  return ok(views.html.resourcePage.render(currentUser.sourceList, skeletonResourceFormNew, true));
//	}

	// create the source and corresponding StreamParser objects
	@Deprecated
	@Security.Authenticated(Secured.class)
	public static Result add() {		
		Form<SkeletonResource> theForm;
		// error check
		try {
			theForm = skeletonResourceForm.bindFromRequest();
		} catch (Exception e) {
		  return badRequest("Bad parsing of form");
		}
		// validate form
		if (theForm.hasErrors()) {
		  return badRequest("Bad request");
		} else {
			SkeletonResource skeleton = theForm.get();
			if(skeleton.streamParserWrapers !=null && skeleton.streamParserWrapers.get(0) !=null) {
			Logger.info("Adding parser: " 
					+"inputParser: "+ skeleton.streamParserWrapers.get(0).inputParser 
					+"vfilePath: " + skeleton.streamParserWrapers.get(0).vfilePath 
					+"inputType: " + skeleton.streamParserWrapers.get(0).inputType);
			}
			User currentUser = Secured.getCurrentUser();
			if (currentUser == null) { Logger.error("[CtrlResource.add] currentUser is null!"); }

			//Logger.warn("Submit type: "+ skeletonResourceForm.get("poll") );

			if (false) { // if repoll() source
				return ok(views.html.resourcePage.render(currentUser.resourceList, skeletonResourceForm, true));
			} else {
				Resource submitted = skeleton.getResource(currentUser);
				submitted.id = null;
				submitted = Resource.create(submitted);
				List<StreamParser> spList = skeleton.getStreamParsers(submitted);
				if (spList != null) {
					for (StreamParser sp : spList) {
						if(sp!=null){
							sp.id=null;
							StreamParser.create(sp);
						}
					}
				}
				currentUser.sortStreamList(); // reorder streams

				if(submitted != null && submitted.id != null) {
					return redirect(routes.CtrlResource.getById(submitted.id));
				}
			}
			return redirect(routes.CtrlResource.resources());
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result addSubResource() {
	DynamicForm requestData;
	
		try {
			requestData = Form.form().bindFromRequest();

		Long pollingPeriod = Long.parseLong( requestData.get("pollingPeriod") );
		Long parentId = Long.parseLong( requestData.get("parent") );
		User curentUser = Secured.getCurrentUser();
		Resource parent = Resource.get(parentId, curentUser);
		// validate form
//		this(parent, owner, label, pollingPeriod, pollingUrl, pollingAuthenticationKey, "");

			Resource submitted = new Resource(parent, curentUser, requestData.get("label"), pollingPeriod, requestData.get("pollingUrl"), requestData.get("pollingAuthenticationKey"), "Subresource" );
			submitted = Resource.create(submitted);
			Logger.info("Adding a new subresource: " + "Label: " + submitted.label
					+ " URL: " + submitted.getUrl());
			return CtrlResource.getById(parentId);
		} catch (Exception e) {
			return badRequest("Error: " + e.getMessage() + e.getStackTrace()[0].toString());
		}
		//return badRequest("Bad parsing of form");
	}
	
	@Security.Authenticated(Secured.class)
	public static Result addSimple() {
		Form<Resource> theForm;
		// error check
		try {
			theForm = resourceForm.bindFromRequest();
		} catch (Exception e) {
			return badRequest("Bad parsing of form");
		}
		// validate form
		if (theForm.hasErrors()) {
			return badRequest("Bad request");
		} else {
			Resource submitted = theForm.get();
			if (submitted != null) {
				User currentUser = Secured.getCurrentUser();
				if (currentUser == null) {
					Logger.error("[CtrlResource.add] currentUser is null!");
				}
				submitted.id = null;
				submitted.owner = currentUser;
				submitted.pollingPeriod = 0L;
				submitted = Resource.create(submitted);

				Logger.info("Adding a new resource: " + "Label: " + submitted.label
						+ " URL: " + submitted.getPollingUrl());
				// if(submitted != null && submitted.id != null) {
				// return redirect(routes.CtrlResource.getById(submitted.id));
				// }
			}
		}
		return redirect(routes.CtrlResource.resources());
	}
	
	// create the source and corresponding StreamParser objects
	@Security.Authenticated(Secured.class)
	public static Result resources() {		
  	User currentUser = Secured.getCurrentUser();
    return ok(resourcesPage.render(currentUser.resourceList, resourceForm));
	}

	@Security.Authenticated(Secured.class)
	public static Result post(Long id) {
		User currentUser = Secured.getCurrentUser();
		return post(currentUser, id);
	}

	@Security.Authenticated(Secured.class)      
  public static Result edit() {
    return TODO; //ok(accountPage.render(getUser(), userForm));
  }
	
	@Security.Authenticated(Secured.class)
	public static Result modify(Long id) {
		/*
		 * TODO: Create source from Form or update existing Create a parser from an
		 * embedded form and associate the parser with the new source
		 */
		Form<SkeletonResource> theForm = skeletonResourceForm.bindFromRequest();
		// validate form
		if (theForm.hasErrors()) {
			return badRequest("Bad request: " + theForm.errorsAsJson().toString());
		} else {
			SkeletonResource skeleton = theForm.get();
			User currentUser = Secured.getCurrentUser();
			Resource resource = Resource.get(id, currentUser);
			if (resource == null) {
				return badRequest("Resource does not exist: " + id);
			}
			Resource submitted = skeleton.getResource(currentUser);
			submitted.parent = resource.parent;
			Logger.info("Submitted resource url: " + submitted.getPollingUrl());
			List<StreamParser> spList = skeleton.getStreamParsers(submitted);
			try {
				resource.updateResource(submitted);
				if (spList != null) {
					for (StreamParser sp : spList) {
						if (sp.id != null) {
							sp.update();
						} else {
							StreamParser.create(sp);
						}
					}
				} //else { Ebean.delete( source.streamParsers ); }
			} catch (Exception e) {
				Logger.error(e.getMessage() + " Stack trace:\n" + e.getStackTrace()[0].toString());
				return badRequest("Bad request");
			}
			return redirect(routes.CtrlResource.getById(id));
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result delete(Long id) {
		User currentUser = Secured.getCurrentUser();
		// check permission?
		Resource.delete(id);
		return redirect(routes.CtrlResource.resources());
	}
	
	@Security.Authenticated(Secured.class)
	public static Result deleteParser(Long id) {
		StreamParser.delete(id);
		return ok("true");
	}
	
//	@Security.Authenticated(Secured.class)
//	public static Result addParser(Long sourceId, String inputParser, String inputType, String streamPath, String timeformat) {
//		Resource source = Resource.get(sourceId, Secured.getCurrentUser());
//		StreamParser parser = new StreamParser(source, inputParser, inputType, streamPath, timeformat);
//		parser = StreamParser.create(parser);
//		if(parser != null) {
//			return ok("true");
//		}
//		return ok("false");
//	}
	
	@Security.Authenticated(Secured.class)
	public static Result addParser(Long resourceId, String inputParser, String inputType, String streamPath, String timeformat, int dataGroup, int timeGroup, int numberOfPoints) {
		Resource resource = Resource.get(resourceId, Secured.getCurrentUser());
		StreamParser parser = new StreamParser(resource, inputParser, inputType, streamPath, timeformat, dataGroup, timeGroup, numberOfPoints);
		parser = StreamParser.create(parser);
		if(parser != null) {
			return ok("true");
		}
		return ok("false");
	}
	
	public static Result postByKey(String key) {
		Resource resource = Resource.getByKey(key);
		return post(resource.owner, resource.id);
	}

	@Security.Authenticated(Secured.class)
	private static Result postByLabel(String user, String label) {
		User owner = User.getByUserName(user);
		Resource resource = Resource.getByUserLabel(owner, label);
		//return post(owner, resource.id);
		return TODO;
	}
		
	private static Result post(User user, Long id) {
		// rightnow only owner can post
		Resource resource = Resource.get(id, user);
		return postByResource(resource);
		// resolve device from device list
		// if public: good
		// if this currentUser.username is in ACL: good
		// else error message
	}

	public static Result postByResourceKey(Long id, String key) {
		Resource resource = Resource.get(id, key);
		return postByResource(resource);
	}
	
	@BodyParser.Of(BodyParser.TolerantText.class)
	private static Result postByResource(Resource resource) {
		if (resource != null) {
			ResourceLog resourceLog = null;
			try {
				Long requestTime = Utils.currentTime();
				//Log request
				resourceLog = new ResourceLog(resource, request(), requestTime);
				resourceLog = ResourceLog.createOrUpdate( resourceLog );

				// XXX: asText() does not work unless ContentType is "text/plain"
				String strBody = request().body().asText();
				String jsonBodyString = (request().body().asJson()!=null) ? request().body().asJson().toString() : "" ;
				Logger.info("[Resources] post received from: " + " URI "
						+ request().uri() + ", content type: "
						+ request().getHeader("Content-Type") + ", payload: " + strBody + jsonBodyString);
				Boolean parsedSuccessfully = resource.parseAndPost(request(), requestTime);
				resourceLog.updateParsedSuccessfully( parsedSuccessfully );
				if (!parsedSuccessfully) {
					Logger.info("[Resources] Can't parse!");
					return badRequest("Bad request: Can't parse!");
				}
			} catch (Exception e) {
				String msg = "[CtrlResource] Exception while receiving a post in Resource: " + resource.label + "Owner " + resource.owner.userName + "\n" + e.getMessage() + e.getStackTrace()[0].toString();
				Logger.error(msg);
				if(resourceLog != null) {
					resourceLog.updateMessages(msg); 
				}
//				Logger.info("[Streams] User null"
//						+ Boolean.toString(currentUser == null));
				return badRequest("Bad request: Error!" + msg);
			}
			return ok("ok");
		}
		return notFound();
	}

	@Security.Authenticated(Secured.class)
	private static Result getByLabel(String user, String label) {
		User owner = User.getByUserName(user);
		Resource resource = Resource.getByUserLabel(owner, label);
		if (resource== null) {
			Logger.warn("Resource not found!");
			return notFound();
		}
			return TODO;
	}

	@Security.Authenticated(Secured.class)
	public static Result getById(Long id) {
		User currentUser = Secured.getCurrentUser();
		Resource resource = Resource.get(id, currentUser);
		if (resource == null) {
			return badRequest("Resource does not exist: " + id);
		}
		SkeletonResource skeleton = new SkeletonResource(resource);
		Form<SkeletonResource> myForm = skeletonResourceForm.fill(skeleton);
		return ok(resourcePage.render(currentUser.resourceList, myForm, false));
	}
	
	private static Result getData(String ownerName, String path, Long tail,
			Long last, Long since) {
		final User user = Secured.getCurrentUser();
		final User owner = User.getByUserName(ownerName);
		// if(user == null) return notFound();
		return getData(user, owner, path, tail, last, since);
	}

	private static Result getDataById(Long id, Long tail, Long last, Long since) {
		final User user = Secured.getCurrentUser();
		// if(user == null) return notFound();
		Stream stream = Stream.get(id);
		if (stream == null) {
			Logger.warn("Stream not found!");
			return notFound();
		}
		return getData(user, stream, tail, last, since);
	}

	private static Result getDataByUserKey(String user_token, String path,
			Long tail, Long last, Long since) {
		final User user = Secured.getCurrentUser();
		final User owner = User.getByToken(user_token);
		// if(user == null) return notFound();
		return getData(user, owner, path, tail, last, since);
	}

	// @Security.Authenticated(Secured.class)
	private static Result getData(User currentUser, User owner, String path,
			Long tail, Long last, Long since) {
		Vfile f = FileSystem.readFile(owner, path);
		if (f == null) { return notFound(); }

		Stream stream = f.getLink();
		if (stream == null) { return notFound(); }

		return getData(currentUser, stream, tail, last, since);

	}

	private static Result getData(User currentUser, Stream stream, Long tail,
			Long last, Long since) {
		if (stream == null) { return notFound(); }
		if (!stream.canRead(currentUser)) { return unauthorized("Private stream!"); }

		List<? extends DataPoint> dataSet = null;
		if (tail < 0 && last < 0 && since < 0) {
			tail = 1L;
		}
		if (tail >= 0) {
			dataSet = stream.getDataPointsTail(tail);
		} else if (last >= 0) {
			dataSet = stream.getDataPointsLast(last);
		} else if (since >= 0) {
			dataSet = stream.getDataPointsSince(since);
		} else {
			throw new RuntimeException("This cannot happen!");
		}

		ObjectNode result = Json.newObject();
		ArrayNode time = result.putArray("time");
		ArrayNode data = result.putArray("data");

		for (DataPoint dataPoint : dataSet) {
			time.add(dataPoint.timestamp);
			if (stream.getType() == Stream.StreamType.DOUBLE) {
				data.add((Double) dataPoint.getData());
			}
		}

		return ok(result);
	}
}
