package com.colored.coloria.colors

import blobstore.BasePath
import blobstore.s3.S3Path
import cats.effect.{ExitCode, IO}
import com.rabbitmq.client.{DeliverCallback, Delivery}
import cats.implicits._
import com.colored.coloria.colors.models.{ColorsProcessingResult, ColorsRequest}
import com.colored.coloria.colors.repository.ColorsRepository
import com.colored.coloria.core.models.ColoriaError.ColorsProcessingError
import io.circe.parser._
import com.colored.coloria.core.{Coloria, Metadata => CoreMetdata}

/* Example RabbitMQ payload
  {
    "imageId": 1,
    "originalBucket": "colored-backend-eu-north-1",
    "originalKey": "nkharitonov/original/косуля2.jpg"
  }
 */
// todo: use avast/scala-rabbitmq client

object Colors {

  def createUniqueImagePath(
      tempImagePath: String,
      colorsRequest: ColorsRequest
  ): IO[String] = {
    IO.pure(
      tempImagePath + colorsRequest.imageId + System.currentTimeMillis()
    )
  }

  def handleMessage(
      consumerTag: String,
      delivery: Delivery,
      resources: Resources,
      colorsRepository: ColorsRepository
  ): IO[Either[Throwable, Int]] = {
    val messageStr = new String(delivery.getBody, "UTF-8")

    val processing = for {
      colorsRequest <- decode[ColorsRequest](messageStr) match {
        case Left(error) =>
          IO.raiseError(
            ColorsProcessingError("Unable to decode json payload.")
          )
        case Right(value) => IO(value)
      }
      filePath <- createUniqueImagePath(
        resources.config.colors.tempImageFolder,
        colorsRequest
      )
      downloadImage <- resources.s3Store
        .get(
          S3Path(
            colorsRequest.originalBucket,
            colorsRequest.originalKey,
            None
          ),
          4096
        )
        .through(
          resources.fileStore
            .put(BasePath.fromString(filePath, forceRoot = true).get)
        )
        .compile
        .toList
      processingResult <- Coloria.process(
        filePath,
        resources.config.colors.kMeans.clustersCount,
        resources.config.colors.kMeans.attempts,
        resources.config.colors.kMeans.maxCount,
        resources.config.colors.kMeans.epsilon
      )
      deleteImage <- resources.fileStore.remove(
        BasePath.fromString(filePath, forceRoot = true).get
      )
      updateResults <- colorsRepository.updateImageWithColors(
        ColorsProcessingResult(colorsRequest.imageId, processingResult)
      )
    } yield updateResults

    processing
  }

  def start(resources: Resources): IO[ExitCode] = {
    val colorsRepository = new ColorsRepository(resources.transactor)
    val channel = resources.rabbitChannel

    val deliverCb: DeliverCallback =
      (consumerTag: String, delivery: Delivery) => {
        val result =
          handleMessage(consumerTag, delivery, resources, colorsRepository)

        result.unsafeRunSync() match {
          case Left(error) =>
            println(error)
            channel.basicNack(delivery.getEnvelope.getDeliveryTag, false, false)
          case Right(value) =>
            println(value)
            channel.basicAck(delivery.getEnvelope.getDeliveryTag, false)
        }
      }

    channel.basicConsume(
      resources.config.rabbitMQ.queue.name,
      resources.config.rabbitMQ.queue.autoAck,
      deliverCb,
      (tag: String) => ()
    )

    // ???
    IO.never *> IO.pure(ExitCode.Success)
  }

}
