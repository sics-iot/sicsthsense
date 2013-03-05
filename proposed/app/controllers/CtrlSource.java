package controllers;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import play.data.Form;
import java.net.*;

import models.*;
import views.html.*;

@Security.Authenticated(Secured.class)
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

			// get data
			HttpURLConnection connection = submitted.probe();
			String contentType = connection.getContentType();
			Logger.warn(contentType);
			try {
				serverResponse = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );  
				String line;
				while ( (line=serverResponse.readLine())!=null ) {returnBuffer.append(line);}  
			} catch (IOException ioe) {  
				Logger.error(ioe.toString() + "\nStack trace:\n" + ioe.getStackTrace().toString());
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

			SkeletonSource skeleton = new SkeletonSource(submitted);
			skeletonSourceForm = skeletonSourceForm.fill(skeleton);

		  return ok(views.html.configureSource.render(currentUser.sourceList,skeletonSourceForm,skeleton));
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result parseJson(String data, Source submitted) {
			User currentUser = Secured.getCurrentUser();
			Logger.info("Trying to parse Json to then auto fill in StreamParsers!");

			JsonNode root = Json.parse(data);

			Iterator<String> sit = root.getFieldNames();
			while (sit.hasNext()) {
				String str = sit.next();
				Logger.info("Field: "+str);
			}
			Iterator<JsonNode> nit = root.getElements();
			while (nit.hasNext()) {
				JsonNode n = nit.next();
				Logger.info("Node: "+n.getTextValue());
			}

			SkeletonSource skeleton = new SkeletonSource(submitted);
			skeletonSourceForm = skeletonSourceForm.fill(skeleton);
		  return ok(views.html.configureSource.render(currentUser.sourceList,skeletonSourceForm,skeleton));

	}

	// create the source and corresponding StreamParser objects
	@Security.Authenticated(Secured.class)
	public static Result add() {		
		Form<SkeletonSource> theForm = skeletonSourceForm.bindFromRequest();
		// validate form
		if(theForm.hasErrors()) {
		  return badRequest("Bad request");
		} else {
			SkeletonSource skeleton = theForm.get();
			User currentUser = Secured.getCurrentUser();
			if (currentUser == null) { Logger.error("[CtrlSource.add] currentUser is null!"); }

			//Logger.warn("Submit type: "+ skeletonSourceForm.get("poll") );

			if (false) { // if repoll() source
				return ok(views.html.configureSource.render(currentUser.sourceList,skeletonSourceForm,skeleton));
			} else {
				Source submitted = Source.create(skeleton.getSource(currentUser));
				List<StreamParser> spList = skeleton.getStreamParsers(submitted);
				for (StreamParser sp : spList) {
					StreamParser.create(sp);
				}
				return redirect(routes.CtrlSource.manage());
			}
		}
	}

	// create the source and corresponding StreamParser objects
	@Security.Authenticated(Secured.class)
	public static Result manage() {		
  	User currentUser = Secured.getCurrentUser();
    return ok(managePage.render(currentUser.sourceList,sourceForm));
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
		Form<Source> theForm = sourceForm.bindFromRequest();
		if (theForm.hasErrors()) {
			return badRequest("Bad request");
		} else {
			User currentUser = Secured.getCurrentUser();
			Source submitted = theForm.get();
			try {
				Source.get(id, currentUser).updateSource(submitted);
			} catch (Exception e) {
				return badRequest("Bad request");
			}
			return redirect(routes.CtrlSource.getById(submitted.id));
		}    
  }
	
	public static Result postByUserKey(Long id, String ownerToken) {
		User owner = User.getByToken(ownerToken);
		return post(owner, id);
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

	private static Result postBySource(Source source) {
		if (source != null) {
			try {
				String strBody = request().body().asText();
				Logger.info("[Streams] post received from: " + " URI "
						+ request().uri() + ", content type: "
						+ request().getHeader("Content-Type") + ", payload: " + strBody);
				if (!source.parseAndPost(request())) {
					return badRequest("Bad request: Can't parse!");
				}
			} catch (Exception e) {
				Logger.info("[Streams] Exception " + e.getMessage());
//				Logger.info("[Streams] User null"
//						+ Boolean.toString(currentUser == null));
				return badRequest("Bad request: Error!");
			}
			return ok("ok");
		}
		return notFound();
	}

	@Security.Authenticated(Secured.class)
	public static Result getById(Long id) {
		User currentUser = Secured.getCurrentUser();
		Source source = Source.get(id, currentUser);
		if (source == null) {
			return badRequest("Source does not exist: "+id);
		}
		return ok(sourcePage.render(currentUser.sourceList,source));
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
		if (f == null) {
			return notFound();
		}
		Stream stream = f.getLink();
		if (stream == null) {
			return notFound();
		}
		return getData(currentUser, stream, tail, last, since);

	}

	private static Result getData(User currentUser, Stream stream, Long tail,
			Long last, Long since) {
		if (stream == null) {
			return notFound();
		}

		if (!stream.canRead(currentUser))
			return unauthorized("Private stream!");

		List<DataPoint> dataSet = null;
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
