import com.ferhtaydn.akkahttpsoapclient.Dependencies._

lazy val exampleWsdl =
  project
    .in(file("example-wsdl"))
    .enablePlugins(ScalaxbPlugin)
    .settings(
      name := "example-wsdl"
    )
    .settings(
      libraryDependencies ++= Seq(
        Scalaxb.dispatch,
        Scalaxb.scalaParserCombinators,
        Scalaxb.scalaXML
      ),
      scalaxbPackageName in (Compile, scalaxb) :=
        "com.examples",
      scalaxbDispatchVersion in (Compile, scalaxb) := Scalaxb.dispatchV,
      // to eliminate errors from generated sources
      scalacOptions --= Seq(
        "-Ywarn-unused:implicits",
        "-Ywarn-unused:imports",
        "-Ywarn-unused:locals",
        "-Ywarn-unused:params",
        "-Ywarn-unused:patvars",
        "-Ywarn-unused:privates"
      )
    )

lazy val akkaHttpSoapClient =
  project
    .in(file("akka-http-soap-client"))
    .enablePlugins(ScalaxbPlugin)
    .settings(name := "akka-http-soap-client")
    .settings(
      dependencyOverrides ++= Set(
        Akka.actor,
        Akka.stream,
        Akka.http,
        Akka.streamTestkit,
        Akka.slf4j,
        logbackClassic,
        slf4j,
        Akka.Http.xml,
        Scalaxb.scalaParserCombinators,
        Scalaxb.scalaXML
      ),
      libraryDependencies ++= Seq(
        Scalaxb.scalaxb,
        Akka.http,
        Akka.slf4j,
        logbackClassic,
        Akka.Http.xml,
        scalatest % Test,
        Akka.testkit % Test,
        Akka.Http.testkit % Test
      )
    )
    .dependsOn(exampleWsdl)
