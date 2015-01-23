
Test code by annotation. Add tests like these:


```
object TestObject{
  @Test(code="""
     self.test("a") =/= "a22"
     self.test("a")  === "a_"
     self.test("a2") === "a2_"
  """)
  def test(input: String): String =
    input+"_"
}
```

where "===" means equal, and "=\=" means unequal.

And then run all annotation tests in an object like this:

```
   import no.samordnaopptak.TestByAnnotation

...

  "Test by annotation the 'TestObject' object" in {
     TestByAnnotation.testObject(TestObject)
  }
```
