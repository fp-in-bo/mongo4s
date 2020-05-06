package dev.fpinbo.mongo4s.fs2

import cats.effect.{ ExitCode, IO, IOApp }
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {
  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def run(args: List[String]): IO[ExitCode] =
    MongoClient
      .default[IO]
      .use { mongoClient =>
        val collection = mongoClient.getDatabase("test").getCollection("contributors")
        collection.find().take(3).compile.toList.flatMap(rs => logger.info("results: " + rs))
      }
      .as(ExitCode.Success)
}
