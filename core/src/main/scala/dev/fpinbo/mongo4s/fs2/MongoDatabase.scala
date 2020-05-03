package dev.fpinbo.mongo4s.fs2

import cats.effect.ConcurrentEffect
import cats.implicits._
import com.mongodb.internal.async.client.AsyncMongoDatabase

class MongoDatabase[F[_]] private (private val wrapped: AsyncMongoDatabase)(
  implicit F: ConcurrentEffect[F]
) {

  def getCollection(collectionName: String) =
    F.delay(wrapped.getCollection(collectionName))
      .map(mc => MongoCollection(mc))
}

object MongoDatabase {

  def apply[F[_]: ConcurrentEffect](wrapped: AsyncMongoDatabase) =
    new MongoDatabase[F](wrapped)
}
