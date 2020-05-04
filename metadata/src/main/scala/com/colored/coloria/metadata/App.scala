package com.colored.coloria.metadata

import cats.effect.{ExitCode, IO, IOApp}

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val resources = Resources.create()
    resources.use(Metadata.start)
  }
}
