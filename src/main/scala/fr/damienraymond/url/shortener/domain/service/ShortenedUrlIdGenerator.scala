package fr.damienraymond.url.shortener.domain.service

import fr.damienraymond.url.shortener.domain.model.ShortenedUrlId

import scala.util.Random

trait ShortenedUrlIdGenerator {

  def generate(): ShortenedUrlId

}

object ShortenedUrlIdGenerator {
  def make(random: Random, sizeToGenerate: Int): ShortenedUrlIdGenerator = new ShortenedUrlIdGenerator {
    override def generate(): ShortenedUrlId = ???
  }
}