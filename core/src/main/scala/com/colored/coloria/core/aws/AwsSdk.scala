package com.colored.coloria.core.aws

import java.nio.file.Paths

import cats.effect.{IO, Resource}
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.profiles.ProfileFile
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient

object AwsSdk {
  def s3AsyncClient(configPath: String, credsPath: String, profile: String, region: String): Resource[IO, S3AsyncClient] = {
    Resource.liftF(
      IO.pure(
        S3AsyncClient
          .builder()
          .credentialsProvider(
            ProfileCredentialsProvider
              .builder()
              .profileFile(
                ProfileFile.aggregator()
                  .applyMutation(builder => builder.addFile(
                    ProfileFile.builder()
                      .content(Paths.get(configPath))
                      .`type`(ProfileFile.Type.CONFIGURATION)
                      .build())
                  )
                  .applyMutation(builder => builder.addFile(
                    ProfileFile.builder()
                      .content(Paths.get(credsPath))
                      .`type`(ProfileFile.Type.CREDENTIALS)
                      .build())
                  )
                  .build()
              )
              .profileName(profile)
              .build()
          )
          .region(Region.of(region))
          .build()
      )
    )
  }
}
