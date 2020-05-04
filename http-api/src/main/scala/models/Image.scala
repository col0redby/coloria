package com.colored.coloria.http_api.models

final case class Image(
    id: Int,
    title: String,
    description: String,
    original: String,
    versions: List[Version]
)
