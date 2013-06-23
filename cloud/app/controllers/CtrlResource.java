/*
 * Copyright (c) 2013, Swedish Institute of Computer Science
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of The Swedish Institute of Computer Science nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE SWEDISH INSTITUTE OF COMPUTER SCIENCE BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * Description: TODO:
 */

package controllers;

import logic.FileSystem;
import logic.ResourceHub;
import models.Representation;
import models.Resource;
import models.StreamParser;
import models.User;
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
import protocol.http.HttpProtocol;
import views.html.resourcePage;
import views.html.resourcesPage;

import java.util.List;
import java.util.regex.PatternSyntaxException;

public class CtrlResource extends Controller {
    private final static Logger.ALogger logger = Logger.of(CtrlResource.class);

    static private Form<SkeletonResource> skeletonResourceForm = Form.form(SkeletonResource.class);
    static private Form<Resource> resourceForm = Form.form(Resource.class);

    // static private Form<ResourceLogView> logViewForm =
    // Form.form(ResourceLogView.class);

    @Security.Authenticated(Secured.class)
    public static Result addForm() {
        final Form<Resource> theForm = resourceForm.bindFromRequest();

        // validate form
        if (theForm.hasErrors()) {
            return badRequest(theForm.errorsAsJson());
        }

        final User currentUser = Secured.getCurrentUser();

        if (currentUser == null) {
            logger.error("Cannot create resource because there is no user logged in");
            return unauthorized();
        }

        Resource submitted = theForm.get();

        logger.info("Creating new resource " + submitted.label + " for url " + submitted.getPollingUrl());

        submitted.id = null;
        submitted.owner = currentUser;

        logic.Result<Resource> result = ResourceHub.createResource(submitted);

        switch (result.code()) {
            case Ok:
                return redirect(routes.CtrlResource.resources());
            case InternalError:
            default:
                return internalServerError();
        }
    }

    /*
     * Parse a JSON object and create Resource
     */
    @Security.Authenticated(Secured.class)
    @BodyParser.Of(BodyParser.Json.class)
    public static Result addJson() {
        final User currentUser = Secured.getCurrentUser();

        if (currentUser == null) {
            logger.error("Cannot create resource because there is no user logged in");
            return unauthorized();
        }

        Resource submitted = Json.fromJson(request().body().asJson(), Resource.class);

        logger.info("Creating new resource " + submitted.label + " for url " + submitted.getPollingUrl());

        submitted.id = null;
        submitted.owner = currentUser;

        logic.Result<Resource> result = ResourceHub.createResource(submitted);

        switch (result.code()) {
            case Ok:
                return redirect(routes.CtrlResource.resources());
            case InternalError:
            default:
                return internalServerError();
        }
    }


    @Security.Authenticated(Secured.class)
    public static Result modify(Long id) {
        /*
         * TODO: Create source from Form or update existing Create a parser from an embedded form
         * and associate the parser with the new source
         */
        final User currentUser = Secured.getCurrentUser();
        final Form<SkeletonResource> theForm = skeletonResourceForm.bindFromRequest();

        if (!Resource.hasAccess(id, currentUser)) {
            return unauthorized();
        }

        if (theForm.hasErrors()) { // validate form
            return badRequest("Bad request: " + theForm.errorsAsJson().toString());
        }

        final SkeletonResource skeleton = theForm.get();
        final Resource changes = skeleton.getResource();

        logic.Result<Resource> result = ResourceHub.updateResource(id, changes);

        switch (result.code()) {
            case Ok:
                return redirect(routes.CtrlResource.getById(id));
            case NotFound:
                return badRequest("Resource does not exist: " + id);
            case InternalError:
            default:
                return internalServerError();
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result autoParser(Long id) {
        Logger.warn("Auto configuring " + id);

        final User currentUser = Secured.getCurrentUser();
        final Resource resource = Resource.getById(id);

        if (currentUser == null) {
            return unauthorized();
        }

        if (resource == null) {
            return notFound("Resource does not exist: " + id);
        }

        final SkeletonResource skeleton = new SkeletonResource(resource);

        if (!resource.hasUrl()) {
            Form<SkeletonResource> myForm = skeletonResourceForm.fill(skeleton);
            return ok(resourcePage.render(currentUser.resourceList, myForm, false, "The resource has no polling url defined"));
        }

        // fudge URL, should check HTTP
        // get data
        Response response;
        String contentType;

        try {
            Promise<Response> promise = resource.request();
            response = promise.get();
            contentType = response.contentType();
        } catch (Exception e) { // Auto parser failed
            logger.error("Auto add parser failed", e);

            Form<SkeletonResource> myForm = skeletonResourceForm.fill(skeleton);
            return ok(resourcePage.render(currentUser.resourceList, myForm, false,
                    "Error polling resource URL: " + resource.getPollingUrl()));
        }

        logger.info("Probed and found contentType: " + contentType);

        // decide to how to parse this data
        if (contentType.matches("application/json.*") || contentType.matches("text/json.*")) {
            for (StreamParser sp : ResourceHub.parsersFromJson(response.body())) {
                skeleton.streamParserWrappers.add(new StreamParserWrapper(sp));
            }
        } else if (contentType.matches("text/html.*") || contentType.matches("text/plain.*")) {
            for (StreamParser sp : ResourceHub.parsersFromPlain(response.body())) {
                skeleton.streamParserWrappers.add(new StreamParserWrapper(sp));
            }
        } else {
            Logger.warn("Unknown content type!");
        }

        final Form<SkeletonResource> skeletonResourceFormNew = skeletonResourceForm.fill(skeleton);

        return ok(views.html.resourcePage.render(currentUser.resourceList, skeletonResourceFormNew,
                false, "Parsers automatically added."));
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

            Resource submitted =
                    new Resource(parent, curentUser, requestData.get("label"), pollingPeriod,
                            requestData.get("pollingUrl"),
                            requestData.get("pollingAuthenticationKey"), "Subresource");
            submitted = Resource.create(submitted);
            Logger.info("Adding a new subresource: " + "Label: " + submitted.label + " URL: "
                    + submitted.getUrl());
            return CtrlResource.getById(parentId);
        } catch (Exception e) {
            return badRequest("Error: " + e.getMessage() + e.getStackTrace()[0].toString());
        }
        // return badRequest("Bad parsing of form");
    }

    // only list root resources (parent =null)
    @Security.Authenticated(Secured.class)
    public static Result resources() {
        User currentUser = Secured.getCurrentUser();
        List<Resource> rootResourcesList =
                Resource.find.select("id, owner, label, parent").where().eq("owner", currentUser)
                        .eq("parent", null).orderBy("label asc").findList();
        return ok(resourcesPage.render(rootResourcesList, resourceForm, ""));
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(Long id) {
        User currentUser = Secured.getCurrentUser();

        if (Resource.hasAccess(id, currentUser)) {
            return notFound();
        }

        Resource.delete(id);

        return redirect(routes.CtrlResource.resources());
    }

    /*
     * */
    // @Security.Authenticated(Secured.class)
    public static Result addParser(Long resourceId, String inputParser, String inputType,
                                   String streamPath, String timeformat, int dataGroup, int timeGroup, int numberOfPoints) {
        Resource resource = Resource.getById(resourceId);

        if (resource == null) {
            return notFound("Resource with id " + resourceId + " does not exist");
        }

        // check if stream path already exists
        if (FileSystem.fileExists(resource.owner, streamPath)) {
            logger.error("Stream path already exists!");
            streamPath = streamPath + " - " + Utils.dateFormatter(Utils.currentTime());
        }

        StreamParser parser;

        try {
            parser = new StreamParser(resource, inputParser, inputType, streamPath, timeformat, dataGroup, timeGroup, numberOfPoints);
        } catch (PatternSyntaxException e) {
            logger.error("StreamParser not made due to Regex parsing error! ", e);
            return badRequest("StreamParser not made due to Regex parsing error!");
        } catch (Exception e) {
            logger.error("StreamParser not made due to error!", e);
            return badRequest("StreamParser not made due to error!");
        }

        parser = StreamParser.create(parser);

        if (parser != null) {
            Logger.info("StreamParser created!");
            return ok("StreamParser created!");
        }

        return badRequest("StreamParser not made due to undefined error!");
    }

    @Security.Authenticated(Secured.class)
    public static Result deleteParser(Long id) {
        StreamParser sp = StreamParser.find.byId(id);

        if (sp == null || !Resource.hasAccess(sp.resource.id, Secured.getCurrentUser())) {
            return notFound("A streamparser for id " + id + " does not exist");
        }

        StreamParser.delete(id);

        return ok();
    }

    @Security.Authenticated(Secured.class)
    public static Result postById(Long id) {
        if (!Resource.hasAccess(id, Secured.getCurrentUser())) {
            return unauthorized();
        }

        Resource resource = Resource.getById(id);

        if (resource == null) {
            return notFound("Resource with id " + id + " does not exist!");
        }

        return postByResource(resource);
    }

    public static Result postByKey(String key) {
        Resource resource = Resource.getByKey(key);

        if (resource == null) {
            return notFound("Resource with key " + key + " does not exist!");
        }

        // No security check because the device is not logged in and only knows the key.

        return postByResource(resource);
    }

    private static Result postByResource(Resource resource) {
        if (resource == null) {
            return notFound();
        }

        logic.Result<Representation> result =
                ResourceHub.post(resource, HttpProtocol.translateRequest(request()));

        switch (result.code()) {
            case Ok:
                return ok();
            case NotFound:
                return notFound();
            case InternalError:
            default:
                return internalServerError();
        }
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
        return ok(resourcePage.render(currentUser.resourceList, myForm, false, ""));
    }

    private static StringBuffer exploreResourceTree(User user, StringBuffer sb,
                                                    Resource parentResource) {
        String parentResourceId = (parentResource == null) ? "null" : parentResource.id.toString();
        List<Resource> subResources =
                Resource.find.select("id, owner, label, parent").where().eq("owner", user)
                        .eq("parent", parentResource).orderBy("label asc").findList();
        if (subResources != null && subResources.size() > 0) {
            sb.append("\n<ul data-parentResourceId='" + parentResourceId + "' >");
            for (Resource sr : subResources) {
                sb.append("<li><span class='resourceListItem' data-resourceId='" + sr.id.toString()
                        + "'> " + sr.label + "</span>"); // give node name
                sb = exploreResourceTree(user, sb, sr);
                sb.append("</li>");
            }
            sb.append("\n</ul>");
        }
        return sb;
    }

    @Security.Authenticated(Secured.class)
    public static String listResources() {
        User user = Secured.getCurrentUser();
        StringBuffer sb = new StringBuffer();
        sb = exploreResourceTree(user, sb, null);
        return sb.toString();
    }

    @Security.Authenticated(Secured.class)
    public static Result regenerateKey(Long id) {
        User currentUser = Secured.getCurrentUser();

        if (!Resource.hasAccess(id, currentUser)) {
            return notFound("Resource does not exist: " + id);
        }

        Resource resource = Resource.get(id, currentUser);

        if (resource == null) {
            return notFound("Resource does not exist: " + id);
        }

        String key = resource.updateKey();

        return ok(key);
    }

}
