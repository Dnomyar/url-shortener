package fr.damienraymond.url.shortener.domain.service

import scala.util.Random

trait ShortIdGenerator {

  def generate(): String

}

object ShortIdGenerator {
  def make(random: Random, sizeToGenerate: Int): ShortIdGenerator = new ShortIdGenerator {
    override def generate(): String = ???
  }
}