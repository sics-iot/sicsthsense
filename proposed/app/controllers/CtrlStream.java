package controllers;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.ParseException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.libs.Json;
import play.mvc.*;
import play.data.Form;
import play.data.DynamicForm;
import play.data.format.*;
import models.*;
import views.html.*;

public class CtrlStream extends Controller {

	static private Form<Stream> streamForm = Form.form(Stream.class);

	private static boolean canWrite(User user, Stream stream) {
		return (stream != null && user != null && stream.owner.equals(user));
	}

	@Security.Authenticated(Secured.class)
	public static Result getById(Long id) {
		User currentUser = Secured.getCurrentUser();
		Stream stream = Stream.get(id);
		if (stream== null) {
			return badRequest("Stream does not exist: " + id);
		}
		streamForm = streamForm.fill(stream);
		return ok(streamPage.render(currentUser.streamList, stream, streamForm));
	}

	@Security.Authenticated(Secured.class)
	public static Result modify(Long id) {
		
		Form<Stream> theForm = streamForm.bindFromRequest();

		if (theForm.hasErrors()) {
			return badRequest("Bad request: " + theForm.errorsAsJson().toString());
		} else {
			Stream submitted = theForm.get();
			User currentUser = Secured.getCurrentUser();
			Stream stream = Stream.get(id);
			if (stream  == null) {
				return badRequest("Stream does not exist: " + id);
			}

			// probably not the correct way to do it
			if (submitted.latitude != 0.0) {
				//String lon = streamForm.field("longtitude").value();
				//String lat = streamForm.field("latitude").value();
				//Logger.error("form latlon: "+lat+","+lon);
				//Location location = new Location(lon,lat);
				//submitted.location.setLatLon(lat,lon);
			} else {
				Logger.error("location not set");
			}
			stream.updateStream(submitted);
		}
		return redirect(routes.Application.viewStream(id));
	}

	@Security.Authenticated(Secured.class)
	public static Result delete(Long id) {
		final User currentUser = Secured.getCurrentUser();
		Stream stream = Stream.get(id);
		if (canWrite(currentUser, stream)) {
			stream.delete();
			currentUser.sortStreamList(); // reorder streams
			return ok();
		}
		return unauthorized();
	}

	public static Result deleteByKey(String key) {
		final Stream stream = Stream.getByKey(key);
		if (stream == null) {
			return notFound();
		} 
		stream.delete();
		return ok();
	}

	@Security.Authenticated(Secured.class)
	public static Result clear(Long id) {
		final User user = Secured.getCurrentUser();
		// if(user == null) return notFound();
		Stream stream = Stream.get(id);
		if (canWrite(user, stream)) {
			stream.clearStream();
			return ok();
		}
		return unauthorized();
	}
	
	public static Result clearByKey(String key) {
		final Stream stream = Stream.getByKey(key);
		if (stream == null) {
			return notFound();
		} 
		stream.clearStream();
		return ok();
	}

	@Security.Authenticated(Secured.class)
	public static Result setPublicAccess(Long id, Boolean pub) {
		final User user = Secured.getCurrentUser();
		Stream stream = Stream.get(id);
		if (canWrite(user, stream)) {
			return ok(Boolean.toString(stream.setPublicAccess(pub)));
		}
		return unauthorized();
	}

//	@Security.Authenticated(Secured.class)
	public static Result isPublicAccess(Long id) {
		Stream stream = Stream.get(id);
		if(stream == null) {
			return notFound();
		}
		return ok(Boolean.toString(stream.publicAccess));
	}

	@Security.Authenticated(Secured.class)
	public static Result setPublicSearch(Long id, Boolean pub) {
		final User user = Secured.getCurrentUser();
		Stream stream = Stream.get(id);
		if (canWrite(user, stream)) {
			return ok(Boolean.toString(stream.setPublicSearch(pub)));
		}
		return unauthorized();
	}
	
//@Security.Authenticated(Secured.class)
	public static Result isPublicSearch(Long id) {
		Stream stream = Stream.get(id);
		if(stream == null) {
			return notFound();
		}
		return ok(Boolean.toString(stream.publicSearch));
	}

	public static Result getByKey(String key, Long tail,
			Long last, Long since) {
		final Stream stream = Stream.getByKey(key);
		if (stream == null)
			return notFound();
		return getData(stream.owner, stream, tail, last, since);
	}

	public static Result postByKey(String key) {
		final Stream stream = Stream.getByKey(key);
		if (stream == null)
			return notFound();
		return post(stream.owner, stream);
	}

	@Security.Authenticated(Secured.class)
	public static Result post(Long id) {
		final User user = Secured.getCurrentUser();
		Stream stream = Stream.get(id);
		if(stream == null) {
			return notFound();
		}
		return post(user, stream);
	}

	private static Result post(User user, Stream stream) {
		if (canWrite(user, stream)) {
			String strBody = request().body().asText();
			long currentTime = Utils.currentTime();
			if (!stream.post(strBody, currentTime)) {
				return badRequest("Bad request: Error!");
			} else {
				return ok("ok");
			}
		}
		return unauthorized();
	}

	@Security.Authenticated(Secured.class)
	public static Result getData(String ownerName, String path, Long tail,
			Long last, Long since) {
		final User user = Secured.getCurrentUser();
		final User owner = User.getByUserName(ownerName);
		// if(user == null) return notFound();
		return getData(user, owner, path, tail, last, since);
	}

	@Security.Authenticated(Secured.class)
	public static Result getDataById(Long id, Long tail, Long last, Long since) {
		final User user = Secured.getCurrentUser();
		Stream stream = Stream.get(id);
		return getData(user, stream, tail, last, since);
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

	//tail: number of points, last: point during last {seconds}, since: Unix timestamp
	private static Result getData(User currentUser, Stream stream, Long tail,
			Long last, Long since) {
		if (stream == null) {
			return notFound();
		}

		if (!stream.canRead(currentUser))
			return unauthorized("Private stream!");

		List<? extends DataPoint> dataSet = null;
		if (tail < 0 && last < 0 && since < 0) {
			tail = 1L;
		}
		if (tail >= 0) {
			dataSet = stream.getDataPointsTail(tail);
		} else if (last >= 0) {
			dataSet = stream.getDataPointsLast(last*1000);
		} else if (since >= 0) {
			dataSet = stream.getDataPointsSince(since);
		} else {
			throw new RuntimeException("This cannot happen!");
		}

		ObjectNode result = Json.newObject();
		ArrayNode time = result.putArray("time");
		ArrayNode data = result.putArray("data");

		if (dataSet!= null) {
			for (DataPoint dataPoint : dataSet) {
				time.add(dataPoint.timestamp);
				if (stream.getType() == Stream.StreamType.DOUBLE) {
					data.add((Double) dataPoint.getData());
				}
			}
		}
		return ok(result);
	}
}
