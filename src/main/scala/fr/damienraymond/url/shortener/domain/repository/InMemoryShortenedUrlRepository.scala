package fr.damienraymond.url.shortener.domain.repository

import cats.Applicative
import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.implicits._
import fr.damienraymond.url.shortener.domain.model.{ShortenedUrl, ShortenedUrlId, Url}

class InMemoryShortenedUrlRepository[F[_]: Applicative](ref: Ref[F, Map[ShortenedUrlId, ShortenedUrl]]) extends ShortenedUrlRepository[F] {
  override def get(id: ShortenedUrlId): F[Option[ShortenedUrl]] =
    ref.get.map(_.get(id))

  override def getByUrl(url: Url): F[Option[ShortenedUrl]] =
    ref.get.map(_.find(_._2.originalUrl == url).map(_._2))

  override def save(shortenedUrl: ShortenedUrl): F[Unit] =
    ref.update(_ + (shortenedUrl.id -> shortenedUrl))
}


object InMemoryShortenedUrlRepository {
  def make[F[_]: Sync](map: Map[ShortenedUrlId, ShortenedUrl]): F[InMemoryShortenedUrlRepository[F]] = {
    for{
      ref <- Ref[F].of(map)
    } yield new InMemoryShortenedUrlRepository[F](ref)
  }
}

