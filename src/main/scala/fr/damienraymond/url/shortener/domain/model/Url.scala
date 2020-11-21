package fr.damienraymond.url.shortener.domain.model

import java.net.URL

import cats.implicits._

import scala.util.{Failure, Success, Try}

case class Url private(url: String)

object Url {
  def fromString(url: String): Either[MalformedUrlError, Url] =
    Try(new URL(url)) match {
      case Failure(_) => MalformedUrlError(url).asLeft[Url]
      case Success(_) => Url(url).asRight[MalformedUrlError]
    }
}

case class MalformedUrlError(input: String) extends Exception(s"Expected a well formatted url but got $input")