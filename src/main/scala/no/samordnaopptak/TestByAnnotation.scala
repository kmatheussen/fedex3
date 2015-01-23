package no.samordnaopptak.TestByAnnotation


object TestObject{
  import scala.reflect.runtime._
  import scala.tools.reflect.ToolBox

  val cm = universe.runtimeMirror(getClass.getClassLoader)
  val tb = cm.mkToolBox()

  case class AnnotatedTest(method: java.lang.reflect.Method, code: String, methodName: String)

  def getAnnotatedTests[T](o: T): List[AnnotatedTest] = {
    val class_ = o.getClass()
    //println("class: "+class_)

    val annotatedTests = class_.getDeclaredMethods().toList.filter(method => {
      val annotations = method.getAnnotations()
      annotations.find(_.isInstanceOf[Test]) != None
    }).map(method => {
      val annotations = method.getAnnotations()
      val testAnnotation = annotations.find(_.isInstanceOf[Test]).get
      val code = testAnnotation.asInstanceOf[Test].code()
      AnnotatedTest(method, code, method.getName())
    })

    //println("annotatedTests: "+annotatedTests)

    annotatedTests
  }

  def createExceptionString(linenum: Int, methodName: String, message: String) =
    s"""Failed input/Output test #$linenum for method '$methodName': $message"""

  def splitLine(linenum: Int, methodName: String, line: String) =
    if (line.contains("===")) {
      val splitted = line.split("===")
      ("==", splitted(0), splitted(1))
    } else if (line.contains("=/=")) {
      val splitted = line.split("=/=")
      ("!=", splitted(0), splitted(1))
    } else
      throw new Exception(createExceptionString(linenum, methodName, s""""$line" does not contain "===" or "=/=")"""))

  def createAssertionCodeLine(linenum: Int, methodName: String, line: String) = {
    val (comparitor, a, b) = splitLine(linenum, methodName, line)
    s"""if (!($a $comparitor $b)) throw new Exception("\"\"""" +createExceptionString(linenum, methodName, s"""Assertion failed for "$line\"\"")""")
  }

  import scala.reflect.runtime.universe._

  def evalCode[T: TypeTag](o: T, methodName: String, code: String) = {

    def evalLine(linenum: Int, line: String) = {
      val assertionCodeLine = createAssertionCodeLine(linenum, methodName, line.trim)      
      println(Console.GREEN+s"AssertByAnnotitation: Testing #${linenum}/$methodName: "+Console.RESET+"\""+line+"\"")
      val tree = try{
        tb.parse(assertionCodeLine)
      } catch {
        case e: Throwable => throw new Exception(createExceptionString(linenum, methodName, s""""$line" failed while parsing "$assertionCodeLine": ${e.getMessage()}""""))
      }
      tb.eval(
        Block(
          List(ValDef(Modifiers(), newTermName("self"), TypeTree(), reify{o.asInstanceOf[T]}.tree)),
          //reify{import Gakk._}.tree,
          tree
        )
      )
    }

    def evalLines(lineNum: Int, codeLines: List[String]): Unit =
      if (!codeLines.isEmpty) {
        val trimmedLine = codeLines.head.trim
        if (trimmedLine != "")
          evalLine(lineNum, codeLines.head.trim)

        evalLines(lineNum+1, codeLines.tail)
      }

    evalLines(0, code.split("\n").toList)
  }

  def apply[T: TypeTag](o: T) = {
    val annotatedTests = getAnnotatedTests[T](o)

    annotatedTests.foreach(annotatedTest => {
      evalCode(o, annotatedTest.methodName, annotatedTest.code)
    })
  }
}
