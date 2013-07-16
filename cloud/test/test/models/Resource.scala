package test.models

import models._
import org.specs2.mutable._

import play.api.test.Helpers._
import models.ResourceLog.InteractionType
import controllers.Utils
import play.api.test.FakeApplication

class ResourceTest extends Specification {
  "Resource" should {

    "be retrieved by id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val user = User.create(new User("a@a.de", "a", "A", "B"))
        val resource = Resource.create(new Resource(user))

        User.find.all().size().mustEqual(1).orThrow
        Resource.find.all.size().mustEqual(1).orThrow
        Resource.availableResources(user).size.mustEqual(1).orThrow
      }
    }

    "be deletable" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val user = User.create(new User("a@a.de", "a", "A", "B"))
        val resource = Resource.create(new Resource(user))

        Resource.delete(resource.id)

        Resource.availableResources(user).size.mustEqual(0).orThrow
      }
    }

    "be deletable with StreamParser" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val user = User.create(new User("a@a.de", "a", "A", "B"))
        val resource = Resource.create(new Resource(user))
        val sp = StreamParser.create(new StreamParser(resource, ".*", "text/plain", "/test", "unix", 1, 2, 1))

        Resource.delete(resource.id)
        Resource.availableResources(user).size.mustEqual(0).orThrow
      }
    }

    "be deletable with StreamParser and data" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val user = User.create(new User("a@a.de", "a", "A", "B"))
        val resource = Resource.create(new Resource(user))
        val sp = StreamParser.create(new StreamParser(resource, ".*", "text/plain", "/test", "unix", 1, 2, 1))

        sp.stream.post(1.0, 12).mustEqual(true).orThrow

        Resource.delete(resource.id)
        Resource.availableResources(user).size.mustEqual(0).orThrow
      }
    }

    "be deletable with ResourceLog" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val user = User.create(new User("a@a.de", "a", "A", "B"))
        val resource = Resource.create(new Resource(user))
        val log = new ResourceLog()

        log.interactionType = InteractionType.Push
        log.method = "POST"
        log.resource = resource
        log.setCreationTimestamp(Utils.currentTime())

        ResourceLog.create(log)

        (ResourceLog.getByResource(resource) must not beNull).orThrow

        Resource.delete(resource.id)
        Resource.availableResources(user).size.mustEqual(0).orThrow
        (ResourceLog.getByResource(resource) must beNull).orThrow
      }
    }

    "be deletable with Representation" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val user = User.create(new User("a@a.de", "a", "A", "B"))
        val resource = Resource.create(new Resource(user))
        val repr = new Representation()

        repr.content = ""
        repr.contentType = "application/json"
        repr.expires = Utils.currentTime
        repr.parent = resource
        repr.timestamp = Utils.currentTime

        Representation.create(repr)

        (Representation.getByResourceId(resource.id) must not beNull).orThrow

        Resource.delete(resource.id)
        Resource.availableResources(user).size.mustEqual(0).orThrow
        (Representation.getByResourceId(resource.id) must beNull).orThrow
      }
    }
  }
}
