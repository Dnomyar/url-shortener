package fr.damienraymond.url.shortener

import cats.effect.{ExitCode, IO, IOApp}
import fr.damienraymond.url.shortener.domain.model.Url
import fr.damienraymond.url.shortener.infrastructure.http.EndPoints
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.global

class App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val Right(base) = Url.fromString("http://localhost:8080")

    val endPoints = new EndPoints(httpPrefix = base)
    
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(endPoints.routes.orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
