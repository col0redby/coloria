package com.colored.coloria.resizer.models

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class ResizeRequest (
    imageId: Int,
    originalBucket: String,
    originalKey: String,
    filename: String,
    targetBucket: String,
    targetKeyPrefix: String
)

object ResizeRequest {
  implicit val requestDecoder: Decoder[ResizeRequest] = deriveDecoder[ResizeRequest]
}
