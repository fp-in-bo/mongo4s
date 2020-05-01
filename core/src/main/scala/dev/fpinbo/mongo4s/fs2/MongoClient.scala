package dev.fpinbo.mongo4s.fs2

import cats.effect.{ConcurrentEffect, Resource}
import com.mongodb.internal.async.client.AsyncMongoClient
import com.mongodb.reactivestreams.client.{
  MongoClients,
  MongoClient => JMongoClient
}

class MongoClient[F[_]] private (private val wrapper: AsyncMongoClient)(
  implicit F: ConcurrentEffect[F]
) {
  def getDatabase(name: String): F[MongoDatabase[F]] =
    F.delay(MongoDatabase(wrapper.getDatabase(name)))
}

object MongoClient {
  def default[F[_]](
    implicit F: ConcurrentEffect[F]
  ): Resource[F, MongoClient[F]] =
    Resource
      .fromAutoCloseable(F.delay(MongoClients.create()))
      .map(m => new MongoClient(m)) //defaulting to localhost
}