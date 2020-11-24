package fr.damienraymond.url.shortener.infrastructure.http

import cats.effect.Sync
import io.circe._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

case class ShortenUrlResponse(shortenedUrl: String, originalUrl: String)

object ShortenUrlResponse {
  implicit val shortenUrlResponseCircleEncoder: Encoder[ShortenUrlResponse] = deriveEncoder[ShortenUrlResponse]

  implicit def shortenUrlResponseHttp4sEncoder[F[_]]: EntityEncoder[F, ShortenUrlResponse] = jsonEncoderOf[F, ShortenUrlResponse]

  implicit val shortenUrlResponseCircleDecoder: Decoder[ShortenUrlResponse] = deriveDecoder[ShortenUrlResponse]

  implicit def shortenUrlResponseHttp4sDecoder[F[_] : Sync]: EntityDecoder[F, ShortenUrlResponse] = jsonOf[F, ShortenUrlResponse]
}