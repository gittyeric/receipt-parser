

lazy val GatlingTest = config("gatling") extend Test

scalaVersion := "2.11.11"

resolvers += "Tess4J" at "https://mvnrepository.com/artifact/net.sourceforge.tess4j/tess4j"
resolvers += "JAI" at "https://mvnrepository.com/artifact/com.github.jai-imageio/jai-imageio-core"

libraryDependencies += "com.netaporter" %% "scala-uri" % "0.4.14"
libraryDependencies += "net.codingwell" %% "scala-guice" % "4.1.0"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.2" % Test
libraryDependencies += "io.gatling" % "gatling-test-framework" % "2.2.2" % Test
libraryDependencies += "com.github.jai-imageio" % "jai-imageio-core" % "1.3.1"
libraryDependencies += "net.sourceforge.tess4j" % "tess4j" % "3.3.1"
libraryDependencies += "com.novocode" % "junit-interface" % "0.8" % "test->default"
libraryDependencies += guice
//libraryDependencies += "com.google.inject" % "guice" % "4.0"

// The Play project itself
lazy val root = (project in file("."))
  .enablePlugins(Common, PlayScala, GatlingPlugin)
  .configs(GatlingTest)
  .settings(inConfig(GatlingTest)(Defaults.testSettings): _*)
  .settings(
    name := """Receipts""",
    scalaSource in GatlingTest := baseDirectory.value / "/gatling/simulation"
  )

// Documentation for this project:
//    sbt "project docs" "~ paradox"
//    open docs/target/paradox/site/index.html
lazy val docs = (project in file("docs")).enablePlugins(ParadoxPlugin).
  settings(
    paradoxProperties += ("download_url" -> "https://example.lightbend.com/v1/download/play-rest-api")
  )

routesGenerator := InjectedRoutesGenerator