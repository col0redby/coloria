package com.colored.coloria.core

import cats.effect.IO
import com.colored.coloria.core.models.{ColorEntry, ProcessingResult}
import org.opencv.core.{Core, CvType, Mat, TermCriteria}
import org.opencv.imgcodecs.Imgcodecs

object Coloria {

  def process(
      imagePath: String,
      clustersCount: Int,
      attempts: Int,
      maxCount: Int,
      epsilon: Double
  ): IO[ProcessingResult] = IO {

    val startTime = System.currentTimeMillis()

    val img = Imgcodecs.imread(imagePath)
    val samples = img.reshape(1, img.cols * img.rows)
    val samples32f = new Mat
    samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0)
    val criteria =
      new TermCriteria(
        TermCriteria.EPS + TermCriteria.MAX_ITER,
        maxCount,
        epsilon
      )
    val labels = new Mat
    val centers = new Mat
    Core.kmeans(
      samples32f,
      clustersCount,
      labels,
      criteria,
      attempts,
      Core.KMEANS_PP_CENTERS,
      centers
    )

    centers.convertTo(centers, CvType.CV_8UC1, 255.0)
    centers.reshape(3)

    val counts = scala.collection.mutable.Map.empty[Int, Int]
    for (i <- 0 until img.rows * img.cols) {
      val label = labels.get(i, 0)(0).toInt
      counts(label) = counts.getOrElse(label, 0) + 1
    }

    val endTime = System.currentTimeMillis()

    val processingTimeMillis = (endTime - startTime).toInt
    val total = counts.values.sum
    val colors = counts.map {
      case (k, v) => {
        val r = formatChannel(centers.get(k, 2)(0).toInt)
        val g = formatChannel(centers.get(k, 1)(0).toInt)
        val b = formatChannel(centers.get(k, 0)(0).toInt)
        ColorEntry(s"#$r$g$b", 100f * v / total)
      }
    }

    img.release()
    samples.release()
    samples32f.release()
    centers.release()

    ProcessingResult(imagePath, processingTimeMillis, colors.toList)
  }

  private def paddLeft(x: String): String = "0" * (2 - x.length) + x

  private def formatChannel(n: Int): String = paddLeft(n.toHexString)

}
