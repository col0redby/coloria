package com.colored.coloria.api.repository

import cats.effect.IO
import com.colored.coloria.http_api.models.{Color, Image}
import com.colored.coloria.http_api.models.dao.{ImageDao, VersionDao}
import doobie.free.connection.ConnectionIO
import doobie.util.transactor.Transactor
import doobie.implicits._
import cats.implicits._
import com.colored.coloria.http_api.models.Mappers._

class ProcessingResultsRepository(transactor: Transactor[IO]) {

  def getProcessingResults(): IO[List[Image]] = {
    selectImages()
      .flatMap(
        _.traverse(imageDao =>
          getVersionsByImageId(imageDao.id)
            .flatMap(
              _.traverse(versionDao =>
                getColorsByVersionId(versionDao.id).map(colors =>
                  mapVersionDaoToVersion(versionDao, colors)
                )
              ).map(mapImageDaoToImage(imageDao, _))
            )
        )
      )
      .transact(transactor)
  }

  def selectImages(): ConnectionIO[List[ImageDao]] = {
    sql"SELECT id, title, description, original FROM images"
      .query[ImageDao]
      .to[List]
  }

  def getVersionsByImageId(
      imageId: Int
  ): ConnectionIO[List[VersionDao]] = {
    sql"SELECT id, image_id, version, processing_time_millis FROM image_versions where image_id = $imageId"
      .query[VersionDao]
      .to[List]
  }

  def getColorsByVersionId(versionId: Int): ConnectionIO[List[Color]] = {
    sql"SELECT id, image_version_id, color, pct FROM version_colors where image_version_id = $versionId"
      .query[Color]
      .to[List]
  }
}
