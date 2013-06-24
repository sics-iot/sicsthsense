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

package logic

import controllers.Utils
import models.Representation
import models.Resource
import models.Resource.UpdateMode
import models.ResourceLog
import models.StreamParser
import play.api.Logger
import play.api.http.ContentTypes
import play.api.libs.json.JsArray
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsNull
import play.api.libs.json.JsNumber
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsUndefined
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import protocol.Request
import scala.Option.option2Iterable
import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.collection.JavaConversions.seqAsJavaList
import scala.util.Try


object ResourceHub {
  private lazy val logger = Logger(this.getClass())

  def get(res: Resource): Result[Representation] = Result(Try {
    var i = 0;
    var repr = Representation.getByResourceId(res.id)

    while (repr.expires < Utils.currentTime()) {
      if (i >= 10) return Result(ResultCode.TimedOut)

      i += 1
      repr = Representation.getByResourceId(res.id)
    }

    repr
  })

  def post(resource: Resource, request: Request): Result[Representation] = Result(Try {
    val requestTime = Utils.currentTime()

    logger.info(s"Post received from URI: ${request.uri}, Content-Type: ${request.contentType}, Content: ${request.body}");

    val resourceLog = ResourceLog.fromRequest(resource, request, requestTime)
    ResourceLog.create(resourceLog)

    // if first POST (and no poll's), auto make parsers
    if (resource.streamParsers.isEmpty() && resource.isUnused() && request.contentType.equalsIgnoreCase(ContentTypes.JSON)) {
      // Logger.info("Automatically making parsers on empty unused Resource.");
      createParsersFromJson(resource, request)
    }

    val repr = Representation.fromRequest(request, resource)
    repr.save()

    for (sp <- StreamParser.forResource(resource)) {
      val points = sp.parse(request.body, request.contentType)
      sp.stream.post(points, Utils.currentTime())
    }

    repr
  })

  def createResource(res: Resource): Result[Resource] = Result(Try {
    val stored = Resource.create(res)

    if (stored.updateMode == UpdateMode.Poll && stored.pollingPeriod > 0) {
      Poller.schedulePoll(stored.id, stored.pollingPeriod)
    }

    stored
  })

  def updateResource(id: Long, changes: Resource): Result[Resource] = Result(Try {
    val res = Resource.getById(id)
    val oldMode = res.updateMode
    val oldTime = res.pollingPeriod

    res.updateResource(changes)

    for (sp <- changes.streamParsers) {
      if (sp.id == null) {
        sp.resource = res
        StreamParser.create(sp)
      } else {
        StreamParser.find.byId(id).updateStreamParser(sp)
      }
    }

    if (res.updateMode == UpdateMode.Poll && res.pollingPeriod > 0
      && (oldMode != UpdateMode.Poll || oldTime != res.pollingPeriod)) {
      Poller.schedulePoll(res.id, res.pollingPeriod)
    }

    res
  })

  private def newParser(prefix: String, node: JsValue): Option[StreamParser] = node match {
    case JsBoolean(v) => Some(new StreamParser(prefix, "application/json", prefix, "unix", 1, 2, 1))
    case JsNumber(v) => Some(new StreamParser(prefix, "application/json", prefix, "unix", 1, 2, 1))
    case JsString(v) => Some(new StreamParser(prefix, "application/json", prefix, "unix", 1, 2, 1))
    case _ => None
  }

  private def getParsers(prefix: String, obj: JsValue): Seq[StreamParser] = obj match {
    case JsObject(fields) =>
      fields.flatMap {
        case (name, o: JsObject) => getParsers(s"$prefix/$name", o)
        case (name, a: JsArray) => Seq.empty
        case (name, u: JsUndefined) => Seq.empty
        case (name, JsNull) => Seq.empty
        case (name, v) => newParser(s"$prefix/$name", v)
      }
    case _ => Seq.empty
  }

  private def createParsersFromJson(resource: Resource, request: Request): Seq[StreamParser] = {
    logger.info("Trying to parse Json to then auto fill in StreamParsers!")

    val parsers = for {
      sp <- parsersFromJson(request.body)
      if !FileSystem.exists(resource.owner, sp.inputParser)
    } yield {
      sp.resource = resource
      sp.save()
      sp
    }

    parsers.to[Seq]
  }

  def parsersFromJson(text: String): java.util.List[StreamParser] = {
    val json = Json.parse(text)

    getParsers("/", json)
  }

  def parsersFromPlain(text: String): java.util.List[StreamParser] = {
    Seq(new StreamParser("(.*)", "text/plain", "/", "unix", 1, 2, 1))
  }
}
