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

 * Authors:
 *  26/08/2013 Adrian Kündig (adkuendi@ethz.ch)
 *  Before     Unknown
 */

package models;

import controllers.Utils;
import logic.State;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import protocol.Request;
import protocol.Response;
import scala.concurrent.duration.FiniteDuration;

import javax.persistence.*;
import java.util.concurrent.TimeUnit;

@Entity
@Table(name = "representations")
public class Representation extends Model {
    private static final int BODY_MAX_LENGTH = 8 * 1024;
    /**
     *
     */
    private static final long serialVersionUID = -1198020774968640869L;

    @Id
    public Long id;

    @Required
    public long timestamp;

    @Required
    public long expires;

    @Required
    public String contentType;

    @Required
    @Column(length = BODY_MAX_LENGTH)
    public String content;

    @ManyToOne(optional = false)
    public Resource parent = null;

    public static final Model.Finder<Long, Representation> find =
            new Model.Finder<Long, Representation>(Long.class, Representation.class);

    public long getTimestamp() {
        return timestamp;
    }

    public FiniteDuration getExpiresAsDuration() {
        return FiniteDuration.apply(expires, TimeUnit.SECONDS);
    }

    private void normalize() {
        State.notNull(content);

        content = content.substring(0, Math.min(BODY_MAX_LENGTH, content.length()));
    }

    public static Representation fromRequest(Request req, Resource parent) {
        Representation repr = new Representation();

        repr.content = req.body();
        repr.contentType = req.contentType();
        repr.expires = 0;
        repr.timestamp = Utils.currentTime();
        repr.parent = parent;

        repr.normalize();

        return repr;
    }

    public static Representation fromResponse(Response res, Resource parent) {
        Representation repr = new Representation();

        repr.content = res.body();
        repr.contentType = res.contentType();
        repr.expires = res.expires();
        repr.timestamp = res.receivedAt();
        repr.parent = parent;

        repr.normalize();

        return repr;
    }

    public static Representation getByResourceId(long id) {
        return find.where().eq("parent", Resource.getById(id)).findUnique();
    }

    public static Representation create(Representation repr) {
        repr.save();
        return repr;
    }
}
