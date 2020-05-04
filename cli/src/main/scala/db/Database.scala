package com.colored.coloria.cli.db

import cats.effect.{Blocker, ContextShift, IO, Resource}
import com.colored.coloria.cli.config.DatabaseConfig
import com.colored.coloria.cli.config.DatabaseConfig
import doobie.hikari.HikariTransactor

import scala.concurrent.ExecutionContext

object Database {
  def transactor(
      config: DatabaseConfig,
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
