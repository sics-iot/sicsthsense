package logic

import scala.Option.option2Iterable
import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.util.Try

import controllers.Utils
import models.FileSystem
import models.Representation
import models.Resource
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

  def updateResource(id: Long, changes: Resource): Result[Resource] = Result(Try {
    val res = Resource.getById(id)

    res.updateResource(changes)

    for (sp <- changes.streamParsers) {
      if (sp.id == null) {
        sp.resource = res
        StreamParser.create(sp)
      } else {
        StreamParser.find.byId(id).updateStreamParser(sp)
      }
    }

    res
  })

  private def newParser(prefix: String, node: JsValue): Option[StreamParser] = node match {
    case JsBoolean(v) => Some(new StreamParser(null, prefix, "application/json", prefix, "unix", 1, 2, 1))
    case JsNumber(v)  => Some(new StreamParser(null, prefix, "application/json", prefix, "unix", 1, 2, 1))
    case JsString(v)  => Some(new StreamParser(null, prefix, "application/json", prefix, "unix", 1, 2, 1))
    case _            => None
  }

  private def getParsers(prefix: String, obj: JsValue): Seq[StreamParser] = obj match {
    case JsObject(fields) =>
      fields.flatMap {
        case (name, o: JsObject)    => getParsers(s"$prefix/$name", o)
        case (name, a: JsArray)     => Seq.empty
        case (name, u: JsUndefined) => Seq.empty
        case (name, JsNull)         => Seq.empty
        case (name, v)              => newParser(s"$prefix/$name", v)
      }
    case _ => Seq.empty
  }

  private def createParsersFromJson(resource: Resource, request: Request): Seq[StreamParser] = {
    logger.info("Trying to parse Json to then auto fill in StreamParsers!");

    val json = Json.parse(request.body)

    for {
      sp <- getParsers("/", json)
      if !FileSystem.fileExists(resource.owner, sp.streamVfilePath)
    } yield {
      sp.resource = resource
      sp.save()
      sp
    }
  }
}
