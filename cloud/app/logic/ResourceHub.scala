package logic

import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.util.Try

import controllers.Utils
import models.Representation
import models.Resource
import models.StreamParser
import protocol.Request

object ResourceHub {
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

  def post(res: Resource, req: Request): Result[Representation] = Result(Try {
    val repr = Representation.fromRequest(req, res)

    repr.save()

    for (sp <- StreamParser.forResource(res)) {
      val points = sp.parse(req.body, req.contentType)
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
}