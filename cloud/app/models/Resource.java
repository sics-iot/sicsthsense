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

package models;

import com.avaje.ebean.Ebean;
import controllers.ScalaUtils;
import controllers.Utils;
import logic.Argument;
import play.Logger;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.libs.F.Promise;
import protocol.GenericRequest;
import protocol.Request;
import protocol.Response;
import protocol.coap.CoapProtocol;
import protocol.http.HttpProtocol;
import rx.Observable;
import scala.collection.immutable.Map;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import javax.persistence.*;
import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Entity
@Table(name = "resources", uniqueConstraints = {@UniqueConstraint(columnNames = {"owner_id",
        "parent_id", "label"})})
public class Resource extends Model {
    @Id
    public Long id;

    @ManyToOne(optional = false)
    public User owner;

    @Required
    public String label = "NewResource" + Utils.timeStr(Utils.currentTime());

    public long pollingPeriod = 0L;
    public long lastPolled = 0L;
    public long lastPosted = 0L;

    @ManyToOne(cascade = {CascadeType.ALL})
    public Resource parent = null;
    // if parent is not null, pollingUrl should be a subpath under parent
    // never use field access. Always use getter...
    private String pollingUrl = "";
    public String pollingAuthenticationKey = null;
    public String description = "";

    /**
     * Secret key for authenticating posts coming from outside
     */
    @Column(name = "secret_key")
    public String key; // key is a reserved keyword in mysql

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    public List<Representation> representations = new ArrayList<Representation>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    public List<Resource> subResources = new ArrayList<Resource>();

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    public List<StreamParser> streamParsers = new ArrayList<StreamParser>();

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    public List<Stream> streams = new ArrayList<Stream>();

    @Version
    private int version; // for concurrency protection
    /**
     * The serialization runtime associates with each serializable class a version number, called a
     * serialVersionUID
     */
    private static final long serialVersionUID = 7683451697925144957L;

    public static Model.Finder<Long, Resource> find = new Model.Finder<Long, Resource>(Long.class,
            Resource.class);

    public Resource(Resource parent, User owner, String label, long pollingPeriod,
                    String pollingUrl, String pollingAuthenticationKey, String description) {
        super();
        this.parent = parent;
        this.label = label;
        this.owner = owner;
        this.pollingPeriod = pollingPeriod;
        this.lastPolled = 0L;
        this.lastPosted = 0L;
        this.pollingUrl = pollingUrl;
        this.pollingAuthenticationKey = pollingAuthenticationKey;
        this.description = description;
    }

    public Resource(User owner, String label, long pollingPeriod, String pollingUrl,
                    String pollingAuthenticationKey) {
        this(null, owner, label, pollingPeriod, pollingUrl, pollingAuthenticationKey, "");
    }

    public Resource(String label, long pollingPeriod, String pollingUrl,
                    String pollingAuthenticationKey) {
        this(null, null, label, pollingPeriod, pollingUrl, pollingAuthenticationKey, "");
    }

    public Resource(User user) {
        this(null, user, "NewResource" + Utils.timeStr(Utils.currentTime()), 0L, null, null, "");
    }

    public Resource() {
        this(null, null, "NewResource" + Utils.timeStr(Utils.currentTime()), 0L, null, null, "");
    }


    public boolean isUnused() {
        if (lastPolled == 0 && lastPosted == 0) {
            return true;
        }
        return false;
    }

    public String getPollingUrl() {
        return pollingUrl;
    }

    public void setPollingUrl(String pollingUrl) {
        if (pollingUrl == null) {
            Logger.warn("Trying to set URL to null!");
            return;
        }
        if (pollingUrl.endsWith("/")) {
            pollingUrl = pollingUrl.substring(0, pollingUrl.length() - 1);
        }
        this.pollingUrl = pollingUrl;
    }

    /**
     * Call to create, or update an access token
     */
    public String updateKey() {
        String newKey = UUID.randomUUID().toString();
        key = newKey;
        if (id > 0) {
            this.update();
        }
        return key;
    }

    public String getKey() {
        return key;
    }

    public String getUrl() {
        String path = "";
        if (parent != null && parent.hasUrl()) {
            // if (parent.getUrl().endsWith("/")) {
            // path = parent.getUrl().substring(0, parent.getUrl().length() - 1);
            // } else {
            // path = parent.getUrl();
            // }
            path = parent.getUrl();
        }
        if (getPollingUrl() == null) {
            return null;
        }
        path += getPollingUrl();

        if (!path.equalsIgnoreCase("") && !path.startsWith("http://")
                && !path.startsWith("https://") && !path.startsWith("coap://")) {
            path = "http://" + path;
        }

        return path;
    }

    public boolean hasUrl() {
        return (Utils.isValidURL(getUrl()));
    }

    public Promise<Response> request() {
        // Get Url and parse default parameters
        final URI uri = URI.create(getUrl());
        final Map<String, String[]> headers =  ScalaUtils.emptyMap();
        final Map<String, String[]> params = ScalaUtils.parseQueryString(uri.getQuery());

        // Create the Request
        final Request req =
                new GenericRequest(uri, "GET", headers, params, "");

        return request(req);
    }

    public boolean isPoll() {
        return pollingPeriod > 0;
    }

    public boolean isPush() {
        return pollingPeriod == 0;
    }

    public boolean isObserve() {
        return pollingPeriod == -1;
    }

    public FiniteDuration getPollingPeriodDuration() {
        return FiniteDuration.apply(pollingPeriod, TimeUnit.SECONDS);
    }

    public Promise<Response> request(String method, Map<String, String[]> headers,
                                     Map<String, String[]> queryString, String body) {
        if (method == null) throw new IllegalArgumentException();
        if (headers == null) headers = ScalaUtils.emptyMap();
        if (queryString == null) queryString = ScalaUtils.emptyMap();

        // Get Url and parse parameters
        final URI uri = URI.create(getUrl());
        final Map<String, String[]> params = ScalaUtils.parseQueryString(uri.getQuery(), queryString);

        // Create the Request
        final Request req = new GenericRequest(uri, method, headers, params, body);

        return request(req);
    }

    public Promise<Response> request(Request req) {
        final URI uri = req.uri();

        // Create connection depending on protocol
        if (uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https")) {
            final Future<Response> promise = HttpProtocol.request(req);
            return new Promise<Response>(promise);
        } else if (uri.getScheme().equalsIgnoreCase("coap")) {
            final Future<Response> promise = CoapProtocol.request(req);
            return new Promise<Response>(promise);
        } else {
            throw new IllegalStateException("Unknown protocol in uri: " + getUrl());
        }
    }

    public Observable<Response> observe() {
        // Get Url and parse default parameters
        final URI uri = URI.create(getUrl());
        final Map<String, String[]> params = ScalaUtils.parseQueryString(uri.getQuery());

        // Create connection depending on protocol
        if (uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https")) {
            return HttpProtocol.observe(uri, params);
        } else if (uri.getScheme().equalsIgnoreCase("coap")) {
            return CoapProtocol.observe(uri, params);
        } else {
            throw new IllegalStateException("Unknown protocol in uri: " + getUrl());
        }
    }

    public Boolean checkKey(String token) {
        return key.equals(this.key);
    }

    public String showKey(User user) {
        if (this.owner.equals(user)) {
            return this.key;
        }
        return null;
    }

    public void updateResource(Resource resource) {
        this.label = resource.label;
        this.pollingPeriod = resource.pollingPeriod;
        this.pollingUrl = resource.getPollingUrl();
        this.description = resource.description;

        if (key == null || "".equalsIgnoreCase(key)) {
            updateKey();
        }

        save();

        // update indexes
        Resource.index(this);
    }

    public void verify() {
        this.label = label.replaceAll("[\\/:\"*?<>|']+", "");
    }

    @Override
    public void update() {
        verify();
        super.update();
    }

    @Override
    public void save() {
        verify();
        super.save();
    }

    public static Resource getById(Long id) {
        Resource resource = find.byId(id);
        return resource;
    }

    public static Resource get(Long id, String key) {
        Resource resource = find.byId(id);
        if (resource != null && resource.checkKey(key)) return resource;
        return null;
    }

    public static Resource get(Long id, User user) {
        Resource resource = find.byId(id);
        if (resource != null && resource.owner.equals(user)) return resource;
        return null;
    }

    public static Resource getByKey(String key) {
        Resource resource = find.where().eq("key", key).findUnique();
        return resource;
    }

    public static boolean hasAccess(Long id, User user) {
        Resource resource = find.byId(id);
        return resource != null && resource.owner.equals(user);
    }

    public static boolean labelExists(User user, Resource parent, String label) {
        Argument.notNull(user);
        Argument.notEmpty(label);

        return
                find.select("id, owner, label, parent")
                        .where()
                        .eq("owner", user)
                        .eq("parent", parent)
                        .eq("label", label)
                        .findRowCount() > 0;
    }

    public static List<Resource> availableResources(User user) {
        Argument.notNull(user);

        return
            Resource.find
                .where()
                .eq("owner", user)
                .findList();
    }

    public static Resource create(User user) {
        Argument.notNull(user);

        Resource resource = new Resource(user);
        // Liam: not sure if we need an index creation here?
        // Beshr: I added it in the other create()
        return Resource.create(resource);
    }

    public static void index(Resource resource) {
        /*
         * Search disabled try { // add search indexing through Elastic Search
         * Logger.warn("Trying to send indexed resource"); Indexer indexer = new Indexer();
         * indexer.id = resource.id; indexer.label = resource.label; indexer.url =
         * resource.getUrl(); //Beshr: to get the full url if (!resource.description.equals("")) {
         * indexer.description = resource.description; } indexer.index(); // Not sure if this is
         * actually required? //IndexService.refresh(); } catch (java.lang.NullPointerException e) {
         * Logger.info("ElasticSearch server not available"); } catch (Throwable e) { // catch all!
         * Logger.error("ElasticSearch index() error! " + e.getMessage()); }
         */
    }

    public static Resource create(Resource resource) {
        Argument.notNull(resource);
        Argument.notNull(resource.owner);

        if (!Utils.isNullOrWhitespace(resource.label) &&
                labelExists(resource.owner, resource.parent, resource.label)) {
            resource.label =
                    resource.label + new Random(new Date().getTime()).nextInt() + "_at_"
                            + (new Date().toString());
        }

        resource.save();
        resource.updateKey();

        Resource.index(resource);

        return resource;
    }

    public static void delete(Long id) {
        Resource resource = find.byId(id);
        if (resource != null) resource.delete();

        // Liam: need to delete index for this resource
        // Beshr: Maybe in the resource.delete()?
        // TODO: Adrian: Or here because I deleted resource.delete()
    }

}
