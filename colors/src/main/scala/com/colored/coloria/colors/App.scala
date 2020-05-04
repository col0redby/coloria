package com.colored.coloria.colors

import cats.effect.{ExitCode, IO, IOApp}
import org.opencv.core.Core

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    // OpenCv requirement
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

    val resources = Resources.create()
    resources.use(Colors.start)
  }
}
