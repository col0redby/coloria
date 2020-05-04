package com.colored.coloria.http_api

import cats.effect.IOApp
import cats.effect.{ExitCode, IO}

object App extends IOApp {
	def run(args: List[String]): IO[ExitCode] = {
		HttpServer.create()
	}
}