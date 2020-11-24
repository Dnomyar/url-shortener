package fr.damienraymond.url.shortener

import java.util.concurrent.Executors

import cats.effect.{IO, _}
import fr.damienraymond.url.shortener.infrastructure.http.{ShortenUrlCommand, ShortenUrlResponse}
import org.http4s.Method.{GET, POST}
import org.http4s.Status._
import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{Request, Uri}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.global

class ShortenUrlSpec extends AnyFlatSpec with Matchers {

  implicit val cs = IO.contextShift(global)

  val blockingEC = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(5))

  val httpClient: Client[IO] = JavaNetClientBuilder[IO](Blocker.liftExecutionContext(blockingEC)).create

  it should "shorten and redirect" in {
    for {
      app <- App.program.start


      shortenedUrl <- httpClient.fetchAs[ShortenUrlResponse](
        Request[IO](method = POST, uri = uri"http://localhost:8080/shorten")
          .withEntity(ShortenUrlCommand("http://google.com"))
      )

      status <- httpClient.status(Request[IO](method = GET, uri = Uri.unsafeFromString(shortenedUrl.shortenedUrl)))

      _ <- IO{
        status should be (PermanentRedirect)
      }

      _ <- app.cancel
    } yield ()
  }.unsafeRunSync()

}
