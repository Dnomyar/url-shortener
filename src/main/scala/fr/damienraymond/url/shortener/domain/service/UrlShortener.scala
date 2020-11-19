package fr.damienraymond.url.shortener.domain.service

import fr.damienraymond.url.shortener.domain.model.{ShortenedUrl, ShortenedUrlError, Url}

trait UrlShortener[F[_]] {

  def shorten(url: Url): F[Either[ShortenedUrlError, ShortenedUrl]]

}
