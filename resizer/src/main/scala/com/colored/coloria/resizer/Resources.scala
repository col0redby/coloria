package com.colored.coloria.resizer

import cats.effect.{
  Blocker,
  ConcurrentEffect,
  ContextShift,
  ExitCode,
  IO,
  Resource,
  Timer
}
import com.colored.coloria.resizer.config.Config
import com.colored.coloria.resizer.db.ColoredDatabase
import com.rabbitmq.client.{Channel, Connection, ConnectionFactory}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import software.amazon.awssdk.services.s3.S3AsyncClient

case class Resources(
    config: Config,
    transactor: HikariTransactor[IO],
    s3AsyncClient: S3AsyncClient,
    rabbitConnection: Connection,
    rabbitChannel: Channel
)

object Resources {

  def create(
      configFile: String = "application.conf"
  )(
      implicit contextShift: ContextShift[IO],
      concurrentEffect: ConcurrentEffect[IO],
      timer: Timer[IO]
  ): Resource[IO, Resources] = {
    for {
      config <- Config.load(configFile)
      ec <- ExecutionContexts.fixedThreadPool[IO](
        config.colored.database.threadPoolSize
      )
      blocker <- Blocker[IO]
      transactor <- ColoredDatabase.transactor(
        config.colored.database,
        ec,
        blocker
      )
      s3AsyncClient <- Resource.liftF(
        IO.pure(
          S3AsyncClient
            .builder()
            .build()
        )
      )
      rabbitConnection <- Resource.fromAutoCloseable(IO.delay {
        val factory = new ConnectionFactory
        factory.setHost(config.rabbitMQ.connection.host)
        factory.setPort(config.rabbitMQ.connection.port)
        factory.newConnection()
      })
      rabbitChannel <- Resource.fromAutoCloseable(IO.delay {
        val channel = rabbitConnection.createChannel()
        channel.basicQos(config.rabbitMQ.queue.basicQos)
        channel
          .queueDeclare(config.rabbitMQ.queue.name, false, false, false, null)

        channel
      })
    } yield Resources(
      config,
      transactor,
      s3AsyncClient,
      rabbitConnection,
      rabbitChannel
    )
  }
}
