libraryDependencies += "com.samskivert" % "sbt-pom-util" % "0.6-SNAPSHOT"

// this is needed to wire up LWJGL when running the java version
addSbtPlugin("com.github.philcali" % "sbt-lwjgl-plugin" % "3.1.4")

// this wires up JRebel; start game with JRebel via: java/re-start
addSbtPlugin("io.spray" % "sbt-revolver" % "0.6.2")
