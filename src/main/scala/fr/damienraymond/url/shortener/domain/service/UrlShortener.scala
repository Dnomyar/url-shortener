package fr.damienraymond.url.shortener.domain.service

import cats.effect.{Sync, Timer}
import cats.implicits._
import fr.damienraymond.url.shortener.domain.model.{ExistingIdInRepository, ShortenedUrl, ShortenedUrlId, Url}
import fr.damienraymond.url.shortener.domain.repository.ShortenedUrlRepository
import retry.{RetryPolicies, Sleep, retryingM}

trait UrlShortener[F[_]] {

  def shorten(url: Url): F[Either[ExistingIdInRepository, ShortenedUrl]]

}

object UrlShortener {
  def make[F[_] : Sync : Timer](shortenedUrlIdGenerator: ShortenedUrlIdGenerator,
                                shortenedUrlRepository: ShortenedUrlRepository[F],
                                numberOfRetriesForDuplicateIds: Int): UrlShortener[F] = new UrlShortener[F] {
    override def shorten(url: Url): F[Either[ExistingIdInRepository, ShortenedUrl]] =
      shortenedUrlRepository.getByUrl(url).flatMap {
        case Some(shortenedUrl) => shortenedUrl.asRight[ExistingIdInRepository].pure[F]
        case None =>
          retryingM[Either[ExistingIdInRepository, ShortenedUrl]](
            policy = RetryPolicies.limitRetries[F](numberOfRetriesForDuplicateIds),
            wasSuccessful = _.isRight,
            onFailure = (_, _) => ().pure[F]
          )(createAndSaveShortenedUrl(url))
      }

    private def createAndSaveShortenedUrl(url: Url): F[Either[ExistingIdInRepository, ShortenedUrl]] = {
      val id = shortenedUrlIdGenerator.generate()
      shortenedUrlRepository.get(id).flatMap {
        case Some(_) =>
          ExistingIdInRepository()
            .asLeft[ShortenedUrl]
            .pure[F]
        case None =>
          ShortenedUrl(id, url)
            .asRight[ExistingIdInRepository]
            .pure[F]
      }
    }
  }
}
