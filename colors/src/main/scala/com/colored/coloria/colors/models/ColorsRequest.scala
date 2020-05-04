package com.colored.coloria.colors.models

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class ColorsRequest (
    imageId: Int,
    originalBucket: String,
    originalKey: String
)

object ColorsRequest {
  implicit val metadataRequestDecoder: Decoder[ColorsRequest] = deriveDecoder[ColorsRequest]
}
