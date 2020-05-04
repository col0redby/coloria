package com.colored.coloria.colors.repository

import cats.effect.IO
import com.colored.coloria.colors.models.ColorsProcessingResult
import doobie.util.transactor.Transactor
import doobie.implicits._
import cats.implicits._

class ColorsRepository(transactor: Transactor[IO]) {

  def updateImageWithColors(
      colorsProcessingResult: ColorsProcessingResult
  ): IO[Either[Throwable, Int]] = {
    val imageId = colorsProcessingResult.imageId
    val colors = colorsProcessingResult.processingResult.colors
    (for {
      deletePrevious <- sql"DELETE FROM image_colors WHERE image_id = $imageId".update.run
      insertNew <- colors
        .traverse(c =>
          sql"INSERT INTO image_colors(image_id, pct, color) VALUES ($imageId, ${c.pct}, ${c.color})".update.run
        ).map(_.sum)
    } yield insertNew)
      .transact(transactor)
      .attempt

  }

}
