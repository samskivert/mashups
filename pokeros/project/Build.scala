import sbt._
import sbt.Keys._

object GameBuild extends samskivert.MavenBuild {

  override val globalSettings = Seq(
    crossPaths    := false,
    javacOptions  ++= Seq("-Xlint", "-Xlint:-serial", "-source", "1.6", "-target", "1.6"),
    javaOptions   ++= Seq("-ea"),
    fork in Compile := true,
    autoScalaLibrary in Compile := false, // no scala-library dependency
    libraryDependencies ++= Seq(
      "com.novocode" % "junit-interface" % "0.7" % "test->default" // make JUnit tests work
    )
  )

  override def moduleSettings (name :String, pom :pomutil.POM) = name match {
    case "java" => spray.revolver.RevolverPlugin.Revolver.settings
    case _ => Nil
  }

  override def profiles = Seq("java")
}
