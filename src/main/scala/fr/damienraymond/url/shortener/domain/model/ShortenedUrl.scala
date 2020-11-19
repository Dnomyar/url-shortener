package fr.damienraymond.url.shortener.domain.model

case class ShortenedUrlId(id: String)

case class ShortenedUrl(id: ShortenedUrlId, originalUrl: Url)