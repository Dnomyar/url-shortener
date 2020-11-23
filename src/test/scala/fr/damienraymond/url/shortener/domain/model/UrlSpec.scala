package fr.damienraymond.url.shortener.domain.model

import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UrlSpec extends AnyFlatSpec with Matchers with EitherValues {

  it should "should create a Url from a valid url" in {
    Url.fromString("http://google.com").right.value.url should be ("http://google.com")
    Url.fromString("http://example.com").right.value.url should be ("http://example.com")
  }

  it should "fail to create a Url from a falsified" in {
    Url.fromString("htp:/google.com").isLeft should be (true)
  }

  it should "extend a url inserting a slash" in {
    val Right(url) = Url.fromString("http://google.com")
    url.slash("test").url should be ("http://google.com/test")
  }

  it should "extend a url without inserting a slash" in {
    val Right(url) = Url.fromString("http://google.com/")
    url.slash("test").url should be ("http://google.com/test")
  }

}
