package dev.fpinbo.mongo4s.fs2

import cats.effect.{ ExitCode, IO, IOApp }
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {
  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def run(args: List[String]): IO[ExitCode] =
    MongoClient
      .default[IO]
      .use(mongoClient =>
        for {
          db   <- mongoClient.getDatabase("test")
          coll <- db.getCollection("contributors")
          _    <- logger.info("Executing a find operation")
          //         firstDoc <- coll.find.flatMap(_.first)
          findOps <- coll.find
          allDocs <- findOps.stream.compile.toList
//          _        <- logger.info("Result:" + firstDoc)
          _ <- logger.info("Result:" + allDocs)
        } yield allDocs
      )
      .as(ExitCode.Success)
}
