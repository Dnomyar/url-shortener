package fr.damienraymond.url.shortener.domain.service

import cats.effect.IO
import cats.effect.concurrent.Ref
import fr.damienraymond.url.shortener.domain.model.{ShortenedUrl, ShortenedUrlId, Url}
import fr.damienraymond.url.shortener.domain.repository.FakeShortenedUrlRepository
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import cats.implicits._
import cats.effect.Timer

import scala.concurrent.ExecutionContext.global

class UrlShortenerSpec extends AnyFlatSpec with Matchers with EitherValues {
  implicit val timer: Timer[IO] = IO.timer(global)

  it should "shorten http://google.com" in {
    for {
      repo <- FakeShortenedUrlRepository.make[IO](Map.empty)
      urlShortener = UrlShortener.make[IO](
        shortenedUrlIdGenerator = FakeShortenedUrlIdGenerator.withSameResult("5dK1qw"),
        shortenedUrlRepository = repo,
        numberOfRetriesForDuplicateIds = 0
      )
      Right(url) = Url.fromString("http://google.com")
      shortenedUrl <- urlShortener.shorten(url)
    } yield shortenedUrl.right.value should be(ShortenedUrl(ShortenedUrlId("5dK1qw"), url))
  }.unsafeRunSync()

  it should "shorten http://example.com (with existing non related urls in the repository)" in {
    val Right(url1) = Url.fromString("http://google.com")
    val existingShortenedUrl = ShortenedUrl(ShortenedUrlId("5dK1qw"), url1)
    for {
      repo <- FakeShortenedUrlRepository.make[IO](Map(existingShortenedUrl.id -> existingShortenedUrl))
      urlShortener = UrlShortener.make[IO](
        shortenedUrlIdGenerator = FakeShortenedUrlIdGenerator.withSameResult("d0f2DE"),
        shortenedUrlRepository = repo,
        numberOfRetriesForDuplicateIds = 0
      )
      Right(url) = Url.fromString("http://example.com")
      shortenedUrl <- urlShortener.shorten(url)
    } yield shortenedUrl.right.value should be(ShortenedUrl(ShortenedUrlId("d0f2DE"), url))
  }.unsafeRunSync()

  it should "return the existing shortened url when the url already exists in the repository" in {
    val Right(url) = Url.fromString("http://example.com")
    val existingShortenedUrl = ShortenedUrl(ShortenedUrlId("d0f2DE"), url)
    for {
      repo <- FakeShortenedUrlRepository.make[IO](Map(existingShortenedUrl.id -> existingShortenedUrl))
      urlShortener = UrlShortener.make[IO](
        shortenedUrlIdGenerator = FakeShortenedUrlIdGenerator.withSameResult("5dK1qw"),
        shortenedUrlRepository = repo,
        numberOfRetriesForDuplicateIds = 0
      )
      shortenedUrl <- urlShortener.shorten(url)
    } yield shortenedUrl.right.value should be(existingShortenedUrl)
  }.unsafeRunSync()


  it should "not try to save a new shortened url with an existing id associated with another url" in {
    val Right(url1) = Url.fromString("http://example.com")
    val Right(url2) = Url.fromString("http://google.com")
    val sameId = "d0f2DE"
    val nonExistingId = "5dK1qw"
    val existingShortenedUrl = ShortenedUrl(ShortenedUrlId(sameId), url1)
    for {
      repo <- FakeShortenedUrlRepository.make[IO](Map(existingShortenedUrl.id -> existingShortenedUrl))
      urlShortener = UrlShortener.make[IO](
        shortenedUrlIdGenerator = FakeShortenedUrlIdGenerator.withQueueOfResult(sameId, nonExistingId),
        shortenedUrlRepository = repo,
        numberOfRetriesForDuplicateIds = 1
      )
      shortenedUrl <- urlShortener.shorten(url2)
    } yield shortenedUrl.right.value should be(ShortenedUrl(ShortenedUrlId(nonExistingId), url2))
  }.unsafeRunSync()

}
