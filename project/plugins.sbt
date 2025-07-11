// The Play plugin
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.8")

// Defines scaffolding (found under .g8 folder)
// http://www.foundweekends.org/giter8/scaffolding.html
// sbt "g8Scaffold form"
addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "0.17.0")
addSbtPlugin("org.playframework" % "sbt-play-ebean" % "8.3.0")
//addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "2.0.0-M4") // https://github.com/sbt/sbt-native-packager/releases
//addSbtPlugin("com.github.sbt" % "sbt-proguard" % "0.6.0") // https://github.com/sbt/sbt-proguard/releases
