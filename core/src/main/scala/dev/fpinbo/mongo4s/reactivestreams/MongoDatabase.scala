package dev.fpinbo.mongo4s.reactivestreams

import cats.effect.ConcurrentEffect
import cats.implicits._
import com.mongodb.reactivestreams.client.{ MongoDatabase => JMongoDatabase }

class MongoDatabase[F[_]] private (private val wrapped: JMongoDatabase)(
  implicit F: ConcurrentEffect[F]
) {

  def getCollection(collectionName: String) =
    F.delay(wrapped.getCollection(collectionName))
      .map(mc => MongoCollection(mc))
}

object MongoDatabase {

  def apply[F[_]: ConcurrentEffect](wrapped: JMongoDatabase) =
    new MongoDatabase[F](wrapped)
}
