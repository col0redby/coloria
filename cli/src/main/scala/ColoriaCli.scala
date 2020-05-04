package com.colored.coloria.cli

import cats.effect._
import com.colored.coloria.cli.config.{CliConfig, PureConfig}
import com.colored.coloria.cli.db.Database
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import cats.implicits._
import com.colored.coloria.cli.repository.ProcessingResultsRepository
import com.colored.coloria.core.Coloria

object ColoriaCli {
  def create(cliArgs: CliConfig, configFile: String = "application.conf")(
      implicit contextShift: ContextShift[IO],
      concurrentEffect: ConcurrentEffect[IO],
      timer: Timer[IO]
  ): IO[ExitCode] = {
    resources(cliArgs, configFile).use(create)
  }

  private def resources(
      cliArgs: CliConfig,
      configFile: String
  )(implicit contextShift: ContextShift[IO]): Resource[IO, Resources] = {
    for {
      config <- PureConfig.load(configFile)
      ec <- ExecutionContexts.fixedThreadPool[IO](
        config.database.threadPoolSize
      )
      blocker <- Blocker[IO]
      transactor <- Database.transactor(config.database, ec, blocker)
    } yield Resources(cliArgs, config, transactor)
  }

  private def create(resources: Resources): IO[ExitCode] = {
    val repo = new ProcessingResultsRepository(resources.transactor)
    val cliArgs = resources.cliArgs

    for {
      processingResults <- cliArgs.folder
        .listFiles()
        .toList
        .map(imagePath =>
          Coloria.process(
            imagePath.getAbsolutePath,
            cliArgs.clustersCount,
            cliArgs.attempts,
            cliArgs.maxCount,
            cliArgs.epsilon
          )
        )
        .sequence
      savingResults <- repo.saveProcessingResults(processingResults, cliArgs.version)
    } yield ExitCode.Success
  }

  case class Resources(
      cliArgs: CliConfig,
      config: PureConfig,
      transactor: HikariTransactor[IO]
  )
}
