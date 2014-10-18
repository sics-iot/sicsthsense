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

package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import play.Logger;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Http.Request;
import protocol.Response;
import protocol.coap.CoapProtocol;
import protocol.http.HttpProtocol;
import scala.concurrent.Future;

import com.avaje.ebean.Ebean;

import controllers.ScalaUtils;
import controllers.Utils;

@Entity
@Table(name = "resources", uniqueConstraints = {@UniqueConstraint(columnNames = {"owner_id", "parent_id",
        "label"})})
public class Resource extends Operator {

    @Id
    public Long id;
    @ManyToOne
    public User owner;
    @Required
    public String label = "NewResource" + Utils.timeStr(Utils.currentTime());
    public Long pollingPeriod = 0L;
    public Long lastPolled = 0L;
    public Long lastPosted = 0L;
    @ManyToOne
    public Resource parent = null;
    // if parent is not null, pollingUrl should be a subpath under parent
    // never use field access. Always use getter...
    public String pollingUrl = "";
    public String pollingAuthenticationKey = null;
    public String description = "";

    /** Secret key for authenticating posts coming from outside */
    @Column(name = "secret_key")
    public String key; // key is a reserved keyword in mysql

    @OneToMany(mappedBy = "parent")
    public List<Resource> subResources = new ArrayList<Resource>();

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    public List<StreamParser> streamParsers = new ArrayList<StreamParser>();

    @OneToMany(mappedBy = "resource")
    public List<Stream> streams = new ArrayList<Stream>();

    @Version
    private int version; // for concurrency protection
    /**
     * The serialization runtime associates with each serializable class a version number, called a
     * serialVersionUID
     */
    private static final long serialVersionUID = 7683451697925144957L;

    public static Model.Finder<Long, Resource> find = new Model.Finder<Long, Resource>(Long.class, Resource.class);

    public Resource(Resource parent, User owner, String label, Long pollingPeriod,
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

    public Resource(User owner, String label, Long pollingPeriod, String pollingUrl,
            String pollingAuthenticationKey) {
        this(null, owner, label, pollingPeriod, pollingUrl, pollingAuthenticationKey, "");
    }

    public Resource(String label, Long pollingPeriod, String pollingUrl,
            String pollingAuthenticationKey) {
        this(null, null, label, pollingPeriod, pollingUrl, pollingAuthenticationKey, "");
    }

    public Resource(User user) {
        this(null, user, "NewResource" + Utils.timeStr(Utils.currentTime()), 0L, null, null, "");
    }

    public Resource() {
        this(null, null, "NewResource" + Utils.timeStr(Utils.currentTime()), 0L, null, null, "");
    }

		public String getHierarchy() {
			if (owner==null) {
				Logger.error("null owner");
				return "NullResource";
			}
			return "/users/"+owner.getId()+"/resources/"+id;
		}

		public boolean isUnused() {
			if (lastPolled==0 && lastPosted==0) { return true; }
			return false;
		}

    public String getPollingUrl() {
        return pollingUrl;
    }

    public void setPollingUrl(String pollingUrl) {
			if (pollingUrl==null) {Logger.warn("Trying to set URL to null!"); return;}
			if (pollingUrl.endsWith("/")) {
				pollingUrl = pollingUrl.substring(0, pollingUrl.length() - 1);
			}
			this.pollingUrl = pollingUrl;
			//rebuildEngineResource(this.owner.getId(), this.id);
    }

    /** Call to create, or update an access token */
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

    public boolean canRead(User user) {
        return (owner.equals(user)); // || isShare(user) || publicAccess;
    }

    public String getUrl() {
        String path = "";
        if (parent != null &&  parent.hasUrl()) {
//            if (parent.getUrl().endsWith("/")) {
//                path = parent.getUrl().substring(0, parent.getUrl().length() - 1);
//            } else {
//                path = parent.getUrl();
//            }
					path = parent.getUrl();
        }
				if (getPollingUrl()==null) {return null;}
        path += getPollingUrl();

        if (!path.equalsIgnoreCase("") && !path.startsWith("http://") && !path.startsWith("https://")
                && !path.startsWith("coap://")) {
            path = "http://" + path;
        }

        return path;
    }

    public boolean hasUrl() {
        return (Utils.isValidURL(getUrl()));
    }

    public Promise<Response> request(String method, Map<String, String[]> headers,
            Map<String, String[]> queryString, String body) {
        // Get Url and parse default parameters
        final String url = getUrl();
        final Map<String, String[]> params = ScalaUtils.parseQueryString(url);

        // Update default parameters with parameters passed in as argument
        params.putAll(queryString);

        // Create connection depending on protocol
        if (url.startsWith("http") || url.startsWith("https")) {
            final Future<Response> promise =
                    HttpProtocol.request(url, method, headers, params, body);
            return new Promise<Response>(promise);
        } else if (url.startsWith("coap")) {
            final Future<Response> promise =
                    CoapProtocol.request(url, method, headers, params, body);
            return new Promise<Response>(promise);
        }

        return null;
    }

    // register asychronous polling of data
    private Promise<Response> asynchPoll() {
        final Resource thisResource = this;
        final Promise<Response> promise =
                request("GET", new HashMap<String, String[]>(), new HashMap<String, String[]>(), "");

        // Update the lastPolled time
        lastPolled = Utils.currentTime();
        update();

        return promise.map(new F.Function<Response, Response>() {
            public Response apply(Response response) {
                // Log request
                // String textBody = response.getBody();
                // Logger.info("Incoming data: " +
                // response.getHeader("Content-type")
                // + textBody);
                // Stream parsers should handle data parsing and response type
                // checking..
                Long currentTime = Utils.currentTime();

                boolean parsedSuccessfully = false;
                String msgs = "";
                for (StreamParser sp : streamParsers) {
                    try {
                        parsedSuccessfully |= sp.parseResponse(response, currentTime);
                    } catch (Exception e) {
                        msgs +=
                                e.getMessage() + e.getStackTrace()[0].toString() + e.toString()
                                        + "\n";
                        Logger.error("Exception: " + thisResource.label + ": asynchPoll(): " + msgs);
                    }
                }
                // Logger.info("[asynchPoll] before resourceLog");
                ResourceLog resourceLog = new ResourceLog(thisResource, response, thisResource.lastPolled, currentTime);
                // Logger.info("[asynchPoll] after resourceLog");

                resourceLog = ResourceLog.createOrUpdate(resourceLog);
                // Logger.info("[asynchPoll] after resourceLog create");

                resourceLog.updateParsedSuccessfully(parsedSuccessfully);
                if (!msgs.equalsIgnoreCase("")) {
                    resourceLog.updateMessages(msgs);
                }

                return response;
            }
        });
    }

    public boolean poll() {
        // perform a poll() if it is time
        if (!hasUrl()) {
            return false;
        }

        long currentTime = Utils.currentTime();
        // Logger.info("time: "+currentTime+" last polled "+lastPolled+" period: "+pollingPeriod);
        if ((lastPolled + (pollingPeriod * 1000)) > currentTime) {
            return false;
        }
        // Logger.info("Poll() happening!");

        // TODO: A race is happening between checking for the lastPolled and
        // starting an async poll.
        // The responsibility of checking for polling time should happen in
        // asyncPoll.
        asynchPoll();

        return true;
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

    public void setPeriod(Long period) {
			this.pollingPeriod = period;
			rebuildEngineResource(this.owner.getId(), this.id);
    }

    public boolean parseAndPost(Request req, Long currentTime) throws Exception {
				//Logger.info("Parsing and Posting");
        boolean result = false;
        if (streamParsers != null) {
					for (StreamParser sp : streamParsers) {
						//Logger.info("Applying to parser: ");
						if (sp != null) {
							//Logger.info("handing request to stream parser:");
							// Logger.info("New request: " + req.body().asText());
							result |= sp.parseRequest(req, currentTime);
						}
					}
        }
				this.lastPosted = Utils.currentTime();
        return result;
    }

    public void updateResource(Resource resource) {
        this.label = resource.label;
        // this.key = resource.getKey();
        this.pollingPeriod = resource.pollingPeriod;
        this.lastPolled = resource.lastPolled;
        this.lastPosted = resource.lastPosted;
        this.pollingUrl = resource.getPollingUrl();
        this.parent = resource.parent;
        this.description = resource.description;
        this.pollingAuthenticationKey = resource.pollingAuthenticationKey;
        if (key == null || "".equalsIgnoreCase(key)) {
            updateKey();
        }
        update();
        // update indexes
        Resource.index(this);
				rebuildEngineResource(this.owner.getId(), this.id);
    }

    public void verify() {
    	this.label=label.replaceAll("[\\/:\"*?<>|']+", "");
    }

    @Override
    public void update() {
    	verify();
    	super.update();
			rebuildEngineResource(this.owner.getId(), this.id);
    }

    @Override
    public void save() {
    	verify();
    	super.save();
			rebuildEngineResource(this.owner.getId(), this.id);
    }

    @Override
    public void delete() {
        this.pollingPeriod = 0L;
        // remove references
				// Liam: stopped detaching resources, so they are deleted
        //Stream.dattachResource(this);
        ResourceLog.deleteByResource(this);
        // Indexer thisIndexer = Indexer.find.byId(id.toString());
        // if(thisIndexer != null) {
        // thisIndexer.deleteAsync();
        // //TODO: check for success
        // }
        // delete sub resources and their sub resources, etc...
        List<Resource> subResList = Ebean.find(Resource.class).select("id, parent, pollingPeriod").where().eq("parent_id", this.id).findList();
        for (Resource sub : subResList) {
            sub.delete();
        }
				List<Stream> streams = Ebean.find(Stream.class).where().eq("resource_id",this.id).findList();
				for (Stream stream: streams) {
					stream.delete();
				}
        super.delete();
				rebuildEngineResource(this.owner.getId(), this.id);
    }

    public static Resource getById(Long id) {
        Resource resource = find.byId(id);
        return resource;
    }

    public static Resource get(Long id, String key) {
        Resource resource = find.byId(id);
        if (resource == null) {
			Logger.error("Resource does not exist!");
			return null;
		}
		if (!resource.checkKey(key)) {
			Logger.error("User not owner of resource!");
			return null;
		}
        return resource;
    }

    public static Resource get(Long id, User user) {
        Resource resource = find.byId(id);
        if (resource == null) {
			Logger.error("Resource does not exist!");
			return null;
		}
		if (!resource.owner.equals(user)) {
			Logger.error("User not owner of resource!");
			return null;
		}
        return resource;
    }

    public static Resource getByKey(String key) {
        Resource resource = find.where().eq("key", key).findUnique();
        return resource;
    }

    public static Resource getByUserLabel(User user, Resource parent, String label) {
        Resource resource = find.select("id, owner, label, parent").where().eq("owner", user).eq("parent", parent).eq("label", label).findUnique();
        return resource;
    }

    public static List<Resource> availableResources(User user) {
        // should add public resources...
        return user.resourceList;
    }

    public static Resource create(User user) {
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
        if (resource.owner != null) {
            if (getByUserLabel(resource.owner, resource.parent, resource.label) != null) {
                resource.label =
                        resource.label + new Random(new Date().getTime()).nextInt() + "_at_"
                                + (new Date().toString());
            }
            resource.save();
            resource.updateKey();
            Resource.index(resource);
            return resource;
        }
				rebuildEngineResource(resource.owner.getId(), resource.id);
        return null;
    }

    public static void delete(Long id) {
        Resource resource = find.ref(id);
        if (resource != null) resource.delete();
				rebuildEngineResource(resource.owner.getId(), resource.id);

        // Liam: need to delete index for this resource
        // Beshr: Maybe in the resource.delete()?
    }


		// Tell the engine to rebuild the Polling System, so that the resource's GETs will be
		// performed correctly.
		public static void rebuildEngineResource(long userId, long resourceId) {
			HttpURLConnection conn;
			BufferedReader rd;
			String line;
			String result = "";
			try {
				 URL url = new URL("http://localhost:8080/users/"+userId+"/resources/"+resourceId+"/rebuild");
				 conn = (HttpURLConnection) url.openConnection();
				 conn.setRequestMethod("GET");
				 rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				 while ((line = rd.readLine()) != null) { result += line; }
				 rd.close();
			} catch (java.net.ConnectException e) {
				Logger.error("SicsthSense Engine server seems uncontactable!");
			} catch (IOException e) {
				Logger.error("Problem contacting SicsthSense Engine server!");
				//e.printStackTrace();
			} catch (Exception e) {
				Logger.error("Problem contacting SicsthSense Engine server!");
				//e.printStackTrace();
			}
			//return result;
		}


}
