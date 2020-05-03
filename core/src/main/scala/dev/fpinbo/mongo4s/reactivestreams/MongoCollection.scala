package dev.fpinbo.mongo4s.reactivestreams

import cats.effect.ConcurrentEffect
import cats.implicits._
import com.mongodb.reactivestreams.client.{ MongoCollection => JMongoCollection }
import org.bson.Document

class MongoCollection[F[_]] private (
  private val wrapped: JMongoCollection[Document]
)(implicit F: ConcurrentEffect[F]) {
  def find: F[FindOps[F]] = F.delay(wrapped.find()).map(fp => FindOps[F](fp))
}

object MongoCollection {

  def apply[F[_]: ConcurrentEffect](
    wrapped: JMongoCollection[Document]
  ): MongoCollection[F] =
    new MongoCollection(wrapped)
}
