package fr.damienraymond.url.shortener.domain.repository

import cats.Applicative
import cats.effect.{IO, Sync}
import cats.effect.concurrent.Ref
import fr.damienraymond.url.shortener.domain.model.{ShortenedUrl, ShortenedUrlId, Url}
import cats.implicits._

class FakeShortenedUrlRepository[F[_]: Applicative](ref: Ref[F, Map[ShortenedUrlId, ShortenedUrl]]) extends ShortenedUrlRepository[F] {
  override def get(id: ShortenedUrlId): F[Option[ShortenedUrl]] =
    ref.get.map(_.get(id))

  override def getByUrl(url: Url): F[Option[ShortenedUrl]] =
    ref.get.map(_.find(_._2.originalUrl == url).map(_._2))

  override def save(shortenedUrl: ShortenedUrl): F[Unit] =
    ref.update(_ + (shortenedUrl.id -> shortenedUrl))
}


object FakeShortenedUrlRepository {
  def make[F[_]: Sync](map: Map[ShortenedUrlId, ShortenedUrl]) = {
    for{
      ref <- Ref[F].of(map)
    } yield new FakeShortenedUrlRepository[F](ref)
  }
}

