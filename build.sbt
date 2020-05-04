import Dependencies._

lazy val commonSettings = Seq(
  organization := "com.colored",
  organizationName := "colored",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.13.1"
)

val catsVersion = "2.1.0"
val circeVersion = "0.13.0"
val doobieVersion = "0.8.8"
val pureConfigVersion = "0.12.3"
val http4sVersion = "0.21.1"
val scoptVersion = "4.0.0-RC2"

lazy val core = (project in file("core"))
  .enablePlugins(UniversalPlugin)
  .settings(
    commonSettings,
    name := "coloria-core",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.1.0",
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-h2" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,
      "mysql" % "mysql-connector-java" % "8.0.19",
      "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % pureConfigVersion,
      "com.drewnoakes" % "metadata-extractor" % "2.13.0",
      "software.amazon.awssdk" % "s3" % "2.12.0",
      scalaTest % Test
    )
  )

lazy val cli = (project in file("cli"))
  .dependsOn(core)
  .enablePlugins(UniversalPlugin)
  .settings(
    commonSettings,
    name := "coloria-cli",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.1.0",
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-h2" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,
      "mysql" % "mysql-connector-java" % "8.0.19",
      "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % pureConfigVersion,
      "com.github.scopt" %% "scopt" % scoptVersion,
      "io.chrisdavenport" %% "log4cats-slf4j" % "1.0.1",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      scalaTest % Test
    ),
    javaOptions ++= Seq(
      "-Djava.library.path=/home/nikitakharitonov/Software/opencv-4.3.0/build/lib"
    ),
    fork in run := true
  )

lazy val http_api = (project in file("http-api"))
  .dependsOn(core)
  .settings(
    commonSettings,
    name := "coloria-http-api",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.1.0",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-generic-extras" % circeVersion,
      "io.circe" %% "circe-optics" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-h2" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,
      "mysql" % "mysql-connector-java" % "8.0.19",
      "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % pureConfigVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "io.chrisdavenport" %% "log4cats-slf4j" % "1.0.1",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      scalaTest % Test
    )
  )

lazy val resizer = (project in file("resizer"))
  .settings(
    commonSettings,
    name := "coloria-resizer",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.1.0",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-generic-extras" % circeVersion,
      "io.circe" %% "circe-optics" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-h2" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,
      "mysql" % "mysql-connector-java" % "8.0.19",
      "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % pureConfigVersion,
      "io.chrisdavenport" %% "log4cats-slf4j" % "1.0.1",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.rabbitmq" % "amqp-client" % "5.9.0",
      "software.amazon.awssdk" % "s3" % "2.12.0",
      scalaTest % Test
    )
  )

lazy val metadata = (project in file("metadata"))
  .dependsOn(core)
  .settings(
    commonSettings,
    name := "coloria-metadata",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.1.0",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-generic-extras" % circeVersion,
      "io.circe" %% "circe-optics" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-h2" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,
      "mysql" % "mysql-connector-java" % "8.0.19",
      "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % pureConfigVersion,
      "io.chrisdavenport" %% "log4cats-slf4j" % "1.0.1",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.rabbitmq" % "amqp-client" % "5.9.0",
      "software.amazon.awssdk" % "s3" % "2.12.0",
      "com.github.fs2-blobstore" %% "core" % "0.7.0",
      "com.github.fs2-blobstore" %% "s3" % "0.7.0",
      scalaTest % Test
    )
  )

lazy val colors = (project in file("colors"))
  .dependsOn(core)
  .settings(
    commonSettings,
    name := "coloria-colors",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.1.0",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-generic-extras" % circeVersion,
      "io.circe" %% "circe-optics" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-h2" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,
      "mysql" % "mysql-connector-java" % "8.0.19",
      "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % pureConfigVersion,
      "io.chrisdavenport" %% "log4cats-slf4j" % "1.0.1",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.rabbitmq" % "amqp-client" % "5.9.0",
      "software.amazon.awssdk" % "s3" % "2.12.0",
      "com.github.fs2-blobstore" %% "core" % "0.7.0",
      "com.github.fs2-blobstore" %% "s3" % "0.7.0",
      scalaTest % Test
    ),
    javaOptions ++= Seq(
      "-Djava.library.path=/home/nikitakharitonov/Software/opencv-4.3.0/build/lib"
    ),
    fork in run := true
  )
