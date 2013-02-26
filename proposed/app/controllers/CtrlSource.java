package controllers;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.libs.Json;
import play.mvc.*;
import models.*;

public class CtrlSource extends Controller {

	@Security.Authenticated(Secured.class)
	public static Result post(Long id) {
		User currentUser = Secured.getCurrentUser();
		return post(currentUser, id);
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
