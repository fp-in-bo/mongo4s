package dev.fpinbo.mongo4s.fs2

import cats.effect.ConcurrentEffect
import cats.implicits._
import com.mongodb.internal.async.client.AsyncMongoCollection
import com.mongodb.reactivestreams.client.{MongoCollection => JMongoCollection}
import org.bson.Document

class MongoCollection[F[_]] private (
  private val wrapped: AsyncMongoCollection[Document]
)(implicit F: ConcurrentEffect[F]) {
  def find: F[FindOps[F]] = new FindOps(wrapped.find())
}

object MongoCollection {
  def apply[F[_]: ConcurrentEffect](
    wrapped: JMongoCollection[Document]
  ): MongoCollection[F] =
    new MongoCollection(wrapped)
}
