package fr.damienraymond.url.shortener.domain.service

import cats.effect.IO
import fr.damienraymond.url.shortener.domain.model.{ShortenedUrl, ShortenedUrlId, Url}
import fr.damienraymond.url.shortener.domain.repository.InMemoryShortenedUrlRepository
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ShortenedUrlFinderServiceSpec extends AnyFlatSpec with Matchers {

  it should "find a existing url from the id" in {
    val Right(url) = Url.fromString("http://example.com")
    val existingShortenedUrl = ShortenedUrl(ShortenedUrlId("d0f2DE"), url)
    for {
      repo <- InMemoryShortenedUrlRepository.make[IO](Map(existingShortenedUrl.id -> existingShortenedUrl))
      shortenedUrlFinderService = ShortenedUrlFinderService.make[IO](repo)
      shortenedUrlFound <- shortenedUrlFinderService.find(ShortenedUrlId("d0f2DE"))
    } yield shortenedUrlFound should contain (existingShortenedUrl)
  }.unsafeRunSync()

  it should "not find an non existing url" in {
    val Right(url) = Url.fromString("http://example.com")
    val existingShortenedUrl = ShortenedUrl(ShortenedUrlId("d0f2DE"), url)
    for {
      repo <- InMemoryShortenedUrlRepository.make[IO](Map(existingShortenedUrl.id -> existingShortenedUrl))
      shortenedUrlFinderService = ShortenedUrlFinderService.make[IO](repo)
      shortenedUrlFound <- shortenedUrlFinderService.find(ShortenedUrlId("5dK1qw"))
    } yield shortenedUrlFound should be (empty)
  }.unsafeRunSync()

}
