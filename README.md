
Test code by annotation. Add tests like these:


```
object ObjectToTestWith{
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

  "Test by annotation the 'ObjectToTestWith' object" in {
     TestByAnnotation.TestObject(ObjectToTestWith)
  }
```
