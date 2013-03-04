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
public class CtrlFunction extends Controller {

	static private Form<Function> functionForm = Form.form(Function.class);

	@Security.Authenticated(Secured.class)
	public static Result attach() {		
		Form<Function> theForm = functionForm.bindFromRequest();
		if(theForm.hasErrors()) {
		  return badRequest("Bad request");
		} else {
			Function operator = theForm.get();
			User currentUser = Secured.getCurrentUser();
			Function.create(currentUser,theForm.field("output").value(),theForm.field("inputStream1").value(),theForm.field("inputStream2").value());
		  return redirect(routes.CtrlSource.manage());
		}
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
		Form<Function> theForm = functionForm.bindFromRequest();
		if (theForm.hasErrors()) {
			return badRequest("Bad request");
		} else {
			User currentUser = Secured.getCurrentUser();
			Function submitted = theForm.get();
			try {
				//Source.get(id, currentUser).updateSource(submitted);
			} catch (Exception e) {
				return badRequest("Bad request");
			}
		  return redirect(routes.CtrlSource.manage());
		}    
  }
	

	@Security.Authenticated(Secured.class)
	public static Result getById(Long id) {
		User currentUser = Secured.getCurrentUser();
		//Operator operator = Operator.get(id, currentUser);
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
