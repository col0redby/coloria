package com.colored.coloria.resizer.config

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

case class ResizerConfig(
    scriptPath: String,
    sizes: List[SizeConfig]
)

case class SizeConfig(
    title: String,
    size: Int
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

case class Config(colored: ColoredConfig, rabbitMQ: RabbitMQConfig, resizer: ResizerConfig)

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
