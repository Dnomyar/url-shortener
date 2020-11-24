package fr.damienraymond.url.shortener.domain.service

import fr.damienraymond.url.shortener.domain.model.ShortenedUrlId

import scala.util.Random

trait ShortenedUrlIdGenerator {

  def generate(): ShortenedUrlId

}

object ShortenedUrlIdGenerator {
  def make(random: Random, sizeToGenerate: Int): ShortenedUrlIdGenerator = new ShortenedUrlIdGenerator {
    private val chars: Seq[Char] = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')
    override def generate(): ShortenedUrlId =
      ShortenedUrlId(LazyList.continually(chars(random.nextInt(chars.length))).take(sizeToGenerate).mkString)
  }
}