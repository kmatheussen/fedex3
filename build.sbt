name := """assert_input_output"""

version := "1.0"

scalaVersion := "2.10.4"

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.6" % "test"

// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.3"

scalacOptions := Seq("-feature", "-deprecation")


resolvers += bintray.Opts.resolver.repo("bmc", "maven")

seq(bintrayResolverSettings:_*)

//libraryDependencies += "org.clapper" % "classutil" % "1.0.4"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value



