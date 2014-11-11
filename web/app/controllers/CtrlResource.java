/*
 * Copyright (c) 2013, Swedish Institute of Computer Science All rights reserved. Redistribution and
 * use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer. * Redistributions in binary form
 * must reproduce the above copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the distribution. * Neither the name of
 * The Swedish Institute of Computer Science nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE SWEDISH INSTITUTE OF
 * COMPUTER SCIENCE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

/*
 * Description: TODO:
 */

package controllers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.StringWriter;
import java.io.PrintWriter;

import models.DataPoint;
import models.FileSystem;
import models.Resource;
import models.ResourceLog;
import models.Stream;
import models.StreamParser;
import models.User;
import models.Vfile;
import models.Engine;
import controllers.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.avaje.ebean.Ebean;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import protocol.Response;
import views.html.resourcePage;
import views.html.resourcesPage;
import java.util.regex.PatternSyntaxException;


public class CtrlResource extends Controller {

	static public int pageSize = 10;
	static private Form<SkeletonResource> skeletonResourceForm = Form.form(SkeletonResource.class);
	static private Form<Resource> resourceForm = Form.form(Resource.class);

	// static private Form<ResourceLogView> logViewForm =
	// Form.form(ResourceLogView.class);

	@Security.Authenticated(Secured.class)
	public static Result addSimple() {
		Form<Resource> theForm;

		// error check
		try {
			theForm = resourceForm.bindFromRequest();
		} catch (Exception e) {
			Logger.error("addSimple() Form has errors");
			return badRequest("Bad parsing of form");
		}

		// validate form
		if (theForm.hasErrors()) {
			Logger.error("addSimple() Form has errors: "+theForm);
			return badRequest("Bad request: Form has errors:"+theForm);
		} else {
			Resource submitted = theForm.get();
			Logger.info("preurl"+submitted.getPollingUrl());

			if (submitted != null) {
				final User currentUser = Secured.getCurrentUser();

				if (currentUser == null) {
					Logger.error("[CtrlResource.add] currentUser is null!");
				}

				submitted.id = null;
				submitted.owner = currentUser;
				submitted.pollingPeriod = 0L;
				submitted = Resource.create(submitted);

				Logger.info("Adding a new resource: " + "Label: " + submitted.label
						+ " URL: " + submitted.getUrl());
				if(submitted != null && submitted.id != null) {
					return redirect(routes.CtrlResource.getById(submitted.id));
				}
			}
		}

		return redirect(routes.CtrlResource.resources(0));
	}

	// check the JSON describes a new Resource sufficiently
	// and instantite it to be stored
	public static boolean validateResourceJson(JsonNode root) {
		Logger.info("[CtrlResource] validating and creating");
		final User currentUser = Secured.getCurrentUser();
		if (currentUser==null) {return false;}

		HashMap map = Utils.jsonToMap(root);
		Resource resource = new Resource(currentUser);

		// ensure the resource has a label
		if (map.get("label")==null) {return false;}
		resource.label = (String)map.get("label");
		// add any optional attributes
		if (map.get("url")!=null) {
			resource.setPollingUrl((String)map.get("url"));
		} else {
			resource.setPollingUrl("");
		}
		if (map.get("period")!=null) {
			resource.pollingPeriod = Long.parseLong( (String)map.get("period") );
		}
		if (map.get("description")!=null) {resource.description=(String)map.get("description");}

		//Logger.info("[CtrlResource] save new resource");
		Resource.create(resource); // save the defined resource
		return true;
	}

	/*
	 * Parse a JSON object and create Resource
	 */
	@Security.Authenticated(Secured.class)
	public static Result createPost() {
		JsonNode root;
		String body = "";
		Logger.info("[CtrlResource] making Resource from JSON");
		try { // recusively parse JSON and add() all fields
			body = request().body().asText();
			root = request().body().asJson();
		} catch (Exception e) { // nevermind, move on...
			Logger.warn("[CtrlResource] had problems parsing JSON to make Resource:"+body);
			return badRequest("[CtrlResource] had problems parsing JSON to make Resource: "+body);
		}
		if (!validateResourceJson(root)) {
			Logger.error("JSON does not sufficiently describe Resource: "+body);
			return badRequest("JSON does not sufficiently describe Resource: "+body);
		}

		return ok("Made the resource!");
	}


	@Security.Authenticated(Secured.class)
	public static Result modify(Long id) {
		/*
		 * TODO: Create source from Form or update existing Create a parser from an
		 * embedded form and associate the parser with the new source
		 */
		final Form<SkeletonResource> theForm = skeletonResourceForm.bindFromRequest();

		if (theForm.hasErrors()) { // validate form
			return badRequest("Bad request: " + theForm.errorsAsJson().toString());
		} else {
			SkeletonResource skeleton = theForm.get();
			final User currentUser = Secured.getCurrentUser();
			final Resource resource = Resource.get(id, currentUser);

			if (resource == null) {
				return badRequest("Resource does not exist: " + id);
			}

			Resource submitted = skeleton.getResource(currentUser);
			submitted.parent = resource.parent;
			Logger.info("Submitted resource url: " + submitted.getUrl());
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
				} // else { Ebean.delete( source.streamParsers ); }
			} catch (Exception e) {
				Logger.error("CtrlResource: " + e.getMessage() + " Stack trace:\n" + e.getStackTrace()[0].toString());
				List<Stream> streams = new ArrayList<Stream>();
				return ok(views.html.resourcePage.render(currentUser.resourceList, theForm, streams, true, "Error: Problem compiling the definition of a parser! (likely Regex mistake)"));
			}
			Resource.rebuildEngineResource(resource.owner.getId(),id);
			return redirect(routes.CtrlResource.getById(id));

		}
	}

	@Security.Authenticated(Secured.class)
	public static Result autoParser(Long id) {
		Logger.warn("Auto configuring " + id);

		final User currentUser = Secured.getCurrentUser();
		final Resource resource = Resource.getById(id);

		if (resource == null) { return notFound("Error getting resource"); }

		if (resource.hasUrl()) {
			// fudge URL, should check HTTP
			// get data
			Response response = null;
			String contentType = null;
			try {
			Promise<Response> promise = resource.request("GET",
					new HashMap<String, String[]>(), new HashMap<String, String[]>(),
					null);
				response = promise.get();
				contentType = response.contentType();
			} catch (Exception e) { // Auto parser failed
				Logger.error("Auto add parser failed: "+e.toString());
				SkeletonResource skeleton = new SkeletonResource(resource);
				Form<SkeletonResource> myForm = skeletonResourceForm.fill(skeleton);
				List<Stream> streams = new ArrayList<Stream>();
				return ok(resourcePage.render(currentUser.resourceList, myForm, streams, false, "Error polling resource URL: "+resource.getPollingUrl()));
			}

			Logger.warn("Probed and found contentType: " + contentType);

			// decide to how to parse this data
			if (contentType.matches("application/json.*") || contentType.matches("text/json.*")) {
				Logger.info("json file!");
				return parseJson(response.body(), resource);
			} else if (contentType.matches("text/html.*") || contentType.matches("text/plain.*")) {
				Logger.info("html file!");
				return parseJson(response.body(), resource);
				// } else if (contentType.matches("text/csv.*")) {
				// Logger.info("csv file!");
				// return parseCSV(returnBuffer.toString(), resource);
			} else {
				Logger.warn("Unknown content type!");
			}
		}

		final SkeletonResource skeleton = new SkeletonResource(resource);
		final Form<SkeletonResource> skeletonResourceFormNew = skeletonResourceForm
				.fill(skeleton);

				List<Stream> streams = new ArrayList<Stream>();
		return ok(views.html.resourcePage.render(currentUser.resourceList,
				skeletonResourceFormNew, streams, false, "Parsers automatically added."));
	}

	@Security.Authenticated(Secured.class)
	public static void parseJsonNode(JsonNode node, SkeletonResource skeleton,
			String parents) {
		// descend to all nodes to find all primitive element paths...
		Iterator<String> nodeIt = node.fieldNames();
		while (nodeIt.hasNext()) {
			String field = nodeIt.next();
			// Logger.info("field: "+field);
			JsonNode n = node.get(field);
			if (n.isValueNode()) {
				Logger.info("value node: " + parents + "/" + field);
				// TODO: try to guess time format instead of defaulting to
				// "unix"!
				skeleton.addStreamParser("/" + skeleton.label + parents + "/" + field,
						parents + "/" + field, "application/json", "unix");
			} else {
				String fullNodeName = parents + "/" + field;
				Logger.info("Node: " + fullNodeName);
				parseJsonNode(n, skeleton, fullNodeName);
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
			parseJsonNode(root, skeleton, "");
		} catch (Exception e) {
			// nevermind, move on...
			Logger.warn("CtrlResource had problems parsing JSON...");
		}

		Form<SkeletonResource> skeletonResourceFormNew = skeletonResourceForm
				.fill(skeleton);
				List<Stream> streams = new ArrayList<Stream>();
		return ok(views.html.resourcePage.render(currentUser.resourceList,
				skeletonResourceFormNew, streams, true, "Parsers automatically filled in."));
	}

	@Security.Authenticated(Secured.class)
	public static Result parseHTML(String data, Resource submitted) {
		Logger.info("Adding single default Regex StreamPaser to HTML input");
		User currentUser = Secured.getCurrentUser();
		SkeletonResource skeleton = new SkeletonResource(submitted);
		// TODO: try to guess time format instead of defaulting to
		// "yy-mm-dd kk:mm:ss"!
		skeleton.addStreamParser("/" + skeleton.label + "/" + "regex1", "(.*)",
				"text/html", "yy-mm-dd kk:mm:ss");

		Form<SkeletonResource> skeletonResourceFormNew = skeletonResourceForm
				.fill(skeleton);
				List<Stream> streams = new ArrayList<Stream>();
		return ok(views.html.resourcePage.render(currentUser.resourceList,
				skeletonResourceFormNew, streams, true, "Regex parser assumed."));
	}

	// create the source and corresponding StreamParser objects
	@Deprecated
	@Security.Authenticated(Secured.class)
	public static Result add() {
		Form<SkeletonResource> theForm;
		try { // error check for the bind, bad encoding?
			theForm = skeletonResourceForm.bindFromRequest();
		} catch (Exception e) {
			return badRequest("Bad parsing of form");
		}
		if (theForm.hasErrors()) {
			return badRequest("Bad request");
		} else {
			SkeletonResource skeleton = theForm.get();
			if (skeleton.streamParserWrapers != null
					&& skeleton.streamParserWrapers.get(0) != null) {
				Logger.info("Adding parser: " + "inputParser: "
						+ skeleton.streamParserWrapers.get(0).inputParser + "vfilePath: "
						+ skeleton.streamParserWrapers.get(0).vfilePath + "inputType: "
						+ skeleton.streamParserWrapers.get(0).inputType);
			}
			User currentUser = Secured.getCurrentUser();
			if (currentUser == null) {
				Logger.error("[CtrlResource.add] currentUser is null!");
			}
			// Logger.warn("Submit type: "+ skeletonResourceForm.get("poll") );

			Resource submitted = skeleton.getResource(currentUser);
			submitted.id = null;
			submitted = Resource.create(submitted);
			List<StreamParser> spList = skeleton.getStreamParsers(submitted);
			if (spList != null) {
				for (StreamParser sp : spList) {
					if (sp != null) {
						sp.id = null;
						StreamParser.create(sp);
					}
				}
			}
			currentUser.sortStreamList(); // reorder streams

			if (submitted != null && submitted.id != null) { // go to
				// successfully
				// added Resource
				return redirect(routes.CtrlResource.getById(submitted.id));
			}
			return redirect(routes.CtrlResource.resources(0));
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result addSubResource() {
		DynamicForm requestData;

		try {
			requestData = Form.form().bindFromRequest();

			Long pollingPeriod = Long.parseLong(requestData.get("pollingPeriod"));
			Long parentId = Long.parseLong(requestData.get("parent"));
			User curentUser = Secured.getCurrentUser();
			Resource parent = Resource.get(parentId, curentUser);
			// validate form
			// this(parent, owner, label, pollingPeriod, pollingUrl,
			// pollingAuthenticationKey, "");

			Resource submitted = new Resource(parent, curentUser,
					requestData.get("label"), pollingPeriod,
					requestData.get("pollingUrl"),
					requestData.get("pollingAuthenticationKey"), "Subresource");
			submitted = Resource.create(submitted);
			Logger.info("Adding a new subresource: " + "Label: " + submitted.label
					+ " URL: " + submitted.getUrl());
			return CtrlResource.getById(parentId);
		} catch (Exception e) {
			return badRequest("Error: " + e.getMessage()
					+ e.getStackTrace()[0].toString());
		}
		// return badRequest("Bad parsing of form");
	}

	//only list root resources (parent =null)
	@Security.Authenticated(Secured.class)
	public static Result resources(Integer p) {
		User currentUser = Secured.getCurrentUser();
		List<Resource> rootResourcesList = Resource.find.select("id, owner, label, parent").where().eq("owner", currentUser)
				.eq("parent", null).orderBy("id asc").findPagingList(pageSize).getPage(p.intValue()).getList();
		return ok(resourcesPage.render(rootResourcesList, resourceForm, p, ""));
	}

	@Security.Authenticated(Secured.class)
	public static Result post(Long id) {
		User currentUser = Secured.getCurrentUser();
		return post(currentUser, id);
	}

	@Security.Authenticated(Secured.class)
	public static Result edit() {
		return TODO; // ok(accountPage.render(getUser(), userForm));
	}

	@Security.Authenticated(Secured.class)
	public static Result delete(Long id) {
		User currentUser = Secured.getCurrentUser();
		// check permission?
		//Resource resource = Resource.getById(resourceId);
		Resource.delete(id);
		//Resource.rebuildEngineResource(resource.owner.getId(),id);
		return redirect(routes.CtrlResource.resources(0));
	}

	@Security.Authenticated(Secured.class)
	public static Result deleteParser(Long id) {
		StreamParser.delete(id);
		return ok("true");
	}

	/*
	 * */
	//@Security.Authenticated(Secured.class)
	public static Result addParser(Long resourceId, String inputParser,
			String inputType, String streamPath, String timeformat, int dataGroup,
			int timeGroup, int numberOfPoints) {
		Resource resource = Resource.getById(resourceId);
		StreamParser parser = null;
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		//Logger.error("[CtrlResource]: StreamParser trying to make parser: "+streamPath);

		// check if stream path already exists
		if (FileSystem.fileExists(resource.owner,streamPath)) {
			Logger.error("[CtrlResource]: Stream path already exists!");
			streamPath = streamPath +" - " +Utils.dateFormatter(Utils.currentTime());
	 	}

		if (resource==null) {
			Logger.error("[CtrlResource]: Resource is null!!");
			return notFound("No resource found");
	 	}

		try {
			parser = new StreamParser(resource, inputParser, inputType,
				streamPath, timeformat, dataGroup, timeGroup, numberOfPoints);
		} catch (PatternSyntaxException e) {
			Logger.error("StreamParser not made due to Regex parsing error! "+e.toString());
			return badRequest("StreamParser not made due to Regex parsing error!");
		} catch (Exception e) {
			e.printStackTrace(pw);
			Logger.error("StreamParser not made due to error! "+sw.toString());
			return badRequest("StreamParser not made due to error!");
		}
		if (parser==null) {Logger.error("Parser is null!"); return badRequest("Problems making parser");}
		parser = StreamParser.create(parser);
		if (parser != null) {
			Logger.info("[CtrlResource]: StreamParser created!");
			return ok("[CtrlResource]: StreamParser created!");
		}
		return badRequest("StreamParser not made due to undefined error!");
	}

	public static Result postByKey(String key) {
		if (key==null || "".equals(key)) {return notFound();}
		Resource resource = Resource.getByKey(key);
		if (resource==null) {
			Logger.error("Resource with key "+key+" does not exist!");
			return notFound();
		}
		return post(resource.owner, resource.id);
	}

	@Security.Authenticated(Secured.class)
	private static Result postByLabel(String user, String labelPath) {
		User owner = User.getByUserName(user);
		Resource parent = null;
		labelPath = Utils.decodePath(labelPath);
		Resource resource = Resource.getByUserLabel(owner, null, labelPath);
		// return post(owner, resource.id);
		return TODO;
	}

	private static Result post(User user, Long id) {
		// rightnow only owner can post
		Resource resource = Resource.get(id, user);
		// resolve device from device list
		// if public: good
		// if this currentUser.username is in ACL: good
		// else error message
		return postByResource(resource);
	}

	public static Result postByResourceKey(Long id, String key) {
		Resource resource = Resource.get(id, key);
		return postByResource(resource);
	}

	@BodyParser.Of(BodyParser.TolerantText.class)
	private static Result postByResource(Resource resource) {
		if (resource == null) { return notFound(); }
		ResourceLog resourceLog = null;
		Long requestTime = Utils.currentTime();
		boolean parsedSuccessfully = false;
		String requestBody = getRequestBody();
		try {
			resourceLog = new ResourceLog(resource, request(), requestTime);
			resourceLog = ResourceLog.createOrUpdate(resourceLog);

			Logger.info("[Resources] post received from URI: " + request().uri()
					+ ", content type: " + request().getHeader("Content-Type")
					+ ", payload: " + requestBody);
			// if first POST (and no poll's), auto make parsers
      if (resource.streamParsers.isEmpty() && resource.isUnused()) {
        //Logger.info("Automatically making parsers on empty unused Resource.");
        autoCreateParsers(resource,requestBody);
				resource.update();
      }
			// force recreation of resource to include streamparsers
			resource = Resource.getById(resource.id);
			resource.update();

			parsedSuccessfully = resource.parseAndPost(request(), requestTime);
			resourceLog.updateParsedSuccessfully(parsedSuccessfully);
		} catch (Exception e) {
			String msg = "[CtrlResource] Exception while receiving a post in Resource: "+resource.label
					+ " Owner " + resource.owner.username + "\n"
					+ e.getMessage() + e.getStackTrace()[0].toString();
			Logger.error(msg);
			if (resourceLog != null) { resourceLog.updateMessages(msg); }
			return badRequest("Bad request: Error! " + msg);
		}
		if (!parsedSuccessfully) {
			Logger.info("[CtrlResource] Bad request: Not parsed successfully! "+requestBody);
			return badRequest("Bad request: not parsed successfully! "+requestBody);
		}
		return ok("ok");
	}

	public static String getRequestBody() {
    String body = "";
    if (request().getHeader("Content-Type").equals("text/plain")) {
      // XXX: asText() does not work unless ContentType is // "text/plain"
      body = request().body().asText();
    } else if (request().getHeader("Content-Type").equals("application/json") || request().getHeader("Content-Type").equals("text/json")) {
      body = (request().body().asJson() != null) ? request().body().asJson().toString() : "";
    } else {
      Logger.error("[CtrlResource] request() did not have a recognised Content-Type");
      body = "";
    }
    Logger.info("[Resources] post received from URI: " + request().uri()
      + ", content type: " + request().getHeader("Content-Type")
      + ", payload: " + body);
    return body;
  }

  // Walk Json tree creating resource parsers
  //@Security.Authenticated(Secured.class)
  public static void parseJsonNode(Resource resource, JsonNode node, String parents) {
    // descend to all nodes to find all primitive element paths...
    Iterator<String> nodeIt = node.fieldNames();
    while (nodeIt.hasNext()) {
      String field = nodeIt.next();
      //Logger.info("field: "+field);
      JsonNode n = node.get(field);
      if (n.isValueNode()) {
        //Logger.info("value node: " + parents + "/" + field);
        // TODO: try to guess time format instead of defaulting to "unix"!
        String nodePath = parents+"/"+field;
        //Logger.info("addParser() "+resource.id+" "+nodePath+" "+"application/json"+" "+ resource.label+nodePath);
				//Logger.info("Parser count: "+resource.streamParsers.size());
        addParser(resource.id, nodePath, "application/json", "/"+resource.label+nodePath, "unix", 1, 2, 1);
      } else {
        String fullNodeName = parents + "/" + field;
        //Logger.info("Node: " + fullNodeName);
        parseJsonNode(resource, n, fullNodeName);
      }
    }
  }

  // Parse Json into resource parsers
  //@Security.Authenticated(Secured.class)
  public static boolean createJsonParsers(Resource resource, String data) {
    Logger.info("[CtrlResource] createJsonParsers() Trying to parse Json to then auto fill in StreamParsers!");
    try {
      // recusively parse JSON and add() all fields
      JsonNode root = Json.parse(data);
      parseJsonNode(resource, root, "");
    } catch (Exception e) { // nevermind, move on...
      Logger.error("[CtrlResource] createJsonParsers() had problems parsing JSON: "+  data);
      return false;
    }
    Logger.error("[CtrlResource] about to update resource");
    resource.update();
    return true;
  }

  // create parsers in the resource with the json body of a post/poll
  //@Security.Authenticated(Secured.class)
  private static boolean autoCreateParsers(Resource resource, String jsonBody) {
    if (!resource.streamParsers.isEmpty() || !resource.isUnused()) { return false; }
    createJsonParsers(resource,jsonBody);
    return true;
  }

	@Security.Authenticated(Secured.class)
	private static Result getByLabel(String user, String labelPath) {
		User owner = User.getByUserName(user);
		labelPath = Utils.decodePath(labelPath);
		Resource parent = null;
		Resource resource = Resource.getByUserLabel(owner, null, labelPath);
		if (resource == null) {
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
			return badRequest("Specified resource does not exist: " + id);
		}
		List<Stream> streams = Ebean.find(Stream.class).where().eq("resource_id",resource.id).findList();
		SkeletonResource skeleton = new SkeletonResource(resource);
		Form<SkeletonResource> myForm = skeletonResourceForm.fill(skeleton);
		return ok(resourcePage.render(currentUser.resourceList, myForm, streams, false, ""));
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
		if (user == null)
			return notFound();
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
		if (!stream.canRead(currentUser)) {
			return unauthorized("Private stream!");
		}

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

	private static StringBuffer exploreResourceTree(User user, StringBuffer sb, Resource parentResource) {
		String parentResourceId = (parentResource == null) ? "null" : parentResource.id.toString();
		List<Resource> subResources = Resource.find.select("id, owner, label, parent").where().eq("owner", user).eq("parent", parentResource).orderBy("id asc").findList();
		if (subResources != null && subResources.size()>0) {
			sb.append("\n<ul data-parentResourceId='" + parentResourceId + "' >");
			for (Resource sr : subResources) {
				sb.append("<li><span class='resourceListItem' data-resourceId='" + sr.id.toString() + "'> "
						+ sr.label + "</span>"); // give node name
				sb = exploreResourceTree(user, sb, sr);
				sb.append("</li>");
			}
			sb.append("\n</ul>");
		}
		return sb;
	}

	// list all resources, for the navigation menu
	@Security.Authenticated(Secured.class)
	public static String listResources() {
		User user = Secured.getCurrentUser();
		StringBuffer sb = new StringBuffer();
		sb = exploreResourceTree(user, sb, null);
		return sb.toString();
	}
	@Security.Authenticated(Secured.class)
	public static int listResourcesLength() {
		User user = Secured.getCurrentUser();
		return user.resourceList.size();
	}

	@Security.Authenticated(Secured.class)
	public static Result regenerateKey(Long id) {
		User currentUser = Secured.getCurrentUser();
		Resource resource = Resource.get(id, currentUser);
		if (resource == null) {
			return badRequest("Resource does not exist: " + id);
		}
		String key = resource.updateKey();
		//return ok("Resource key reset successfully: " + id + " New key: " + key);
		return ok(key);
	}


	/*
	 * poll the source data and fill the stream definition form // with default
	 * sensible parameters for the user to confirm
	 *
	 * @Security.Authenticated(Secured.class) public static Result initialise() {
	 * Form<Resource> theForm = resourceForm.bindFromRequest();
	 * if(theForm.hasErrors()) { return badRequest("Bad request"); } else { User
	 * currentUser = Secured.getCurrentUser(); Resource submitted = theForm.get();
	 * StringBuffer returnBuffer = new StringBuffer(); BufferedReader
	 * serverResponse;
	 *
	 * if(submitted.getPollingUrl() != null &&
	 * !"".equalsIgnoreCase(submitted.getPollingUrl())) { //fudge URL, should
	 * check HTTP if (!submitted.getPollingUrl().startsWith("http://") &&
	 * !submitted.getPollingUrl().startsWith("https://") &&
	 * !submitted.getPollingUrl().startsWith("coap://") && submitted.parent ==
	 * null) { submitted.setPollingUrl("http://"+submitted.getPollingUrl()); } //
	 * get data HttpURLConnection connection = submitted.probe(); String
	 * contentType = connection.getContentType();
	 * Logger.warn("Probed and found contentType: "+contentType); try {
	 * serverResponse = new BufferedReader( new InputStreamReader(
	 * connection.getInputStream() ) ); String line; while (
	 * (line=serverResponse.readLine())!=null ) {returnBuffer.append(line);} }
	 * catch (IOException ioe) { Logger.error(ioe.toString() + "\nStack trace:\n"
	 * + ioe.getStackTrace()[0].toString()); return
	 * badRequest("Error collecting data from the resource URL."); //return
	 * ok(views.html.resourcePage.render(currentUser.resourceList,
	 * skeletonResourceFormNew, true,
	 * "Error: Problem collecting data from the resource URL.")); } // decide to
	 * how to parse this data if (contentType.matches("application/json.*") ||
	 * contentType.matches("text/json.*")) { Logger.info("json file!"); return
	 * parseJson(returnBuffer.toString(), submitted); } else if
	 * (contentType.matches("text/html.*") || contentType.matches("text/plain.*"))
	 * { Logger.info("html file!"); return parseHTML(returnBuffer.toString(),
	 * submitted); // } else if (contentType.matches("text/csv.*")) { //
	 * Logger.info("csv file!"); // return parseCSV(returnBuffer.toString(),
	 * submitted); } else { Logger.warn("Unknown content type!"); } }
	 * SkeletonResource skeleton = new SkeletonResource(submitted);
	 * Form<SkeletonResource> skeletonResourceFormNew =
	 * skeletonResourceForm.fill(skeleton); return
	 * ok(views.html.resourcePage.render(currentUser.resourceList,
	 * skeletonResourceFormNew, true, "Resource initialised")); } }
	 */

}
