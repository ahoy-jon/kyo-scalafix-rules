lazy val V = _root_.scalafix.sbt.BuildInfo

lazy val scala3Version = "3.6.3"

val kyoSettings = Seq(
  libraryDependencies += "io.getkyo" %% "kyo-core" % "0.18.0",
  libraryDependencies += "io.getkyo" %% "kyo-direct" % "0.18.0",
  scalacOptions ++= Seq(
    "-Wvalue-discard",
    "-Wnonunit-statement",
    "-Wconf:msg=(unused.*value|discarded.*value|pure.*statement):error",
    "-language:strictEquality"
  )
)

inThisBuild(
  List(
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    organization := "com.github.ahoy-jon"
  )
)

lazy val `kyoRules` = (project in file("."))
  .aggregate(rules, input, output, tests)
  .settings(
    publish / skip := true
  )

lazy val rules = (project in file("rules"))
  .settings(
    name := "kyo-scalafix-rules",
    scalaVersion := V.scala213,
    libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion
  )


lazy val input = (project in file("input"))
  .settings(
    publish / skip := true,
    scalaVersion := scala3Version,
  )
  .settings(kyoSettings *)

lazy val output = (project in file("output"))
  .settings(
    publish / skip := true,
    scalaVersion := scala3Version,
  )
  .settings(kyoSettings *)


lazy val tests = (project in file("tests"))
  .settings(
    scalaVersion := V.scala213,
    publish / skip := true,
    scalafixTestkitOutputSourceDirectories :=
      (output / Compile / unmanagedSourceDirectories).value,
    scalafixTestkitInputSourceDirectories :=
      (input / Compile / unmanagedSourceDirectories).value,
    scalafixTestkitInputClasspath :=
      (input / Compile / fullClasspath).value,
    scalafixTestkitInputScalacOptions :=
      (input / Compile / scalacOptions).value,
    scalafixTestkitInputScalaVersion :=
      (input / Compile / scalaVersion).value
  )
  .dependsOn(rules)
  .enablePlugins(ScalafixTestkitPlugin)
