package fr.damienraymond.url.shortener.infrastructure.http

import cats.effect.IO
import fr.damienraymond.url.shortener.domain.model.Url
import org.http4s.Method.POST
import org.http4s.Request
import org.http4s.Status.Ok
import org.http4s.implicits.http4sLiteralsSyntax
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EndPointsSpec extends AnyFlatSpec with Matchers {

  it should "shorten a url" in {
    val Right(base) = Url.fromString("http://localhost:8080")

    val response = new EndPoints(httpPrefix = base).routes.run(
      Request[IO](method = POST, uri = uri"shorten")
        .withEntity(ShortenUrlCommand("http://google.com"))
    ).value.unsafeRunSync().get

    response.status should be (Ok)
    response.as[ShortenUrlResponse].unsafeRunSync() should be (ShortenUrlResponse(
      shortenedUrl = "http://localhost:8080/5dK1qw",
      originalUrl = "http://google.com"
    ))
  }

}
