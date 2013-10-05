package test.models

import models._
import org.specs2.mutable._

import play.api.test.Helpers._
import play.api.test.FakeApplication
import logic.{StreamDrive, ResultCode, ResourceHub}
import scala.collection.JavaConversions.{seqAsJavaList, iterableAsScalaIterable}
import controllers.StreamParserWrapper

class ResourceHubSpec extends Specification {
  "ResourceHub" should {

    "create a Resource" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val user = User.create(new User("a@a.de", "a", "A", "B"))
        val result = ResourceHub.createResource(new Resource(user))

        result.isSuccess.mustEqual(true).orThrow
        result.code.mustEqual(ResultCode.Ok).orThrow
        (result.exception must beNull).orThrow

        Resource.find.all.size.mustEqual(1).orThrow
      }
    }

    "update a Resource" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val user = User.create(new User("a@a.de", "a", "A", "B"))
        val resource = ResourceHub.createResource(new Resource(user)).data
        val changes = Resource.getById(resource.id)

        changes.label = "Dummy Lable"
        changes.description = "Dummy Description"
        changes.pollingPeriod = 15

        val result = ResourceHub.updateResource(resource.id, changes)

        result.code.mustEqual(ResultCode.Ok).orThrow

        Resource.find.all.size.mustEqual(1).orThrow
      }
    }

    "update a Resource with StreamParsers" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val user = User.create(new User("a@a.de", "a", "A", "B"))
        val resource = ResourceHub.createResource(new Resource(user)).data
        val changes = Resource.getById(resource.id)

        changes.label = "Dummy Label"
        changes.description = "Dummy Description"
        changes.pollingPeriod = 0

        val newParsers = Seq(
          new StreamParserWrapper(null, "/absolute/child1", ".*", "application/json", "unix", 1, 2, 1),
          new StreamParserWrapper(null, "relative/child1", ".*", "application/json", "unix", 1, 2, 1),
          new StreamParserWrapper(null, "/absolute/child2", ".*", "text/plain", "unix", 1, 2, 1),
          new StreamParserWrapper(null, "relative/child2", ".*", "text/plain", "unix", 1, 2, 1)
        )

        val result = ResourceHub.updateResource(resource.id, changes, newParsers)

        result.code.mustEqual(ResultCode.Ok).orThrow

        Resource.find.all.size.mustEqual(1).orThrow
        StreamParser.forResource(resource).size.mustEqual(4).orThrow

        for (spw <- newParsers) {
          StreamDrive.exists(user, spw.vfilePath).mustEqual(true)
        }
      }
    }
  }
}
