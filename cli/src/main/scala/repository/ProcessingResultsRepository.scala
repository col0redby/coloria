package com.colored.coloria.cli.repository

import cats.effect.IO
import doobie.util.transactor.Transactor
import doobie.implicits._
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import cats._
import cats.effect._
import cats.implicits._
import com.colored.coloria.core.models.ProcessingResult
import models.db.ProcessingResultSaveResult

class ProcessingResultsRepository(transactor: Transactor[IO]) {

  def saveProcessingResults(results: List[ProcessingResult], version: String): IO[List[ProcessingResultSaveResult]] = {
    results.map { result =>
      for {
        existingId <- sql"SELECT id FROM images WHERE original = ${result.imagePath}"
          .query[Int].option
        imageId <- existingId match {
          case Some(id) => id.pure[ConnectionIO]
          case None => sql"INSERT INTO images (original) VALUES (${result.imagePath})"
            .update.withUniqueGeneratedKeys[Int]("id")
        }
        insertedVersionId <- sql"INSERT INTO image_versions (image_id, version, processing_time_millis) VALUES ($imageId, $version, ${result.processingTimeMillis})"
          .update.withUniqueGeneratedKeys[Int]("id")
        insertedColorIds <- result.colors.map(
          color => sql"INSERT INTO version_colors (image_version_id, color, pct) VALUES ($insertedVersionId, ${color.color}, ${color.pct})"
            .update.run
        ).sequence
      } yield ProcessingResultSaveResult(imageId, insertedVersionId, insertedColorIds)
    }
      .sequence
      .transact(transactor)
  }

}
