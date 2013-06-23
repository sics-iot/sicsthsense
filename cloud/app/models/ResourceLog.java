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
import com.avaje.ebean.annotation.EnumValue;
import controllers.Utils;
import logic.Argument;
import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.mvc.Http.HeaderNames;
import protocol.Request;
import protocol.Response;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "resource_log")
public class ResourceLog extends Model {
    private static final Logger.ALogger logger = Logger.of(ResourceLog.class);
    private static final int MAX_LENGTH = 4 * 1024;
    /**
     *
     */
    private static final long serialVersionUID = -5437709421347005124L;

    @Id
    public Long id;

    @ManyToOne
    @Column(nullable = false)
    public Resource resource;

    @Constraints.Required
    public long creationTimestamp;

    public Long responseTimestamp;

    public static enum InteractionType {
        @EnumValue("U")
        Push,
        @EnumValue("D")
        Pull,
        @EnumValue("O")
        Proxy
    }

    public InteractionType interactionType = InteractionType.Push;

    @Constraints.Pattern("GET|POST|PUT|DELETE")
    @Column(nullable = false)
    public String method = "";

    public String uri = "";

    @Column(length = MAX_LENGTH)
    public String headers = "";

    public static Model.Finder<Long, ResourceLog> find = new Model.Finder<Long, ResourceLog>(
            Long.class, ResourceLog.class);

    public ResourceLog(long id, Resource resource, long creationTimestamp, long responseTimestamp,
                       String method, String uri, String headers) {
        super();
        this.id = id;
        this.resource = resource;
        this.method = method;
        this.uri = uri;
        this.headers = headers;
        setCreationTimestamp(creationTimestamp);
        setResponseTimestamp(responseTimestamp);
    }

    public ResourceLog() {
    }

    public static ResourceLog fromRequest(Resource resource, Request request, long creationTimestamp) {
        Argument.notNull(resource);
        Argument.notNull(request);

        ResourceLog log = new ResourceLog();

        try {
            log.resource = resource;
            log.method = request.method();
            log.uri = request.uri().toString();
            log.headers =
                    HeaderNames.CONTENT_TYPE + " " + request.header(HeaderNames.CONTENT_TYPE) + "\n" +
                            HeaderNames.CONTENT_ENCODING + " " + request.header(HeaderNames.CONTENT_ENCODING) + "\n" +
                            HeaderNames.CONTENT_LENGTH + " " + request.header(HeaderNames.CONTENT_LENGTH) + "\n";

            log.setCreationTimestamp(creationTimestamp);
        } catch (Exception e) {
            Logger.error(e.getMessage() + e.getStackTrace()[0].toString() + e.toString());
        }

        return log;
    }

    public static ResourceLog fromResponse(Resource resource, Response response, long creationTimestamp,
                                           long responseTimestamp) {
        Argument.notNull(resource);
        Argument.notNull(response);

        ResourceLog log = new ResourceLog();

        try {
            log.resource = resource;
            log.method = response.request().method();
            log.uri = response.uri().toString();
            log.headers =
                    "Status " + response.statusText() + "\n" + HeaderNames.CONTENT_TYPE + " "
                            + response.contentType() + "\n" + HeaderNames.CONTENT_ENCODING
                            + " " + response.contentEncoding() + "\n"
                            + HeaderNames.CONTENT_LENGTH + " " + response.contentLength()
                            + "\n";

            log.setCreationTimestamp(creationTimestamp);
            log.setResponseTimestamp(responseTimestamp);
        } catch (Exception e) {
            Logger.error(e.getMessage() + e.getStackTrace()[0].toString() + e.toString());
        }

        return log;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = (creationTimestamp <= 0) ? Utils.currentTime() : creationTimestamp;
    }

    public void setResponseTimestamp(long responseTimestamp) {
        this.responseTimestamp = (responseTimestamp <= 0) ? Utils.currentTime() : responseTimestamp;
    }

    public String getTimestamp() {
        return new Date(creationTimestamp).toString();
    }

    public static ResourceLog getByResource(Resource resource) {
        Argument.notNull(resource);

        ResourceLog rpl =
                find.where()
                        .eq("resource_id", resource.id)
                        .orderBy("-creationTimestamp")
                        .setMaxRows(1)
                        .findUnique();

        if (rpl == null) {
            logger.warn("Could not find a log for resource: " + resource.id + ", label: " + resource.label);
        }

        return rpl;
    }

    public static ResourceLog getById(Long id) {
        return find.byId(id);
    }

    public static ResourceLog create(ResourceLog log) {
        log.save();

        return log;
    }

    public static void delete(Long id) {
        find.ref(id).delete();
    }

    public static void deleteByResource(Resource resource) {
        Ebean.delete(find.where().eq("resource_id", resource.id).findList());
    }

}
