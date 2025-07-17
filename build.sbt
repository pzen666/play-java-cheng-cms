name := """play-java-cheng-cms"""
organization := "com.cms"
enablePlugins(JavaAppPackaging)
version := "1.0-SNAPSHOT"

// 禁用 Scala 编译器的 -Werror
Compile / scalacOptions --= Seq("-Werror")
Test / scalacOptions --= Seq("-Werror")

// 禁用 Java 编译器的 -Werror
Compile / javacOptions --= Seq("-Werror")
Test / javacOptions --= Seq("-Werror")

lazy val root = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean)
  .enablePlugins(PlayNettyServer)
  .disablePlugins(PlayPekkoHttpServer)
  .settings(
    name := "play-java-ebean-example",
    version := "1.0.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.16"),
    scalaVersion := crossScalaVersions.value.head,

    libraryDependencies ++= Seq(
      guice,
      jdbc,
      "org.postgresql" % "postgresql" % "42.7.7",
      "io.ebean" % "ebean" % "17.0.0-RC3",
      "org.awaitility" % "awaitility" % "4.2.1" % Test,
      "org.assertj" % "assertj-core" % "3.26.3" % Test,
      "org.mockito" % "mockito-core" % "5.18.0" % Test,
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.14.1",
      "org.projectlombok" % "lombok" % "1.18.38"
      //      "com.fasterxml.jackson.module" % "jackson-module-scala" % "2.14.3"
    ),

    playEbeanModels := Seq("models.*"),
    (Test / testOptions) += Tests.Argument(TestFrameworks.JUnit, "-a", "-v"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-Werror"),

    dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.14.1"

  )


