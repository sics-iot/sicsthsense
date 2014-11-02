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
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

/* Description:
 * TODO:
 * */
package se.sics.sicsthsense.resources.atmosphere;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.inject.Inject;
import java.io.InputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.dropwizard.jersey.caching.CacheControl;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.pegdown.PegDownProcessor;
import java.io.IOException;
import java.net.URL;

import se.sics.sicsthsense.core.*;
import se.sics.sicsthsense.jdbi.*;
import se.sics.sicsthsense.model.BaseModel;
import se.sics.sicsthsense.views.PublicFreemarkerView;
import se.sics.sicsthsense.Utils;

/**
 *
 * @since 0.0.1
 */
@Path("/{userId}/resources/{resourceId}/streams/{streamId}/monitor")
@Produces(MediaType.TEXT_HTML)
public class Monitor {
	private final StorageDAO storage;
	private final Logger logger = LoggerFactory.getLogger(Monitor.class);

	@Inject
	public Monitor() {
		this.storage = DAOFactory.getInstance();
	}

  /**
	 *
   */
	@GET
	@Timed
	//public PublicFreemarkerView monitor() {
	public Response monitor(@PathParam("userId") long userId, @PathParam("resourceId") String resourceName, @PathParam("streamId") String streamName, @QueryParam("key") String key) throws IOException {
		User user = storage.findUserById(userId);
		Resource resource = Utils.findResourceByIdName(storage, resourceName);
		Stream stream     = Utils.findStreamByIdName(storage, streamName);
		Utils.checkHierarchy(storage, user, resource, stream);
		if (!resource.isAuthorised(key) && !user.isAuthorised(key) && !stream.isAuthorised(key)) { return Utils.resp(Status.FORBIDDEN, new JSONMessage("Error: Key does not match! "+key), logger); }

    URL url = Monitor.class.getResource("/views/pub.html");
	logger.info("Monitor URL: "+url);
	if (url==null) {return Utils.resp(Status.NOT_FOUND,"Missing internal resource web page",null);}
    String markdown = Resources.toString(url, Charsets.UTF_8).trim();
	markdown = markdown.replace("%userId%",    String.valueOf(userId));
	markdown = markdown.replace("%resourceId%",String.valueOf(resource.getId()));
	markdown = markdown.replace("%streamId%",  String.valueOf(streamName));
	return Utils.resp(Status.OK, markdown, null);
  }

}
