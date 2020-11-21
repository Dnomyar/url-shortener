package fr.damienraymond.url.shortener.domain.service

import fr.damienraymond.url.shortener.domain.model.{ShortenedUrl, ShortenedUrlId}
import fr.damienraymond.url.shortener.domain.repository.ShortenedUrlRepository

trait ShortenedUrlFinderService[F[_]] {

  def find(id: ShortenedUrlId): F[Option[ShortenedUrl]]

}

object ShortenedUrlFinderService {
  def make[F[_]](repository: ShortenedUrlRepository[F]): ShortenedUrlFinderService[F] =
    (id: ShortenedUrlId) => repository.get(id)
}

