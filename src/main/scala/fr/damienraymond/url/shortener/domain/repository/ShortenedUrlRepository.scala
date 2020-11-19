package fr.damienraymond.url.shortener.domain.repository

import fr.damienraymond.url.shortener.domain.model.{ShortenedUrl, ShortenedUrlId}

trait ShortenedUrlRepository[F[_]] {

  def get(id: ShortenedUrlId): F[Option[ShortenedUrl]]

  def save(shortenedUrl: ShortenedUrl): F[Unit]

}
