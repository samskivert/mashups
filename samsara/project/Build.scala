import sbt._
import sbt.Keys._

object GameBuild extends samskivert.MavenBuild {

  override val globalSettings = Seq(
    crossPaths      := false,
    scalacOptions   ++= Seq("-unchecked", "-deprecation", "-feature",
                            "-language:implicitConversions"),
    fork in Compile := true,
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
