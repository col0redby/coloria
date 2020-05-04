package com.colored.coloria.cli

import args_parser.ArgsParser
import cats.effect.{ExitCode, IO, IOApp}
import org.opencv.core.Core

/*
Cli args:
  1. -Djava.library.path=/home/nikitakharitonov/Software/opencv-4.3.0/build/lib
  2. -f /home/nikitakharitonov/Projects/colored/coloria-fe/src/assets/images2 -v 1.0.0 -c 4 -a 1 -m 10 -e 0.1
 */

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    // OpenCv requirement
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

    ArgsParser.parse(args) match {
      case Some(config) => ColoriaCli.create(config)
      case None => IO.pure(ExitCode.Error)
    }
  }
}
