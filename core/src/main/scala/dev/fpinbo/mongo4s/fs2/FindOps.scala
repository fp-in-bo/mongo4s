package dev.fpinbo.mongo4s.fs2

import java.util

import cats.effect.{ConcurrentEffect, IO}
import com.mongodb.internal.async.{AsyncBatchCursor, SingleResultCallback}
import com.mongodb.internal.async.client.AsyncFindIterable
import fs2.concurrent.Queue
import org.bson.Document
import org.bson.conversions.Bson
import cats.implicits._
import scala.jdk.CollectionConverters._

class FindOps[F[_]](private val wrapped: AsyncFindIterable[Document])(
  implicit F: ConcurrentEffect[F]
) {
  def first = F.async[Document] { cb =>
    wrapped.first((result: Document, t: Throwable) => {
      if (result == null) cb(Left(t))
      else cb(Right(result))
    })
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

          wrapped.batchCursor(
            (result: AsyncBatchCursor[Document], t: Throwable) => {
              if (result == null)
                enqueue(Some(Left(t)))
              else {
                result.next((result: util.List[Document], t: Throwable) => {
                  if (result == null && t == null) enqueue(None)
                  else if (t != null) enqueue(Some(Left(t)))
                  else
                    result.asScala.toList.foldl(())(
                      (_, document) => enqueue(Some(Right(document)))
                    )
                })
              }

            }
          )
        }
      }
      doc <- q.dequeue.unNoneTerminate.rethrow
    } yield doc

  def filter(filter: Bson) = wrapped.filter(filter)
}
