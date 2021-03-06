package com.colored.coloria.resizer.db

import cats.effect.{Blocker, ContextShift, IO, Resource}
import com.colored.coloria.resizer.config.ColoredDatabaseConfig
import doobie.hikari.HikariTransactor

import scala.concurrent.ExecutionContext

object ColoredDatabase {
  def transactor(
    config: ColoredDatabaseConfig,
    executionContext: ExecutionContext,
    blocker: Blocker
  )(
      implicit contextShift: ContextShift[IO]
  ): Resource[IO, HikariTransactor[IO]] = {
    HikariTransactor.newHikariTransactor[IO](
      config.driver,
      config.url,
      config.user,
      config.password,
      executionContext,
      blocker
    )
  }
}
