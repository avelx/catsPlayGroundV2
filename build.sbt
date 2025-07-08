ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.6"

lazy val root = (project in file("."))
  .settings(
    name := "catsPlayGroundV2"
  )

libraryDependencies += "org.typelevel" %% "cats-core" % "2.9.0"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.19"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test"

libraryDependencies += "com.h2database" % "h2" % "2.3.232"