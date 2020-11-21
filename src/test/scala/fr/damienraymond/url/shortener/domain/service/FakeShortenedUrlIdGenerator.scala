package fr.damienraymond.url.shortener.domain.service

import fr.damienraymond.url.shortener.domain.model.ShortenedUrlId

import scala.collection.mutable

object FakeShortenedUrlIdGenerator {
  def withSameResult(result: String): ShortenedUrlIdGenerator =
    () => ShortenedUrlId(result)

  def withQueueOfResult(results: String*): ShortenedUrlIdGenerator = {
    val q = mutable.Queue(results: _*)
    () => ShortenedUrlId(q.dequeue())
  }
}