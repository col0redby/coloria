package com.colored.coloria.metadata

import java.nio.file.Paths

import blobstore.fs.FileStore
import blobstore.s3.S3Store
import cats.effect.{Blocker, ConcurrentEffect, ContextShift, ExitCode, IO, Resource, Timer}
import com.colored.coloria.core.aws.AwsSdk
import com.colored.coloria.metadata.config.Config
import com.colored.coloria.metadata.db.ColoredDatabase
import com.rabbitmq.client.{Channel, Connection, ConnectionFactory}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import software.amazon.awssdk.services.s3.S3AsyncClient

case class Resources(
    config: Config,
    transactor: HikariTransactor[IO],
    s3AsyncClient: S3AsyncClient,
    s3Store: S3Store[IO],
    fileStore: FileStore[IO],
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
      s3AsyncClient <- AwsSdk.s3AsyncClient(
        config.aws.credentialsProvider.config,
        config.aws.credentialsProvider.credentials,
        config.aws.credentialsProvider.profile,
        config.aws.credentialsProvider.region
      )
      s3Store <- Resource.liftF(S3Store[IO](s3AsyncClient))
      rabbitConnection <- Resource.fromAutoCloseable(IO.delay {
        val factory = new ConnectionFactory
        factory.setHost(config.rabbitMQ.connection.host)
        factory.setPort(config.rabbitMQ.connection.port)
        factory.newConnection()
      })
      fileStore <- Resource.liftF(IO(FileStore[IO](Paths.get("/"), blocker)))
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
      s3Store,
      fileStore,
      rabbitConnection,
      rabbitChannel
    )
  }
}
