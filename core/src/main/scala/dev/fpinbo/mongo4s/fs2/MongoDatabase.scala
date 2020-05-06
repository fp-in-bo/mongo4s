package dev.fpinbo.mongo4s.fs2

import cats.effect.ConcurrentEffect
import com.mongodb.internal.async.client.AsyncMongoDatabase
import org.mongodb.scala.bson.BsonDocument

class MongoDatabase[F[_]] private (private val wrapped: AsyncMongoDatabase)(
  implicit F: ConcurrentEffect[F]
) {

  def getCollection(
    collectionName: String
  ): MongoCollection[F] =
    MongoCollection(wrapped.getCollection(collectionName, classOf[BsonDocument]))
}

object MongoDatabase {

  def apply[F[_]: ConcurrentEffect](wrapped: AsyncMongoDatabase) =
    new MongoDatabase[F](wrapped)
}
