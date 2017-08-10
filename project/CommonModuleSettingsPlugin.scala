import com.ferhtaydn.akkahttpsoapclient.Dependencies
import com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin
import sbt._
import sbt.Keys._
import scoverage.ScoverageKeys._
import wartremover._

object CommonModuleSettingsPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Setting[_]] =
    resolverSettings ++
      scalaSettings ++
      scalafmtSettings ++
      wartRemoverSettings ++
      testSettings ++
      coverageSettings ++
      addCommandAlias(
        "validate",
        ";reload plugins; sbt:scalafmt::test; scalafmt::test; reload return; " +
          "sbt:scalafmt::test; scalafmt::test; test:scalafmt::test; " +
          "scalastyle; test:scalastyle"
      )

  private lazy val resolverSettings = Seq(
    conflictManager := ConflictManager.strict
  )

  private lazy val scalaSettings = Seq(
    scalaVersion := "2.12.3",
    // See https://tpolecat.github.io/2017/04/25/scalac-flags.html for
    // explanations.
    scalacOptions ++=
      Seq(
        "-deprecation",
        "-encoding",
        "utf-8",
        "-explaintypes",
        "-feature",
        "-unchecked",
        "-Xfatal-warnings",
        "-Xlint:adapted-args",
        "-Xlint:by-name-right-associative",
        "-Xlint:constant",
        "-Xlint:delayedinit-select",
        "-Xlint:doc-detached",
        "-Xlint:inaccessible",
        "-Xlint:infer-any",
        "-Xlint:missing-interpolator",
        "-Xlint:nullary-override",
        "-Xlint:nullary-unit",
        "-Xlint:option-implicit",
        "-Xlint:package-object-classes",
        "-Xlint:poly-implicit-overload",
        "-Xlint:private-shadow",
        "-Xlint:stars-align",
        "-Xlint:type-parameter-shadow",
        "-Xlint:unsound-match",
        "-Yno-adapted-args",
        "-Ywarn-dead-code",
        "-Ywarn-extra-implicit",
        "-Ywarn-inaccessible",
        "-Ywarn-infer-any",
        "-Ywarn-nullary-override",
        "-Ywarn-nullary-unit",
        "-Ywarn-numeric-widen",
        "-Ywarn-unused:implicits",
        "-Ywarn-unused:imports",
        "-Ywarn-unused:locals",
        "-Ywarn-unused:params",
        "-Ywarn-unused:patvars",
        "-Ywarn-unused:privates",
        "-Ywarn-value-discard"
      ),
    dependencyOverrides ++= Set(
      "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      "org.scala-lang" % "scala-library" % scalaVersion.value,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scala-lang" % "scalap" % scalaVersion.value
    )
  )

  private lazy val scalafmtSettings = Seq(
    ScalafmtCorePlugin.autoImport.scalafmtVersion :=
      Dependencies.scalafmtVersion
  )

  private lazy val wartRemoverSettings = {
    val warts = Warts.unsafe ++
      Seq(Wart.FinalCaseClass, Wart.ExplicitImplicitTypes)
    Seq(
      wartremoverErrors in (Compile, compile) := warts,
      wartremoverWarnings in (Test, compile) := warts,
      wartremoverWarnings in (IntegrationTest, compile) := warts,
      wartremoverExcluded in Compile ++= (managedSources in Compile).value
    )
  }

  private lazy val testSettings = Seq(
    testOptions in Test +=
      Tests.Argument(
        TestFrameworks.ScalaTest,
        "-oDF",
        "-W",
        "120",
        "60",
        "-y",
        "org.scalatest.FreeSpec"
      )
  )

  private lazy val coverageSettings = Seq(
    coverageMinimum := 80,
    coverageFailOnMinimum := true,
    coverageExcludedFiles := ".*/target/.*"
  )

}
