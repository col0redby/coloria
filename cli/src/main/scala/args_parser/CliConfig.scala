package com.colored.coloria.cli.args_parser

import java.io.File

import com.colored.coloria.cli.config.CliConfig
import scopt.OParser



object ArgsParser {

  def parse(args: List[String]): Option[CliConfig] = {
    val builder = OParser.builder[CliConfig]

    val parser = {
      import builder._
      OParser.sequence(
        programName("coloria"),
        head("Coloria CLI", "0.0.1"),
        opt[File]('f', "folder")
          .action((x, c) => c.copy(folder = x))
          .validate({ x: File =>
            if (x.exists() && x.isDirectory) success
            else failure("Specified path should be existing folder.")
          })
          .text("Folder which contains test images"),

        opt[String]('v', "version")
          .action((x, c) => c.copy(version = x))
          .required()
          .text("Version of run, this is just a string to differ runs."),

        opt[Int]('c', "clusters-count")
          .action((x, c) => c.copy(clustersCount = x))
          .text("The number of clusters to split into, see k-means algo."),

        opt[Int]('a', "attempts")
          .action((x, c) => c.copy(attempts = x))
          .text("The number of attempts in the k-means algo."),

        opt[Int]('m', "max-count")
          .action((x, c) => c.copy(maxCount = x))
          .text("The max number of iterations in the k-means algo."),

        opt[Double]('e', "epsilon")
          .action((x, c) => c.copy(epsilon = x))
          .text("The minimum required accuracy.")
      )
    }

    OParser.parse(parser, args, CliConfig())
  }
}
