package fr.damienraymond.url.shortener.infrastructure.http

import cats.effect.{IO, Timer}
import fr.damienraymond.url.shortener.domain.model.{ShortenedUrl, ShortenedUrlId, Url}
import fr.damienraymond.url.shortener.domain.repository.InMemoryShortenedUrlRepository
import fr.damienraymond.url.shortener.domain.service.{FakeShortenedUrlIdGenerator, ShortenedUrlFinderService, UrlShortenerService}
import org.http4s.Method.{GET, POST}
import org.http4s.Request
import org.http4s.Status._
import org.http4s.dsl.io.PermanentRedirect
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.util.CaseInsensitiveString
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks

import scala.concurrent.ExecutionContext.global

class EndPointsSpec extends AnyFlatSpec with Matchers with TableDrivenPropertyChecks {

  implicit val timer: Timer[IO] = IO.timer(global)

  val urlExamples = Table(
    ("url", "shortened id", "base url", "expected url"),
    ("http://google.com", "5dK1qw", "http://localhost:8080", "http://localhost:8080/go/5dK1qw"),
    ("http://example.com", "d0f2DE", "http://0.0.0.0:8080/", "http://0.0.0.0:8080/go/d0f2DE"),
  )

  forAll(urlExamples) { case (url, shortenedId, baseUrl, expectedUrl) =>
    it should s"shorten $url to $expectedUrl" in {
      val Right(base) = Url.fromString(baseUrl)
      for {
        repo <- InMemoryShortenedUrlRepository.make[IO](Map.empty)

        urlShortenerService = UrlShortenerService.make[IO](
          shortenedUrlIdGenerator = FakeShortenedUrlIdGenerator.withSameResult(shortenedId),
          shortenedUrlRepository = repo,
          numberOfRetriesForDuplicateIds = 0
        )

        shortenedUrlFinderService = ShortenedUrlFinderService.make[IO](repository = repo)

        response <- new EndPoints(httpPrefix = base, urlShortenerService = urlShortenerService, shortenedUrlFinderService = shortenedUrlFinderService).routes.run(
          Request[IO](method = POST, uri = uri"shorten")
            .withEntity(ShortenUrlCommand(url))
        ).value

      } yield {
        response.get.status should be(Ok)
        response.get.as[ShortenUrlResponse].unsafeRunSync() should be(ShortenUrlResponse(
          shortenedUrl = expectedUrl,
          originalUrl = url
        ))
      }
    }.unsafeRunSync()
  }

  it should "fail when the json is missing" in {
    val Right(base) = Url.fromString("http://localhost:8080")

    for {
      repo <- InMemoryShortenedUrlRepository.make[IO](Map.empty)
      urlShortenerService = UrlShortenerService.make[IO](
        shortenedUrlIdGenerator = FakeShortenedUrlIdGenerator.withQueueOfResult(),
        shortenedUrlRepository = repo,
        numberOfRetriesForDuplicateIds = 0
      )

      shortenedUrlFinderService = ShortenedUrlFinderService.make[IO](repository = repo)

      response <- new EndPoints(httpPrefix = base, urlShortenerService = urlShortenerService, shortenedUrlFinderService = shortenedUrlFinderService).routes.run(
        Request[IO](method = POST, uri = uri"shorten")
          .withEmptyBody
      ).value

    } yield response.get.status should be(BadRequest)

  }.unsafeRunSync()


  it should "fail when the url is malformed" in {
    val Right(base) = Url.fromString("http://localhost:8080")

    for {
      repo <- InMemoryShortenedUrlRepository.make[IO](Map.empty)
      urlShortenerService = UrlShortenerService.make[IO](
        shortenedUrlIdGenerator = FakeShortenedUrlIdGenerator.withSameResult("5dK1qw"),
        shortenedUrlRepository = repo,
        numberOfRetriesForDuplicateIds = 0
      )

      shortenedUrlFinderService = ShortenedUrlFinderService.make[IO](repository = repo)

      response <- new EndPoints(httpPrefix = base, urlShortenerService = urlShortenerService, shortenedUrlFinderService = shortenedUrlFinderService).routes.run(
        Request[IO](method = POST, uri = uri"shorten")
          .withEntity(ShortenUrlCommand("hp:/google.com"))
      ).value

    } yield response.get.status should be(BadRequest)

  }.unsafeRunSync()

  it should "redirect permanently to the right url" in {
    val Right(base) = Url.fromString("http://localhost:8080")
    val Right(url) = Url.fromString("http://google.com")
    val id = "d0f2DE"
    val existingShortenedUrl = ShortenedUrl(ShortenedUrlId(id), url)
    for {
      repo <- InMemoryShortenedUrlRepository.make[IO](Map(existingShortenedUrl.id -> existingShortenedUrl))
      urlShortenerService = UrlShortenerService.make[IO](
        shortenedUrlIdGenerator = FakeShortenedUrlIdGenerator.withSameResult("5dK1qw"),
        shortenedUrlRepository = repo,
        numberOfRetriesForDuplicateIds = 0
      )

      shortenedUrlFinderService = ShortenedUrlFinderService.make[IO](repository = repo)

      response <- new EndPoints(httpPrefix = base, urlShortenerService = urlShortenerService, shortenedUrlFinderService = shortenedUrlFinderService).routes.run(
        Request[IO](method = GET, uri = uri"/go" / id)
      ).value

    } yield {
      response.get.status should be(PermanentRedirect)
      response.get.headers.get(CaseInsensitiveString("Location")).map(_.value) should contain(url.url)
    }

  }.unsafeRunSync()

  it should "return an not found if the id does not exists" in {
    val Right(base) = Url.fromString("http://localhost:8080")
    val id = "d0f2DE"
    for {
      repo <- InMemoryShortenedUrlRepository.make[IO](Map.empty)
      urlShortenerService = UrlShortenerService.make[IO](
        shortenedUrlIdGenerator = FakeShortenedUrlIdGenerator.withSameResult("5dK1qw"),
        shortenedUrlRepository = repo,
        numberOfRetriesForDuplicateIds = 0
      )

      shortenedUrlFinderService = ShortenedUrlFinderService.make[IO](repository = repo)

      response <- new EndPoints(httpPrefix = base, urlShortenerService = urlShortenerService, shortenedUrlFinderService = shortenedUrlFinderService).routes.run(
        Request[IO](method = GET, uri = uri"/go" / id)
      ).value

    } yield response.get.status should be(NotFound)

  }.unsafeRunSync()

}

