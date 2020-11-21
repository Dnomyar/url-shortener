package fr.damienraymond.url.shortener.infrastructure.http

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.Method.GET
import cats.effect._
import fr.damienraymond.url.shortener.domain.model.Url
import cats.implicits._
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
import cats.effect._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._

case class ShortenUrlCommand(url: String)

object ShortenUrlCommand {
  implicit val shortenUrlCommandCircleEncoder: Encoder[ShortenUrlCommand] = deriveEncoder[ShortenUrlCommand]
  implicit def shortenUrlCommandHttp4sEncoder[F[_]]: EntityEncoder[F, ShortenUrlCommand] = jsonEncoderOf[F, ShortenUrlCommand]
}


case class ShortenUrlResponse(shortenedUrl: String, originalUrl: String)

object ShortenUrlResponse {
  implicit val shortenUrlResponseCircleEncoder: Encoder[ShortenUrlResponse] = deriveEncoder[ShortenUrlResponse]
  implicit def shortenUrlResponseHttp4sEncoder[F[_]]: EntityEncoder[F, ShortenUrlResponse] = jsonEncoderOf[F, ShortenUrlResponse]
  implicit val shortenUrlResponseCircleDecoder: Decoder[ShortenUrlResponse] = deriveDecoder[ShortenUrlResponse]
  implicit def shortenUrlResponseHttp4sDecoder[F[_]: Sync]: EntityDecoder[F, ShortenUrlResponse] = jsonOf[F, ShortenUrlResponse]
}

class EndPoints() {

  val routes = HttpRoutes.of[IO] {
    case POST -> Root / "shorten" =>
      Ok(ShortenUrlResponse(
        shortenedUrl = "http://localhost:8080/5dK1qw",
        originalUrl = "http://google.com"
      ))
  }

}