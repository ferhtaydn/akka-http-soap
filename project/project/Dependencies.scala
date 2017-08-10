package com.ferhtaydn.akkahttpsoapclient

import sbt._

object Dependencies {

  val scalafmtVersion: String = "1.1.0"

  val logbackClassic: ModuleID = "ch.qos.logback" % "logback-classic" % "1.2.3"
  val playJson: ModuleID = "com.typesafe.play" %% "play-json" % "2.6.2"
  val scalatest: ModuleID = "org.scalatest" %% "scalatest" % "3.0.3"
  val slf4j: ModuleID = "org.slf4j" % "slf4j-api" % "1.7.25"
  val typesafeConfig: ModuleID = "com.typesafe" % "config" % "1.3.1"

  object Scalaxb {

    val dispatchV: String = "0.12.0"

    val dispatch: ModuleID =
      "net.databinder.dispatch" %% "dispatch-core" % dispatchV
    val scalaParserCombinators: ModuleID =
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.6"
    val scalaxb: ModuleID = "org.scalaxb" %% "scalaxb" % "1.5.2"
    val scalaXML: ModuleID = "org.scala-lang.modules" %% "scala-xml" % "1.0.6"

  }

  object Akka {

    val version = "2.5.3"

    val actor: ModuleID = "com.typesafe.akka" %% "akka-actor" % version
    val http: ModuleID = "com.typesafe.akka" %% "akka-http" % Http.version
    val slf4j: ModuleID = "com.typesafe.akka" %% "akka-slf4j" % version
    val stream: ModuleID = "com.typesafe.akka" %% "akka-stream" % version

    val streamTestkit: ModuleID =
      "com.typesafe.akka" %% "akka-stream-testkit" % version
    val testkit: ModuleID = "com.typesafe.akka" %% "akka-testkit" % version

    object Http {

      val version = "10.0.9"

      val playJson: ModuleID =
        "de.heikoseeberger" %% "akka-http-play-json" % "1.17.0"
      val testkit: ModuleID =
        "com.typesafe.akka" %% "akka-http-testkit" % version
      val xml: ModuleID =
        "com.typesafe.akka" %% "akka-http-xml" % version

    }
  }

  object SbtPlugins {

    val buildinfo: ModuleID = "com.eed3si9n" % "sbt-buildinfo" % "0.7.0"
    val dynver: ModuleID = "com.dwijnand" % "sbt-dynver" % "2.0.0"
    val scalafmt: ModuleID = "com.lucidchart" % "sbt-scalafmt" % "1.10"

    val scalastyle: ModuleID =
      "org.scalastyle" %% "scalastyle-sbt-plugin" % "0.9.0"

    val scalaxb: ModuleID = "org.scalaxb" % "sbt-scalaxb" % "1.5.2"

    val scoverage: ModuleID = "org.scoverage" % "sbt-scoverage" % "1.5.0"
    val wartRemover: ModuleID = "org.wartremover" % "sbt-wartremover" % "2.1.1"

  }
}
