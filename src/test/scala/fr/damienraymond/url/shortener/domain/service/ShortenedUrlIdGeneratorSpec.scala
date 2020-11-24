package fr.damienraymond.url.shortener.domain.service

import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable
import scala.util.Random

class ShortenedUrlIdGeneratorSpec extends AnyFlatSpec with Matchers with EitherValues {

  class FakeRandom(values: Int*) extends Random {
    private val valuesQueue = mutable.Queue(values: _*)
    override def nextInt(n: Int): Int = valuesQueue.dequeue()
  }

  it should "generate a shortened url" in {
    ShortenedUrlIdGenerator.make(new Random(), sizeToGenerate = 6).generate().id.length should be (6)
  }

  it should "the right id" in {
    ShortenedUrlIdGenerator.make(new FakeRandom(0,1,2,3,4,5), sizeToGenerate = 6).generate().id should be ("abcdef")
  }

}
