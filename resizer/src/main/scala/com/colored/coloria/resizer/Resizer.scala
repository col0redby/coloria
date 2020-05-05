package com.colored.coloria.resizer

import cats.effect.{ExitCode, IO}
import com.colored.coloria.resizer.config.Config
import com.rabbitmq.client.{DeliverCallback, Delivery}
import cats.implicits._
import com.colored.coloria.resizer.models.{ResizeRequest, ResizeResult, Size}
import com.colored.coloria.resizer.repository.ResizeRepository
import io.circe.parser._
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.GetUrlRequest

import sys.process._
import scala.util.Try


/* Example RabbitMQ payload
  {
    "imageId": 1,
    "originalBucket": "colored-backend-eu-north-1",
    "originalKey": "nkharitonov/original/DSC_0835-small.jpg",
    "filename": "testFileTarget",
    "targetBucket": "colored-backend-eu-north-1",
    "targetKeyPrefix": "images/user/nkharitonov"
  }
 */
// todo: use avast/scala-rabbitmq client

object Resizer {

  def resize(
      s3: S3AsyncClient,
      request: ResizeRequest,
      config: Config
  ): IO[Either[Throwable, ResizeResult]] = {

    val bashCommand =
      s"${config.resizer.scriptPath} " +
        s"${request.originalBucket} " +
        s"${request.originalKey} " +
        s"${request.targetBucket} " +
        s"${request.targetKeyPrefix} " +
        s"${request.filename} " +
        s"'${config.resizer.sizes.map(_.size).mkString(",")}'"

    val result = IO { Either.fromTry(Try(bashCommand.!!)) }

    result.map(
      _.map(_ =>
        ResizeResult(
          request.imageId,
          config.resizer.sizes.map(s =>
            Size(
              s.title,
              buildS3Url(
                s3,
                request.targetBucket,
                s"${request.targetKeyPrefix}/${s.size}/${request.filename}"
              )
            )
          )
        )
      )
    )
  }

  def buildS3Url(s3: S3AsyncClient, bucket: String, key: String): String = {
    s3.utilities()
      .getUrl(
        GetUrlRequest
          .builder()
          .bucket(bucket)
          .key(key)
          .build()
      )
      .toString
  }

  def handleMessage(
      consumerTag: String,
      delivery: Delivery,
      resources: Resources,
      resizeRepository: ResizeRepository
  ): IO[Either[Throwable, Int]] = {
    val messageStr = new String(delivery.getBody, "UTF-8")

    val resizing = for {
      resizeRequest <- decode[ResizeRequest](messageStr).pure[IO]
      resizeResult <- resizeRequest.fold(
        e => Either.left(e).pure[IO],
        resize(resources.s3AsyncClient, _, resources.config)
      )
      updateResults <- resizeResult.fold(
        e => Either.left(e).pure[IO],
        rr => resizeRepository.updateImageWithResizingResults(rr)
      )
    } yield updateResults

    resizing
  }

  def start(resources: Resources): IO[ExitCode] = {
    val resizeRepository = new ResizeRepository(resources.transactor)
    val channel = resources.rabbitChannel

    val deliverCb: DeliverCallback =
      (consumerTag: String, delivery: Delivery) => {
        val resizing =
          handleMessage(consumerTag, delivery, resources, resizeRepository)

        resizing.unsafeRunSync() match {
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
