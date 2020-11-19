package fr.damienraymond.url.shortener.domain.model

case class Url private(url: String)

object Url {
  def fromString(url: String): Either[MalformedUrlError, Url] = ???
}

case class MalformedUrlError(exception: Exception) extends Exception