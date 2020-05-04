package com.colored.coloria.http_api.models.dao

final case class VersionDao(
    id: Int,
    imageId: Int,
    version: String,
    processingTimeMillis: Int
)
