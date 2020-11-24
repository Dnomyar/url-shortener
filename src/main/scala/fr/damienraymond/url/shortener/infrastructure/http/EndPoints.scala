package fr.damienraymond.url.shortener.infrastructure.http

import cats.effect.IO
import fr.damienraymond.url.shortener.domain.model.{ExistingIdInRepository, MalformedUrlError, ShortenedUrlId, Url}
import fr.damienraymond.url.shortener.domain.service.{ShortenedUrlFinderService, UrlShortenerService}
import org.http4s.Method.GET
import org.http4s._
import org.http4s.dsl.io._

class EndPoints(httpPrefix: Url,
                urlShortenerService: UrlShortenerService[IO],
                shortenedUrlFinderService: ShortenedUrlFinderService[IO]) {

  case class ParsingError() extends Exception()
  case class ShortenedUrlIdNotFound(id: String) extends Exception()

  val routes = HttpRoutes.of[IO] {
    case req@POST -> Root / "shorten" =>
      {
        for {
          shortenUrlCommand <- req.as[ShortenUrlCommand].handleErrorWith{ _ => IO.raiseError(ParsingError()) }
          url <- IO.fromEither(Url.fromString(shortenUrlCommand.url))
          shortenedUrlEither <- urlShortenerService.shorten(url)
          shortenedUrl <- IO.fromEither(shortenedUrlEither)
          resp <- Ok(ShortenUrlResponse(
            shortenedUrl = httpPrefix.slash(shortenedUrl.id.id).url,
            originalUrl = shortenedUrl.originalUrl.url
          ))
        } yield resp
      }.handleErrorWith{
        case ParsingError() => BadRequest("BadRequest")
        case e: MalformedUrlError => BadRequest(e.getMessage)
        case ExistingIdInRepository() => InternalServerError("Unable to shorten the url, try again later")
        case _ => InternalServerError("Unexpected error")
      }

    case GET -> Root / "go" / id => {
      for {
        shortenedUrlOption <- shortenedUrlFinderService.find(ShortenedUrlId(id))
        shortenedUrl <- IO.fromOption(shortenedUrlOption)(ShortenedUrlIdNotFound(id))
        url = shortenedUrl.originalUrl.url
        resp <- PermanentRedirect(url)
      } yield resp.withHeaders(Header("Location", url))
    }.handleErrorWith{
      case ShortenedUrlIdNotFound(id) => NotFound(s"No matching url $id")
      case _ => InternalServerError("Unexpected error")
    }
  }

}
