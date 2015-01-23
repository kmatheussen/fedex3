package no.samordnaopptak.TestByAnnotation

import org.scalatest._
import org.scalatest.Matchers



object ObjectToTestWith {
  
  @Test(code="""
     self.test("a") =/= "a22"
     self.test("a")  === "a_"
     self.test("a2") === "a2_"
  """)
  def test(input: String): String =
    input+"_"


  @Test(code="""
    self.test2("a",List(5,2,3)) === List("b")
    self.test2("a",List(5,2,3)) === List("b")
    self.test2("a",List(5,2,3)) =/= List()
  """)
  def test2(input: String, b: List[Int]): List[String] =
    List("b")


  def notest() = None
}


class AssertByAnnotationSpec extends FlatSpec with Matchers {
  "TestObject" should "not fail the annotation tests" in {
    TestObject(ObjectToTestWith)
    true === true
  }
}
