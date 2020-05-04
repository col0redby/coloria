package com.colored.coloria.http_api

import cats.effect._
import com.colored.coloria.api.repository.ProcessingResultsRepository
import com.colored.coloria.http_api.db.Database
import com.colored.coloria.http_api.config.Config
import com.colored.coloria.http_api.routes.ProcessingResultsRoutes
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._

object HttpServer {
  def create(configFile: String = "application.conf")(
      implicit contextShift: ContextShift[IO],
      concurrentEffect: ConcurrentEffect[IO],
      timer: Timer[IO]
  ): IO[ExitCode] = {
    resources(configFile).use(create)
  }

  private def resources(
      configFile: String
  )(implicit contextShift: ContextShift[IO]): Resource[IO, Resources] = {
    for {
      config <- Config.load(configFile)
      ec <- ExecutionContexts.fixedThreadPool[IO](
        config.database.threadPoolSize
      )
      blocker <- Blocker[IO]
      transactor <- Database.transactor(config.database, ec, blocker)
    } yield Resources(transactor, config)
  }

  private def create(resources: Resources)(
      implicit concurrentEffect: ConcurrentEffect[IO],
      timer: Timer[IO]
  ): IO[ExitCode] = {
     val repository = new ProcessingResultsRepository(resources.transactor)
    BlazeServerBuilder[IO]
      .bindHttp(resources.config.server.port, resources.config.server.host)
      .withHttpApp(new ProcessingResultsRoutes(repository).routes.orNotFound)
      .serve
      .compile
      .lastOrError
  }

  case class Resources(transactor: HikariTransactor[IO], config: Config)
}
