package dev.fpinbo.mongo4s.fs2

import java.util

import cats.effect.{ ConcurrentEffect, IO }
import cats.implicits._
import com.mongodb.internal.async.AsyncBatchCursor
import com.mongodb.internal.async.client.{ AsyncFindIterable, AsyncMongoCollection }
import fs2.concurrent.Queue
import org.mongodb.scala.bson.BsonDocument

import scala.jdk.CollectionConverters._

class MongoCollection[F[_]] private (
  private val wrapped: AsyncMongoCollection[BsonDocument]
)(implicit F: ConcurrentEffect[F]) {

  def find(
    filter: Option[BsonDocument] = None,
    projection: Option[BsonDocument] = None,
    sort: Option[BsonDocument] = None
  ) = {
    val filtered          = filter.fold(wrapped.find())(f => wrapped.find(f))
    val projected         = projection.fold(filtered)(filtered.projection)
    val asyncFindIterable = sort.fold(projected)(projected.projection)
    FindOperation.stream(asyncFindIterable)
  }

  def first = F.async[BsonDocument] { cb =>
    wrapped.find().first { (result: BsonDocument, t: Throwable) =>
      if (result == null) cb(Left(t))
      else cb(Right(result))
    }
  }
}

object MongoCollection {

  def apply[F[_]: ConcurrentEffect](
    wrapped: AsyncMongoCollection[BsonDocument]
  ): MongoCollection[F] =
    new MongoCollection(wrapped)
}

object FindOperation {

  def stream[F[_]](wrapped: AsyncFindIterable[BsonDocument])(implicit F: ConcurrentEffect[F]) =
    for {
      q <- fs2.Stream.eval(
            Queue.unbounded[F, Option[Either[Throwable, BsonDocument]]]
          )
      _ <- fs2.Stream.eval {
            F.delay {
              def enqueue(value: Option[Either[Throwable, BsonDocument]]): Unit =
                F.runAsync(q.enqueue1(value))(_ => IO.unit).unsafeRunSync

              def nextData(cursor: AsyncBatchCursor[BsonDocument]): Unit =
                cursor.next { (result: util.List[BsonDocument], t: Throwable) =>
                  if (result == null && t == null) {
                    enqueue(None)
                  } else if (t != null) {
                    enqueue(Some(Left(t)))
                  } else {
                    result.asScala.toList.foldl(())((_, document) => enqueue(Some(Right(document))))
                    nextData(cursor)
                  }
                }

              wrapped.batchCursor { (cursor: AsyncBatchCursor[BsonDocument], t: Throwable) =>
                if (t != null) enqueue(Some(Left(t)))
                else if (cursor == null) enqueue(None)
                else nextData(cursor)
              }
            }
          }
      doc <- q.dequeue.unNoneTerminate.rethrow
    } yield doc
}
