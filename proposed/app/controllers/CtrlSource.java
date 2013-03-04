package controllers;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import views.html.accountPage;
import models.*;
import play.data.Form;

@Security.Authenticated(Secured.class)
public class CtrlSource extends Controller {

	static private Form<SkeletonSource> skeletonSourceForm = Form.form(SkeletonSource.class);
	static private Form<Source> sourceForm = Form.form(Source.class);
	//static public Form<Source> sourceFormInitial = Form.form(Source.class);

	public static Result manage() {
  	User currentUser = Secured.getCurrentUser();
  	//Ugly work around to get Streams only
    return ok(views.html.managePage.render(currentUser.sourceList, sourceForm));
  }
	
	// poll the source data and fill the stream definition form
	// with default sensible parameters for the user to confirm
	@Security.Authenticated(Secured.class)
	public static Result initialise() {
		Form<Source> theForm = sourceForm.bindFromRequest();
		if(theForm.hasErrors()) {
			Logger.error("[CtrlSource.initialise] Form errors: " + theForm.errors().toString());
		  return badRequest("Bad request");
		} else {
			Source submitted = theForm.get();
			//Logger.info("[CtrlSource.initialise] Submitted polling URL: " + submitted.pollingUrl + " Period: " + submitted.pollingPeriod.toString());
			// get data
			//submitted.initialise():
			// parse initially, and guess values
			SkeletonSource skeleton = new SkeletonSource(submitted);
			skeletonSourceForm = skeletonSourceForm.fill(skeleton);
		  return ok(views.html.configureSource.render(skeletonSourceForm));
		}
	}
	
	// create the source and corresponding StreamParser objects
	@Security.Authenticated(Secured.class)
	public static Result add() {		
		Form<SkeletonSource> theForm = skeletonSourceForm.bindFromRequest();
		// validate form
		SkeletonSource skeleton = theForm.get();
		User currentUser = Secured.getCurrentUser();
		if(currentUser == null) {
			Logger.error("[CtrlSource.add] currentUser is null!");
		}
		Source submitted = Source.create(skeleton.getSource(currentUser));
		List<StreamParser> spList = skeleton.getStreamParsers(submitted);
		for (StreamParser sp : spList) {
			StreamParser.create(sp);
		}
        //TODO: validate the form... error handling
		//if(theForm.hasErrors()) {
		  //return badRequest("Bad request");
		//} else {
		  //Source submitted = theForm.get();
		  return redirect(routes.CtrlSource.manage());
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
		return TODO;
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
