package com.colored.coloria.metadata.models

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class MetadataRequest (
    imageId: Int,
    originalBucket: String,
    originalKey: String
)

object MetadataRequest {
  implicit val metadataRequestDecoder: Decoder[MetadataRequest] = deriveDecoder[MetadataRequest]
}
