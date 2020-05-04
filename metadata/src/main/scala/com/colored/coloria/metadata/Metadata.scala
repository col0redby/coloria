package com.colored.coloria.metadata

import blobstore.BasePath
import blobstore.s3.S3Path
import cats.effect.{ExitCode, IO}
import com.rabbitmq.client.{DeliverCallback, Delivery}
import cats.implicits._
import com.colored.coloria.core.models.ColoriaError.MetadataExtractionError
import com.colored.coloria.metadata.models.{
  MetadataExtractResult,
  MetadataRequest
}
import com.colored.coloria.metadata.repository.MetadataRepository
import io.circe.parser._
import com.colored.coloria.core.{Metadata => CoreMetdata}
import com.colored.coloria.metadata.config.Config

/* Example RabbitMQ payload
  {
    "imageId": 1,
    "originalBucket": "colored-backend-eu-north-1",
    "originalKey": "nkharitonov/original/косуля2.jpg"
  }
 */

object Metadata {

  def createUniqueImagePath(
      tempImagePath: String,
      metadataRequest: MetadataRequest
  ): IO[String] = {
    IO.pure(
      tempImagePath + metadataRequest.imageId + System.currentTimeMillis()
    )
  }

  def handleMessage(
      consumerTag: String,
      delivery: Delivery,
      resources: Resources,
      metadataRepository: MetadataRepository
  ): IO[Either[Throwable, Int]] = {
    val messageStr = new String(delivery.getBody, "UTF-8")

    val extracting = for {
      metadataRequest <- decode[MetadataRequest](messageStr) match {
        case Left(error) =>
          IO.raiseError(
            MetadataExtractionError("Unable to decode json payload.")
          )
        case Right(value) => IO(value)
      }
      filePath <- createUniqueImagePath(
        resources.config.metadata.tempImageFolder,
        metadataRequest
      )
      downloadImage <- resources.s3Store
        .get(
          S3Path(
            metadataRequest.originalBucket,
            metadataRequest.originalKey,
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
      metadataExtractionResult <- CoreMetdata.metadataFromLocalImage(filePath)
      deleteImage <- resources.fileStore.remove(
        BasePath.fromString(filePath, forceRoot = true).get
      )
      updateResults <- metadataExtractionResult.fold(
        e => IO.raiseError(e),
        mer =>
          metadataRepository.updateImageWithMetadata(
            MetadataExtractResult(metadataRequest.imageId, mer)
          )
      )
    } yield updateResults

    extracting
  }

  def start(resources: Resources): IO[ExitCode] = {
    val metadataRepository = new MetadataRepository(resources.transactor)
    val channel = resources.rabbitChannel

    val deliverCb: DeliverCallback =
      (consumerTag: String, delivery: Delivery) => {
        val result =
          handleMessage(consumerTag, delivery, resources, metadataRepository)

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
