package mongo

import org.mongodb.scala.bson.BsonDocument

trait BsonCodec[T] {
  def asScala(bsonDocument: BsonDocument): Option[T]
  def toBson(element: T): BsonDocument
}
