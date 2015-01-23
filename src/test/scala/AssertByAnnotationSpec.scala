package no.samordnaopptak.TestByAnnotation

import org.scalatest._
import org.scalatest.Matchers


object Tester{

  import scala.reflect.runtime._
  import scala.tools.reflect.ToolBox

  val cm = universe.runtimeMirror(getClass.getClassLoader)
  val tb = cm.mkToolBox()

  case class AnnotatedTest(method: java.lang.reflect.Method, code: String, methodName: String)

  def getAnnotatedTests(o: Object): List[AnnotatedTest] = {
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

  def evalCode(o: Object, methodName: String, code: String) = {

    def evalLine(linenum: Int, line: String) = {
      val assertionCodeLine = createAssertionCodeLine(linenum, methodName, line.trim)
      //println("assertionCodeLine: "+assertionCodeLine)
      val tree = try{
        tb.parse(assertionCodeLine)
      } catch {
        case e: Throwable => throw new Exception(createExceptionString(linenum, methodName, s""""$line" failed while parsing "$assertionCodeLine": ${e.getMessage()}""""))
      }
      //println("reify: " +reify{Gakk}.tree)
      tb.eval(
        Block(
          List(ValDef(Modifiers(), newTermName("self"), TypeTree(), reify{o}.tree)),
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

  def testObject(o: Object) = {
    val annotatedTests = getAnnotatedTests(o)

    annotatedTests.foreach(annotatedTest => {
      evalCode(o, annotatedTest.methodName, annotatedTest.code)
    })
  }
}


class HelloSpec extends FlatSpec with Matchers {
  "Test by annotation the 'TestObject' object" in {

    Tester.testObject(TestObject)

    true === true
  }
}
