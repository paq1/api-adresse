package repository

import errors.data.ValidatedErr
import cats.data.Validated

trait WriteOnlyRepository[F[_], DATA, ID] {
  def insertOne(data: DATA): F[ValidatedErr[Unit]]
//  def updateOne(id: ID, data: DATA): F[ValidatedErr[DATA]]
}
