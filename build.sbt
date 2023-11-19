import Dependencies.Version

ThisBuild / scalaVersion := "2.13.12"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .aggregate(api, core, models)
  .settings(
    name := """api-adresse"""
  )

lazy val api = (project in file("src/api"))
  .dependsOn(models, core, myFrameworkInfra, shared)
  .enablePlugins(PlayScala)
  .settings(
    name := """api""",
    libraryDependencies ++= List(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
    )
  )

lazy val core = (project in file("src/core"))
  .dependsOn(models, myFrameworkCore)
  .settings(
    name := """core"""
  )

lazy val models = (project in file("src/models"))
  .settings(
    name := """models"""
  )
/////////////////////////////////////////// partie impl partagée

lazy val shared = (project in file("src/shared"))
  .dependsOn(models, core, myFrameworkInfra)
  .settings(
    name := """shared""",
    libraryDependencies ++= List(
      "com.typesafe.play" %% "play-json" % "2.9.4",
      "com.sksamuel.elastic4s" %% "elastic4s-client-esjava" % "8.8.1",
      "com.sksamuel.elastic4s" %% "elastic4s-core" % "8.8.1"
    )
  )

/////////////////////////////////////////// partie framework
// MKDMKD todo en faire une lib à terme
lazy val myFrameworkInfra = (project in file("src/my-framework-infra"))
  .dependsOn(myFrameworkCore)
  .settings(
    name := """my-framework-infra""",
    libraryDependencies ++= List(
      "org.mongodb.scala" %% "mongo-scala-driver" % "4.9.0"
    )
  )

lazy val myFrameworkCore = (project in file("src/my-framework-core"))
  .settings(
    name := """my-framework-core""",
    libraryDependencies ++= List(
      "org.typelevel" %% "cats-core" % "2.10.0"
    )
  )

/////////////////////////////////////////// partie spark

lazy val dataApp = (project in file("src/data-app"))
  .dependsOn(models, core, shared)
  .settings(
    name := """data-app""",
    libraryDependencies ++= List(
      "org.apache.spark" %% "spark-core" % Version.spark,
      "org.apache.spark" %% "spark-sql" % Version.spark,
      "org.scala-lang" %% "toolkit" % "0.1.7"
    )
  )
