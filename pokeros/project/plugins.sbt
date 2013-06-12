libraryDependencies += "com.samskivert" % "sbt-pom-util" % "0.5"

// this wires up JRebel; start game with JRebel via: java/re-start
addSbtPlugin("io.spray" % "sbt-revolver" % "0.6.2")
