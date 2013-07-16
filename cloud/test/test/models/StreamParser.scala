package test.models

import models.{StreamParser, Resource, User}
import org.specs2.mutable.Specification
import play.api.test.FakeApplication
import play.api.test.Helpers._

class StreamParserSpec extends Specification {

  "StreamParser" should {
    "be creatable" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val user = User.create(new User("a@a.de", "a", "A", "B"))
        val resource = Resource.create(new Resource(user))
        val sp = StreamParser.create(new StreamParser(resource, ".*", "text/plain", "/test", "unix", 1, 2, 1))

        StreamParser.find.all.size.mustEqual(1).orThrow
        StreamParser.forResource(resource).size().mustEqual(1).orThrow
      }
    }

    "should have stream and file" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val user = User.create(new User("a@a.de", "a", "A", "B"))
        val resource = Resource.create(new Resource(user))
        val sp = StreamParser.create(new StreamParser(resource, ".*", "text/plain", "/test", "unix", 1, 2, 1))

        (sp.stream must not beNull).orThrow
        (sp.stream.file must not beNull).orThrow

        models.Stream.availableStreams(user).size.mustEqual(1)
      }
    }

    "be deletable" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val user = User.create(new User("a@a.de", "a", "A", "B"))
        val resource = Resource.create(new Resource(user))
        val sp = StreamParser.create(new StreamParser(resource, ".*", "text/plain", "/test", "unix", 1, 2, 1))

        StreamParser.delete(sp.id)
        StreamParser.forResource(resource).size.mustEqual(0).orThrow
      }
    }

    "be deletable with data" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val user = User.create(new User("a@a.de", "a", "A", "B"))
        val resource = Resource.create(new Resource(user))
        val sp = StreamParser.create(new StreamParser(resource, ".*", "text/plain", "/test", "unix", 1, 2, 1))

        sp.stream.post(1.0, 12).mustEqual(true).orThrow

        StreamParser.delete(sp.id)
        StreamParser.forResource(resource).size.mustEqual(0).orThrow
      }
    }
  }
}
