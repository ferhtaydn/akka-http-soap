import com.ferhtaydn.akkahttpsoapclient.Dependencies

unmanagedSources in Compile +=
  baseDirectory.value / "project" / "Dependencies.scala"

ivyLoggingLevel := UpdateLogging.Quiet
scalacOptions in Compile ++= Seq("-feature", "-deprecation")

scalafmtVersion := "1.1.0"

addSbtPlugin(Dependencies.SbtPlugins.buildinfo)
addSbtPlugin(Dependencies.SbtPlugins.dynver)
addSbtPlugin(Dependencies.SbtPlugins.scalafmt)
addSbtPlugin(Dependencies.SbtPlugins.scalastyle)
addSbtPlugin(Dependencies.SbtPlugins.scalaxb)
addSbtPlugin(Dependencies.SbtPlugins.scoverage)
addSbtPlugin(Dependencies.SbtPlugins.wartRemover)
