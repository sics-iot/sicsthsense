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

import controllers.{StreamParserWrapper, Utils}
import models.Representation
import models.Resource
import models.StreamParser
import play.api.Logger
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
  private val logger = Logger(this.getClass())

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

  def post(resource: Resource, request: Request): Result[Unit] = Result(Try {
    Updater.push(resource.id, request)
  })

  def createResource(res: Resource): Result[Resource] = Result(Try {
    val stored = Resource.create(res)

    checkMode(stored)

    res
  })

  def updateResource(id: Long, changes: Resource): Result[Resource] =
    Result(Try {
      val res = Resource.getById(id)

      res.updateResource(changes)
      checkMode(res)

      res
    })

  def updateResource(
    id: Long,
    changes: Resource,
    parsers: java.util.List[StreamParserWrapper]): Result[Resource] =
    Result(Try {
      val res = Resource.getById(id)

      res.updateResource(changes)

      for (sp <- parsers) {
        val parser = sp.getStreamParser(res)

        if (parser.id == null) {
          parser.resource = res
          StreamParser.create(sp.vfilePath, parser)
        } else {
          Option(StreamParser.find.byId(id)).foreach(_.updateStreamParser(parser))
        }
      }

      checkMode(res)

      res
    })

  private def checkMode(res: Resource): Unit = {
    val id = res.id

    if (res.isPoll) {
      Updater.poll(id, res.pollingPeriod)
    } else if (res.isObserve) {
      Updater.observe(id)
    } else {
      // Updater should automatically stop polling resources
      Updater.stopObserve(id)
    }
  }

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

  def createParsersFromJson(resource: Resource, request: Request): Seq[StreamParser] = {
    logger.info("Trying to parse Json to then auto fill in StreamParsers!")

    val parsers = for {
      sp <- parsersFromJson(request.body)
      if !FileSystem.exists(resource.owner, sp.inputParser)
    } yield {
      sp.resource = resource
      StreamParser.create(sp)
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
