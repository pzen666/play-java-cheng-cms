// The Play plugin
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.10")

// Defines scaffolding (found under .g8 folder)
// http://www.foundweekends.org/giter8/scaffolding.html
// sbt "g8Scaffold form"
addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "0.17.0")
addSbtPlugin("org.playframework" % "sbt-play-ebean" % "8.3.0")
// https://github.com/sbt/sbt-native-packager/releases
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.11.7")


//addSbtPlugin("com.github.sbt" % "sbt-proguard" % "0.6.0") // https://github.com/sbt/sbt-proguard/releases
//启用 sbt-assembly 插件 打成一个jar包
//addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.1.1")