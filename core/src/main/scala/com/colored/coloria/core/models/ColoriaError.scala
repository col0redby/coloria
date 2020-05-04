package com.colored.coloria.core.models

sealed abstract class ColoriaError extends Exception

object ColoriaError {
  final case class DatabaseError(msg: String) extends ColoriaError
  final case class MetadataExtractionError(msg: String) extends ColoriaError
  final case class ColorsProcessingError(msg: String) extends ColoriaError
}
