package fr.damienraymond.url.shortener.domain.service

import cats.effect.Sync
import cats.implicits._
import fr.damienraymond.url.shortener.domain.model.{ShortenedUrl, ShortenedUrlError, ShortenedUrlId, Url}
import fr.damienraymond.url.shortener.domain.repository.ShortenedUrlRepository


trait UrlShortener[F[_]] {

  def shorten(url: Url): F[Either[ShortenedUrlError, ShortenedUrl]]

}

object UrlShortener {
  def make[F[_]: Sync](shortenedUrlIdGenerator: ShortenedUrlIdGenerator,
                       shortenedUrlRepository: ShortenedUrlRepository[F]): UrlShortener[F] = new UrlShortener[F] {
    override def shorten(url: Url): F[Either[ShortenedUrlError, ShortenedUrl]] = {

      shortenedUrlRepository.getByUrl(url).flatMap{
        case Some(shortenedUrl) => shortenedUrl.asRight[ShortenedUrlError].pure[F]
        case None =>
          ShortenedUrl(shortenedUrlIdGenerator.generate(), url)
            .asRight[ShortenedUrlError]
            .pure[F]
      }

    }
  }
}
