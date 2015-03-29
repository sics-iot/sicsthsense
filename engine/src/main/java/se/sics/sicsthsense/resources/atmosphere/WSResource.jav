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
 * @author Liam McNamara 
 */
@Path("/{userId}/resources/{resourceId}/ws")
public class WSResource {

    //private @PathParam("streamId") Broadcaster topic;
		private final Logger logger = LoggerFactory.getLogger(WSResource.class);
		private final StorageDAO storage;
		public ParseData parseData;

		public WSResource() {
			this.storage = DAOFactory.getInstance();
			this.parseData = new ParseData();;
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
    public Broadcastable publish(@PathParam("userId") long userId, @PathParam("resourceId") String resourceName, @QueryParam("key") String key, @FormParam("message") String data) {
			User user = storage.findUserById(userId);
			Resource resource = Utils.findResourceByIdName(resourceName);
			Stream stream =			Utils.findStreamByIdName(topic.getID());
			Utils.checkHierarchy(user,resource,stream);
			
			//if (!resource.isAuthorised(key) && !user.isAuthorised(key)) { return Utils.resp(Status.FORBIDDEN, "Error: Key does not match! "+key, logger); }
/*
			logger.info(" Just received message: "+datapoint.toString());

			// if parsers are undefined, create them!
			List<Parser> parsers = storage.findParsersByResourceId(resource.getId());
			if (parsers==null || parsers.size()==0) { 
				logger.info("No parsers defined! Trying to auto create for: "+resource.getLabel());
				try {
					// staticness is a mess...
					parseData.autoCreateJsonParsers(PollSystem.getInstance().mapper, resource, data); 
				} catch (Exception e) {
					//return Utils.resp(Status.BAD_REQUEST, "Error: JSON parsing for auto creation failed!", logger);
					logger.error("Error: JSON parsing for auto creation failed!");
					return null;
				}
			}
			//run it through the parsers and update resource log
			//	Utils.applyParsers(resource, data);
			*/

			// update Resource last_posted
			storage.postedResource(resource.getId(),System.currentTimeMillis());
      return new Broadcastable(datapoint.toJson(), "", topic);
    }
}
