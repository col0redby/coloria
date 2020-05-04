package com.colored.coloria.http_api.models

final case class Version(
    id: Int,
    imageId: Int,
    version: String,
    processingTimeMillis: Int,
    colors: List[Color]
)
