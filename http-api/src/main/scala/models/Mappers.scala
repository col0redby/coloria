package com.colored.coloria.http_api.models

import com.colored.coloria.http_api.models.dao.{ImageDao, VersionDao}

object Mappers {
  def mapVersionDaoToVersion(vd: VersionDao): Version =
    Version(
      vd.id,
      vd.imageId,
      vd.version,
      vd.processingTimeMillis,
      List.empty[Color]
    )

  def mapImageDaoToImage(id: ImageDao, versions: List[Version]): Image =
    Image(id.id, id.title, id.description, id.original, versions)

  def mapVersionDaoToVersion(vd: VersionDao, colors: List[Color]): Version =
    Version(vd.id, vd.imageId, vd.version, vd.processingTimeMillis, colors.sortBy(-_.pct))
}
