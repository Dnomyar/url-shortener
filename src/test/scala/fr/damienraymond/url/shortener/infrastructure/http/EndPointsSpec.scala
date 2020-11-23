package fr.damienraymond.url.shortener.infrastructure.http

import cats.effect.IO
import fr.damienraymond.url.shortener.domain.model.Url
import org.http4s.Method.POST
import org.http4s.Request
import org.http4s.Status._
import org.http4s.implicits.http4sLiteralsSyntax
import org.scalacheck.Prop.forAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks

class EndPointsSpec extends AnyFlatSpec with Matchers with TableDrivenPropertyChecks {

  val urlExamples = Table(
    ("url", "shortened id", "base url", "expected url"),
    ("http://google.com", "5dK1qw", "http://localhost:8080", "http://localhost:8080/5dK1qw"),
    ("http://example.com", "d0f2DE", "http://0.0.0.0:8080/", "http://0.0.0.0:8080/d0f2DE"),
  )

  forAll(urlExamples) { case (url, shortenedId, baseUrl, expectedUrl) =>
    it should s"shorten $url to $expectedUrl" in {

      val Right(base) = Url.fromString(baseUrl)

      val response = new EndPoints(httpPrefix = base).routes.run(
        Request[IO](method = POST, uri = uri"shorten")
          .withEntity(ShortenUrlCommand(url))
      ).value.unsafeRunSync().get

      response.status should be(Ok)
      response.as[ShortenUrlResponse].unsafeRunSync() should be(ShortenUrlResponse(
        shortenedUrl = expectedUrl,
        originalUrl = url
      ))
    }
  }

  it should "fail when the resquest is not valid" in {
    val Right(base) = Url.fromString("http://localhost:8080")

    val response = new EndPoints(httpPrefix = base).routes.run(
      Request[IO](method = POST, uri = uri"shorten")
        .withEmptyBody
    ).value.unsafeRunSync().get

    response.status should be(BadRequest)
  }

}

