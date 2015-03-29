/*
 * Copyright 2013 Jeanfrancois Arcand
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package se.sics.sicsthsense.resources.atmosphere;

import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.atmosphere.annotation.Broadcast;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.jersey.Broadcastable;
import org.atmosphere.jersey.SuspendResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.sicsthsense.Utils;
import se.sics.sicsthsense.core.*;
import se.sics.sicsthsense.jdbi.*;
import se.sics.sicsthsense.model.*;

/**
 *
 * @author Jeanfrancois Arcand
 * @author Liam McNamara (minor changes)
 */
@Path("/{userId}/resources/{resourceId}/streams/{streamId}/ws")
public class WSStream {

    private @PathParam("streamId") Broadcaster topic;
	private final Logger logger = LoggerFactory.getLogger(WSStream.class);
	private final StorageDAO storage;
	public ParseData parseData;

	public WSStream () {
		this.storage = DAOFactory.getInstance();
		this.parseData = new ParseData(storage);;
	}

    @GET
    public SuspendResponse<String> subscribe() {
		logger.info("Just received subscription");
      return new SuspendResponse.SuspendResponseBuilder<String>()
        .broadcaster(topic)
        .outputComments(true)
        .addListener(new EventsLogger())
        .build();
    }

    @POST
    @Broadcast
    @Produces("text/html;charset=ISO-8859-1")
    //public Broadcastable publish(@FormParam("message") String data) {
    public Broadcastable publish(@PathParam("userId") long userId, @PathParam("resourceId") String resourceName, @QueryParam("key") String key, @FormParam("message") String data) throws Exception {
			User user = storage.findUserById(userId);
			Resource resource = Utils.findResourceByIdName(storage, resourceName);
			Stream stream     = Utils.findStreamByIdName(storage, topic.getID());
			Utils.checkHierarchy(storage,user,resource,stream);

			//if (!resource.isAuthorised(key) && !user.isAuthorised(key)) { return Utils.resp(Status.FORBIDDEN, "Error: Key does not match! "+key, logger); }
			DataPoint datapoint = new DataPoint(data);
			//logger.info("Publish: "+datapoint.toString());
			datapoint.setStreamId(stream.getId()); // keep consistency
			Utils.insertDataPoint(storage,datapoint); // insert first to fail early
			topic.broadcast(datapoint.toString());
			stream.notifyDependents(storage); // notify all streams that depend on this
			stream.testTriggers(datapoint); // see if any of the actions are triggered

      return new Broadcastable(datapoint.toJson(), "", topic);
    }
}
