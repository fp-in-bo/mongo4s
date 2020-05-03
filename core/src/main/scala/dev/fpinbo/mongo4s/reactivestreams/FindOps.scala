package dev.fpinbo.mongo4s.reactivestreams

import cats.effect.ConcurrentEffect
import com.mongodb.reactivestreams.client.FindPublisher
import fs2.interop.reactivestreams._
import org.bson.Document

class FindOps[F[_]: ConcurrentEffect] private (
  private val wrapped: FindPublisher[Document]
) {

  def first(): F[Document] =
    wrapped.first().toStream[F].head.compile.lastOrError
}

object FindOps {

  def apply[F[_]: ConcurrentEffect](
    wrapped: FindPublisher[Document]
  ): FindOps[F] =
    new FindOps(wrapped)
}
