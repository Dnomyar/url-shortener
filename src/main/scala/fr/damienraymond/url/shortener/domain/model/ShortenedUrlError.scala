package fr.damienraymond.url.shortener.domain.model

case class ShortenedUrlError(message: String) extends Exception(message)