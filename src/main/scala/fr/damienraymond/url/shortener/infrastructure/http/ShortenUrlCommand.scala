package fr.damienraymond.url.shortener.infrastructure.http

import cats.effect.Sync
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

case class ShortenUrlCommand(url: String)

object ShortenUrlCommand {
  implicit val shortenUrlCommandCircleEncoder: Encoder[ShortenUrlCommand] = deriveEncoder[ShortenUrlCommand]

  implicit def shortenUrlCommandHttp4sEncoder[F[_]]: EntityEncoder[F, ShortenUrlCommand] = jsonEncoderOf[F, ShortenUrlCommand]

  implicit val shortenUrlCommandCircleDecoder: Decoder[ShortenUrlCommand] = deriveDecoder[ShortenUrlCommand]

  implicit def shortenUrlCommandHttp4sDecoder[F[_] : Sync]: EntityDecoder[F, ShortenUrlCommand] = jsonOf[F, ShortenUrlCommand]
}

