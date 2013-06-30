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

import models.Resource;
import models.User;
import play.Logger;
import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Http.HeaderNames;
import play.mvc.Result;
import play.mvc.Security;
import protocol.Response;
import scala.collection.immutable.Map;

public class Proxy extends Controller {
    private final static Logger.ALogger logger = Logger.of(Proxy.class);

    /*** Request timeout (in ms) **/
    public final static Long REQUEST_TIMEOUT = 30000L; // 30 seconds

    @Security.Authenticated(Secured.class)
    public static Result forwardById(Long id, String arguments) {
        final User currentUser = Secured.getCurrentUser();
        final Resource resource = Resource.get(id, currentUser);
        final Map<String, String[]> params = ScalaUtils.parseQueryString(arguments);

        if (resource == null) {
            return badRequest("Resource does not exist: " + id);
        }

        return forward(resource, params);
    }

    public static Result forwardByKey(String key, String arguments) {
        final Resource resource = Resource.getByKey(key);
        final Map<String, String[]> params = ScalaUtils.parseQueryString(arguments);

        if (resource == null) {
            return badRequest("Resource does not exist: " + key);
        }

        return forward(resource, params);
    }

    private static Result forward(final Resource resource, final Map<String, String[]> queryParameters) {

        final String method = request().method();
        Map<String, String[]> headers = ScalaUtils.toScalaMap(request().headers());
        final Map<String, String[]> queryString = ScalaUtils.toScalaMap(request().queryString());
        final String body = request().body().asText();

        if (headers.contains(ACCEPT_ENCODING)) {
            headers = headers.updated(ACCEPT_ENCODING, new String[0]);
        }
        if (headers.contains(HOST)) {
            headers = headers.updated(HOST, new String[] {headers.apply(HeaderNames.HOST)[0]});
        }

        Promise<Response> responsePromise = resource.request(method, headers, queryString, body);
        Promise<Result> resultPromise = responsePromise.map(new F.Function<Response, Result>() {
            public Result apply(Response response) {
                String contentType;

                try {
                    contentType = response.contentType();
                } catch (Exception e) {
                    contentType = "<Content Type not found>";
                }

                long contentLength = response.contentLength();
                logger.info("Got response for: " + method + " " + resource.getUrl()
                        + ", content-type: " + contentType
                        + ", body length: " + contentLength + " bytes");
                return status(response.status(), response.body());
            }
        });

        return async(resultPromise);
    }
}
