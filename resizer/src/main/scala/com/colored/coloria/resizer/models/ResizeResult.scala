package com.colored.coloria.resizer.models

case class ResizeResult (
    imageId: Int,
    sizes: List[Size]
)

case class Size(
    title: String,
    url: String
)
