lazy val V = _root_.scalafix.sbt.BuildInfo

lazy val scala3Version = "3.6.3"


val kyoDeps = Seq(
  libraryDependencies += "io.getkyo" %% "kyo-core" % "0.18.0",
  libraryDependencies += "io.getkyo" %% "kyo-direct" % "0.18.0"
)

inThisBuild(
  List(
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision
  )
)

lazy val `kyo` = (project in file("."))
  .aggregate(rules, input, output, tests)
  .settings(
    publish / skip := true
  )

lazy val rules = (project in file("rules"))
  .settings(
    scalaVersion := V.scala213,
    moduleName := "scalafix",
    libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion
  )


lazy val input = (project in file("input"))
  .settings(
    publish / skip := true,
    scalaVersion := scala3Version,
  )
  .settings(kyoDeps *)

lazy val output = (project in file("output"))
  .settings(
    publish / skip := true,
    scalaVersion := scala3Version,
  )
  .settings(kyoDeps *)


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
