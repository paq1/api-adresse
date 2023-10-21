ThisBuild / scalaVersion := "2.13.12"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .aggregate(api, core, models)
  .settings(
    name := """api-adresse"""
  )

lazy val api = (project in file("src/api"))
  .dependsOn(models, core)
  .enablePlugins(PlayScala)
  .settings(
    name := """api""",
    libraryDependencies ++= Seq(
      guice,
      "org.mongodb.scala" %% "mongo-scala-driver" % "4.8.1",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
    )
  )

lazy val core = (project in file("src/core"))
  .dependsOn(models)
  .settings(
    name := """core"""
  )

lazy val models = (project in file("src/models"))
  .settings(
    name := """models"""
  )
