package com.colored.coloria.metadata.repository

import cats.effect.IO
import com.colored.coloria.metadata.models.MetadataExtractResult
import doobie.util.transactor.Transactor
import doobie.implicits._

class MetadataRepository(transactor: Transactor[IO]) {

  def updateImageWithMetadata(metadataExtractResult: MetadataExtractResult): IO[Either[Throwable, Int]] = {
    val m = metadataExtractResult.metadata

    sql"""
         |INSERT INTO image_metadata(image_id, exposure_time_description, exposure_time_inverse, iso_description, iso,
         |                           aperture_desctiprion, aperture, gps_latitude_description, gps_latitude,
         |                           gps_longitude_description, gps_longitude, gps_altitude_description, gps_altitude_meters)
         |VALUES (${metadataExtractResult.imageId}, ${m.exposureTimeDescription}, ${m.exposureTimeInverse}, ${m.isoDescription},
         |${m.iso}, ${m.apertureDescription}, ${m.aperture}, ${m.gpsLatitudeDescription}, ${m.gpsLatitude},
         |${m.gpsLongitudeDescription}, ${m.gpsLongitude}, ${m.gpsAltitudeDescription}, ${m.gpsAltitudeMeters})
         |""".stripMargin
      .update
      .run
      .transact(transactor)
      .attempt
  }

}
