package no.samordnaopptak.TestByAnnotation

import no.samordnaopptak.TestByAnnotation.Test


object TestObject {
  
  @Test(code="""
     self.test("a") === "a22"
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

