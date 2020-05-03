package dev.fpinbo.mongo4s.fs2

import java.util

import cats.effect.{ ConcurrentEffect, IO }
import cats.implicits._
import com.mongodb.internal.async.AsyncBatchCursor
import com.mongodb.internal.async.client.AsyncFindIterable
import fs2.concurrent.Queue
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.bson.Document
import org.bson.conversions.Bson

import scala.jdk.CollectionConverters._

class FindOps[F[_]] private (private val wrapped: AsyncFindIterable[Document])(
  implicit F: ConcurrentEffect[F]
) {

  implicit val logger: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger[F]

  def first = F.async[Document] { cb =>
    wrapped.first { (result: Document, t: Throwable) =>
      if (result == null) cb(Left(t))
      else cb(Right(result))
    }
  }

  def stream: fs2.Stream[F, Document] =
    for {
      q <- fs2.Stream.eval(
            Queue.unbounded[F, Option[Either[Throwable, Document]]]
          )
      _ <- fs2.Stream.eval {
            F.delay {
              def enqueue(value: Option[Either[Throwable, Document]]): Unit =
                F.runAsync(q.enqueue1(value))(_ => IO.unit).unsafeRunSync

              def nextData(cursor: AsyncBatchCursor[Document]): Unit =
                cursor.next { (result: util.List[Document], t: Throwable) =>
                  if (result == null && t == null) {
                    enqueue(None)
                  } else if (t != null) {
                    enqueue(Some(Left(t)))
                  } else {
                    result.asScala.toList.foldl(())((_, document) => enqueue(Some(Right(document))))
                    nextData(cursor)
                  }
                }

              wrapped.batchSize(1).batchCursor { (cursor: AsyncBatchCursor[Document], t: Throwable) =>
                if (cursor == null) enqueue(Some(Left(t)))
                else { nextData(cursor) }
              }
            }
          }
      doc <- q.dequeue.unNoneTerminate.rethrow
    } yield doc

  def filter(filter: Bson) = new FindOps[F](wrapped.filter(filter))
}

object FindOps {

  def apply[F[_]: ConcurrentEffect](wrapped: AsyncFindIterable[Document]) =
    new FindOps[F](wrapped)

}
