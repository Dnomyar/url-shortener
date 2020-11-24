package fr.damienraymond.url.shortener

import cats.effect.{ExitCode, IO, IOApp}
import fr.damienraymond.url.shortener.domain.model.Url
import fr.damienraymond.url.shortener.domain.repository.InMemoryShortenedUrlRepository
import fr.damienraymond.url.shortener.domain.service.{ShortenedUrlFinderService, ShortenedUrlIdGenerator, UrlShortenerService}
import fr.damienraymond.url.shortener.infrastructure.http.EndPoints
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.global
import scala.util.Random

object App extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = program.as(ExitCode.Success)

  val program: IO[Unit] = {
    val Right(base) = Url.fromString("http://localhost:8080")
    for {
      repo <- InMemoryShortenedUrlRepository.make[IO](Map.empty)

      endPoints = new EndPoints(
        httpPrefix = base,
        urlShortenerService = UrlShortenerService.make[IO](
          shortenedUrlIdGenerator = ShortenedUrlIdGenerator.make(new Random(), 6),
          shortenedUrlRepository = repo,
          numberOfRetriesForDuplicateIds = 10
        ),
        shortenedUrlFinderService = ShortenedUrlFinderService.make[IO](
          repository = repo
        )
      )

      server <- BlazeServerBuilder[IO](global)
        .bindHttp(8080, "localhost")
        .withHttpApp(endPoints.routes.orNotFound)
        .serve
        .compile
        .drain

    } yield server
  }
}
