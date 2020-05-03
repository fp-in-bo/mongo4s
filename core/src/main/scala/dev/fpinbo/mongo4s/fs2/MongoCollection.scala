package dev.fpinbo.mongo4s.fs2

import cats.effect.ConcurrentEffect
import com.mongodb.internal.async.client.AsyncMongoCollection
import org.bson.Document

class MongoCollection[F[_]] private (
  private val wrapped: AsyncMongoCollection[Document]
)(implicit F: ConcurrentEffect[F]) {
  def find = F.delay(FindOps(wrapped.find()))
}

object MongoCollection {

  def apply[F[_]: ConcurrentEffect](
    wrapped: AsyncMongoCollection[Document]
  ): MongoCollection[F] =
    new MongoCollection(wrapped)
}
