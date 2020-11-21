package fr.damienraymond.url.shortener.domain.repository

import fr.damienraymond.url.shortener.domain.model.{ShortenedUrl, ShortenedUrlId, Url}

trait ShortenedUrlRepository[F[_]] {

  def get(id: ShortenedUrlId): F[Option[ShortenedUrl]]

  def getByUrl(url: Url): F[Option[ShortenedUrl]]

  def save(shortenedUrl: ShortenedUrl): F[Unit]

}
