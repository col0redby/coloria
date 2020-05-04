package com.colored.coloria.http_api.routes

import org.http4s.{HttpRoutes, MediaType}
import cats.effect.IO
import com.colored.coloria.api.repository.ProcessingResultsRepository
import org.http4s.dsl.io._
import org.http4s.multipart.Multipart
import cats.implicits._
import org.http4s.headers.`Content-Type`

class ProcessingResultsRoutes(processingResultsRepository: ProcessingResultsRepository) {

  import io.circe.generic.auto._
  import org.http4s.circe.CirceEntityCodec._

  val routes = HttpRoutes.of[IO] {

    case GET -> Root / "api" / "v1" / "test" / "results" => Ok(processingResultsRepository.getProcessingResults())

  }
}
