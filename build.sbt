name := "play-java-cheng-cms"
organization := "com.cms"
version := "1.0-SNAPSHOT"
scalaVersion := "2.13.16"

// 启用 Play Java 插件
lazy val root = (project in file(".")).enablePlugins(PlayJava)

// 依赖项
libraryDependencies ++= Seq(
  // Play Framework 核心依赖
  "org.playframework" %% "play-core" % "3.1.0",
  "org.playframework" %% "play-server" % "3.1.0",
  "org.playframework" %% "play-akka-http-server" % "3.1.0",
  "org.playframework" %% "play-guice" % "3.1.0",
  "org.playframework" %% "play-json" % "3.1.0",
  "org.playframework" %% "play-logback" % "3.1.0",

  // PostgreSQL JDBC 驱动
  "org.postgresql" % "postgresql" % "42.7.7",

  // Ebean ORM
  "io.ebean" % "ebean" % "17.0.0-RC3",

  // 数据库连接池
  "com.zaxxer" % "HikariCP" % "5.1.0",

  // 测试依赖
  "org.awaitility" % "awaitility" % "4.2.1" % Test,
  "org.assertj" % "assertj-core" % "3.26.3" % Test,
  "org.mockito" % "mockito-core" % "5.18.0" % Test,

  // JSON 处理
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.17.0",

  // Lombok（可选，用于简化 Java 代码）
  "org.projectlombok" % "lombok" % "1.18.38" % Compile,

  // JWT 支持
  "com.auth0" % "java-jwt" % "4.5.0"
)

// 禁用 Java 编译警告为错误
Compile / javacOptions --= Seq("-Werror")
Test / javacOptions --= Seq("-Werror")

// Maven 仓库（确保能找到 Play 3.1.0）
resolvers += "Maven Central" at "https://repo1.maven.org/maven2 "

// 启动入口（可选，默认已由 Play 插件处理）
 Compile / run := { (Compile / runMain).evaluated }