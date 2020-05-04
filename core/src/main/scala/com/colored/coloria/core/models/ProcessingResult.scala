package com.colored.coloria.core.models

final case class ProcessingResult(
    imagePath: String,
    processingTimeMillis: Int,
    colors: List[ColorEntry]
)

final case class ColorEntry(
    color: String,
    pct: Float
)
