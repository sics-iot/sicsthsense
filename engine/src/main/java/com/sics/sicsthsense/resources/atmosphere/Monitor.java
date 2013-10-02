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
package com.sics.sicsthsense.resources.atmosphere;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.yammer.dropwizard.jersey.caching.CacheControl;
import com.yammer.metrics.annotation.Timed;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.pegdown.PegDownProcessor;
import java.io.IOException;
import java.net.URL;

import com.sics.sicsthsense.model.BaseModel;
import com.sics.sicsthsense.views.PublicFreemarkerView;

/**
 *
 * @since 0.0.1
 */
//@Path("/users/{userId}/resources/{resourceId}/monitor")
@Path("/monitor")
@Produces(MediaType.TEXT_HTML)
public class Monitor {

  /**
	 *
   */
  @GET
  @Timed
  //public PublicFreemarkerView monitor(@PathParam("userId") long userId, @PathParam("resourceId") long resourceId) {
  public String monitor() throws IOException {
		System.out.println("resource ID: ");

    URL url = Monitor.class.getResource("/views/ftl/common/monitor.ftl");
    String markdown = Resources.toString(url, Charsets.UTF_8).trim();
		return markdown;
		/*
    BaseModel model = new BaseModel();
		PublicFreemarkerView<BaseModel> v = new PublicFreemarkerView<BaseModel>("common/monitor.ftl",model);
		return v.getModel().getMarkdownHtml();
		*/
  }

}
