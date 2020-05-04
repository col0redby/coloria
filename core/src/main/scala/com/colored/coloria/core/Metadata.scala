package com.colored.coloria.core

import java.io.File

import cats.effect.IO
import com.colored.coloria.core.models.ColoriaError.MetadataExtractionError
import com.colored.coloria.core.models.{ColoriaError, ImageMetadata}
import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.{ExifSubIFDDescriptor, ExifSubIFDDirectory, GpsDescriptor, GpsDirectory}

import scala.util.{Failure, Success, Try}

object Metadata {

  def metadataFromLocalImage(
      path: String
  ): IO[Either[ColoriaError, ImageMetadata]] = {
    IO {
      Try(ImageMetadataReader.readMetadata(new File(path))) match {
        case Failure(error) => Left(MetadataExtractionError(error.getMessage))
        case Success(value) => Right(value)
      }
    }.map(_.map { metadata =>
      val exifDirectory =
        Option(metadata.getFirstDirectoryOfType(new ExifSubIFDDirectory().getClass))
      val exifDescriptor = exifDirectory.map(ed => new ExifSubIFDDescriptor(ed))
      val gpsDirectory =
        Option(metadata.getFirstDirectoryOfType(new GpsDirectory().getClass))
      val gpsDescriptor = gpsDirectory.map(gd => new GpsDescriptor(gd))

      ImageMetadata(
        exifDescriptor.flatMap(exif => Option(exif.getExposureTimeDescription)),
        None,
        exifDescriptor.flatMap(exif => Option(exif.getIsoEquivalentDescription)),
        None,
        exifDescriptor.flatMap(exif => Option(exif.getApertureValueDescription)),
        None,
        gpsDescriptor.flatMap(gps => Option(gps.getGpsLatitudeDescription)),
        gpsDirectory.flatMap(gd => Option(gd.getGeoLocation).map(_.getLatitude)),
        gpsDescriptor.flatMap(gps => Option(gps.getGpsLongitudeDescription)),
        gpsDirectory.flatMap(gd => Option(gd.getGeoLocation).map(_.getLongitude)),
        gpsDescriptor.flatMap(gps => Option(gps.getGpsAltitudeDescription)),
        None
      )
    })
  }
}
