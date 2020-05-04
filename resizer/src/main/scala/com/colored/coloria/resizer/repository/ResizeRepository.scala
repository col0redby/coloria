package com.colored.coloria.resizer.repository

import cats.effect.IO
import com.colored.coloria.resizer.models.ResizeResult
import doobie.util.transactor.Transactor
import doobie.implicits._
import doobie.util.fragment.Fragment

class ResizeRepository(transactor: Transactor[IO]) {

  def updateImageWithResizingResults(
      resizeResult: ResizeResult
  ): IO[Either[Throwable, Int]] = {
    val set =
      resizeResult.sizes.map(s => s"${s.title} = '${s.url}'").mkString(", ")
    Fragment.const(s"UPDATE images SET $set WHERE id = ${resizeResult.imageId}").update.run
      .transact(transactor)
      .attempt
  }

}
