package com.colored.coloria.metadata.config

import cats.effect.{Blocker, ContextShift, IO, Resource}
import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.generic.ProductHint
import pureconfig.module.catseffect.syntax._
import pureconfig.generic.auto._

case class ColoredConfig(
    database: ColoredDatabaseConfig
)

case class ColoredDatabaseConfig(
    driver: String,
    url: String,
    user: String,
    password: String,
    threadPoolSize: Int
)

case class RabbitMQConfig(
    connection: RabbitMQConnection,
    queue: Queue
)

case class RabbitMQConnection(
    host: String,
    port: Int
)

case class Queue(
    name: String,
    basicQos: Int,
    autoAck: Boolean
)

final case class Aws(
    credentialsProvider: CredentialsProvider
)

final case class CredentialsProvider(
    profile: String,
    region: String,
    config: String,
    credentials: String
)

final case class Metadata(
    tempImageFolder: String
)

case class Config(
    colored: ColoredConfig,
    rabbitMQ: RabbitMQConfig,
    aws: Aws,
    metadata: Metadata
)

object Config {

  implicit def hint[T]: ProductHint[T] =
    ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  def load(
      configFile: String = "application.conf"
  )(implicit cs: ContextShift[IO]): Resource[IO, Config] = {
    Blocker[IO].flatMap { blocker =>
      Resource.liftF(
        ConfigSource
          .fromConfig(ConfigFactory.load(configFile))
          .loadF[IO, Config](blocker)
      )
    }
  }
}
