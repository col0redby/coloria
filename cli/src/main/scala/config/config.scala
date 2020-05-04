package com.colored.coloria.cli.config

import java.io.File

import cats.effect.{Blocker, ContextShift, IO, Resource}
import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.module.catseffect.syntax._
import pureconfig.generic.auto._

case class CliConfig(
  folder: File = new File("."),
  version: String = "0.0.1",
  clustersCount: Int = 4,
  attempts: Int = 1,
  maxCount: Int = 10,
  epsilon: Double = 1.0
)

case class DatabaseConfig(
    driver: String,
    url: String,
    user: String,
    password: String,
    threadPoolSize: Int
)

case class PureConfig(database: DatabaseConfig)

object PureConfig {
  def load(
      configFile: String = "application.conf"
  )(implicit cs: ContextShift[IO]): Resource[IO, PureConfig] = {
    Blocker[IO].flatMap { blocker =>
      Resource.liftF(
        ConfigSource
          .fromConfig(ConfigFactory.load(configFile))
          .loadF[IO, PureConfig](blocker)
      )
    }
  }
}
