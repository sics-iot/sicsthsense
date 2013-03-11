package controllers;

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
import views.html.*;

public class CtrlSource extends Controller {

	static private Form<SkeletonSource> skeletonSourceForm = Form.form(SkeletonSource.class);
	static private Form<Source> sourceForm = Form.form(Source.class);

	// poll the source data and fill the stream definition form
	// with default sensible parameters for the user to confirm
	@Security.Authenticated(Secured.class)
	public static Result initialise() {
		Form<Source> theForm = sourceForm.bindFromRequest();
		if(theForm.hasErrors()) {
		  return badRequest("Bad request");
		} else {
			User currentUser = Secured.getCurrentUser();
			Source submitted = theForm.get();
			StringBuffer returnBuffer = new StringBuffer();  
			BufferedReader serverResponse;
			
			if(submitted.pollingUrl != null && !"".equalsIgnoreCase(submitted.pollingUrl)) {
			// get data
				HttpURLConnection connection = submitted.probe();
				String contentType = connection.getContentType();
				Logger.warn(contentType);
				try {
					serverResponse = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );  
					String line;
					while ( (line=serverResponse.readLine())!=null ) {returnBuffer.append(line);}  
				} catch (IOException ioe) {  
					Logger.error(ioe.toString() + "\nStack trace:\n" + ioe.getStackTrace()[0].toString());
					return badRequest("Error collecting data from the source URL");
				}
				// decide to how to parse this data	
				if (contentType.matches("application/json.*") || contentType.matches("text/json.*")) {
					Logger.info("json file!");
					return parseJson(returnBuffer.toString(), submitted);
				} else if (contentType.matches("text/html.*")) {
					Logger.info("html file!");
				} else {
					Logger.warn("Unknown content type!");
				}	
			}
			SkeletonSource skeleton = new SkeletonSource(submitted);
			Form<SkeletonSource> skeletonSourceFormNew = skeletonSourceForm.fill(skeleton);

		  return ok(views.html.configureSource.render(currentUser.sourceList, skeletonSourceFormNew));
		}
	}
	
	@Security.Authenticated(Secured.class)
	public static void parseJsonNode(JsonNode node, SkeletonSource skeleton, String parents) {
		// descend to all nodes to find all primitive element paths...
		Iterator<String> nodeIt = node.getFieldNames();
		while (nodeIt.hasNext()) {
			String field = nodeIt.next();
			//Logger.info("field: "+field);
			JsonNode n = node.get(field);
			if (n.isValueNode()) {
				Logger.info("value node: "+parents+"/"+field);
				skeleton.addStreamParser("/"+skeleton.label+parents+"/"+field, parents+"/"+field, "application/json");
			} else {
				String fullNodeName = parents+"/"+field;
				Logger.info("Node: "+fullNodeName);
				parseJsonNode(n,skeleton,fullNodeName);
			}
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result parseJson(String data, Source submitted) {
			Logger.info("Trying to parse Json to then auto fill in StreamParsers!");
			User currentUser = Secured.getCurrentUser();
			SkeletonSource skeleton = new SkeletonSource(submitted);

			try {
				// recusively parse JSON and add() all fields
				JsonNode root = Json.parse(data);
				parseJsonNode(root,skeleton,"");
			} catch (Exception e) {
				// nevermind, move on...
			}
	
			Form<SkeletonSource> skeletonSourceFormNew = skeletonSourceForm.fill(skeleton);
		  return ok(views.html.configureSource.render(currentUser.sourceList, skeletonSourceFormNew));
	}

	// create the source and corresponding StreamParser objects
	@Security.Authenticated(Secured.class)
	public static Result add() {		
		Form<SkeletonSource> theForm = skeletonSourceForm.bindFromRequest();
		// validate form
		if (theForm.hasErrors()) {
		  return badRequest("Bad request");
		} else {
			SkeletonSource skeleton = theForm.get();
			User currentUser = Secured.getCurrentUser();
			if (currentUser == null) { Logger.error("[CtrlSource.add] currentUser is null!"); }

			//Logger.warn("Submit type: "+ skeletonSourceForm.get("poll") );

			if (false) { // if repoll() source
				return ok(views.html.configureSource.render(currentUser.sourceList, skeletonSourceForm));
			} else {
				Source submitted = Source.create(skeleton.getSource(currentUser));
				List<StreamParser> spList = skeleton.getStreamParsers(submitted);
				for (StreamParser sp : spList) {
					StreamParser.create(sp);
					Stream newstream = new Stream();
					newstream.create(currentUser);
				}
				return redirect(routes.CtrlSource.manage());
			}
		}
	}

	// create the source and corresponding StreamParser objects
	@Security.Authenticated(Secured.class)
	public static Result manage() {		
  	User currentUser = Secured.getCurrentUser();
    return ok(managePage.render(currentUser.sourceList, sourceForm));
	}
	//
//DynamicForm requestData = Form.form().bindFromRequest();
//Long pollingPeriod = Long.parseLong( requestData.get("pollingPeriod") );
//String pollingUrl = requestData.get("pollingUrl");
//String authentication = requestData.get("authentication");

//extracting stream parsers
//int n = 0;
//while() {
//String inputType = requestData.get("inputType["+ Integer.toString(n)+"]")
//Long pollingPeriod = Long.parseLong( requestData.get("pollingPeriod") );
//}
//Long pollingPeriod = Long.parseLong( requestData.get("pollingPeriod") );

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
		Form<SkeletonSource> theForm = skeletonSourceForm.bindFromRequest();
		// validate form
		if (theForm.hasErrors()) {
			return badRequest("Bad request: " + theForm.errors());
		} else {
			SkeletonSource skeleton = theForm.get();
			User currentUser = Secured.getCurrentUser();
			Source source = Source.get(id, currentUser);
			if (source == null) {
				return badRequest("Source does not exist: " + id);
			}
			Source submitted = skeleton.getSource(currentUser);
			List<StreamParser> spList = skeleton.getStreamParsers(submitted);
			try {
				source.updateSource(submitted);
				if (spList != null) {
					for (StreamParser sp : spList) {
						if (sp.id != null) {
							sp.update();
						}
					}
				} //else { Ebean.delete( source.streamParsers ); }
			} catch (Exception e) {
				Logger.error(e.getMessage() + " Stack trace:\n" + e.getStackTrace()[0].toString());
				return badRequest("Bad request");
			}
			return redirect(routes.CtrlSource.getById(id));
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result delete(Long id) {
		// check permission?
		Source.delete(id);
		return redirect(routes.CtrlSource.manage());
	}
	
	public static Result postByKey(String key) {
		Source source = Source.getByKey(key);
		return post(source.owner, source.id);
	}

	public static Result postByLabel(String user, String label) {
		User owner = User.getByUserName(user);
		Source source = Source.getByUserLabel(owner, label);
		return post(owner, source.id);
	}
		
	private static Result post(User user, Long id) {
		// rightnow only owner can post
		Source source = Source.get(id, user);
		return postBySource(source);
		// resolve device from device list
		// if public: good
		// if this currentUser.username is in ACL: good
		// else error message
	}

	public static Result postBySourceKey(Long id, String key) {
		Source source = Source.get(id, key);
		return postBySource(source);
	}
	
	@BodyParser.Of(BodyParser.TolerantText.class)
	private static Result postBySource(Source source) {
		if (source != null) {
			try {
				// XXX: asText() does not work unless ContentType is "text/plain"
				String strBody = request().body().asText();
				Logger.info("[Streams] post received from: " + " URI "
						+ request().uri() + ", content type: "
						+ request().getHeader("Content-Type") + ", payload: " + strBody);
				if (!source.parseAndPost(request())) {
					return badRequest("Bad request: Can't parse!");
				}
			} catch (Exception e) {
				Logger.error("[Streams] Exception " + e.getMessage() + e.getStackTrace()[0].toString());
//				Logger.info("[Streams] User null"
//						+ Boolean.toString(currentUser == null));
				return badRequest("Bad request: Error!");
			}
			return ok("ok");
		}
		return notFound();
	}

	public static Result getByLabel(String user, String label) {
		User owner = User.getByUserName(user);
		Source source = Source.getByUserLabel(owner, label);
		if (source== null) {
			Logger.warn("Source not found!");
			return notFound();
		}
			return ok("ok");
	}

	@Security.Authenticated(Secured.class)
	public static Result getById(Long id) {
		User currentUser = Secured.getCurrentUser();
		Source source = Source.get(id, currentUser);
		if (source == null) {
			return badRequest("Source does not exist: " + id);
		}
		SkeletonSource skeleton = new SkeletonSource(source);
		Form<SkeletonSource> myForm = skeletonSourceForm.fill(skeleton);
		return ok(sourcePage.render(currentUser.sourceList, myForm));
	}

	public static Result getData(String ownerName, String path, Long tail,
			Long last, Long since) {
		final User user = Secured.getCurrentUser();
		final User owner = User.getByUserName(ownerName);
		// if(user == null) return notFound();
		return getData(user, owner, path, tail, last, since);
	}

	public static Result getDataById(Long id, Long tail, Long last, Long since) {
		final User user = Secured.getCurrentUser();
		// if(user == null) return notFound();
		Stream stream = Stream.get(id);
		if (stream == null) {
			Logger.warn("Stream not found!");
			return notFound();
		}
		return getData(user, stream, tail, last, since);
	}

	public static Result getDataByUserKey(String user_token, String path,
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
